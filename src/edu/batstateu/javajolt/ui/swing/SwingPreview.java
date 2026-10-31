package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.entity.Player;
import javax.swing.SwingUtilities;

// Temporary launcher so the UI can be viewed without touching GameApplication.
public class SwingPreview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(new Player("Preview", 0));
            window.setVisible(true);
        });
    }
}
