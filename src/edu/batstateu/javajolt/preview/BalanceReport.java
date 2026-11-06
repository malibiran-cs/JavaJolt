package edu.batstateu.javajolt.preview;

import edu.batstateu.javajolt.service.LevelRewardService;
import edu.batstateu.javajolt.service.LevelService;

// Dev tool: prints the D1 level curve for levels 1-25 (XP threshold per level)
// next to the D3 max HP band at that level. Print-only; never touches saves.
public class BalanceReport {

    private static final int MAX_REPORTED_LEVEL = 25;
    private static final String LINE =
            "-------+--------------+--------------+--------";

    public static void main(String[] args) {
        System.out.println("JavaJolt balance report (levels 1-" + MAX_REPORTED_LEVEL + ")");
        System.out.println();
        System.out.println(" level | XP threshold | XP to level  | max HP");
        System.out.println(LINE);

        for (int level = 1; level <= MAX_REPORTED_LEVEL; level++) {
            long threshold = LevelService.totalXpForLevel(level);
            int maxHp = LevelRewardService.maxHpForLevel(level);
            System.out.printf("%6d | %12d | %12d | %6d%n",
                    level, threshold,
                    LevelService.calculateLevel((int) Math.min(threshold, Integer.MAX_VALUE)),
                    maxHp);
        }

        System.out.println(LINE);
        System.out.println("""
                Tuning notes:
                  Levels: Level 1 starts below 100 XP; level n (n >= 2) starts at 50 * n XP.
                  Max HP: 10 + min(level / 3, 5) - 10 HP at L1-2, 11 at L3-5, ... 15 at L15+.
                """);
    }
}
