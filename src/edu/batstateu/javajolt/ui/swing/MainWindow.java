
package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.ui.swing.screen.MainMenuScreen;
import edu.batstateu.javajolt.ui.swing.screen.QuestionScreen;
import edu.batstateu.javajolt.ui.swing.screen.TopicSelectionScreen;
import edu.batstateu.javajolt.ui.swing.screen.WelcomeScreen;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class MainWindow extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);

    private final Player player;
    private final QuestionService questionService;
    private final LessonRunnerService lessonRunnerService;
    private final Difficulty difficulty;
    private GameSession session;
    private final QuestionScreen questionScreen;
    private TopicSelectionScreen topicScreen;
    private int questionIndex;

    public MainWindow(Player player, QuestionService questionService,
            LessonRunnerService lessonRunnerService, Difficulty difficulty) {
        super("JavaJolt");
        this.player = player;
        this.questionService = questionService;
        this.lessonRunnerService = lessonRunnerService;
        this.difficulty = difficulty;

        container.add(new WelcomeScreen(name -> {
            player.setName(name);
            showScreen(ScreenId.MAIN_MENU);
        }), ScreenId.WELCOME.name());

        container.add(
                new MainMenuScreen(player,
                        () -> showTopicSelection()),
                ScreenId.MAIN_MENU.name()
        );
        questionScreen = new QuestionScreen(this::submitAnswer, this::showNextQuestion,
                () -> showScreen(ScreenId.TOPIC_SELECTION));
        container.add(questionScreen, ScreenId.QUESTION.name());

        setContentPane(container);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 520));
        setSize(960, 600);
        setLocationRelativeTo(null);
    }

    private void showTopicSelection() {
        List<Topic> availableTopics =
                player.getAvailableTopics(difficulty);

        if (topicScreen != null) container.remove(topicScreen);
        topicScreen = new TopicSelectionScreen(availableTopics, topic -> {
                    startLesson(topic);
                });

        container.add(topicScreen, ScreenId.TOPIC_SELECTION.name());
        showScreen(ScreenId.TOPIC_SELECTION);
    }

    private void startLesson(Topic topic) {
        player.setHp(Player.getStartingHp());
        session = new GameSession(player, topic, difficulty,
                questionService.selectQuestions(topic, difficulty));
        questionIndex = -1;
        showNextQuestion();
        showScreen(ScreenId.QUESTION);
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
                    questionScreen.showAnswerResult(get(), current.getExplanation(), player);
                } catch (Exception e) {
                    questionScreen.setCheckingAnswer(false);
                    javax.swing.JOptionPane.showMessageDialog(MainWindow.this,
                            "The answer could not be checked.", "Question Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showNextQuestion() {
        if (session == null) return;
        questionIndex++;
        if (questionIndex >= session.getQuestions().size() || !player.isAlive()) {
            lessonRunnerService.recordLessonCompletion(session);
            questionScreen.showLessonResult(session, lessonRunnerService.hasPassed(session));
            return;
        }
        session.setCurrentQuestionIndex(questionIndex + 1);
        questionScreen.showQuestion(session.getQuestions().get(questionIndex),
                questionIndex + 1, session.getQuestions().size(), player);
    }

    public void showScreen(ScreenId id) {
        cards.show(container, id.name());
    }
}
