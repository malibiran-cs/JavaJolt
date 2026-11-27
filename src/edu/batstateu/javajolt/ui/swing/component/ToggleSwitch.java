package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.Theme;
import java.awt.*;
import javax.swing.JToggleButton;

/** Keyboard-operable, themed on/off control with a native Swing button model. */
public final class ToggleSwitch extends JToggleButton {
    public ToggleSwitch(String name) {
        super(name);
        setPreferredSize(new Dimension(64, 30));
        setMinimumSize(getPreferredSize());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        getAccessibleContext().setAccessibleName(name);
        setToolTipText(name);
    }
    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = 52, h = 26, x = (getWidth()-w)/2, y=(getHeight()-h)/2;
            g.setColor(isSelected() && isEnabled() ? Theme.ACCENT : Theme.INSET_BORDER);
            g.fillRoundRect(x,y,w,h,h,h);
            g.setColor(isEnabled() ? (isSelected() ? Theme.TEXT_ON_ACCENT : Theme.TEXT) : Theme.TEXT_MUTED);
            g.fillOval(x+(isSelected()?w-h+4:4), y+4,h-8,h-8);
            if (isFocusOwner() || getModel().isRollover()) {
                g.setColor(Theme.ACCENT); g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(x-3,y-2,w+6,h+4,h+4,h+4);
            }
        } finally {g.dispose();}
    }
}
