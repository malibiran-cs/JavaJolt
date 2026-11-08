import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.service.AnswerTimerService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.ui.GameUI;
import edu.batstateu.javajolt.ui.swing.MainWindow;
import edu.batstateu.javajolt.ui.swing.ScreenId;
import edu.batstateu.javajolt.ui.swing.screen.TopicSelectionScreen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/** Launches real Swing views with a temporary save location. No real saves are touched. */
public class SwingNavigationSmokeCheck {
    private static MainWindow window;
    private static int passed;

    public static void main(String[] args) throws Exception {
        Path tempFolder = Files.createTempDirectory("javajolt-navigation-");
        try {
            SwingUtilities.invokeAndWait(() -> {
                Player player = new Player("Test Player", 0);
                QuestionService questions = new QuestionService(new QuestionBank());
                LessonRunnerService runner = new LessonRunnerService(
                        questions, new LessonService(), new EnemyRoster(),
                        new ScoreService(), new GameUI());
                window = new MainWindow(player, questions, runner, Difficulty.BEGINNER,
                        new PowerUpService(), new AnswerTimerService(),
                        new SaveService(tempFolder.resolve("player.properties")));
                window.showScreen(ScreenId.WELCOME);
                window.setVisible(true);

                assertTrue(findButton(window, "GET STARTED") != null,
                        "Welcome screen opens with assets");
                JTextComponent nameInput = findEditable(window);
                assertTrue(nameInput != null, "Welcome screen name input exists");
                nameInput.setText("Smoke Test Player");
                findButton(window, "GET STARTED").doClick();

                assertTrue(findButton(window, "Explore Lessons") != null,
                        "Main Menu opens after welcome");
                findButton(window, "Explore Lessons").doClick();

                TopicSelectionScreen topics = findScreen(window, TopicSelectionScreen.class);
                assertTrue(topics != null && topics.isShowing(), "Topic Selection opens");
                Component center = ((BorderLayout) topics.getLayout()).getLayoutComponent(
                        BorderLayout.CENTER);
                assertTrue(center instanceof JScrollPane && center.isShowing(),
                        "Topic cards remain inside the scroll pane");
                AbstractButton variables = findButton(window, Topic.VARIABLES.getLabel());
                assertTrue(variables != null && variables.isEnabled(),
                        "Available Variables topic can be opened");
                variables.doClick();
                assertTrue(findButton(window, "Start Quiz") != null,
                        "Textbook opens with its quiz action");
                findButton(window, "Start Quiz").doClick();
                assertTrue(findButton(window, "Submit answer") != null,
                        "Question screen opens with quiz controls");
                window.showScreen(ScreenId.MAIN_MENU);
                assertTrue(findButton(window, "Achievements") != null,
                        "Returning to the Main Menu works");
                findButton(window, "Achievements").doClick();
                JComboBox<?> filter = findCombo(window);
                assertTrue(filter != null && filter.getItemCount() == 3,
                        "Achievement gallery opens with three filters");
                filter.setSelectedItem("Locked");
                assertTrue("Locked".equals(filter.getSelectedItem()),
                        "Achievement filter switches to Locked");
                findButton(window, "Back to Main Menu").doClick();
                assertTrue(findButton(window, "Explore Lessons") != null,
                        "Achievement gallery returns to the Main Menu");
                window.dispose();
            });
            System.out.println("ALL SWING NAVIGATION CHECKS PASSED (" + passed + ")");
        } finally {
            if (window != null) SwingUtilities.invokeAndWait(() -> window.dispose());
            Files.deleteIfExists(tempFolder.resolve("player.properties"));
            Files.deleteIfExists(tempFolder);
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        passed++;
        System.out.println("PASS: " + label);
    }

    private static AbstractButton findButton(Component component, String name) {
        if (!component.isShowing()) return null;
        if (component instanceof AbstractButton button) {
            String buttonText = button.getText();
            if (name.equals(buttonText) || hasLabel(button, name)) return button;
        }
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                AbstractButton result = findButton(child, name);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static boolean hasLabel(Component component, String name) {
        if (component instanceof JLabel label && name.equals(label.getText())) return true;
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                if (hasLabel(child, name)) return true;
            }
        }
        return false;
    }

    private static JTextComponent findEditable(Component component) {
        if (!component.isShowing()) return null;
        if (component instanceof JTextComponent text && text.isEditable()) return text;
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                JTextComponent result = findEditable(child);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static JComboBox<?> findCombo(Component component) {
        if (!component.isShowing()) return null;
        if (component instanceof JComboBox<?> combo) return combo;
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                JComboBox<?> result = findCombo(child);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static <T extends Component> T findScreen(Component component, Class<T> type) {
        if (type.isInstance(component) && component.isShowing()) return type.cast(component);
        if (component instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                T result = findScreen(child, type);
                if (result != null) return result;
            }
        }
        return null;
    }
}
