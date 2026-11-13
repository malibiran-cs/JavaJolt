package edu.batstateu.javajolt.ui.swing.component;

import edu.batstateu.javajolt.enums.Topic;
import java.util.Objects;

/** The single mapping between game characters, emotions, and exported PNG assets. */
public final class CharacterSpriteCatalog {
    private static final String ROOT = "characters/";

    private CharacterSpriteCatalog() {
    }

    public static String mascot(CharacterEmotion emotion) {
        return ROOT + "mascot/" + Objects.requireNonNull(emotion).getFileName();
    }

    public static String enemy(Topic topic, CharacterEmotion emotion) {
        String folder = switch (Objects.requireNonNull(topic)) {
            case VARIABLES -> "variables";
            case OPERATORS -> "operators";
            case DECISIONS -> "decisions";
            case LOOPS -> "loops";
            case ARRAYS -> "arrays";
            case EXCEPTION_HANDLING -> "exception";
            case OOP -> "oop";
        };
        return ROOT + "enemies/" + folder + "/"
                + Objects.requireNonNull(emotion).getFileName();
    }
}
