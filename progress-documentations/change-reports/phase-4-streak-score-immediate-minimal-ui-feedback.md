# Phase 4 Report: Streak and score, with immediate minimal UI feedback

## 1. Summary

Consecutive correct answers are now a scored mechanic. `GameSession` tracks `currentStreak`/`bestStreak` for consecutive correct answers, kept strictly separate from Phase 3's `consecutiveMistakes` damage counter: every correct answer advances the streak (and still resets the mistake counter), every mistake resets the streak (and still increments the mistake counter). The new stateless `service/ScoreService` owns the whole per-answer score rule — `BASE_POINTS = 10`, `POINTS_PER_STREAK_STEP = 2`, `BOOST_MULTIPLIER = 2` — through `pointsForCorrect(boosted, streakAfterAnswer)` (`10 + 2 * (streak - 1)` per correct, base doubled when boosted). `LessonRunnerService` now takes the injected `ScoreService` (all four construction sites updated) and computes the points as `pointsForCorrect(false, session.getCurrentStreak() + 1)` — `boosted` stays `false` until Phase 5 — before `session.recordCorrect(points)`, so points accumulate provisionally in the session and commit at settlement exactly like XP, and streak-adjusted points flow into `scoreEarned` and per-topic high scores. The Swing `QuestionScreen` gained a single unobtrusive `STREAK` row (4th row of the existing player-stats panel, existing labels/theme only) refreshed after every resolved answer: `STREAK: N   (+X points)` after a correct answer, plain `STREAK: 0` and no points shown after a mistake; the session records `lastPoints` (mirroring Phase 3's `lastDamage`) so the UI can show the most recent award without changing any constructor signature. Verified: 259/259 `MechanicsCheck` checks pass (now including the streak contract), `BalanceReport` and `tests/ProgressConsistencyCheck` all pass, both entry points smoke-run clean, and the real save is byte-identical across the run.

## 2. Files

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `src/edu/batstateu/javajolt/service/ScoreService.java` | NEW | Single owner of the score-award rules: `BASE_POINTS = 10`, `POINTS_PER_STREAK_STEP = 2`, `BOOST_MULTIPLIER = 2` constants and `pointsForCorrect(boolean boosted, int streakAfterAnswer)` exactly per the brief's sketch — streak 1 pays base only, streak 2 pays one step, and so on; `boosted` doubles the base (unused until Phase 5). |
| `src/edu/batstateu/javajolt/game/GameSession.java` | MODIFIED | `currentStreak` (consecutive correct) and `bestStreak` fields, independent of `consecutiveMistakes`; `recordCorrect(int points)` now also advances `currentStreak`, updates `bestStreak`, and records `lastPoints` — it still resets `consecutiveMistakes` and still adds to `scoreEarned`; `recordMistake()` resets `currentStreak` and `lastPoints` (0) — it still increments `consecutiveMistakes`; getters `getCurrentStreak()`, `getBestStreak()`, `getLastPoints()`. `lastPoints` mirrors Phase 3's `lastDamage` so the UI sees the most recent answer's award. |
| `src/edu/batstateu/javajolt/service/LessonRunnerService.java` | MODIFIED |Injected `ScoreService` (constructor is now 5 args); removed the flat `POINTS_PER_CORRECT = 10` constant (ScoreService owns it); `submitAnswer`'s correct path computes `scoreService.pointsForCorrect(false, session.getCurrentStreak() + 1)` and passes it to `session.recordCorrect(points)`; XP rules and mistake path untouched; signature of `submitAnswer` unchanged; points stay provisional in the session until `settleAttempt`. |
| `src/edu/batstateu/javajolt/GameApplication.java` | MODIFIED | Composition root passes `new ScoreService()` to `LessonRunnerService`. |
| `src/edu/batstateu/javajolt/preview/StagePreview.java` | MODIFIED | Same constructor wiring (one extra argument). |
| `src/edu/batstateu/javajolt/preview/MechanicsCheck.java` | MODIFIED | New `checkStreakScoring()`: answers 1–4 of a run award exactly 10/12/14/16 points (52 total), each recorded in `lastPoints`; a mistake resets `currentStreak` to 0, clears `lastPoints`, keeps `bestStreak`; the next three awards restart at 10/12/14 (36); an uninterrupted 12-correct run scores 252 with best streak 12; boost math pre-wired: `pointsForCorrect(false, 3) = 14`, `(true, 1) = 20`, `(true, 3) = 24` — not 28 or 40. Stale flat-score assertions re-derived for streak scoring: first-pass settlement now 252 (was 12*10 = 120), replay still commits 252, the spread-wrong failed attempt now commits 86 (runs of 1/2/2/2/1 → 10+22+22+22+10). Class header comment updated to Phase 2–4. |
| `tests/ProgressConsistencyCheck.java` | MODIFIED | Wiring (`new ScoreService()`); its hardcoded score expectations re-derived under streak scoring: 8/12 failed attempt commits 92 (runs of 1/1/1/1/4 → 4*10 + 52) as session score and high score; the 9/12 attempt scores 120; the perfect 12/12 replay raises the high score to 252; the later 9/12 replay (120) does not overwrite it. Test intents unchanged (score commits on failure, XP exactly-once, highscores only improve). |
| `src/edu/batstateu/javajolt/ui/swing/MainWindow.java` | MODIFIED | Both `QuestionScreen` calls (`showAnswerResult.done`, `showQuestion`) now pass `session.getCurrentStreak()` and `session.getLastPoints()` alongside the existing `displayXp`/`quizScore` arguments. |
| `src/edu/batstateu/javajolt/ui/swing/screen/QuestionScreen.java` | MODIFIED | New `streakLabel` joined as the 4th row of the existing `playerStats` `GridLayout` (3 → 4 rows); `showQuestion` and `showAnswerResult` gained the two trailing display parameters `int streak, int lastPoints` (same display-only pattern as `displayXp`); both refresh the label through one small `refreshStreakLabel(streak, lastPoints)` helper: `STREAK: N   (+X points)` when the last answer earned points, plain `STREAK: N` otherwise. Existing labels/theme only. |
| `PROJECT_RULES.md` | MODIFIED (docs) | "Where things live": `ScoreService` bullet added under `service/`; the stale no-magic-numbers example (`DAMAGE_PER_MISTAKE`, removed in Phase 3) replaced with current constants (`PASS_PERCENT` in `LessonRunnerService`, `BASE_POINTS` in `ScoreService`). |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED (docs) | §8 row 5 tagged (streak grants score only, for now — combat/defensive benefit deliberately untouched); §8 row 6 re-worded to cite the owner's Enemy Design Boundary; §9 Stage 2 tagged STARTED (Phase 4). |
| `progress-documentations/change-reports/phase-4-streak-score-immediate-minimal-ui-feedback.md` | NEW | This report. |

