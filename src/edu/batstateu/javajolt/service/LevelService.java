
package edu.batstateu.javajolt.service;

public class LevelService {

    private static final int XP_STEP = 50;

    // Total accumulated XP required to reach a level
    public static long totalXpForLevel(int level) {

        if (level < 1) {
            throw new IllegalArgumentException(
                    "Level must be at least 1."
            );
        }

        return (long) XP_STEP * level * (level - 1) / 2;
    }

    // Calculate player's level based on total XP
    public static int calculateLevel(int xp) {

        int level = 1;
        int totalXp = Math.max(0, xp);

        while (totalXpForLevel(level + 1) <= totalXp) {
            level++;
        }

        return level;
    }
}
