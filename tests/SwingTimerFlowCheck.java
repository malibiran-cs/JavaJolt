import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.data.TutorialContent;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.InputMode;
import edu.batstateu.javajolt.enums.JoltMessage;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.AnswerTimerService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.service.TutorialService;
import edu.batstateu.javajolt.ui.swing.MainWindow;
import edu.batstateu.javajolt.ui.swing.ScreenId;
import edu.batstateu.javajolt.ui.swing.TutorialCoordinator;
import edu.batstateu.javajolt.ui.swing.component.TutorialPopup;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

// Headful EDT integration check: actual navigation, editors, timer events,
// background answer checking, and settlement; all saves are temporary.
public class SwingTimerFlowCheck {
    private static MainWindow window;
    private static Player player;
    private static RecordingRunner runner;
    private static JTextComponent editor;
    private static int checks;
    private static TutorialService tutorial;
    private static Path savePath;

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--tutorial")) {
            open(Difficulty.BEGINNER);
            onEdt(() -> window.showScreen(ScreenId.MAIN_MENU));
            SwingUtilities.invokeLater(() -> window.startTutorial(false));
            return;
        }
        try {
            open(Difficulty.ADVANCED);
            onEdt(() -> {
                require(!button(window, "Heal").isEnabled(), "Heal disabled at full HP");
                JRadioButton choice = (JRadioButton) button(window, "A. answer");
                choice.doClick();
            });
            await(() -> runner.last.isQuestionTimedOut());
            onEdt(() -> {
                require(((JRadioButton) button(window, "A. answer")).isSelected(), "timeout preserves choice");
                require(player.getHp() == 12 && runner.last.getAnsweredCount() == 0,
                        "timeout applies damage without answering");
                require(button(window, "Submit answer").isEnabled(), "submission remains available");
                require(!button(window, "Time Stop").isEnabled(), "Time Stop disabled after expiry");
                button(window, "Submit answer").doClick();
            });
            await(() -> runner.last.getAnsweredCount() == 1 && showingButton("Continue"));
            onEdt(() -> {
                AbstractButton next = button(window, "Continue");
                next.doClick();
                next.doClick();
            });
            onEdt(() -> {
                require(runner.last.getCurrentQuestionIndex() == 2 && !runner.last.isQuestionTimedOut(),
                        "transition resets timeout; duplicate Continue cannot skip");
                editor = editable(window);
                editor.setText("answer kept during freeze");
                button(window, "Time Stop").doClick();
            });
            Thread.sleep(1400);
            onEdt(() -> {
                require(runner.last.isQuestionTimerFrozen() && !runner.last.isQuestionTimedOut()
                        && player.getHp() == 12, "Time Stop freezes beyond deadline without damage");
                require(editor == editable(window) && editor.getText().equals("answer kept during freeze"),
                        "freeze preserves identification editor");
                require(hasLabel(window, "TIME: 1 s (frozen)"), "remaining time stays frozen");
                button(window, "Submit answer").doClick();
            });
            await(() -> runner.last.getAnsweredCount() == 2 && showingButton("Continue"));
            onEdt(() -> {
                button(window, "Continue").doClick();
                require(!runner.last.isQuestionTimerFrozen(), "next question resumes countdown");
                require(!button(window, "Time Stop").isEnabled(), "Time Stop remains used this quiz");
                editor = editable(window);
                editor.setText("System.out.println(42);");
                button(window, "Shield").doClick();
            });
            await(() -> runner.last.isQuestionTimedOut());
            onEdt(() -> {
                require(player.getHp() == 12 && !runner.last.isShieldArmed(), "Shield absorbs code timeout");
                require(editor == editable(window) && editor.getText().equals("System.out.println(42);"),
                        "timeout preserves code editor and typed code");
                require(runner.last.getCurrentStreak() == 2 && runner.last.getAccuracyPercent() == 100,
                        "timeout preserves correct streak and accuracy");
                button(window, "Submit answer").doClick();
            });
            await(() -> runner.last.getAnsweredCount() == 3 && showingButton("Continue"));
            onEdt(() -> {
                button(window, "Continue").doClick();
                player.setHp(3);
            });
            await(() -> runner.last.isSettled());
            onEdt(() -> {
                require(player.getHp() == 0 && runner.last.getMistakeCount() == 0
                        && runner.last.getAnsweredCount() == 3 && runner.last.getCurrentStreak() == 3,
                        "timeout defeat records no incorrect answer");
                require(runner.settlements == 1 && player.getScore() == 36 && player.getXp() == 1000,
                        "timeout defeat settles score once with zero XP");
                require(hasLabel(window, "KEEP PRACTICING!"), "timeout defeat reaches results");
            });
            Thread.sleep(1200);
            onEdt(() -> {
                require(runner.settlements == 1, "completion has no later timer callback");
                window.dispose();
            });

            open(Difficulty.ADVANCED);
            onEdt(() -> button(window, "A. answer").doClick());
            // Checking deliberately takes longer than the injected countdown.
            onEdt(() -> runner.questions.get(0).slow = true);
            onEdt(() -> button(window, "Submit answer").doClick());
            await(() -> runner.last.getAnsweredCount() == 1 && showingButton("Continue"));
            onEdt(() -> require(player.getHp() == 15 && !runner.last.isQuestionTimedOut(),
                    "countdown stops before slow background answer processing"));
            onEdt(() -> button(window, "Continue").doClick());
            GameSession abandoned = runner.last;
            onEdt(() -> window.showScreen(ScreenId.TOPIC_SELECTION));
            Thread.sleep(1300);
            onEdt(() -> {
                require(player.getHp() == 15 && !abandoned.isQuestionTimedOut() && !abandoned.isSettled()
                        && player.getScore() == 0, "navigation abandons timer and provisional rewards");
                startQuiz();
                require(runner.last != abandoned && !runner.last.isQuestionTimerFrozen(),
                        "replacement session has fresh timer state");
                window.dispose();
            });
            Thread.sleep(1300);
            onEdt(() -> require(player.getHp() == 15 && !runner.last.isQuestionTimedOut(),
                    "disposed window cannot expire"));

            open(Difficulty.BEGINNER);
            onEdt(() -> button(window, "Shield").doClick());
            await(() -> runner.last.isQuestionTimedOut());
            onEdt(() -> require(player.getHp() == 15 && runner.last.isShieldArmed(),
                    "Beginner expiry preserves Shield"));
            onEdt(() -> window.dispose());

            open(Difficulty.INTERMEDIATE);
            onEdt(() -> button(window, "A. answer").doClick());
            await(() -> runner.last.isQuestionTimedOut());
            onEdt(() -> {
                require(player.getHp() == 14 && button(window, "Heal").isEnabled(),
                        "Heal becomes available after timeout damage");
                button(window, "Heal").doClick();
                require(player.getHp() == 15 && hasLabel(window, "HP: 15/15")
                                && hasLabel(window, "Heal restored 1 HP."),
                        "Heal refreshes HP and reports capped recovery");
                require(((JRadioButton) button(window, "A. answer")).isSelected()
                                && button(window, "Submit answer").isEnabled(),
                        "Heal preserves answer input and submission");
                require(!button(window, "Heal").isEnabled(), "Heal disabled after use");
            });
            onEdt(() -> window.dispose());
            checkTutorial();
            System.out.println("ALL SWING TIMER FLOW CHECKS PASSED (" + checks + ")");
        } finally {
            onEdt(() -> {
                for (Window open : Window.getWindows()) {
                    open.dispose();
                }
            });
        }
    }

    private static void open(Difficulty difficulty) throws Exception {
        Path temp = Files.createTempDirectory("javajolt-timer-flow-");
        temp.toFile().deleteOnExit();
        Path save = temp.resolve("player.properties");
        savePath = save;
        save.toFile().deleteOnExit();
        player = new Player("Timer flow", 1000);
        SaveService saves = new SaveService(save);
        saves.save(player, difficulty);
        onEdt(() -> {
            QuestionService questions = new QuestionService(new QuestionBank()) {
                @Override
                public List<Question> selectQuestions(Topic topic, Difficulty level) {
                    return List.of(new FlowQuestion(1, false, true), new FlowQuestion(2, false, false),
                            new FlowQuestion(3, true, false), new FlowQuestion(4, false, false));
                }
            };
            ScoreService scores = new ScoreService();
            runner = new RecordingRunner(questions, scores);
            AnswerTimerService shortTimer = new AnswerTimerService() {
                @Override
                public int getTimeLimitSeconds(Difficulty level, InputMode mode) {
                    return 1;
                }
            };
            PowerUpService powers = new PowerUpService();
            tutorial = new TutorialService(new TutorialContent(runner, scores, shortTimer, powers),
                    runner, shortTimer, powers);
            window = new MainWindow(player, questions, runner, difficulty,
                    powers, shortTimer, saves, new TutorialCoordinator(tutorial));
            window.showScreen(ScreenId.MAIN_MENU);
            window.setVisible(true);
            button(window, "Explore Lessons").doClick();
            startQuiz();
        });
    }

    private static void checkTutorial() throws Exception {
        open(Difficulty.BEGINNER);
        onEdt(() -> {
            window.showScreen(ScreenId.MAIN_MENU);
            // A dead real player proves guards and display use the tutorial's own player.
            player.setHp(0);
        });
        byte[] savedBefore = Files.readAllBytes(savePath);
        SwingUtilities.invokeLater(() -> window.startTutorial(false));
        await(() -> tutorialPopup() != null);
        GameSession demo = tutorial.getSession();
        Thread.sleep(1400);
        onEdt(() -> {
            TutorialPopup popup = tutorialPopup();
            require(popup.isModal() && hasLabel(popup, "Step 1 of 14"), "tutorial popup is modal and numbers its steps");
            require(button(popup, "Exit tutorial") == null && button(popup, "Skip") == null,
                    "first-run popup has no skip or replay exit");
            require(!demo.isQuestionTimedOut() && demo.getPlayer().getHp() == 15
                            && hasLabel(window, "TIME: 1 s") && demo.getUsedPowerUps().isEmpty(),
                    "reading past the deadline pauses the shared timer without consuming Time Stop");
            button(window, "A. int count = 3;").doClick();
            button(window, "Submit answer").doClick();
            require(demo.getAnsweredCount() == 0 && runner.submissions == 0,
                    "underlying submit callbacks are blocked while a popup is open");
        });
        dismissTutorialSteps(8);
        await(() -> tutorialPopup() == null);
        await(demo::isQuestionTimedOut);
        onEdt(() -> {
            require(demo.getPlayer().getHp() == 14 && player.getHp() == 0,
                    "resumed tutorial countdown uses the shared timeout path on the demo player");
            button(window, "Submit answer").doClick();
        });
        await(() -> demo.getAnsweredCount() == 1 && showingButton("Continue"));
        SwingUtilities.invokeLater(() -> button(window, "Continue").doClick());
        await(() -> tutorialPopup() != null);
        Thread.sleep(1400);
        onEdt(() -> {
            require(hasLabel(tutorialPopup(), "Step 9 of 14") && demo.getPlayer().getHp() == 9,
                    "next question uses the same index path and performs Heal setup before popups");
            button(window, "Heal").doClick();
            require(!demo.hasUsed(PowerUp.HEAL) && demo.getPlayer().getHp() == 9 && !demo.isQuestionTimedOut(),
                    "popup blocks power-up callbacks and pauses the new countdown too");
        });
        dismissTutorialSteps(4);
        await(() -> tutorialPopup() == null);
        onEdt(() -> {
            button(window, "Time Stop").doClick();
            button(window, "Heal").doClick();
            button(window, "Hint").doClick();
            button(window, "Shield").doClick();
            require(demo.getPlayer().getHp() == 14 && demo.isQuestionTimerFrozen()
                            && demo.isShieldArmed() && demo.getUsedPowerUps().size() == 4,
                    "tutorial power-ups execute through the existing shared handler");
            button(window, "B. 4").doClick();
            button(window, "Submit answer").doClick();
        });
        await(() -> demo.getAnsweredCount() == 2 && showingButton("Continue"));
        SwingUtilities.invokeLater(() -> button(window, "Continue").doClick());
        await(() -> tutorialPopup() != null && hasLabel(tutorialPopup(), "Build a Jolt"));
        onEdt(() -> require(hasLabel(tutorialPopup(), "Step 13 of 14")
                        && demo.getCurrentQuestionIndex() == 3,
                "Jolt teaching popup appears before question three"));
        dismissTutorialSteps(1);
        await(() -> tutorialPopup() == null);
        onEdt(() -> {
            button(window, "C. boolean").doClick();
            button(window, "Submit answer").doClick();
        });
        await(() -> demo.getAnsweredCount() == 3 && showingButton("Continue"));
        onEdt(() -> {
            require(demo.getLastJoltMessage() == JoltMessage.SPARK
                            && hasLabel(window, "SPARK!") && hasLabel(window, "JOLT: x3   (+14 points)"),
                    "tutorial correct answers display SPARK and the unchanged score suffix");
            button(window, "Continue").doClick();
            require(hasLabel(window, "SPARK!"), "milestone persists into the next question");
            button(window, "D. 5").doClick();
            button(window, "Submit answer").doClick();
        });
        await(() -> demo.getAnsweredCount() == 4 && showingButton("Continue"));
        onEdt(() -> {
            require(demo.getLastJoltMessage() == JoltMessage.SPARK
                            && hasLabel(window, "SPARK!") && hasLabel(window, "JOLT: x4   (+16 points)"),
                    "SPARK persists above its threshold with the unchanged score progression");
            button(window, "Continue").doClick();
            button(window, "B. int = count 4;").doClick();
            button(window, "Submit answer").doClick();
        });
        await(() -> demo.getAnsweredCount() == 5 && showingButton("Continue"));
        onEdt(() -> require(demo.getLastJoltMessage() == JoltMessage.SHORT_CIRCUIT
                        && hasLabel(window, "SHORT CIRCUIT!") && hasLabel(window, "JOLT: x0"),
                "tutorial mistake displays SHORT CIRCUIT through the shared answer path"));
        SwingUtilities.invokeLater(() -> button(window, "Continue").doClick());
        await(() -> tutorialPopup() != null && hasLabel(tutorialPopup(), "You're ready!"));
        onEdt(() -> {
            require(hasLabel(tutorialPopup(), "Step 14 of 14") && runner.submissions == 5
                            && runner.settlements == 0 && !demo.isSettled(),
                    "full tutorial reaches wrap-up through shared submission without settlement");
            require(!player.isTutorialCompleted(), "wrap-up must be confirmed before setting completion");
            button(tutorialPopup(), "Got it").doClick();
        });
        await(() -> tutorialPopup() == null && showingButton("Explore Lessons"));
        require(new SaveService(savePath).load().tutorialCompleted(), "confirmed first run saves completion immediately");
        onEdt(() -> require(player.getXp() == 1000 && player.getScore() == 0 && player.getHp() == 0
                        && player.isTutorialCompleted() && player.copyHighScores().isEmpty()
                        && player.getProgress().copyCompleted().isEmpty(),
                "first completion changes only the real tutorial flag"));
        savedBefore = Files.readAllBytes(savePath);
        onEdt(() -> player.setTutorialCompleted(false));

        SwingUtilities.invokeLater(() -> window.startTutorial(true));
        await(() -> tutorialPopup() != null);
        GameSession abandoned = tutorial.getSession();
        onEdt(() -> button(tutorialPopup(), "Exit tutorial").doClick());
        await(() -> tutorialPopup() == null && showingButton("Explore Lessons"));
        Thread.sleep(1400);
        onEdt(() -> require(!abandoned.isQuestionTimedOut() && !abandoned.isSettled() && runner.settlements == 0,
                "replay exit clears timer and session without a later callback"));
        require(Arrays.equals(savedBefore, Files.readAllBytes(savePath)), "replay exit does not re-save");
        onEdt(() -> require(!player.isTutorialCompleted(), "replay never sets an incomplete player's flag"));
        onEdt(() -> {
            button(window, "Explore Lessons").doClick();
            startQuiz();
            require(runner.last != abandoned && runner.last.getCurrentQuestionIndex() == 1
                            && runner.last.getAnsweredCount() == 0 && player.getHp() == 15,
                    "normal quiz after tutorial starts clean on the real player");
            window.showScreen(ScreenId.MAIN_MENU);
        });

        SwingUtilities.invokeLater(() -> window.startTutorial(false));
        await(() -> tutorialPopup() != null);
        SwingUtilities.invokeLater(() -> window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING)));
        await(() -> exitDialog() != null);
        onEdt(() -> button(exitDialog(), "No").doClick());
        await(() -> exitDialog() == null && tutorialPopup() != null);
        onEdt(() -> require(window.isDisplayable() && !player.isTutorialCompleted()
                        && !tutorial.getSession().isQuestionTimedOut(), "declining exit preserves the paused, incomplete tutorial"));
        SwingUtilities.invokeLater(() -> tutorialPopup().dispatchEvent(
                new WindowEvent(tutorialPopup(), WindowEvent.WINDOW_CLOSING)));
        await(() -> exitDialog() != null);
        onEdt(() -> button(exitDialog(), "Yes").doClick());
        await(() -> !window.isDisplayable() && tutorialPopup() == null);
        require(!new SaveService(savePath).load().tutorialCompleted() && runner.settlements == 0,
                "closing mid-tutorial follows exit confirmation and saves no completion or demo rewards");

        open(Difficulty.BEGINNER);
        onEdt(() -> window.showScreen(ScreenId.MAIN_MENU));
        byte[] replayBefore = Files.readAllBytes(savePath);
        SwingUtilities.invokeLater(() -> window.startTutorial(true));
        await(() -> tutorialPopup() != null);
        GameSession replaced = tutorial.getSession();
        SwingUtilities.invokeLater(() -> window.startTutorial(true));
        await(() -> tutorial.getSession() != replaced && tutorialPopup() != null);
        onEdt(() -> require(hasLabel(tutorialPopup(), "Step 1 of 14") && !replaced.isSettled(),
                "replacement run is protected from the old modal stack"));
        GameSession navigated = tutorial.getSession();
        onEdt(() -> window.showScreen(ScreenId.TOPIC_SELECTION));
        onEdt(() -> require(tutorialPopup() != null && tutorial.getSession() == navigated,
                "topic navigation cannot escape an active tutorial"));
        onEdt(() -> button(tutorialPopup(), "Exit tutorial").doClick());
        await(() -> tutorialPopup() == null && showingButton("Explore Lessons"));
        Thread.sleep(1400);
        onEdt(() -> require(!navigated.isQuestionTimedOut() && !navigated.isSettled(),
                "explicit replay exit returns to a clean menu after blocked navigation"));
        SwingUtilities.invokeLater(() -> window.startTutorial(true));
        await(() -> tutorialPopup() != null);
        SwingUtilities.invokeLater(() -> window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING)));
        await(() -> exitDialog() != null);
        onEdt(() -> button(exitDialog(), "Yes").doClick());
        await(() -> !window.isDisplayable() && tutorialPopup() == null);
        require(Arrays.equals(replayBefore, Files.readAllBytes(savePath)), "application exit during replay does not re-save");
    }

    private static void dismissTutorialSteps(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            await(() -> tutorialPopup() != null);
            onEdt(() -> button(tutorialPopup(), "Got it").doClick());
        }
    }

    private static TutorialPopup tutorialPopup() {
        for (Window open : Window.getWindows()) {
            if (open instanceof TutorialPopup popup && popup.isShowing()) return popup;
        }
        return null;
    }

    private static JDialog exitDialog() {
        for (Window open : Window.getWindows()) {
            if (open instanceof JDialog dialog && dialog.isShowing() && dialog.getTitle().equals("Exit")) return dialog;
        }
        return null;
    }

    private static void startQuiz() {
        button(window, Topic.VARIABLES.getLabel()).doClick();
        button(window, "Start Quiz").doClick();
    }

    private static AbstractButton button(Component component, String text) {
        if (!component.isShowing()) {
            return null;
        }
        if (component instanceof AbstractButton candidate
                && (text.equals(candidate.getText())
                        || (candidate.getText() != null
                                && candidate.getText().replaceAll("<[^>]*>", "").equals(text))
                        || hasLabel(candidate, text))) {
            return candidate;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                AbstractButton found = button(child, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean hasLabel(Component component, String text) {
        if (!component.isShowing()) {
            return false;
        }
        if (component instanceof JLabel label && text.equals(label.getText())) {
            return true;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                if (hasLabel(child, text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static JTextComponent editable(Component component) {
        if (component.isShowing() && component instanceof JTextComponent text && text.isEditable()) {
            return text;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                JTextComponent found = editable(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean showingButton(String text) {
        return button(window, text) != null;
    }

    private static void await(BooleanSupplier condition) throws Exception {
        long deadline = System.nanoTime() + 8_000_000_000L;
        boolean[] ready = {false};
        while (!ready[0] && System.nanoTime() < deadline) {
            onEdt(() -> ready[0] = condition.getAsBoolean());
            Thread.sleep(25);
        }
        if (!ready[0]) {
            throw new AssertionError("Timed out waiting for Swing flow");
        }
    }

    private static void onEdt(Runnable action) throws Exception {
        SwingUtilities.invokeAndWait(action);
    }

    private static void require(boolean ok, String message) {
        if (!ok) {
            throw new AssertionError(message);
        }
        checks++;
        System.out.println("PASS: " + message);
    }

    private static class RecordingRunner extends LessonRunnerService {
        GameSession last;
        List<FlowQuestion> questions;
        int settlements;
        int submissions;

        RecordingRunner(QuestionService questionService, ScoreService scores) {
            super(questionService, new LessonService(), new EnemyRoster(), scores, null);
        }

        @Override
        public GameSession startSession(Player player, Topic topic, Difficulty difficulty, List<Question> scripted) {
            last = super.startSession(player, topic, difficulty, scripted);
            return last;
        }

        @Override
        public boolean submitAnswer(GameSession session, Question question, String answer) {
            submissions++;
            return super.submitAnswer(session, question, answer);
        }

        @Override
        public GameSession startSession(Player player, Topic topic, Difficulty difficulty) {
            last = super.startSession(player, topic, difficulty);
            // The fixture owns these questions; no production question type is inspected.
            questions = last.getQuestions().stream().map(question -> (FlowQuestion) question).toList();
            return last;
        }

        @Override
        public boolean settleAttempt(GameSession session) {
            settlements++;
            return super.settleAttempt(session);
        }
    }

    private static class FlowQuestion extends Question {
        private final boolean code;
        private final boolean choices;
        volatile boolean slow;

        FlowQuestion(int id, boolean code, boolean choices) {
            super(id, "Timer flow question", Topic.VARIABLES, Difficulty.ADVANCED, "Test feedback");
            this.code = code;
            this.choices = choices;
        }

        @Override
        public InputMode getInputMode() {
            return code ? InputMode.MULTI_LINE : InputMode.SINGLE_LINE;
        }

        @Override
        public List<String> getChoices() {
            return choices ? List.of("A. answer", "B. other") : List.of();
        }

        @Override
        public boolean isCorrect(String answer) {
            if (slow) {
                try {
                    Thread.sleep(1400);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted test answer", e);
                }
            }
            return !answer.isBlank();
        }

        @Override
        public String display() {
            return getQuestion();
        }
    }
}
