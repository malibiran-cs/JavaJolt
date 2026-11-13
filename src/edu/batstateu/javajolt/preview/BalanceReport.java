package edu.batstateu.javajolt.preview;

import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.Enemy;
import edu.batstateu.javajolt.service.LevelRewardService;
import edu.batstateu.javajolt.service.LevelService;

// Dev tool: prints balance tables. Section 1 is the D1 level curve (levels 1-25)
// with the D3 max HP band. Sections 2-4 cover the Phase 3 enemy damage model:
// survivability per HP band x difficulty, the exhaustive 4,096 twelve-answer
// outcome classification, and probability-weighted outcome shares. Print-only;
// never touches saves.
public class BalanceReport {

    private static final int MAX_REPORTED_LEVEL = 25;
    private static final int HP_BAND_MIN = 10;
    private static final int HP_BAND_MAX = 15;
    private static final int QUIZ_SIZE = 12;
    private static final int[] UNIFORM_RATES = {60, 70, 80, 90};
    private static final String LINE =
            "-------+--------------+--------";

    private static final EnemyRoster roster = new EnemyRoster();

    public static void main(String[] args) {
        printLevelTable();
        printSurvivabilityTables();
        printEnumerationTables();
        printProbabilityTables();
    }

    private static void printLevelTable() {
        System.out.println("Section 1: level curve, levels 1-" + MAX_REPORTED_LEVEL);
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

    // For every HP band x difficulty: base damage, escalation, and the damage
    // profile of an uninterrupted mistake chain vs isolated mistakes.
    private static void printSurvivabilityTables() {
        System.out.println("Section 2: enemy damage survivability per max HP band x difficulty");
        System.out.println("chain damage assumes a full-HP player and no correct resets;");
        System.out.println("isolated damage assumes a correct answer between every mistake.");
        System.out.println();
        System.out.println(" max HP | diff        | esc | base | dmg @ m1/m2/m3 | chain to defeat | 4 isolated total");
        System.out.println("--------+-------------+-----+------+----------------+-----------------+-----------------");

        for (int maxHp = HP_BAND_MIN; maxHp <= HP_BAND_MAX; maxHp++) {
            for (Difficulty difficulty : Difficulty.values()) {
                Enemy enemy = roster.getEnemy(Topic.VARIABLES, difficulty);
                int base = Enemy.baseDamageForMaxHp(maxHp);
                int escalation = enemy.getEscalationPerMistake();
                int m1 = base;
                int m2 = base + escalation;
                int m3 = base + 2 * escalation;
                int chainLength = chainLengthToDefeat(maxHp, base, escalation);
                int isolated = 4 * base;
                boolean isolatedSurvives = isolated < maxHp;

                System.out.printf(" %5d  | %-11s | %3d | %4d | %14s | %15d | %4d (stays alive: %b)%n",
                        maxHp, difficulty.getLabel(), escalation, base,
                        m1 + "/" + m2 + "/" + m3,
                        chainLength, isolated, isolatedSurvives);
            }
        }
        System.out.println();
    }

    private static int chainLengthToDefeat(int maxHp, int base, int escalation) {
        int hpLost = 0;
        for (int mistake = 1; mistake <= QUIZ_SIZE; mistake++) {
            hpLost += base + (mistake - 1) * escalation;
            if (hpLost >= maxHp) {
                return mistake;
            }
        }
        return -1; // not defeated within one quiz
    }

    // Exhaustive enumeration: every one of the 4,096 twelve-answer sequences
    // per band x difficulty, classified as
    //   A = HP defeat, accuracy over answered >= 70
    //   B = HP defeat, accuracy over answered < 70
    //   C = finished alive, failed by accuracy
    //   D = passed (alive, accuracy >= 70)
    private static void printEnumerationTables() {
        System.out.println("Section 3: exhaustive 12-answer classification (4,096 sequences per cell)");
        System.out.println("Counts are combinatorial sequence counts, NOT real-world player outcome probabilities.");
        System.out.println();
        System.out.println(" max HP | diff        |   A    |   B    |   C    |   D");
        System.out.println("--------+-------------+--------+--------+--------+--------");

        for (int maxHp = HP_BAND_MIN; maxHp <= HP_BAND_MAX; maxHp++) {
            for (Difficulty difficulty : Difficulty.values()) {
                Enemy enemy = roster.getEnemy(Topic.VARIABLES, difficulty);
                int[] counts = enumerateOutcomes(maxHp, enemy);
                System.out.printf(" %5d  | %-11s | %5d  | %5d  | %5d  | %5d%n",
                        maxHp, difficulty.getLabel(),
                        counts[0], counts[1], counts[2], counts[3]);
            }
        }
        System.out.println();
    }

    // Probability-weighted pass share and HP-defeat share for assumed
    // correctness rates. Model U assumes every question is answered correctly
    // with probability p. Model T assumes lower correctness on the later,
    // harder tiers of the quiz order (application -10pp, challenge -20pp,
    // clamped to 1..99) - an assumption, not a measurement.
    private static void printProbabilityTables() {
        System.out.println("Section 4: probability-weighted outcomes (weights enumerate all 4,096 masks,");
        System.out.println("deduplicated per realized below-death prefix; shares add to 100%).");
        System.out.println("pass = expected share of passes (D); HPdef = expected share of HP defeats (A+B).");

        for (String title : new String[]{
                "Model U: uniform per-question correctness",
                "Model T: -10pp application tier, -20pp challenge tier (assumption)"}) {
            boolean tiered = title.startsWith("Model T");
            System.out.println();
            System.out.println(title);
            System.out.printf("%10s  %-12s | %s | %s%n", "max HP", "difficulty",
                    rightPad("pass @ rates", UNIFORM_RATES.length * 8),
                    "HPdef @ rates");
            System.out.println("----------+--------------+" + dashes(UNIFORM_RATES.length * 8)
                    + "+----------------------");

            for (int maxHp = HP_BAND_MIN; maxHp <= HP_BAND_MAX; maxHp++) {
                for (Difficulty difficulty : Difficulty.values()) {
                    Enemy enemy = roster.getEnemy(Topic.VARIABLES, difficulty);
                    double[] pass = new double[UNIFORM_RATES.length];
                    double[] defeat = new double[UNIFORM_RATES.length];

                    for (int r = 0; r < UNIFORM_RATES.length; r++) {
                        int[] slots = tiered
                                ? tieredSlots(UNIFORM_RATES[r])
                                : uniformSlots(UNIFORM_RATES[r]);
                        double[] shares = weightedOutcomes(maxHp, enemy, slots);
                        pass[r] = shares[3];
                        defeat[r] = shares[0] + shares[1];
                    }

                    System.out.printf("%10d  %-12s | ", maxHp, difficulty.getLabel());
                    for (double share : pass) {
                        System.out.printf("%6.2f%%", share * 100);
                    }
                    System.out.print("  | ");
                    for (double share : defeat) {
                        System.out.printf("%6.2f%%", share * 100);
                    }
                    System.out.println();
                }
            }
        }
        System.out.println();
        System.out.println("""
                Reading notes:
                  Base damage is fixed per band: (maxHp - 1) / 4 using integer division.
                  Escalation grows with difficulty (2 / 3 / 4 per difficulty).
                  Three consecutive mistakes defeat every band; four isolated mistakes never do.
              """);
    }

    private static String rightPad(String text, int width) {
        return text + " ".repeat(Math.max(0, width - text.length()));
    }

    private static String dashes(int width) {
        return "-".repeat(width);
    }

    private static int[] uniformSlots(int rate) {
        int[] slots = new int[QUIZ_SIZE];
        java.util.Arrays.fill(slots, rate);
        return slots;
    }

    private static int[] tieredSlots(int foundationalRate) {
        int[] slots = new int[QUIZ_SIZE];
        for (int i = 0; i < QUIZ_SIZE; i++) {
            if (i < 4) {
                slots[i] = clampRate(foundationalRate);
            } else if (i < 8) {
                slots[i] = clampRate(foundationalRate - 10);
            } else {
                slots[i] = clampRate(foundationalRate - 20);
            }
        }
        return slots;
    }

    private static int clampRate(int rate) {
        return Math.max(1, Math.min(99, rate));
    }

    // Shared core: distinct outcome sequences across the 4,096 masks using the
    // same damage math as the real submitAnswer path. A run that dies is
    // counted once per below-death prefix (bits after the killing answer do
    // not matter). Returns [A, B, C, D] counts.
    private static int[] enumerateOutcomes(int maxHp, Enemy enemy) {
        int base = Enemy.baseDamageForMaxHp(maxHp);
        int escalation = enemy.getEscalationPerMistake();
        int[] counts = new int[4];
        boolean[] countedKey = new boolean[1 << QUIZ_SIZE];

        for (int mask = 0; mask < (1 << QUIZ_SIZE); mask++) {
            int hp = maxHp;
            int correct = 0;
            int answered = 0;
            int consecutive = 0;
            boolean dead = false;

            for (int i = 0; i < QUIZ_SIZE; i++) {
                boolean isCorrect = (mask & (1 << i)) != 0;
                if (dead) {
                    break; // defeat ended the run; later questions are not answered
                }
                answered++;
                if (isCorrect) {
                    correct++;
                    consecutive = 0;
                } else {
                    hp -= base + consecutive * escalation;
                    if (hp <= 0) {
                        dead = true;
                    } else {
                        consecutive++;
                    }
                }
            }

            int key = dead
                    ? mask & ~(((1 << (QUIZ_SIZE - answered)) - 1) << answered)
                    : mask;
            if (countedKey[key]) {
                continue;
            }
            countedKey[key] = true;

            int accuracy = correct * 100 / answered;
            if (dead) {
                counts[accuracy >= 70 ? 0 : 1]++;
            } else if (accuracy >= 70) {
                counts[3]++;
            } else {
                counts[2]++;
            }
        }
        return counts;
    }

    // Same enumeration with per-slot correctness weights; each realized outcome
    // contributes its below-death prefix weight exactly once, so the shares
    // add to 1. Returns the share [A, B, C, D].
    private static double[] weightedOutcomes(int maxHp, Enemy enemy, int[] slotRates) {
        int base = Enemy.baseDamageForMaxHp(maxHp);
        int escalation = enemy.getEscalationPerMistake();
        double[] shares = new double[4];
        boolean[] weightedKey = new boolean[1 << QUIZ_SIZE];

        for (int mask = 0; mask < (1 << QUIZ_SIZE); mask++) {
            int hp = maxHp;
            int correct = 0;
            int answered = 0;
            int consecutive = 0;
            boolean dead = false;
            double weight = 1.0;

            for (int i = 0; i < QUIZ_SIZE; i++) {
                boolean isCorrect = (mask & (1 << i)) != 0;
                if (dead) {
                    break; // post-death answers carry no weight: any branch sums to 1
                }
                double p = slotRates[i] / 100.0;
                weight *= isCorrect ? p : (1.0 - p);
                answered++;
                if (isCorrect) {
                    correct++;
                    consecutive = 0;
                } else {
                    hp -= base + consecutive * escalation;
                    if (hp <= 0) {
                        dead = true;
                    } else {
                        consecutive++;
                    }
                }
            }

            int key = dead
                    ? mask & ~(((1 << (QUIZ_SIZE - answered)) - 1) << answered)
                    : mask;
            if (weightedKey[key]) {
                continue;
            }
            weightedKey[key] = true;

            int accuracy = correct * 100 / answered;
            int category;
            if (dead) {
                category = accuracy >= 70 ? 0 : 1;
            } else if (accuracy >= 70) {
                category = 3;
            } else {
                category = 2;
            }
            shares[category] += weight;
        }
        return shares;
    }
}
