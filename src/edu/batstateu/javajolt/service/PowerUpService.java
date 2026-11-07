package edu.batstateu.javajolt.service;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.PowerUpResult;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.game.GameSession;

import java.util.ArrayList;
import java.util.List;

// Stateless power-up rules: the fixed pool unlocks by level, each unlocked
// power-up is usable once per quiz, and a use marks the session only when it
// actually applies. Time Stop stays locked until the Phase 7 timer exists.
public class PowerUpService {

    // Level >= the power-up's unlock level.
    public boolean isUnlocked(Player player, PowerUp powerUp) {
        return LevelService.calculateLevel(player.getXp()) >= powerUp.getUnlockLevel();
    }

    // Unlocked and not already used this quiz.
    public boolean canUse(GameSession session, PowerUp powerUp) {
        return isUnlocked(session.getPlayer(), powerUp) && !session.hasUsed(powerUp);
    }

    // A use that applies arms its session effect; one that does not (Hint on
    // a non-multiple-choice question, Time Stop, locked, already used) leaves the
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
                        "Shield armed: your next mistake deals no damage.",
                        List.of());
            }
            case POINT_BOOST -> {
                session.markUsed(powerUp);
                session.armPointBoost();
                yield new PowerUpResult(true,
                        "Point Boost armed: your next correct answer earns double base points.",
                        List.of());
            }
            case TIME_STOP -> new PowerUpResult(false,
                    "Time Stop is not available yet.",
                    List.of());
        };
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
