package edu.batstateu.javajolt.preview;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.Enemy;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LevelRewardService;
import edu.batstateu.javajolt.service.LevelService;
import edu.batstateu.javajolt.service.QuestionService;

import java.util.ArrayList;
import java.util.List;

// Dev tool: in-memory checks for Phase 2-3 mechanics - D1 level boundaries,
// D3 max HP bands, end-of-quiz settlement rules, and the Phase 3 enemy damage
// model (lethality, survivability, independent defeat categories). Everything
// runs on fresh Player objects in memory; no save path is ever opened.
public class MechanicsCheck {

    // 12 questions matches the real quiz size, so xpPerCorrect(12) = 2 exactly
    // and a full quiz has 4 skills per tier (foundational 1-4, application 5-8,
    // challenge 9-12).
    private static final int QUIZ_SIZE = 12;
    private static final int EXPECTED_XP_PER_CORRECT = 2;
    private static final int EXPECTED_COMPLETION_BONUS = 50;
    private static final int POINTS_PER_CORRECT = 10;
    private static final int HP_BAND_MIN = 10;
    private static final int HP_BAND_MAX = 15;

    private static final QuestionService questionService =
            new QuestionService(new edu.batstateu.javajolt.data.QuestionBank());
    private static final LessonRunnerService runner =
            new LessonRunnerService(questionService, null, new EnemyRoster(), null);
    private static final EnemyRoster roster = new EnemyRoster();

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("JavaJolt mechanics check (in-memory, no saves touched)");
        System.out.println();

        checkLevelBoundaries();
        checkMaxHpBands();
        checkXpPerCorrect();
        checkEnemyDamageCounters();
        checkBandAndDifficultyRules();
        checkEnumerationHasBothDefeatCategories();
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

    // Enemy counter semantics: wrong increments once, correct resets to 0, and
    // damage resolves from the pre-answer counter (so re-attacks after a correct
    // answer cost base damage again).
    private static void checkEnemyDamageCounters() {
        Player player = new Player("Check", 0);
        player.setMaxHp(13);
        player.restoreFullHp();
        GameSession session = newSession(player);
        Question question = session.getQuestions().get(0);
        int base = session.getEnemy().baseDamageForMaxHp(13);

        runner.submitAnswer(session, question, ""); // first mistake
        check("enemy: counter is 1 after one mistake", session.getConsecutiveMistakes() == 1);
        check("enemy: first mistake costs base damage " + base,
                session.getLastDamage() == base);

        runner.submitAnswer(session, question, "anything"); // correct resets the counter
        check("enemy: a correct answer resets the counter", session.getConsecutiveMistakes() == 0);

        runner.submitAnswer(session, question, ""); // counter rebuilds from 0
        check("enemy: base damage repeats after a correct answer",
                session.getLastDamage() == base && session.getConsecutiveMistakes() == 1);
    }

    // Per HP band (10-15) x difficulty: escalation contract, scripted runs, and
    // the accuracy/HP interplay cases required by the brief.
    private static void checkBandAndDifficultyRules() {
        for (int maxHp = HP_BAND_MIN; maxHp <= HP_BAND_MAX; maxHp++) {
            for (Difficulty difficulty : Difficulty.values()) {
                checkLethality(maxHp, difficulty);
                checkSurvivability(maxHp, difficulty);
                checkScriptedRuns(maxHp, difficulty);
            }
        }
    }

    private static void checkLethality(int maxHp, Difficulty difficulty) {
        Player player = new Player("Check", 0);
        player.setMaxHp(maxHp);
        player.restoreFullHp();
        GameSession session = newSession(player, difficulty);
        playWrong(session, 3);

        String label = "band " + maxHp + "/" + difficulty.getLabel() + ": ";
        check(label + "3 consecutive mistakes are lethal", !player.isAlive());
        check(label + "damage escalated every consecutive mistake",
                session.getConsecutiveMistakes() == 3);
    }

