package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.TutorialStep;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.enums.TutorialTrigger;
import edu.batstateu.javajolt.game.Enemy;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.AnswerTimerService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.List;

// The single tutorial script; numeric gameplay facts come from their rule owners.
public class TutorialContent {
    // The first practice question demonstrates a short countdown.
    private static final int TIMER_QUESTION_INDEX = 1;
    // Power-ups are introduced together before the second practice question.
    private static final int HEAL_QUESTION_INDEX = 2;

    private final LessonRunnerService lessons;
    private final ScoreService scores;
    private final AnswerTimerService timer;
    private final PowerUpService powerUps;

    public TutorialContent(LessonRunnerService lessons, ScoreService scores,
            AnswerTimerService timer, PowerUpService powerUps) {
        this.lessons = lessons;
        this.scores = scores;
        this.timer = timer;
        this.powerUps = powerUps;
    }

    // Negative IDs keep these fresh samples separate from registered question-bank content.
    public List<Question> createQuestions(Topic topic, Difficulty difficulty) {
        return List.of(
                new MultipleChoiceQuestion(-1,
                        "Which line creates a whole-number variable named count?",
                        topic, difficulty, "int stores whole numbers, so int count = 3; fits the question.",
                        List.of("A. int count = 3;", "B. String count = \"three\";",
                                "C. boolean count = true;", "D. double count = 3.5;"), "A"),
                new MultipleChoiceQuestion(-2,
                        "After int count = 3; followed by count = 4; what value does count hold?",
                        topic, difficulty, "Assigning 4 replaces the earlier value, so count now holds 4.",
                        List.of("A. 3", "B. 4", "C. 7", "D. count"), "B"));
    }

    public int getTimerQuestionIndex() {
        return TIMER_QUESTION_INDEX;
    }

    public int getHealQuestionIndex() {
        return HEAL_QUESTION_INDEX;
    }

    public List<TutorialStep> createSteps(GameSession session, int demoSeconds) {
        return List.of(
                new TutorialStep(TutorialTrigger.SESSION_START, 0, "Your goal", goal(session)),
                new TutorialStep(TutorialTrigger.SESSION_START, 0, "Your HP", hp(session)),
                new TutorialStep(TutorialTrigger.SESSION_START, 0, "Enemy attacks", damage(session)),
                new TutorialStep(TutorialTrigger.SESSION_START, 0, "Score and streaks", score()),
                new TutorialStep(TutorialTrigger.SESSION_START, 0, "Keeping your score", records()),
                new TutorialStep(TutorialTrigger.BEFORE_QUESTION, TIMER_QUESTION_INDEX,
                        "Clearing a quiz", winning()),
                new TutorialStep(TutorialTrigger.BEFORE_QUESTION, TIMER_QUESTION_INDEX,
                        "Two ways to lose", losing()),
                new TutorialStep(TutorialTrigger.BEFORE_QUESTION, TIMER_QUESTION_INDEX,
                        "The question timer", countdown(session, demoSeconds)),
                new TutorialStep(TutorialTrigger.BEFORE_QUESTION, HEAL_QUESTION_INDEX,
                        "Your power-ups", powers()),
                new TutorialStep(TutorialTrigger.BEFORE_QUESTION, HEAL_QUESTION_INDEX,
                        "Try Heal", heal(session)),
                new TutorialStep(TutorialTrigger.BEFORE_QUESTION, HEAL_QUESTION_INDEX,
                        "XP and levels", xp(session)),
                new TutorialStep(TutorialTrigger.BEFORE_QUESTION, HEAL_QUESTION_INDEX,
                        "Where to go next", progression()),
                new TutorialStep(TutorialTrigger.RESULTS, 0, "You're ready!",
                        "You're ready to explore JavaJolt. Try a lesson, study its Textbook, and take your time learning."));
    }

    private String goal(GameSession session) {
        return "A quiz is a fixed set of questions; this sample has " + session.getQuestions().size() + ". "
                + "The enemy is the topic's creature, here " + session.getEnemy().getName()
                + ", and it has no HP to remove. You clear a quiz by answering its questions.";
    }

    private String hp(GameSession session) {
        return "You start every quiz at full HP; this practice player has " + session.getPlayer().getMaxHp()
                + " HP. Your maximum HP grows with your level, and HP resets every quiz. "
                + "This practice uses a separate player, so your real progress stays safe.";
    }

