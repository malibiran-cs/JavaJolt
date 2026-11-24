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

        private static BufferedImage recolor(
            BufferedImage source,
            int oldFill,
            int oldBorder,
            int newFill,
            int newBorder) {

        BufferedImage result = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        int fr = (oldFill >> 16) & 255;
        int fg = (oldFill >> 8) & 255;
        int fb = oldFill & 255;

        int dr = ((oldBorder >> 16) & 255) - fr;
        int dg = ((oldBorder >> 8) & 255) - fg;
        int db = (oldBorder & 255) - fb;

        double length = dr * dr + dg * dg + db * db;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {

                int pixel = source.getRGB(x, y);

                int alpha = (pixel >>> 24) & 255;

                if (alpha == 0) {
                    continue;
                }

                int r = (pixel >> 16) & 255;
                int g = (pixel >> 8) & 255;
                int b = pixel & 255;

                double t = length == 0 ? 0 :
                        ((r - fr) * dr
                        + (g - fg) * dg
                        + (b - fb) * db) / length;

                t = Math.max(0, Math.min(1, t));

                int nr = blendChannel(
                        (newFill >> 16) & 255,
                        (newBorder >> 16) & 255, t);

                int ng = blendChannel(
                        (newFill >> 8) & 255,
                        (newBorder >> 8) & 255, t);

                int nb = blendChannel(
                        newFill & 255,
                        newBorder & 255, t);

                result.setRGB(x, y,
                        (alpha << 24)
                        | (nr << 16)
                        | (ng << 8)
                        | nb);
            }
        }

        return result;
    }

    private static int blendChannel(
            int start, int end, double amount) {

        return (int) Math.round(
                start + (end - start) * amount
        );
    }

    public static BufferedImage lightImage(String name) {
    return LIGHT_IMAGES.computeIfAbsent(
            name, UiAssets::createLightImage
    );
}

private static BufferedImage createLightImage(String name) {
    BufferedImage source = image(name);

        return switch (name) {

            case "panels/panel" ->
                recolor(source,
                        0x202227, 0x2E3138,
                        0xFFFFFF, 0xD5D9E0);

            case "panels/panel_inset", "fields/input_normal" ->
                recolor(source,
                        0x2A2D33, 0x373B43,
                        0xE8EBF0, 0xC8CDD6);

            case "fields/input_focus" ->
                recolor(source,
                        0x2A2D33, 0xFF7A45,
                        0xE8EBF0, 0xFF7A45);

            case "panels/divider", "items/star_empty" ->
                recolor(source,
                        0x2E3138, 0x2E3138,
                        0xD5D9E0, 0xD5D9E0);

            case "buttons/button_large_secondary_normal" ->
                recolor(source,
                        0x2A2D33, 0x3A3E47,
                        0xE8EBF0, 0xC8CDD6);

            case "buttons/button_large_secondary_hover" ->
                recolor(source,
                        0x3F4247, 0x3A3E47,
                        0xDCE1E8, 0xC8CDD6);

            case "buttons/button_large_secondary_pressed" ->
                recolor(source,
                        0x25282D, 0x3A3E47,
                        0xCDD3DC, 0xB8BEC8);

            case "buttons/button_large_disabled_normal" ->
                recolor(source,
                        0x26282D, 0x3A3E47,
                        0xE1E4E8, 0xCDD2DA);

            default -> source;
        };
    }
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
    private static final Map<String, BufferedImage> LIGHT_IMAGES = new HashMap<>();
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
        NinePatch patch = new NinePatch(
                image(name),
                lightImage(name),
                left,
                top,
                right,
                bottom
        );
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
