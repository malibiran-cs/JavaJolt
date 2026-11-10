
package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.ui.swing.screen.TitleScreen;
import edu.batstateu.javajolt.enums.Achievement;
import java.util.ArrayList;
import java.util.EnumSet;
import edu.batstateu.javajolt.ui.swing.screen.AchievementsScreen;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.entity.PowerUpResult;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.LessonFileException;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.DifficultyProgressionService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.LevelService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.ui.swing.screen.MainMenuScreen;
import edu.batstateu.javajolt.ui.swing.screen.QuestionScreen;
import edu.batstateu.javajolt.ui.swing.screen.TextbookScreen;
import edu.batstateu.javajolt.ui.swing.screen.TopicSelectionScreen;
import edu.batstateu.javajolt.ui.swing.screen.WelcomeScreen;
import edu.batstateu.javajolt.ui.swing.screen.ResultsScreen;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.util.concurrent.TimeUnit;
import edu.batstateu.javajolt.service.AnswerTimerService;

public class MainWindow extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);

    private final EnumSet<Achievement> achievementsAtLessonStart =
        EnumSet.noneOf(Achievement.class);
    private AchievementsScreen achievementsScreen;
    private final Player player;
    private final QuestionService questionService;
    private final LessonRunnerService lessonRunnerService;
    private Difficulty difficulty;
    private final SaveService saveService;
    private final PowerUpService powerUpService;
    private final AnswerTimerService answerTimerService;
    private static final int COUNTDOWN_TICK_MILLIS = 100;
    private ScreenId titleStartDestination = ScreenId.WELCOME;
    private Timer countdown;
    private long countdownVersion;
    private long countdownDeadline;
    private int remainingSeconds;
    private final MainMenuScreen mainMenuScreen;
    private boolean lessonResultRecorded;
    private final LessonService lessonService = new LessonService();
    private TextbookScreen textbookScreen;
    private GameSession session;
    private final QuestionScreen questionScreen;
    private TopicSelectionScreen topicScreen;
    private int questionIndex;
    private ResultsScreen resultsScreen;
    private int xpAtLessonStart;
    // Power-ups are only usable while an unanswered question is active;
    // submitAnswer flips this off and showNextQuestion flips it back on.
    private boolean questionActive;
    // No real save is created before the new player confirms a name.
    private boolean welcomeCompleted;

    public MainWindow(
            Player player,
            QuestionService questionService,
            LessonRunnerService lessonRunnerService,
            Difficulty difficulty,
            PowerUpService powerUpService,
            AnswerTimerService answerTimerService) {

        this(
                player,
                questionService,
                lessonRunnerService,
                difficulty,
                powerUpService,
                answerTimerService,
                new SaveService()
        );
    }

    public MainWindow(
            Player player,
            QuestionService questionService,
            LessonRunnerService lessonRunnerService,
            Difficulty difficulty,
            PowerUpService powerUpService,
            AnswerTimerService answerTimerService,
            SaveService saveService) {
        super("JavaJolt");
        this.player = player;
        this.questionService = questionService;
        this.lessonRunnerService = lessonRunnerService;
        this.difficulty = difficulty;
        this.powerUpService = powerUpService;
        this.answerTimerService = answerTimerService;
        this.saveService = saveService;
        this.welcomeCompleted = saveService.hasSave();


        container.add(
                new TitleScreen(
                        () -> showScreen(titleStartDestination),
                        this::confirmExit
                ),
                ScreenId.TITLE.name()
        );



        container.add(new WelcomeScreen(name -> {
            player.setName(name);
            welcomeCompleted = true;
            showScreen(ScreenId.MAIN_MENU);
        }), ScreenId.WELCOME.name());

        mainMenuScreen = new MainMenuScreen(
                player,
                this::showTopicSelection,
                this::saveWithFeedback,
                this::showProgress,
                this::showAchievements,
                this::confirmExit
        );

        container.add(
                mainMenuScreen,
                ScreenId.MAIN_MENU.name()
        );
        questionScreen = new QuestionScreen(this::submitAnswer, this::showNextQuestion,
                () -> showScreen(ScreenId.TOPIC_SELECTION), this::usePowerUp);
        container.add(questionScreen, ScreenId.QUESTION.name());

        setContentPane(container);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });


        setMinimumSize(new Dimension(900, 620));
        setSize(1100, 720);
        setLocationRelativeTo(null);
    }

    private void showTopicSelection() {
        List<Topic> availableTopics =
                player.getAvailableTopics(difficulty);

        if (topicScreen != null) container.remove(topicScreen);
        topicScreen = new TopicSelectionScreen(
                player,
                difficulty,
                availableTopics,
                this::showTextbook,
                () -> showScreen(ScreenId.MAIN_MENU)
        );

        container.add(topicScreen, ScreenId.TOPIC_SELECTION.name());
        showScreen(ScreenId.TOPIC_SELECTION);
    }

    
    private void showAchievements() {

        // Remove the previous gallery, if one exists.
        if (achievementsScreen != null) {
            container.remove(achievementsScreen);
        }

        // Create a fresh gallery with current player progress.
        achievementsScreen = new AchievementsScreen(
                player,
                () -> showScreen(ScreenId.MAIN_MENU)
        );

        container.add(
                achievementsScreen,
                ScreenId.ACHIEVEMENTS.name()
        );

        showScreen(ScreenId.ACHIEVEMENTS);

        container.revalidate();
        container.repaint();
    }


    private void showTextbook(Topic topic) {

        List<LessonPage> pages;

        try {
            pages = lessonService.loadPages(topic, difficulty);

        } catch (LessonFileException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Textbook Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Remove the previously opened textbook screen
        if (textbookScreen != null) {
            container.remove(textbookScreen);
        }

        // Create a fresh textbook screen for this topic
        textbookScreen = new TextbookScreen(
                topic,
                pages,
                () -> startLesson(topic),
                () -> showScreen(ScreenId.TOPIC_SELECTION)
        );

        // Register the new screen
        container.add(
                textbookScreen,
                ScreenId.TEXTBOOK.name()
        );

        // Display the textbook
        showScreen(ScreenId.TEXTBOOK);
    }



    private void startLesson(Topic topic) {
        startLesson(topic, difficulty);
    }

    private void startLesson(
            Topic topic,
            Difficulty lessonDifficulty) {

        stopCountdown();
        questionActive = false;
        // Remember XP before the quiz begins
        xpAtLessonStart = player.getXp();

                 
        achievementsAtLessonStart.clear();

        for (Achievement achievement : Achievement.values()) {

            if (achievement.isUnlocked(player)) {
                achievementsAtLessonStart.add(achievement);
            }
        }


        // Single start-of-quiz home: questions, level-derived max HP, full HP.
        session = lessonRunnerService.startSession(
                player,
                topic,
                lessonDifficulty
        );

        questionIndex = -1;
        lessonResultRecorded = false;
        questionActive = false;

        // Display the question screen first
        showScreen(ScreenId.QUESTION);

        // Load the first question
        showNextQuestion();
    }


    
private boolean saveProgress() {
    if (!welcomeCompleted) {
        return false;
    }
    try {
        saveService.save(player, difficulty);
        return true;

    } catch (SaveFileException e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE
        );
        return false;
    }
}

