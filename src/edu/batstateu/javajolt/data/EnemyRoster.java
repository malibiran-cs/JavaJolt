package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.Enemy;

// Maps every topic/difficulty pair to its quiz enemy. Variables has the only
// named roster; every other topic gets a placeholder with the same escalation
// until the Phase 6 roster expands.
public class EnemyRoster {

    // Escalation increments per difficulty, in enum order (Beginner/Intermediate/Advanced).
    private static final int ESCALATION_BEGINNER = 2;
    private static final int ESCALATION_INTERMEDIATE = 3;
    private static final int ESCALATION_ADVANCED = 4;

    public Enemy getEnemy(Topic topic, Difficulty difficulty) {
        int escalation = escalationFor(difficulty);

        if (topic == Topic.VARIABLES) {
            return switch (difficulty) {
                case BEGINNER -> new Enemy("Piglet Byte", topic, difficulty, escalation);
                case INTERMEDIATE -> new Enemy("Boar Byte", topic, difficulty, escalation);
                case ADVANCED -> new Enemy("Warthog Byte", topic, difficulty, escalation);
            };
        }

        return new Enemy(topic.getLabel() + " Bug", topic, difficulty, escalation);
    }

    private static int escalationFor(Difficulty difficulty) {
        return switch (difficulty) {
            case BEGINNER -> ESCALATION_BEGINNER;
            case INTERMEDIATE -> ESCALATION_INTERMEDIATE;
            case ADVANCED -> ESCALATION_ADVANCED;
        };
    }
}
