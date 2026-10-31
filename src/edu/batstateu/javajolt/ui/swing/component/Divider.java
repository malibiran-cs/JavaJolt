package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.NinePatch;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * A thin horizontal rule drawn with the kit's divider image.
 * Only the left and right margins are protected, so the line stretches freely.
 */
public class Divider extends JComponent {
    private static final NinePatch PATCH = UiAssets.patch("panels/divider", 4, 0, 4, 0);

    public Divider() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 4));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        PATCH.paint(g2, getWidth(), getHeight());
        g2.dispose();
    }
}
