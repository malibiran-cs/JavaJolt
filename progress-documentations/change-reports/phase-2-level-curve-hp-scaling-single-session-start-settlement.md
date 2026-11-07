# Phase 2 Report: Level curve, HP scaling, single session start, reward settlement

## 1. Summary

Levels now follow the linear D1 curve (level 1 below 100 XP, level n starting at 50·n XP), and levels give a mechanical benefit for the first time: each quiz starts with a level-derived max HP (10 at L1–2, rising by 1 every 3 levels, capped at 15 at L15+), applied by the new single start-of-quiz home `LessonRunnerService.startSession`, which both the console flow and the Swing flow call. The reward economy was consolidated into `LessonRunnerService.settleAttempt`: score accumulates in the session and only commits when an attempt reaches the normal end-of-quiz path (pass or HP defeat), while first-clear per-answer XP plus the 50 XP completion bonus commit together only on a pass of an uncleared pair — failed attempts, completed replays, and abandoned quizzes earn no XP, and an abandoned quiz banks no score. Settlement runs exactly once per attempt (session settled flag). Two new read-only dev tools, `preview/BalanceReport` and `preview/MechanicsCheck`, verify the curve, HP bands, and settlement rules (43/43 checks pass) without touching `saves/`.

## 2. Files

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `src/edu/batstateu/javajolt/service/LevelService.java` | MODIFIED | D1 linear formula (`totalXpForLevel(1)=0`, `totalXpForLevel(n)=50*n` for n≥2, `calculateLevel(xp)= xp<100 ? 1 : xp/50`). Both public static signatures kept. |
| `src/edu/batstateu/javajolt/service/LevelRewardService.java` | NEW | `static int maxHpForLevel(int level)` per D3: `10 + min(level/3, 5)`. Pure stateless math. |
| `src/edu/batstateu/javajolt/entity/Player.java` | MODIFIED | New `maxHp` field (default 10) with `getMaxHp()`/`setMaxHp(int)` (guarded ≥ 1), capped `heal(int)` (clamps to `maxHp`), `restoreFullHp()`. Old methods and signatures kept. The resolved `TODO(gameplay balance)` comment (separating current from maximum HP) removed per brief §7.9. |
| `src/edu/batstateu/javajolt/game/GameSession.java` | MODIFIED | New `provisionalXp` (+`addProvisionalXp`, `getProvisionalXp`) and `settled` flag (+`markSettled`, `isSettled`). `recordCorrect`/`recordMistake` texts unchanged in responsibility. |
| `src/edu/batstateu/javajolt/service/LessonRunnerService.java` | MODIFIED | New `startSession(Player, Topic, Difficulty)` (questions via the existing `QuestionService`, session, max-HP from level, full HP restore). `submitAnswer` no longer touches `player.addScore`/`player.addXp`; score/provisional XP accumulate in the session. `recordLessonCompletion` replaced by `settleAttempt` (exactly-once settlement; returns whether the pair was newly completed). `player.setHp(getStartingHp())` in `playLesson` replaced by `startSession`'s restore. Balance TODOs for damage/HP kept (Phase 3 scope). |
| `src/edu/batstateu/javajolt/game/GameService.java` | MODIFIED | Console flow now calls `startSession` instead of building the session itself; after `playLesson` it calls `settleAttempt(session)` before `recordHighScore`; the old inline `isLessonComplete` + bonus-XP block was absorbed by settlement. Unused `Question` import removed. |
| `src/edu/batstateu/javajolt/ui/swing/MainWindow.java` | MODIFIED | `startLesson` reset + session creation replaced by `lessonRunnerService.startSession(...)`; end-of-quiz calls `settleAttempt(session)` before `recordHighScore` (the `lessonResultRecorded` UI guard stays). Mid-quiz `showQuestion`/`showAnswerResult` calls pass `player.getXp() + session.getProvisionalXp()` as the displayed XP so replays never look like they earn uncommittable XP. |
| `src/edu/batstateu/javajolt/ui/swing/screen/QuestionScreen.java` | MODIFIED | `showQuestion(...)` and `showAnswerResult(...)` gained a `displayXp` int parameter used in `xpScoreLabel` in place of `player.getXp()` (display-only change; quiz score already came from the session). |
| `src/edu/batstateu/javajolt/ui/GameUI.java` | MODIFIED | Console `askQuestion` now takes `GameSession` and prints session score and `player XP + provisional XP` (mirrors the Swing display; provisional until settlement). |
| `src/edu/batstateu/javajolt/preview/BalanceReport.java` | NEW | Dev tool: prints levels 1–25 with XP threshold and max HP. |
| `src/edu/batstateu/javajolt/preview/MechanicsCheck.java` | NEW | Dev tool: level boundary checks, HP-band checks, settlement scenarios (pass-once/no-double-commit, replay, failure, abandonment, early defeat, high-score behavior), all in-memory. |
| `tests/ProgressConsistencyCheck.java` | MODIFIED | Legacy dev tool switched from `recordLessonCompletion` to `settleAttempt` in its `attempt` helper; the failed-attempt assertions now expect zero XP and a committed 80 score (new rules). Kept minimal, as pre-announced in the Phase 0 report. |
| `progress-documentations/AGENT_BRIEF.md` | UNTOUCHED this phase | v1.3 already the current version. |
| `PROJECT_RULES.md` | MODIFIED (docs) | §1 state line corrected (no longer "Session only: HP, score"); `LevelRewardService` added and `LessonRunnerService` description extended in "Where things live". |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED (docs) | New §2.4 implementation subsection (settlement + linear levels + max HP); §8 row 1 updated (stricter rule, no longer "decide later"); §8 row 3 tagged DECIDED (Phase 2) AND IMPLEMENTED. |
| `progress-documentations/change-reports/phase-2-level-curve-hp-scaling-single-session-start-settlement.md` | NEW | This report. |

