package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.ui.swing.Theme;
import java.awt.Color;

/**
 * The three button looks that exist in the current screens.
 * Each name points at a family of kit images: normal, hover and pressed.
 */
public enum ButtonStyle {
    PRIMARY("primary", Theme.TEXT_ON_ACCENT),
    SECONDARY("secondary", Theme.TEXT),
    DANGER("danger", Theme.TEXT);

    private final String assetName;
    private final Color textColor;

    ButtonStyle(String assetName, Color textColor) {
        this.assetName = assetName;
        this.textColor = textColor;
    }

    String assetName() {
        return assetName;
    }

    Color textColor() {
        return textColor;
    }
}
