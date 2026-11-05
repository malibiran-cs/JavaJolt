package edu.batstateu.javajolt.service;

import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.LessonFileException;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.ui.GameUI;

public class LessonRunnerService {
    // TODO(gameplay balance): Revisit damage and survival pressure when enemy attacks/status effects are designed.
    private static final int DAMAGE_PER_MISTAKE = 2;
    private static final int POINTS_PER_CORRECT = 10;
    private static final int PASS_PERCENT = 70;
    // XP tuning (fixed for now, but kept in methods so they can scale by difficulty later)
    private static final int XP_PER_CORRECT = 5;
    private static final int XP_BUDGET_PER_QUIZ = 25;
    private static final int LESSON_COMPLETE_XP = 50;

    private final QuestionService questionService;
    private final LessonService lessonService;
    private final GameUI ui;

    public LessonRunnerService(QuestionService questionService, LessonService lessonService, GameUI ui) {
        this.questionService = questionService;
        this.lessonService = lessonService;
        this.ui = ui;
    }

    public void study(Topic topic, Difficulty difficulty) {
        try {
            List<LessonPage> pages = lessonService.loadPages(topic, difficulty);
            if (pages.isEmpty()) {
                ui.showMessage("There is no textbook for " + topic.getLabel() + " yet. Try Play now.");
            } else {
                ui.showLessonPages(pages);
            }
        } catch (LessonFileException e) {
            ui.showMessage(e.getMessage());
        }
    }

    public void playLesson(GameSession session) {
        Player player = session.getPlayer();
        List<Question> questions = session.getQuestions();
        // Restore HP at the beginning of each lesson.
        player.setHp(Player.getStartingHp());
        for (int i = 0; i < questions.size() && player.isAlive(); i++) {
            Question question = questions.get(i);
            session.setCurrentQuestionIndex(i + 1);
            String answer = ui.askQuestion(question, i + 1, questions.size(), player);
            if (submitAnswer(session, question, answer)) {
                ui.showCorrect(question);
            } else {
                ui.showWrong(question, player);
            }
        }
    }

    // Records one answer using the same rules for console and Swing callers.
    public boolean submitAnswer(GameSession session, Question question, String answer) {
        Player player = session.getPlayer();
        if (questionService.isCorrect(question, answer)) {
            player.addScore(POINTS_PER_CORRECT);

            // A completed topic/difficulty pair no longer awards repeat XP.
            if (!player.isComplete(session.getTopic(), session.getDifficulty())) {
                player.addXp(xpPerCorrect(session.getQuestions().size()));
            }

            session.recordCorrect(POINTS_PER_CORRECT);
            return true;
        }
        player.takeDamage(DAMAGE_PER_MISTAKE);
        session.recordMistake();
        return false;
    }

    // TODO(gameplay balance): Keep HP as a meaningful survival condition alongside accuracy.
    // Current damage may not defeat a player before the 70% accuracy rule already makes the run fail.
    public boolean hasPassed(GameSession session) {
        return session.getPlayer().isAlive()
                && session.getAccuracyPercent() >= PASS_PERCENT;
    }

    // Applies the progress reward once when a lesson is passed.
    public void recordLessonCompletion(GameSession session) {
        if (hasPassed(session)
                && session.getPlayer().getProgress().complete(session.getTopic(), session.getDifficulty())) {
            session.getPlayer().addXp(lessonCompleteBonus(session.getDifficulty()));
        }
    }

    // XP awarded per correct answer. Fixed value for now; can scale by difficulty later.

    public int xpPerCorrect(int questionCount) {

        if (questionCount <= 0) {
            return 0;
        }

        // Distribute approximately 25 XP across the quiz
        int scaledXp = Math.round(
                (float) XP_BUDGET_PER_QUIZ / questionCount
        );

        // Limit the reward to 1–5 XP per correct answer
        return Math.max(
                1,
                Math.min(XP_PER_CORRECT, scaledXp)
        );
    }


    // Bonus XP for clearing a lesson. Fixed value for now; can scale by difficulty later.
    public int lessonCompleteBonus(Difficulty difficulty) {
        return LESSON_COMPLETE_XP;
    }
}
