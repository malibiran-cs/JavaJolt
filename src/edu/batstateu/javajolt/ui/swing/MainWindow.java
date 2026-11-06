
package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.enums.Achievement;
import java.util.ArrayList;
import java.util.EnumSet;
import edu.batstateu.javajolt.ui.swing.screen.AchievementsScreen;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.LessonFileException;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.DifficultyProgressionService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.LevelService;
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
    // No real save is created before the new player confirms a name.
    private boolean welcomeCompleted;

    public MainWindow(
            Player player,
            QuestionService questionService,
            LessonRunnerService lessonRunnerService,
            Difficulty difficulty) {

        this(
                player,
                questionService,
                lessonRunnerService,
                difficulty,
                new SaveService()
        );
    }

    public MainWindow(
            Player player,
            QuestionService questionService,
            LessonRunnerService lessonRunnerService,
            Difficulty difficulty,
            SaveService saveService) {
        super("JavaJolt");
        this.player = player;
        this.questionService = questionService;
        this.lessonRunnerService = lessonRunnerService;
        this.difficulty = difficulty;
        this.saveService = saveService;
        this.welcomeCompleted = saveService.hasSave();

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
                () -> showScreen(ScreenId.TOPIC_SELECTION));
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

        // Remember XP before the quiz begins
        xpAtLessonStart = player.getXp();

                
        achievementsAtLessonStart.clear();

        for (Achievement achievement : Achievement.values()) {

            if (achievement.isUnlocked(player)) {
                achievementsAtLessonStart.add(achievement);
            }
        }


        // Reset player HP
        player.setHp(Player.getStartingHp());

        // Create the quiz session
        session = new GameSession(
                player,
                topic,
                lessonDifficulty,
                questionService.selectQuestions(
                        topic,
                        lessonDifficulty
                )
        );

        questionIndex = -1;
        lessonResultRecorded = false;

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
        if (session == null || questionIndex >= session.getQuestions().size()) return;
        edu.batstateu.javajolt.Question current = session.getQuestions().get(questionIndex);
        questionScreen.setCheckingAnswer(true);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return lessonRunnerService.submitAnswer(session, current, answer);
            }

            @Override
            protected void done() {
                try {
                questionScreen.showAnswerResult(
                        get(),
                        current.getExplanation(),
                        player,
                        session.getScoreEarned()
                );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    questionScreen.setCheckingAnswer(false);
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "The answer could not be checked.", "Question Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (ExecutionException e) {
                    questionScreen.setCheckingAnswer(false);
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "The answer could not be checked.", "Question Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }


    private void showNextQuestion() {

        if (session == null) {
            return;
        }

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

            // Record completed lessons and bonus XP
            lessonRunnerService.recordLessonCompletion(session);

            
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

            // Remove the previous results screen
            if (resultsScreen != null) {
                container.remove(resultsScreen);
            }

            // Create the new results screen
            resultsScreen = new ResultsScreen(
                    session,
                    passed,
                    xpEarned,

                    // TRY AGAIN
                    () -> startLesson(
                            completedTopic,
                            completedDifficulty
                    ),

                    // CHOOSE TOPIC
                    this::showTopicSelection,

                    // MAIN MENU
                    () -> showScreen(ScreenId.MAIN_MENU)
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

        questionScreen.showQuestion(
                session.getQuestions().get(questionIndex),
                questionIndex + 1,
                session.getQuestions().size(),
                player,
                session.getScoreEarned()
        );
    }


    public void showScreen(ScreenId id) {
        if (id == ScreenId.MAIN_MENU) {
            mainMenuScreen.refresh(player);
        }

        cards.show(container, id.name());
    }
}
