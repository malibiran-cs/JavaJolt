package edu.batstateu.javajolt.ui.swing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Draws a stretchable kit image without distorting its corners.
 *
 * <p>The image is split into 9 regions: four corners that keep their size, four
 * edges that stretch in one direction, and a center that stretches in both.
 * This is the same idea as a NinePatchRect in Godot or a 9-slice border in CSS.
 * The margin values are the 1x numbers from the kit's {@code slices.json}; the
 * 2x image is sampled at twice those values (see {@link UiAssets#PIXEL_SCALE}).</p>
 */
public final class NinePatch {
    private final BufferedImage image;
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    NinePatch(BufferedImage image, int left, int top, int right, int bottom) {
        this.image = image;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /** Paints the patch at the component's origin. */
    public void paint(Graphics2D g, int width, int height) {
        paint(g, 0, 0, width, height);
    }

    public void paint(Graphics2D g, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        int scale = UiAssets.PIXEL_SCALE;
        int srcLeft = left * scale;
        int srcTop = top * scale;
        int srcRight = right * scale;
        int srcBottom = bottom * scale;

        // Target corner sizes, in 1x design pixels. Shrink them for very small targets.
        int dstLeft = Math.min(left, width);
        int dstRight = Math.min(right, width - dstLeft);
        int dstTop = Math.min(top, height);
        int dstBottom = Math.min(bottom, height - dstTop);

        int[] sx = { 0, srcLeft, image.getWidth() - srcRight, image.getWidth() };
        int[] sy = { 0, srcTop, image.getHeight() - srcBottom, image.getHeight() };
        int[] dx = { x, x + dstLeft, x + width - dstRight, x + width };
        int[] dy = { y, y + dstTop, y + height - dstBottom, y + height };

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int cellWidth = dx[col + 1] - dx[col];
                int cellHeight = dy[row + 1] - dy[row];
                if (cellWidth <= 0 || cellHeight <= 0) {
                    continue;
                }
                g2.drawImage(image,
                        dx[col], dy[row], dx[col + 1], dy[row + 1],
                        sx[col], sy[row], sx[col + 1], sy[row + 1], null);
            }
        }

        g2.dispose();
    }
}
