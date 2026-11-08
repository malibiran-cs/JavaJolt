# Phase 1 Report: Heal power-up and unlock order

## 1. Summary

Heal replaces Point Boost, restoring difficulty-based HP instantly once per quiz. Unlock order is Hint / Shield / Heal / Time Stop at levels 5 / 10 / 15 / 20. Ordinary correct-streak scoring remains 10, 12, 14, 16...; the obsolete multiplier, armed state and display flags are removed. No tutorial behavior or persistence field is added in this phase.

## 2. Files

Java paths are relative to `src/edu/batstateu/javajolt/` unless prefixed with `tests/`.

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `enums/PowerUp.java` | MODIFIED | Heal at level 15; Time Stop at 20; enum order matches unlock order. |
| `service/PowerUpService.java` | MODIFIED | Heal tuning, public percentage/amount queries, applicability, recovery, consumption and actual-recovery message. |
| `service/ScoreService.java` | MODIFIED | Remove multiplier and boolean argument; retain base plus streak growth. |
| `service/LessonRunnerService.java` | MODIFIED | Use the single score/session entry points without consuming a scoring effect. |
| `game/GameSession.java` | MODIFIED | Remove scoring-effect state and methods; keep one `recordCorrect(int)` entry point. |
| `ui/swing/MainWindow.java` | MODIFIED | Remove obsolete display arguments and armed probe; refresh displayed HP after Heal. |
| `ui/swing/screen/QuestionScreen.java` | MODIFIED | Remove obsolete label logic; expose a small HP refresh hook without rebuilding inputs. |
| `preview/MechanicsCheck.java` | MODIFIED | Replace obsolete checks; verify exact unlocks, Heal rules and unaffected scoring/statistics. |
| `preview/StagePreview.java` | MODIFIED | Document XP probes for Heal and Time Stop; existing arbitrary XP override needs no logic change. |
| `data/ArraysIntermediateExpansion.java` | MODIFIED | Reword one explanation containing “doubled” to satisfy the literal source cleanup; arithmetic/content meaning is unchanged. |
| `tests/ProgressConsistencyCheck.java` | MODIFIED | Load a temporary legacy-format fixture without any power-up data. |
| `tests/SwingTimerFlowCheck.java` | MODIFIED | Verify full-HP disabled state, activation after damage, displayed recovery/HP, preserved input and used state. |
| `PROJECT_RULES.md` | MODIFIED | Correct ScoreService and PowerUpService ownership descriptions. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED | Replace power-up table and development-order description; document implemented rules. |
| `progress-documentations/change-reports/phase-1-heal-power-up.md` | NEW | This report. |

## 3. Ownership and rule check

`PowerUp` alone owns unlock levels. `PowerUpService` owns Heal percentages, integer calculation, rejection and once-per-quiz use; `Player.heal` already caps HP and is reused unchanged. `GameSession` owns transient usage and answer counters; `ScoreService` owns scoring. Swing displays results and reports actions, with no recovery arithmetic. Existing constructor injection and composition roots are unchanged. No PROJECT_RULES exception, library, asset, restyling or save change was required.

## 4. Existing code touched

Every existing-file edit and its reason is listed in section 2. The question/result view signatures and their two controller calls lose the obsolete boolean together. The existing power-up row automatically follows enum order and continues hiding locked buttons and disabling used/inapplicable buttons. The only new view hook is `showHp(Player)`, also used for timeout HP refresh.

## 5. Player-visible behavior changes (before -> after)

- Armed next-answer score effect -> instant Heal, with an inline notice reporting actual HP recovered.
- Time Stop at level 15 -> Heal at 15 and Time Stop at 20.
- Full HP -> Heal unavailable; direct service requests are rejected with a message without consuming the use.
- After recovery -> HP label refreshes immediately; current answer selection/editor remains intact.
- Score feedback -> ordinary streak and points only. Shield's existing ready label remains.

## 6. Save compatibility

Save format and APIs are unchanged; power-up usage remains in `GameSession`. Save/load and a legacy property fixture pass through the unchanged `SaveService`, using a temporary path. Preview wiring still uses `PreviewSandbox`; no test or preview here opened or modified the real `saves/` folder. Builds use `%TEMP%`, leaving `out/`, `build/` and assets untouched.

## 7. Balance values introduced or changed

| Owner | Constant/value | Purpose |
| --- | --- | --- |
| `PowerUpService` | `BEGINNER_HEAL_PERCENT = 20` | Beginner share of max HP. |
| `PowerUpService` | `INTERMEDIATE_HEAL_PERCENT = 35` | Intermediate share of max HP. |
| `PowerUpService` | `ADVANCED_HEAL_PERCENT = 50` | Advanced share of max HP. |
| `PowerUpService` | `MINIMUM_HEAL_AMOUNT = 1` | Minimum potential recovery before HP cap. |
| `PowerUp` | `HEAL("Heal", 15)` | Replacement unlock. |
| `PowerUp` | `TIME_STOP("Time Stop", 20)` | Reordered unlock. |

Hint 5, Shield 10, base score 10 and streak increment 2 remain unchanged. The score multiplier is removed. `getHealPercent(Difficulty)` and `getHealAmount(int, Difficulty)` expose the owning rules for future tutorial text; UI notices use actual HP change after `Player.heal` caps it.

