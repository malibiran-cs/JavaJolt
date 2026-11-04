
package edu.batstateu.javajolt.service;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;

public final class DifficultyProgressionService {

    private DifficultyProgressionService() {}

    public static Difficulty currentDifficulty(Player player) {
        if (!player.getProgress()
                .isDifficultyComplete(Difficulty.BEGINNER)) {
            return Difficulty.BEGINNER;
        }

        if (!player.getProgress()
                .isDifficultyComplete(Difficulty.INTERMEDIATE)) {
            return Difficulty.INTERMEDIATE;
        }

        return Difficulty.ADVANCED;
    }
}
