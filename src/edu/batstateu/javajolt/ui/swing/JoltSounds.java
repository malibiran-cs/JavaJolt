package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.enums.JoltMessage;

public final class JoltSounds {
    private JoltSounds() {
    }

    public static SoundEffect soundFor(JoltMessage before, JoltMessage after) {
        if (after == before) {
            return null;
        }
        // NOTE: JAVAJOLT has no sound yet; add its SoundEffect mapping here later.
        return switch (after) {
            case SPARK -> SoundEffect.JOLT_SPARK;
            case JOLT -> SoundEffect.JOLT_JOLT;
            case SURGE -> SoundEffect.JOLT_SURGE;
            case SHORT_CIRCUIT -> SoundEffect.JOLT_SHORT_CIRCUIT;
            default -> null;
        };
    }

    public static void onAnswerResolved(JoltMessage before, JoltMessage after) {
        SoundEffect effect = soundFor(before, after);
        if (effect != null) {
            UiSounds.play(effect);
        }
    }

}
