package edu.batstateu.javajolt.enums;

public enum Difficulty {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");

    private final String label;

    Difficulty(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
