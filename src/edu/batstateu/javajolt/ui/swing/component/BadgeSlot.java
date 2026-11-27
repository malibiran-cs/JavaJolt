package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.enums.Achievement;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;

/** Displays the original pixel-art badge in color when earned, muted when locked. */
public final class BadgeSlot extends JComponent {
    private static final Map<Achievement, BufferedImage> LOCKED_IMAGES =
            new EnumMap<>(Achievement.class);

    private final Achievement achievement;
    private final int size;
    private boolean unlocked;

    public BadgeSlot(Achievement achievement, boolean unlocked, int size) {
        this.achievement = Objects.requireNonNull(achievement);
        if (size <= 0) {
            throw new IllegalArgumentException("Badge size must be positive");
        }
        this.size = size;
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setUnlocked(unlocked);
    }

    public static String assetPath(Achievement achievement) {
        return "achievements/" + achievement.name().toLowerCase(Locale.ROOT);
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
        setToolTipText(achievement.getTitle() + (unlocked ? " — Unlocked" : " — Locked"));
        repaint();
    }

    private static BufferedImage lockedImage(Achievement achievement) {
        return LOCKED_IMAGES.computeIfAbsent(achievement, key -> {
            BufferedImage source = UiAssets.image(assetPath(key));
            BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < source.getHeight(); y++) {
                for (int x = 0; x < source.getWidth(); x++) {
                    int pixel = source.getRGB(x, y);
                    int alpha = pixel >>> 24;
                    int red = (pixel >>> 16) & 255;
                    int green = (pixel >>> 8) & 255;
                    int blue = pixel & 255;
                    int gray = (red * 299 + green * 587 + blue * 114 + 500) / 1000;
                    result.setRGB(x, y, (alpha << 24) | (gray << 16) | (gray << 8) | gray);
                }
            }
            return result;
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage image = unlocked ? UiAssets.image(assetPath(achievement))
                : lockedImage(achievement);
        int available = Math.min(size, Math.min(getWidth(), getHeight()));
        if (available <= 0) return;
        double scale = Math.min((double) available / image.getWidth(),
                (double) available / image.getHeight());
        int width = Math.max(1, (int) Math.round(image.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(image.getHeight() * scale));
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            if (!unlocked) {
                g2.setComposite(AlphaComposite.SrcOver.derive(0.68f));
            }
            g2.drawImage(image, (getWidth() - width) / 2, (getHeight() - height) / 2,
                    width, height, null);
        } finally {
            g2.dispose();
        }
    }
}
