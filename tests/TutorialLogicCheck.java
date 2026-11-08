import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.data.TutorialContent;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.PowerUpResult;
import edu.batstateu.javajolt.entity.TutorialStep;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.enums.TutorialTrigger;
import edu.batstateu.javajolt.game.Enemy;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.AnswerTimerService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LevelRewardService;
import edu.batstateu.javajolt.service.LevelService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.service.TutorialService;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Pure tutorial checks: no Swing, compiler, save service or real player enters the tutorial.
public class TutorialLogicCheck {
    private static final ScoreService scores = new ScoreService();
    private static final AnswerTimerService timer = new AnswerTimerService();
    private static final PowerUpService powerUps = new PowerUpService();
    private static final EnemyRoster roster = new EnemyRoster();
    private static final QuestionService questions = new QuestionService(new QuestionBank()) {
        @Override
        public List<Question> selectQuestions(Topic topic, Difficulty difficulty) {
            throw new AssertionError("Tutorial must not select from or update question-bank history");
        }
    };
    private static final LessonRunnerService lessons =
            new LessonRunnerService(questions, null, roster, scores, null) {
                @Override
                public boolean settleAttempt(GameSession session) {
                    throw new AssertionError("Tutorial must never settle an attempt");
                }
            };
    private static final TutorialContent content = new TutorialContent(lessons, scores, timer, powerUps);
    private static int checks;

    public static void main(String[] args) {
        checkRenderedNumbers(args.length > 0 && args[0].equals("--print-script"));
        checkWrapUps();
        for (boolean firstCorrect : new boolean[] {false, true}) {
            for (boolean secondCorrect : new boolean[] {false, true}) {
                for (boolean expire : new boolean[] {false, true}) {
                    for (boolean usePowers : new boolean[] {false, true}) {
                        checkRun(firstCorrect, secondCorrect, expire, usePowers);
                    }
                }
            }
        }
        if (args.length > 0 && args[0].equals("--retuned")) {
            require(lessons.getPassPercent() == 75
                            && timer.getTimeLimitSeconds(Difficulty.INTERMEDIATE,
                                    content.createQuestions(Topic.VARIABLES, Difficulty.INTERMEDIATE).get(0).getInputMode()) == 24
                            && powerUps.getHealPercent(Difficulty.INTERMEDIATE) == 40
                            && PowerUp.TIME_STOP.getUnlockLevel() == 22,
                    "Temporary recompiled constants are active");
            System.out.println("RETUNED TEXT CHECKS PASSED");
        }
        System.out.println("ALL TUTORIAL LOGIC CHECKS PASSED (" + checks + ")");
    }

    private static TutorialService tutorial() {
        TutorialService tutorial = new TutorialService(content, lessons, timer, powerUps);
        tutorial.startRun();
        return tutorial;
    }

    private static void checkRenderedNumbers(boolean printScript) {
        TutorialService tutorial = tutorial();
        GameSession session = tutorial.getSession();
        List<TutorialStep> steps = content.createSteps(session, tutorial.getTimeLimitSeconds(1));
        if (printScript) {
            for (TutorialStep step : steps) {
                System.out.println(step.title() + "\n" + step.body() + "\n");
            }
        }
        require(steps.stream().map(TutorialStep::title).toList().equals(List.of(
                "Your goal", "Your HP", "Enemy attacks", "Score and streaks", "Keeping your score", "Clearing a quiz",
                "Two ways to lose", "The question timer", "Your power-ups", "Try Heal", "XP and levels",
                "Where to go next", "You're ready!")), "Required concepts plus existing score records in teaching order");
        expectNumbers(steps.get(0), session.getQuestions().size());
        expectNumbers(steps.get(1), session.getPlayer().getMaxHp());
        expectNumbers(steps.get(2), Enemy.baseDamageForMaxHp(session.getPlayer().getMaxHp()),
                session.getEnemy().getEscalationPerMistake());
        expectNumbers(steps.get(3), scores.pointsForCorrect(1), scores.pointsForCorrect(2),
                scores.pointsForCorrect(3), scores.pointsForCorrect(1));
        expectNumbers(steps.get(4));
        expectNumbers(steps.get(5), lessons.getPassPercent());
        expectNumbers(steps.get(6), lessons.getPassPercent());
        expectNumbers(steps.get(7), timer.getTimeLimitSeconds(session.getDifficulty(),
                session.getQuestions().get(0).getInputMode()), tutorial.getTimeLimitSeconds(1),
                timer.getTimeoutDamage(session));
        expectNumbers(steps.get(8), PowerUp.HINT.getUnlockLevel(), PowerUp.SHIELD.getUnlockLevel(),
                PowerUp.HEAL.getUnlockLevel(), powerUps.getHealPercent(Difficulty.BEGINNER),
                powerUps.getHealPercent(Difficulty.INTERMEDIATE), powerUps.getHealPercent(Difficulty.ADVANCED),
                PowerUp.TIME_STOP.getUnlockLevel());
        expectNumbers(steps.get(9), session.getPlayer().getMaxHp(),
                powerUps.getHealAmount(session.getPlayer().getMaxHp(), session.getDifficulty()));
        expectNumbers(steps.get(10), lessons.lessonCompleteBonus(session.getDifficulty()));
        expectNumbers(steps.get(11));
        expectNumbers(steps.get(12), session.getPlayer().getHp(), 0, 0, 0, lessons.getPassPercent());
        require(tutorial.getTimeLimitSeconds(1) == Math.min(5,
                        timer.getTimeLimitSeconds(session.getDifficulty(), session.getQuestions().get(0).getInputMode())),
                "The demo timer is five seconds without changing real limits");
        require(steps.get(7).body().contains("Beginner has no penalty")
                        && steps.get(7).body().contains("still answer")
                        && steps.get(6).body().contains("checked separately"),
                "Deterministic explanations include timeout answering and independent loss conditions");
    }

