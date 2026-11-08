# Phase 3 Report: Tutorial content and sandbox logic

## 1. Summary

The single tutorial now has pure logic and content, with thirteen deterministic teaching popups and two real multiple-choice sample questions. Each run creates a fresh Intermediate Variables session and a player whose XP is derived from the highest power-up unlock. Answers, enemy damage, scoring, HP setup, timeout and power-ups reuse the real services; the tutorial never receives real progress or a save service and never settles rewards. No Swing or first-run navigation changes are included.

The owner's revised Phase 5 routing, incomplete-save regression and newplayer preview requirements are recorded in the living gameplay notes for that later phase.

## 2. Files

Java paths are relative to `src/edu/batstateu/javajolt/` unless prefixed with `tests/`.

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `entity/TutorialStep.java` | NEW | Popup trigger, question index, title and body. |
| `enums/TutorialTrigger.java` | NEW | Three moments used by this script: SESSION_START, BEFORE_QUESTION, RESULTS. |
| `data/TutorialContent.java` | NEW | Fresh sample questions and the approved ordered text, reading gameplay numbers from rule owners. |
| `service/TutorialService.java` | NEW | Throwaway run, step tracking, scripted timing/preparation and delegation to real mechanics. |
| `service/LessonRunnerService.java` | MODIFIED | Public pass-percent getter and scripted-question start overload sharing existing enemy/HP setup. |
| `tests/TutorialLogicCheck.java` | NEW | Pure checks for text, isolation, all answer/timeout/power-up paths, once-only steps, replay and rule delegation. |
| `tests/TutorialRetuningCheck.ps1` | NEW | Recompile temporary source copies after changing real tuning constants, then verify the tutorial text against them. |
| `PROJECT_RULES.md` | MODIFIED | Add the four tutorial types to the ownership map. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED | Document implemented logic, pending UI work and revised locked Phase 5 requirements. |
| `progress-documentations/change-reports/phase-3-tutorial-logic-content.md` | NEW | This report. |

## 3. Ownership and rule check

TutorialContent owns questions and text. TutorialService owns this one script's run and shown-step state; it injects its collaborators and constructs only plain Player/GameSession-related data through the normal service path. It has no Swing, file access, save collaborator or real-player parameter. LevelService derives demo XP; LevelRewardService and EnemyRoster are reused through LessonRunnerService's shared setup. ScoreService, PowerUpService and AnswerTimerService remain the rule owners.

Only the four suggested gameplay types were added. TutorialStep also carries a question index because BEFORE_QUESTION must distinguish the two deterministic moments; no generic trigger or popup framework was introduced. No PROJECT_RULES exception, new dependency, art, audio, animation or restyling is needed.

## 4. Existing code touched

- LessonRunnerService.startSession(Player, Topic, Difficulty) still selects real quiz questions once, then delegates to the new overload accepting scripted questions. Both paths share exactly the same existing enemy creation, level-derived max HP and full restore.
- LessonRunnerService.getPassPercent exposes the existing private constant without changing pass evaluation or tuning.
- PROJECT_RULES and gameplay notes receive the ownership/status updates listed above. Existing edits in gameplay notes are preserved. AGENT_BRIEF and historical reports are not rewritten.

No existing Swing, persistence, enemy roster, scoring formula, power-up formula or normal timer limit changes in this phase. Production wiring of the new collaborators waits for UI integration; tests act as their composition root here.

## 5. Player-visible behavior changes (before -> after)

Normal gameplay is unchanged until Phase 4 integration. The reviewable tutorial script is:

1. Session start: goal, HP, damage escalation, score/streak, score records.
2. Before question one: winning, the independent losing checks, and the timer.
3. Before question two: all four power-ups, Heal practice, XP/levels and progression/Textbook.
4. At tutorial results: a short ready-to-play wrap-up, without real ResultsScreen settlement.

Both questions are simple Variables multiple-choice questions. No player must answer wrongly or wait for expiry to see any concept. The first countdown is shortened; power-ups begin at their teaching moment so Heal cannot be consumed prematurely. Heal preparation applies damage once through Player.takeDamage and leaves the demo alive. One additional base-mechanic popup, Keeping your score, explains cumulative score, per-topic/difficulty high scores and abandoned-quiz score loss; these real progress rules were missing from the requested list. This addition was reported in chat under the owner's instruction to include missing base mechanics.

## 6. Save compatibility

No persistence changes. The tutorial never receives the real Player or SaveService and never calls settlement or high-score recording. Practice XP/score may accumulate provisionally in its throwaway session only. Completing the script does not set any real tutorial-completed flag; that belongs to Phase 5 after the wrap-up is confirmed. Checks use in-memory players; no real saves are opened or written.

## 7. Balance values introduced or changed

| Owner | Constant | Value / purpose |
| --- | --- | --- |
| TutorialService | DEMO_TIMER_SECONDS | 3 seconds on the timer-demo question, capped by the normal limit if shorter. |
| TutorialService | DEMO_DIFFICULTY | INTERMEDIATE, the locked tutorial difficulty. |
| TutorialService | DEMO_TOPIC | VARIABLES, the locked tutorial topic. |
| TutorialContent | TIMER_QUESTION_INDEX | 1, the script's first practice question. |
| TutorialContent | HEAL_QUESTION_INDEX | 2, the script's power-up/Heal teaching moment. |

No real gameplay balance values change. The record's index and these script positions are one-based, matching GameSession; non-question steps use zero. Demo XP, max HP, scoring, pass percent, escalation, normal seconds, timeout damage, unlock levels, Heal percentages/recovery and completion bonus are all queried from their existing owners.

