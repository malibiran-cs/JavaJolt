
package edu.batstateu.javajolt;

import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.data.TutorialContent;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.game.GameService;
import edu.batstateu.javajolt.service.DifficultyProgressionService;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.AnswerTimerService;
import edu.batstateu.javajolt.service.TutorialService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.ui.GameUI;
import edu.batstateu.javajolt.ui.swing.MainWindow;
import edu.batstateu.javajolt.ui.swing.ScreenId;
import edu.batstateu.javajolt.ui.swing.TutorialCoordinator;

import java.util.NoSuchElementException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameApplication {

    public static void main(String[] args) {
        QuestionService questions =
                new QuestionService(new QuestionBank());

        LessonService lessons = new LessonService();
        SaveService saves = new SaveService();
        GameUI consoleUI = new GameUI();

        ScoreService scores = new ScoreService();
        LessonRunnerService runner =
                new LessonRunnerService(questions, lessons, new EnemyRoster(),
                        scores, consoleUI);
        PowerUpService powerUps = new PowerUpService();
        AnswerTimerService answerTimer = new AnswerTimerService();
        TutorialContent tutorialContent = new TutorialContent(runner, scores, answerTimer, powerUps);
        TutorialService tutorial = new TutorialService(tutorialContent, runner, answerTimer, powerUps);
        TutorialCoordinator tutorialCoordinator = new TutorialCoordinator(tutorial);

        // Console mode remains available for development.
        if (args.length > 0
                && "--console".equalsIgnoreCase(args[0])) {
            try {
                new GameService(
                        runner,
                        saves,
                        consoleUI,
                        (player, difficulty) -> {}
                ).run();
            } catch (NoSuchElementException | SaveFileException e) {
                System.err.println(e.getMessage());
            }
            return;
        }

        // Prepare the player for the GUI.
        Player player = new Player("Player", 0);
        boolean loaded = false;
        String loadWarning = null;

        if (saves.hasSave()) {
            try {
                SaveData data = saves.load();

                player = new Player(data.name(), data.xp());
                player.setScore(data.score());
                player.restoreProgress(data.completedTopics());
                player.restoreHighScores(data.highScores());
                player.setTutorialCompleted(data.tutorialCompleted());
                loaded = true;

            } catch (SaveFileException e) {
                loadWarning =
                        "Could not load saved progress. Starting a new game.";
            }
        }

        Difficulty difficulty =
                DifficultyProgressionService.currentDifficulty(player);

        final Player readyPlayer = player;
        final boolean hasLoadedSave = loaded;
        final String warning = loadWarning;

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(
                    readyPlayer,
                    questions,
                    runner,
                    difficulty,
                    powerUps,
                    answerTimer,
                    saves,
                    tutorialCoordinator
            );

            window.setTitleStartDestination(
                    startDestination(readyPlayer, hasLoadedSave)
            );
            // Start with the Title Screen for both new and returning players.
            window.showScreen(ScreenId.TITLE);

            window.setVisible(true);

            if (warning != null) {
                JOptionPane.showMessageDialog(
                        window,
                        warning,
                        "Save Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }
    // A loaded incomplete player already has a name and must resume the tutorial.
    public static ScreenId startDestination(Player player, boolean loaded) {
        if (!loaded) return ScreenId.WELCOME;
        return player.isTutorialCompleted() ? ScreenId.MAIN_MENU : ScreenId.QUESTION;
    }
}
