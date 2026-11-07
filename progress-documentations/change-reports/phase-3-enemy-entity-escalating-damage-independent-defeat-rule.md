# Phase 3 Report: Enemy entity, escalating damage, independent defeat rule

## 1. Summary

Every topic/difficulty quiz now has an opponent. The new concrete `game/Enemy` computes each mistake's damage as base `(maxHp - 1) / 4` plus `consecutiveMistakes * escalationPerMistake` from the pre-answer counter (`data/EnemyRoster` supplies Piglet Byte / Boar Byte / Warthog Byte for Variables and `<Topic label> Bug` placeholders elsewhere, escalating 2/3/4 by difficulty), so three consecutive mistakes defeat every max HP band (10–15) while four isolated never do. Counters moved into `GameSession` (reset by correct answers, incremented after damage), `accuracy` is now computed over actually answered questions (`correct * 100 / answered`), and `getLastDamage()` records the applied damage for the Phase 5 Shield. The fixed 2 damage constant and both damage `TODO`s are removed. `BalanceReport` gained the survivability tables, exhaustive 4,096-sequence classification (A/B/C/D) per band × difficulty, and probability-weighted shares for uniform 60/70/80/90% correctness plus a documented tiered model; `MechanicsCheck` verifies per band × difficulty lethality, survivability, both defeat categories, the scripted 12-answer cases, and counter semantics — 245/245 checks pass.

## 2. Files

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `src/edu/batstateu/javajolt/game/Enemy.java` | NEW | Concrete, non-final opponent: `name`, `topic`, `difficulty`, `escalationPerMistake` + getters, the overridable `resolveMistake(GameSession)` hook per the brief's sketch, and the single static/pure `baseDamageForMaxHp(int) = (maxHp-1)/4` helper (public for the Phase 7 timer). |
| `src/edu/batstateu/javajolt/data/EnemyRoster.java` | NEW | `Enemy getEnemy(Topic, Difficulty)`. Escalation constants at the top (2/3/4 per difficulty). Variables: Piglet Byte / Boar Byte / Warthog Byte; every other topic: `<Topic label> Bug` with the same increments. |
| `src/edu/batstateu/javajolt/game/GameSession.java` | MODIFIED | Holds `Enemy` (supplied by `startSession`); `consecutiveMistakes` initially 0 (+`getConsecutiveMistakes()`); `recordCorrect` resets it to 0, `recordMistake` increments once (after damage — sequencing owned by the caller); `getAnsweredCount()`; `getAccuracyPercent()` = `correct*100/answered` (0 when nothing answered); `lastDamage` + `get/setLastDamage()`. |
| `src/edu/batstateu/javajolt/service/LessonRunnerService.java` | MODIFIED | Injects `EnemyRoster` (new constructor parameter); `startSession` sets the session's enemy; `DAMAGE_PER_MISTAKE` removed; on a wrong answer: resolve `session.getEnemy().resolveMistake(session)` before recording, apply damage, record lastDamage as actual HP lost, then `recordMistake()` (increments the counter). `hasPassed` unchanged (alive AND ≥70%). Both damage `TODO` comments removed (resolved by this phase). |
| `src/edu/batstateu/javajolt/GameApplication.java` | MODIFIED | Builds `EnemyRoster` in the composition root and passes it to `LessonRunnerService`. |
| `src/edu/batstateu/javajolt/preview/StagePreview.java` | MODIFIED | Same constructor wiring for the dev launcher. |
| `src/edu/batstateu/javajolt/preview/BalanceReport.java` | MODIFIED | Section 2: survivability table (per HP band 10–15 × difficulty: base, escalation, chain damage at mistakes 1/2/3, chain length to defeat, total damage from four isolated mistakes). Section 3: exhaustive classification of all 4,096 twelve-answer masks per band × difficulty into A (HP defeat, accuracy over answered ≥70), B (HP defeat, <70), C (alive accuracy-fail), D (pass) — deduplicated per realized below-death prefix so early defeats are not triple/multi-counted. Section 4: probability-weighted pass and HP-defeat shares for uniform 60/70/80/90% correctness, plus a clearly labeled second model (−10pp application / −20pp challenge tiers, documented as an assumption). Explicit note: counts are combinatorial, not outcome probabilities. |
| `src/edu/batstateu/javajolt/preview/MechanicsCheck.java` | MODIFIED | Per band 10–15 × difficulty (via the real `Enemy`/roster and real machinery): three consecutive mistakes lethal; four isolated mistakes survivable; 8-correct-then-3-consecutive-wrong ends dead with answered accuracy 8/11 = 72 (fails because dead); four isolated wrong across 12 stays alive at 8/12 = 66 (fails by accuracy); full 12-correct run passes. New counter checks: one mistake ⇒ counter 1 and `lastDamage` = base; correct answer resets to 0; the next mistake again costs base (no residual escalation). Exhaustive-range checks: categories A, B, C, D all occur for every band × difficulty. Phase 2 settlement checks kept intact (with scenario wording updated to the new damage model: the accuracy failures now use isolated mistakes since clustered ones defeat the player). |
| `tests/ProgressConsistencyCheck.java` | MODIFIED | Its `attempt` helper now attaches the roster's real enemy to its hand-built session (its old fixed-2-damage assumption no longer holds) and spreads the wrong answers between correct ones — under escalation, clustered mistakes defeat the player, which would turn its accuracy scenarios into HP-defeat scenarios. Test intents unchanged: 8/12 fails, 9/12 passes, replay/high-score rules hold. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED (docs) | New §2.5 implementation subsection; §8 row 6 tagged DECIDED (Phase 3) AND IMPLEMENTED; §8 row 8 tagged DECIDED (Phase 3); §9 Stage 3 tagged STARTED (Phase 3). |
| `PROJECT_RULES.md` | MODIFIED (docs) | "Where things live": `Enemy` added to `game/`, `EnemyRoster` to `data/`, `LessonRunnerService` description says "enemy damage". |
| `progress-documentations/change-reports/phase-3-enemy-entity-escalating-damage-independent-defeat-rule.md` | NEW | This report. |

