# Phase 5 Report: First-run flow, replay and safe preview

## 1. Summary

Tutorial routing now follows the completion flag: new players enter their name first, loaded incomplete players resume the tutorial without Welcome, and complete/legacy players retain Title -> menu. Confirming the first-run wrap-up marks completion and saves immediately; replays never mark or save. The menu now offers Tutorial, navigation cannot escape an active run, and StagePreview newplayer provides the full fresh-player flow through a temporary save path. The included optional content revision adds a result-aware wrap-up and a five-second demo timer.

## 2. Files

Java paths are relative to `src/edu/batstateu/javajolt/` unless prefixed with `tests/`.

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `GameApplication.java` | MODIFIED | Small pure startDestination decision used by production and relaunch tests; loaded false flags select the existing QUESTION destination. |
| `ui/swing/MainWindow.java` | MODIFIED | Title/tutorial and Welcome transitions, confirmed completion save, menu callback, topics-control lifecycle and rejection of other navigation during tutorials. |
| `ui/swing/screen/QuestionScreen.java` | MODIFIED | One tutorial-agnostic setter enabling/disabling the existing topics button. |
| `ui/swing/screen/MainMenuScreen.java` | MODIFIED | Add Tutorial through one Runnable, keep four secondary tiles balanced and put Exit beneath them. |
| `preview/PreviewSandbox.java` | MODIFIED | Add an unseeded temporary SaveService factory; retain existing seeded preview behavior. |
| `preview/StagePreview.java` | MODIFIED | Add newplayer with zero XP/no progress/false flag, Title/Welcome entry and no seed save. |
| `data/TutorialContent.java` | MODIFIED | Render the single wrap-up from real pass/accuracy/HP state with three reasons and practice reassurance. |
| `service/TutorialService.java` | MODIFIED | Five-second demo limit and fresh RESULTS text when its once-per-run step is requested. |
| `tests/SwingNavigationSmokeCheck.java` | MODIFIED | New first run, interrupted relaunch, legacy menu, full replay hash, disabled topics, menu fit and actual newplayer-launcher checks. |
| `tests/SwingTimerFlowCheck.java` | MODIFIED | Update completion expectations and mandatory navigation protection; preserve timer/replay/close checks. |
| `tests/TutorialLogicCheck.java` | MODIFIED | Verify all wrap-up variants, live numeric text and five-second demo tuning. |
| `tests/ProgressConsistencyCheck.java` | MODIFIED | Verify unseeded new-player sandbox and false-flag persistence/isolation. |
| `RUN_TESTS.bat` | MODIFIED | Add TutorialLogicCheck to compile/run lists, as requested; not executed. |
| `PROJECT_RULES.md` | MODIFIED | Document flag routing and GameApplication/MainWindow ownership; existing six-type map is retained. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED | Finalize tutorial behavior, sandbox, Heal reference, result text and safe preview instructions. |
| `progress-documentations/change-reports/phase-5-first-run-flow.md` | NEW | This report. |

## 3. Ownership and rule check

GameApplication chooses the start destination; MainWindow owns transitions and the existing shared quiz paths. The existing QUESTION destination signals tutorial entry from Title, so no screen or navigation system is added. TutorialCoordinator still presents popups only, and TutorialService still has no second execution loop. TutorialContent reads LessonRunnerService.hasPassed/getPassPercent, GameSession accuracy/counts and Player HP for the final text. QuestionScreen's setter knows nothing about tutorials. PreviewSandbox owns temporary paths; only SaveService handles persistence. No new libraries, assets, effects or rule exceptions.

## 4. Existing code touched

Every existing-file edit and its reason is listed in section 2. The normal quiz submit/timeout/power-up/settlement paths remain shared and intact. MainWindow saves only after the same run's confirmed wrap-up returns and only for a non-replay incomplete player; cleanup precedes menu display. Programmatic navigation and old quiz-start callbacks are guarded during an active tutorial, and the topics control is restored during cleanup.

MainMenuScreen receives one more callback through its existing constructor/createBody/createMenuButtons chain. Its existing two-by-two secondary grid now contains Progress, Save, Achievements and Tutorial; Exit uses a full-width row below. No existing button changes its callback or visual style.

## 5. Player-visible behavior changes

- Fresh profile: Title -> Welcome -> mandatory tutorial -> menu.
- Saved false flag: Title -> mandatory tutorial, using the saved name; the mere existence of a save no longer skips it.
- Saved true/missing legacy flag: Title -> menu, with Tutorial available.
- First-run completion: only Got it on the wrap-up marks/saves true; no save-success popup.
- Any tutorial: Choose another topic is disabled, other navigation is blocked; replay has Exit tutorial, and window close retains existing confirmation.
- Replay: same sample and popups, with no real progress, flag or disk changes.
- Wrap-up: the real pass, accuracy-failure or HP-defeat reason and numbers, ending with the practice reassurance. Only the demo timer changes, from three to five seconds.

## 6. Save compatibility

