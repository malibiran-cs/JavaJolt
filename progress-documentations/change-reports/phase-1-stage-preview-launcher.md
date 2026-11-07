# Phase 1 Report: Stage preview launcher (new file); SwingPreview removed

*Revision 1 (post-review cleanup, owner-directed): the obsolete legacy launcher `ui/swing/SwingPreview.java` was removed, `GameApplication` is the single official entry point for normal gameplay, and this report now reflects the final state of Phase 1. The brief was updated to v1.3 accordingly. Historical notes about the interim `SwingPreview` isolation attempt were removed from the actionable sections and are kept only where the phase-0 audit documents them as history.*

## 1. Summary

A new development launcher `preview/StagePreview` lets the owner open a preview game at Beginner, Intermediate, Advanced, or in `all` mode (difficulty chooser plus a dev-only switcher window), with every topic freely selectable and an optional `--xp=N` override, while all saving happens in a throwaway temp directory instead of `saves/`. A shared `preview/PreviewSandbox` helper is the single home of that isolation rule. Per the owner's post-review decision, the obsolete legacy launcher `SwingPreview` was deleted and `GameApplication` remains the single official entry point for normal gameplay. The real `saves/player.properties` was hash-checked before and after live runs of both entry points and is byte-identical; the D12 first-run guard from the owner's commit `591ac8b` was reviewed and left untouched.

## 2. Files

| Path | NEW/MODIFIED/REMOVED | What changed and why |
| --- | --- | --- |
| `src/edu/batstateu/javajolt/preview/StagePreview.java` | NEW | Phase 1 launcher: own `main`, `<beginner\|intermediate\|advanced\|all>` modes, `--xp=N` override, preview player seeding, difficulty chooser (for `all`), dev difficulty-switcher window, `[PREVIEW - not saved]` title tag, opens straight on `ScreenId.MAIN_MENU`. |
| `src/edu/batstateu/javajolt/preview/PreviewSandbox.java` | NEW | Single home of the save-isolation rule: creates a `javajolt-preview` temp dir, `SaveService(Path)` (existing overload), one seed save written to the sandbox, file `deleteOnExit`. |
| `src/edu/batstateu/javajolt/ui/swing/SwingPreview.java` | REMOVED (Revision 1) | Obsolete legacy launcher, deleted by owner decision. Its only purpose (viewing the UI without touching `GameApplication`) is superseded by `GameApplication` for normal gameplay and by `StagePreview` for testing. |
| `PROJECT_RULES.md` | MODIFIED (docs) | Added a `preview/` bullet to "Where things live" (new classes need a listed place); trimmed the known-deviations clause naming the now-deleted `SwingPreview` temporary launcher. |
| `progress-documentations/AGENT_BRIEF.md` | MODIFIED (docs, Revision 1) | Updated to v1.3: header change note, section 2 source list and entry-point statement, D10, section 6 constructor-caller list, Phase 0 paragraph, Phase 1 save-isolation bullet — all `SwingPreview` maintenance/verification references removed or replaced with the removal decision; `StagePreview` untouched. |
| `progress-documentations/change-reports/phase-1-stage-preview-launcher.md` | NEW | This report (revised to final state). |

## 3. Ownership and rule check

| Rule | Owner |
| --- | --- |
| Preview save isolation (D10) | `PreviewSandbox` (only place that knows about the temp sandbox; `SaveService` untouched and reused as-is) |
| Preview player seeding and XP defaults | `StagePreview` (`seedPlayer`, `chosenXp`) |
| Difficulty choosing for `all` (dev-only, bypassing progression gates) | `StagePreview` (`askStartDifficulty`, `showDifficultySwitcher`) |
| Normal gameplay entry | `GameApplication` — unchanged, single official entry point |
| First-run "no save before name" guard (D12) | `MainWindow` — owner-supplied; verified, not modified |
| Difficulty-stay-stable seeding (first six topics) | `StagePreview.seedPlayer` (preview-only; never applies to the real game) |

- PROJECT_RULES compliance: services/entities/enums/data untouched; no new libraries; no `instanceof` on `Question`; no changes to question content; `out/`, `saves/`, kit assets untouched; DI honored (`StagePreview` is a composition root (`new` of wiring only); `MainWindow` receives everything through its existing 5-arg constructor).
- Rule tension noted, not bent in substance: the literal wording "nothing outside `ui/` imports Swing" cannot hold for a launcher that must open a Swing window; `GameApplication` (outside `ui/`) already imports Swing for the same reason. The rule's protected directions (`entity/`, `enums/`, `exception/`, `data/` must not depend on `ui/`) do not apply to `preview/` dev launchers with a `main` entry.
- No rule bent otherwise; no clarifying questions needed.

## 4. Existing code touched

