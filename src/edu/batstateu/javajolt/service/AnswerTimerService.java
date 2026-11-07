package edu.batstateu.javajolt.service;

import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.InputMode;
import edu.batstateu.javajolt.game.Enemy;
import edu.batstateu.javajolt.game.GameSession;

// Timeout rules only; the UI owns elapsed time and countdown events.
public class AnswerTimerService {
    // Timer tuning in seconds, grouped by difficulty and input mode.
    private static final int BEGINNER_SINGLE_SECONDS = 30;
    private static final int BEGINNER_CODE_SECONDS = 120;
    private static final int INTERMEDIATE_SINGLE_SECONDS = 20;
    private static final int INTERMEDIATE_CODE_SECONDS = 90;
    private static final int ADVANCED_SINGLE_SECONDS = 15;
    private static final int ADVANCED_CODE_SECONDS = 75;
    private static final int INTERMEDIATE_DAMAGE_DIVISOR = 2;
    private static final int MINIMUM_TIMEOUT_DAMAGE = 1;

    public int getTimeLimitSeconds(Difficulty difficulty, InputMode inputMode) {
        boolean code = inputMode == InputMode.MULTI_LINE;
        return switch (difficulty) {
            case BEGINNER -> code ? BEGINNER_CODE_SECONDS : BEGINNER_SINGLE_SECONDS;
            case INTERMEDIATE -> code ? INTERMEDIATE_CODE_SECONDS : INTERMEDIATE_SINGLE_SECONDS;
            case ADVANCED -> code ? ADVANCED_CODE_SECONDS : ADVANCED_SINGLE_SECONDS;
        };
    }

    public int getTimeoutDamage(GameSession session) {
        int base = Enemy.baseDamageForMaxHp(session.getPlayer().getMaxHp());
        return switch (session.getDifficulty()) {
            case BEGINNER -> 0;
            case INTERMEDIATE -> Math.max(MINIMUM_TIMEOUT_DAMAGE,
                    base / INTERMEDIATE_DAMAGE_DIVISOR);
            case ADVANCED -> base;
        };
    }

    public void applyTimeout(GameSession session) {
        if (!session.isQuestionTimerRunning()) {
            return;
        }
        int damage = getTimeoutDamage(session);
        // A soft Beginner expiry is not an attack and leaves Shield armed.
        if (damage > 0 && session.consumeShield()) {
            damage = 0;
        }
        session.getPlayer().takeDamage(damage);
        session.recordTimeout(damage);
    }
}
