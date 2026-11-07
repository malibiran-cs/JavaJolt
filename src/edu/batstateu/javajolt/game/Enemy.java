package edu.batstateu.javajolt.game;

import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;

// One quiz opponent. Base damage scales with the player's max HP and damage
// escalates across uncorrected (consecutive) mistakes. Signature effects later
// phases are meant to override the hook, so the class is concrete and non-final.
public class Enemy {
    private final String name;
    private final Topic topic;
    private final Difficulty difficulty;
    private final int escalationPerMistake;

    public Enemy(String name, Topic topic, Difficulty difficulty, int escalationPerMistake) {
        this.name = name;
        this.topic = topic;
        this.difficulty = difficulty;
        this.escalationPerMistake = escalationPerMistake;
    }

    public String getName() {
        return name;
    }

    public Topic getTopic() {
        return topic;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getEscalationPerMistake() {
        return escalationPerMistake;
    }

    // Damage for this mistake; the session counter is updated AFTER applying it.
    public int resolveMistake(GameSession session) {
        int baseDamage = baseDamageForMaxHp(session.getPlayer().getMaxHp());
        return baseDamage + session.getConsecutiveMistakes() * escalationPerMistake;
    }

    // Single static/pure base-damage helper; kept public for the Phase 7 timer.
    public static int baseDamageForMaxHp(int maxHp) {
        return (maxHp - 1) / 4;
    }
}
