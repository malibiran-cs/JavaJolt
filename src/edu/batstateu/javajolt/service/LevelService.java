
package edu.batstateu.javajolt.service;

public class LevelService {

    // Tuning: linear curve. Level 1 is below 100 XP; Level n (n >= 2) starts at 50*n XP.
    private static final int XP_STEP = 50;
    private static final int LEVEL_2_THRESHOLD = 100;

    // Total accumulated XP required to reach a level
    public static long totalXpForLevel(int level) {

        if (level < 1) {
            throw new IllegalArgumentException(
                    "Level must be at least 1."
            );
        }

        if (level == 1) {
            return 0;
        }
        return (long) XP_STEP * level;
    }

    // Calculate player's level based on total XP
    public static int calculateLevel(int xp) {

        int totalXp = Math.max(0, xp);

        if (totalXp < LEVEL_2_THRESHOLD) {
            return 1;
        }
        return totalXp / XP_STEP;
    }
}
