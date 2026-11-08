import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.Enemy;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import edu.batstateu.javajolt.ui.swing.component.EnemyPortraitView;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

/** Verifies enemy images, portrait rendering, and consistent names across difficulties. */
public final class EnemyArtworkIntegrationCheck {
    public static void main(String[] args) throws Exception {
        EnemyRoster roster = new EnemyRoster();
        for (Topic topic : Topic.values()) {
            String expected = switch (topic) {
                case VARIABLES -> "enemies/enemy_variables";
                case OPERATORS -> "enemies/enemy_operators";
                case DECISIONS -> "enemies/enemy_decisions";
                case LOOPS -> "enemies/enemy_loops";
                case ARRAYS -> "enemies/enemy_arrays";
                case EXCEPTION_HANDLING -> "enemies/enemy_exception";
                case OOP -> "enemies/enemy_oop";
            };
            BufferedImage image = UiAssets.image(expected);
            require(image.getWidth() > 100 && image.getHeight() > 100,
                    "Enemy image missing or too small: " + topic);
            require(image.getColorModel().hasAlpha(), "Enemy image needs alpha: " + topic);
            String enemyName = null;
            for (Difficulty difficulty : Difficulty.values()) {
                Enemy enemy = roster.getEnemy(topic, difficulty);
                if (enemyName == null) enemyName = enemy.getName();
                require(enemy.getName().equals(enemyName),
                        "Enemy identity changes with difficulty: " + topic);
            }
            SwingUtilities.invokeAndWait(() -> {
                EnemyPortraitView portrait = new EnemyPortraitView();
                portrait.setTopic(topic);
                portrait.setSize(156, 200);
                BufferedImage canvas = new BufferedImage(156, 200, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = canvas.createGraphics();
                try { portrait.paint(g); } finally { g.dispose(); }
                boolean anyOpaque = false;
                for (int y=0; y<200 && !anyOpaque; y+=10) {
                    for (int x=0; x<156; x+=10) {
                        if ((canvas.getRGB(x,y) >>> 24) != 0) {
                            anyOpaque = true;
                            break;
                        }
                    }
                }
                require(anyOpaque, "Enemy portrait not rendered: " + topic);
            });
            System.out.println("PASS: " + topic.getLabel() + " (all 3 difficulties)");
        }
        System.out.println("ALL 7 ENEMY PORTRAITS PASSED");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
