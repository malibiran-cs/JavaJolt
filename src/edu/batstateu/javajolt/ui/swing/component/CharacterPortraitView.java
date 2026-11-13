package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Shared pixel-art renderer for player and enemy characters.
 *
 * <p>Changing emotions selects one of the supplied Aseprite exports. A short
 * green/red glow and a damage shake supplement the artwork without changing
 * quiz mechanics. Timers run on Swing's event-dispatch thread.</p>
 */
public abstract class CharacterPortraitView extends JComponent {
    private static final int FLASH_MILLIS = 600;
    private static final int SHAKE_FRAMES = 8;
    private static final int SHAKE_FRAME_MILLIS = 45;
    private static final int IDLE_FRAME_MILLIS = 80;

    private final boolean floating;
    private CharacterEmotion emotion = CharacterEmotion.IDLE;
    private Color glowColor;
    private int shakeFramesRemaining;
    private int idleTick;

    private final Timer idleTimer = new Timer(IDLE_FRAME_MILLIS, event -> {
        if (isShowing()) {
            idleTick++;
            repaint();
        }
    });

    private final Timer glowTimer = new Timer(FLASH_MILLIS, event -> {
        glowColor = null;
        repaint();
    });

    private final Timer shakeTimer = new Timer(SHAKE_FRAME_MILLIS, event -> {
        if (--shakeFramesRemaining <= 0) {
            shakeFramesRemaining = 0;
            shakeTimerStop();
        }
        repaint();
    });

    protected CharacterPortraitView(boolean floating) {
        this.floating = floating;
        setOpaque(false);
        glowTimer.setRepeats(false);
    }

    protected abstract String spritePath(CharacterEmotion emotion);

    /** Hook for a decorative frame; used by enemy portraits only. */
    protected void paintFrame(Graphics2D graphics) {
    }

    public final CharacterEmotion getEmotion() {
        return emotion;
    }

    /** Normal state when a new question begins or in non-battle screens. */
    public final void showIdle() {
        emotion = CharacterEmotion.IDLE;
        glowTimer.stop();
        shakeTimer.stop();
        shakeFramesRemaining = 0;
        glowColor = null;
        repaint();
    }

    /** Success state with green glow, no shake. */
    public final void showCorrect() {
        emotion = CharacterEmotion.CORRECT;
        shakeTimer.stop();
        shakeFramesRemaining = 0;
        flash(new Color(80, 255, 130));
    }

    /** Hurt state with red glow and a short sideways shake. */
    public final void showHurt() {
        emotion = CharacterEmotion.HURT;
        shakeFramesRemaining = SHAKE_FRAMES;
        shakeTimer.restart();
        flash(new Color(255, 65, 85));
    }

    private void flash(Color color) {
        glowColor = color;
        glowTimer.restart();
        repaint();
    }

    private void shakeTimerStop() {
        shakeTimer.stop();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (floating) {
            idleTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        idleTimer.stop();
        glowTimer.stop();
        shakeTimer.stop();
        glowColor = null;
        shakeFramesRemaining = 0;
        super.removeNotify();
    }

    @Override
    protected final void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        try {
            paintFrame(g2);

            BufferedImage sprite = UiAssets.image(spritePath(emotion));
            int padding = Math.max(6, Math.min(getWidth(), getHeight()) / 18);
            int availableWidth = Math.max(1, getWidth() - padding * 2);
            int availableHeight = Math.max(1, getHeight() - padding * 2);
            double scale = Math.min(availableWidth / (double) sprite.getWidth(),
                    availableHeight / (double) sprite.getHeight());
            int drawWidth = Math.max(1, (int) Math.round(sprite.getWidth() * scale));
            int drawHeight = Math.max(1, (int) Math.round(sprite.getHeight() * scale));
            int x = (getWidth() - drawWidth) / 2;
            int y = (getHeight() - drawHeight) / 2;

            if (shakeFramesRemaining > 0) {
                x += (shakeFramesRemaining % 2 == 0) ? 4 : -4;
            }
            if (floating) {
                y += (int) Math.round(Math.sin(idleTick * 0.20) * 3);
            }

            // Behind the transparent character: a soft green/red signal light.
            if (glowColor != null) {
                drawGlow(g2, x, y, drawWidth, drawHeight, glowColor);
            }

            // Pixel art must never use bilinear interpolation.
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(sprite, x, y, drawWidth, drawHeight, null);
        } finally {
            g2.dispose();
        }
    }

    private static void drawGlow(Graphics2D graphics, int x, int y,
            int width, int height, Color glowColor) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float radius = Math.max(1f, Math.max(width, height) * 0.65f);
        graphics.setPaint(new RadialGradientPaint(
                new Point2D.Float(centerX, centerY),
                radius,
                new float[]{0f, 0.50f, 1f},
                new Color[]{
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 115),
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 75),
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 0)
                }
        ));
        graphics.fillOval(Math.round(centerX - radius), Math.round(centerY - radius),
                Math.round(radius * 2), Math.round(radius * 2));
    }
}
