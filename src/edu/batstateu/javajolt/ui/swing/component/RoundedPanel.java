package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.NinePatch;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 * A panel whose background is a 9-sliced kit image (the window panel by
 * default, or the darker inset panel). Because it is a real Swing container,
 * child components can be added with any layout manager.
 */
public class RoundedPanel extends JPanel {
    private final NinePatch patch;

    public RoundedPanel(LayoutManager layout) {
        this(layout, "panels/panel", 16);
    }

    public RoundedPanel(LayoutManager layout, String asset, int slice) {
        super(layout);
        this.patch = UiAssets.patch(asset, slice, slice, slice, slice);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        patch.paint(g2, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
