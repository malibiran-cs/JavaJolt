package edu.batstateu.javajolt.preview;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LevelRewardService;
import edu.batstateu.javajolt.service.LevelService;
import edu.batstateu.javajolt.service.QuestionService;

import java.util.ArrayList;
import java.util.List;

// Dev tool: in-memory checks for the Phase 2 mechanics - D1 level boundaries,
// D3 max HP bands, and end-of-quiz settlement rules. Everything runs on fresh
// Player objects in memory; QuestionService is built read-only and no save
// path is ever opened (saves/ is never touched).
public class MechanicsCheck {

    // 12 questions matches the real quiz size, so xpPerCorrect(12) = 2 exactly.
    private static final int QUIZ_SIZE = 12;
    private static final int EXPECTED_XP_PER_CORRECT = 2;
    private static final int EXPECTED_COMPLETION_BONUS = 50;
    private static final int POINTS_PER_CORRECT = 10;

    private static final QuestionService questionService =
            new QuestionService(new edu.batstateu.javajolt.data.QuestionBank());
    private static final LessonRunnerService runner =
            new LessonRunnerService(questionService, null, null);

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("JavaJolt mechanics check (in-memory, no saves touched)");
        System.out.println();

        checkLevelBoundaries();
        checkMaxHpBands();
        checkXpPerCorrect();
        checkPassSettlesOnce();
        checkReplayEarnsNothing();
        checkFailedAttemptEarnsNoXp();
        checkAbandonmentBanksNothing();
        checkDeathBeforeFinish();
        checkHighScoreOnlyImproves();

