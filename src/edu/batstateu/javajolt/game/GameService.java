package edu.batstateu.javajolt.game;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.ui.GameUI;
import java.util.List;

// The game rules and flow. All printing and typing lives in GameUI.
public class GameService {
    // Gameplay values used to calculate health, XP, and lesson completion.
    private static final int STARTING_HP = 10;
    private static final int DAMAGE_PER_MISTAKE = 3;
    private static final int POINTS_PER_CORRECT = 10;
    private static final int PASS_PERCENT = 70;

    // Services used to load questions and communicate with the player.
    private final QuestionService questionService;
    private final GameUI ui;

    // Creates a game service with its question and user-interface dependencies.
    public GameService(QuestionService questionService, GameUI ui) {
        this.questionService = questionService;
        this.ui = ui;
    }

    // Runs the welcome screen, lesson loop, and goodbye screen.
    public void run() {
        ui.showWelcome();
        Player player = new Player(ui.askName(), STARTING_HP, 0);

        boolean playing = true;
        while (playing) {
            Difficulty difficulty = ui.askDifficulty();
            Topic topic = ui.askTopic();

            try {
                List<Question> questions = questionService.selectQuestions(topic, difficulty);
                GameSession session = new GameSession(player, topic, difficulty, questions);

                playLesson(session);
                ui.showResult(session, hasPassed(session));
            } catch (Exception e) {
                ui.showMessage(e.getMessage());
            }

            playing = ui.askPlayAgain();
        }

        ui.showGoodbye(player);
    }

    // Presents each question and updates the player's score, HP, and results.
    private void playLesson(GameSession session) {
        Player player = session.getPlayer();
        List<Question> questions = session.getQuestions();
        player.setHp(STARTING_HP); 

        for (int i = 0; i < questions.size() && player.isAlive(); i++) {
            Question question = questions.get(i);
            session.setCurrentQuestionIndex(i + 1);

            String answer = ui.askQuestion(question, i + 1, questions.size(), player);

            if (questionService.isCorrect(question, answer)) {
                player.addScore(POINTS_PER_CORRECT);
                session.recordCorrect(POINTS_PER_CORRECT);
                ui.showCorrect(question);
            } else {
                player.takeDamage(DAMAGE_PER_MISTAKE);
                session.recordMistake();
                ui.showWrong(question, player);
            }
        }
    }

    // A lesson passes only when the player survives and reaches the required accuracy.
    private boolean hasPassed(GameSession session) {
        return session.getPlayer().isAlive() && session.getAccuracyPercent() >= PASS_PERCENT;
    }
}
