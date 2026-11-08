# Phase 4 Report: Tutorial popups on the shared quiz flow

## 1. Summary

The tutorial now runs on the real QuestionScreen through MainWindow's existing answer, timeout, power-up and question-transition paths. A modal TutorialPopup presents the same script at the player's pace, while TutorialCoordinator handles presentation only. The shared countdown pauses and resumes with its remaining time; tutorial completion shows the wrap-up and returns to the menu without settlement. Phase 5 routing, completion persistence and the menu button remain pending.

The owner's added Phase 4 constraint is implemented: no second quiz loop. TutorialService's answer, timeout and power-up execution wrappers were removed.

## 2. Files

Java paths are relative to `src/edu/batstateu/javajolt/` unless prefixed with `tests/`.

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `ui/swing/component/TutorialPopup.java` | NEW | Modal kit-styled title/body/step counter, Got it, replay-only Exit tutorial and existing application-close callback. |
| `ui/swing/TutorialCoordinator.java` | NEW | Present due steps, replay state and popup cleanup; no submit, timeout or power-up execution. |
| `ui/swing/MainWindow.java` | MODIFIED | Session-player display/guards, injected coordinator, minimal tutorial entry/cleanup/step hooks, settlement bypass and shared-clock pause/resume. |
| `ui/swing/Theme.java` | MODIFIED | Popup body rows/columns in the existing UI sizing owner. |
| `service/TutorialService.java` | MODIFIED | Remove second-loop execution wrappers and active-question state; read the shared session for timing/gating and prepare Heal only. |
| `GameApplication.java` | MODIFIED | Wire the tutorial collaborators and inject the coordinator using the same rule instances. |
| `preview/StagePreview.java` | MODIFIED | Wire a coordinator per preview window with the shared rules, keeping sandbox injection. |
| `ui/swing/SwingPreview.java` | MODIFIED | Wire the same tutorial collaborators into the existing isolated preview. |
| `tests/SwingTimerFlowCheck.java` | MODIFIED | Full tutorial walkthrough, modal pause/input guards, shared handlers, wrap-up, replay exit, cleanup and close confirmation. Add a manual walkthrough option. |
| `tests/TutorialLogicCheck.java` | MODIFIED | Adapt pure checks to the revised service responsibilities; answer/timeout/power-up execution is tested through rule owners. |
| `PROJECT_RULES.md` | MODIFIED | Record shared quiz-loop and tutorial presentation ownership. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED | Replace pending Phase 4 note with the implemented boundary and the owner's no-second-loop constraint. |
| `progress-documentations/change-reports/phase-4-tutorial-popup-integration.md` | NEW | This report. |

## 3. Ownership and rule check

MainWindow retains the only Swing quiz loop and countdown. Its existing methods still call LessonRunnerService.submitAnswer, AnswerTimerService.applyTimeout and PowerUpService.use on the current session. TutorialService supplies the throwaway session, popup timing, power-up gate, demo time limit and Heal preparation; it does not execute those events. TutorialCoordinator only presents TutorialPopup components. GameApplication and preview/test composition roots construct the injected collaborators. QuestionScreen is unchanged.

No gameplay formulas, persistence encoding, assets, libraries, animation, audio or restyling were added. No PROJECT_RULES exception was required. Window owners and close callbacks are passed as per-popup presentation context; the coordinator's persistent service collaborator is received through its constructor.

## 4. Existing code touched

MainWindow changes are targeted hooks, not a quiz-loop rewrite: 93 added and 19 removed lines (net +74 at integration review). The normal results/settlement body is untouched; a tutorial branch returns before it. Active quiz display/HP guards now read session.getPlayer(), while persistent save/progression still use the real player field. The sole existing question-index update remains in place, followed by Heal preparation when applicable. No tutorial logic was added to the large QuestionScreen.

The shared startCountdown method now has a nanosecond overload so a popup can preserve partial seconds, using the same Swing Timer and version guard. Submit/timeout/power-up/Continue guards reject callbacks while a teaching popup is open. Clearing a tutorial cancels the coordinator/popup and clock, nulls the session, resets index/active/remainder/result state, and invalidates stale workers/timer callbacks.

The remaining existing-file changes and their reasons are all listed in section 2. Legacy MainWindow constructors remain available for existing callers; composition roots use the new injected-coordinator overload. No first-run routing or menu behavior changed.

## 5. Player-visible behavior changes (before -> after)

- No integrated tutorial -> the approved sample runs on the real question view with thirteen modal teaching steps.
- Popup open -> the countdown pauses without using Time Stop or recording an expiry; the owner window's input is blocked and controller callbacks are guarded.
- Got it -> the next step appears, or the same countdown resumes with its remaining time.
- Replay -> Exit tutorial returns to the menu without saving. The first run has no Skip/Exit tutorial button.
- Tutorial end -> ready popup, then a clean menu; no real ResultsScreen, reward settlement or high-score mutation.
- Closing the application or teaching popup -> existing exit confirmation; declining stays in the paused tutorial, accepting leaves completion false. Closing during replay does not re-save.

## 6. Save compatibility

No format changes. The real player remains separate from the tutorial session's player. Tutorial progress never reaches settlement or high-score recording, and ordinary walkthrough/replay exit never writes a save. First-run exit may use the existing save path with the real player's still-false flag; Phase 5 will mark/save only confirmed completion. Checks use temporary saves, never the real saves/ folder.

## 7. Balance values introduced or changed