1. `src/edu/batstateu/javajolt/ui/swing/SwingPreview.java` — **removed** (owner decision; brief v1.3 records it). Justification: obsolete once Swing was integrated into `GameApplication`; keeping two launchers risked divergence. No other class referenced it (`grep` clean).
2. `PROJECT_RULES.md` — added the `preview/` bullet under "Where things live"; trimmed the `SwingPreview` clause from the known-deviations line (that line became false after the removal; rest of the line unchanged). Justification: documentation duty.
3. `progress-documentations/AGENT_BRIEF.md` — version 1.3 header note plus the section 2 / D10 / section 6 / Phase 0 / Phase 1 edits listed in section 2. Justification: owner's explicit instruction to update the brief throughout.

No gameplay code, service, entity, screen, or save plumbing was edited. `MainWindow`, `GameApplication`, `SaveService`, `SaveData`, `LevelService`, `QuestionService`, and all screens are unchanged from Phase 0.

## 5. Player-visible behavior changes (before -> after)

Real game: **unchanged** (same screens, same flow, same entry point behavior).

Preview/dev tools:
- `SwingPreview` entry point: **removed** (no longer launchable from source).
- New `StagePreview`: `StagePreview beginner|intermediate|advanced|all [--xp=N]` opens a fully unlocked chosen difficulty under a `Preview` player, straight on the main menu, titled `JavaJolt  [PREVIEW - not saved] `; `all` asks for a starting difficulty and keeps a small always-on-top switcher window that swaps the preview window to any difficulty (including back to Beginner).

## 6. Save compatibility

- Expected: no change — confirmed. No save-format code was touched; `SaveData`/`SaveService` are byte-identical to Phase 0. Old saves still load through the unchanged `GameApplication` path (verified by code path inspection; `GameApplication` GUI smoke run loaded the existing real save without error).
- Real-save safety proof: hash `7AD0484A1E74A72EFC20A866A563E24AD6B7D98A87BCB7E43ED2ADCED07E3730` and last-write `2026-11-06T22:40:06.4831416+08:00` were identical before and after the live `GameApplication` GUI run, a live `StagePreview intermediate --xp=0` GUI run, a later `StagePreview advanced` GUI run, and the bad-args run. Preview saves only appear in `%TEMP%\javajolt-preview...\player.properties` (contents verified in the earlier smoke run: 7 Beginner + first 6 Intermediate completions, `difficulty=INTERMEDIATE`).

## 7. Balance values introduced or changed

| Constant | Value | File | Rule |
| --- | --- | --- | --- |
| `XP_PER_COMPLETED_LESSON` | 75 | `preview/StagePreview` | Preview seeding default XP (0 / 525 / 1,050 per brief); `--xp=N` overrides |
| `TITLE_TAG` | `  [PREVIEW - not saved] ` | `preview/StagePreview` | Window title marker, brief wording kept exactly |

