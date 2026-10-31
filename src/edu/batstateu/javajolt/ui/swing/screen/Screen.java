package edu.batstateu.javajolt.ui.swing.screen;

import edu.batstateu.javajolt.ui.swing.Theme;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public abstract class Screen extends JPanel {
    protected Screen() {
        super(new BorderLayout(0, Theme.GAP_L));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(Theme.GAP_XL, Theme.GAP_XL, Theme.GAP_XL, Theme.GAP_XL));
    }
}
