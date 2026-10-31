package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.ui.swing.screen.MainMenuScreen;
import edu.batstateu.javajolt.ui.swing.screen.WelcomeScreen;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);

    // Player is handed in by the caller so the screens can be populated once they are wired.
    public MainWindow(Player player) {
        super("JavaJolt");

        container.add(new WelcomeScreen(name -> {
            player.setName(name);
            showScreen("MAIN_MENU");
        }), "WELCOME");

        container.add(new MainMenuScreen(player, () -> {
        }), "MAIN_MENU");

        setContentPane(container);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 520));
        setSize(960, 600);
        setLocationRelativeTo(null);
    }

    public void showScreen(String id) {
        cards.show(container, id);
    }
}