private void saveWithFeedback() {
    if (saveProgress()) {
        JOptionPane.showMessageDialog(
                this,
                "Progress saved successfully.",
                "Saved",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}

private void showProgress() {
    StringBuilder message = new StringBuilder()
            .append("Player: ").append(player.getName())
            .append("\nXP: ").append(player.getXp())
            .append("\nLevel: ")
            .append(LevelService.calculateLevel(player.getXp()))
            .append("\nCurrent difficulty: ")
            .append(difficulty.getLabel())
            .append("\n\nCompleted topics:\n");

    for (Difficulty level : Difficulty.values()) {
        int count = 0;

        for (Topic topic : Topic.values()) {
            if (player.isComplete(topic, level)) {
                count++;
            }
        }

        message.append(level.getLabel())
                .append(": ")
                .append(count)
                .append(" / ")
                .append(Topic.values().length)
                .append('\n');
    }

    JOptionPane.showMessageDialog(
            this,
            message.toString(),
            "Player Progress",
            JOptionPane.INFORMATION_MESSAGE
    );
}

private void confirmExit() {
    if (!welcomeCompleted) {
        dispose();
        return;
    }
    int choice = JOptionPane.showConfirmDialog(
            this,
            "Save progress and exit JavaJolt?",
            "Exit",
            JOptionPane.YES_NO_OPTION
    );

    if (choice != JOptionPane.YES_OPTION) {
        return;
    }

    if (saveProgress()) {
        dispose();
    }
}


    private void submitAnswer(String answer) {
        if (session == null || !questionActive || !player.isAlive()
                || questionIndex < 0 || questionIndex >= session.getQuestions().size()) return;
        stopCountdown();
        GameSession answerSession = session;
        // No power-up may be used once an answer was submitted: this flag turns
        // off before checking and stays off through the result and transitions.
        questionActive = false;
        edu.batstateu.javajolt.Question current = session.getQuestions().get(questionIndex);
        questionScreen.setCheckingAnswer(true);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return lessonRunnerService.submitAnswer(answerSession, current, answer);
            }

            @Override
            protected void done() {
                if (session != answerSession || !isDisplayable()
                        || !questionScreen.isVisible()) {
                    return;
                }
                try {
                questionScreen.showAnswerResult(
                        get(),
                        current.getExplanation(),
                        player,
                        player.getXp() + session.getProvisionalXp(),
                        session.getScoreEarned(),
                        session.getCurrentStreak(),
                        session.getLastPoints(),
                        session.isLastAnswerBoosted()
                );
                refreshBattleInfo(false);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    questionScreen.setCheckingAnswer(false);
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "The answer could not be checked.", "Question Error",
                            JOptionPane.ERROR_MESSAGE);
                    restoreQuestionAfterCheckFailure();
                } catch (ExecutionException e) {
                    questionScreen.setCheckingAnswer(false);
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "The answer could not be checked.", "Question Error",
                            JOptionPane.ERROR_MESSAGE);
                    restoreQuestionAfterCheckFailure();
                }
            }
        }.execute();
    }


    private void showNextQuestion() {
        if (session == null || !questionScreen.isVisible() || lessonResultRecorded
                || (questionActive && player.isAlive())) {
            return;
        }
        stopCountdown();
        questionActive = false;

        questionIndex++;

        // Check whether the lesson is finished
        if (questionIndex >= session.getQuestions().size()
                || !player.isAlive()) {

            // Prevent recording rewards twice
            if (lessonResultRecorded) {
                return;
            }

            lessonResultRecorded = true;

            boolean passed =
                    lessonRunnerService.hasPassed(session);

            Difficulty oldDifficulty = difficulty;

            // Settle the finished attempt exactly once: score always commits;
            // first-clear XP plus the completion bonus only for a passed uncleared pair.
            lessonRunnerService.settleAttempt(session);

            
            // Check for newly unlocked achievements
            List<Achievement> newlyUnlocked = new ArrayList<>();

            for (Achievement achievement : Achievement.values()) {

                boolean unlockedNow =
                        achievement.isUnlocked(player);

                boolean wasUnlocked =
                        achievementsAtLessonStart.contains(achievement);

                if (unlockedNow && !wasUnlocked) {
                    newlyUnlocked.add(achievement);
                }
            }


            // Keep the best score for this topic and difficulty for future records.
            player.recordHighScore(
                    session.getTopic(),
                    session.getDifficulty(),
                    session.getScoreEarned()
            );

            // Calculate XP earned during this session
            int xpEarned = Math.max(
                    0,
                    player.getXp() - xpAtLessonStart
            );

            // Update difficulty progression
            difficulty =
                    DifficultyProgressionService
                            .currentDifficulty(player);

            // Refresh Main Menu statistics
            mainMenuScreen.refresh(player);

            // Save player progress
            if (!saveProgress()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Your lesson results could not be saved.",
                        "Save Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            Topic completedTopic = session.getTopic();
            Difficulty completedDifficulty =
                    session.getDifficulty();

            // Level-up / power-up notices: the same before/after pattern as the
            // achievements above. unlockedBetween covers the (old, new] level
            // interval, so crossing several thresholds reports all of them.
            int levelAtStart = LevelService.calculateLevel(xpAtLessonStart);
            int levelAtEnd = LevelService.calculateLevel(player.getXp());
            String progressNotice = buildProgressNotice(
                    levelAtStart,
                    levelAtEnd,
                    powerUpService.unlockedBetween(levelAtStart, levelAtEnd)
            );

            // Remove the previous results screen
            if (resultsScreen != null) {
                container.remove(resultsScreen);
            }

            // Create the new results screen
            resultsScreen = new ResultsScreen(
            session,
            passed,
            xpEarned,
                session.getQuestions().isEmpty()
                    ? 0
                    : Math.min(
                        100,
                        session.getScoreEarned() * 100
                            / session.getQuestions().size()
                    ),
                    // TRY AGAIN
                    () -> startLesson(
                            completedTopic,
                            completedDifficulty
                    ),

                    // CHOOSE TOPIC
                    this::showTopicSelection,

                    // MAIN MENU
                    () -> showScreen(ScreenId.MAIN_MENU),

                    progressNotice
            );

            // Register and display the screen
            container.add(
                    resultsScreen,
                    ScreenId.RESULTS.name()
            );

            showScreen(ScreenId.RESULTS);

            
            // Notify the player about newly earned achievements
            if (!newlyUnlocked.isEmpty()) {

                StringBuilder message = new StringBuilder(
                        newlyUnlocked.size() == 1
                                ? "Achievement unlocked!\n\n"
                                : "New achievements unlocked!\n\n"
                );

                for (Achievement achievement : newlyUnlocked) {

                    message.append("\u2605  ")
                            .append(achievement.getTitle())
                            .append("\n")
                            .append(achievement.getDescription())
                            .append("\n\n");
                }

                JOptionPane.showMessageDialog(
                        this,
                        message.toString().trim(),
                        "New Achievement",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }


            // Notify player of a new difficulty
            if (passed && difficulty != oldDifficulty) {
                JOptionPane.showMessageDialog(
                        this,
                        difficulty.getLabel()
                                + " difficulty unlocked!",
                        "New Difficulty",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            return;
        }

        // Continue displaying questions
        session.setCurrentQuestionIndex(
                questionIndex + 1
        );

        // The new question is active: power-ups may be used again (the
        // controller re-enables them; Hint visuals are per-question, since the
        // answer editor is rebuilt above).
        questionActive = true;

        questionScreen.showQuestion(
                session.getQuestions().get(questionIndex),
                questionIndex + 1,
                session.getQuestions().size(),
                player,
                player.getXp() + session.getProvisionalXp(),
                session.getScoreEarned(),
                session.getCurrentStreak(),
                session.getLastPoints(),
                session.isLastAnswerBoosted()
        );
        refreshBattleInfo(true);
        startCountdown(answerTimerService.getTimeLimitSeconds(session.getDifficulty(),
                session.getQuestions().get(questionIndex).getInputMode()));
    }

    private void restoreQuestionAfterCheckFailure() {
        questionActive = true;
        refreshBattleInfo(true);
        if (session.isQuestionTimerRunning()) {
            startCountdown(remainingSeconds);
        }
    }

    private void startCountdown(int seconds) {
        stopCountdown();
        remainingSeconds = seconds;
        countdownDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(remainingSeconds);
        questionScreen.showCountdown(remainingSeconds, false);
        long version = countdownVersion;
        GameSession timedSession = session;
        int timedIndex = questionIndex;
        countdown = new Timer(COUNTDOWN_TICK_MILLIS, event -> {
            if (version != countdownVersion || event.getSource() != countdown
                    || session != timedSession || questionIndex != timedIndex || !questionActive) {
                return;
            }
            long nanosLeft = Math.max(0, countdownDeadline - System.nanoTime());
            remainingSeconds = (int) ((nanosLeft + TimeUnit.SECONDS.toNanos(1) - 1)
                    / TimeUnit.SECONDS.toNanos(1));
            questionScreen.showCountdown(remainingSeconds, false);
            if (remainingSeconds == 0) {
                stopCountdown();
                handleTimeout();
            }
        });
        countdown.start();
    }

    private void stopCountdown() {
        countdownVersion++;
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    private void handleTimeout() {
        if (!questionActive || session == null) {
            return;
        }
        answerTimerService.applyTimeout(session);
        if (!player.isAlive()) {
            // Reuse the existing completion path without recording an answer.
            showNextQuestion();
            return;
        }
        questionScreen.showTimeout(player);
        refreshBattleInfo(true);
    }

    @Override
    public void dispose() {
        stopCountdown();
        questionActive = false;
        super.dispose();
    }

    // Applies one proposed power-up use. The flag guard enforces the amendment
    // here (not only in the buttons): no use after submit, during transitions,
    // or after the quiz. PowerUpService stays authoritative for every rule.
    private void usePowerUp(PowerUp powerUp) {
        if (session == null
                || questionIndex < 0
                || questionIndex >= session.getQuestions().size()
                || !questionActive) {
            return;
        }

        edu.batstateu.javajolt.Question current =
                session.getQuestions().get(questionIndex);
        PowerUpResult result =
                powerUpService.use(session, powerUp, current);

        if (result.applied() && powerUp == PowerUp.TIME_STOP) {
            stopCountdown();
            questionScreen.showCountdown(remainingSeconds, true);
        }
        if (result.applied() && !result.removedChoices().isEmpty()) {
            questionScreen.applyHintRemovedChoices(result.removedChoices());
        }
        questionScreen.showPowerUpNotice(result.message());
        refreshBattleInfo(true);
    }

    // Builds the view-only battle meta from session state and pushes it to the
    // screen. "Used" and "not applicable" fold into one disabled set; armed
    // state uses read-only probes; nothing here has side effects on rules.
    private void refreshBattleInfo(boolean questionActive) {
        EnumSet<PowerUp> unlocked = EnumSet.noneOf(PowerUp.class);
        EnumSet<PowerUp> disabled = session.getUsedPowerUps();
        EnumSet<PowerUp> armed = EnumSet.noneOf(PowerUp.class);

        edu.batstateu.javajolt.Question current = questionIndex >= 0
                && questionIndex < session.getQuestions().size()
                ? session.getQuestions().get(questionIndex)
                : null;

        for (PowerUp powerUp : PowerUp.values()) {
            if (powerUpService.isUnlocked(player, powerUp)) {
                unlocked.add(powerUp);
            }
            if (session.hasUsed(powerUp)
                    || !powerUpService.isApplicable(powerUp, session, current)) {
                disabled.add(powerUp);
            }
            if (powerUp == PowerUp.SHIELD && session.isShieldArmed()) {
                armed.add(powerUp);
            }
            if (powerUp == PowerUp.POINT_BOOST && session.isPointBoostArmed()) {
                armed.add(powerUp);
            }
        }

        String enemyName = session.getEnemy() == null
                ? ""
                : session.getEnemy().getName();
        questionScreen.showBattleMeta(
                session.getTopic(),
                enemyName,
                unlocked,
                disabled,
                armed,
                questionActive
        );
    }

    // End-of-quiz progress notice: the level reached and/or the power-ups that
    // unlocked in the (old, new] interval. Blank when nothing changed.
    private String buildProgressNotice(
            int levelAtStart,
            int levelAtEnd,
            List<PowerUp> newPowerUps) {

        boolean leveledUp = levelAtEnd > levelAtStart;
        boolean unlocked = newPowerUps != null && !newPowerUps.isEmpty();
        if (!leveledUp && !unlocked) {
            return null; // nothing to announce; the results screen stays as-is
        }

        StringBuilder notice = new StringBuilder();
        if (leveledUp) {
            notice.append("New level reached: ").append(levelAtEnd).append("!");
        }
        if (unlocked) {
            if (notice.length() > 0) {
                notice.append(' ');
            }
            if (newPowerUps.size() == 1) {
                notice.append("New power-up unlocked: ")
                        .append(newPowerUps.get(0).getLabel()).append('!');
            } else {
                notice.append("New power-ups unlocked: ");
                for (int i = 0; i < newPowerUps.size(); i++) {
                    if (i > 0) {
                        notice.append(", ");
                    }
                    notice.append(newPowerUps.get(i).getLabel());
                }
                notice.append('!');
            }
        }
        return notice.toString();
    }

    
public void setTitleStartDestination(ScreenId destination) {

    if (destination != ScreenId.WELCOME
            && destination != ScreenId.MAIN_MENU) {

        throw new IllegalArgumentException(
                "Invalid title destination: " + destination
        );
    }

    this.titleStartDestination = destination;
}


    public void showScreen(ScreenId id) {
        if (id != ScreenId.QUESTION) {
            stopCountdown();
            questionActive = false;
        }
        if (id == ScreenId.MAIN_MENU) {
            mainMenuScreen.refresh(player);
        }

        cards.show(container, id.name());
    }
}