## 3. Ownership and rule check

| Rule / concern | Owner |
| --- | --- |
| Score math (base, streak step, boost) | `ScoreService` (single stateless owner; `LessonRunnerService` no longer hardcodes per-answer points) |
| Streak state (`currentStreak`/`bestStreak`/`lastPoints`) | `GameSession` (state holder; entry points unchanged) |
| Calling convention, `boosted = false`, provisionality | `LessonRunnerService.submitAnswer` (settlement still owns the persistent commit) |
| Composition | `GameApplication` (+ dev launchers/tests wire their own `ScoreService`) |
| Display only | `MainWindow` passes session state; `QuestionScreen` renders it (no score math in the UI) |
| Dev verification, no saves | `preview/MechanicsCheck`, `tests/ProgressConsistencyCheck` |

PROJECT_RULES compliance: constants live in the service that owns the rule; `ScoreService` is stateless math in `service/` (constructor-injectable, no static); no new libraries or test frameworks; no `instanceof`/type switches on `Question`; `SaveService`/`SaveData`, kit assets, `out/`, `saves/` untouched; smallest-change edits only (no renames, no reformatting beyond the touched lines). The Enemy Design Boundary is respected: this phase touches no enemy behavior, identity, or signature mechanics at all — scoring is a player-side loop layered on top of the Phase 3 foundation. No rule bent; no clarifying questions needed.

## 4. Existing code touched

