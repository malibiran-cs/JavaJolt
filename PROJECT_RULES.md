# JavaJolt — Project Rules

Context file for AI agents and teammates. Read it fully before writing or changing code.
If a request conflicts with a rule here, say so in chat and ask before breaking it.

---

## 1. Project Snapshot

- **What it is:** JavaJolt, a gamified Java learning game (OOP 101 school project, 3 students). Players study lesson pages and answer quiz questions per Topic and Difficulty, earning XP and level while managing HP.
- **Stack:** Plain Java SE. Modern features are already in use (records, switch expressions, text blocks, `Stream.toList()`). No build tool, no external libraries, no frameworks. Do not add any. (This is why saves use `java.util.Properties` instead of JSON.)
- **Two front-ends:** The console (`ui/GameUI`) currently drives the game loop through `GameService`. The Swing UI (`ui/swing`) is being built in parallel and is opened by a callback once the player is ready. The Swing screens are still mostly visual; the functionality is being wired in.
- **Game flow:** Topics unlock one after another (enum order) within a difficulty. Completing every topic of a difficulty moves the player to the next one.
- **State:** Persisted: name, XP, difficulty, completed topics, cumulative score, high scores. One quiz attempt: HP (max HP derived per quiz from level) and the session's provisional score/XP, which commit only once at end-of-quiz settlement.
- **Root package:** `edu.batstateu.javajolt`

### Where things live

- `GameApplication`: composition root. The only place that builds and wires the main objects and catches top-level errors.
- `game/`: `GameService` (orchestrates the flow), `GameSession` (state of one lesson run), `Enemy` (one quiz opponent; damage grows with max HP and consecutive mistakes).
- `service/`: one-purpose logic classes.
  - `QuestionService`: select and check questions.
  - `LessonRunnerService`: play and study rules (XP, enemy damage, pass mark) and the end-of-quiz settlement.
  - `LessonService`: load textbook pages.
  - `SaveService`: the only class that reads or writes the save file.
  - `PlayerProgressService`: completed topics and unlocking.
  - `LevelService`: XP to level.
  - `LevelRewardService`: level-based quizzes rewards (max HP per level).
  - `CodeQuestionService`: compile and run the player's code.
- `entity/`: data holders that guard their own state (`Player`, `SaveData`, `LessonPage`).
- `Question` and `types/`: abstract `Question` and its subclasses (`MultipleChoiceQuestion`, `IdentificationQuestion`, `CodeQuestion`).
- `data/`: question content (`<Topic>Questions`), registered in `QuestionBank`; `EnemyRoster` maps each topic/difficulty pair to its enemy. Lesson text is in the project-root `data/lessons/`.
- `enums/`: `Topic`, `Difficulty`, `InputMode`.
- `exception/`: domain exceptions.
- `preview/`: development-only launchers and checks (`StagePreview`, later `BalanceReport` and `MechanicsCheck`). They build their own wiring like a composition root and must never read or write the real `saves/` folder (throwaway temp save location instead).
- `ui/`: console UI. `ui/swing/`: `MainWindow`, `ScreenId`, `Theme`, `UiAssets`, `NinePatch`, `screen/`, `component/`.

---

## 2. Core Design Rules

### Responsibility

- **Single Responsibility:** One class, one reason to change. One method does exactly what its name says. If describing it needs "and", split it. After writing a method, check that its body still matches its name.
- **Separation of Responsibility:** Put code in the layer or package that owns that concern (see the map above). Never drop new logic into the nearest convenient file. A new concern means a new class in the right package.
- **Orchestrators only orchestrate:** `GameService.run()` and similar coordinators call other classes. They do not calculate rules, parse, or format text. Anything like that gets extracted into a method or another class.
- **UI holds no game rules:** The UI displays state, collects input and reports events. XP, damage, pass mark, unlocking, scoring, levels and saving live in services and entities.
- **Layer direction:** The UI calls into the game and services, never the reverse. Nothing outside `ui/` imports Swing. `entity/`, `enums/`, `exception/` and `data/` must not depend on `ui/`.

