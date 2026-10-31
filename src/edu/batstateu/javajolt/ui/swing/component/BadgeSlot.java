package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 * An empty badge space drawn with the kit's star icon.
 * The art is a 48x48 design, shown here at 64x64; the 2x source still has
 * enough pixels to stay sharp on high-DPI screens.
 */
public class BadgeSlot extends JComponent {
    private static final int SIZE = 64;

    public BadgeSlot() {
        setOpaque(false);
        setPreferredSize(new Dimension(SIZE, SIZE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(UiAssets.image("items/star_empty"), 0, 0, getWidth(), getHeight(), null);
        g2.dispose();
    }
}
