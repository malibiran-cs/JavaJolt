# JavaJolt – Integrated pixel enemy pack

This archive is based on the **latest project provided by the user** (`JavaJolt-main (9)(1).zip`).
It preserves the existing Title Screen, Welcome Screen mascot, GUI features, save system, and lesson mechanics.

## Included artwork

| Topic | Enemy name | Asset |
|---|---|---|
| Variables | Andr0id X | `assets/enemies/enemy_variables.png` |
| Operators | OpRiot | `assets/enemies/enemy_operators.png` |
| Decisions | Fork Oracle | `assets/enemies/enemy_decisions.png` |
| Loops | Loopuin | `assets/enemies/enemy_loops.png` |
| Arrays | Clip Stack | `assets/enemies/enemy_arrays.png` |
| Exception Handling | Crash Monkey | `assets/enemies/enemy_exception.png` |
| Object-Oriented Programming | Window Warden | `assets/enemies/enemy_oop.png` |

The `assets/enemies/` folder above is located under
`src/edu/batstateu/javajolt/ui/swing/`.
All seven are PNG sprites with transparent backgrounds, shown inside the game's existing inset-panel border.

## Integration details

- `EnemyPortraitView.java` displays the correct pixel art for the active Topic.
- `QuestionScreen.java` uses the new portrait on the enemy side only; the player side retains its earlier placeholder.
- `MainWindow.java` passes the current session topic to the portrait.
- `EnemyRoster.java` returns a unique, consistent enemy name per topic, unchanged across difficulties.
- Damage escalation remains unchanged between Beginner, Intermediate and Advanced.
- `GameApplication.java` now shows the title first, with **START GAME** routing to Welcome for a new player or Main Menu for a saved player.
- Your current `online.png` character artwork remains intact and unchanged. It is still a poster-based sprite.

## Running and testing on Windows

1. Extract the ZIP into **a new empty folder** such as `JavaJolt` (not into an existing repository).
2. Open that folder in VS Code. `src/`, `RUN_JAVAJOLT.bat`, and `README.md` should be directly visible.
3. Double-click `RUN_JAVAJOLT.bat` or run it from Command Prompt.
4. Run `RUN_TESTS.bat` to verify the gameplay, navigation and enemy artwork.
5. To make it your GitHub repository, use `git init`/`git remote` or copy its contents into the working tree of your existing cloned repo, preserving that repo's `.git` directory.

The ZIP does **not** include `.git` or your personal save data. Do not delete your old save before backing it up.