    private static void expectNumbers(TutorialStep step, int... expectedValues) {
        List<Integer> expected = new ArrayList<>();
        for (int value : expectedValues) {
            expected.add(value);
        }
        List<Integer> actual = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\b\\d+\\b").matcher(step.body());
        while (matcher.find()) {
            actual.add(Integer.parseInt(matcher.group()));
        }
        require(actual.equals(expected), "All numeric text reads its rule owner: " + step.title());
    }

    private static void checkWrapUps() {
        for (String outcome : List.of("passed", "accuracy", "hp")) {
            TutorialService tutorial = tutorial();
            GameSession session = tutorial.getSession();
            beginQuestion(tutorial, 1);
            lessons.submitAnswer(session, session.getQuestions().get(0), "A");
            beginQuestion(tutorial, 2);
            lessons.submitAnswer(session, session.getQuestions().get(1), outcome.equals("accuracy") ? "A" : "B");
            if (outcome.equals("hp")) session.getPlayer().takeDamage(session.getPlayer().getHp());
            TutorialStep step = tutorial.getStepsDue(TutorialTrigger.RESULTS, 0).get(0);
            require(step.body().endsWith("This was practice, nothing was lost."), "Wrap-up reassurance: " + outcome);
            if (outcome.equals("passed")) {
                require(lessons.hasPassed(session) && step.body().contains("You cleared"), "Passed wrap-up uses hasPassed");
                expectNumbers(step, session.getCorrectCount(), session.getAnsweredCount(), session.getAccuracyPercent(),
                        lessons.getPassPercent(), session.getPlayer().getHp());
            } else {
                require(step.body().contains(outcome.equals("hp") ? "ended in defeat" : "below the"),
                        "Wrap-up names the real failure reason: " + outcome);
                expectNumbers(step, session.getPlayer().getHp(), session.getCorrectCount(), session.getAnsweredCount(),
                        session.getAccuracyPercent(), lessons.getPassPercent());
            }
            require(tutorial.getStepsDue(TutorialTrigger.RESULTS, 0).isEmpty(), "One result variant per run");
        }
    }