## 3. Ownership and rule check

| Rule / concern | Owner |
| --- | --- |
| Level curve math (D1) | `LevelService` (unchanged signatures) |
| Max-HP derivation (D3) | `LevelRewardService` (static, pure) |
| HP/max-HP state, guarded setters, capped heal | `entity/Player` (guards its own state) |
| Quiz start (questions + max HP + restore) | `LessonRunnerService.startSession` (console and Swing both route through it) |
| Answer rules and provisional accumulation | `LessonRunnerService.submitAnswer` |
| End-of-quiz settlement (exactly once, commit rules) | `LessonRunnerService.settleAttempt` |
| Session state (score, provisional XP, settled flag) | `GameSession` |
| Flow sequencing (settle → recordHighScore → results) | `GameService.run` (console), `MainWindow.showNextQuestion` (Swing) |
| Display-only provisional values | `GameUI.askQuestion`, `QuestionScreen` (via `displayXp` parameter) |
| Dev verification, no saves | `preview/BalanceReport`, `preview/MechanicsCheck` |

PROJECT_RULES compliance: new logic went into the owning layers (`service`, `entity` guards, `game` session state, `preview/` tools); no libraries, no test frameworks, no `instanceof`/type switches on `Question` (dev tools subclass `Question` deterministically, the same pattern the legacy `tests/ProgressConsistencyCheck.java` already used); question content and kit assets untouched; `SaveService`/`SaveData` read-only and untouched; `out/`, `saves/` untouched; smallest-change edits (no renames beyond the two brief-required API changes; no reformatting).

Rule notes:
- `QuestionService` import was made public package use in MechanicsCheck via the existing `QuestionService(QuestionBank)` constructor — no new coupling beyond Phase 1's pattern.
- `MainMenuScreen` was *not* touched: the brief asked to verify, and its progress-bar math already produces sensible values under D1 (verified by inspection, §8/§11).

No clarifying questions were needed; nothing was bent.

## 4. Existing code touched

