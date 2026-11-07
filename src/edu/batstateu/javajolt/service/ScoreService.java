package edu.batstateu.javajolt.service;

// Owns the per-answer quiz score rules: base points, streak growth, and the
// boost multiplier for later phases. Stateless math; Phase 4 leaves boosts off.
public class ScoreService {

    private static final int BASE_POINTS = 10;
    private static final int POINTS_PER_STREAK_STEP = 2;
    private static final int BOOST_MULTIPLIER = 2;

    // Points for one correct answer, given the streak *after* the answer.
    // Streak 1 = base only; streak 2 pays one step, and so on.
    public int pointsForCorrect(boolean boosted, int streakAfterAnswer) {
        int base = BASE_POINTS * (boosted ? BOOST_MULTIPLIER : 1);
        return base + POINTS_PER_STREAK_STEP * (streakAfterAnswer - 1);
    }
}
