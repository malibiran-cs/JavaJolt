# Phase 0 Report: Read and audit (no code changes)

## 1. Summary

Phase 0 is a read-only audit: no source file was created, modified, or deleted, and `saves/`, `out/`, and all asset files were untouched. All section 2 documents and source files were read, facts F1-F10 were each checked against the real code, and the real balance numbers (level thresholds, XP ceiling, mistakes-to-die) were computed from the compiled question bank. One fact (F5) is inaccurate as stated, and the design notes' description of the current level curve does not match the code; both are flagged below. Everything else in the brief checks out, and the proposed phase order is confirmed workable with the adjustments noted in section 11.

## 2. Files

| Path | Status | What happened |
| --- | --- | --- |
| `PROJECT_RULES.md` | READ | Binding rules confirmed (sections 2, 3, 6, 7 noted in particular). |
| `progress-documentations/gameplay-design-notes.md` | READ | Design intent confirmed; scope is core mechanics only. |
| `entity/Player` (`entity/Player.java`) | READ | STARTING_HP=10, takeDamage floors at 0, `heal` uncapped, `TODO(gameplay balance)` present. |
| `game/GameSession` (`game/GameSession.java`) | READ | Accuracy divides by `questions.size()` (all questions); no streak, no enemy, no last-damage. |
| `game/GameService` (`game/GameService.java`) | READ | Builds its own `GameSession` in `run()`. |
| `service/LessonRunnerService` (`service/LessonRunnerService.java`) | READ | `submitAnswer(...)` shared entry confirmed; HP reset in `playLesson`; XP/score/pass constants all present. |
| `service/LevelService` (`service/LevelService.java`) | READ | Current curve is quadratic `25*n*(n-1)`. |
| `service/QuestionService` (`service/QuestionService.java`) | READ | 12 questions, 4 per tier, sorted Foundational -> Application -> Challenge. |
| `Question` (`Question.java`) | READ | `getChoices()` hook exists; no hint hook. |
| `types/MultipleChoiceQuestion` (`types/MultipleChoiceQuestion.java`) | READ | Choices shuffled and re-labeled in `prepareForLesson()`; answer kept as the current letter. |
| `GameApplication` (`GameApplication.java`) | READ | Composition root for GUI branch confirmed. |
| `ui/swing/MainWindow` (`ui/swing/MainWindow.java`) | READ | `startLesson` duplicates HP reset + session build; uses own `questionIndex`. |
| `ui/swing/SwingPreview` (`ui/swing/SwingPreview.java`) | READ | Temporary launcher; uses 4-arg `MainWindow` constructor. |
| `ui/swing/screen/QuestionScreen` (`ui/swing/screen/QuestionScreen.java`) | READ | HP label, enemy-side `CharacterPlaceholder`, no streak/power-up/timer UI yet. |
| `ui/swing/screen/ResultsScreen` (`ui/swing/screen/ResultsScreen.java`) | READ | Accuracy over total; no best-streak line, no defeat line. |
| `ui/swing/screen/MainMenuScreen` (`ui/swing/screen/MainMenuScreen.java`) | READ | Calls static `LevelService.calculateLevel` and `totalXpForLevel` (per F7). |
| `service/SaveService` (`service/SaveService.java`) | READ (do-not-modify honored) | Keys: name, xp, score, difficulty, completedTopics, highScores. HP never saved. |
| `entity/SaveData` (`entity/SaveData.java`) | READ (do-not-modify honored) | Record with 6 components; matches the brief's list. |
| `enums/Topic`, `enums/Difficulty`, `enums/ChallengeTier`, `service/DifficultyProgressionService`, `service/PlayerProgressService` | READ (supporting) | 7 topics x 3 difficulties = 21 pairs; tier enum order confirms F8. |
| `tests/ProgressConsistencyCheck.java` (outside `src/`, default package) | READ (noticed) | Existing plain-main progression checker that constructs `GameSession` and calls `Player.getStartingHp()`; see section 11. |
| Temp dir `C:\Users\shakloc\AppData\Local\Temp\opencode\jj-audit\Phase0Probe.java` | TEMP TOOL (deleted from consideration; outside repo) | Compiled probe used only to count questions and compute XP; writes nothing to the repo. |