1. `service/LevelService.java` — formula rewrite only (D1); both public static signatures preserved. Justification: brief Phase 2 line 1.
2. `entity/Player.java` — max-HP additions + capped heal + resolved `TODO` removal. Justification: brief D3 and §7.9; default `maxHp = 10` matches the old fixed HP, so old saves behave identically until the first `startSession`.
3. `game/GameSession.java` — provisional XP + settled flag. Justification: brief requires intra-session tracking with no persistent mutation per answer.
4. `service/LessonRunnerService.java` — `startSession`, provisional `submitAnswer`, `settleAttempt` replacing `recordLessonCompletion`. Justification: brief Phase 2 lines 1 (startSession), 2 (settlement), 3 (exactly-once).
5. `game/GameService.java` — console flow re-sequenced through `startSession`/`settleAttempt`; removed now-unused `Question` import. Justification: brief "Replace the duplicated HP-reset and session-creation code in ... `GameService.run`"; the old inline `complete()` + bonus block had to go to keep settlement the single commit point.
6. `ui/swing/MainWindow.java` — `startLesson` and end-of-quiz edits as listed. Justification: brief (same replace instruction); settlement must wrap the Swing flow; `displayXp` came from the pre-approved per-answer provisional display requirement.
7. `ui/swing/screen/QuestionScreen.java` — `displayXp` parameter added to the two existing methods. Justification: brief's "display these normally during the quiz" for replays; no layout/behavior change.
8. `ui/GameUI.java` — console `askQuestion` session-based display. Justification: same display rule for the console twin of that screen.
9. `tests/ProgressConsistencyCheck.java` — `settleAttempt` + zero-XP failure assertions. Justification: the old assertions encoded the superseded "failed attempts earn XP" rule; brief v1.3 supersedes it and the Phase 0 report pre-announced this update.
10. `PROJECT_RULES.md`, `progress-documentations/gameplay-design-notes.md` — documentation duties (§10 below).

## 5. Player-visible behavior changes (before -> after)

- Level display: levels now rise linearly (e.g. 525 XP reads as L10 instead of L5 under the old quadratic curve; 1,050 XP as L20 instead of L7). Main-menu progress bar still fills sensibly (L1 band 0–99 toward L2, then 50 XP bands).
- Max HP: the quiz HP cap now grows with level — 10 HP at L1–2 up to 15 HP at L15+ — instead of always 10. Healing and restores cannot exceed the cap.
- Quiz start: HP always fully restores (to the new max) whether the previous attempt ended dead or alive; both console and Swing start identically through `startSession`.
- Replay XP display: replaying a completed pair now displays flat "+0 XP" accumulation during the quiz (previously it also displayed the flat zero-XP state, but computed from `player.getXp()` alone — display values for first clears are unchanged).
- Failed attempts: earn zero XP (before: per-answer XP committed even on failed first-clear attempts). Their attempt score does commit and can set the topic high score.
- Abandoned quizzes (window closed mid-quiz, or starting a different quiz): bank no score and no XP (before: every answer committed persistently as it arrived).
- Console/Blending: no visible change for normal first-clear passes other than the above; pass/ XP/score amount formulas are unchanged (2 XP per correct on 12-question quizzes, 50 XP completion bonus, 10 points per correct).

## 6. Save compatibility

- Expected: no change — confirmed. No save-format or `SaveService`/`SaveData` code touched. `maxHp` is deliberately not persisted (derived from XP at each `startSession`); old saves load unchanged through the untouched load path and get their max HP on the first quiz start.
- Real-save safety proof: SHA-256 of `saves/player.properties` was `973551BAD55C0D5C0BBE2BBC56911452765A38D283FF2AC3D6601BA1D60F3756` immediately before and after this phase's live `GameApplication` and `StagePreview` GUI smoke runs (byte-identical). (The absolute value differs from the Phase 1 report baseline because the owner played the real game in between — a normal save write by the owner, not this phase.)
- `preview/MechanicsCheck` and `preview/BalanceReport` construct their `Player`s in memory and never open `saves/`; `StagePreview`'s sandbox behavior was re-smoked (message printed, no real-save change).

## 7. Balance values introduced or changed

| Value | Before | After | File | Source |
| --- | --- | --- | --- | --- |
| Level curve | quadratic | linear: L1 < 100 XP; L(n≥2) starts at 50·n XP (L2=100, L5=250, L10=500, L15=750) | `service/LevelService` (D1) |
| Max HP | fixed 10 | `10 + min(level/3, 5)`: 10 at L1–2, 11 at L3–5, 12 at L6–8, 13 at L9–11, 14 at L12–14, ≥15 from L15 | `service/LevelRewardService` (D3) |
| XP per correct (12-question quiz) | 2 | 2 (unchanged; formula preserved) | `LessonRunnerService` |
| Completion bonus | 50 | 50 (unchanged) | `LessonRunnerService` |
| Score per correct / damage per mistake | 10 / 2 | unchanged (damage revisit deferred to Phase 3 per TODO) | `LessonRunnerService` |

`Player.getStartingHp()` status per brief instruction: no longer called by any `src/` code after `startSession` took over HP reset. It is still referenced by the legacy dev tool `tests/ProgressConsistencyCheck.java` (line 165), so it was kept rather than removed.

## 8. Verification

All commands run from the project root; compile targets are temp directories, never `out/`:

1. Full compile (src + tests) with `javac -d %TEMP%\opencode\jj-build-phase2 ...` → **exit code 0**.
2. `preview/BalanceReport` — prints the levels 1–25 table; thresholds exactly 0/100/150/.../1250; max HP column cycles 10,10,11,11,11,12,...,15 and caps at 15. Every "XP-to-level" cell matches its own threshold (round-trip).
3. `preview/MechanicsCheck` — 43 checks, **43 passed / 0 failed**:
   - level boundaries: 0→L1, 99→L1, 100→L2, 249→L4, 250→L5, 750→L15 (+ threshold round-trips at L2/L10/L15);
   - max HP: L1=10, L2=10, L3=11, L5=11, L6=12, L9=13, L12=14, L15=15, L40=15; heal clamps and restore lands exactly on max HP;
   - per-correct XP: `xpPerCorrect(12) == 2`;
   - pass-once: settle true, second settle no-op, XP = 12·2 + 50 = 74, score = 120, pair complete, session settled;
   - replay: no provisional XP, settle false, XP unchanged, attempt score still commits;
   - failed first-clear: zero XP, 80 score commits, pair not complete;
   - abandonment: no score banked, no XP banked, pair incomplete, session never settled, provisional XP stays in the dead session;
   - early HP defeat: player dead, nothing banked without settlement;
   - high score: first accepted, lower rejected, better accepted.
4. `tests/ProgressConsistencyCheck` — all 5 checks pass under the new rules (failed attempt = zero XP + 80 committed score; first pass XP = 9·2 + 50; save/load round-trip; no duplicate replay XP; high-score preservation).
5. GUI smoke runs: `GameApplication` (10 s, killed) and `StagePreview advanced` (8 s, killed) — stdout/stderr clean, no exceptions; `StagePreview` printed its sandbox banner.
6. Real save untouched: hash identical before/after all runs of this phase (§6).
7. Acceptance checks from the brief: no hard-coded HP reset in flow code anymore (the only HP constants left are the Player defaults and the Phase-3-deferred `DAMAGE_PER_MISTAKE`); both Swing and console start through `startSession`; `MainMenuScreen` progress values verified sensible for L1/L2/L3 by inspection (L1: 0–99/100; L2: 0–49/50; L3: 0–49/50); no interruption can farm XP and unfinished attempts bank nothing (MechanicsCheck abandonment/death scenarios + no-reward rule).

## 9. Manual test steps for the owner

1. Normal game: start `GameApplication`. Main menu should show your real XP and level under the new curve (e.g. 525 XP → Level 10, progress bar relative to the 50 XP bands).
2. Play any first-clear topic: each correct answer raises the displayed XP by 2 (12-question quizzes); after the results screen it stays at XP + 24 + 50.
3. Verify the HP cap scales: at a high-XP profile, the question screen's HP label starts at the level-derived max (up to 15 at L15+). `StagePreview all --xp=750` starts Level 15 with 15 max HP.
4. Replay a completed topic: the XP counter must not move during the quiz, the quiz score still ticks up, and after the results screen only your cumulative score changes (+10 per correct). No XP gains are committed.
5. Deliberately fail a first-clear quiz (≤ 8/12): zero XP is earned; your cumulative score still increases by the attempt's points; results screen still offers Try Again.
6. Abandonment: mid-quiz, close the window and confirm "Save and exit" — your XP and score must not reflect the abandoned attempt's answers (nothing was banked). Save file loads again normally afterwards.
7. HP defeat: die mid-first-clear-quiz — the attempt settles once (score commits, XP stays 0 because the attempt failed) and the results screen shows the failure.
8. Run `java -cp <build> edu.batstateu.javajolt.preview.MechanicsCheck` and `...preview.BalanceReport` yourself — expect ALL CHECKS PASSED and the level table.
9. After every step, ensure `saves/player.properties` hash/timestamp only changes through your own gameplay saves.

## 10. Docs updated

