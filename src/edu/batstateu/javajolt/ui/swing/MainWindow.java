
package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.screen.MainMenuScreen;
import edu.batstateu.javajolt.ui.swing.screen.TopicSelectionScreen;
import edu.batstateu.javajolt.ui.swing.screen.WelcomeScreen;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);

    private final Player player;

    public MainWindow(Player player) {
        super("JavaJolt");
        this.player = player;

        container.add(new WelcomeScreen(name -> {
            player.setName(name);
            showScreen(ScreenId.MAIN_MENU);
        }), ScreenId.WELCOME.name());

        container.add(
                new MainMenuScreen(player,
                        () -> showTopicSelection()),
                ScreenId.MAIN_MENU.name()
        );

        setContentPane(container);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 520));
        setSize(960, 600);
        setLocationRelativeTo(null);
    }

    private void showTopicSelection() {
        List<Topic> availableTopics =
                player.getAvailableTopics(Difficulty.BEGINNER);

        TopicSelectionScreen topicScreen =
                new TopicSelectionScreen(availableTopics, topic -> {
                    javax.swing.JOptionPane.showMessageDialog(
                            this,
                            "You selected: " + topic.getLabel(),
                            "Topic Selected",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE
                    );
                });

        container.add(topicScreen, ScreenId.TOPIC_SELECTION.name());
        showScreen(ScreenId.TOPIC_SELECTION);
    }

    public void showScreen(ScreenId id) {
        cards.show(container, id.name());
    }
}
