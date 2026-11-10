package edu.batstateu.javajolt.ui.swing.component;

import javax.swing.Timer;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Color;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.JComponent;

public final class MascotView extends JComponent {

    public enum Mood {
        ONLINE("online"),
        CHARGED("charged"),
        THINKING("thinking"),
        LOW_BATTERY("low_battery"),
        UPGRADE("upgrade"),
        REBOOTING("rebooting");

        private final String fileName;

        Mood(String fileName) {
            this.fileName = fileName;
        }
    }

    private Mood mood = Mood.ONLINE;


    private Color glowColor = null;

    private final Timer glowTimer = new Timer(600, event -> {
        glowColor = null;
        repaint();
    });

    public void flashGlow(boolean correct) {
        glowColor = correct
                ? new Color(80, 255, 130)  // Green
                : new Color(255, 65, 85);  // Red

        glowTimer.restart();
        repaint();
    }



    private int idleTick = 0;


    private int shakeFramesRemaining = 0;

    private final Timer shakeTimer = new Timer(45, event -> {

        shakeFramesRemaining--;

        if (shakeFramesRemaining <= 0) {
            shakeFramesRemaining = 0;
            ((Timer) event.getSource()).stop();
        }

        repaint();
    });

    public void shakeDamage() {
        shakeFramesRemaining = 8;
        shakeTimer.restart();
        repaint();
    }


    private final Timer idleTimer = new Timer(80, event -> {
        if (isShowing()) {
            idleTick++;
            repaint();
        }
    });


    public MascotView() {
        setOpaque(false);
        glowTimer.setRepeats(false);
    }


    @Override
    public void addNotify() {
        super.addNotify();
        idleTimer.start();
    }

    @Override
    public void removeNotify() {
        idleTimer.stop();
        shakeTimer.stop();
        shakeFramesRemaining = 0;
        super.removeNotify();
    }


    public void setMood(Mood mood) {
        this.mood = Objects.requireNonNull(mood);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        BufferedImage image;

        try {
            image = UiAssets.image("mascot/" + mood.fileName);
        } catch (IllegalStateException missingMood) {
            if (mood == Mood.ONLINE) {
                throw missingMood;
            }

            image = UiAssets.image("mascot/online");
        }
        int usableWidth = Math.max(1, getWidth() - 16);
        int usableHeight = Math.max(1, getHeight() - 16);
        double scale = Math.min(
                usableWidth / (double) image.getWidth(),
                usableHeight / (double) image.getHeight()
        );
        int width = Math.max(1, (int) Math.round(image.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(image.getHeight() * scale));


        int x = (getWidth() - width) / 2;

        // Shake horizontally when the player takes damage.
        if (shakeFramesRemaining > 0) {
            x += (shakeFramesRemaining % 2 == 0) ? 4 : -4;
        }


        int y = (getHeight() - height) / 2
        + (int) Math.round(
                Math.sin(idleTick * 0.20) * 3
        );

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

g2.drawImage(image, x, y, width, height, null);

// Temporary green or red glow.
if (glowColor != null) {

    float centerX = getWidth() / 2f;
    float centerY = getHeight() / 2f;

    float radius = Math.max(
            1f,
            Math.min(getWidth(), getHeight()) * 0.48f
    );

    int red = glowColor.getRed();
    int green = glowColor.getGreen();
    int blue = glowColor.getBlue();

    g2.setPaint(new RadialGradientPaint(
            new Point2D.Float(centerX, centerY),
            radius,
            new float[]{0f, 0.55f, 1f},
            new Color[]{
                    new Color(red, green, blue, 15),
                    new Color(red, green, blue, 110),
                    new Color(red, green, blue, 0)
            }
    ));

    g2.fillOval(
            Math.round(centerX - radius),
            Math.round(centerY - radius),
            Math.round(radius * 2),
            Math.round(radius * 2)
    );
}

g2.dispose();

    }
}