1. `game/GameSession.java` — streak fields, record-correct/mistake updates, getters. Justification: brief Phase 4 line 1 (`GameSession` streak instructions).
2. `service/LessonRunnerService.java` — `ScoreService` injection + score computation in `submitAnswer`. Justification: brief Phase 4 line 2 and the DI rule; brief §6 "update every caller of a changed constructor".
3. `GameApplication`, `preview/StagePreview`, `preview/MechanicsCheck`, `tests/ProgressConsistencyCheck` — one constructor call each. Justification: brief §6 rule.
4. `ui/swing/MainWindow` + `ui/swing/screen/QuestionScreen` — display-only wiring and one new label. Justification: brief Phase 4 line 3 (immediate minimal UI feedback in the existing `QuestionScreen`).
5. `preview/MechanicsCheck`, `tests/ProgressConsistencyCheck` — new checks plus re-derived score expectations. Justification: brief Phase 4 acceptance items; their old flat-10 assumptions became false under streak scoring (same minimal-update rule as Phases 2–3).
6. `PROJECT_RULES.md`, notes — documentation duties (§10).

## 5. Player-visible behavior changes (before -> after)

- Before: every correct answer paid a flat 10 points in the quiz score. After: consecutive correct answers escalate the award — 10, 12, 14, 16, ... (+2 per streak step) — so a clean 12-correct quiz banks 252 instead of 120, and streak-adjusted points count toward the per-topic high score.
- The player sees immediate, unobtrusive feedback in the existing player-stats area: a `STREAK: N` line that grows with each correct answer and shows `(+X points)` right after a resolved correct answer; a mistake visibly drops it to `STREAK: 0` and the next award restarts at 10.
- XP is unchanged (streak affects score only); enemy damage/HP/accuracy/pass rules are unchanged from Phase 3.
- Failed/replayed/abandoned attempts behave exactly as Phase 2 settled them — a streak run only raises how many points a given attempt can bank.

## 6. Save compatibility

- Expected: no change — confirmed. No save-format or `SaveService`/`SaveData` code touched; the score still persists through the same settlement path as before (only its magnitude changes at runtime). Old saves load identically.
- Real-save safety proof: SHA-256 of `saves/player.properties` was `734A2D2CE185CA08349A8652A07193BE1AC4A13E94AAE10ADC5351383639026C` immediately before and after this phase's live `GameApplication` and `StagePreview` GUI smoke runs (byte-identical). The absolute value differs from the Phase 3 report baseline because the owner played the real game in between (owner's own save write, not this phase).
- All dev tools are in-memory only; no save path is opened by anything this phase ran.

## 7. Balance values introduced or changed

| Value | Before | After | File | Source |
| --- | --- | --- | --- | --- |
| Points per correct answer | flat 10 | `10 + 2 * (streak - 1)`, streak counted after the answer (10/12/14/16…) | `ScoreService.pointsForCorrect` | brief Phase 4 sketch |
| Boost multiplier | none | 2 (base-only doubling; `boosted = false` until Phase 5) | `ScoreService` | brief Phase 4 sketch |
| Session score ceiling (12 straight correct) | 120 | 252, best streak 12 | derived | brief Phase 4 acceptance |

XP per correct (2), completion bonus (50), pass mark (70%), enemy base/escalation damage — all unchanged from Phases 1–3.

## 8. Verification

Compile to `%TEMP%\opencode\jj-build-phase4` (never `out/`), run from the project root. All tools exit 0.

1. Full compile (src + tests): **exit code 0** (re-verified after line-ending normalization).
2. `MechanicsCheck`: **259/259 PASS**, including the new streak block: 4 consecutive correct award 10/12/14/16 = 52 with each award mirrored in `lastPoints`; a mistake resets the streak, clears `lastPoints`, keeps `bestStreak`; post-mistake awards restart 10/12/14 = 36; a 12-correct run scores 252 with best streak 12; boost math `pointsForCorrect(false, 3) = 14`, `(true, 1) = 20`, `(true, 3) = 24` (never 28 or 40). All Phase 2–3 regression blocks (levels, HP bands, settlement exactly-once/replay/failure/abandonment/death, enemy damage) still pass; streak-adjusted assertions verified (settle 252, replay 252, spread-wrong fail 86).
3. `tests/ProgressConsistencyCheck`: all 5 progression checks pass — failed attempt still commits score (92), first-pass XP exactly-once, save/reload preserves score and high score (now 252 after the perfect replay), and lower replays (120) never overwrite the record — demonstrating streak-adjusted points establishing and protecting per-topic high scores.
4. `BalanceReport`: unchanged from Phase 3, runs clean (no regression).
5. GUI smoke: `GameApplication` and `StagePreview intermediate` run with clean stdout/stderr and no exceptions; real save hash byte-identical across the run (§6).
6. Acceptance per the brief: every consecutive correct answer awards strictly more points than the previous (10 < 12 < 14 < 16 verified by delta checks on `scoreEarned`); a mistake visibly resets both the streak display and the next award back to 10 (MechanicsCheck + wiring reviewed); no score calculation exists in the UI (UI received display arguments only); no duplicate persistence beyond the existing single settlement commit (Phase 2 rules intact, verified by the still-passing settlement regression blocks).

