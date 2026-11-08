package edu.batstateu.javajolt.service;

import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.LessonFileException;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.ui.GameUI;

public class LessonRunnerService {
    private static final int PASS_PERCENT = 70;
    // XP tuning (fixed for now, but kept in methods so they can scale by difficulty later)
    private static final int XP_PER_CORRECT = 5;
    private static final int XP_BUDGET_PER_QUIZ = 25;
    private static final int LESSON_COMPLETE_XP = 50;

    private final QuestionService questionService;
    private final LessonService lessonService;
    private final EnemyRoster enemyRoster;
    private final ScoreService scoreService;
    private final GameUI ui;

    public LessonRunnerService(QuestionService questionService, LessonService lessonService,
            EnemyRoster enemyRoster, ScoreService scoreService, GameUI ui) {
        this.questionService = questionService;
        this.lessonService = lessonService;
        this.enemyRoster = enemyRoster;
        this.scoreService = scoreService;
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

    // Single start-of-quiz home: questions through QuestionService, the session,
    // the level-derived max HP, the topic's enemy, and a full HP restore (brief Phase 2/3).
    public GameSession startSession(Player player, Topic topic, Difficulty difficulty) {
        List<Question> questions = questionService.selectQuestions(topic, difficulty);
        GameSession session = new GameSession(player, topic, difficulty, questions);
        session.setEnemy(enemyRoster.getEnemy(topic, difficulty));
        player.setMaxHp(LevelRewardService.maxHpForLevel(LevelService.calculateLevel(player.getXp())));
        player.restoreFullHp();
        return session;
    }

    public void playLesson(GameSession session) {
        Player player = session.getPlayer();
        List<Question> questions = session.getQuestions();
        for (int i = 0; i < questions.size() && player.isAlive(); i++) {
            Question question = questions.get(i);
            session.setCurrentQuestionIndex(i + 1);
            String answer = ui.askQuestion(question, i + 1, questions.size(), session);
            if (submitAnswer(session, question, answer)) {
                ui.showCorrect(question);
            } else {
                ui.showWrong(question, player);
            }
        }
    }

    // Records one answer using the same rules for console and Swing callers.
    // Score points come from ScoreService based on the streak that ends with
    // this answer; they accumulate in the session (provisional) and settle only
    // at settleAttempt, exactly like XP. Shield zeroes the next mistake's damage;
    // the mistake still counts and still escalates the chain.
    public boolean submitAnswer(GameSession session, Question question, String answer) {
        Player player = session.getPlayer();
        if (questionService.isCorrect(question, answer)) {
            int points =
                    scoreService.pointsForCorrect(session.getCurrentStreak() + 1);
            session.recordCorrect(points);

            // First-clear XP is provisional until the attempt settles.
            if (!player.isComplete(session.getTopic(), session.getDifficulty())) {
                session.addProvisionalXp(xpPerCorrect(session.getQuestions().size()));
            }

            return true;
        }
        // The enemy resolves first (pre-answer counter); a shielded mistake
        // deals 0 damage. recordMistake records the actual damage and
        // increments the counter either way.
        int damage = session.getEnemy().resolveMistake(session);
        if (session.consumeShield()) {
            damage = 0;
        }
        player.takeDamage(damage);
        session.recordMistake(damage);
        return false;
    }

    public boolean hasPassed(GameSession session) {
        return session.getPlayer().isAlive()
                && session.getAccuracyPercent() >= PASS_PERCENT;
    }

    // End-of-quiz settlement; runs exactly once per attempt. Every finished attempt
    // commits its score; first-clear per-answer XP plus the 50 XP completion bonus
    // commit together only when a passed attempt clears an uncleared pair. Failed
    // attempts, replays of completed pairs, and abandoned quizzes (window closed,
    // back navigation, different quiz) earn no XP, and abandoned quizzes bank no score.
    // Returns true when this attempt newly completed the topic/difficulty pair.
    public boolean settleAttempt(GameSession session) {
        if (session.isSettled()) {
            return false;
        }
        session.markSettled();

        Player player = session.getPlayer();
        player.addScore(session.getScoreEarned());

        if (hasPassed(session)
                && !player.isComplete(session.getTopic(), session.getDifficulty())) {
            player.addXp(session.getProvisionalXp()
                    + lessonCompleteBonus(session.getDifficulty()));
            player.complete(session.getTopic(), session.getDifficulty());
            return true;
        }
        return false;
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
