package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.Enemy;

// Maps all seven topics to their own themed enemy across all difficulties.
// Damage escalation still depends on difficulty; artwork does not.
public class EnemyRoster {

    // Escalation increments per difficulty, in enum order (Beginner/Intermediate/Advanced).
    private static final int ESCALATION_BEGINNER = 2;
    private static final int ESCALATION_INTERMEDIATE = 3;
    private static final int ESCALATION_ADVANCED = 4;

    public Enemy getEnemy(Topic topic, Difficulty difficulty) {
        int escalation = escalationFor(difficulty);

        // Same enemy identity and artwork at every difficulty.
        // Difficulty still controls its underlying damage escalation.
        return new Enemy(enemyNameFor(topic), topic, difficulty, escalation);
    }

    private static String enemyNameFor(Topic topic) {
        return switch (topic) {
            case VARIABLES -> "Andr0id X";
            case OPERATORS -> "OpRiot";
            case DECISIONS -> "Fork Oracle";
            case LOOPS -> "Loopuin";
            case ARRAYS -> "Clip Stack";
            case EXCEPTION_HANDLING -> "Crash Monkey";
            case OOP -> "Window Warden";
        };
    }

    private static int escalationFor(Difficulty difficulty) {
        return switch (difficulty) {
            case BEGINNER -> ESCALATION_BEGINNER;
            case INTERMEDIATE -> ESCALATION_INTERMEDIATE;
            case ADVANCED -> ESCALATION_ADVANCED;
        };
    }
}