## 3. Ownership and rule check

| Rule / concern | Owner |
| --- | --- |
| Enemy identity, damage math, overridable hook | `game/Enemy` (single static base helper; hook has no side effects) |
| Roster mapping + escalation constants | `data/EnemyRoster` (all escalation values at the top) |
| Mistake counter, answered count, accuracy, lastDamage | `GameSession` (state holder; entry points unchanged) |
| Damage sequencing (resolve → apply → record) | `LessonRunnerService.submitAnswer` |
| Enemy supply at quiz start | `startSession` (single start-of-quiz home from Phase 2) |
| Composition | `GameApplication` (and dev launchers/tests wire their own) |
| Dev verification, no saves | `preview/BalanceReport`, `preview/MechanicsCheck` |

PROJECT_RULES compliance: new classes in the right packages (`game/` entity-like gameplay object, `data/` mapping table); no new libraries or test frameworks; no `instanceof`/type switches on `Question` (unchanged placeholder subclassing in dev tools); `SaveService`/`SaveData`, kit assets, `out/`, `saves/` untouched; smallest-change edits (constructor injection only, no renames, no reformatting). The two `TODO(gameplay balance)` comments in `LessonRunnerService` were removed because both are resolved exactly as this phase prescribed (damage escalation now creates HP-primary defeats). No rule bent; no clarifying questions needed.

## 4. Existing code touched

1. `game/GameSession.java` — counter/accuracy/lastDamage/enemy state. Justification: brief Phase 3 lines 3 (GameSession instructions).
2. `service/LessonRunnerService.java` — roster injection, enemy damage sequencing, constant + TODO removal. Justification: brief Phase 3 (inject `EnemyRoster`; remove `DAMAGE_PER_MISTAKE`; brief's resolve-before-record order; §7.9 TODO removal).
3. `preview/BalanceReport.java`, `preview/MechanicsCheck.java` — extended per brief Phase 3 table/exhaustive-enumeration instructions.
4. `tests/ProgressConsistencyCheck.java` — helper wiring + mistake spread. Justification: its session needed an enemy after `submitAnswer` gained the enemy hook, and its damage assumptions became false under escalation (same minimal-update rule as Phase 2).
5. `GameApplication` + `StagePreview` — constructor call updates (one line each). Justification: brief §6 "update every caller of a changed constructor".
6. `PROJECT_RULES.md`, notes — documentation duties (§10).

## 5. Player-visible behavior changes (before -> after)

- Wrong answers now hurt more and **escalate**: the 1st mistake in an uncorrected chain costs 2 HP (Beginner, 10-max-HP player), the 2nd costs 4, the 3rd costs 6 — any three consecutive mistakes defeat the player, where the old model needed ten. Four isolated mistakes cost only 4×base (8 or 12) and stay survivable on every band.
- Dangerous runs end early: a defeat at question 11 ends the quiz there; the results screen shows accuracy over the 11 answered questions (e.g. 72%) but the attempt still fails because the player died — accuracy can no longer "fall below 70% only because of unanswered questions after an early defeat" being the lens (the old denominator counted the whole quiz).
- Failed-but-survivable runs read their real accuracy over answered questions (12 answered when alive at the end — unchanged numbers for completed quizzes).
- No UI/screens were changed; enemy identity is not displayed yet (Phase 6 wires visuals).

## 6. Save compatibility

- Expected: no change — confirmed. No save-format or `SaveService`/`SaveData` code touched; the enemy, counters, and `lastDamage` are session-only (not persisted). Old saves load identically and get their per-quiz state from `startSession`.
- Real-save safety proof: SHA-256 of `saves/player.properties` was `3CF4DF24589D2F5950DE5745A223BBE72113EE50C263767FE466A98E036AC469` immediately before and after this phase's live `GameApplication` and `StagePreview` GUI smoke runs (byte-identical). The absolute value differs from the Phase 2 report baseline because the owner played the real game in between (owner's own save write, not this phase).
- All dev tools are in-memory only; `MechanicsCheck`'s hypothetical `Session` uses no UI and no save path.

