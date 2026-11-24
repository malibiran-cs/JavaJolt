
package edu.batstateu.javajolt.ui.swing;

import java.util.prefs.Preferences;

/**
 * Stores JavaJolt application preferences separately
 * from player progress and saved games.
 */
public final class AppSettings {

    public static void applyAudioSettings() {
    UiSounds.configureAudio(
            getMasterVolume(),
            isSoundEnabled(),
            isButtonSoundEnabled(),
            isAnswerSoundEnabled(),
            isCountdownSoundEnabled()
    );
}

    public enum ThemeMode {
        DARK, LIGHT
    }

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(AppSettings.class);

    private AppSettings() {
    }

    // APPEARANCE

    public static ThemeMode getThemeMode() {
        String stored = PREFS.get("theme", "DARK");

        return "LIGHT".equalsIgnoreCase(stored)
                ? ThemeMode.LIGHT : ThemeMode.DARK;
    }

    public static void setThemeMode(ThemeMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException(
                    "Theme cannot be null"
            );
        }

        PREFS.put("theme", mode.name());
        applyThemeSettings();
    }

    // MASTER AUDIO

    public static int getMasterVolume() {
        return clamp(
                PREFS.getInt("masterVolume", 100)
        );
    }

    public static void setMasterVolume(int percent) {
        PREFS.putInt(
                "masterVolume", clamp(percent)
        );
        applyAudioSettings();
    }

    public static boolean isSoundEnabled() {
        return PREFS.getBoolean("soundEnabled", true);
    }

    public static void setSoundEnabled(boolean enabled) {
        PREFS.putBoolean("soundEnabled", enabled);
        applyAudioSettings();
    }

    // INDIVIDUAL AUDIO OPTIONS

    public static boolean isButtonSoundEnabled() {
        return PREFS.getBoolean("buttonSound", true);
    }

    public static void setButtonSoundEnabled(boolean enabled) {
        PREFS.putBoolean("buttonSound", enabled);
        applyAudioSettings();
    }

    public static boolean isAnswerSoundEnabled() {
        return PREFS.getBoolean("answerSound", true);
    }

    public static void setAnswerSoundEnabled(boolean enabled) {
        PREFS.putBoolean("answerSound", enabled);
        applyAudioSettings();
    }

    public static boolean isCountdownSoundEnabled() {
        return PREFS.getBoolean("countdownSound", true);
    }

    public static void setCountdownSoundEnabled(
            boolean enabled) {
        PREFS.putBoolean("countdownSound", enabled);
        applyAudioSettings();
    }

    // ACCESSIBILITY

    public static boolean isReducedMotionEnabled() {
        return PREFS.getBoolean("reducedMotion", false);
    }

    public static void setReducedMotionEnabled(
            boolean enabled) {
        PREFS.putBoolean("reducedMotion", enabled);
    }

    // RESET SETTINGS

    public static void resetDefaults() {
        setThemeMode(ThemeMode.DARK);
        setMasterVolume(100);
        setSoundEnabled(true);
        setButtonSoundEnabled(true);
        setAnswerSoundEnabled(true);
        setCountdownSoundEnabled(true);
        setReducedMotionEnabled(false);
        applyAudioSettings();
        applyThemeSettings();
    }

    public static void applyThemeSettings() {
    Theme.setLightMode(
            getThemeMode() == ThemeMode.LIGHT
    );
}

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
