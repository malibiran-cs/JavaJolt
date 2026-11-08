package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.UiAssets;
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

    public MascotView() {
        setOpaque(false);
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
        int y = (getHeight() - height) / 2;

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        g2.drawImage(image, x, y, width, height, null);
        g2.dispose();
    }
}