## 7. Balance values introduced or changed

| Value | Before | After | File | Source |
| --- | --- | --- | --- | --- |
| Damage per mistake | fixed 2 | `baseDamageForMaxHp(maxHp) + consecutiveMistakes * escalation`, base = `(maxHp-1)/4` (2 for bands 10–12, 3 for 13–15), escalation 2/3/4 by difficulty | `Enemy.resolveMistake`, `EnemyRoster` constants | brief Phase 3 |
| Accuracy denominator | total quiz questions (12) | answered questions (`correct + mistakes`; 0 when none answered) | `GameSession.getAccuracyPercent` | brief Phase 3 |
| Lethality profile | 5 mistakes (band 10) | 3 consecutive mistakes defeat every band; 4 isolated never | derived (BalanceReport §2) | brief Phase 3 acceptance |

XP per correct (2), completion bonus (50), score per correct (10), pass mark (70%) — unchanged from Phases 1–2.

## 8. Verification

Compile to `%TEMP%\opencode\jj-build-phase3` (never `out/`), run from the project root. All three dev tools + tests exit 0.

1. Full compile (src + tests): **exit code 0**.
2. `MechanicsCheck`: **245/245 PASS**:
   - Phase 2 regression blocks (level curve, HP bands, per-correct XP, settlement exactly-once/replay/failure×zero-XP/abandonment/death/high-score) all pass — now run against the enemy machinery;
   - enemy counter semantics: counter = 1 after one mistake, `lastDamage` = base; correct resets to 0; next mistake costs base again;
   - every band 10–15 × difficulty: 3 consecutive mistakes lethal (escalated every hit), 4 isolated survivable, 8-correct + 3-consecutive-wrong = dead with answered accuracy **72** (≥70 but fails as HP defeat), 4-isolated-wrong across 12 = alive with answered accuracy **66** (fails by accuracy), 12-correct passes at 100;
   - exhaustive range: categories A, B, C, D **each occur** for every one of the 18 band/difficulty pairs (HP-primary and accuracy-primary failures both demonstrable everywhere — the acceptance criterion).
3. `BalanceReport` (full output in the saved run):
   - Section 2: chain damage 2/4/6 (Beginner, bands 10–12) up to 3/7/11 (Advanced, bands 13–15); chain-to-defeat = 3 everywhere; 4-isolated total 8 or 12 < band, always survivable;
   - Section 3: combinatorial counts per band/difficulty (e.g. band 10/B: A=3, B=457, C=126, D=289) — category B dominates (HP defeats at low accuracy are common by construction), each cell contains every category;
   - Section 4: Model U pass shares 16–97% across rates; HP-defeat share 1.30–78.50% falling with rate and band. Model T (−10pp/−20pp tiers, documented assumption) shows HP-defeat shares 12–93% — visible difficulty-vs-band difference for Advanced. Shares sum to 100% per cell (deduplicated masks).
4. `tests/ProgressConsistencyCheck`: all 5 checks pass under the new model.
5. GUI smoke: `GameApplication` and `StagePreview intermediate` launched, clean stdout/stderr, no exceptions; real save hash unchanged (§6).
6. Brief phase acceptance verified: no fixed HP constant in flow code; both front-ends start through `startSession` (unchanged from Phase 2); no interrupted attempt banks XP/score (Phase 2 settlement retained); and per the brief's Phase 3 acceptance — HP-primary and accuracy-primary failures demonstrated for every tested difficulty and HP band, including a quiz ending in HP defeat while the answered accuracy reads ≥70 (the 8-correct + 3-consecutive-wrong case, verified with real machinery at 8/11 = 72).

## 9. Manual test steps for the owner