### Dependency Injection

- **Receive, don't create:** A class gets its collaborators through the constructor and stores them in `private final` fields. Red flag: `new SomeService()` or `new SomeUI()` inside a class that is not the composition root.
- **Wiring happens at the edge:** `GameApplication` wires the core objects. `MainWindow` builds Swing screens.
- **Allowed `new` elsewhere:** Plain data objects (`Player`, `GameSession`, collections), exceptions, and Swing components inside UI classes.
- **Depend on abstractions at real boundaries:** When something touches the outside world or may be swapped (UI, files, console), depend on a small interface. For a single action, use `Runnable` or `Consumer<T>`. Do not create an interface for every class; add one only when there is a real second implementation or a testing need.
- **Settings are injected too:** Paths and similar config come through a constructor overload, and the default constructor delegates to it (see `SaveService(Path)` and `LessonService(Path)`).
- **Static is for pure calculation only:** Stateless math (`LevelService.calculateLevel`) and constants (`Theme`). Anything with state, I/O or that you might want to replace is an injected instance.

### Behavior and Data

- **Polymorphism over type checks:** Behavior that differs per question type lives in the subclass (`isCorrect`, `getInputMode`). Never use `instanceof` or type switches on `Question` in services or UI. A new question type means a new subclass in `types/`, with no edits to services.
- **Encapsulation:** Entities protect their own rules (`Player.takeDamage` never goes below 0). Prefer intention-revealing methods (`addXp`, `complete`) over raw setters from outside. Hand out copies of internal collections (see `copyCompleted`), never the live ones.
- **Content is data, not logic:** Questions go in `data/<Topic>Questions`, textbook pages in `data/lessons/*.txt`. Never hardcode content in services or UI.
- **Persistence behind one door:** Only `SaveService` touches the save file. It converts to and from `SaveData`; no other class knows the file format or keys.
- **No magic numbers:** Gameplay values are named `private static final` constants in the service that owns the rule (`PASS_PERCENT`, `DAMAGE_PER_MISTAKE` in `LessonRunnerService`). UI values (colors, fonts, spacing) go in `Theme`.

### Errors

- **Domain exceptions:** Use the types in `exception/` (`SaveFileException`, `LessonFileException`, `InvalidNameException`, ...). Add a new one when a new kind of failure appears.
- **Wrap low-level errors:** Catch `IOException` and parse errors where they happen, and rethrow a domain exception with a clear message plus the original cause. Never swallow an exception silently. Never show a stack trace to the player.
- **The layer that can respond decides:** Services throw. The caller handles it (`GameService.loadSave` starts a new game on a corrupt save; `GameApplication` reports fatal errors).
- **Validate at the boundary:** Check user input and file contents when they enter the system. Trust the data inside.

---

## 3. Code Style (readability comes first)

- **Teammates know basic Java only.** Write the simplest code that works: plain loops and ifs, short stream chains (`filter` and `toList` level). No reflection, no generic tricks, no clever one-liners, no pattern or framework names unless asked.
- **Small methods:** One idea each. Classes are nouns, methods are verbs, booleans read as `is...` or `has...`. Names say what, not how.
- **No speculative abstraction:** Don't add layers, interfaces or config "for later". Add them when a rule here requires it or a second use appears.
- **Comments:** Short, and only for the purpose or the *why*. One line above a method or a non-obvious step; `// NOTE:` for a non-obvious decision. Never comment every line or restate the code. Longer explanations go in chat, not in the file.
- **Match the existing format:** 4-space indent, braces on the same line, one public class per file (file name = class name), explicit imports, and the section labels used in model classes (`// CONSTRUCTOR`, `// GETTERS AND SETTERS`).

---

## 4. Swing UI Rules