        System.out.println();
        System.out.println("Checks: " + passed + " passed, " + failed + " failed, "
                + (passed + failed) + " total.");
        if (failed == 0) {
            System.out.println("ALL CHECKS PASSED");
        } else {
            System.out.println("SOME CHECKS FAILED");
            System.exit(1);
        }
    }

    // D1 contract: 99 -> L1, 100 -> L2, 249 -> L4, 250 -> L5, 750 -> L15.
    private static void checkLevelBoundaries() {
        check("level: 0 XP is level 1", LevelService.calculateLevel(0) == 1);
        check("level: 99 XP is level 1 (just below 100)", LevelService.calculateLevel(99) == 1);
        check("level: 100 XP is level 2", LevelService.calculateLevel(100) == 2);
        check("level: 249 XP is level 4", LevelService.calculateLevel(249) == 4);
        check("level: 250 XP is level 5", LevelService.calculateLevel(250) == 5);
        check("level: 750 XP is level 15", LevelService.calculateLevel(750) == 15);
        check("level: threshold L2 = 100", LevelService.totalXpForLevel(2) == 100);
        check("level: threshold L10 = 500", LevelService.totalXpForLevel(10) == 500);
        check("level: threshold L15 = 750", LevelService.totalXpForLevel(15) == 750);
    }

    // D3 contract: 10 + min(level / 3, 5). L1-2 = 10, L3-5 = 11, ... L15+ = 15.
    private static void checkMaxHpBands() {
        check("max HP: level 1 = 10", LevelRewardService.maxHpForLevel(1) == 10);
        check("max HP: level 2 = 10 (same band)", LevelRewardService.maxHpForLevel(2) == 10);
        check("max HP: level 3 = 11", LevelRewardService.maxHpForLevel(3) == 11);
        check("max HP: level 5 = 11 (same band)", LevelRewardService.maxHpForLevel(5) == 11);
        check("max HP: level 6 = 12", LevelRewardService.maxHpForLevel(6) == 12);
        check("max HP: level 9 = 13", LevelRewardService.maxHpForLevel(9) == 13);
        check("max HP: level 12 = 14", LevelRewardService.maxHpForLevel(12) == 14);
        check("max HP: level 15 = 15", LevelRewardService.maxHpForLevel(15) == 15);
        check("max HP: level 40 = 15 (capped)", LevelRewardService.maxHpForLevel(40) == 15);
        check("max HP: heal clamps to max HP; restore lands exactly on max HP", healCapWorks());
    }

    private static boolean healCapWorks() {
        Player player = new Player("Check", 0);
        player.setMaxHp(11);
        player.setHp(3);
        player.heal(999);
        player.restoreFullHp();
        return player.getHp() == 11 && player.getMaxHp() == 11;
    }

    private static void checkXpPerCorrect() {
        check("per-correct XP: 12-question quiz = " + EXPECTED_XP_PER_CORRECT,
                runner.xpPerCorrect(QUIZ_SIZE) == EXPECTED_XP_PER_CORRECT);
    }

    // First pass of an uncleared pair: provisional XP + bonus commit together,
    // exactly once; score always commits.
    private static void checkPassSettlesOnce() {
        Player player = new Player("Check", 0);
        GameSession session = playAll(newSession(player));

        boolean firstSettle = runner.settleAttempt(session);
        boolean secondSettle = runner.settleAttempt(session); // must be a no-op

        check("settle pass: returns true on the first call", firstSettle);
        check("settle pass: second call is a no-op", !secondSettle);
        check("settle pass: XP = 12*2 provisional + 50 bonus = "
                + (QUIZ_SIZE * EXPECTED_XP_PER_CORRECT + EXPECTED_COMPLETION_BONUS),
                player.getXp() == QUIZ_SIZE * EXPECTED_XP_PER_CORRECT + EXPECTED_COMPLETION_BONUS);
        check("settle pass: score = 12*10 = " + (QUIZ_SIZE * POINTS_PER_CORRECT),
                player.getScore() == QUIZ_SIZE * POINTS_PER_CORRECT);
        check("settle pass: pair is complete", player.isComplete(Topic.VARIABLES, Difficulty.BEGINNER));
        check("settle pass: session marked settled", session.isSettled());
    }

    // Replay of a completed pair: score still commits, XP stays untouched.
    private static void checkReplayEarnsNothing() {
        Player player = new Player("Check", 100);
        player.complete(Topic.VARIABLES, Difficulty.BEGINNER);
        int xpBefore = player.getXp();

        GameSession session = playAll(newSession(player));
        check("replay: no provisional XP while the pair is complete",
                session.getProvisionalXp() == 0);

        boolean newlyCompleted = runner.settleAttempt(session);
        check("replay: settle returns false", !newlyCompleted);
        check("replay: XP unchanged", player.getXp() == xpBefore);
        check("replay: attempt score still commits", player.getScore() == QUIZ_SIZE * POINTS_PER_CORRECT);
    }

    // Failed finish (below 70% accuracy): score commits, zero XP, nothing completes.
    private static void checkFailedAttemptEarnsNoXp() {
        Player player = new Player("Check", 0);
        GameSession session = newSession(player);
        playCorrect(session, 8); // 8/12 = 67% < 70%: fails while alive
        playWrong(session, 4);
        runner.settleAttempt(session);

        check("fail: zero XP", player.getXp() == 0);
        check("fail: attempt score commits", player.getScore() == 8 * POINTS_PER_CORRECT);
        check("fail: pair not complete",
                !player.isComplete(Topic.VARIABLES, Difficulty.BEGINNER));
    }

    // Abandoned quiz (never finished, no settlement): banks nothing at all.
    private static void checkAbandonmentBanksNothing() {
        Player player = new Player("Check", 0);
        GameSession session = newSession(player);
        playCorrect(session, 5);
        // Quiz abandoned here (window closed / different quiz): no settleAttempt call.

        check("abandon: score not banked", player.getScore() == 0);
        check("abandon: XP not banked", player.getXp() == 0);
        check("abandon: pair not complete",
                !player.isComplete(Topic.VARIABLES, Difficulty.BEGINNER));
        check("abandon: session never marked settled", !session.isSettled());
        check("abandon: provisional XP stays in the dead session",
                session.getProvisionalXp() > 0);
    }

    // Out of HP before the quiz ends (never finished): banks nothing.
    private static void checkDeathBeforeFinish() {
        Player player = new Player("Check", 0);
        player.setMaxHp(10);
        player.restoreFullHp();
        GameSession session = newSession(player);
        playWrong(session, QUIZ_SIZE); // 2 damage x 12 hits defeats 10 HP

        check("death: player is out of HP", !player.isAlive());
        check("death: score not banked without settlement", player.getScore() == 0);
    }

    private static void checkHighScoreOnlyImproves() {
        Player player = new Player("Check", 0);
        boolean first = player.recordHighScore(Topic.VARIABLES, Difficulty.BEGINNER, 150);
        boolean second = player.recordHighScore(Topic.VARIABLES, Difficulty.BEGINNER, 80);
        boolean third = player.recordHighScore(Topic.VARIABLES, Difficulty.BEGINNER, 200);

        check("high score: first record accepted", first);
        check("high score: lower score rejected", !second);
        check("high score: better score accepted", third);
    }

    // Helpers: settled checks use fixed always-correct placeholders so the
    // scenarios stay deterministic without reading any question files.
    private static GameSession newSession(Player player) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < QUIZ_SIZE; i++) {
            questions.add(new AlwaysCorrectCheck(i + 1));
        }
        return new GameSession(player, Topic.VARIABLES, Difficulty.BEGINNER, questions);
    }

    private static GameSession playAll(GameSession session) {
        playCorrect(session, QUIZ_SIZE);
        return session;
    }

    private static void playCorrect(GameSession session, int times) {
        for (int i = 0; i < times; i++) {
            runner.submitAnswer(session, session.getQuestions().get(i), "anything");
        }
    }

    private static void playWrong(GameSession session, int times) {
        for (int i = 0; i < times; i++) {
            runner.submitAnswer(session, session.getQuestions().get(i), "");
        }
    }

    // Always considered correct by submitAnswer (any non-empty answer).
    private static class AlwaysCorrectCheck extends Question {
        AlwaysCorrectCheck(int id) {
            super(id, "MechanicsCheck placeholder question", Topic.VARIABLES,
                    Difficulty.BEGINNER, "placeholder explanation");
        }

        @Override
        public boolean isCorrect(String userInput) {
            return userInput != null && !userInput.isBlank();
        }

        @Override
        public String display() {
            return "MechanicsCheck placeholder question";
        }
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name);
        }
    }
}