    private String damage(GameSession session) {
        return "A wrong answer lets the enemy attack for "
                + Enemy.baseDamageForMaxHp(session.getPlayer().getMaxHp()) + " HP at first. "
                + "Each mistake in a row adds " + session.getEnemy().getEscalationPerMistake()
                + " HP to the next hit. A correct answer resets that extra pressure, "
                + "so the next mistake starts at the first hit again.";
    }

    private String score() {
        return "Correct answers earn score, separate from XP. As your correct streak grows, "
                + "your next answers earn " + scores.pointsForCorrect(1) + ", " + scores.pointsForCorrect(2)
                + ", then " + scores.pointsForCorrect(3) + " points. A mistake resets the streak; "
                + "the next correct answer starts again at " + scores.pointsForCorrect(1) + " points.";
    }

    private String winning() {
        return "To pass, you must still be alive and finish with at least " + lessons.getPassPercent()
                + "% accuracy. Accuracy means correct answers divided by answered questions; "
                + "questions you have not answered are not counted.";
    }

    private String records() {
        return "Finishing a quiz adds its points to your total score, even if you don't pass. "
                + "Your best quiz score for each topic and difficulty is saved as a high score. "
                + "Leaving a quiz unfinished keeps no points from that attempt; this practice does not save any score.";
    }

    private String losing() {
        return "If HP reaches zero, the quiz ends immediately. Even with HP left, finishing below "
                + lessons.getPassPercent() + "% accuracy fails. Survival and accuracy are checked "
                + "separately, so you must meet both.";
    }

    private String countdown(GameSession session, int demoSeconds) {
        Question question = session.getQuestions().get(TIMER_QUESTION_INDEX - 1);
        return "The usual timer for this kind of question is "
                + timer.getTimeLimitSeconds(session.getDifficulty(), question.getInputMode())
                + " seconds at " + session.getDifficulty().getLabel() + ". This practice question uses "
                + demoSeconds + " seconds; let it run out if you'd like to see what happens. "
                + "Here the enemy attacks once for " + timer.getTimeoutDamage(session)
                + " HP when time runs out, but you can still answer; Beginner has no penalty, "
                + "while Advanced uses the enemy's base attack.";
    }

    private String powers() {
        return "Power-ups unlock by level, and each can be used once per quiz. "
                + unlock(PowerUp.HINT) + " removes one wrong choice; " + unlock(PowerUp.SHIELD)
                + " blocks the next attack, but a wrong answer still counts. " + unlock(PowerUp.HEAL)
                + " instantly restores " + powerUps.getHealPercent(Difficulty.BEGINNER) + "% / "
                + powerUps.getHealPercent(Difficulty.INTERMEDIATE) + "% / "
                + powerUps.getHealPercent(Difficulty.ADVANCED)
                + "% of maximum HP on Beginner / Intermediate / Advanced, capped at full HP; "
                + "full HP does not consume it. " + unlock(PowerUp.TIME_STOP) + " pauses the question timer.";
    }

    private String unlock(PowerUp powerUp) {
        return powerUp.getLabel() + " (level " + powerUp.getUnlockLevel() + ")";
    }

    private String heal(GameSession session) {
        return "I've given this practice player some damage so you can try Heal. At "
                + session.getDifficulty().getLabel() + " with " + session.getPlayer().getMaxHp()
                + " maximum HP, it restores up to "
                + powerUps.getHealAmount(session.getPlayer().getMaxHp(), session.getDifficulty())
                + " HP, stopping at full HP. Heal changes HP only; your score, accuracy and streak stay the same.";
    }

    private String xp(GameSession session) {
        return "XP comes from correct answers on a successful first clear; finishing an uncleared "
                + "topic and difficulty also gives a one-time " + lessons.lessonCompleteBonus(session.getDifficulty())
                + " XP bonus. Failed or abandoned attempts and completed-lesson replays give no ordinary XP. "
                + "Levels raise maximum HP and unlock power-ups; this practice never awards real XP or score.";
    }

    private String progression() {
        return "Topics unlock in order as you clear them. Finishing all topics in a difficulty "
                + "advances you to the next difficulty. The Textbook is there to study before quizzing.";
    }
}