## 8. Verification

Compilation from the project root (all sources and plain Java tests):

```powershell
$healBuild = Join-Path $env:TEMP 'javajolt-heal-phase1-build'
New-Item -ItemType Directory -Force -Path $healBuild | Out-Null
$healSources = @(rg --files src tests -g '*.java')
[System.IO.File]::WriteAllLines((Join-Path $healBuild 'sources.txt'), $healSources)
javac -encoding UTF-8 -d $healBuild ('@' + (Join-Path $healBuild 'sources.txt'))
java -cp $healBuild edu.batstateu.javajolt.preview.MechanicsCheck
java -cp $healBuild ProgressConsistencyCheck
java -cp $healBuild edu.batstateu.javajolt.preview.BalanceReport
java -cp "$healBuild;src" SwingTimerFlowCheck
java -cp $healBuild ContentCoverageCheck
java -cp "$healBuild;src" SwingNavigationSmokeCheck
java -cp "$healBuild;src" EnemyArtworkIntegrationCheck
rg -n -i 'boost|doubled' src tests PROJECT_RULES.md progress-documentations/gameplay-design-notes.md
git diff --check
```

Final observed result excerpts:

```text
Compilation: exit 0
Checks: 453 passed, 0 failed, 453 total.
ALL CHECKS PASSED
PASS: Legacy save without power-up data
ALL PROGRESSION CHECKS PASSED
ALL SWING TIMER FLOW CHECKS PASSED (27)
ALL CONTENT COVERAGE CHECKS PASSED (21 topic/difficulty combinations, 504 questions in bank)
ALL SWING NAVIGATION CHECKS PASSED (12)
ALL 7 ENEMY PORTRAITS PASSED
```

BalanceReport exited 0 with the existing level, survivability, enumeration and weighted tables. All six regression suites passed. The source/living-doc scan returns no matches (rg exit 1 is expected); historical reports and AGENT_BRIEF are excluded. Diff whitespace check passes with existing Git LF/CRLF normalization notices. ProgressConsistencyCheck and EnemyArtworkIntegrationCheck initially hit sandbox denial reading the JDK's `java.security`; their approved reruns passed. Headful Swing checks use temporary saves and shortened injected timers.

After report creation, compilation and all 453 mechanics checks passed again, including a strengthened check that the same player can use Heal in a fresh session after using it in the previous quiz. Content, navigation and artwork regression checks also passed. Swing's 27-check run passed before the final test-only change; gameplay and view behavior were unchanged afterward.

## 9. Manual test steps for the owner

1. Launch `java -cp "$env:TEMP\javajolt-heal-phase1-build;src" edu.batstateu.javajolt.preview.StagePreview all --xp=750`. Confirm Heal appears and Time Stop is still locked.
2. Start Variables on each difficulty. Heal is disabled at full HP. Take damage, then use Heal; confirm displayed HP rises, capped recovery is reported, inputs remain intact and the button becomes disabled.
3. At the level-15 max of 15 HP, potential recovery is 3 / 5 / 7 by difficulty, limited by missing HP. Start another quiz to reset HP and usage.
4. Repeat with `--xp=1000`; all four buttons appear in Hint / Shield / Heal / Time Stop order. Confirm Time Stop and Shield still work.
5. Use `--xp=749` to confirm Heal remains locked. Answer correctly in sequence to observe 10 / 12 / 14 / 16 points; after a mistake the next correct answer gives 10.

## 10. Docs updated

- `PROJECT_RULES.md`, where-things-live map: ScoreService line removes the old multiplier; PowerUpService line includes difficulty-based recovery.
- `gameplay-design-notes.md`, sections 3.1, 8 power-up row and 9 Stage 4: final pool/order/levels, one use each, recovery arithmetic, cap/rejection and functional UI behavior. Pre-existing edits elsewhere are preserved.
- This report added under the new task's Phase 1 numbering; older phase reports and AGENT_BRIEF are unchanged.

## 11. Deviations from this brief, assumptions made, and risks

The current user task supersedes historical power-up decisions in AGENT_BRIEF; history was not rewritten. Full-HP rejection follows Hint's existing pattern. The amount query reports potential recovery from max HP; the activation notice reports actual recovery, including a partial cap. StagePreview already supports arbitrary XP, so it needed probe documentation only. The source-wide wording requirement was interpreted literally, hence the single equivalent question-explanation edit.

Manual full-length playthrough/layout review remains for the owner; automated Swing checks exercise the real controller/view with deterministic fixtures. No tutorial phase has started. RUN_TESTS.bat is deferred to the requested final phase; it currently writes to `out/`, which PROJECT_RULES forbids touching, so Phase 1 checks use the temporary-build equivalents.

## 12. Noticed but intentionally not changed

Existing architecture deviations, other historical design prose, console controls and preview launch behavior remain unchanged. Existing edits to ShakSuggestions.txt and gameplay notes, plus the untracked AGENT_BRIEF, were preserved. Changes are uncommitted. Stop after Phase 1 verification and wait for owner approval before Phase 2.
