package edu.batstateu.javajolt.ui.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

public final class ButtonSounds {
    public static final String SILENT_ZONE = "javajolt.silentZone";
    private static boolean installed;
    private static JButton hoveredButton;

    private ButtonSounds() {
    }

    public static synchronized void install() {
        if (installed) {
            return;
        }
        // NOTE: One global hook covers custom buttons, dialogs, tutorial popups and future buttons.
        Toolkit.getDefaultToolkit().addAWTEventListener(ButtonSounds::handleEvent, AWTEvent.MOUSE_EVENT_MASK);
        installed = true;
    }

    private static void handleEvent(AWTEvent event) {
        if (!(event instanceof MouseEvent mouse)) {
            return;
        }
        clearHoverOnExit(mouse);
        if (!(mouse.getSource() instanceof JButton button)) {
            return;
        }
        if (!isEligible(button)) {
            return;
        }
        if (mouse.getID() == MouseEvent.MOUSE_ENTERED && hoveredButton != button) {
            hoveredButton = button;
            UiSounds.play(SoundEffect.BUTTON_HOVER);
        }
        if (mouse.getID() == MouseEvent.MOUSE_PRESSED && SwingUtilities.isLeftMouseButton(mouse)) {
            UiSounds.play(SoundEffect.BUTTON_CLICK);
        }
    }

    private static void clearHoverOnExit(MouseEvent mouse) {
        if (mouse.getID() != MouseEvent.MOUSE_EXITED || hoveredButton == null) {
            return;
        }
        Component source = mouse.getComponent();
        if (source != hoveredButton && !SwingUtilities.isDescendingFrom(source, hoveredButton)) {
            return;
        }
        // NOTE: Tooltip labels cause exits inside a topic card; only leaving its bounds ends the hover.
        if (!hoveredButton.contains(SwingUtilities.convertPoint(source, mouse.getPoint(), hoveredButton))) {
            hoveredButton = null;
        }
    }

    private static boolean isEligible(JButton button) {
        if (!button.isEnabled()) {
            return false;
        }
        for (Component component = button; component != null; component = component.getParent()) {
            if (component instanceof JComponent view
                    && Boolean.TRUE.equals(view.getClientProperty(SILENT_ZONE))) {
                return false;
            }
        }
        if (SwingUtilities.getAncestorOfClass(JScrollBar.class, button) != null) {
            return false;
        }
        if (SwingUtilities.getAncestorOfClass(JComboBox.class, button) != null) {
            return false;
        }
        return true;
    }
}