## 3. Ownership and rule check

- Phase 0 introduces no rules, owners, or code. All work was reading, compiling to a temp directory, and running a throwaway probe class from the OS temp folder.
- PROJECT_RULES compliance: no rule touched. `out/`, `saves/`, kit assets were never used as targets; the baseline compile went to `Temp\opencode\jj-audit-build`, per the brief's protocol (same practice, temp path).
- No rule was bent. No clarifying questions were needed; the two findings in section 11 are report items, not blockers.

## 4. Existing code touched

None. No edit was made to any pre-existing file.

## 5. Player-visible behavior changes (before -> after)

None. Phase 0 makes no changes.

## 6. Save compatibility

- The real `saves/player.properties` was never read or written during this phase. The probe used only `QuestionBank`.

## 7. Balance values introduced or changed

No new balance values (audit only). Current values found in code, as the baseline for tuning:

| Value | Name | File | Current value |
| --- | --- | --- | --- |
| Starting HP | `STARTING_HP` | `Player` | 10 |
| Mistake damage | `DAMAGE_PER_MISTAKE` | `LessonRunnerService` | 2 |
| Points per correct | `POINTS_PER_CORRECT` | `LessonRunnerService` | 10 |
| Pass mark | `PASS_PERCENT` | `LessonRunnerService` | 70 (%) |
| XP per quiz budget | `XP_BUDGET_PER_QUIZ` | `LessonRunnerService` | 25 |
| XP per answer | `XP_PER_CORRECT` (cap) | `LessonRunnerService` | 5 (min 1) |
| Completion bonus | `LESSON_COMPLETE_XP` | `LessonRunnerService` | 50 |
| Questions per quiz | `QUESTIONS_PER_QUIZ` | `QuestionService` | 12 (4 per tier) |
| XP curve step | `XP_STEP` | `LevelService` | 50 (used as `25*L*(L-1)` total) |

### Computed real numbers (Phase 0 deliverables)

**A. Level thresholds under the CURRENT curve** (`totalXpForLevel(n) = 25*n*(n-1)`):

| Level | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 15 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Total XP | 0 | 50 | 150 | 300 | 500 | 750 | 1,050 | 1,400 | 1,800 | 2,250 | 5,250 |

**B. XP ceiling (first-clear only):** every one of the 21 topic/difficulty pairs has 24 available questions, so each quiz is a full 12 questions. Per-answer XP = `round(25/12) = 2`, so a perfect first clear earns 24 XP + the 50 XP completion bonus = **74 XP per pair**. Total realistic ceiling = 21 x 74 = **1,554 XP**, which reaches **Level 8** (1,400 XP); Level 9 (1,800 XP) is unreachable. Confirms F1: unlocks at Level 10 (Shield) and Level 15 (Time Stop) cannot be reached under the current curve. *(For reference, under the proposed D1 linear curve the same 1,554 XP would reach Level 31, so the Level 5/10/15/20 unlocks become reachable during normal play.)*