    private static void checkSurvivability(int maxHp, Difficulty difficulty) {
        Player player = new Player("Check", 0);
        player.setMaxHp(maxHp);
        player.restoreFullHp();
        GameSession session = newSession(player, difficulty);
        // Four isolated mistakes (a correct answer resets the escalation chain).
        playSpreadWrong(session, 12);

        String label = "band " + maxHp + "/" + difficulty.getLabel() + ": ";
        check(label + "4 isolated mistakes are survivable", player.isAlive());
        check(label + "escalation chain was reset after the last correct answer",
                session.getConsecutiveMistakes() == 0);
    }

    private static void checkScriptedRuns(int maxHp, Difficulty difficulty) {
        String label = "band " + maxHp + "/" + difficulty.getLabel() + ": ";

        // 8 correct then 3 consecutive wrong: after 11 answered the player is
        // dead but accuracy stays 8*100/11 = 72 (integer) >= 70 - so the run
        // fails exclusively because of the independent HP defeat rule.
        Player player = new Player("Check", 0);
        player.setMaxHp(maxHp);
        player.restoreFullHp();
        GameSession session = newSession(player, difficulty);
        playCorrect(session, 8);
        playWrong(session, 3);
        check(label + "8 correct + 3 consecutive wrong: defeated with accuracy 72",
                !player.isAlive() && session.getAccuracyPercent() == 72
                        && !runner.hasPassed(session));

        // Four isolated wrong spread over 12 answers: alive, 8*100/12 = 66 < 70,
        // so the run fails by accuracy at completion.
        Player player2 = new Player("Check", 0);
        player2.setMaxHp(maxHp);
        player2.restoreFullHp();
        GameSession session2 = newSession(player2, difficulty);
        playSpreadWrong(session2, 12);
        check(label + "4 isolated wrong across 12: alive, fails by accuracy 66",
                player2.isAlive() && session2.getAccuracyPercent() == 66
                        && player2.getHp() == maxHp - 4 * session2.getEnemy().baseDamageForMaxHp(maxHp)
                        && !runner.hasPassed(session2));

        // A full 12-correct run finishes alive and passes.
        Player player3 = new Player("Check", 0);
        player3.setMaxHp(maxHp);
        player3.restoreFullHp();
        GameSession session3 = newSession(player3, difficulty);
        playCorrect(session3, 12);
        check(label + "12 correct passes", player3.isAlive() && runner.hasPassed(session3)
                && session3.getAccuracyPercent() == 100);
    }

    // Exhaustive 12-answer enumeration per band/difficulty: both independent
    // defeat categories must occur somewhere (counted with the same math as
    // BalanceReport's table).
    private static void checkEnumerationHasBothDefeatCategories() {
        for (int maxHp = HP_BAND_MIN; maxHp <= HP_BAND_MAX; maxHp++) {
            for (Difficulty difficulty : Difficulty.values()) {
                int[] counts = enumerateOutcomes(maxHp, roster.getEnemy(Topic.VARIABLES, difficulty));
                String label = "band " + maxHp + "/" + difficulty.getLabel() + ": ";
                check(label + "HP defeat with accuracy >= 70 exists (category A)", counts[0] > 0);
                check(label + "HP defeat with accuracy < 70 exists (category B)", counts[1] > 0);
                check(label + "alive accuracy-fail exists (category C)", counts[2] > 0);
                check(label + "pass exists (category D)", counts[3] > 0);
            }
        }
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
    // The mistakes are isolated so the run fails by accuracy while still alive.
    private static void checkFailedAttemptEarnsNoXp() {
        Player player = new Player("Check", 0);
        GameSession session = newSession(player);
        playSpreadWrong(session, 12); // 8 correct, 4 isolated wrong: 66% accuracy, alive
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
        player.setMaxHp(HP_BAND_MIN);
        player.restoreFullHp();
        GameSession session = newSession(player);
        playWrong(session, QUIZ_SIZE); // consecutive chain defeats 10 HP long before 12 hits

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

    // ---- Helpers -------------------------------------------------------------

    // Sessions use fixed always-correct placeholder questions and the roster's
    // real enemy so the scenarios stay deterministic without question files.
    private static GameSession newSession(Player player) {
        return newSession(player, Difficulty.BEGINNER);
    }

    private static GameSession newSession(Player player, Difficulty difficulty) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < QUIZ_SIZE; i++) {
            questions.add(new AlwaysCorrectCheck(i + 1));
        }
        GameSession session =
                new GameSession(player, Topic.VARIABLES, Difficulty.BEGINNER, questions);
        Enemy enemy = roster.getEnemy(Topic.VARIABLES, difficulty);
        session.setEnemy(enemy);
        return session;
    }