- `progress-documentations/gameplay-design-notes.md`: new §2.4 "Quiz settlement, linear levels, and level-based max HP" (placed after §2.3), documenting the implemented commit rules and curve; §8 row 1 updated to the stricter zero-XP failure rule (removing the now-false "decide later whether stricter" clause); §8 row 3 tagged DECIDED (Phase 2) AND IMPLEMENTED with the formula. Sections 2 table, 3, 4–7, 9 prose untouched (empty §8 row 2 "Power-up limit" remains an open decision for Phase 5).
- `PROJECT_RULES.md`: §1 "State:" line corrected (persisted: name, XP, difficulty, completed topics, cumulative score, high scores; quiz-attempt HP and provisional score/XP commit once at settlement); "Where things live": added `LevelRewardService`, extended `LessonRunnerService` description (settlement).
- `progress-documentations/progress.md`: not touched (forbidden).
- `progress-documentations/change-reports/phase-2-level-curve-hp-scaling-single-session-start-settlement.md`: this report.

## 11. Deviations from this brief, assumptions made, and risks

1. **Display strategy (pre-announced in the plan):** mid-quiz provisional XP display required two small display-only signature changes (`QuestionScreen.showQuestion`/`showAnswerResult` gained `int displayXp`; console `GameUI.askQuestion` now takes `GameSession` instead of `Player`). The brief requires provisional displays but does not name the mechanism; this was the smallest mechanism (the display for quiz score already flowed from the session the same way). Console `HP`/score/XP line now mirrors the Swing question screen.
2. **`settleAttempt` return value:** the brief replaces `recordLessonCompletion` without specifying a return type. Returning `boolean` (pair newly completed) lets `GameService` and future phases message/act on the outcome without re-checking progress; both callers currently ignore or use it minimally (console ignores it; it exists for Phase 4/6 wiring).
3. **HP-defeat settlement path:** the Swing flow settles whenever the quiz ends (all questions answered **or** the player died), per the brief ("including a genuine HP defeat"). Verified via MainWindow sequencing; the accuracy denominator behavior is unchanged until Phase 3 (per notes §2.2, and Phase 3 changes it).
4. **`getStartingHp()` kept:** unused in `src/` but still used by `tests/ProgressConsistencyCheck.java`; removing it would require either rewriting the legacy tool beyond the brief or leaving a broken tool. Reported per brief instruction, removed in a later phase if desired.
5. **`MainWindow.questionService` field retains its constructor wiring; it is no longer needed for question selection (startSession owns that now).** The field was kept to avoid constructor signature churn in this phase (also it feeds Phase 6 wiring when per-question services may be needed). Listed as noticed-not-changed below rather than deleted, per "no renames/refactors beyond the brief."
6. **`GameUI` shows quiz-local score** (session score) instead of the cumulative score in its question header. Rationale: the Swing equivalent already shows quiz score ("QUIZ SCORE"), the session is the actual accruing value under settlement, and the results screen still shows the session score. Behavior difference vs. the old console display (cumulative score) is intentional under the new model; the cumulative score remains visible via save/menu screens.
7. **Balance-report `main` prints XP-to-level verification column** (threshold → `calculateLevel` round trip). Pure addon to make the table self-verifying; no gameplay impact.
8. **Risks:** (a) preview `MechanicsCheck` constructs `LessonRunnerService(questionService, null, null)` — the nulls are only reachable through `study`/`playLesson`, which the tool never calls; (b) `MainWindow`'s 4-arg constructor (real-save default) remains unused; (c) the quiz HP label shows the session HP, which is only correct once a quiz has begun through `startSession` (the only path that exists); (d) the settlement rule "score commits on the normal results path" means a quiz that ends by HP death *after* answering ≥ 1 question commits score — intended per the brief.

## 12. Noticed but intentionally not changed

- `MainWindow.questionService` is now unused in the start path (field kept; see §11.5).
- `Player.getStartingHp()` is unused in `src/` (kept for the legacy tests tool; §7).
- `LessonService`'s fourth constructor argument wiring in `MechanicsCheck` uses nulls (dev-only; §11.8).
- `MainWindow`'s 4-arg (real-save default) constructor is caller-less (Phase 1 note; still pending an owner decision).
- The two remaining `TODO(gameplay balance)` comments in `LessonRunnerService` (damage, HP/accuracy interplay) are Phase 3 scope and stay.
- `DifficultyProgressionService` difficulty-flip timing: settlement completing a pair still triggers the same progression drift in the Swing menu as before Phase 2 (Phase 1 report §11.3); `StagePreview`'s "first six topics" seeding already accounts for it.
- No achievements/difficulty-message text changes: result texts still derive from `hasPassed` + progress, now after settlement; cosmetic polish belongs to Phase 6.

**Phase 2 is complete. Stopping here — Phase 3 starts only when the owner says "Proceed to Phase 3".**
