# Phase 6 Report: Functional UI wiring

## 1. Summary

The Swing UI is now functionally wired to the Phase 5 power-up logic. `QuestionScreen` (still a pure view) gained a right-aligned power-up row at the battle panel's SOUTH — one button per enum value, labeled by the enum itself — that stays completely invisible while nothing is unlocked or no unanswered question is active, so every existing quiz layout is byte-identical for players below Level 5. `showBattleMeta(...)` (new view method, driven by `MainWindow`) names the enemy under the enemy-side placeholder, refreshes the row, folds "already used" and "not applicable" into a disabled set, and shows armed states as "Shield ready" / "Boost ready" (disabled, brief's exact wording). `Hint` hides the returned wrong choice via `applyHintRemovedChoices(...)` matched against the raw `choiceValues` (never the HTML-wrapped button text); the buttons are rebuilt per question, so no hint state carries over and answer letters never renumber. HP now reads `HP: x/y` via `player.getMaxHp()`, the streak label refines to `(+X points · doubled)` when a boost produced the last award (one display, no duplication; `GameSession` gained a display-only `lastAnswerBoosted` flag recorded by the answer flow), and defeat feedback names the enemy ("Defeated by <enemy>! Continue to see your results." on the battle card, "Defeated by <enemy>" on `ResultsScreen`). Amendment 1 is enforced in the controller: `MainWindow.questionActive` turns off in `submitAnswer` before the SwingWorker starts and back on after `setCurrentQuestionIndex(questionIndex + 1)` (the preserved Phase 0 call) for the next question — so no power-up can be used after submission, during transitions, or after the quiz, independent of the disabled buttons. Level-up/unlock notices reuse the achievements' `xpAtLessonStart` before/after pattern: `levelAtStart/levelAtEnd` plus `unlockedBetween(old, new]` build one notice string (single or multiple unlocks supported, exactly `null` when nothing changed), passed as a trailing `ResultsScreen` parameter. Amendment 4 holds: accuracy reuses `session.getAccuracyPercent()` / `getCorrectCount()` / `getAnsweredCount()` — the ACCURACY card now reads `8/11 (72%)` and the bar string `72% (8/11)`, no new formula anywhere. `PowerUpService.isApplicable(...)` (HINT → `question.supportsHint()`, TIME_STOP → false until Phase 7, Shield/Boost → true) keeps the service authoritative; `Question.supportsHint()` defaults false, `MultipleChoiceQuestion` overrides to `choices.size() > 2`. Verified: compile exit 0, **MechanicsCheck 296/296** (287 + 9 new checks), `ProgressConsistencyCheck` 5/5, `BalanceReport` clean, all four GUI launch smokes clean, real save byte-identical across the whole run.