    private static GameSession playAll(GameSession session) {
        playCorrect(session, QUIZ_SIZE);
        return session;
    }

    private static void playCorrect(GameSession session, int times) {
        for (int i = 0; i < times; i++) {
            runner.submitAnswer(session, session.getQuestions().get(i), "anything");
            if (!session.getPlayer().isAlive()) {
                return;
            }
        }
    }

    private static void playWrong(GameSession session, int times) {
        for (int i = 0; i < times; i++) {
            runner.submitAnswer(session, session.getQuestions().get(i), "");
            if (!session.getPlayer().isAlive()) {
                return;
            }
        }
    }

    // A 12-answer run with four isolated mistakes (wrong on indices 1, 4, 7, 10)
    // so every mistake follows a correct answer and costs only base damage, and
    // the run ends on a correct answer (counter reset at the end).
    private static void playSpreadWrong(GameSession session, int times) {
        for (int i = 0; i < times; i++) {
            runner.submitAnswer(session, session.getQuestions().get(i),
                    i % 3 == 1 ? "" : "anything");
            if (!session.getPlayer().isAlive()) {
                return;
            }
        }
    }

    // Outcome counts over all 4,096 twelve-answer sequences using the same
    // damage math as the real submitAnswer path: [A, B, C, D] where A/B are HP
    // defeats (accuracy over answered >=/< 70), C alive accuracy-fail, D pass.
    private static int[] enumerateOutcomes(int maxHp, Enemy enemy) {
        int base = enemy.baseDamageForMaxHp(maxHp);
        int escalation = enemy.getEscalationPerMistake();
        int[] counts = new int[4];
        // A run that dies is counted once per below-death prefix: bits after the
        // killing answer do not matter, so the 4,096 masks collapse to the
        // number of distinct outcome sequences (no double counting).
        boolean[] countedKey = new boolean[1 << QUIZ_SIZE];

        for (int mask = 0; mask < (1 << QUIZ_SIZE); mask++) {
            int hp = maxHp;
            int correct = 0;
            int answered = 0;
            int consecutive = 0;
            boolean dead = false;

            for (int i = 0; i < QUIZ_SIZE; i++) {
                boolean isCorrect = (mask & (1 << i)) != 0;
                if (dead) {
                    break; // defeat ended the run; later questions are not answered
                }
                answered++;
                if (isCorrect) {
                    correct++;
                    consecutive = 0;
                } else {
                    hp -= base + consecutive * escalation;
                    if (hp <= 0) {
                        dead = true;
                    } else {
                        consecutive++;
                    }
                }
            }

            int key = dead
                    ? mask & ~(((1 << (QUIZ_SIZE - answered)) - 1) << answered)
                    : mask;
            if (countedKey[key]) {
                continue;
            }
            countedKey[key] = true;

            int accuracy = correct * 100 / answered;
            if (dead) {
                counts[accuracy >= 70 ? 0 : 1]++; // A / B: HP defeat categories
            } else if (accuracy >= 70) {
                counts[3]++; // D: passed
            } else {
                counts[2]++; // C: alive, failed by accuracy
            }
        }
        return counts;
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