None. Theme.TUTORIAL_BODY_ROWS = 9 and TUTORIAL_BODY_COLUMNS = 56 are UI viewport sizes, not gameplay values. The existing demo limit, normal limits, percentages, pass mark, scoring and damage remain unchanged. Popup timing uses MainWindow's existing monotonic clock and Swing Timer.

## 8. Verification

Build/check commands from the project root:

```powershell
$tutorialBuild = Join-Path $env:TEMP 'javajolt-tutorial-phase4-build'
New-Item -ItemType Directory -Force -Path $tutorialBuild | Out-Null
$tutorialSources = @(rg --files src tests -g '*.java')
[System.IO.File]::WriteAllLines((Join-Path $tutorialBuild 'sources.txt'), $tutorialSources)
javac -encoding UTF-8 -d $tutorialBuild ('@' + (Join-Path $tutorialBuild 'sources.txt'))
java -cp "$tutorialBuild;src" SwingTimerFlowCheck
java -cp "$tutorialBuild;src" SwingNavigationSmokeCheck
java -cp $tutorialBuild TutorialLogicCheck
& .\tests\TutorialRetuningCheck.ps1
java -cp $tutorialBuild edu.batstateu.javajolt.preview.MechanicsCheck
java -cp $tutorialBuild edu.batstateu.javajolt.preview.BalanceReport
git diff --check
```

Compilation exited 0. Final result excerpts:

```text
ALL SWING TIMER FLOW CHECKS PASSED (46)
ALL SWING NAVIGATION CHECKS PASSED (12)
ALL TUTORIAL LOGIC CHECKS PASSED (575)
RETUNED TEXT CHECKS PASSED
ALL TUTORIAL LOGIC CHECKS PASSED (576)
PASS: Retuning changed only temporary source copies; project constants were untouched.
Checks: 453 passed, 0 failed, 453 total.
ALL CHECKS PASSED
```

BalanceReport exited 0 with the unchanged level, survivability, enumeration and weighted results. All final checks passed after report creation; no failing check required a gameplay correction. `git diff --check` passed with existing Git LF/CRLF normalization notices. The source audit confirms that TutorialService and TutorialCoordinator contain no submit/timeout/power-up execution path or additional timer.

Compile output is under TEMP. The Swing extension operates the actual dialogs and quiz controls on the EDT, uses shortened injected limits and temporary save files, and holds popups longer than a countdown. It intentionally gives the real player zero HP to prove tutorial HP, display and guards use the throwaway player instead. The walkthrough reaches all thirteen popups and both answers, checks shared service calls and no settlement, compares save bytes after completion and replay exit, starts a normal quiz afterward, tests decline/accept exit confirmation, replaces an open modal run, navigates away, and confirms replay application exit does not re-save. Earlier duplicate-submission, frozen-clock, code-editor, timeout and ordinary settlement checks remain in the same suite.

The pure check count is lower than Phase 3 because duplicate-submission and post-answer timeout lifecycle assertions moved out of the removed TutorialService wrappers and are exercised by the real MainWindow integration checks instead. No required rule coverage was removed. Production defaults were also recompiled with the existing fifteen temporary constant changes; rendered text and pure sandbox checks still pass.

## 9. Manual test steps for the owner

1. Run `java -cp "$env:TEMP\javajolt-tutorial-phase4-build;src" SwingTimerFlowCheck --tutorial` from the project root. This opens an isolated development window and starts the first-run version of the tutorial; it does not touch real saves.
2. Read each popup at your own pace. Hold the first popup longer than the displayed timer; HP/time and Time Stop usage must not change until all introductory steps are confirmed.
3. Let the first sample expire, then answer it. Continue to the power-up steps; Heal is available after the script's damage preparation. Try Heal, Hint, Shield and Time Stop through the existing controls.
4. Answer the second sample and confirm the final ready popup. Check that the menu appears without ordinary quiz results or rewards.
5. Close the window during another first-run walkthrough. Decline, then accept the existing exit confirmation; neither action marks tutorial completion. Automated checks additionally cover replay Exit tutorial and a normal quiz afterward.

## 10. Docs updated

PROJECT_RULES now maps TutorialCoordinator/TutorialPopup and states that MainWindow owns the shared loop/countdown. Gameplay notes section 2.8 records the implemented popup/pause/cleanup behavior and the no-second-loop boundary, preserving the revised Phase 5 routing/newplayer requirements. AGENT_BRIEF and historical reports remain unchanged.

## 11. Deviations from this brief, assumptions made, and risks

The owner's latest instruction supersedes Phase 3's execution wrappers, which were removed rather than kept as a second loop. Its active-question flag was also removed; MainWindow owns input lifecycle and the service reads shared session state for gating. Popup shown-step numbering derives from the same thirteen-step script. A coordinator run version prevents an old modal stack from affecting a replacement run.

Phase 4 intentionally leaves the real tutorial-completed flag unchanged even after the wrap-up; setting it and saving immediately belong to Phase 5. The new manual test mode supplies an isolated entry point before the menu button exists, without adding another tutorial or production launcher mode. Existing preview modes remain unchanged; the requested newplayer mode stays in Phase 5.

The automated one-second fixture verifies pause/resume and routing; a full-duration visual walkthrough remains available through the manual option. There are no changes to normal quiz rewards or save encoding. The final RUN_TESTS.bat update/run is still reserved for Phase 5, including reconciling its out/ build target with PROJECT_RULES first.

## 12. Noticed but intentionally not changed

Known architecture deviations and unrelated navigation/formatting remain untouched. No new menu button, automatic flag routing, first-completion save or newplayer preview mode was implemented early. Stop after Phase 4 verification and wait for Phase 5 approval. Changes remain uncommitted.
