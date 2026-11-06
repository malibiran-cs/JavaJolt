package edu.batstateu.javajolt.service;

// Derives quiz rewards from the player's level. Pure stateless math.
public class LevelRewardService {

    // Tuning: max HP = 10 + min(level / 3, 5): 10 at L1-2, 11 at L3-5, ... 15 at L15 and above.
    private static final int BASE_MAX_HP = 10;
    private static final int HP_LEVELS_PER_STEP = 3;
    private static final int MAX_HP_BONUS_CAP = 5;

    public static int maxHpForLevel(int level) {
        int safeLevel = Math.max(1, level);
        int steps = Math.min(safeLevel / HP_LEVELS_PER_STEP, MAX_HP_BONUS_CAP);
        return BASE_MAX_HP + steps;
    }
}
