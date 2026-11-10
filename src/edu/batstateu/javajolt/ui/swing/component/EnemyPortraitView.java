package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.NinePatch;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Color;
import java.awt.BasicStroke;
import javax.swing.Timer;
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


    private int hitShakeFrames = 0;

    private final Timer hitShakeTimer = new Timer(45, event -> {

        hitShakeFrames--;

        if (hitShakeFrames <= 0) {
            hitShakeFrames = 0;
            ((Timer) event.getSource()).stop();
        }

        repaint();
    });

    private boolean hitFlash = false;

    private final Timer hitTimer = new Timer(450, event -> {
        hitFlash = false;
        repaint();
    });


    public void flashHit() {

        // Red hit flash.
        hitFlash = true;
        hitTimer.restart();

        // Horizontal damage shake.
        hitShakeFrames = 8;
        hitShakeTimer.restart();

        repaint();
    }



    public EnemyPortraitView() {
        setOpaque(false);
        hitTimer.setRepeats(false);
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
    public void removeNotify() {
        hitTimer.stop();
        hitShakeTimer.stop();
        hitFlash = false;
        hitShakeFrames = 0;
        super.removeNotify();
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


        // Shake the enemy horizontally when hit.
        if (hitShakeFrames > 0) {
            x += (hitShakeFrames % 2 == 0) ? 4 : -4;
        }


            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(image, x, y, drawWidth, drawHeight, null);


        // Brief red flash when the enemy is hit.
        if (hitFlash) {

            int inset = 4;
            int flashWidth = Math.max(1, getWidth() - inset * 2);
            int flashHeight = Math.max(1, getHeight() - inset * 2);

            // Translucent red overlay.
            g2.setColor(new Color(255, 60, 80, 65));

            g2.fillRoundRect(
                    inset, inset,
                    flashWidth, flashHeight,
                    16, 16
            );

            // Bright red outline.
            g2.setColor(new Color(255, 70, 90, 220));
            g2.setStroke(new BasicStroke(3f));

            g2.drawRoundRect(
                    inset, inset,
                    flashWidth, flashHeight,
                    16, 16
            );
        }

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
