package edu.batstateu.javajolt.ui.swing;

import javax.swing.SwingUtilities;

// Temporary launcher so the UI can be viewed without touching GameApplication.
public class SwingPreview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.showScreen(ScreenId.WELCOME); // change to ScreenId.MAIN_MENU to preview the other screen
            window.setVisible(true);
        });
    }
}
