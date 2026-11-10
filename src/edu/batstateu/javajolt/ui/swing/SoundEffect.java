package edu.batstateu.javajolt.ui.swing;

public enum SoundEffect {
    // VOLUME TUNING: 0.0 = mute, 1.0 = the file's original loudness, above 1.0 = boost.

    BUTTON_HOVER("ui/button_hover.wav", 1.0f),
    BUTTON_CLICK("ui/button_click2.wav", 0.4f),

    ANSWER_CORRECT("ui/answer_correct.wav", 0.65f),
    ANSWER_INCORRECT("ui/answer_incorrect.wav", 0.65f),

    JOLT_SPARK("jolt/jolt_spark.wav", 1.0f),
    JOLT_JOLT("jolt/jolt_jolt.wav", 0.8f),
    JOLT_SURGE("jolt/jolt_surge.wav", 0.9f),
    JOLT_SHORT_CIRCUIT("jolt/jolt_short_circuit.wav", 0.5f),

    TIMER_COUNTDOWN("timer/timer_countdown_3.wav", 1.0f);


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