## 8. Verification

Commands run from the project root:

```powershell
$tutorialBuild = Join-Path $env:TEMP 'javajolt-tutorial-phase3-build'
New-Item -ItemType Directory -Force -Path $tutorialBuild | Out-Null
$tutorialSources = @(rg --files src tests -g '*.java')
[System.IO.File]::WriteAllLines((Join-Path $tutorialBuild 'sources.txt'), $tutorialSources)
javac -encoding UTF-8 -d $tutorialBuild ('@' + (Join-Path $tutorialBuild 'sources.txt'))
java -cp $tutorialBuild TutorialLogicCheck --print-script
java -cp $tutorialBuild TutorialLogicCheck
& .\tests\TutorialRetuningCheck.ps1
java -cp $tutorialBuild edu.batstateu.javajolt.preview.MechanicsCheck
java -cp $tutorialBuild ContentCoverageCheck
java -cp $tutorialBuild edu.batstateu.javajolt.preview.BalanceReport
git diff --check
```

Compilation passed. Final result excerpts:

```text
ALL TUTORIAL LOGIC CHECKS PASSED (639)
RETUNED TEXT CHECKS PASSED
ALL TUTORIAL LOGIC CHECKS PASSED (640)
PASS: Retuning changed only temporary source copies; project constants were untouched.
Checks: 453 passed, 0 failed, 453 total.
ALL CHECKS PASSED
ALL CONTENT COVERAGE CHECKS PASSED (21 topic/difficulty combinations, 504 questions in bank)
```

BalanceReport exited 0 with the unchanged level, survivability, enumeration and weighted results. `git diff --check` passed with only existing Git LF/CRLF normalization notices. The initial tutorial run caught a fixture that changed max HP without restoring HP, and the initial retuning compile caught Windows backslashes in quoted javac argument-file paths; both test-harness issues were corrected before the successful runs. After adding the requested missing base-mechanic score-records popup, both tutorial checks passed again at the final counts above. No gameplay failure was found.

Builds and retuned copies are under TEMP; out/, build/, real saves and assets remain untouched. The retuning script changes fifteen declarations only in copied sources, covering pass percent, score, HP, timer seconds/damage, enemy escalation, all four unlocks, all Heal percentages and the completion bonus. Each rendered numeric sequence is compared with its owning API, then all sixteen combinations of answer correctness, expiry and power-up use run against the retuned rules too. Project constants are unchanged.

The pure fixture fails immediately if tutorial setup selects from QuestionBank or calls settlement. It checks expected teaching order, one return per step/run, full concept coverage on every route, real-player isolation, shared mechanics, safe Heal preparation, no duplicate answer/timeout, and fresh replay state. `--print-script` prints the actual rendered popup text for review. RUN_TESTS.bat integration remains for the final phase.

## 9. Manual test steps for the owner

1. Run TutorialLogicCheck --print-script from the temporary build to read the actual popup text and verify the pure checks.
2. Run tests/TutorialRetuningCheck.ps1; verify RETUNED TEXT CHECKS PASSED without modifying any project constants.
3. Full QuestionScreen walkthrough, timer pausing, popup dismissal and replay exit wait for Phase 4. Phase 3 intentionally adds no menu button or first-run routing.

## 10. Docs updated

PROJECT_RULES maps TutorialService, TutorialStep, TutorialContent and TutorialTrigger. Gameplay notes section 2.8 explains the small deterministic script, reused rules and sandbox boundary. It also records the owner's Phase 5 replacements: route loaded false flags into the tutorial without Welcome, preserve the true-flag Title/menu flow, save first completion immediately, test incomplete-save relaunch, and add the newplayer preview mode with false flag and no progress. These later behaviors are not implemented here.

## 11. Deviations from this brief, assumptions made, and risks

Two questions keep the tutorial short and allow every concept to appear before any possible final defeat under current tuning, even if both answers are wrong and timers expire. Five concepts share the session-start moment, three the first question, and four the second. Only deterministic triggers are needed; no reactive event popup or unrelated concept is added. The added score-records popup covers existing base mechanics under the owner's explicit allowance. Power-ups are gated until their introduction; real per-quiz limits/effects remain in PowerUpService. Heal setup damage is sized from its real recovery and capped to leave the sample alive; the text says “up to” to remain accurate if recovery reaches the max-HP cap.

The current EnemyRoster names the Variables opponent Andr0id X across difficulties; the tutorial uses its real Intermediate object and escalation, without reinstating obsolete historical forms. The overload avoids duplicating the enemy/max-HP start rules or altering question-bank selection/history. Sample questions use negative IDs and remain entirely outside QuestionBank; their small Java numeric examples are question content, not gameplay tuning.

The popup record's added questionIndex is the only adjustment to the proposed type shapes. UI pause/resume, stale MainWindow cleanup, first-run completion/save and the revised newplayer launcher remain for their later phases. Arbitrarily lethal future retuning could require reviewing tutorial pacing; current and deliberately retuned paths are checked. The new checks will need inclusion in the final RUN_TESTS.bat work, whose current out/ output also needs reconciling with PROJECT_RULES before that final run.

## 12. Noticed but intentionally not changed

Known architecture deviations, older design-note background, enemy identities/art, persisted flags and normal navigation remain unchanged. No Phase 4 or Phase 5 implementation has started; the revised Phase 5 contract is recorded only. Stop after Phase 3 verification for owner review. Changes are uncommitted.