    private static void checkRun(boolean firstCorrect, boolean secondCorrect, boolean expire, boolean usePowers) {
        Player real = new Player("Real learner", 123);
        real.setScore(77);
        real.complete(Topic.OPERATORS, Difficulty.BEGINNER);
        real.recordHighScore(Topic.OPERATORS, Difficulty.BEGINNER, 52);
        real.setMaxHp(12);
        real.restoreFullHp();
        real.takeDamage(4);
        real.setTutorialCompleted(usePowers);
        TutorialService tutorial = tutorial();
        GameSession demo = tutorial.getSession();
        int xpBefore = demo.getPlayer().getXp();
        int highestUnlock = 1;
        for (PowerUp powerUp : PowerUp.values()) {
            highestUnlock = Math.max(highestUnlock, powerUp.getUnlockLevel());
            require(powerUps.isUnlocked(demo.getPlayer(), powerUp), "Derived demo level unlocks " + powerUp);
        }
        require(demo.getPlayer() != real && demo.getPlayer().getXp() == LevelService.totalXpForLevel(highestUnlock)
                        && demo.getPlayer().getMaxHp() == LevelRewardService.maxHpForLevel(highestUnlock)
                        && demo.getPlayer().getHp() == demo.getPlayer().getMaxHp(),
                "Fresh demo player has derived XP/max HP and starts full");
        require(demo.getDifficulty() == Difficulty.INTERMEDIATE && demo.getTopic() == Topic.VARIABLES
                        && demo.getEnemy().getName().equals(roster.getEnemy(Topic.VARIABLES, Difficulty.INTERMEDIATE).getName())
                        && demo.getEnemy().getEscalationPerMistake()
                                == roster.getEnemy(Topic.VARIABLES, Difficulty.INTERMEDIATE).getEscalationPerMistake(),
                "Demo uses the real Intermediate Variables enemy");
        require(demo.getQuestions().stream().allMatch(question -> question.getClass() == MultipleChoiceQuestion.class),
                "Only real multiple-choice questions, with no code compiler needed");
        require(tutorial.getStepsDue(TutorialTrigger.RESULTS, 0).isEmpty()
                        && tutorial.getStepsDue(TutorialTrigger.BEFORE_QUESTION, 1).isEmpty(),
                "Future steps cannot be consumed before their moment");
        List<String> seen = new ArrayList<>();
        collect(tutorial, seen, TutorialTrigger.SESSION_START, 0, 5);
        beginQuestion(tutorial, 1);
        collect(tutorial, seen, TutorialTrigger.BEFORE_QUESTION, 1, 3);
        require(tutorial.getTimeLimitSeconds(1) <= timer.getTimeLimitSeconds(demo.getDifficulty(),
                        demo.getQuestions().get(0).getInputMode()) && timer.getTimeoutDamage(demo) > 0,
                "Only the demo countdown is shortened; timeout is visibly damaging");
        require(!tutorial.canUsePowerUp(PowerUp.HEAL)
                        && !demo.hasUsed(PowerUp.HEAL), "Heal cannot be consumed before its introduction");
        if (expire) {
            checkTimeout(tutorial);
        }
        checkAnswer(tutorial, firstCorrect ? "A" : "B", firstCorrect);
        require(!tutorial.isFinished(), "Every answer route reaches the second practice question");
        int hpBefore = demo.getPlayer().getHp();
        int streakBefore = demo.getCurrentStreak();
        int mistakesBefore = demo.getConsecutiveMistakes();
        beginQuestion(tutorial, 2);
        int hpAfterPreparation = demo.getPlayer().getHp();
        beginQuestion(tutorial, 2);
        require(hpAfterPreparation < hpBefore && demo.getPlayer().getHp() == hpAfterPreparation
                        && demo.getCurrentStreak() == streakBefore && demo.getConsecutiveMistakes() == mistakesBefore,
                "Heal preparation uses damage once without recording an answer or changing counters");
        collect(tutorial, seen, TutorialTrigger.BEFORE_QUESTION, 2, 4);
        require(tutorial.getTimeLimitSeconds(2) == timer.getTimeLimitSeconds(demo.getDifficulty(),
                demo.getQuestions().get(1).getInputMode()), "Other questions retain their real time limit");
        require(tutorial.canUsePowerUp(PowerUp.HEAL), "Heal is usable at its practice moment");
        if (usePowers) {
            checkPowers(tutorial);
        }
        if (expire) {
            checkTimeout(tutorial);
        }
        checkAnswer(tutorial, secondCorrect ? "B" : "A", secondCorrect);
        require(tutorial.isFinished(), "Sample finishes after its fixed questions or final defeat");
        collect(tutorial, seen, TutorialTrigger.RESULTS, 0, 1);
        require(seen.size() == tutorial.getTotalStepCount() && tutorial.getShownStepCount() == seen.size()
                        && seen.stream().distinct().count() == seen.size(), "Every concept is seen exactly once on every route");
        require(!demo.isSettled() && demo.getPlayer().getXp() == xpBefore && demo.getPlayer().getScore() == 0
                        && demo.getPlayer().getProgress().copyCompleted().isEmpty()
                        && demo.getPlayer().copyHighScores().isEmpty(), "Tutorial never settles or commits demo rewards");
        require(real.getName().equals("Real learner") && real.getXp() == 123 && real.getScore() == 77
                        && real.getHp() == 8 && real.getMaxHp() == 12 && real.isTutorialCompleted() == usePowers
                        && real.getProgress().copyCompleted().size() == 1
                        && real.isComplete(Topic.OPERATORS, Difficulty.BEGINNER)
                        && real.getHighScore(Topic.OPERATORS, Difficulty.BEGINNER) == 52,
                "Real player and tutorial-completion flag remain untouched");
        Question previousQuestion = demo.getQuestions().get(0);
        tutorial.startRun();
        require(tutorial.getSession() != demo && tutorial.getSession().getPlayer() != demo.getPlayer()
                        && tutorial.getSession().getQuestions().get(0) != previousQuestion
                        && tutorial.getShownStepCount() == 0 && tutorial.getSession().getUsedPowerUps().isEmpty(),
                "Replay starts a fresh player, session, questions, usage and step tracking");
        require(tutorial.getStepsDue(TutorialTrigger.SESSION_START, 0).size() == 5,
                "Replaying shows the same tutorial again");
    }