No gameplay balance values changed. (Level numbers shown in the background: under the current quadratic curve 525/1,050 XP read as L5/L7; after Phase 2's D1 curve they become L10/L20 — as expected by the brief. The `StagePreview advanced` smoke run printed XP 1050, matching the seeded 14 earlier completions.)

## 8. Verification

Commands run from the project root (compile targets were temp directories, never `out/`):

1. Full-source compile **after the SwingPreview removal**: `javac -d ...\opencode\jj-build-phase1r <all src files>` -> **exit code 0** (no reference to the deleted class anywhere).
2. `grep SwingPreview` over `src/` -> no matches; over the brief -> only the four intentional v1.3 decision statements remain.
3. Bad-args check (headless-safe) for the testing launcher: `java -cp <build> edu.batstateu.javajolt.preview.StagePreview` -> `Usage: StagePreview <beginner|intermediate|advanced|all> [--xp=N]`, exit code 1. No GUI opened, no files touched.
4. Normal gameplay smoke: `java -cp <build> edu.batstateu.javajolt.GameApplication` launched, 10 seconds, killed. stdout/stderr empty — the window built without exceptions on the real-save path. (Full clicking-through is left to the owner; see section 9.)
5. Testing-launcher smoke: `StagePreview advanced` launched, 8 seconds, killed. stdout: `StagePreview: Advanced (XP 1050), saving into the preview sandbox only`; stderr empty. An earlier smoke run (`intermediate --xp=0`) also passed and printed the expected XP 0 override.
6. Real-save untouched: hash identical before/after every run (section 6).
7. `MechanicsCheck`/`BalanceReport` not run: they do not exist until Phase 2.

## 9. Manual test steps for the owner

1. Normal gameplay: run the game as usual (double-click/run `GameApplication` from the project root) — behavior must be exactly as before this cleanup; the welcome/main-menu flow and saving still work against `saves/`.
2. Run `StagePreview intermediate` — expect the main menu titled `JavaJolt  [PREVIEW - not saved] `, all 7 topics marked available (first six completed, `Object-Oriented Programming` shows AVAILABLE).
3. Play any topic; the results screen appears normally and saving happens only in the sandbox (verify your real save's file timestamp/hash is unchanged).
4. Exit the window (`Exit Game`); confirm nothing changed in real `saves/`.
5. Repeat steps 2-4 with `advanced` and `beginner`.
6. Run `StagePreview all` — expect the difficulty chooser; pick Beginner; then use the "difficulty switcher" window to jump to Advanced and back to Beginner mid-session; each switch opens a fresh preview window.
7. Run `StagePreview all --xp=1000` — after Phase 5 this crosses all power-up thresholds; between now and Phase 5, only the level display differs.
8. D12 regression (real game): temporarily rename `saves/`, start `GameApplication`, close it without entering a name — no `saves/player.properties` must appear. Restore the folder.

## 10. Docs updated

- `progress-documentations/AGENT_BRIEF.md` (owner-requested, v1.3): header version note; section 2 source list and entry-point statement (removed `ui/swing/SwingPreview` and the "inspect the existing SwingPreview entry point" sentence); D10 (only `StagePreview` remains); section 6 constructor-caller list (`GameApplication`, `MainWindow` only); Phase 0 narrative (welcome-save guard remains, `SwingPreview` recorded as removed); Phase 1 save-isolation bullet (legacy launcher verification sentence replaced by the removal note). No phase substance, numbers, or LOCKED decisions changed; nothing about `StagePreview` was removed or weakened.
- `PROJECT_RULES.md`: new `preview/` bullet in "Where things live"; known-deviations line trimmed of the `SwingPreview` clause.
- `gameplay-design-notes.md`: no edit — the notes contain no rows describing a stage preview launcher, and no row in sections 2, 8, or 9 matched Phase 1's scope (noting the absence here so it is visible rather than silently skipped).
- `progress-documentations/change-reports/phase-0-audit.md`: untouched (historical record; brief forbids rewriting Phase 0 observations).

## 11. Deviations from this brief, assumptions made, and risks

1. **`SwingPreview` history, preserved in one line for traceability:** after Phase 0 it was briefly isolated via the sandbox (initial Phase 1 implementation); the owner then decided it is obsolete and mandated removal (Revision 1). The D12 first-run guard (`MainWindow.welcomeCompleted`, commit `591ac8b`) remains and keeps real first-run saves impossible in the normal entry point.
2. **The StagePreview sandbox is pre-seeded with one save file** so `MainWindow.hasSave()` is true and the preview behaves like a normal loaded game: no "results could not be saved" warnings, exit dialog works, and every write lands in the temp sandbox. Assumption documented: a preview save is throwaway data, which D10 permits ("a throwaway save location; never read or write `saves/`").
3. **Seeding is "first six topics" instead of "all seven."** Completing all 7 topics of the chosen difficulty would make `DifficultyProgressionService.currentDifficulty` advance (quiz-end drift in `MainWindow`), contradicting Phase 1's "difficulty comes from the preview selector/argument." Completing the first six unlocks all seven topics while keeping the difficulty stable; earlier difficulties are fully completed, preserving the brief's 525/1,050 default XP values exactly. Stated as the minimal compliant reading of "seed the earlier difficulty completions as necessary."
4. **`all`-mode switching opens a fresh preview window per difficulty** (dispose + rebuild inside the launcher; no `MainWindow` edits — the brief does not authorize `MainWindow` changes in Phase 1, and free switching without a window rebuild would require one). Within each window, the quiz-end progression drift still follows real rules; the dev switcher restores direct control. Documented as a tooltip on the switcher.
5. **One launcher process shares a sandbox** across difficulty switches; its seed file may go stale inside the sandbox but `MainWindow` never reloads from disk (the player object is supplied directly), so nothing stale is displayed.
6. **D12 first-run guard verified but not modified:** reviewed commit `591ac8b` (`welcomeCompleted` gating `saveProgress`/`confirmExit`); owner manual step in section 9.8.
7. **Risks:** (a) the always-on-top switcher window is a plain, unstyled dev dialog (accepted for dev tools); (b) force-killing a preview process leaves one small file in `%TEMP%`; normal closes delete it; (c) preview XP for Phase 5 tests (`--xp=1000`) is meaningful only after Phases 2-5 land; (d) `MainWindow`'s 4-arg constructor now has no callers after the removal — kept for smallest-change (see section 12) and will be revisited if Phase 2+ needs it.

## 12. Noticed but intentionally not changed

- `MainWindow`'s 4-arg constructor (real-save default) is currently unused after the `SwingPreview` removal; kept (no rename/removal churn outside the owner's instruction) and listed for a Phase 2+ decision.
- `GameApplication`'s console branch (`--console`) is unchanged; the console path remains available for development.
- The level display under the current (pre-D1) quadratic curve shows low level numbers for 525/1,050 XP in preview; Phase 2's curve change fixes this — not touched here.
- No changes to achievement popups, difficulty-unlock popups, or any screen; Phase 6 will wire those areas.
- `tests/ProgressConsistencyCheck.java` (default package, outside `src/`) remains unchanged; its dependency risks are tracked in the Phase 0 report.

**Phase 1 is complete (including the owner-directed cleanup). Stopping here — Phase 2 starts only when the owner says "Proceed to Phase 2".**