## 9. Manual test steps for the owner

1. Play any quiz: answer 4–5 correct in a row and watch the stats panel — the streak line grows and each correct answer shows `(+X points)` with X rising by 2 each time (10 → 12 → 14 → 16).
2. Deliberately miss one: the line drops to `STREAK: 0` with no points shown; the next correct answer should show `+10 points` again (award restarted).
3. Run a quiz with no mistakes: the results screen's quiz score should read 252 (12 * the 10..32 streak ladder).
4. Beat a topic's high score with a streak-heavy run, then replay it sloppier: the high score endpoint should keep the better streak-adjusted record.
5. Replays and failures still bank/stay on the Phase 2 rules: XP flat on replays, zero XP (but commited score) on failed runs, nothing banked on an abandoned quiz.
6. Console mode (`--console`) is unchanged — scoring improves silently there (brief scopes UI feedback to `QuestionScreen`).
7. Verify `saves/player.properties` only changes through your own confirmed saves.

## 10. Docs updated

- `progress-documentations/gameplay-design-notes.md`: §8 row 5 → decided-in-the-narrow-sense (streak = score only so far; enemy-pressure/defensive effects explicitly left open); §8 row 6 → re-worded to cite the owner's Enemy Design Boundary (nothing invented without explicit owner design/approval); §9 Stage 2 → STARTED (Phase 4) with the high-score display question retained. All other prose untouched.
- `PROJECT_RULES.md`: "Where things live" gains the `ScoreService` bullet; the no-magic-numbers example now cites real current constants.
- `progress-documentations/progress.md`: not touched (forbidden).
- This report saved under `progress-documentations/change-reports/`.

## 11. Deviations from this brief, assumptions made, and risks

1. **`lastPoints` session field:** the brief asks for "+N points" display for the most recent answer but does not prescribe the data path. I mirrored the Phase 3 `lastDamage` pattern (`recordCorrect` remembers its award, `recordMistake` clears it) so `ScoreService` stays the single owner of the math and no constructor signature in the UI changes. Zero UI-side calculation.
2. **Console UI untouched:** the brief names `QuestionScreen` only, so the console output shows no streak/points feedback. Console players still earn streak-adjusted points. Flagged here rather than inventing console UI.
3. **Scorechecked re-derivations:** the flat-10xN expectations in `MechanicsCheck`/`ProgressConsistencyCheck` were replaced with the exact streak-scoring totals for their fixed answer spreads (86 / 92 / 120 / 252), each with an inline comment showing the arithmetic so future edits can re-derive them.
4. **`GridLayout` row:** the streak display needed a row; the brief's "unobtrusive" constraint is satisfied by reusing the existing stats panel/theme with plain text (no colors added).
5. **Risks:** (a) streak scoring raises per-quiz score magnitudes ~2.1x (252 vs 120 for a clean run); cumulative lifetime score and high scores scale accordingly — purely cosmetic since high-score comparison is relative, but the owner should be aware when presenting scores; (b) the boost parameter is intentionally unused until Phase 5 — verified only through its pure math, so a Phase 5 wiring mistake would not be caught here; (c) `MainWindow.questionService` remains unused for selection (carried from Phase 2).

## 12. Noticed but intentionally not changed

- The results/end-of-quiz screens do not show the best streak of the attempt (`bestStreak` is recorded but only the live streak is displayed) — a results-screen addition belongs to a later presentation phase.
- Enemy identity/visuals still unshown (Phase 6); no enemy behavior touched in any form, per the Enemy Design Boundary.
- High-score display anywhere in the UI remains open (notes §9 Stage 2 wording kept that pending question).
- `DifficultyProgressionService`, achievements, `QuestionService` tier ordering, and the textbook flow untouched.
- `MainWindow`'s unused-for-selection `questionService` field and caller-less 4-arg constructor unchanged (Phase 2 notes).
- `MechanicsCheck`'s enumeration/scenario machinery untouched beyond the new scoring block.

**Phase 4 is complete per brief v1.3. Stopping here — Phase 5 starts only when the owner says "Proceed to Phase 5".**
