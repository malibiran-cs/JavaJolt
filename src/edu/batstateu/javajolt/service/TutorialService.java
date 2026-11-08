package edu.batstateu.javajolt.service;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.TutorialContent;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.PowerUpResult;
import edu.batstateu.javajolt.entity.TutorialStep;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.enums.TutorialTrigger;
import edu.batstateu.javajolt.game.GameSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// One throwaway run of the single tutorial; never receives a real player or a save service.
public class TutorialService {
    // Intermediate makes a timeout visibly cost HP.
    private static final Difficulty DEMO_DIFFICULTY = Difficulty.INTERMEDIATE;
    // The sample uses the real Variables opponent for the demo difficulty.
    private static final Topic DEMO_TOPIC = Topic.VARIABLES;
    // Only the timer-demo question uses this shortened limit.
    private static final int DEMO_TIMER_SECONDS = 3;

    private final TutorialContent content;
    private final LessonRunnerService lessons;
    private final AnswerTimerService timer;
    private final PowerUpService powerUps;
    private final Set<Integer> shownSteps = new HashSet<>();
    private GameSession session;
    private List<TutorialStep> steps = List.of();
    private boolean questionActive;
    private boolean healPrepared;

    public TutorialService(TutorialContent content, LessonRunnerService lessons,
            AnswerTimerService timer, PowerUpService powerUps) {
        this.content = content;
        this.lessons = lessons;
        this.timer = timer;
        this.powerUps = powerUps;
    }

    public void startRun() {
        int highestUnlock = 1;
        for (PowerUp powerUp : PowerUp.values()) {
            highestUnlock = Math.max(highestUnlock, powerUp.getUnlockLevel());
        }
        Player demo = new Player("Practice", Math.toIntExact(LevelService.totalXpForLevel(highestUnlock)));
        session = lessons.startSession(demo, DEMO_TOPIC, DEMO_DIFFICULTY,
                content.createQuestions(DEMO_TOPIC, DEMO_DIFFICULTY));
        shownSteps.clear();
        questionActive = false;
        healPrepared = false;
        steps = content.createSteps(session, getTimeLimitSeconds(content.getTimerQuestionIndex()));
    }

    public List<TutorialStep> getStepsDue(TutorialTrigger trigger, int questionIndex) {
        requireRun();
        if (trigger == TutorialTrigger.RESULTS && !isFinished()) {
            return List.of();
        }
        if (trigger == TutorialTrigger.BEFORE_QUESTION
                && (!questionActive || questionIndex != session.getCurrentQuestionIndex())) {
            return List.of();
        }
        List<TutorialStep> due = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            TutorialStep step = steps.get(i);
            if (step.trigger() == trigger && step.questionIndex() == questionIndex && shownSteps.add(i)) {
                due.add(step);
            }
        }
        return List.copyOf(due);
    }

    public void beginQuestion(int questionIndex) {
        requireRun();
        if (questionActive && questionIndex == session.getCurrentQuestionIndex()) {
            return;
        }
        if (isFinished() || questionIndex != session.getAnsweredCount() + 1) {
            throw new IllegalStateException("Continue the current tutorial before starting another question.");
        }
        session.setCurrentQuestionIndex(questionIndex);
        questionActive = true;
        if (questionIndex == content.getHealQuestionIndex() && !healPrepared) {
            prepareHeal();
        }
    }

    private void prepareHeal() {
        Player demo = session.getPlayer();
        int recovery = powerUps.getHealAmount(demo.getMaxHp(), session.getDifficulty());
        // Script setup leaves the sample alive; the normal Player damage method still owns HP changes.
        demo.takeDamage(Math.min(recovery, Math.max(0, demo.getHp() - 1)));
        healPrepared = true;
    }

    public boolean submitAnswer(String answer) {
        requireActiveQuestion();
        boolean correct = lessons.submitAnswer(session, currentQuestion(), answer);
        questionActive = false;
        return correct;
    }

    public void applyTimeout() {
        requireRun();
        if (questionActive) {
            timer.applyTimeout(session);
            if (!session.getPlayer().isAlive()) {
                questionActive = false;
            }
        }
    }

    public boolean canUsePowerUp(PowerUp powerUp) {
        requireRun();
        return questionActive && session.getCurrentQuestionIndex() >= content.getHealQuestionIndex()
                && powerUps.canUse(session, powerUp)
                && powerUps.isApplicable(powerUp, session, currentQuestion());
    }

    public PowerUpResult usePowerUp(PowerUp powerUp) {
        requireActiveQuestion();
        if (session.getCurrentQuestionIndex() < content.getHealQuestionIndex()) {
            return new PowerUpResult(false, "Power-ups will become available at their practice step.", List.of());
        }
        return powerUps.use(session, powerUp, currentQuestion());
    }

    public int getTimeLimitSeconds(int questionIndex) {
        requireRun();
        Question question = session.getQuestions().get(questionIndex - 1);
        int seconds = timer.getTimeLimitSeconds(session.getDifficulty(), question.getInputMode());
        return questionIndex == content.getTimerQuestionIndex() ? Math.min(DEMO_TIMER_SECONDS, seconds) : seconds;
    }

    public GameSession getSession() {
        requireRun();
        return session;
    }

    public int getTotalStepCount() {
        return steps.size();
    }

    public int getShownStepCount() {
        return shownSteps.size();
    }

    public boolean isFinished() {
        requireRun();
        return !session.getPlayer().isAlive() || session.getAnsweredCount() == session.getQuestions().size();
    }

    private Question currentQuestion() {
        return session.getQuestions().get(session.getCurrentQuestionIndex() - 1);
    }

    private void requireRun() {
        if (session == null) {
            throw new IllegalStateException("Start the tutorial first.");
        }
    }

    private void requireActiveQuestion() {
        requireRun();
        if (!questionActive || !session.getPlayer().isAlive()) {
            throw new IllegalStateException("There is no active tutorial question.");
        }
    }
}
