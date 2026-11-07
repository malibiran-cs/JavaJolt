# Phase 7 Report: Answer timer

## 1. Summary

Phase 7 is complete. Each question has a difficulty/input-mode countdown, one optional timeout attack, and functional Time Stop. Surviving expiry preserves inputs and answering; timeout never changes accuracy, either streak counter, score, or XP. Timeout defeat reuses existing results and settlement without recording an incorrect answer.

## 2. Files

Java paths below are relative to `src/edu/batstateu/javajolt/`.

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `service/AnswerTimerService.java` | NEW | Limits, base-only timeout damage, guarded application, and Shield handling. |
| `game/GameSession.java` | MODIFIED | Timeout/frozen state, eligibility, actual timeout damage, and index-change reset. |
| `service/PowerUpService.java` | MODIFIED | Functional Time Stop, applicability, and accurate Shield notice. |
| `ui/swing/MainWindow.java` | MODIFIED | Injected timer service; one countdown, invalidation, expiry/defeat routing, freeze, and retry/transition guards. |
| `ui/swing/screen/QuestionScreen.java` | MODIFIED | Header countdown; timeout refresh updates HP/notice without replacing inputs. |
| `GameApplication.java` | MODIFIED | Construct and inject timer service. |
| `preview/StagePreview.java` | MODIFIED | Inject timer service into every isolated preview window. |
| `preview/MechanicsCheck.java` | MODIFIED | Add 114 timer checks; update two obsolete Time Stop expectations. |
| `tests/SwingTimerFlowCheck.java` | NEW | 22 headful checks through actual Swing controls, timer events, workers, and settlement. |
| `PROJECT_RULES.md` | MODIFIED | Document timer ownership and current test tooling. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED | Add Phase 7 implementation/status notes, preserving pre-existing edits. |
| `progress-documentations/change-reports/phase-7-answer-timer.md` | NEW | This report. |

## 3. Ownership and rule check

`AnswerTimerService` owns gameplay limits/damage; `GameSession` owns temporary state; `PowerUpService` owns activation and once-per-quiz usage. `MainWindow` owns the Swing clock and callbacks; `QuestionScreen` only presents data. Services are injected from composition roots. No rule exceptions, libraries, assets, new enemies, or save changes were introduced.

## 4. Existing code touched

The MODIFIED rows in section 2 are the complete inventory and justification for existing-file edits. The existing question-index update remains single; reset occurs only when its value changes. Controller guards reject duplicate Continue/submission and callbacks from old timers, sessions, hidden screens, or disposed windows. Answer-check failures restore eligibility and resume an unfrozen, unexpired countdown from its displayed remaining seconds.

`LessonRunnerService`, `Enemy`, `ResultsScreen`, persistence, progression, question content, and balance formulas remain unchanged.

## 5. Player-visible behavior changes (before -> after)

- No timer -> countdown in the existing question header, with longer limits for code.
- Disabled Time Stop -> freezes the displayed remaining seconds for this question only; unavailable after expiry and once used this quiz.
- Expiry -> one timeout consequence and an inline notice; surviving players retain selections/text/code and can submit normally.
- Lethal expiry -> existing defeat results and settlement, with no incorrect-answer feedback.
- Shield notice now describes the next damaging attack, including timeout; Beginner expiry leaves Shield armed.

## 6. Save compatibility

No format or persistence API changes. Save/load, replay XP, and high-score checks pass using temporary saves. The real save was not opened or modified by these checks; compatibility with old saves remains through the unchanged `SaveService`/`SaveData` implementation. Existing documentation edits and the untracked `AGENT_BRIEF.md` were preserved. Builds went to `%TEMP%`, not `out/` or `build/`.

## 7. Balance values introduced or changed

All new gameplay constants are private in `service/AnswerTimerService.java`:

| Constant | Value |
| --- | ---: |
| `BEGINNER_SINGLE_SECONDS` | 30 |
| `BEGINNER_CODE_SECONDS` | 120 |
| `INTERMEDIATE_SINGLE_SECONDS` | 20 |
| `INTERMEDIATE_CODE_SECONDS` | 90 |
| `ADVANCED_SINGLE_SECONDS` | 15 |
| `ADVANCED_CODE_SECONDS` | 75 |
| `INTERMEDIATE_DAMAGE_DIVISOR` | 2 |
| `MINIMUM_TIMEOUT_DAMAGE` | 1 |

Beginner damage is zero; Advanced uses the existing shared base helper. No previous balance values changed. `MainWindow.COUNTDOWN_TICK_MILLIS = 100` is a UI refresh interval; elapsed time comes from `System.nanoTime`, not tick counts.

## 8. Verification

Full compilation from the project root, PowerShell:

```powershell
$phase7Build = Join-Path $env:TEMP 'javajolt-phase7-build'
New-Item -ItemType Directory -Force -Path $phase7Build | Out-Null
$phase7Sources = @(rg --files src tests -g '*.java')
[System.IO.File]::WriteAllLines((Join-Path $phase7Build 'sources.txt'), $phase7Sources)
javac -encoding UTF-8 -d $phase7Build ('@' + (Join-Path $phase7Build 'sources.txt'))
java -cp $phase7Build edu.batstateu.javajolt.preview.MechanicsCheck
java -cp $phase7Build ProgressConsistencyCheck
java -cp $phase7Build edu.batstateu.javajolt.preview.BalanceReport
java -cp "$phase7Build;src" SwingTimerFlowCheck
git diff --check
```

Compile exit 0; all four final check/report commands exited 0. Diff check passed; Git emitted only existing LF-to-CRLF normalization notices. ProgressConsistencyCheck initially encountered sandbox denial reading the JDK security configuration; its approved rerun passed.

Verbatim result excerpts:

```text
Checks: 410 passed, 0 failed, 410 total.
ALL CHECKS PASSED

PASS: Failed attempt
PASS: First completion
PASS: Save and load
PASS: No duplicate XP
PASS: High score preservation

ALL PROGRESSION CHECKS PASSED

  Reading notes:
    Base damage is fixed per band: (maxHp - 1) / 4 using integer division.
    Escalation grows with difficulty (2 / 3 / 4 per difficulty).
    Three consecutive mistakes defeat every band; four isolated mistakes never do.

ALL SWING TIMER FLOW CHECKS PASSED (22)
```

Mechanics coverage includes all six limits, all intended HP bands/difficulties, base-only damage despite mistake pressure, repeated expiry, same-index protection, next-question reset, Shield, Time Stop rejection/reset, score/XP/accuracy/streak preservation, post-expiry answering, and defeat settlement once.

Swing coverage includes selection/text/code preservation, frozen display beyond deadline, fresh next-question countdown, duplicate Continue rejection, Shield, lethal expiry/results, slow background answer checking, navigation, replacement, disposal, and Beginner Shield preservation. Tests interact on the EDT without reflection. The flow fixture uses deterministic questions and injected one-second limits; production rules remain unchanged.

## 9. Manual test steps for the owner

1. Launch `java -cp "$phase7Build;src" edu.batstateu.javajolt.preview.StagePreview all --xp=1000` and select a difficulty. Use preview only for save isolation.
2. Open Variables and start a quiz. Confirm 30/20/15 seconds for single-line and 120/90/75 for code, by difficulty.
3. Enter/select an answer and let time expire. Check HP, preserved input, unchanged counters, and continued submission. Wait longer: no second hit.
4. Arm Shield before expiry: Intermediate/Advanced consume it with no HP loss; Beginner preserves it for the next damaging attack.
5. Use Time Stop before expiry. Confirm remaining time freezes without damage; submit and Continue. The next countdown runs normally, while Time Stop stays used.
6. Reach low HP and expire on Intermediate/Advanced. Confirm defeat results, unchanged answered/mistake counts, zero XP, and score settlement once.
7. Leave a quiz, start another, and switch preview difficulty while a countdown runs. Old windows/questions must not apply later timeout damage or bank unfinished rewards.

## 10. Docs updated

- `PROJECT_RULES.md`: line 31 adds timer-service ownership; line 122 replaces the obsolete no-tests statement with current check locations and headful-test requirements.
- `gameplay-design-notes.md`: section 2.6 (line 85) describes implementation; section 5 (line 205), section 8 timer row (line 259), and section 9 stages 4/5 (lines 273/275) reflect implemented timing/Time Stop.
- This report added. `AGENT_BRIEF.md` and `progress.md` were not edited.

## 11. Deviations from this brief, assumptions made, and risks

The approved file plan gained `tests/SwingTimerFlowCheck.java` to verify actual UI flow. MainWindow constructor signatures now require the injected timer service; all repository callers were updated. Reassigning the same question index deliberately preserves its timeout/frozen guard. Time Stop freezes the displayed whole-second remainder; answer-check time is excluded, and exceptional retries resume from that remainder.

Limitations: automated Swing checks use shortened limits and deterministic questions, not a full-duration playthrough of the real question bank or visual layout review. Native exit confirmation and answer-error dialogs were not exercised automatically. Normal console gameplay remains without a live clock, as the brief specifies Swing timer ownership. No broader worker cancellation or unrelated navigation refactor was introduced.

## 12. Noticed but intentionally not changed

Pre-existing duplicated imports, legacy UI formatting, known DI deviations, console power-up controls, and broader design brainstorming remain as-is. No further gameplay phases or features were started. Phase 7 stops here for owner review; changes are uncommitted.
