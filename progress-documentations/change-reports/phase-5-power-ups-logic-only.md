# Phase 5 Report: Power-ups (logic only)

## 1. Summary

The fixed four-power-up pool now exists as pure logic. New `enums/PowerUp` (HINT, SHIELD, TIME_STOP, POINT_BOOST) carries the display labels and unlock levels (5/10/15/20 — the only place they live). `GameSession` gained `usedPowerUps` (free EnumSet, exposed only as a detached copy), `shieldArmed`, ``pointBoostArmed``, and intention-revealing methods (`markUsed`, `hasUsed`, `armShield`, `consumeShield`, `armPointBoost`, `consumePointBoost`); everything resets naturally per quiz. `Question.getHintChoicesToRemove()` defaults to `List.of()` and `MultipleChoiceQuestion` overrides it to hand back exactly one randomly chosen **wrong** choice (never the correct one; empty at two or fewer choices) — no `instanceof` anywhere, pure polymorphism. The new `entity/PowerUpResult` record `(applied, message, removedChoices)` is returned by the stateless injected `PowerUpService` with `isUnlocked`, `canUse`, `use`, and `unlockedBetween`; a use marks the session only when it actually applies, so a Hint on a non-multiple-choice question and Time Stop (until Phase 7) are rejected **without being consumed**. `submitAnswer` matches the brief's sketch exactly: boost consumed and applied only on the next correct answer (24 at streak 3, not 28 or 40); damage resolves first, then an armed shield zeroes it — but `recordMistake(damage)` still counts the mistake, still escalates the chain, still clears the correct streak, and records the 0 as `lastDamage`. `PowerUpService` is wired through both composition roots and `MainWindow` (no `new` inside MainWindow). Per the owner's note from the Phase 4 report, the results screen now shows `BEST STREAK` alongside `PERSONAL BEST` as a third card in the existing stat row — same `Theme.GAP_M` gap and card style as before, so no spacing breaks. Verified: **287/287** `MechanicsCheck` checks (28 new), `ProgressConsistencyCheck` 5/5, both entry points smoke-run clean, real save byte-identical across the run.

