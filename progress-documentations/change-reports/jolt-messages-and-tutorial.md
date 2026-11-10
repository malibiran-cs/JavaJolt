# Report: Jolt messages and tutorial

## 1. Summary

The correct-answer streak is now presented as Jolt, with persistent milestone messages and a two-second minimum display for a perfect run. Counting, scoring, damage, XP, timeouts and power-ups are unchanged. The tutorial now has five questions and fourteen popups, including an optional Jolt demonstration before question three.

## 2. Files

Java source paths below are relative to `src/edu/batstateu/javajolt/`.

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `enums/JoltMessage.java` | NEW | Text, milestone thresholds and pure answer-to-message selection. |
| `game/GameSession.java` | MODIFIED | Record the last message after correct answers and mistakes; expose its getter. |
| `preview/MechanicsCheck.java` | MODIFIED | Add milestone, perfect-run, reset, timeout, Heal and unchanged-score checks. |
| `ui/swing/Theme.java` | MODIFIED | Three milestone colors and an independent message font. |
| `ui/swing/screen/QuestionScreen.java` | MODIFIED | Jolt multiplier wording, centered message presentation and Continue enablement. |
| `ui/swing/screen/ResultsScreen.java` | MODIFIED | BEST JOLT with an x-prefixed value. |
| `ui/swing/MainWindow.java` | MODIFIED | Refresh the session message on answers/questions; guard and cancel the perfect hold. |
| `data/TutorialContent.java` | MODIFIED | Three additional questions, reworked Jolt teaching and one question-three popup. |
| `tests/TutorialLogicCheck.java` | MODIFIED | Adapt script/count/outcome checks; verify Jolt presentation data and reachable-step uniqueness. |
| `tests/SwingTimerFlowCheck.java` | MODIFIED | Walk all five questions and verify the Jolt popup, SPARK persistence and SHORT CIRCUIT. |
| `tests/SwingNavigationSmokeCheck.java` | MODIFIED | Complete the five-question tutorial in the existing first-run, replay and save-failure routes. |
| `tests/TutorialRetuningCheck.ps1` | MODIFIED | Retune four Jolt constants in disposable copies to detect hardcoded tutorial numbers. |
| `PROJECT_RULES.md` | MODIFIED | Document naming, enum ownership and Theme ownership. |
| `progress-documentations/change-reports/jolt-messages-and-tutorial.md` | NEW | Required finished-work report. |

## 3. Ownership and rule check

`JoltMessage` owns the rule; `GameSession` owns temporary answer state. Screens display supplied state, `Theme` owns appearance, `MainWindow` owns Swing timing, and `TutorialContent` owns teaching content. `TutorialService`, `ScoreService`, `LessonRunnerService` and `TitleScreen` required no edits. Existing streak identifiers and test names remain intact, except the explicitly requested private screen field/method rename.

The owner's single-pass instruction supersedes the historical phase stops and approval checkpoint. The exact requested compile/test commands authorize their generated `out/` artifacts. No other production rule exception was needed. Original source line endings were preserved.

## 4. Existing code touched

The table above lists every existing file edited. In the screen, only the Jolt label wording/width sample, CENTER message label, message mapping and Continue setter changed. The constructor's silent-zone marker, left column, progress bar, prompt, answer behavior and existing effects remain unchanged. MainWindow adds message refreshes beside its existing streak refresh calls and hold cleanup at session starts, results, navigation and disposal. Tutorial checks retain their original route purposes and reward/save assertions.

## 5. Player-visible behavior changes

- STREAK: 3 -> JOLT: x3, preserving the points suffix.
- Milestones: SPARK from x3, JOLT from x6, SURGE from x9; a perfect eligible quiz takes precedence as JAVAJOLT.
- Losing a configured minimum streak shows SHORT CIRCUIT; the next correct answer clears it until another tier is reached.
- Messages persist across question transitions; new sessions begin blank.
- Perfect-run Continue waits only for the remainder of two seconds, disables Continue/topics while waiting and settles once afterward. Tutorial runs share this path without settling practice rewards.
- BEST STREAK -> BEST JOLT, with an x-prefixed value.
- Two tutorial questions/thirteen popups -> five questions/fourteen popups, keeping five SESSION_START steps.

## 6. Save compatibility

No save schema, persistence code or persisted field changed. Existing progression checks confirm legacy saves still load. Integration checks use temporary save files and verify first completion, replay save preservation and save-failure behavior. The real `saves/` directory was not used by the checks.

## 7. Balance values introduced or changed

| Constant | Default | Owner |
| --- | --- | --- |
| `SPARK_STREAK` | 3 | `JoltMessage` |
| `JOLT_STREAK` | 6 | `JoltMessage` |
| `SURGE_STREAK` | 9 | `JoltMessage` |
| `SHORT_CIRCUIT_MIN_STREAK` | 1 | `JoltMessage` |
| `PERFECT_HOLD_MILLIS` | 2000 | `MainWindow` |
| `JOLT_QUESTION_INDEX` | 3 | `TutorialContent` |

