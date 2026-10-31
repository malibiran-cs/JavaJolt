package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.ui.swing.screen.MainMenuScreen;
import edu.batstateu.javajolt.ui.swing.screen.WelcomeScreen;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import edu.batstateu.javajolt.entity.Player; // Imports the Player class to access the player's information.

public class MainWindow extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);

  // Receives the Player object created or loaded by GameService so the GUI can access the player's information.
    public MainWindow(Player player)  {
        super("JavaJolt");

        container.add(new WelcomeScreen(), ScreenId.WELCOME.name());
        container.add(new MainMenuScreen(), ScreenId.MAIN_MENU.name());

        setContentPane(container);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 520));
        setSize(960, 600);
        setLocationRelativeTo(null);
    }

    public void showScreen(ScreenId id) {
        cards.show(container, id.name());
    }
}
