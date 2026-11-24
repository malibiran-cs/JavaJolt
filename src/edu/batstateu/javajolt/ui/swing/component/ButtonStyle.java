package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.Theme;
import java.awt.Color;

public enum ButtonStyle {

    PRIMARY("primary"),
    SECONDARY("secondary"),
    DANGER("danger");

    private final String assetName;

    ButtonStyle(String assetName) {
        this.assetName = assetName;
    }

    String assetName() {
        return assetName;
    }

    Color textColor() {
        return switch (this) {
            case PRIMARY -> Theme.TEXT_ON_ACCENT;
            case SECONDARY -> Theme.TEXT;
            case DANGER -> new Color(0xEEF0F4);
        };
    }
}