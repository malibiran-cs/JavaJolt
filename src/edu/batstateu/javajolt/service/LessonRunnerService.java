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
    private static final int DAMAGE_PER_MISTAKE = 3;
    private static final int POINTS_PER_CORRECT = 10;
    private static final int PASS_PERCENT = 70;

    private QuestionService questionService;
    private LessonService lessonService;
    private GameUI ui;

    public LessonRunnerService(QuestionService questionService, LessonService lessonService, GameUI ui) {
        this.questionService = questionService;
        this.lessonService = lessonService;
        this.ui = ui;
    }

    private void study(Topic topic, Difficulty difficulty) {
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

    private void playLesson(GameSession session) {
        Player player = session.getPlayer();
        List<Question> questions = session.getQuestions();
        // Restore HP at the beginning of each lesson.
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

    private boolean hasPassed(GameSession session) {
        return session.getPlayer().isAlive()
                && session.getAccuracyPercent() >= PASS_PERCENT;
    }
}
