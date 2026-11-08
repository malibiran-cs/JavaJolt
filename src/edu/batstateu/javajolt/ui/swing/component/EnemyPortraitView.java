package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.NinePatch;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * Pixel-sharp, framed enemy portrait. Each topic has one sprite shared across
 * Beginner, Intermediate, and Advanced; battle rules remain independent.
 */
public final class EnemyPortraitView extends JComponent {
    private static final NinePatch FRAME =
            UiAssets.patch("panels/panel_inset", 13, 13, 13, 13);

    private Topic topic = Topic.VARIABLES;

    public EnemyPortraitView() {
        setOpaque(false);
    }

    public void setTopic(Topic topic) {
        if (topic == null) {
            throw new IllegalArgumentException("Enemy topic cannot be null");
        }
        this.topic = topic;
        setToolTipText(topic.getLabel() + " enemy");
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        try {
            FRAME.paint(g2, getWidth(), getHeight());
            // Keep more portrait space than the silhouette placeholder uses.
            int pad = Math.max(6, Math.min(getWidth(), getHeight()) / 16);
            int availableWidth = getWidth() - pad * 2;
            int availableHeight = getHeight() - pad * 2;
            if (availableWidth <= 0 || availableHeight <= 0) {
                return;
            }

            BufferedImage image = UiAssets.image(assetFor(topic));
            double scale = Math.min(
                    availableWidth / (double) image.getWidth(),
                    availableHeight / (double) image.getHeight());
            int drawWidth = Math.max(1, (int) Math.round(image.getWidth() * scale));
            int drawHeight = Math.max(1, (int) Math.round(image.getHeight() * scale));
            int x = (getWidth() - drawWidth) / 2;
            int y = (getHeight() - drawHeight) / 2;

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(image, x, y, drawWidth, drawHeight, null);
        } finally {
            g2.dispose();
        }
    }

    private static String assetFor(Topic topic) {
        return switch (topic) {
            case VARIABLES -> "enemies/enemy_variables";
            case OPERATORS -> "enemies/enemy_operators";
            case DECISIONS -> "enemies/enemy_decisions";
            case LOOPS -> "enemies/enemy_loops";
            case ARRAYS -> "enemies/enemy_arrays";
            case EXCEPTION_HANDLING -> "enemies/enemy_exception";
            case OOP -> "enemies/enemy_oop";
        };
    }
}
