package edu.batstateu.javajolt.ui.swing.component;

/** The three sprite states shared by the player mascot and every enemy. */
public enum CharacterEmotion {
    IDLE("idle"),
    CORRECT("correct"),
    HURT("hurt");

    private final String fileName;

    CharacterEmotion(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