- **Screens are pure views:** Each screen extends `Screen`. Reusable widgets go in `component/`.
- **Data and callbacks come in through the constructor:** `Player`, `Runnable`, `Consumer<T>`. A screen never navigates on its own and never changes game state directly. The code that creates the screen supplies callbacks that call services.
- **Navigation belongs to `MainWindow`:** It uses `CardLayout` plus `ScreenId`. A new screen means a new `ScreenId` constant and one registration in `MainWindow`.
- **Look lives in `Theme`:** Colors, fonts and `GAP_*` spacing. No hardcoded colors, font sizes or pixel gaps inside screens.
- **Use the kit images:** Visuals come from the Softline UI Kit Flat Night PNGs through `UiAssets` and `NinePatch` (9-slice). Do not redraw the kit's look with `Graphics2D`. A new image is copied into `ui/swing/assets/` and listed in that folder's `README.txt`.
- **Build screens from small `createX()` methods,** one per region of the layout.
- **Threading:** Create and update Swing components on the event dispatch thread (`SwingUtilities.invokeLater`).
- **Look and feel:** Minimal, Duolingo-like, with generous padding between elements and from the window edge. Reserve space for features not built yet (character, level, badges).
- **Don't use `Question.display()` in Swing.** Render from the question's getters.
- **The console `GameUI` stays as it is** unless asked. Swing is built alongside it.

---

## 5. Adding Content

- **New question:** Add it to the topic's `data/<Topic>Questions` class with a unique ID in that topic's block: 1xx Variables, 2xx Operators, 3xx Decisions, 4xx Loops, 5xx Arrays, 6xx Exception Handling, 7xx OOP. `QuestionBank` throws on duplicate IDs. Multiple choice: choices are written `"A. ..."` and the answer is the letter only.
- **New topic:** Add to the `Topic` enum (**order is unlock order**), create `<Topic>Questions`, add one line to `QuestionBank`, and add `data/lessons/<topic lowercase>.txt`. If the file name differs from the enum name, update `LessonService.fileNameFor`.
- **Lesson page format:** `## PAGE <n> [BEGINNER|INTERMEDIATE|ADVANCED|ALL]: Title`
- **New question type:** A subclass of `Question` in `types/`.
- **New saved field:** Update `SaveData` and the keys, `encode` and `decode` in `SaveService`. Old saves with a missing key must still load.
- **Save file:** `saves/player.properties` is git-ignored and never committed.

---

## 6. Working Rules for AI Agents

- **Read first:** Read the surrounding code and follow the existing pattern before writing anything.
- **Name the owner:** Before coding, say which class owns the change. If a task requires bending a rule, say so first.
- **Smallest change that works:** Put new work in new files or classes rather than editing working code. Don't rename, reformat or refactor unrelated code.
- **Don't add:** Libraries, build tools or frameworks.
- **Don't touch:** `out/` (compiled output), `saves/`, and the kit's asset files.
- **Keep it compiling:** The project is a VS Code Java project (source `src/`, output `out/`). Run from the project root, because `data/lessons` and `saves` are relative paths. There are no automated tests yet, so keep logic in injectable services where it can be tested later.
- **Explain in chat:** After a change, list the files created or changed and explain each part and why, in an organized way. Don't over-comment the code instead.
- **Don't fix or copy deviations:** Section 7 lists known deviations. Don't imitate them in new code, and don't fix them unless asked.

---

## 7. Known Deviations (do not copy, fix only when asked)

- `Player` creates its own `PlayerProgressService` with `new` (breaks DI). That class is really player state but sits in `service/`.
- `CodeQuestion` creates a static `CodeQuestionService` with `new` (breaks DI).
- `GameService` and `LessonRunnerService` depend on the concrete `GameUI` (no interface), so the game flow is tied to the console. Direction: extract a UI interface when Swing takes over the flow.
- `Question.display()` builds console text inside the model (marked temporary).
- `GameService.run()` is longer than pure orchestration should be. The replay and next-action logic is marked for extraction.
- `MainMenuScreen` calls static `LevelService` directly. This is acceptable for pure math, but prefer passing the level in.
- `MainWindow` hardcodes `Difficulty.BEGINNER` for topic selection and uses a placeholder dialog.
