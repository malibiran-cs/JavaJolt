package edu.batstateu.javajolt.entity;

import java.util.List;

// Outcome of one power-up use: whether it applied, the short player-facing
// message, and (Hint only) the choices that were removed from the question.
public record PowerUpResult(boolean applied, String message, List<String> removedChoices) {
    public PowerUpResult {
        removedChoices = removedChoices == null ? List.of() : List.copyOf(removedChoices);
    }
}