Save format and legacy decoding are unchanged from Phase 2. Mid-first-run exit can save false; the next loaded launch resumes the tutorial. First completion writes the real profile with true through saveProgress; no tutorial score, XP, high scores or completions settle. Replays never save, including completion, Exit tutorial and application close. The tests use temporary fixtures and SHA-256 comparisons; no real saves/ file is opened or modified.

## 7. Balance values introduced or changed

| Owner | Constant | Before -> after |
| --- | --- | --- |
| TutorialService | DEMO_TIMER_SECONDS | 3 -> 5, capped at the normal limit if shorter |

Real question limits, scoring, pass mark, enemy damage and Heal percentages are unchanged. Result text uses owning APIs and is rebuilt at RESULTS rather than retaining the session-start snapshot.

## 8. Verification

Temporary-build commands (PowerShell, from the project root):

```powershell
$b = Join-Path $env:TEMP 'javajolt-tutorial-phase5-build'
New-Item -ItemType Directory -Force -Path $b | Out-Null
[IO.File]::WriteAllLines((Join-Path $b 'sources.txt'), @(rg --files src tests -g '*.java'))
javac -encoding UTF-8 -d $b ('@' + (Join-Path $b 'sources.txt'))
if ($LASTEXITCODE) { throw 'Compilation failed' }
$checks = @('edu.batstateu.javajolt.preview.MechanicsCheck', 'ProgressConsistencyCheck',
    'TutorialLogicCheck', 'ContentCoverageCheck', 'SwingTimerFlowCheck',
    'SwingNavigationSmokeCheck', 'EnemyArtworkIntegrationCheck')
foreach ($check in $checks) {
    java -cp "$b;src" $check
    if ($LASTEXITCODE) { throw "Failed: $check" }
}
& .\tests\TutorialRetuningCheck.ps1
java -cp $b edu.batstateu.javajolt.preview.BalanceReport
git diff --check
```

Results pending after report creation. RUN_TESTS.bat is edited only and is not run. All compiled output and retuned source copies are under TEMP; out/, build/, real saves and assets remain untouched.

## 9. Manual test steps for the owner

1. Build with section 8, then run `java -cp "$b;src" edu.batstateu.javajolt.preview.StagePreview newplayer`.
2. Start from Title, enter a name and read the mandatory tutorial. Try the topics action and other navigation; neither should escape. Only the wrap-up confirmation completes it and returns to the menu.
3. Repeat newplayer and close during the tutorial. The false flag may be saved only to that temporary path. Automated relaunch checks load an equivalent incomplete fixture and verify Title -> tutorial, without Welcome.
4. Use the menu Tutorial button; finish or choose Exit tutorial and verify menu cleanup. Automated SHA-256 checks cover both routes and preserve existing progress.
5. Run `java -cp "$b;src" edu.batstateu.javajolt.preview.StagePreview all --xp=1000` to check existing preview behavior and all four power-ups. Existing modes remain seeded/completed.
6. Menu geometry is checked at the designed 1100x720 window size. An optional `SwingNavigationSmokeCheck --capture-menu` writes TEMP/javajolt-phase5-menu.png for visual review.

## 10. Docs updated

PROJECT_RULES adds the flag-routing rule and composition-root/transition ownership. Its existing map already includes all six tutorial types. Gameplay notes sections 2.7/2.8 now describe the complete feature, isolated newplayer mode, mandatory routing, confirmed completion save, read-only replay, current result-aware content and Heal's section 3.1 rules. Existing unrelated notes/edits are preserved; AGENT_BRIEF and Phase 1-4 reports are unchanged.

## 11. Assumptions, deviations and risks

- Optional block F is included because it remains in the owner's Phase 5 request. It changes only the one RESULTS step and demo timer, not the question count or normal rules.
- QUESTION is the existing Title destination for tutorial entry, not a new screen. Loaded name availability still determines whether Welcome is needed; the saved flag determines tutorial versus menu afterward.
- Finishing practice does not require passing. Confirmation completes onboarding after any of the three outcomes, while real progress remains untouched.
- On save failure the flag remains true in memory, the existing saveProgress error dialog appears, and the menu follows as requested. A later launch follows the value actually on disk.
- newplayer rejects XP overrides to guarantee zero XP/no seeded progress and does not create a seed save before name entry. Existing preview modes and the old SwingPreview remain completed/sandboxed.
- Tests may materialize the existing result-area topics control behind the wrap-up modal to exercise its disabled/enabled contract; production uses the regular shared question flow.
- Menu layout is a functional extra action using the existing Theme/Rounded styles; no general restyling or minimum-window redesign is included.

## 12. Noticed but intentionally not changed

Known DI/console/model deviations, historic design prose, enemy identities/assets, textbook persistence and unrelated UI formatting remain unchanged. The legacy RUN_TESTS.bat still targets out/; only its requested tutorial entries are changed, and verification uses the temporary commands above. No commits or real-save edits are made. Stop after final verification.