## 2. Files

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `src/edu/batstateu/javajolt/ui/swing/screen/QuestionScreen.java` | MODIFIED | Pure-view additions only: new constructor callback `Consumer<PowerUp> onPowerUp` (4th param); fields `powerUpRow` (fixed `createPowerUpRow()` — FlowLayout right, `GAP_S`, 4 `RoundedButton` SECONDARY built from `PowerUp.values()`, row invisible initially), `powerUpButtons` (EnumMap), `enemyNameLabel` (centered muted, invisible until named, SOUTH under the enemy placeholder), `currentEnemyName`; new `showBattleMeta(enemyName, unlocked, disabled, armed, questionActive)`, `applyHintRemovedChoices(removedChoices)`, `showPowerUpNotice(message)` (writes the result message into the existing `feedbackLabel` — no dialogs); HP `x/y` in `showQuestion`/`showAnswerResult`; `refreshStreakLabel(streak, lastPoints, lastAnswerBoosted)` renders `(+" + points + " points · doubled)`; both display methods gained a trailing `boolean lastAnswerBoosted`; `createFeedbackCard` defeat footer names the enemy (previously-generic "Out of HP" kept as fallback when no name is set). |
| `src/edu/batstateu/javajolt/ui/swing/MainWindow.java` | MODIFIED | Controller wiring: `questionActive` field; `QuestionScreen` construction gains `this::usePowerUp`; `submitAnswer` sets `questionActive = false` before the SwingWorker and its `done()` passes `session.isLastAnswerBoosted()` then calls `refreshBattleInfo(false)`; `showNextQuestion` sets `questionActive = true` after the preserved `setCurrentQuestionIndex(questionIndex + 1)` call, passes the boost flag to `showQuestion`, then `refreshBattleInfo(true)`; results block computes `levelAtStart/levelAtEnd` from `xpAtLessonStart` (same before/after pattern as achievements) and passes `buildProgressNotice(...)` to `ResultsScreen`; new privates `usePowerUp` (range + `questionActive` guard, then `powerUpService.use(...)`, applies Hint choices, shows the notice, re-arms the meta), `refreshBattleInfo(boolean)` (view-only sets: unlocked / used+not-applicable disabled / armed via read-only probes), `buildProgressNotice` (null when nothing changed; single/multiple unlock wording); `startLesson` initializes `questionActive = false`. |
| `src/edu/batstateu/javajolt/ui/swing/screen/ResultsScreen.java` | MODIFIED | Trailing `String progressNotice` (null/blank renders nothing; existing struts untouched); ACCURACY card value `correctCount + "/" + answeredCount + " (" + accuracyPercent + "%)"` and bar string `accuracyPercent + "% (" + correctCount + "/" + answeredCount + ")"` — all three from the session's existing calculations (local reads only, no new formula); `Defeated by <enemy>` line (Theme.DANGER, tight GAP_S strut) after `Mistakes:` when the player is not alive. |
| `src/edu/batstateu/javajolt/game/GameSession.java` | MODIFIED | Display-only state: `lastAnswerBoosted` (set by the new `recordCorrect(int, boolean)` overload; reset by `recordMistake`), accessor `isLastAnswerBoosted()`; the approved armed probes `isShieldArmed()`/`isPointBoostArmed()` (read-only; consume methods unchanged). |
| `src/edu/batstateu/javajolt/service/PowerUpService.java` | MODIFIED | New `isApplicable(PowerUp, GameSession, Question)`: HINT → `current.supportsHint()`, TIME_STOP → false (Phase 7), SHIELD/POINT_BOOST → true. Phase 5 methods untouched. |
| `src/edu/batstateu/javajolt/Question.java` | MODIFIED | `supportsHint()` default false (button-state hook; randomness stays in `getHintChoicesToRemove`). |
| `src/edu/batstateu/javajolt/types/MultipleChoiceQuestion.java` | MODIFIED | `supportsHint()` override: `choices.size() > 2`. |
| `src/edu/batstateu/javajolt/service/LessonRunnerService.java` | MODIFIED | One line: `session.recordCorrect(points, boosted)` so the display flag reflects the actual answer. |
| `src/edu/batstateu/javajolt/preview/MechanicsCheck.java` | MODIFIED | `checkPowerUps()` extended with 9 checks (§8); header now says Phase 2-6. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED (docs) | §9 Stage 4 → WIRED FOR USE (Phases 5-6), Time Stop pending (Phase 7). |
| `progress-documentations/change-reports/phase-6-functional-ui-wiring.md` | NEW | This report. |

`PROJECT_RULES.md` unchanged — nothing structural moved; the service/enum/hook additions live in already-documented places.

## 3. Ownership and rule check

| Rule / concern | Owner |
| --- | --- |
| Screens stay pure views | `QuestionScreen`/`ResultsScreen` only display; every rule arrives via constructor callbacks/params |
| Power-up activation & applicability | `PowerUpService` (`use`, `isApplicable`) — controller never invents rules |
| Once-per-quiz / unlocked / armed state | `GameSession` (guards its own state; copy-out usage set; read-only armed probes) |
| Post-submission ban (Amendment 1) | `MainWindow.questionActive` (controller state, not the buttons) |
| Hint reset / no renumbering (Amendment 2) | per-question editor rebuild + `choiceValues` matching in the view |
| Unlock levels & notifications (Amendment 3) | `PowerUp` enum + `PowerUpService.unlockedBetween` + controller notice string |
| Accuracy display (Amendment 4) | `GameSession.getAccuracyPercent()/getCorrectCount()/getAnsweredCount()` only |
| Composition | `MainWindow` gets `UsePowerUp` wired as a method reference; no `new` inside the view |

PROJECT_RULES compliance: UI holds no game rules; nothing outside `ui/` imports Swing; no new libraries/test frameworks; no `instanceof` on `Question`; `SaveService`/`SaveData`, `out/`, `saves/`, kit assets untouched; existing components/`Theme` only. The Enemy Design Boundary holds: no enemy identity or new behavior — only the name string from the existing roster is displayed, and `resolveMistake` is untouched.

## 4. Existing code touched

