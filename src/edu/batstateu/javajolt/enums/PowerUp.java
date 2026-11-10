package edu.batstateu.javajolt.enums;

// The fixed power-up pool. Unlock levels live only here.
public enum PowerUp {
    HINT("Hint", 5),
    SHIELD("Shield", 10),
    HEAL("Heal", 15),
    TIME_STOP("Time Stop", 20);

    private final String label;
    private final int unlockLevel; // LevelService level at which the power-up unlocks

    PowerUp(String label, int unlockLevel) {
        this.label = label;
        this.unlockLevel = unlockLevel;
    }

    public String getLabel() {
        return label;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }
}