## 2. Files

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `src/edu/batstateu/javajolt/enums/PowerUp.java` | NEW | `HINT("Hint", 5)`, `SHIELD("Shield", 10)`, `TIME_STOP("Time Stop", 15)`, `POINT_BOOST("Point Boost", 20)` with `getLabel()` and `getUnlockLevel()`. Unlock levels live only here, per the brief. |
| `src/edu/batstateu/javajolt/entity/PowerUpResult.java` | NEW | Record `(boolean applied, String message, List<String> removedChoices)` exactly per the brief; compact constructor never lets `removedChoices` be null and always detaches it. |
| `src/edu/batstateu/javajolt/service/PowerUpService.java` | NEW | Stateless, injected. `isUnlocked(player, powerUp)` = `LevelService.calculateLevel(xp) >= unlockLevel`; `canUse(session, powerUp)` = unlocked AND not used this quiz; `use(session, powerUp, current)` arms SHIELD/POINT_BOOST (marked used) or applies HINT through the question's polymorphic `getHintChoicesToRemove()`; hints with nothing to remove, Time Stop, and locked/already-used requests return `applied=false` and are not consumed; `unlockedBetween(oldLevel, newLevel)` returns the pool entries in unlock order for level-up announcements. |
| `src/edu/batstateu/javajolt/Question.java` | MODIFIED | Default `getHintChoicesToRemove()` returns `List.of()` per the brief. |
| `src/edu/batstateu/javajolt/types/MultipleChoiceQuestion.java` | MODIFIED | Override reads the current (already shuffled) labeled choices and answer, never offers the correct choice (case-insensitive letter-prefix match against `answer`, mirroring `prepareForLesson`'s detection), returns empty when two or fewer choices exist, otherwise one random wrong choice. |
| `src/edu/batstateu/javajolt/game/GameSession.java` | MODIFIED | `usedPowerUps` (EnumSet, `getUsedPowerUps()` returns a copy; empty set safe — `EnumSet.copyOf` quirk guarded), `shieldArmed`, `pointBoostArmed` fields; `markUsed`/`hasUsed`/`armShield`/`consumeShield` (returns whether armed, clears either way)/`armPointBoost`/`consumePointBoost` (same semantics); `recordMistake()` became `recordMistake(int damage)` which records the actual damage (0 for a shielded mistake) instead of the separate `setLastDamage` bookkeeping — the now-redundant setter was removed; comment updated. |
| `src/edu/batstateu/javajolt/service/LessonRunnerService.java` | MODIFIED | `submitAnswer` follows the brief's Phase 5 sketch exactly: on a correct answer `boolean boosted = session.consumePointBoost()` then `pointsForCorrect(boosted, currentStreak + 1)` → `recordCorrect(points)`; on a mistake `resolveMistake` (pre-answer counter) → `if (session.consumeShield()) damage = 0` → `takeDamage(damage)` → `recordMistake(damage)`. XP and signature unchanged. |
| `src/edu/batstateu/javajolt/ui/swing/MainWindow.java` | MODIFIED | Constructor now takes `PowerUpService` (4- and 6-arg pair, 4-arg delegates as before); stored as a final field, unused until Phase 6 wires the buttons. |
| `src/edu/batstateu/javajolt/ui/swing/screen/ResultsScreen.java` | MODIFIED | Owner-directed (Phase 4 follow-up): `BEST STREAK` card (`session.getBestStreak()`) added as the third card of the existing `scoreStats` row; grid widened `GridLayout(1, 2, Theme.GAP_M, 0)` → `(1, 3, Theme.GAP_M, 0)` — identical gap constants to the matching 3-card row above it, all spacing elsewhere untouched; section comment updated. |
| `src/edu/batstateu/javajolt/GameApplication.java` | MODIFIED | Composition root constructs one `PowerUpService` and passes it to `MainWindow`. |
| `src/edu/batstateu/javajolt/preview/StagePreview.java` | MODIFIED | Same wiring for the dev launcher (shared static, one `PowerUpService` per run). |
| `src/edu/batstateu/javajolt/preview/MechanicsCheck.java` | MODIFIED | New `checkPowerUps()`: unlock levels at exact probe XP (200 → L4 none, 250 → L5 Hint only, 500 → L10 Hint+Shield, 1000 → L20 all four) plus `unlockedBetween` interval checks; one use per unlocked power-up per session (second use rejected); 200 Hint runs on a shuffled 4-choice question — always exactly one removed choice, never the correct one; Hint on an `IdentificationQuestion` not applied and not consumed; Time Stop unused/not consumed; Shield scenario (player at 13 HP, Beginner): 2 correct for a streak, shielded mistake deals 0 damage but `consecutiveMistakes` = 1 and streak = 0, next unshielded mistake costs escalated `3 + 1*2 = 5`; Point Boost scenario: arms, survives a mistake, the next correct pays 20 (doubled base), the following correct pays 12 again, and no XP is involved (`Player.xp` untouched, provisional XP only). Class header updated to Phase 2–5. |
| `PROJECT_RULES.md` | MODIFIED (docs) | "Where things live": `PowerUpService` bullet under `service/`; `PowerUpResult` added to `entity/`; `PowerUp` added to `enums/` with the note that unlock levels live only there. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED (docs) | §8 row 2 (power-up limit) → DECIDED (Phase 5): one use of each unlocked power-up per quiz; §9 Stage 4 → STARTED (Phase 5). |
| `progress-documentations/change-reports/phase-5-power-ups-logic-only.md` | NEW | This report. |

## 3. Ownership and rule check

| Rule / concern | Owner |
| --- | --- |
| Pool definition + unlock levels | `enums/PowerUp` (single source, per brief) |
| Session usage/armed state | `GameSession` (guards its own state; `usedPowerUps` readable only as a copy) |
| Unlock/use/limit rules | `PowerUpService` (stateless, injected; no `new` inside UI) |
| Hint choice selection | `MultipleChoiceQuestion` override (polymorphism; no `instanceof`/type switch on `Question`) |
| Answer-flow consumption order | `LessonRunnerService.submitAnswer` (brief's exact sketch) |
| Composition | `GameApplication` + `StagePreview` (and the 4-arg `MainWindow` delegates unchanged) |
| Dev verification, no saves | `preview/MechanicsCheck` |

PROJECT_RULES compliance: new classes in the right packages (`enums/`, `entity/`, `service/`); DI respected everywhere (`PowerUpService` constructed at composition roots only); no new libraries/test frameworks; no `instanceof` on `Question`; `SaveService`/`SaveData`, kit assets, `out/`, `saves/` untouched; unlock levels appear only in the enum (single-place rule). The Enemy Design Boundary holds: no enemy behavior or identity was touched — Shield/Boost reshape the player's answer flow, and `resolveMistake` itself is unchanged. No rule bent; no clarifying questions needed.

## 4. Existing code touched

1. `game/GameSession.java` — power-up state/behavior + `recordMistake(int)` per brief lines 2 and the sketch.
2. `service/LessonRunnerService.java` — answer flow per the brief's exact sketch (§4).
3. `Question` + `types/MultipleChoiceQuestion` — hint hooks per brief line 3.
4. `ui/swing/MainWindow` — constructor wiring only (brief §5: "Wire `PowerUpService` through the constructors that need it"; §6: no `new` in MainWindow).
5. `GameApplication` + `StagePreview` — composition root updates (brief §6 rule).
6. `preview/MechanicsCheck` — brief-mandated checks (§ line 8).
7. `ui/swing/screen/ResultsScreen` — owner-directed display addition carried over from the Phase 4 report review.
8. `PROJECT_RULES.md`, notes — documentation duties (§10).

## 5. Player-visible behavior changes (before -> after)

- Nothing is player-visible yet except the results screen's new `BEST STREAK` card: Phase 5 deliberately ships logic only, with no UI buttons (Phase 6 wires them), so quizzes behave byte-for-byte as before when no power-up is used.
- The results screen now also displays the attempt's best streak next to QUIZ SCORE / PERSONAL BEST (owner-directed from Phase 4 review).
- Internally, once Phase 6 lands: Hint will remove one wrong choice on multiple-choice questions, Shield will block one mistake's damage, Point Boost will double the next correct answer's base points — each once per quiz, unlocked at levels 5/10/20.
- `lastDamage` is now 0 (not the blocked amount) after a shielded mistake — reading zero is correct "after Shield" semantics agreed in Phase 3.

## 6. Save compatibility

- Expected: no change — confirmed. No save-format or `SaveService`/`SaveData` code touched; power-up usage is session-only state that never persists.
- Real-save safety proof: SHA-256 of `saves/player.properties` was `E66B8165160FEA4B46FF65E928CCB59A046A9331E60B6DDF1CD91667E5F53F41` immediately before and after this phase's live `GameApplication` and `StagePreview` GUI smoke runs (byte-identical). The absolute value differs from the Phase 4 report baseline because the owner played the real game in between (owner's own save write, not this phase).
- All dev tools are in-memory only; nothing this phase ran opened a save path.

## 7. Balance values introduced or changed

| Value | Before | After | Source |
| --- | --- | --- | --- |
| Unlock levels | none landed | Hint 5, Shield 10, Time Stop 15, Point Boost 20 | brief Phase 5 |
| Per-quiz limit | open question (notes §8 row 2) | one use of **each** unlocked power-up per quiz | brief Phase 5 (`canUse` semantics) |
| Boost application | wired off (`boosted=false`) | consumed on the next correct answer; survives mistakes; doubles base only | brief Phase 5 sketch |
| Shield application | none | zeroes the next mistake's damage; mistake still counts and escalates | brief Phase 5 sketch |

Base damage, escalation, streak scoring, XP, pass mark — unchanged.

## 8. Verification

Compile to `%TEMP%\opencode\jj-build-phase5` (never `out/`), run from the project root; re-verified after line-ending normalization.

1. Full compile (src + tests): **exit code 0** (twice).
2. `MechanicsCheck`: **287/287 PASS** (259 prior + 28 new): unlock levels L4/L5/L10/L20 exact; `unlockedBetween` intervals exact (empty above 20); each of Shield/Point Boost/Hint applies once and rejects the second use; **200 Hint runs** — always exactly one removed choice and never the correct one; Hint on an identification question not applied, not consumed; Time Stop not applied, not consumed; Shield scenario — 0 damage, `consecutiveMistakes` 1, correct streak reset, next unshielded mistake costs the escalated 3 + 1*2 = 5; Point Boost scenario — survives a mistake, next correct pays 20, subsequent correct pays 12, `Player.xp` never moves. All Phase 2–4 regression blocks still pass.
3. `tests/ProgressConsistencyCheck`: 5/5 pass (the answer-flow change didn't disturb failure/completion/save semantics).
4. `BalanceReport`: unchanged, runs clean (no regression).
5. GUI smoke: `GameApplication` and `StagePreview intermediate` launched with clean stdout/stderr and no exceptions; real-save hash byte-identical across the run (§6).
6. Acceptance per the brief: no power-up awards XP (verified explicitly in the boost scenario), none bypasses a question (nothing removes a question from the session), and Hint never reveals the correct answer (200-run exhaustive-style probe + exact-one-wrong selection logic). **STOP criteria met.**

## 9. Manual test steps for the owner

1. Run `java -cp <build> edu.batstateu.javajolt.preview.MechanicsCheck` and confirm ALL CHECKS PASSED (287 checks).
2. Finish any quiz and look at the results screen: the score row should read QUIZ SCORE / PERSONAL BEST / BEST STREAK, evenly spaced with the row above it — confirm nothing feels cramped or shifted.
3. Confirm `saves/player.properties` only changes through your own confirmed saves.
4. Optionally `StagePreview all --xp=1000` (Level 20) after Phase 6: power-up buttons will be wired there; for now nothing consumes the service.
5. `--console` behavior is unchanged — no power-up commands exist yet (Phase 6/7 wire UI).

## 10. Docs updated

- `progress-documentations/gameplay-design-notes.md`: §8 row 2 → DECIDED (Phase 5) — one use of each unlocked power-up per quiz; §9 Stage 4 → STARTED (Phase 5) with the pending UI (Phase 6) and Time Stop effect (Phase 7) noted. All other prose untouched.
- `PROJECT_RULES.md`: `PowerUpService` (service), `PowerUpResult` (entity), `PowerUp` (enum + "unlock levels live only here").
- `progress-documentations/progress.md`: not touched (forbidden).
- This report saved under `progress-documentations/change-reports/`.

## 11. Deviations from this brief, assumptions made, and risks

1. **Results-screen `BEST STREAK` (owner-directed, not in the brief):** the owner asked, when approving Phase 4, that bestStreak also be displayed wherever the high score is displayed. The per-topic high score is displayed on `ResultsScreen` (PERSONAL BEST), so the card was added there — the only UI change this phase, kept to one card in an existing row with identical spacing constants. Documented here as an owner-approved addition to the "logic only" phase.
2. **`recordMistake(int damage)` signature:** the brief's sketch passes the (possibly zeroed) damage into `recordMistake`, which subsumed the Phase 3 `setLastDamage`/`hpBefore` bookkeeping; the setter was removed as now-dead code (single caller, updated).
3. **`unlockedBetween` interval semantics:** implemented as `(oldLevel, newLevel]` per the brief's phrasing "unlocked between".
4. **Mid-level-save caveat (carried):** cumulative high-score/score math is untouched, so nothing here affects Phase 2's mid-quiz-save consistency notes.
5. **Risks:** (a) `MainWindow`'s `powerUpService` field is currently unused — deliberate, Phase 6 wires the buttons; (b) Hint's letter-prefix matching relies on the `prepareForLesson` labeling convention (`"A. text"`); exact-format deviations in a future question bank would surface loudly in the 200-run check; (c) `consumeShield`/`consumePointBoost` clear-on-read semantics are intentional (single consumer = `submitAnswer`), so future call sites must not probe them speculatively.

## 12. Noticed but intentionally not changed

- No power-up buttons, badges, or HUD in either UI — Phase 6 wires the Swing screen and Phase 7 owns the timer that TIME_STOP waits for.
- Console mode has no power-up commands yet (a Phase 6/7 question, driven by the brief's UI phases).
- `GameService`/console path untouched; enemy behavior untouched (Enemy Design Boundary respected).
- Recognition that power-up armed state could be displayed when armed (e.g. a `SHIELD ARMED` hint near the streak label) belongs with Phase 6's UI wiring, not this one.
- `MainWindow`'s unused-for-selection `questionService` and caller-less 4-arg constructor unchanged (Phase 2 notes; the 4-arg signature grew a parameter per the DI rule).

**Phase 5 is complete per brief v1.3. Stopping here — Phase 6 starts only when the owner says "Proceed to Phase 6".**
