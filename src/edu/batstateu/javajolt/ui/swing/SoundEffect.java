package edu.batstateu.javajolt.ui.swing;

public enum SoundEffect {
    // VOLUME TUNING: 0.0 = mute, 1.0 = the file's original loudness, above 1.0 = boost.

    BUTTON_HOVER("ui/button_hover.wav", 1.0f),
    BUTTON_CLICK("ui/button_click2.wav", 0.4f),

    ANSWER_CORRECT("ui/answer_correct.wav", 0.65f),
    ANSWER_INCORRECT("ui/answer_incorrect.wav", 0.65f);


    private final String resourcePath;
    private final float volume;

    SoundEffect(String resourcePath, float volume) {
        this.resourcePath = resourcePath;
        this.volume = volume;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public float getVolume() {
        return volume;
    }
}