    private static void collect(TutorialService tutorial, List<String> seen,
            TutorialTrigger trigger, int questionIndex, int expectedCount) {
        List<TutorialStep> due = tutorial.getStepsDue(trigger, questionIndex);
        require(due.size() == expectedCount, "Expected steps at " + trigger + " " + questionIndex);
        for (TutorialStep step : due) {
            seen.add(step.title());
        }
        require(tutorial.getStepsDue(trigger, questionIndex).isEmpty(), "Steps are returned only once at " + trigger);
    }

    private static void checkAnswer(TutorialService tutorial, String answer, boolean correct) {
        GameSession demo = tutorial.getSession();
        int hpBefore = demo.getPlayer().getHp();
        int damage = correct || demo.isShieldArmed() ? 0 : demo.getEnemy().resolveMistake(demo);
        int points = correct ? scores.pointsForCorrect(demo.getCurrentStreak() + 1) : 0;
        int scoreBefore = demo.getScoreEarned();
        require(lessons.submitAnswer(demo, demo.getQuestions().get(demo.getCurrentQuestionIndex() - 1), answer) == correct
                        && demo.getPlayer().getHp() == Math.max(0, hpBefore - damage)
                        && demo.getScoreEarned() == scoreBefore + points,
                "Answers use the real enemy damage and scoring path");
    }

    private static void beginQuestion(TutorialService tutorial, int index) {
        tutorial.getSession().setCurrentQuestionIndex(index);
        tutorial.prepareQuestion();
    }

    private static void checkTimeout(TutorialService tutorial) {
        GameSession demo = tutorial.getSession();
        int hpBefore = demo.getPlayer().getHp();
        int answered = demo.getAnsweredCount();
        int streak = demo.getCurrentStreak();
        int mistakes = demo.getConsecutiveMistakes();
        int expected = demo.isQuestionTimerFrozen() || demo.isShieldArmed() ? 0 : timer.getTimeoutDamage(demo);
        timer.applyTimeout(demo);
        timer.applyTimeout(demo);
        require(demo.getPlayer().getHp() == Math.max(0, hpBefore - expected)
                        && demo.getAnsweredCount() == answered && demo.getCurrentStreak() == streak
                        && demo.getConsecutiveMistakes() == mistakes,
                "Real timeout applies once without answering or changing either streak");
    }

    private static void checkPowers(TutorialService tutorial) {
        GameSession demo = tutorial.getSession();
        PowerUpResult hint = powerUps.use(demo, PowerUp.HINT, demo.getQuestions().get(1));
        require(hint.applied() && hint.removedChoices().size() == 1
                        && !demo.getQuestions().get(1).isCorrect(hint.removedChoices().get(0).substring(0, 1)),
                "Real Hint removes a wrong choice");
        int before = demo.getPlayer().getHp();
        int streak = demo.getCurrentStreak();
        int mistakes = demo.getConsecutiveMistakes();
        int score = demo.getScoreEarned();
        int accuracy = demo.getAccuracyPercent();
        int expected = Math.min(demo.getPlayer().getMaxHp() - before,
                powerUps.getHealAmount(demo.getPlayer().getMaxHp(), demo.getDifficulty()));
        require(powerUps.use(demo, PowerUp.HEAL, demo.getQuestions().get(1)).applied() && demo.getPlayer().getHp() == before + expected
                        && demo.getCurrentStreak() == streak && demo.getConsecutiveMistakes() == mistakes
                        && demo.getScoreEarned() == score && demo.getAccuracyPercent() == accuracy,
                "Real Heal restores the described amount without changing score or counters");
        require(powerUps.use(demo, PowerUp.SHIELD, demo.getQuestions().get(1)).applied() && demo.isShieldArmed(), "Real Shield arms");
        require(powerUps.use(demo, PowerUp.TIME_STOP, demo.getQuestions().get(1)).applied() && demo.isQuestionTimerFrozen(), "Real Time Stop freezes");
        for (PowerUp powerUp : PowerUp.values()) {
            require(!tutorial.canUsePowerUp(powerUp) && !powerUps.use(demo, powerUp, demo.getQuestions().get(1)).applied(),
                    "Power-up cannot be reused: " + powerUp);
        }
    }

    private static void require(boolean ok, String message) {
        if (!ok) {
            throw new AssertionError(message);
        }
        checks++;
    }
}
