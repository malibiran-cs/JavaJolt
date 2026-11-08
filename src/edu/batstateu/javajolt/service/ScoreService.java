package edu.batstateu.javajolt.service;

// Owns the per-answer quiz score rules: base points and correct-streak growth.
public class ScoreService {

    // Points for the first correct answer in a streak.
    private static final int BASE_POINTS = 10;
    // Extra points for each following correct answer in the streak.
    private static final int POINTS_PER_STREAK_STEP = 2;

    // Points for one correct answer, given the streak *after* the answer.
    // Streak 1 = base only; streak 2 pays one step, and so on.
    public int pointsForCorrect(int streakAfterAnswer) {
        return BASE_POINTS + POINTS_PER_STREAK_STEP * (streakAfterAnswer - 1);
    }
}
