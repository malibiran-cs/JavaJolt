# Phase 2 Report: Tutorial completion persistence

## 1. Summary

The tutorial completion flag is now player state carried by SaveData and SaveService. New players start false; legacy saves missing the property load true. Both application loading paths restore it, and previews mark their throwaway players complete. This phase adds no tutorial content, UI or navigation behavior.

## 2. Files

Java paths below are relative to `src/edu/batstateu/javajolt/` unless prefixed with `tests/`.

| Path | NEW/MODIFIED | What changed and why |
| --- | --- | --- |
| `entity/Player.java` | MODIFIED | Private tutorialCompleted boolean, getter and setter; Java's boolean default leaves new players incomplete. |
| `entity/SaveData.java` | MODIFIED | Add the boolean to the save snapshot record. |
| `service/SaveService.java` | MODIFIED | Add one property key, encode the flag, default missing keys to true, validate boolean values and update the sole record constructor call. |
| `GameApplication.java` | MODIFIED | Restore the flag from loaded SaveData for GUI gameplay. |
| `game/GameService.java` | MODIFIED | Preserve the flag through console load/save as well. |
| `preview/StagePreview.java` | MODIFIED | Mark every seeded preview player complete, including replacement windows. |
| `preview/PreviewSandbox.java` | MODIFIED | Set the flag before writing the temporary seed save. |
| `ui/swing/SwingPreview.java` | MODIFIED | Use PreviewSandbox and inject its SaveService instead of falling back to the real save path. |
| `tests/ProgressConsistencyCheck.java` | MODIFIED | Check defaults, both round-trips, restored player state, legacy variants, invalid flags and preview isolation. |
| `PROJECT_RULES.md` | MODIFIED | List tutorial completion as persisted state and record legacy/new-player defaults. |
| `progress-documentations/gameplay-design-notes.md` | MODIFIED | Update the persistence row and add section 2.7 with the implemented boundary and remaining tutorial work. |
| `progress-documentations/change-reports/phase-2-tutorial-persistence.md` | NEW | This report. |

## 3. Ownership and rule check

`Player` owns its private boolean; `SaveData` transports it; `SaveService` alone owns the property name and file encoding/decoding. Application and preview composition roots restore or seed state through the setter. No Swing screen reads the save format or calculates gameplay rules. Plain Java SE, constructor injection, existing line endings, and the phase boundary are preserved. No PROJECT_RULES exception is needed.

## 4. Existing code touched

Section 2 lists every existing-file change and its purpose. All Java constructor references were searched: SaveService was the only SaveData construction site. The console restoration line is necessary so a console session cannot overwrite a completed flag with a new Player's false default. PreviewSandbox sets the flag before its existing seed write; StagePreview also sets it on each separately created window player.

The still-present SwingPreview used MainWindow's constructor that creates a default SaveService. Routing it through PreviewSandbox corrects this task's preview save-isolation boundary while retaining its Title screen and existing wiring. MainWindow and screens are unchanged in Phase 2.

## 5. Player-visible behavior changes (before -> after)

No normal navigation or gameplay changes yet. Save files now carry tutorial completion, ready for the later first-run flow. Preview sessions use completed flags; the legacy SwingPreview's save actions now target a temporary sandbox.

## 6. Save compatibility

The additive property is `tutorialCompleted`, encoded as true/false like other property values. A missing key means true, regardless of player XP or other optional fields. Explicit false remains false across saving, loading and Player restoration; true remains true. Invalid non-boolean values use SaveFileException's existing corrupted-save handling. Existing score/progress/high-score defaults remain intact.

Tests use temporary fixtures and PreviewSandbox; the real project saves/ folder is not read or written. No migration writes happen during load. No HP, power-up usage or tutorial session data is added to saves.

## 7. Balance values introduced or changed

None. `SaveService.KEY_TUTORIAL_COMPLETED = "tutorialCompleted"` is a file-format key, not a balance value. New-player false and legacy-missing-key true are the locked compatibility decisions.

## 8. Verification

Checks run from the project root:

```powershell
$tutorialBuild = Join-Path $env:TEMP 'javajolt-tutorial-phase2-build'
New-Item -ItemType Directory -Force -Path $tutorialBuild | Out-Null
$tutorialSources = @(rg --files src tests -g '*.java')
[System.IO.File]::WriteAllLines((Join-Path $tutorialBuild 'sources.txt'), $tutorialSources)
javac -encoding UTF-8 -d $tutorialBuild ('@' + (Join-Path $tutorialBuild 'sources.txt'))
java -cp $tutorialBuild ProgressConsistencyCheck
java -cp $tutorialBuild edu.batstateu.javajolt.preview.MechanicsCheck
java -cp "$tutorialBuild;src" SwingNavigationSmokeCheck
rg -n 'new SaveData|tutorialCompleted|setTutorialCompleted' src tests
git diff --check
```

Compilation exited 0. Result excerpts:

```text
PASS: New player tutorial flag defaults to false
PASS: Tutorial flag round-trip true
PASS: Tutorial flag round-trip false
PASS: Legacy save tutorial flag defaults to true
PASS: Preview flags and isolated sandbox writes
PASS: Minimal legacy save defaults
PASS: Invalid tutorial flag rejected
ALL PROGRESSION CHECKS PASSED

Checks: 453 passed, 0 failed, 453 total.
ALL CHECKS PASSED

ALL SWING NAVIGATION CHECKS PASSED (12)
```

All three suites exited 0. The constructor/flag scan confirms one updated SaveData constructor site, both restoration paths, and both preview seed points. `git diff --check` passed; Git emitted only existing LF/CRLF normalization notices. Tests ran after report creation. Builds go to TEMP, not out/ or build/. RUN_TESTS.bat remains reserved for the final phase; the relevant suites here use the temporary build.

## 9. Manual test steps for the owner

1. Run ProgressConsistencyCheck from the temporary build; confirm false/true round-trips, legacy defaults and preview checks pass.
2. Launch `StagePreview beginner --xp=750` using the temporary build plus src classpath. It should still open the menu directly and use its temporary save path.
3. If using the old SwingPreview entry point, confirm its Title flow still works and saving reports success through the preview sandbox.
4. Welcome still proceeds directly to the menu in Phase 2; the mandatory tutorial flow belongs to Phase 5.

## 10. Docs updated

PROJECT_RULES's persisted-state line now includes tutorial completion and the two defaults. Gameplay notes section 2's persistence row and new section 2.7 document persistence, validation, both loading paths, and preview seeding/isolation. This report records the new task's Phase 2; AGENT_BRIEF and historical reports are unchanged.

## 11. Deviations from this brief, assumptions made, and risks

The current task explicitly authorizes the save-format addition despite the historical brief's earlier no-save-change scope. Boolean values are validated at the persistence boundary rather than silently treating arbitrary text as false. The console restore and legacy SwingPreview isolation changes cover real paths found by inspection; neither adds tutorial behavior. A boolean setter can restore either valid state, as required by round-trip acceptance.

Preview isolation is checked through the shared factory with a separate existing save fixture, and launcher construction is audited for explicit sandbox injection. No manual flag edits to the real save, or real-save hash reads, are required. Full tutorial content and first-run navigation remain unimplemented until their authorized phases.

## 12. Noticed but intentionally not changed

Existing MainWindow convenience-constructor wiring, known architecture deviations, textbook-reward persistence, and gameplay rules are unchanged. Existing Phase 1 work and user edits are preserved, and nothing is committed. After verification, stop and wait for the owner's Phase 3 go-ahead.
