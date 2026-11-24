import edu.batstateu.javajolt.GameApplication;
import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.data.TutorialContent;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.preview.StagePreview;
import edu.batstateu.javajolt.service.AnswerTimerService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.service.TutorialService;
import edu.batstateu.javajolt.ui.GameUI;
import edu.batstateu.javajolt.ui.swing.MainWindow;
import edu.batstateu.javajolt.ui.swing.ScreenId;
import edu.batstateu.javajolt.ui.swing.TutorialCoordinator;
import edu.batstateu.javajolt.ui.swing.component.TutorialPopup;
import edu.batstateu.javajolt.ui.swing.screen.QuestionScreen;
import edu.batstateu.javajolt.ui.swing.screen.TopicSelectionScreen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/** Launches real Swing views with a temporary save location. No real saves are touched. */
public class SwingNavigationSmokeCheck {
    private static MainWindow window;
    private static int passed;
    private static Player routedPlayer;
    private static SaveService routedSave;
    private static TutorialService routedTutorial;
    private static GameSession routedSession;
    private static AbstractButton topicsAction;
    private static boolean captureMenu;

    public static void main(String[] args) throws Exception {
        captureMenu = args.length > 0 && args[0].equals("--capture-menu");
        try {
            checkFirstRunPaths();
            checkCompletionSaveFailure();
            checkExistingNavigation();
            checkNewPlayerLauncher();
            System.out.println("ALL SWING NAVIGATION CHECKS PASSED (" + passed + ")");
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                for (Window open : Window.getWindows()) open.dispose();
            });
        }
    }

    private static void checkExistingNavigation() throws Exception {
        Path tempFolder = Files.createTempDirectory("javajolt-navigation-");
        try {
            SwingUtilities.invokeAndWait(() -> {
                Player player = new Player("Test Player", 0);
                player.setTutorialCompleted(true);
                QuestionService questions = new QuestionService(new QuestionBank());
                LessonRunnerService runner = new LessonRunnerService(
                        questions, new LessonService(), new EnemyRoster(),
                        new ScoreService(), new GameUI());
                window = new MainWindow(player, questions, runner, Difficulty.BEGINNER,
                        new PowerUpService(), new AnswerTimerService(),
                        new SaveService(tempFolder.resolve("player.properties")));
                window.showScreen(ScreenId.WELCOME);
                window.setVisible(true);

                assertTrue(findButton(window, "GET STARTED") != null,
                        "Welcome screen opens with assets");
                JTextComponent nameInput = findEditable(window);
                assertTrue(nameInput != null, "Welcome screen name input exists");
                nameInput.setText("Smoke Test Player");
                findButton(window, "GET STARTED").doClick();

                assertTrue(findButton(window, "Explore Lessons") != null,
                        "Main Menu opens after welcome");
                findButton(window, "Explore Lessons").doClick();

                TopicSelectionScreen topics = findScreen(window, TopicSelectionScreen.class);
                assertTrue(topics != null && topics.isShowing(), "Topic Selection opens");
                Component center = ((BorderLayout) topics.getLayout()).getLayoutComponent(
                        BorderLayout.CENTER);
                assertTrue(center instanceof JScrollPane && center.isShowing(),
                        "Topic cards remain inside the scroll pane");
                AbstractButton variables = findButton(window, Topic.VARIABLES.getLabel());
                assertTrue(variables != null && variables.isEnabled(),
                        "Available Variables topic can be opened");
                variables.doClick();
                assertTrue(findButton(window, "Start Quiz") != null,
                        "Textbook opens with its quiz action");
                findButton(window, "Start Quiz").doClick();
                assertTrue(findButton(window, "Submit answer") != null,
                        "Question screen opens with quiz controls");
                window.showScreen(ScreenId.MAIN_MENU);
                assertTrue(findButton(window, "Achievements") != null,
                        "Returning to the Main Menu works");
                findButton(window, "Achievements").doClick();
                JComboBox<?> filter = findCombo(window);
                assertTrue(filter != null && filter.getItemCount() == 3,
                        "Achievement gallery opens with three filters");
                filter.setSelectedItem("Locked");
                assertTrue("Locked".equals(filter.getSelectedItem()),
                        "Achievement filter switches to Locked");
                findButton(window, "Back to Main Menu").doClick();
                assertTrue(findButton(window, "Explore Lessons") != null,
                        "Achievement gallery returns to the Main Menu");
                findButton(window, "Settings").doClick();
                assertTrue(findButton(window, "BACK TO DASHBOARD") != null,
                        "Settings screen opens from the Main Menu");
                findButton(window, "BACK TO DASHBOARD").doClick();
                assertTrue(findButton(window, "Explore Lessons") != null,
                        "Settings returns to the Main Menu");
                window.dispose();
            });
        } finally {
            if (window != null) SwingUtilities.invokeAndWait(() -> window.dispose());
            Files.deleteIfExists(tempFolder.resolve("player.properties"));
            Files.deleteIfExists(tempFolder);
        }
    }

    private static void openRoute(Player player, SaveService saves, boolean loaded) throws Exception {
        routedPlayer = player;
        routedSave = saves;
        SwingUtilities.invokeAndWait(() -> {
            QuestionService questions = new QuestionService(new QuestionBank());
            ScoreService scores = new ScoreService();
            LessonRunnerService runner = new LessonRunnerService(questions, new LessonService(),
                    new EnemyRoster(), scores, null) {
                @Override
                public GameSession startSession(Player owner, Topic topic, Difficulty difficulty, List<Question> content) {
                    routedSession = super.startSession(owner, topic, difficulty, content);
                    return routedSession;
                }
            };
            PowerUpService powers = new PowerUpService();
            AnswerTimerService timer = new AnswerTimerService();
            routedTutorial = new TutorialService(new TutorialContent(runner, scores, timer, powers), runner, timer, powers);
            window = new MainWindow(player, questions, runner, Difficulty.BEGINNER, powers, timer, saves,
                    new TutorialCoordinator(routedTutorial));
            window.setTitleStartDestination(GameApplication.startDestination(player, loaded));
            window.showScreen(ScreenId.TITLE);
            window.setVisible(true);
        });
    }

    private static void checkFirstRunPaths() throws Exception {
        Path folder = Files.createTempDirectory("javajolt-first-run-");
        Path save = folder.resolve("player.properties");
        SaveService saves = new SaveService(save);
        try {
            openRoute(new Player("Player", 0), saves, false);
            SwingUtilities.invokeAndWait(() -> {
                findButton(window, "START GAME").doClick();
                assertTrue(findButton(window, "GET STARTED") != null, "new player routes Title to Welcome");
                findEditable(window).setText("New learner");
            });
            clickLater("GET STARTED");
            await(() -> popup() != null);
            assertTrue(!saves.hasSave() && !routedPlayer.isTutorialCompleted(), "name entry starts tutorial without premature save");
            SwingUtilities.invokeAndWait(() -> {
                window.showScreen(ScreenId.MAIN_MENU);
                assertTrue(popup() != null && findScreen(window, QuestionScreen.class) != null,
                        "mandatory tutorial rejects navigation to the menu");
            });
            finishTutorial(true);
            SaveData first = saves.load();
            assertTrue(first.tutorialCompleted() && first.name().equals("New learner"), "confirmed wrap-up immediately saves the name and completion");
            assertTrue(first.xp() == 0 && first.score() == 0 && first.completedTopics().isEmpty()
                    && first.highScores().isEmpty(), "first tutorial saves no sample XP, score, high scores or completions");
            SwingUtilities.invokeAndWait(() -> window.dispose());

            Files.deleteIfExists(save);
            openRoute(new Player("Player", 0), saves, false);
            SwingUtilities.invokeAndWait(() -> {
                findButton(window, "START GAME").doClick();
                findEditable(window).setText("Interrupted learner");
            });
            clickLater("GET STARTED");
            await(() -> popup() != null);
            closeThroughConfirmation();
            SaveData interrupted = saves.load();
            assertTrue(!interrupted.tutorialCompleted(), "quitting mid-first-run saves the false flag");
            Player restored = restore(interrupted);
            assertTrue(GameApplication.startDestination(restored, true) == ScreenId.QUESTION,
                    "loaded false flag selects tutorial rather than the menu");
            openRoute(restored, saves, true);
            clickLater("START GAME");
            await(() -> popup() != null);
            SwingUtilities.invokeAndWait(() -> assertTrue(findButton(window, "GET STARTED") == null
                    && routedPlayer.getName().equals("Interrupted learner"), "loaded incomplete save skips Welcome and resumes tutorial"));
            finishTutorial(true);
            assertTrue(saves.load().tutorialCompleted(), "resumed first-run completion saves true");
            SwingUtilities.invokeAndWait(() -> window.dispose());

            Files.writeString(save, "name=Legacy learner\nxp=250\nscore=77\ndifficulty=BEGINNER\n"
                    + "completedTopics=VARIABLES:BEGINNER\nhighScores=VARIABLES:BEGINNER:52\n");
            Player legacy = restore(saves.load());
            assertTrue(legacy.isTutorialCompleted()
                    && GameApplication.startDestination(legacy, true) == ScreenId.MAIN_MENU, "legacy save defaults to the normal menu destination");
            openRoute(legacy, saves, true);
            SwingUtilities.invokeAndWait(() -> {
                findButton(window, "START GAME").doClick();
                assertTrue(findButton(window, "Tutorial") != null && popup() == null, "legacy player reaches menu with Tutorial available");
            });
            if (captureMenu) {
                BufferedImage[] image = new BufferedImage[1];
                SwingUtilities.invokeAndWait(() -> {
                    image[0] = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_RGB);
                    java.awt.Graphics2D graphics = image[0].createGraphics();
                    try { window.paint(graphics); } finally { graphics.dispose(); }
                });
                Path output = Path.of(System.getProperty("java.io.tmpdir"), "javajolt-phase5-menu.png");
                ImageIO.write(image[0], "png", output.toFile());
                System.out.println("Menu capture: " + output);
            }
            SwingUtilities.invokeAndWait(SwingNavigationSmokeCheck::checkMenuLayout);
            byte[] before = hash(save);
            clickLater("Tutorial");
            await(() -> popup() != null);
            finishTutorial(false);
            assertTrue(Arrays.equals(before, hash(save)), "complete menu replay preserves the save SHA-256");
            assertTrue(legacy.getXp() == 250 && legacy.getScore() == 77 && legacy.isComplete(Topic.VARIABLES, Difficulty.BEGINNER)
                    && legacy.getHighScore(Topic.VARIABLES, Difficulty.BEGINNER) == 52,
                    "replay preserves existing XP, score, completed topics and high scores");
            clickLater("Tutorial");
            await(() -> popup() != null);
            SwingUtilities.invokeAndWait(() -> findButton(popup(), "Exit tutorial").doClick());
            await(() -> popup() == null && findButton(window, "Explore Lessons") != null);
            assertTrue(Arrays.equals(before, hash(save)), "replay Exit tutorial also preserves the save SHA-256");
            SwingUtilities.invokeAndWait(() -> {
                assertTrue(topicsAction.isEnabled(), "topic navigation is restored after clearing the tutorial");
                findButton(window, "Explore Lessons").doClick();
                findButton(window, Topic.VARIABLES.getLabel()).doClick();
                findButton(window, "Start Quiz").doClick();
                assertTrue(findButton(window, "Submit answer") != null && popup() == null
                        && routedSession != routedTutorial.getSession() && routedSession.getCurrentQuestionIndex() == 1,
                        "normal quiz starts clean on the real player immediately after a replay");
                window.dispose();
            });
        } finally {
            SwingUtilities.invokeAndWait(() -> { if (window != null) window.dispose(); });
            Files.deleteIfExists(save);
            Files.deleteIfExists(folder);
        }
    }

    private static void finishTutorial(boolean firstRun) throws Exception {
        finishTutorial(firstRun, false);
    }

    private static void finishTutorial(boolean firstRun, boolean expectSaveError) throws Exception {
        dismissSteps(8);
        await(() -> popup() == null);
        SwingUtilities.invokeAndWait(() -> {
            findButton(window, "A. int count = 3;").doClick();
            findButton(window, "Submit answer").doClick();
        });
        await(() -> findButton(window, "Continue") != null);
        clickLater("Continue");
        await(() -> popup() != null);
        dismissSteps(4);
        await(() -> popup() == null);
        SwingUtilities.invokeAndWait(() -> {
            findButton(window, "B. 4").doClick();
            findButton(window, "Submit answer").doClick();
        });
        await(() -> findButton(window, "Continue") != null);
        clickLater("Continue");
        await(() -> popup() != null && hasLabel(popup(), "Build a Jolt"));
        dismissSteps(1);
        await(() -> popup() == null);
        for (String answer : List.of("C. boolean", "D. 5", "A. count = 4;")) {
            SwingUtilities.invokeAndWait(() -> {
                findButton(window, answer).doClick();
                findButton(window, "Submit answer").doClick();
            });
            await(() -> findButton(window, "Continue") != null);
            clickLater("Continue");
        }
        await(() -> popup() != null && hasLabel(popup(), "You're ready!"));
        if (firstRun) {
            assertTrue(!routedPlayer.isTutorialCompleted() && (!routedSave.hasSave() || !routedSave.load().tutorialCompleted()),
                    "showing the wrap-up alone does not complete or save the tutorial");
        }
        SwingUtilities.invokeAndWait(() -> {
            QuestionScreen screen = findScreen(window, QuestionScreen.class);
            // Materialize the existing topics control to exercise the disabled view contract.
            screen.showLessonResult(routedTutorial.getSession(), true);
            topicsAction = findButton(window, "Choose another topic");
            assertTrue(topicsAction != null && !topicsAction.isEnabled(), "Choose another topic is disabled throughout the tutorial");
            topicsAction.doClick();
            assertTrue(popup() != null && findScreen(window, QuestionScreen.class) != null,
                    "disabled topics action cannot escape the tutorial");
            findButton(popup(), "Got it").doClick();
        });
        if (expectSaveError) {
            await(() -> saveErrorDialog() != null);
            assertTrue(routedPlayer.isTutorialCompleted() && !routedSave.hasSave(),
                    "failed completion save reports the error and keeps completion in memory");
            SwingUtilities.invokeAndWait(() -> findButton(saveErrorDialog(), "OK").doClick());
        }
        await(() -> popup() == null && findButton(window, "Tutorial") != null);
        SwingUtilities.invokeAndWait(() -> assertTrue(topicsAction.isEnabled(), "topic control is enabled again after completion"));
    }

    private static void checkCompletionSaveFailure() throws Exception {
        Path folder = Files.createTempDirectory("javajolt-tutorial-save-failure-");
        try {
            SaveService failing = new SaveService(folder.resolve("player.properties")) {
                @Override
                public void save(Player player, Difficulty difficulty) throws SaveFileException {
                    throw new SaveFileException("Intentional test save failure.");
                }
            };
            openRoute(new Player("Player", 0), failing, false);
            SwingUtilities.invokeAndWait(() -> {
                findButton(window, "START GAME").doClick();
                findEditable(window).setText("Save failure learner");
            });
            clickLater("GET STARTED");
            await(() -> popup() != null);
            finishTutorial(true, true);
            assertTrue(routedPlayer.getXp() == 0 && routedPlayer.getScore() == 0,
                    "failed completion save does not commit any practice rewards");
        } finally {
            SwingUtilities.invokeAndWait(() -> window.dispose());
            Files.deleteIfExists(folder);
        }
    }

    private static JDialog saveErrorDialog() {
        for (Window open : Window.getWindows()) {
            if (open instanceof JDialog dialog && dialog.isShowing() && dialog.getTitle().equals("Save Error")) return dialog;
        }
        return null;
    }

    private static void checkMenuLayout() {
        Rectangle bounds = new Rectangle(0, 0, window.getContentPane().getWidth(), window.getContentPane().getHeight());
        AbstractButton tutorialButton = findButton(window, "Tutorial");
        for (String label : List.of("Explore Lessons", "View Progress", "Save Game", "Achievements", "Tutorial", "Exit Game")) {
            AbstractButton button = findButton(window, label);
            Rectangle position = SwingUtilities.convertRectangle(button.getParent(), button.getBounds(), window.getContentPane());
            assertTrue(bounds.contains(position) && button.getHeight() >= button.getPreferredSize().height,
                    "menu button fits at 1100x720 without clipping: " + label
                            + " [" + button.getSize() + ", preferred " + button.getPreferredSize()
                            + ", bounds " + position + "]");
        }
        AbstractButton achievements = findButton(window, "Achievements");
        AbstractButton exit = findButton(window, "Exit Game");
        assertTrue(tutorialButton.getWidth() == achievements.getWidth() && tutorialButton.getHeight() == achievements.getHeight()
                        && exit.getWidth() == tutorialButton.getParent().getWidth(),
                "secondary grid uses equal tiles with full-width Exit below");
    }

    private static Player restore(SaveData data) {
        Player player = new Player(data.name(), data.xp());
        player.setScore(data.score());
        player.restoreProgress(data.completedTopics());
        player.restoreHighScores(data.highScores());
        player.setTutorialCompleted(data.tutorialCompleted());
        return player;
    }

    private static byte[] hash(Path save) throws Exception {
        return MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(save));
    }

    private static void checkNewPlayerLauncher() throws Exception {
        StagePreview.main(new String[] {"newplayer"});
        await(() -> {
            for (Window open : Window.getWindows()) {
                if (open instanceof MainWindow preview && preview.isShowing()) { window = preview; return true; }
            }
            return false;
        });
        SwingUtilities.invokeAndWait(() -> {
            assertTrue(findButton(window, "START GAME") != null, "actual StagePreview newplayer opens Title");
            findButton(window, "START GAME").doClick();
            assertTrue(findButton(window, "GET STARTED") != null, "actual newplayer mode goes to Welcome");
            findEditable(window).setText("Safe preview learner");
        });
        clickLater("GET STARTED");
        await(() -> popup() != null);
        SwingUtilities.invokeAndWait(() -> assertTrue(hasLabel(popup(), "Step 1 of 14"), "actual newplayer mode starts the same mandatory tutorial"));
        closeThroughConfirmation();
    }

    private static void closeThroughConfirmation() throws Exception {
        SwingUtilities.invokeLater(() -> window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING)));
        await(() -> exitDialog() != null);
        SwingUtilities.invokeAndWait(() -> findButton(exitDialog(), "Yes").doClick());
        await(() -> !window.isDisplayable() && popup() == null);
    }

    private static void clickLater(String label) {
        SwingUtilities.invokeLater(() -> findButton(window, label).doClick());
    }

    private static void dismissSteps(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            await(() -> popup() != null);
            SwingUtilities.invokeAndWait(() -> findButton(popup(), "Got it").doClick());
        }
    }

    private static TutorialPopup popup() {
        for (Window open : Window.getWindows()) {
            if (open instanceof TutorialPopup popup && popup.isShowing()) return popup;
        }
        return null;
    }

    private static JDialog exitDialog() {
        for (Window open : Window.getWindows()) {
            if (open instanceof JDialog dialog && dialog.isShowing() && dialog.getTitle().equals("Exit")) return dialog;
        }
        return null;
    }

    private static void await(BooleanSupplier condition) throws Exception {
        long deadline = System.nanoTime() + 10_000_000_000L;
        boolean[] ready = {false};
        while (!ready[0] && System.nanoTime() < deadline) {
            SwingUtilities.invokeAndWait(() -> ready[0] = condition.getAsBoolean());
            Thread.sleep(25);
        }
        if (!ready[0]) throw new AssertionError("Timed out waiting for navigation/tutorial");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        passed++;
        System.out.println("PASS: " + label);
    }

    private static AbstractButton findButton(Component component, String name) {
        if (!component.isShowing()) return null;
        if (component instanceof AbstractButton button) {
            String buttonText = button.getText();
            if (name.equals(buttonText) || (buttonText != null
                    && buttonText.replaceAll("<[^>]*>", "").equals(name)) || hasLabel(button, name)) return button;
        }
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                AbstractButton result = findButton(child, name);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static boolean hasLabel(Component component, String name) {
        if (component instanceof JLabel label && name.equals(label.getText())) return true;
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                if (hasLabel(child, name)) return true;
            }
        }
        return false;
    }

    private static JTextComponent findEditable(Component component) {
        if (!component.isShowing()) return null;
        if (component instanceof JTextComponent text && text.isEditable()) return text;
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                JTextComponent result = findEditable(child);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static JComboBox<?> findCombo(Component component) {
        if (!component.isShowing()) return null;
        if (component instanceof JComboBox<?> combo) return combo;
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                JComboBox<?> result = findCombo(child);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static <T extends Component> T findScreen(Component component, Class<T> type) {
        if (type.isInstance(component) && component.isShowing()) return type.cast(component);
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                T result = findScreen(child, type);
                if (result != null) return result;
            }
        }
        return null;
    }
}