1. `QuestionScreen` — the brief's §Phase 6 target (HP x/y, enemy name, streak refinement, unlocked power-up row, Hint hiding, defeat text) plus the approved service-side enablers' display hooks.
2. `MainWindow` — brief's controller duties (power-up callback via `PowerUpService.use`, preserved `setCurrentQuestionIndex` call untouched, unlock detection with the `xpAtLessonStart` pattern, `progressNotice` param).
3. `ResultsScreen` — brief's results refinements (accuracy display, level/unlock notice, defeat naming).
4. `GameSession`, `PowerUpService`, `Question`, `MultipleChoiceQuestion`, `LessonRunnerService` — the approved enablers only (getters/hook/`isApplicable`/one-line flag).
5. `MechanicsCheck`, notes — verification + documentation duties (§10 of the brief).

## 5. Player-visible behavior changes (before -> after)

- Quiz screen now shows the enemy's name under the enemy art, and HP reads `HP: 11/15` instead of `HP: 11`.
- A Level >= 5 player starting a quiz sees a right-aligned row of power-up buttons above (south of) the battle area: unlocked ones enabled, Hint disabled on questions with fewer than 3 choices, Time Stop always disabled with its message, used ones disabled with the service's message; Shield/Boost read "Shield ready" / "Boost ready" while armed and cannot be re-clicked.
- Using a power-up writes its service message into the existing feedback line (no dialogs).
- A Hint on a multiple-choice question visually removes one wrong choice immediately; the next question never shows leftovers.
- The streak line reads `(+20 points · doubled)` right after a boosted correct answer, otherwise as before.
- Answering while defeated now says "Defeated by <enemy name>! Continue to see your results." and the results screen appends "Defeated by <enemy>".
- Results: ACCURACY card `8/11 (72%)`, bar `72% (8/11)`; a new line ("New level reached: N!" / "New power-up unlocked: Hint!" / both, multiple unlock names comma-joined) appears only when something actually unlocked or leveled.
- Players below unlock threshold / all answers used: quiz layout identical to Phase 5 (row invisible, zero height).

## 6. Save compatibility

