package edu.batstateu.javajolt.enums;

public enum Topic {
    VARIABLES("Variables"),
    OPERATORS("Operators"),
    DECISIONS("Decisions"),
    LOOPS("Loops"),
    ARRAYS("Arrays"),
    EXCEPTION_HANDLING("Exception Handling"),
    OOP("Object-Oriented Programming");

    private final String label;

    Topic(String label) {
        this.label = label;
    }

    // Friendly name shown to the player
    public String getLabel() {
        return label;
    }
}
