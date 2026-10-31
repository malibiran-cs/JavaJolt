package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.NinePatch;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;

/**
 * Reserved space for the learner character, drawn on an inset kit panel.
 * Replace the silhouette with the character image when the art exists.
 */
public class CharacterPlaceholder extends JComponent {
    private static final NinePatch FRAME = UiAssets.patch("panels/panel_inset", 13, 13, 13, 13);

    public CharacterPlaceholder() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        FRAME.paint(g2, getWidth(), getHeight());

        int pad = Theme.GAP_L;
        int innerWidth = getWidth() - pad * 2;
        int innerHeight = getHeight() - pad * 2;
        if (innerWidth <= 0 || innerHeight <= 0) {
            g2.dispose();
            return;
        }

        double size = Math.min(innerWidth, innerHeight);
        double headSize = size * 0.38;
        double bodyWidth = size * 0.92;
        double bodyHeight = size * 0.90;
        double centerX = getWidth() / 2.0;
        double headY = pad + innerHeight * 0.08;

        Ellipse2D head = new Ellipse2D.Double(centerX - headSize / 2, headY, headSize, headSize);
        Ellipse2D body = new Ellipse2D.Double(centerX - bodyWidth / 2,
                headY + headSize + size * 0.05, bodyWidth, bodyHeight * 2);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(pad, pad, innerWidth, innerHeight);
        g2.setStroke(new BasicStroke(2f));
        for (Ellipse2D shape : new Ellipse2D[] { body, head }) {
            g2.setColor(Theme.INSET_BORDER);
            g2.fill(shape);
            g2.setColor(Theme.SECONDARY_HOVER);
            g2.draw(shape);
        }
        g2.dispose();
    }
}
