package edu.batstateu.javajolt.enums;

public enum JoltMessage {
    NONE(""),
    SPARK("SPARK!"),
    JOLT("JOLT!"),
    SURGE("SURGE!"),
    JAVAJOLT("JAVAJOLT!"),
    SHORT_CIRCUIT("SHORT CIRCUIT!");

    public static final int SPARK_STREAK = 3;
    public static final int JOLT_STREAK = 6;
    public static final int SURGE_STREAK = 9;
    public static final int SHORT_CIRCUIT_MIN_STREAK = 1;

    private final String text;

    JoltMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static JoltMessage afterAnswer(int streakBefore, int streakAfter, int totalQuestions) {
        if (streakAfter == 0) {
            return streakBefore >= SHORT_CIRCUIT_MIN_STREAK ? SHORT_CIRCUIT : NONE;
        }
        if (streakAfter == totalQuestions && streakAfter >= SPARK_STREAK) {
            return JAVAJOLT;
        }
        if (streakAfter >= SURGE_STREAK) {
            return SURGE;
        }
        if (streakAfter >= JOLT_STREAK) {
            return JOLT;
        }
        if (streakAfter >= SPARK_STREAK) {
            return SPARK;
        }
        return NONE;
    }
}
