package edu.batstateu.javajolt.ui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.batstateu.javajolt.ui.swing.component.RoundedButton;
import edu.batstateu.javajolt.ui.swing.component.RoundedTextField;

public final class Theme {
    private Theme() {
    }

    // Colors (from the Softline Flat Night kit)
        // Dynamic theme colors
        private static boolean lightMode = false;

        public static Color BACKGROUND = new Color(0x141518);
        public static Color PANEL = new Color(0x202227);
        public static Color PANEL_BORDER = new Color(0x2E3138);
        public static Color INSET = new Color(0x2A2D33);
        public static Color INSET_BORDER = new Color(0x373B43);

        public static final Color ACCENT = new Color(0xFF7A45);
        public static final Color ACCENT_HOVER = new Color(0xFF8758);
        public static final Color ACCENT_PRESSED = new Color(0xE06B3D);

        public static Color SECONDARY_HOVER = new Color(0x3F4247);
        public static Color SECONDARY_PRESSED = new Color(0x25282D);

        public static final Color DANGER = new Color(0xE5566D);
        public static final Color DANGER_HOVER = new Color(0xE8677C);
        public static final Color DANGER_PRESSED = new Color(0xCA4C60);

        public static Color TEXT = new Color(0xEEF0F4);
        public static Color TEXT_MUTED = new Color(0x8B919E);
        public static final Color TEXT_ON_ACCENT = new Color(0x17181C);

        public static final Color JOLT_SPARK = new Color(0xFFC53D);
        public static final Color JOLT_JOLT = new Color(0x3DD9FF);
        public static final Color JOLT_SURGE = new Color(0xB48CFF);

        public static boolean isLightMode() {
        return lightMode;
        }

        public static void setLightMode(boolean enabled) {

        if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(
                        () -> setLightMode(enabled)
                );
                return;
        }

        if (lightMode == enabled) {
                return;
        }

        Color[] previousColors = getPalette();

        lightMode = enabled;

        if (enabled) {
                applyLightTheme();
        } else {
                applyDarkTheme();
        }

        Color[] currentColors = getPalette();

        Map<Color, Color> replacements = new HashMap<>();

        for (int i = 0; i < previousColors.length; i++) {
                replacements.put(
                        previousColors[i],
                        currentColors[i]
                );
        }

        for (Window window : Window.getWindows()) {

                if (window instanceof MainWindow) {
                refreshComponent(window, replacements);

                window.revalidate();
                window.repaint();
                }
        }
        }

        private static Color[] getPalette() {
    return new Color[] {
        BACKGROUND,
        PANEL,
        PANEL_BORDER,
        INSET,
        INSET_BORDER,
        SECONDARY_HOVER,
        SECONDARY_PRESSED,
        TEXT,
        TEXT_MUTED
    };
}

private static void refreshComponent(
        Component component,
        Map<Color, Color> replacements) {

    Color background = component.getBackground();
    Color foreground = component.getForeground();

    if (background != null
            && replacements.containsKey(background)) {

        component.setBackground(
                replacements.get(background)
        );
    }

    if (foreground != null
            && replacements.containsKey(foreground)) {

        component.setForeground(
                replacements.get(foreground)
        );
    }

    if (component instanceof RoundedButton button) {
        button.refreshTheme();
    }

    if (component instanceof RoundedTextField field) {
        field.refreshTheme();
    }

    if (component instanceof Container container) {

        for (Component child : container.getComponents()) {
            refreshComponent(child, replacements);
        }
    }

    if (component instanceof JComponent jc) {
        jc.revalidate();
        jc.repaint();
    }
}

        private static void applyLightTheme() {
        BACKGROUND = new Color(0xF3F4F6);
        PANEL = new Color(0xFFFFFF);
        PANEL_BORDER = new Color(0xD5D9E0);
        INSET = new Color(0xE8EBF0);
        INSET_BORDER = new Color(0xC8CDD6);

        SECONDARY_HOVER = new Color(0xDCE1E8);
        SECONDARY_PRESSED = new Color(0xCDD3DC);

        TEXT = new Color(0x22252B);
        TEXT_MUTED = new Color(0x626978);
        }

        private static void applyDarkTheme() {
        BACKGROUND = new Color(0x141518);
        PANEL = new Color(0x202227);
        PANEL_BORDER = new Color(0x2E3138);
        INSET = new Color(0x2A2D33);
        INSET_BORDER = new Color(0x373B43);

        SECONDARY_HOVER = new Color(0x3F4247);
        SECONDARY_PRESSED = new Color(0x25282D);

        TEXT = new Color(0xEEF0F4);
        TEXT_MUTED = new Color(0x8B919E);
        }

    // Fonts
    public static final Font FONT_TITLE =
            new Font(Font.SANS_SERIF, Font.BOLD, 32);

    public static final Font FONT_HEADING =
            new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font FONT_JOLT_MESSAGE = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    public static final Font FONT_BODY =
            new Font(Font.SANS_SERIF, Font.PLAIN, 16);

    public static final Font FONT_BUTTON =
            new Font(Font.SANS_SERIF, Font.BOLD, 16);

    public static final Font FONT_CODE =
            new Font(Font.MONOSPACED, Font.PLAIN, 14);

    // Corner roundness is part of the kit images; NinePatch keeps the corners
    // fixed while the middle of each component stretches.

    // Spacing
    public static final int GAP_S = 8;
    public static final int GAP_M = 16;
    public static final int GAP_L = 24;
    public static final int GAP_XL = 32;

    // Question screen sizing
    public static final int CHARACTER_SIZE = 110;
    // Fresh-quiz player stats preferred width before compacting the rows.
    public static final int PLAYER_COLUMN_WIDTH = 164;
    public static final int QUESTION_PROMPT_WIDTH = 520;
    public static final int QUESTION_PROMPT_HEIGHT = 170;
    public static final int ANSWER_FIELD_WIDTH = 360;
    public static final int ANSWER_FIELD_HEIGHT = 64;
    public static final int CHOICE_TEXT_WIDTH = 260;
    public static final int CODE_EDITOR_ROWS = 10;
    public static final int CODE_EDITOR_COLUMNS = 70;
    // Tutorial popup body viewport; long text remains scrollable.
    public static final int TUTORIAL_BODY_ROWS = 9;
    public static final int TUTORIAL_BODY_COLUMNS = 56;
}