1. Play a first-clear quiz normally: after one wrong answer the damage feels gentle; careful not to make *consecutive* mistakes — the 2nd and 3rd consecutive hit noticeably harder (6 HP gone by the 3rd on a 10-HP band).
2. Deliberately answer three wrong in a row early: expect defeat and a results screen whose accuracy still shows the answered correctness (e.g. 8 right of 11 answered = 72%) but an "OVER" result; then replay and pass by spreading mistakes.
3. Try Advanced on a high-XP profile (`StagePreview all --xp=1000` after Phase 5; until then `--xp=750` for a 15-HP Level 15 band): three consecutive mistakes cost 3+7+11 = 21 HP — instant defeat even at max HP.
4. Confirm isolated mistakes stay cheap: alternate correct/wrong for a full quiz — the run ends alive (accuracy-fail at 66%) with 8 lost HP (Beginner band 10).
5. Run `java -cp <build> edu.batstateu.javajolt.preview.BalanceReport` and `...preview.MechanicsCheck` — expect the tables and ALL CHECKS PASSED.
6. Play a replayed (already completed) topic: XP counter must stay flat (settlement rules from Phase 2 intact under the new damage model).
7. Verify `saves/player.properties` only changes through your own confirmed saves.

## 10. Docs updated

- `progress-documentations/gameplay-design-notes.md`: new §2.5 (enemy damage model + answered-count accuracy + dev-tool extensions, in the established implementation-notes style); §8 row 6 (enemy identity) → DECIDED (Phase 3) AND IMPLEMENTED with the open "signature mechanic" question retained; §8 row 8 (pass conditions) → DECIDED (Phase 3) with the answered-count rule; §9 Stage 3 → STARTED (Phase 3). All design prose in §2 table, §3–§7 left untouched.
- `PROJECT_RULES.md`: "Where things live" — `game/` line adds `Enemy`; `data/` line adds `EnemyRoster`; `LessonRunnerService` description now says "enemy damage".
- `progress-documentations/progress.md`: not touched (forbidden).
- This report saved under `progress-documentations/change-reports/`.

## 11. Deviations from this brief, assumptions made, and risks

1. **Enumeration semantics (documented in tool output):** the brief's "4,096 sequences" classification counts each *realized* outcome once — masks that differ only after a killing answer (which never got answered) collapse to one outcome. Without this, early defeats were counted 2^r times and probability shares summed far above 100% (caught and fixed during verification; sums now check out exactly).
2. **Model T rate assumptions:** I chose −10pp (application) / −20pp (challenge) on the four-per-tier structure as a reasonable "later questions are harder" assumption, printed next to Model U and labeled an assumption — the brief allows "deterministic enumeration/weights or fixed-seed simulation" with documented assumptions. Real difficulty data can replace the offsets later (a one-line change in `tieredSlots`).
3. **`lastDamage` semantics pre-Shield:** recorded as actual HP lost (`hpBefore - hpAfter`), i.e. the Phase 5 Shield rule ("after Shield") is the natural future behavior without changing this record now.
4. **Tests-tool shape:** `ProgressConsistencyCheck`'s helper needed both enemy wiring and spread mistakes; its assertions and test intents are otherwise untouched, so Phase 2–3 guarantees are still exercised by the legacy tool.
5. **No damage display:** the console "HP left" line and the Swing HP label show current HP only; per-hit damage (`lastDamage`) is recorded but not yet shown — UI wiring belongs to Phase 6, so nothing there changed.
6. **Risks:** (a) Enemy escalation makes failed first-clears statistically frequent at low correctness rates (BalanceReport §4) — a balance question for the owner, tunable via the roster constants; (b) the MechanicsCheck simulation must stay in step with the real rule chain — it is cross-checked by real-machinery scripted runs per band/difficulty, which currently agree everywhere; (c) `MainWindow.questionService` remains unused for selection (carried from Phase 2).

## 12. Noticed but intentionally not changed

- Enemy identity is not yet shown anywhere in the UI (`MainWindow`/screens untouched; Phase 6 wires enemies, badges, animations).
- `DifficultyProgressionService`, achievements, `QuestionService`'s tier ordering (`getChallengeTier`) and question selection are untouched — enumeration assumes the existing easy→hard order.
- `BalanceReport`'s Section 2 "chain to defeat" is always 3 with the current constants — the column exists so future tuning changes show up automatically.
- Deserialized old saves have no per-session resilience because session state stays non-persisted (intentional).
- `MainWindow`'s unused-for-selection `questionService` field and its caller-less 4-arg constructor remain (Phase 2 notes, unchanged).
- No changes to achievement thresholds, difficulty-unlock messaging, or the textbook flow.

**Phase 3 is complete per brief v1.3. Stopping here — Phase 4 starts only when the owner says "Proceed to Phase 4".**
