package edu.batstateu.javajolt.preview;

import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.AnswerTimerService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.ui.GameUI;
import edu.batstateu.javajolt.ui.swing.MainWindow;
import edu.batstateu.javajolt.ui.swing.ScreenId;

/*
 * Development launcher (Phase 1): previews any difficulty and topic without
 * real progression.
 * Usage: StagePreview <beginner|intermediate|advanced|all> [--xp=N]
 * Run from the project root so data/lessons and saves stay relative paths.
 * All saving goes to a throwaway temp directory; the real saves/ folder is
 * never read or written. For "all", a chooser picks the starting difficulty
 * and a small switcher window can open a fresh preview on another difficulty
 * (including back to Beginner) at any time.
 */
public class StagePreview {

    // Tuning: preview seed XP per lesson completed in an earlier difficulty (brief section 9, Phase 1).
    private static final int XP_PER_COMPLETED_LESSON = 75;
    private static final String TITLE_TAG = "  [PREVIEW - not saved] ";
    private static final String PLAYER_NAME = "Preview";
    private static final String USAGE =
            "Usage: StagePreview <beginner|intermediate|advanced|all> [--xp=N]";

    // Shared wiring for every window opened during one preview run.
    private static QuestionService questionService;
    private static LessonRunnerService lessonRunnerService;
    private static PowerUpService powerUpService;
    private static AnswerTimerService answerTimerService;
    private static SaveService sandboxSaveService;
    private static int xpOverride = -1; // -1 = use the difficulty's default XP
    private static MainWindow currentWindow;

    public static void main(String[] args) {
        if (args.length < 1) {
            printUsageAndExit();
        }

        Difficulty fixedMode = null;
        boolean allMode = false;
        switch (args[0].toLowerCase()) {
            case "beginner" -> fixedMode = Difficulty.BEGINNER;
            case "intermediate" -> fixedMode = Difficulty.INTERMEDIATE;
            case "advanced" -> fixedMode = Difficulty.ADVANCED;
            case "all" -> allMode = true;
            default -> printUsageAndExit();
        }

        int override = -1;
        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("--xp=")) {
                Integer parsed = parseXp(args[i].substring("--xp=".length()));
                if (parsed == null) {
                    printUsageAndExit();
                }
                override = parsed;
            } else {
                printUsageAndExit();
            }
        }

        final Difficulty startMode = fixedMode;
        final boolean all = allMode;
        final int xp = override;
        SwingUtilities.invokeLater(() -> start(startMode, all, xp));
    }

    // Builds the shared services, picks the starting difficulty, and opens the first preview window.
    private static void start(Difficulty fixedMode, boolean allMode, int override) {
        xpOverride = override;

        Difficulty chosen = fixedMode;
        if (allMode) {
            chosen = askStartDifficulty();
            if (chosen == null) {
                return; // chooser closed without a selection
            }
        }

        questionService = new QuestionService(new QuestionBank());
        lessonRunnerService =
                new LessonRunnerService(
                        questionService,
                        new LessonService(),
                        new EnemyRoster(),
                        new ScoreService(),
                        new GameUI());
        powerUpService = new PowerUpService();
        answerTimerService = new AnswerTimerService();

        sandboxSaveService =
                PreviewSandbox.createFor(seedPlayer(chosen, chosenXp(chosen)), chosen);

        openWindow(chosen);
        if (allMode) {
            showDifficultySwitcher();
        }
    }

    // Opens a fresh, fully seeded preview window; replaces any previously opened one.
    private static void openWindow(Difficulty difficulty) {
        if (currentWindow != null) {
            currentWindow.dispose();
        }

        Player player = seedPlayer(difficulty, chosenXp(difficulty));
        MainWindow window = new MainWindow(
                player,
                questionService,
                lessonRunnerService,
                difficulty,
                powerUpService,
                answerTimerService,
                sandboxSaveService
        );
        window.setTitle("JavaJolt" + TITLE_TAG);
        window.showScreen(ScreenId.MAIN_MENU);
        window.setVisible(true);
        System.out.println("StagePreview: " + difficulty.getLabel()
                + " (XP " + player.getXp() + "), saving into the preview sandbox only");

        currentWindow = window;
    }

    // Builds the preview player: earlier difficulties fully completed, and the first
    // all-but-last topics of the chosen difficulty completed. Completing the last
    // topic would flip the difficulty calculator to the next stage, so it stays open.
    private static Player seedPlayer(Difficulty difficulty, int xp) {
        Player player = new Player(PLAYER_NAME, xp);

        for (Difficulty earlier : Difficulty.values()) {
            if (earlier == difficulty) {
                break;
            }
            for (Topic topic : Topic.values()) {
                player.complete(topic, earlier);
            }
        }

        Topic[] topics = Topic.values();
        for (int i = 0; i < topics.length - 1; i++) {
            player.complete(topics[i], difficulty);
        }
        return player;
    }

    // XP used for this window: --xp override wins, otherwise 75 per earlier completed lesson.
    private static int chosenXp(Difficulty difficulty) {
        if (xpOverride >= 0) {
            return xpOverride;
        }
        int completedLessons = 0;
        for (Difficulty earlier : Difficulty.values()) {
            if (earlier == difficulty) {
                break;
            }
            completedLessons += Topic.values().length;
        }
        return XP_PER_COMPLETED_LESSON * completedLessons;
    }

    // Development-only chooser so "all" does not depend on the progression calculator.
    private static Difficulty askStartDifficulty() {
        Difficulty[] difficulties = Difficulty.values();
        Object[] options = new Object[difficulties.length];
        for (int i = 0; i < difficulties.length; i++) {
            options[i] = difficulties[i].getLabel();
        }

        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose the difficulty to preview.\nYou can switch at any time while testing.",
                "Stage Preview",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice < 0 || choice >= difficulties.length) {
            return null;
        }
        return difficulties[choice];
    }

    // Dev-only switcher: opens a fresh preview window on the chosen difficulty,
    // allowing free switches between Beginner, Intermediate and Advanced.
    private static void showDifficultySwitcher() {
        JFrame switcher = new JFrame("JavaJolt dev: difficulty switcher");
        switcher.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        switcher.setAlwaysOnTop(true);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        content.setOpaque(false);
        JLabel label = new JLabel("Open a fresh preview window:", SwingConstants.CENTER);
        label.setToolTipText(
                "After a passed quiz the window follows the real progression rules; "
                + "switch here to jump back to any difficulty."
        );
        content.add(label);

        for (Difficulty difficulty : Difficulty.values()) {
            JButton button = new JButton(difficulty.getLabel());
            button.addActionListener(event -> openWindow(difficulty));
            content.add(button);
        }

        switcher.add(content);
        switcher.pack();
        switcher.setLocationRelativeTo(parentWindow());
        switcher.setVisible(true);
    }

    private static Window parentWindow() {
        return currentWindow == null ? null : (Window) currentWindow;
    }

    // Returns the XP value, or null when the argument is not a plain non-negative number.
    private static Integer parseXp(String raw) {
        try {
            int value = Integer.parseInt(raw.trim());
            return value >= 0 ? value : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void printUsageAndExit() {
        System.err.println(USAGE);
        System.exit(1);
    }
}
