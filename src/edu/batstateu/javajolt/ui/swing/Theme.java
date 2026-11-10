package edu.batstateu.javajolt.ui.swing;

import java.awt.Color;
import java.awt.Font;

public final class Theme {
    private Theme() {
    }

    // Colors (from the Softline Flat Night kit)
    public static final Color BACKGROUND = new Color(0x141518);
    public static final Color PANEL = new Color(0x202227);
    public static final Color PANEL_BORDER = new Color(0x2E3138);
    public static final Color INSET = new Color(0x2A2D33);
    public static final Color INSET_BORDER = new Color(0x373B43);

    public static final Color ACCENT = new Color(0xFF7A45);
    public static final Color ACCENT_HOVER = new Color(0xFF8758);
    public static final Color ACCENT_PRESSED = new Color(0xE06B3D);

    public static final Color SECONDARY_HOVER = new Color(0x3F4247);
    public static final Color SECONDARY_PRESSED = new Color(0x25282D);

    public static final Color DANGER = new Color(0xE5566D);
    public static final Color DANGER_HOVER = new Color(0xE8677C);
    public static final Color DANGER_PRESSED = new Color(0xCA4C60);

    public static final Color TEXT = new Color(0xEEF0F4);
    public static final Color TEXT_MUTED = new Color(0x8B919E);
    public static final Color TEXT_ON_ACCENT = new Color(0x17181C);
    public static final Color JOLT_SPARK = new Color(0xFFC53D);
    public static final Color JOLT_JOLT = new Color(0x3DD9FF);
    public static final Color JOLT_SURGE = new Color(0xB48CFF);

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