No existing balance value changed. Restyling locations in `Theme.java`:

| Appearance | Constant and line | Default |
| --- | --- | --- |
| SPARK | `JOLT_SPARK`, line 31 | `0xFFC53D` |
| JOLT | `JOLT_JOLT`, line 32 | `0x3DD9FF` |
| SURGE | `JOLT_SURGE`, line 33 | `0xB48CFF` |
| SHORT CIRCUIT | existing `DANGER`, line 24 | `0xE5566D` |
| JAVA / JOLT! | existing `TEXT`, line 28 / `ACCENT`, line 17 | `0xEEF0F4` / `0xFF7A45` |
| Message font | `FONT_JOLT_MESSAGE`, line 41 | SansSerif, bold, 22 |

Editing a reused color also affects its other existing consumers.

## 8. Verification

At each requested checkpoint:

```powershell
javac -encoding UTF-8 -d out -sourcepath src src\edu\batstateu\javajolt\GameApplication.java
cmd /c "RUN_TESTS.bat < NUL"
```

All four checkpoint builds and full suite runs passed after correcting one new Swing assertion to account for the first choice already selected by its earlier popup test. Existing mechanics assertions were not edited or removed. Final excerpts:

```text
ALL PROGRESSION CHECKS PASSED
ALL CONTENT COVERAGE CHECKS PASSED (21 topic/difficulty combinations, 504 questions in bank)
Checks: 491 passed, 0 failed, 491 total.
ALL CHECKS PASSED
ALL TUTORIAL LOGIC CHECKS PASSED (755)
ALL SWING NAVIGATION CHECKS PASSED (55)
ALL SWING TIMER FLOW CHECKS PASSED (54)
ALL 7 ENEMY PORTRAITS PASSED
ALL JAVAJOLT TEST SUITES PASSED
```

`tests/TutorialRetuningCheck.ps1` passed with nineteen changed constants in disposable source copies:

```text
RETUNED TEXT CHECKS PASSED
ALL TUTORIAL LOGIC CHECKS PASSED (747)
PASS: Retuning changed only temporary source copies; project constants were untouched.
```

`BalanceReport` compiled to TEMP and exited 0 with the unchanged level, survivability and weighted-outcome tables. Its output includes:

```text
Base damage is fixed per band: (maxHp - 1) / 4 using integer division.
Escalation grows with difficulty (2 / 3 / 4 per difficulty).
Three consecutive mistakes defeat every band; four isolated mistakes never do.
```

Disposable graphical inspectors also passed: 284 presentation/geometry assertions, 30 hold/lifecycle assertions and 27 assertions in a live walkthrough of the actual tutorial. Normal and tutorial perfect holds measured 2,126 ms and 2,038 ms. Checks covered disabled controls, duplicate Continue calls, no timer for ordinary results, stale callbacks, navigation, replacement and disposal.

At default and maximized sizes, the message occupies the existing 50 px status row, centered between the two stacks. The progress bar stays at y=90 and the prompt viewport at y=106 relative to QuestionScreen. The player column stays 164 px; the right block remains fixed, measuring 183 px with the new widest Jolt string. The points suffix fits. Images were inspected, including the white/orange perfect message. No temporary inspector or altered game fixture was added to the repository. `git diff --check` passes.

## 9. Manual test steps for the owner

1. Run `RUN_JAVAJOLT.bat`, then start/replay the tutorial. The first three correct choices are A, B, C. Observe SPARK after question three; choose a wrong answer on question four, then answer question five correctly to see SHORT CIRCUIT followed by a blank message. Confirm the final popup.
2. In a normal quiz, build x3, x6 and x9 to inspect the gold, cyan and violet messages. Complete a perfect run and immediately press Continue; JAVAJOLT remains until its two-second minimum is met.
3. Start another quiz, confirm the message is cleared, then check BEST JOLT on results. Edit only the listed Theme constants to restyle messages and recompile.

## 10. Docs updated

`PROJECT_RULES.md` line 16 defines player-facing Jolt naming; line 39 registers `JoltMessage` under enums; line 101 states that colors/font belong to Theme. This report follows the brief's twelve-section format, without introducing phases. Pre-existing gameplay-design-note edits and the untracked brief remain untouched.

## 11. Deviations from this brief, assumptions made, and risks

There are no unverified requested graphical states on the available desktop. A death before question three necessarily prevents the later Jolt popup; the checks now assert that every reachable step appears once and an unreachable Jolt step remains unavailable. Retuned damage values can make more routes end early, so the number of executed pure assertions differs from the default run. The tutorial describes the reset as the first multiplier rather than duplicating an independent numeric counter in its prose.

## 12. Noticed but intentionally not changed

The existing ResultsScreen passing-accuracy label receives a score-derived value from MainWindow; the actual pass rule still belongs to LessonRunnerService. This pre-existing behavior, unrelated formatting/duplicate imports, earlier editor behavior and existing visual/audio effects were not changed. No new effects, assets, libraries or save fields were introduced. Changes remain uncommitted.
