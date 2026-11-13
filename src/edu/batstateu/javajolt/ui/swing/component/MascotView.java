package edu.batstateu.javajolt.ui.swing.component;

/** The team's Aseprite mascot, shared across all JavaJolt screens. */
public final class MascotView extends CharacterPortraitView {
    public MascotView() {
        super(true); // Preserve the gentle idle floating animation.
    }

    @Override
    protected String spritePath(CharacterEmotion emotion) {
        return CharacterSpriteCatalog.mascot(emotion);
    }
}