**C. Mistakes-to-die TODAY:** HP 10, damage 2, so `ceil(10/2) = 5` mistakes kill, on every difficulty. In a 12-question quiz, 70% accuracy over all 12 allows only 3 mistakes, so accuracy fails the run first (the notes' section 2.2 analysis is confirmed by the code).

**D. Planned-curve check (D1 + D3 + section 5 damage), mistakes-to-die by HP band** — the brief asks to flag every pair above 3:

| Difficulty (damage) | HP 10 (L1-2) | HP 11 (L3-5) | HP 12 (L6-8) | HP 13 (L9-11) | HP 14 (L12-14) | HP 15 (L15+) |
| --- | --- | --- | --- | --- | --- | --- |
| Beginner (4) | 3 | 3 | 3 | **4 - FLAG** | **4 - FLAG** | **4 - FLAG** |
| Intermediate (5) | 2 | 3 | 3 | 3 | 3 | 3 |
| Advanced (6) | 2 | 2 | 2 | 3 | 3 | 3 |

The only flags are **Beginner at (typical) level 9 and above, mistakes-to-die = 4**. This exactly matches the brief's own expectation ("Beginner 3 up to Level 8 and 4 from Level 9"), so it is anticipated, not a surprise; it is flagged here per the brief's instruction. One nuance versus the brief's wording: Intermediate reaches mistakes-to-die 3 already at Level 3 (HP 11), not "from Level 9" (it is only 2 at Levels 1-2), and Advanced reaches 3 already at Level 12 (HP 13). Both are at or below 3 at every level, so no action is proposed.

## 8. Verification

Commands run (from the project root; nothing written into the repo or `out/`):

1. Baseline compile of all sources to a temp directory:
   `javac -d C:\Users\shakloc\AppData\Local\Temp\opencode\jj-audit-build (all files under src\*.java)` -> **exit code 0, compiles cleanly**.
2. Throwaway probe (`Phase0Probe.java` in `Temp\opencode\jj-audit\`, later discarded) compiled against that build and run to count the real question bank and compute the numbers in section 7. Full output:

```
topic;difficulty;available;quizSize;xpPerCorrect;maxQuizXp
VARIABLES;BEGINNER;24;12;2;24
VARIABLES;INTERMEDIATE;24;12;2;24
VARIABLES;ADVANCED;24;12;2;24
OPERATORS;BEGINNER;24;12;2;24
OPERATORS;INTERMEDIATE;24;12;2;24
OPERATORS;ADVANCED;24;12;2;24
DECISIONS;BEGINNER;24;12;2;24
DECISIONS;INTERMEDIATE;24;12;2;24
DECISIONS;ADVANCED;24;12;2;24
LOOPS;BEGINNER;24;12;2;24
LOOPS;INTERMEDIATE;24;12;2;24
LOOPS;ADVANCED;24;12;2;24
ARRAYS;BEGINNER;24;12;2;24
ARRAYS;INTERMEDIATE;24;12;2;24
ARRAYS;ADVANCED;24;12;2;24
EXCEPTION_HANDLING;BEGINNER;24;12;2;24
EXCEPTION_HANDLING;INTERMEDIATE;24;12;2;24
EXCEPTION_HANDLING;ADVANCED;24;12;2;24
OOP;BEGINNER;24;12;2;24
OOP;INTERMEDIATE;24;12;2;24
OOP;ADVANCED;24;12;2;24
GRAND_TOTAL_QUESTIONS=504
TOTAL_FIRST_CLEAR_XP(quizXp+50bonus)=1554
CURVE L1=0
CURVE L2=50
CURVE L5=500
CURVE L8=1400
CURVE L9=1800
CURVE L10=2250
CURVE L15=5250
CEILING_LEVEL=8 at 1554 XP
```

MechanicsCheck/BalanceReport could not be run: they do not exist until Phase 2/3. `BalanceReport` and `MechanicsCheck` will be created then.

## 9. Manual test steps for the owner

None required for Phase 0 (no change). The audit numbers above can be re-verified at any time with `preview/BalanceReport` after Phase 2.

## 10. Docs updated

None. Per the brief, documentation updates to `gameplay-design-notes.md` and `PROJECT_RULES.md` begin with the first implementing phase (Phase 2); Phase 0 changes no docs. The doc corrections identified for those phases are listed in section 11.

## 11. Deviations from this brief, assumptions made, and risks

**F5 is inaccurate as stated and is the one deliberate challenge to the brief's fact list.** The brief says `session.setCurrentQuestionIndex` is "only maintained by the console flow." In fact `MainWindow.showNextQuestion()` already calls `session.setCurrentQuestionIndex(questionIndex + 1)` every time a question is shown (MainWindow, the block just before `questionScreen.showQuestion(...)`). Nuance: `MainWindow.submitAnswer(...)` guards with its own `questionIndex` field, and `startLesson` does not set the session index explicitly (a new session starts at 0), so the console and Swing flows are still not identical, but the claim "only maintained by the console flow" is false. This is good news for Phase 7: a per-question timer flag reset in `setCurrentQuestionIndex` will fire in both flows already.

**The design notes (section 2) misdescribe the current level curve.** They state "Level 2 begins at 100 XP; each later level requires another 50 XP" as current behavior, but the code implements the quadratic curve (`L2 = 50`, `L5 = 500`). The brief's F1 anticipated exactly this. When Phase 2 lands, the notes' row must be updated to describe the new (D1) curve as implemented, not as "already linear today."

**Confirmed-fact summary (F1-F10):**

| Fact | Verdict |
| --- | --- |
| F1 curve + ceiling | CONFIRMED (quadratic curve; ceiling 1,554 XP = Level 8; L10/L15 unlocks unreachable) |
| F2 HP/damage 10/2 | CONFIRMED (5 mistakes kill; 70% over 12 questions allows 3) |
| F3 accuracy over all questions | CONFIRMED (`correctCount * 100 / questions.size()`) |
| F4 duplicated session start | CONFIRMED (`playLesson` resets HP, `MainWindow.startLesson` resets HP + builds session, `GameService.run` builds session) |
| F5 session question index | NOT CONFIRMED as written (see above) - Swing does maintain it |
| F6 shared `submitAnswer` | CONFIRMED (both `GameService` console path and `MainWindow` Swing path call it) |
| F7 `LevelService` call sites | CONFIRMED (`MainMenuScreen` progress bar, `MainWindow` progress dialog) |
| F8 tier ordering | CONFIRMED (`ChallengeTier.FOUNDATIONAL, APPLICATION, CHALLENGE`; sorted in `selectQuestions` STEP 5) |
| F9 no hint hook | CONFIRMED (`getChoices()` is polymorphic; no hint method on `Question`) |
| F10 save contents, HP session-only | CONFIRMED (6 keys; no HP in `SaveData`/`SaveService`) |

**Risks / assumptions carried into Phases 1-7 (stated up front, per working rule 10):**

1. **`tests/ProgressConsistencyCheck.java` compiles against `GameSession`'s constructor and `Player.getStartingHp()`.** It lives outside `src/` (default package) and the brief does not mention it. If Phase 3 makes the `GameSession` constructor require an `Enemy`, or Phase 2 removes/renames `STARTING_HP`, this dev tool stops compiling. Assumption: keep `GameSession`'s existing 4-arg constructor working (overload that creates a neutral/placeholder state or takes Enemy in `startSession`-side setter), or minimally update that one dev tool and list it in the report. I will state the exact choice in the Phase 2/3 plans before editing.
2. **`getStartingHp()` usage after Phase 2:** the two flow call sites (`LessonRunnerService.playLesson`, `MainWindow.startLesson`) are replaced by `startSession`, but `tests/ProgressConsistencyCheck` still calls it, so it will not be "unused" unless that tool is updated; I will report its exact status.
3. **`SwingPreview` currently can write to the real `saves/`:** it builds `MainWindow` via the 4-arg constructor, which does `new SaveService()` (default path), and the lesson-end flow calls `saveProgress()`. Phase 1's `StagePreview` must therefore use the 5-arg constructor with a temp-directory `SaveService`; this existing exposure is not fixed in Phase 0 (no code changes) and is not part of the brief's scope to "fix" otherwise.
4. **Two completion paths exist today:** the console path records completion + bonus inside `GameService.run`, while the Swing path uses `LessonRunnerService.recordLessonCompletion`. `startSession` (Phase 2) unifies session start; completion handling stays as-is per D8.
5. **Balance flags:** the only planned (difficulty, level) pair above 3 mistakes-to-die is Beginner at level >= 9 (= 4). No value change is proposed now; the owner may later want Beginner damage 4 limited to early levels, but per D-lock the numbers stay as section 5 states and this is only flagged.
6. **Assumption on phase numbering:** Phase 1 (StagePreview) touches only new files plus possibly a tiny `GameApplication`-style wiring in the new launcher itself; nothing in Phases 1-7 is expected to require a save-format change (F10/D10 confirmed).

**Proposed one-paragraph plan for Phases 1-7.** Phase 1 adds `preview/StagePreview` (own `main`, builds the same objects as the GUI branch, pre-completes prior difficulties at 75 XP each, `SaveService` anchored in a temp dir via the existing `SaveService(Path)` overload, opens on `MAIN_MENU` with a `[PREVIEW - not saved]` title tag) so the owner can test higher stages safely; Phase 2 changes `LevelService` to the D1 linear curve, adds `LevelRewardService.maxHpForLevel` and `Player` max-HP/cap/full-restore, and funnels all three duplicated session starts through a new `LessonRunnerService.startSession`, with `BalanceReport` + `MechanicsCheck` born here; Phase 3 adds `game/Enemy` (one overridable `resolveMistake` hook), `data/EnemyRoster`, enemy in `GameSession`, `getAnsweredCount`, answered-based accuracy, enemy-sourced damage in `LessonRunnerService`, and a survivability table to `BalanceReport`; Phase 4 adds streak state to `GameSession` and a new `ScoreService` (`POINTS_PER_CORRECT` moves there) while `submitAnswer`'s signature and the XP rule stay untouched; Phase 5 adds `enums/PowerUp`, `entity/PowerUpResult`, stateless `PowerUpService`, `getHintChoicesToRemove` (default empty, overridden by `MultipleChoiceQuestion` to hide one random wrong choice), shield/boost arming in the `submitAnswer` flow - logic only, no UI yet; Phase 6 wires the visuals (`HP: x/y`, enemy name in the existing placeholder, streak label, unlocked power-up button row, hint hiding a choice, defeat-by-name and results lines) after posting an insertion-point plan for approval; Phase 7 (optional, owner-gated) adds `AnswerTimerService` limits keyed by `InputMode`, per-difficulty timeout damage that a Shield can absorb, at-most-once-per-question application on a flag reset by `setCurrentQuestionIndex`, and the real Time Stop consumption. Every phase ends with compile to a temp dir, appended `MechanicsCheck` scenarios, an updated `BalanceReport`, the section 8 report, and the notes/rules doc updates.

## 12. Noticed but intentionally not changed

- `Player.heal(amount)` has no cap and could exceed starting HP; Phase 2 caps it at `maxHp` per the brief.
- `Player` has unused-looking `deductScore`; no references found outside `Player`. Left alone (smallest-change rule).
- `QuestionScreen` hardcodes colors `0x403128`, `0xC8D4F0`, `0x504037` inside the screen instead of `Theme` (this is close to the section 7 deviations list); not fixed, not copied.
- `QuestionService.selectQuestions` has an oddly indented middle block and duplicated imports (`EnumMap`, `Comparator` imported twice); left alone.
- `QuestionScreen.showLessonResult` exists but appears unused by `MainWindow` (results go to `ResultsScreen`); left alone.
- `MainWindow` had a stray duplicated `.setVisible`? No - none found; but it does leave `questionScreen` text as "LESSON RESULTS" leftover path unused (same as above point).
- The `TODO(gameplay balance)` comments in `Player` and `LessonRunnerService` (two in the runner) remain until their phases resolve them, per brief rule 9.
- `tests/ProgressConsistencyCheck.java` (default package, outside `src/`) is a legacy-but-working progression checker; see section 11 item 1 before Phases 2-3 change its dependencies.
- Both `achievementsAtLessonStart` and `xpAtLessonStart` patterns in `MainWindow` are the existing before/after pattern the brief reuses for power-up unlock detection in Phase 6.
- `enum` order for replay/next-topic logic in `GameService.run` assumes topic order always equals availability; untouched per D8.

**Phase 0 is complete. Stopping here as instructed - no Phase 1 work has started.**
