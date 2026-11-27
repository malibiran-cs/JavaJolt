import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Achievement;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import edu.batstateu.javajolt.ui.swing.component.BadgeSlot;
import edu.batstateu.javajolt.ui.swing.screen.AchievementsScreen;
import edu.batstateu.javajolt.ui.swing.screen.MainMenuScreen;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

/** Verifies all supplied artwork, badge states, gallery filters, and dashboard refresh. */
public final class AchievementBadgeIntegrationCheck {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var paths = new HashSet<String>();
            var imageHashes = new HashSet<Integer>();
            for (Achievement achievement : Achievement.values()) {
                String path = BadgeSlot.assetPath(achievement);
                require(paths.add(path), "Duplicate asset mapping: " + path);
                BufferedImage image = UiAssets.image(path);
                require(image.getWidth() == 110 && image.getHeight() == 110,
                        "Incorrect source canvas: " + path);
                int[] pixels = image.getRGB(0, 0, 110, 110, null, 0, 110);
                require(imageHashes.add(java.util.Arrays.hashCode(pixels)), "Duplicate art: " + path);
                require(java.util.Arrays.stream(pixels).anyMatch(p -> (p >>> 24) == 0), "Missing transparency");
                BadgeSlot badge = new BadgeSlot(achievement, false, 100);
                badge.setSize(100, 100);
                BufferedImage locked = render(badge);
                require(!hasColor(locked), "Locked badge should be grayscale: " + achievement);
                badge.setUnlocked(true);
                BufferedImage earned = render(badge);
                require(hasColor(earned), "Unlocked badge should show original color: " + achievement);
                require(badge.isUnlocked(), "Badge did not update");
            }
            for (boolean light : new boolean[]{false, true}) {
                Theme.setLightMode(light);
                Player player = new Player("Badge test", 100);
                player.complete(Topic.VARIABLES, Difficulty.BEGINNER);
                AchievementsScreen gallery = new AchievementsScreen(player, () -> {});
                checkGallery(gallery, player, "All");
                checkGallery(gallery, player, "Unlocked");
                checkGallery(gallery, player, "Locked");
                checkGallery(gallery, player, "All");
                MainMenuScreen menu = new MainMenuScreen(player, () -> {}, () -> {}, () -> {},
                        () -> {}, () -> {}, () -> {}, () -> {});
                checkDashboard(menu, player);
                for (Topic topic : Topic.values()) player.complete(topic, Difficulty.BEGINNER);
                menu.refresh(player);
                checkDashboard(menu, player);
                player.addXp(900);
                for (Difficulty difficulty : Difficulty.values()) {
                    for (Topic topic : Topic.values()) player.complete(topic, difficulty);
                }
                AchievementsScreen complete = new AchievementsScreen(player, () -> {});
                checkGallery(complete, player, "All");
                checkGallery(complete, player, "Locked");
                checkGallery(complete, player, "Unlocked");
            }
            Theme.setLightMode(false);
        });
        System.out.println("PASS: All 10 achievement badges, original artwork, locked/unlocked states, filters and dashboard refresh in both themes.");
    }

    private static void checkGallery(AchievementsScreen gallery, Player player, String filter) {
        List<JComboBox> combos = find(gallery, JComboBox.class);
        require(combos.size() == 1, "Gallery filter missing");
        combos.get(0).setSelectedItem(filter);
        EnumSet<Achievement> expected = EnumSet.noneOf(Achievement.class);
        for (Achievement achievement : Achievement.values()) {
            boolean unlocked = achievement.isUnlocked(player);
            if (filter.equals("All") || filter.equals("Unlocked") && unlocked
                    || filter.equals("Locked") && !unlocked) expected.add(achievement);
        }
        List<BadgeSlot> badges = find(gallery, BadgeSlot.class);
        require(badges.size() == expected.size(), "Incorrect badge count for " + filter);
        for (BadgeSlot badge : badges) {
            require(expected.remove(badge.getAchievement()), "Unexpected or duplicate gallery badge");
            require(badge.isUnlocked() == badge.getAchievement().isUnlocked(player), "Incorrect gallery badge state");
        }
        require(expected.isEmpty(), "Missing gallery badges");
    }

    private static void checkDashboard(MainMenuScreen menu, Player player) {
        List<BadgeSlot> badges = find(menu, BadgeSlot.class);
        require(badges.size() == 3, "Dashboard must show three milestone badges");
        EnumSet<Achievement> expected = EnumSet.of(Achievement.FIRST_STEPS,
                Achievement.STEADY_LEARNER, Achievement.BEGINNER_MASTER);
        for (BadgeSlot badge : badges) {
            require(expected.remove(badge.getAchievement()), "Unexpected dashboard badge");
            require(badge.isUnlocked() == badge.getAchievement().isUnlocked(player), "Dashboard badge stale");
        }
    }

    private static BufferedImage render(BadgeSlot badge) {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        badge.paint(graphics);
        graphics.dispose();
        require(java.util.Arrays.stream(image.getRGB(0, 0, 100, 100, null, 0, 100))
                .anyMatch(p -> (p >>> 24) > 0), "Badge invisible");
        return image;
    }

    private static boolean hasColor(BufferedImage image) {
        for (int p : image.getRGB(0, 0, 100, 100, null, 0, 100)) {
            if ((p >>> 24) == 0) continue;
            int r = p >>> 16 & 255, g = p >>> 8 & 255, b = p & 255;
            if (r != g || g != b) return true;
        }
        return false;
    }

    private static <T extends Component> List<T> find(Component component, Class<T> type) {
        List<T> result = new ArrayList<>();
        if (type.isInstance(component)) result.add(type.cast(component));
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) result.addAll(find(child, type));
        }
        return result;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
