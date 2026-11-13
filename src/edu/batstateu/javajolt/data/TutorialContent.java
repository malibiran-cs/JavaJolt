package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.TutorialStep;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.JoltMessage;
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
        // Introduce the timer before Question 1.
        private static final int FIRST_QUESTION_INDEX = 1;
        private static final int TIMER_QUESTION_INDEX = 1;
    // Power-ups are introduced together before the second practice question.
    private static final int HEAL_QUESTION_INDEX = 2;
    private static final int JOLT_QUESTION_INDEX = 3;

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
                        List.of("A. 3", "B. 4", "C. 7", "D. count"), "B"),
                new MultipleChoiceQuestion(-3,
                        "Which Java type stores true or false?",
                        topic, difficulty, "boolean stores the truth values true and false.",
                        List.of("A. int", "B. double", "C. boolean", "D. String"), "C"),
                new MultipleChoiceQuestion(-4,
                        "After int count = 3; followed by count = count + 2; what value does count hold?",
                        topic, difficulty, "Adding 2 to 3 and assigning the result makes count equal to 5.",
                        List.of("A. 2", "B. 3", "C. 4", "D. 5"), "D"),
                new MultipleChoiceQuestion(-5,
                        "Which line assigns 4 to an existing variable named count?",
                        topic, difficulty, "The assignment operator = stores 4 in the existing variable count.",
                        List.of("A. count = 4;", "B. int = count 4;", "C. count == 4;", "D. 4 = count;"), "A"));
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
                new TutorialStep(TutorialTrigger.SESSION_START, 0, "Jolt and score", score()),
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
                new TutorialStep(TutorialTrigger.BEFORE_QUESTION, JOLT_QUESTION_INDEX,
                        "Build a Jolt", jolt()),
                createResultsStep(session));
    }

    // Re-rendered when RESULTS is due so the wrap-up explains the finished sample.
    public TutorialStep createResultsStep(GameSession session) {
        String accuracy = session.getCorrectCount() + " of " + session.getAnsweredCount()
                + " correct = " + session.getAccuracyPercent() + "%";
        String body;
        if (!session.getPlayer().isAlive()) {
            body = "Your HP reached " + session.getPlayer().getHp() + ", so the practice quiz ended in defeat. "
                    + accuracy + "; even reaching the " + lessons.getPassPercent()
                    + "% needed cannot overcome running out of HP. ";
        } else if (lessons.hasPassed(session)) {
            body = "You cleared the practice quiz: " + accuracy + ", meeting the " + lessons.getPassPercent()
                    + "% needed with " + session.getPlayer().getHp() + " HP remaining. You're ready to try a lesson. ";
        } else {
            body = "You finished with " + session.getPlayer().getHp() + " HP, but " + accuracy
                    + ", below the " + lessons.getPassPercent()
                    + "% needed. Stay alive and meet the accuracy goal to clear a real quiz. ";
        }
        return new TutorialStep(TutorialTrigger.RESULTS, 0, "You're ready!",
                body + "This was practice, nothing was lost.");
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
        return "Consecutive correct answers build your Jolt, increasing its multiplier. A higher Jolt pays more: "
                + "your next answers earn " + scores.pointsForCorrect(1) + ", " + scores.pointsForCorrect(2)
                + ", then " + scores.pointsForCorrect(3) + " points, separate from XP. Milestones: "
                + JoltMessage.SPARK.getText() + " at " + JoltMessage.SPARK_STREAK + "x, "
                + JoltMessage.JOLT.getText() + " at " + JoltMessage.JOLT_STREAK + "x, "
                + JoltMessage.SURGE.getText() + " at " + JoltMessage.SURGE_STREAK + "x. "
                + "Answer every question correctly for " + JoltMessage.JAVAJOLT.getText()
                + " A mistake ends a Jolt of at least " + JoltMessage.SHORT_CIRCUIT_MIN_STREAK
                + "x with " + JoltMessage.SHORT_CIRCUIT.getText()
                + " Your next correct answer restarts at the first multiplier and earns "
                + scores.pointsForCorrect(1) + " points.";
    }

    private String jolt() {
        return "Answer the next questions correctly in a row. Reach " + JoltMessage.SPARK_STREAK
                + "x to see " + JoltMessage.SPARK.getText() + " appear, then miss one on purpose if you'd like to see "
                + JoltMessage.SHORT_CIRCUIT.getText() + " This is practice; nothing here is saved.";
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


        private String countdown(
                GameSession session,
                int timerSeconds) {
            int normalSeconds = timer.getTimeLimitSeconds(
                    session.getDifficulty(),
                    session.getQuestions().get(TIMER_QUESTION_INDEX - 1).getInputMode());

        return "Great job completing your first practice question! "
                + "You had unlimited time to think and answer.\n\n"

                    + "In a normal quiz, this question gives you "
                    + normalSeconds
                    + " seconds. In this practice, you will have "
                + timerSeconds
                + " seconds to submit your answer.\n\n"

                + "There is no need to rush. Read the question "
                + "carefully, consider your choices, and answer "
                + "when you're ready.\n\n"

                + "The timer pauses while tutorial instructions "
                + "are being shown.\n\n"

                + "If the countdown reaches zero, you will lose "
                + timer.getTimeoutDamage(session)
                + " HP in this practice, but you can still answer. "
                + "Beginner has no penalty for timing out.";

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
                + " HP, stopping at full HP. Heal changes HP only; your score, accuracy and Jolt stay the same.";
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
