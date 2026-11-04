
package edu.batstateu.javajolt;

import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.game.GameService;
import edu.batstateu.javajolt.service.DifficultyProgressionService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.ui.GameUI;
import edu.batstateu.javajolt.ui.swing.MainWindow;
import edu.batstateu.javajolt.ui.swing.ScreenId;

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

        LessonRunnerService runner =
                new LessonRunnerService(questions, lessons, consoleUI);

        // Console mode remains available for development.
        if (args.length > 0
                && "--console".equalsIgnoreCase(args[0])) {
            try {
                new GameService(
                        questions,
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
                player.restoreProgress(data.completedTopics());
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
                    saves
            );

            window.showScreen(
                    hasLoadedSave
                            ? ScreenId.MAIN_MENU
                            : ScreenId.WELCOME
            );

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
}
