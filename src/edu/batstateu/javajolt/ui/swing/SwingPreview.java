
package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.data.TutorialContent;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.preview.PreviewSandbox;
import edu.batstateu.javajolt.service.AnswerTimerService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.service.TutorialService;
import edu.batstateu.javajolt.ui.GameUI;

import javax.swing.SwingUtilities;

public class SwingPreview {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            QuestionService questions =
                    new QuestionService(new QuestionBank());

            ScoreService scores = new ScoreService();
            LessonRunnerService runner =
                    new LessonRunnerService(
                            questions,
                            new LessonService(),
                            new EnemyRoster(),
                            scores,
                            new GameUI()
                    );

            Player player = new Player("Preview", 0);
            PowerUpService powerUps = new PowerUpService();
            AnswerTimerService timer = new AnswerTimerService();
            TutorialContent content = new TutorialContent(runner, scores, timer, powerUps);
            MainWindow window = new MainWindow(
                    player,
                    questions,
                    runner,
                    Difficulty.BEGINNER,
                    powerUps,
                    timer,
                    PreviewSandbox.createFor(player, Difficulty.BEGINNER),
                    new TutorialCoordinator(new TutorialService(content, runner, timer, powerUps))
            );

            window.showScreen(ScreenId.TITLE);
            window.setVisible(true);
        });
    }
}
