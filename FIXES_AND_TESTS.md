# JavaJolt — repairs and verification (8 November 2026)

Based on the uploaded `JavaJolt-main (9).zip`. The goal was to fix problems
that could be reproduced without changing the team's scoring or artwork.

## Repaired

1. **Missing UI image at launch** — `UiAssets.java` previously failed to find
   `fields/input_normal.png` (and other bundled PNGs) after ordinary `javac`
   compilation because the image files stayed in `src/` rather than being
   copied into `out/`. It now also looks in the included source-assets folders,
   both when opened at the project root and from common parent directories.
   Packaged classpath resources and the existing `-Djavajolt.assets` override
   still take precedence.

2. **Broken topic grid scroll layout** — `TopicSelectionScreen.java` added
   `topicPanel` twice into `BorderLayout.CENTER`, which removed the scroll pane
   from the screen and reparented the cards. The duplicate add was removed.
   The cards remain inside the scroll pane and the responsive columns work.

3. **Friction when launching on Windows** — added `RUN_JAVAJOLT.bat` so you can
   double-click it from the extracted project folder to compile and start the
   GUI without manually managing `java -cp ...` and the working directory.
   `RUN_TESTS.bat` compiles and runs the checks.

4. **Regression coverage** — added `SwingNavigationSmokeCheck.java` and
   `ContentCoverageCheck.java` to test the actual GUI navigation, scroll pane,
   achievements filters, lesson loading, and all 21 topic/difficulty pairs.

## Verified in this workspace (OpenJDK 21)

- Main Java sources compile successfully (**0 compilation errors**).
- `ProgressConsistencyCheck`: **5 passes**.
- `ContentCoverageCheck`: **21 topic/difficulty pairs**, **504 questions**.
- `MechanicsCheck`: **410 passes, 0 failures**.
- `SwingNavigationSmokeCheck`: **12 passes** under a virtual desktop.
- `SwingTimerFlowCheck`: **22 passes** under a virtual desktop.

An all-warnings build (`javac -Xlint:all`) still emits mostly informational
warnings from Swing serialization and constructor style. These are not
compilation failures. The repair does not attempt unrelated refactoring.

## Run on Windows

1. Extract the ZIP and open the inner `JavaJolt-main` folder in VS Code.
2. Use **JDK 21 or newer** and ensure `javac` is on your PATH.
3. Double-click `RUN_JAVAJOLT.bat`, or run it from the VS Code terminal.
4. To recheck gameplay, double-click `RUN_TESTS.bat` (needs a desktop session).

Player saves are *not* included in this ZIP; keep your existing `saves/`
folder and copy it into the new project directory if you want your progress.

If an issue remains, supply the **first exception/stack trace or a screenshot**
plus the steps that reproduce it. Passing the available tests cannot prove that
all possible interactions, machines, or configurations are free from errors.
