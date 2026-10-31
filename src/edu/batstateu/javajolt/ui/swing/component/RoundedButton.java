package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.NinePatch;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * A button whose background is the kit's 192x64 "large" button, stretched with
 * 9-slice. The text is drawn by Swing on top, because the kit's buttons are
 * intentionally blank.
 */
public class RoundedButton extends JButton {
    private static final String ASSET_ROOT = "buttons/button_large_";
    private static final int SLICE = 10; // slices.json: 10px on every side

    private final ButtonStyle style;

    public RoundedButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;

        setFont(Theme.FONT_BUTTON);
        setForeground(style.textColor());
        setBorder(BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_L, Theme.GAP_M, Theme.GAP_L));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (style != null) {
            setForeground(enabled ? style.textColor() : Theme.TEXT_MUTED);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        currentPatch().paint(g2, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    private NinePatch currentPatch() {
        String state;
        if (!isEnabled()) {
            state = "disabled_normal";
        } else if (getModel().isPressed()) {
            state = style.assetName() + "_pressed";
        } else if (getModel().isRollover()) {
            state = style.assetName() + "_hover";
        } else {
            state = style.assetName() + "_normal";
        }
        return UiAssets.patch(ASSET_ROOT + state, SLICE, SLICE, SLICE, SLICE);
    }
}
