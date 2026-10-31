package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.NinePatch;
import edu.batstateu.javajolt.ui.swing.Theme;
import edu.batstateu.javajolt.ui.swing.UiAssets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * A text field skinned with the kit's input_normal / input_focus images.
 * The image is 9-sliced, so the field can be any width and height.
 */
public class RoundedTextField extends JTextField {
    private static final int SLICE = 11; // slices.json: 11px on every side

    private final NinePatch normal = UiAssets.patch("fields/input_normal", SLICE, SLICE, SLICE, SLICE);
    private final NinePatch focused = UiAssets.patch("fields/input_focus", SLICE, SLICE, SLICE, SLICE);

    public RoundedTextField() {
        setOpaque(false);
        setFont(Theme.FONT_BODY);
        setForeground(Theme.TEXT);
        setCaretColor(Theme.ACCENT);
        setSelectionColor(Theme.ACCENT);
        setSelectedTextColor(Theme.TEXT_ON_ACCENT);
        setBorder(BorderFactory.createEmptyBorder(Theme.GAP_M, Theme.GAP_M, Theme.GAP_M, Theme.GAP_M));

        // The focus image is a different file, so repaint when focus changes.
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        (isFocusOwner() ? focused : normal).paint(g2, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
