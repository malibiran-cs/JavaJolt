package edu.batstateu.javajolt.ui.swing;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Loads the Softline UI Kit art used by the Swing screens.
 *
 * <p>Every file in {@code assets/} is the PNG_2x version of the kit. The kit is
 * authored at 1x, so all slice numbers in this code stay in 1x "design pixels"
 * and {@link #PIXEL_SCALE} is used when sampling the real image. That keeps the
 * code readable and lets the UI look sharp on high-DPI displays.</p>
 */
public final class UiAssets {
    /** PNG_2x files are twice as large as the kit's design size. */
    public static final int PIXEL_SCALE = 2;

    private static final String CLASSPATH_ROOT = "/edu/batstateu/javajolt/ui/swing/assets/";

    // Plain javac does not copy non-Java files into out/. Also support
    // opening the parent directory in VS Code (the project is then under JavaJolt/).
    private static final Path SOURCE_ASSET_PATH = Path.of(
            "src", "edu", "batstateu", "javajolt", "ui", "swing", "assets");
    private static final List<Path> FALLBACK_ROOTS = List.of(
            SOURCE_ASSET_PATH,
            Path.of("JavaJolt").resolve(SOURCE_ASSET_PATH),
            Path.of("JavaJolt-main").resolve(SOURCE_ASSET_PATH),
            Path.of("assets", "Softline_UI_Kit_Flat_Night", "PNG_2x"),
            Path.of("..", "assets", "Softline_UI_Kit_Flat_Night", "PNG_2x"));

    private static final Map<String, BufferedImage> IMAGES = new HashMap<>();
    private static final Map<String, NinePatch> PATCHES = new HashMap<>();

    private UiAssets() {
    }

    /** Returns a cached image, for example {@code "items/star_empty"}. */
    public static BufferedImage image(String name) {
        BufferedImage cached = IMAGES.get(name);
        if (cached != null) {
            return cached;
        }
        BufferedImage loaded = read(name);
        IMAGES.put(name, loaded);
        return loaded;
    }

    /** Returns a cached 9-slice painter. Margins are the 1x values from slices.json. */
    public static NinePatch patch(String name, int left, int top, int right, int bottom) {
        String key = name + '|' + left + '|' + top + '|' + right + '|' + bottom;
        NinePatch cached = PATCHES.get(key);
        if (cached != null) {
            return cached;
        }
        NinePatch patch = new NinePatch(image(name), left, top, right, bottom);
        PATCHES.put(key, patch);
        return patch;
    }

    private static BufferedImage read(String name) {
        try (InputStream in = open(name)) {
            BufferedImage image = ImageIO.read(in);
            if (image == null) {
                throw new IOException("the file is not a readable PNG");
            }
            return image;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load UI asset \"" + name + "\"", e);
        }
    }

    private static InputStream open(String name) throws IOException {
        String file = name + ".png";

        InputStream packaged = UiAssets.class.getResourceAsStream(CLASSPATH_ROOT + file);
        if (packaged != null) {
            return packaged;
        }

        String override = System.getProperty("javajolt.assets");
        if (override != null) {
            Path path = Path.of(override).resolve(file);
            if (Files.isRegularFile(path)) {
                return Files.newInputStream(path);
            }
        }

        for (Path root : FALLBACK_ROOTS) {
            Path path = root.resolve(file);
            if (Files.isRegularFile(path)) {
                return Files.newInputStream(path);
            }
        }

        throw new IOException("asset not found on the classpath or in " + FALLBACK_ROOTS);
    }
}
