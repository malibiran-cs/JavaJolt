package edu.batstateu.javajolt.service;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.PowerUpResult;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.game.GameSession;

import java.util.ArrayList;
import java.util.List;

// Stateless power-up rules: the fixed pool unlocks by level, each unlocked
// power-up is usable once per quiz, and a use marks the session only when it
// actually applies. Time Stop freezes only the current question timer.
public class PowerUpService {

    // Share of max HP restored by Heal on Beginner.
    private static final int BEGINNER_HEAL_PERCENT = 20;
    // Share of max HP restored by Heal on Intermediate.
    private static final int INTERMEDIATE_HEAL_PERCENT = 35;
    // Share of max HP restored by Heal on Advanced.
    private static final int ADVANCED_HEAL_PERCENT = 50;
    // Heal always restores at least this much before the max-HP cap.
    private static final int MINIMUM_HEAL_AMOUNT = 1;

    // Level >= the power-up's unlock level.
    public boolean isUnlocked(Player player, PowerUp powerUp) {
        return LevelService.calculateLevel(player.getXp()) >= powerUp.getUnlockLevel();
    }

    // Unlocked and not already used this quiz.
    public boolean canUse(GameSession session, PowerUp powerUp) {
        return isUnlocked(session.getPlayer(), powerUp) && !session.hasUsed(powerUp);
    }

    // A use that applies records its session effect; one that does not (full HP,
    // a non-multiple-choice Hint, expired timer, locked, already used) leaves the
    // session untouched so it can be rejected without being consumed.
    public PowerUpResult use(GameSession session, PowerUp powerUp, Question current) {
        if (!canUse(session, powerUp)) {
            return new PowerUpResult(false,
                    powerUp.getLabel() + " is locked or already used this quiz.",
                    List.of());
        }

        return switch (powerUp) {
            case HINT -> applyHint(session, current);
            case SHIELD -> {
                session.markUsed(powerUp);
                session.armShield();
                yield new PowerUpResult(true,
                        "Shield armed: your next damaging attack deals no damage.",
                        List.of());
            }
            case HEAL -> applyHeal(session);
            case TIME_STOP -> {
                if (!isApplicable(powerUp, session, current)) {
                    yield new PowerUpResult(false, "There is no running countdown to freeze.", List.of());
                }
                session.freezeQuestionTimer();
                session.markUsed(powerUp);
                yield new PowerUpResult(true, "Time Stop: countdown frozen for this question.", List.of());
            }
        };
    }

    // Public tuning readout for tutorial text and other displays.
    public int getHealPercent(Difficulty difficulty) {
        return switch (difficulty) {
            case BEGINNER -> BEGINNER_HEAL_PERCENT;
            case INTERMEDIATE -> INTERMEDIATE_HEAL_PERCENT;
            case ADVANCED -> ADVANCED_HEAL_PERCENT;
        };
    }

    // Potential recovery; Player.heal applies the current max-HP cap.
    public int getHealAmount(int maxHp, Difficulty difficulty) {
        return Math.max(MINIMUM_HEAL_AMOUNT, maxHp * getHealPercent(difficulty) / 100);
    }

    private PowerUpResult applyHeal(GameSession session) {
        if (!isApplicable(PowerUp.HEAL, session, null)) {
            return new PowerUpResult(false, "Heal is unavailable: HP is already full.", List.of());
        }
        Player player = session.getPlayer();
        int hpBefore = player.getHp();
        player.heal(getHealAmount(player.getMaxHp(), session.getDifficulty()));
        session.markUsed(PowerUp.HEAL);
        return new PowerUpResult(true,
                "Heal restored " + (player.getHp() - hpBefore) + " HP.", List.of());
    }

    // A Question type with no removable choices leaves the hint unconsumed.
    private PowerUpResult applyHint(GameSession session, Question current) {
        List<String> removed = current.getHintChoicesToRemove();
        if (removed.isEmpty()) {
            return new PowerUpResult(false,
                    "Hint is unavailable for this question type.",
                    List.of());
        }
        session.markUsed(PowerUp.HINT);
        return new PowerUpResult(true,
                "Hint: one wrong choice was removed.",
                removed);
    }

    // Display rules for the current question: whether the power-up could apply
    // if used now (independent of unlock/once-per-quiz state).
    public boolean isApplicable(PowerUp powerUp, GameSession session, Question current) {
        return switch (powerUp) {
            case HINT -> current != null && current.supportsHint();
            case TIME_STOP -> current != null && session.isQuestionTimerRunning();
            case HEAL -> session.getPlayer().getHp() < session.getPlayer().getMaxHp();
            case SHIELD -> true;
        };
    }

    // Power-ups whose unlock level falls in (oldLevel, newLevel], in unlock order.
    public List<PowerUp> unlockedBetween(int oldLevel, int newLevel) {
        List<PowerUp> unlocked = new ArrayList<>();
        for (PowerUp powerUp : PowerUp.values()) {
            int unlock = powerUp.getUnlockLevel();
            if (unlock > Math.max(0, oldLevel) && unlock <= newLevel) {
                unlocked.add(powerUp);
            }
        }
        return unlocked;
    }
}