- Expected: no change — confirmed. No save-format or `SaveService`/`SaveData` code touched; all new state is session/UI-local.
- Real-save safety proof: SHA-256 of `saves/player.properties` was `47162B381C4A3A942A656D4D741FEBF2C6557D2212DD93519C6D0E5C0019A302` before the first GUI smoke and after all four (GameApplication, StagePreview intermediate, `all --xp=1000`, `all --xp=100`) — byte-identical. The absolute value differs from the Phase 5 baseline because you played the real game in between (owner's own save write, not this phase).

## 7. Balance values introduced or changed

None. This phase wires display and interaction; every constant (damage, escalation, XP, scoring, unlock levels 5/10/15/20) is exactly as Phase 5 left it. `isApplicable` only gates existing rules for display.

## 8. Verification

Compile to `%TEMP%\opencode\jj-build-phase6`; run from the project root.

1. Full compile (src + tests): **exit code 0** (three runs: initial, after the one ordering fix in the new checks, final).
2. `MechanicsCheck`: **296/296 PASS** (287 prior + 9 new). New checks: `supportsHint` — 4-choice MC true, 2-choice MC false (built via constructor, not `instanceof`), identification default false; `isApplicable` — Hint on 4-choice MC true, on identification false, Shield/Boost true on any current question, Time Stop false everywhere until Phase 7; boost display flag — captured `true` right after the boosted correct answer (paid 20), `false` after the following plain correct answer (paid 12, confirming `recordCorrect(int, boolean)` state handling in both directions). One temporary failure (`boost: the boosted answer is flagged`) was my own check ordering — the flag was read after a subsequent plain answer had already cleared it; fixed by capturing the value at the boosted point, no product-code change involved.
3. `tests/ProgressConsistencyCheck`: **5/5 pass** (answer-flow one-liner didn't disturb failure/completion/save semantics).
4. `BalanceReport`: unchanged, runs clean.
5. GUI smokes (save hash identical across all, §6): `GameApplication` and `StagePreview intermediate` — clean, no exceptions; acceptance runs `StagePreview all --xp=1000` (Level 20 window — all four power-up buttons render, Time Stop disabled) and `StagePreview all --xp=100` (Level ~3 — row fully hidden, layout identical to Phase 5) — both clean with empty stderr.
6. Acceptance checks per your amendments: button disabled states derive from `unlocked`/`disabled`/`armed` sets pushed by the controller (verified by construction + the service checks above); activation after submission is impossible (controller guard `!questionActive` returns before the service is ever called, and the row is hidden by `refreshBattleInfo(false)`); Hint resets per question (editor rebuild); level-up/unlock notices null out when nothing changes; accuracy shows counts + the session's own percentage.

## 9. Manual test steps for the owner

1. Run `MechanicsCheck` — expect `ALL CHECKS PASSED` (296). `BattleReport`/PCC also clean.
2. `java ... StagePreview all --xp=1000`: all four buttons visible; Hint usable on 4-choice questions (disabled on hypothetical fewer-choice ones); Time Stop disabled ("Time Stop is not available yet."); use Shield then answer wrong → "Shield armed..." message and a "Shield ready" disabled button; use Point Boost then answer correctly → "+20 points · doubled" streak line and "Boost ready".
3. Submit any answer, then (if before you were blocked) confirm no power-up click does anything while an answer is being checked or on a feedback card; also confirm the row disappears on the answer result and reappears with the next question.
4. `--xp=100`: no power-up row at all; battle area layout identical to before (HP now `x/y`).
5. Answer everything right on a low-level save near a level edge (§6 note below: easier via XP overrides in StagePreview), e.g. crossing two unlock levels in one quiz: the results screen should show "New level reached: N!" and "New power-ups unlocked: Shield, Time Stop!" — and show none of that when nothing crossed.
6. Get defeated: feedback card names the enemy, results screen shows the danger-red "Defeated by <enemy>".
7. Confirm `saves/player.properties` changed only by your own saves.

## 10. Docs updated

- `progress-documentations/gameplay-design-notes.md`: §9 Stage 4 → "WIRED FOR USE (Phases 5-6)" with the interaction rules just approved (unanswered-question-only usage, per-question Hint reset, ready-states) and the remaining pending item (Time Stop's Phase 7 effect). §8 row 2 (power-up limit) unchanged from Phase 5.
- `PROJECT_RULES.md`: unchanged — the new pieces live in already-listed packages (no structural additions).
- `progress-documentations/progress.md`: not touched (forbidden).
- This report saved under `progress-documentations/change-reports/`.

## 11. Deviations from the approved plan, assumptions made, and risks

1. **`showBattleMeta` gains a 5th parameter (`questionActive`)** — the approved plan showed a 4-set meta; while implementing, the post-submission ban needed the row hidden exactly when the question stops being active, and a controller-side boolean is the cleanest way to express "row shows only while an unanswered question is active" without the view second-guessing rules. The four view-only sets (unlocked/disabled/armed/enemy name) are exactly as approved.
2. **`recordCorrect(int, boolean)` overload (approved during planning)** — needed to display "· doubled" truthfully. The brief's Phase 5 sketch call `session.recordCorrect(points)` still compiles via the 1-arg delegate; `LessonRunnerService` now calls the 2-arg form. No other callers exist.
3. **Armed-state enforcement via `isShieldArmed()`/`isPointBoostArmed()` reads** (per the plan's refinement): the consume methods were clear-on-read and would have been destructive probes, so two read-only getters were added (approved enabler list).
4. **`showLessonResult` intentionally not changed** — it is dead code in the current build (defined, never called; the results screen shows via `ResultsScreen`). Touching it would be unrelated refactoring; noted here so its existence stays an explicit, tracked decision. It uses its own generic defeat wording if ever revived.
5. **Assumption:** `ChallengeTier`-tiered MC questions and code questions always follow the `"A. text"` label convention (Hint matching relies on it, same assumption as Phase 5's 200-run check).
6. **Risks:** (a) the power-up row sits at the battle panel's SOUTH (approved choice c) rather than inside `actionRow` — nothing overlaps at current window sizes, re-verified in the `--xp=1000` and `intermediate` launches; (b) `usePowerUp` runs on the EDT (fine: no I/O — service use is pure in-memory); (c) hidden-but-selected hint choice selection is cleared defensively (`clearSelection`) so `readAnswer` can never return a removed choice.

## 12. Noticed but intentionally not changed

- Time Stop's actual effect (Phase 7 timer exists first; it stays everywhere-disabled with its exact message until then).
- Console mode has no power-up commands (per the brief's UI-scope; unchanged from Phase 5's report).
- Enemy behavior, roster, and damage model untouched (Enemy Design Boundary).
- `MainWindow`'s caller-less 4-arg constructor kept (it delegated before; it grew a `PowerUpService` parameter in Phase 5 to stay consistent for any future callers).
- The duplicated import block at the top of `QuestionScreen` (lines 3-24 area: `ActionEvent`/`InputEvent`/`KeyEvent`/`AbstractAction`/`JComponent`/`JProgressBar`/`KeyStroke` declared twice) predates this phase — left as-is (unrelated refactoring).
- Sanitizing/hardening of `feedbackLabel` text (e.g., multi-line messages) is unchanged; the service messages are single-line by design.
- Results-screen XP bar and settlement paths untouched (Phase 2 semantics hold).

**Phase 6 is complete per brief v1.3 with all four amendments. Stopping here — Phase 7 starts only when the owner says "Proceed to Phase 7".**
