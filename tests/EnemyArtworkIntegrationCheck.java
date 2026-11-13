import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.Enemy;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import edu.batstateu.javajolt.ui.swing.component.CharacterEmotion;
import edu.batstateu.javajolt.ui.swing.component.CharacterSpriteCatalog;
import edu.batstateu.javajolt.ui.swing.component.EnemyPortraitView;
import edu.batstateu.javajolt.ui.swing.component.MascotView;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

/** Confirms the team's complete 24-sprite Aseprite export and UI wiring. */
public final class EnemyArtworkIntegrationCheck {
    private static final int ENEMY_SIZE = 110;
    private static final int MASCOT_SIZE = 128;

    public static void main(String[] args) throws Exception {
        for (CharacterEmotion emotion : CharacterEmotion.values()) {
            String path = CharacterSpriteCatalog.mascot(emotion);
            checkSprite(path, MASCOT_SIZE);
        }
        testMascotStates();
        System.out.println("PASS: Team mascot (all 3 emotions)");

        EnemyRoster roster = new EnemyRoster();
        for (Topic topic : Topic.values()) {
            BufferedImage[] variants = new BufferedImage[CharacterEmotion.values().length];
            int index = 0;
            for (CharacterEmotion emotion : CharacterEmotion.values()) {
                String path = CharacterSpriteCatalog.enemy(topic, emotion);
                variants[index++] = checkSprite(path, ENEMY_SIZE);
            }
            require(!equalPixels(variants[0], variants[1]),
                    "Idle and correct sprites are identical for " + topic);
            require(!equalPixels(variants[0], variants[2]),
                    "Idle and hurt sprites are identical for " + topic);

            // The enemy identity is preserved across gameplay difficulty levels.
            String enemyName = null;
            for (Difficulty difficulty : Difficulty.values()) {
                Enemy enemy = roster.getEnemy(topic, difficulty);
                if (enemyName == null) {
                    enemyName = enemy.getName();
                }
                require(enemy.getName().equals(enemyName),
                        "Enemy identity changes with difficulty: " + topic);
            }
            testEnemyStates(topic);
            System.out.println("PASS: " + topic.getLabel()
                    + " (3 emotions, all 3 difficulties)");
        }
        System.out.println("ALL 24 TEAM SPRITES AND PORTRAIT STATES PASSED");
    }

    private static BufferedImage checkSprite(String path, int expectedSize) {
        BufferedImage image = UiAssets.image(path);
        require(image.getWidth() == expectedSize && image.getHeight() == expectedSize,
                "Sprite has wrong canvas size: " + path);
        require(image.getColorModel().hasAlpha(), "No alpha channel: " + path);
        boolean visible = false;
        boolean transparent = false;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int alpha = image.getRGB(x, y) >>> 24;
                visible |= alpha > 0;
                transparent |= alpha < 255;
            }
        }
        require(visible && transparent, "Sprite is invisible or has an opaque canvas: " + path);
        return image;
    }

    private static boolean equalPixels(BufferedImage a, BufferedImage b) {
        if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
            return false;
        }
        for (int y = 0; y < a.getHeight(); y++) {
            for (int x = 0; x < a.getWidth(); x++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void testEnemyStates(Topic topic) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            EnemyPortraitView portrait = new EnemyPortraitView();
            portrait.setSize(156, 200);
            portrait.setTopic(topic);
            portrait.showIdle();
            renderAndCheck(portrait);
            require(portrait.getEmotion() == CharacterEmotion.IDLE, "Wrong idle emotion");
            portrait.showCorrect();
            renderAndCheck(portrait);
            require(portrait.getEmotion() == CharacterEmotion.CORRECT, "Wrong correct emotion");
            portrait.showHurt();
            renderAndCheck(portrait);
            require(portrait.getEmotion() == CharacterEmotion.HURT, "Wrong hurt emotion");
            portrait.showIdle();
            require(portrait.getEmotion() == CharacterEmotion.IDLE, "Enemy failed to reset");
        });
    }

    private static void testMascotStates() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            MascotView portrait = new MascotView();
            portrait.setSize(220, 240);
            portrait.showIdle();
            renderAndCheck(portrait);
            portrait.showCorrect();
            renderAndCheck(portrait);
            portrait.showHurt();
            renderAndCheck(portrait);
            portrait.showIdle();
            require(portrait.getEmotion() == CharacterEmotion.IDLE, "Mascot failed to reset");
        });
    }

    private static void renderAndCheck(javax.swing.JComponent portrait) {
        BufferedImage canvas = new BufferedImage(
                portrait.getWidth(), portrait.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        try {
            portrait.paint(g);
        } finally {
            g.dispose();
        }
        boolean visible = false;
        for (int y = 0; y < canvas.getHeight() && !visible; y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                if ((canvas.getRGB(x, y) >>> 24) != 0) {
                    visible = true;
                    break;
                }
            }
        }
        require(visible, "Character portrait didn't render");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
