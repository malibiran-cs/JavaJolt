package edu.batstateu.javajolt.preview;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.PowerUpResult;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.Enemy;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LevelRewardService;
import edu.batstateu.javajolt.service.LevelService;
import edu.batstateu.javajolt.service.PowerUpService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.ScoreService;
import edu.batstateu.javajolt.types.IdentificationQuestion;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;

import java.util.ArrayList;
import java.util.List;

// Dev tool: in-memory checks for Phase 2-6 mechanics - D1 level boundaries,
// D3 max HP bands, end-of-quiz settlement rules, the Phase 3 enemy damage
// model (lethality, survivability, independent defeat categories), Phase 4
// streak scoring, and the Phase 5-6 power-ups. Everything
// runs on fresh Player objects in memory; no save path is ever opened.
public class MechanicsCheck {

    // 12 questions matches the real quiz size, so xpPerCorrect(12) = 2 exactly
    // and a full quiz has 4 skills per tier (foundational 1-4, application 5-8,
    // challenge 9-12).
    private static final int QUIZ_SIZE = 12;
    private static final int EXPECTED_XP_PER_CORRECT = 2;
    private static final int EXPECTED_COMPLETION_BONUS = 50;
    // Streak score contract (ScoreService): consecutive correct answers pay
    // 10 + 2 * (streak - 1), so an uninterrupted 12-correct run sums to 252.
    private static final int FULL_RUN_STREAK_SCORE = 252;
    private static final int HP_BAND_MIN = 10;
    private static final int HP_BAND_MAX = 15;

    private static final QuestionService questionService =
            new QuestionService(new edu.batstateu.javajolt.data.QuestionBank());
    private static final LessonRunnerService runner =
            new LessonRunnerService(questionService, null,
                    new EnemyRoster(), new ScoreService(), null);
    private static final EnemyRoster roster = new EnemyRoster();
    private static final PowerUpService powerUps = new PowerUpService();

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
        checkStreakScoring();
        checkPowerUps();
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

    // Streak scoring (Phase 4): consecutive correct answers pay 10/12/14/16...,
    // a mistake resets the streak and the next award to the base, the boost
    // multiplier stays pure ScoreService math, and the session records both the
    // running/best streak and the points of the most recent answer (UI feedback).
    private static void checkStreakScoring() {
        ScoreService scoreService = new ScoreService();

        // Phase 5 boost math, already fixed: base 10 * 2, streak steps unchanged
        // - streak 3 boosted is 24 (20 + 2 + 2), never 28 or 40.
        check("score: pointsForCorrect(false, 3) = 14",
                scoreService.pointsForCorrect(false, 3) == 14);
        check("score: pointsForCorrect(true, 1) = 20 (boosted base)",
                scoreService.pointsForCorrect(true, 1) == 20);
        check("score: pointsForCorrect(true, 3) = 24, not 28 or 40",
                scoreService.pointsForCorrect(true, 3) == 24);

        Player player = new Player("Check", 0);
        GameSession session = newSession(player);
        int[] expectedPoints = {10, 12, 14, 16};
        int total = 0;
        for (int i = 0; i < expectedPoints.length; i++) {
            int before = session.getScoreEarned();
            runner.submitAnswer(session, session.getQuestions().get(i), "anything");
            int awarded = session.getScoreEarned() - before;
            check("streak: answer " + (i + 1) + " awards " + expectedPoints[i]
                            + " points (recorded as lastPoints)",
                    awarded == expectedPoints[i]
                            && session.getLastPoints() == expectedPoints[i]);
            total += awarded;
        }
        check("streak: 4 consecutive correct total = 52", total == 52);
        check("streak: current streak 4, best streak 4",
                session.getCurrentStreak() == 4 && session.getBestStreak() == 4);

        // A mistake clears the streak and the points indicator (plain display).
        runner.submitAnswer(session, session.getQuestions().get(4), "");
        check("streak: mistake resets the current streak to 0",
                session.getCurrentStreak() == 0);
        check("streak: mistake clears lastPoints", session.getLastPoints() == 0);
        check("streak: best streak survives the reset", session.getBestStreak() == 4);

        // After a mistake the awards restart at base: 10/12/14.
        int again = 0;
        for (int i = 5; i < 8; i++) {
            int before = session.getScoreEarned();
            runner.submitAnswer(session, session.getQuestions().get(i), "anything");
            again += session.getScoreEarned() - before;
        }
        check("streak: post-mistake awards restart 10/12/14 = 36", again == 36);

        // An uninterrupted 12-correct run: 252 points, best streak 12.
        Player fullPlayer = new Player("Check", 0);
        GameSession fullSession = playAll(newSession(fullPlayer));
        check("streak: 12-correct run = 252 points, best streak 12",
                fullSession.getScoreEarned() == FULL_RUN_STREAK_SCORE
                        && fullSession.getBestStreak() == 12);
    }

    // Phase 5 power-ups: fixed unlock levels, one use of each unlocked
    // power-up per quiz, Hint only ever removes a wrong choice (never the
    // correct one, verified over 200 runs; non-multiple-choice use is not
    // consumed), Shield zeroes the next mistake without stopping the
    // escalation chain, Point Boost survives a mistake and boosts only the
    // next correct answer, and Time Stop stays locked until Phase 7.
    private static void checkPowerUps() {
        // The level curve maps XP/50 to level, so the probe XP values are exact.
        Player level4 = new Player("Check", 200);
        for (PowerUp powerUp : PowerUp.values()) {
            check("power-ups: level 4 does not unlock " + powerUp.getLabel(),
                    !powerUps.isUnlocked(level4, powerUp));
        }
        Player level5 = new Player("Check", 250);
        check("power-ups: level 5 unlocks Hint", powerUps.isUnlocked(level5, PowerUp.HINT));
        check("power-ups: level 5 does not unlock Shield/Time Stop/Point Boost",
                !powerUps.isUnlocked(level5, PowerUp.SHIELD)
                        && !powerUps.isUnlocked(level5, PowerUp.TIME_STOP)
                        && !powerUps.isUnlocked(level5, PowerUp.POINT_BOOST));
        Player level10 = new Player("Check", 500);
        check("power-ups: level 10 unlocks Hint and Shield, not Time Stop/Point Boost",
                powerUps.isUnlocked(level10, PowerUp.HINT)
                        && powerUps.isUnlocked(level10, PowerUp.SHIELD)
                        && !powerUps.isUnlocked(level10, PowerUp.TIME_STOP)
                        && !powerUps.isUnlocked(level10, PowerUp.POINT_BOOST));
        Player level20 = new Player("Check", 1000);
        for (PowerUp powerUp : PowerUp.values()) {
            check("power-ups: level 20 unlocks " + powerUp.getLabel(),
                    powerUps.isUnlocked(level20, powerUp));
        }
        check("power-ups: unlockedBetween(2, 5) = [Hint]",
                powerUps.unlockedBetween(2, 5).equals(List.of(PowerUp.HINT)));
        check("power-ups: unlockedBetween(5, 10) = [Shield]",
                powerUps.unlockedBetween(5, 10).equals(List.of(PowerUp.SHIELD)));
        check("power-ups: unlockedBetween(1, 20) = all four",
                powerUps.unlockedBetween(1, 20).size() == 4);
        check("power-ups: unlockedBetween(20, 25) is empty",
                powerUps.unlockedBetween(20, 25).isEmpty());

        MultipleChoiceQuestion hintable = preparedHintableQuestion();

        // One use of each unlocked power-up per quiz; the second use is rejected.
        Player master = new Player("Check", 1000); // level 20: everything unlocked
        GameSession shieldSession = newSession(master);
        PowerUpResult shieldFirst = powerUps.use(shieldSession, PowerUp.SHIELD, hintable);
        PowerUpResult shieldSecond = powerUps.use(shieldSession, PowerUp.SHIELD, hintable);
        check("power-ups: Shield applies once, second use rejected",
                shieldFirst.applied() && shieldSession.hasUsed(PowerUp.SHIELD)
                        && !shieldSecond.applied());
        GameSession boostSession = newSession(master);
        PowerUpResult boostFirst = powerUps.use(boostSession, PowerUp.POINT_BOOST, hintable);
        PowerUpResult boostSecond = powerUps.use(boostSession, PowerUp.POINT_BOOST, hintable);
        check("power-ups: Point Boost applies once, second use rejected",
                boostFirst.applied() && boostSession.hasUsed(PowerUp.POINT_BOOST)
                        && !boostSecond.applied());
        GameSession hintOnceSession = newSession(master);
        PowerUpResult hintFirst = powerUps.use(hintOnceSession, PowerUp.HINT, hintable);
        PowerUpResult hintSecond = powerUps.use(hintOnceSession, PowerUp.HINT, hintable);
        check("power-ups: Hint applies once, second use rejected",
                hintFirst.applied() && hintOnceSession.hasUsed(PowerUp.HINT)
                        && !hintSecond.applied());

        // 200 Hint runs on a shuffled 4-choice question: always exactly one
        // removed choice, never the correct one.
        boolean allRunsOk = true;
        for (int i = 0; i < 200; i++) {
            GameSession hintSession = newSession(master);
            PowerUpResult result = powerUps.use(hintSession, PowerUp.HINT, hintable);
            if (!result.applied()
                    || result.removedChoices().size() != 1
                    || isCorrectChoice(result.removedChoices().get(0), hintable.getAnswer())) {
                allRunsOk = false;
                break;
            }
        }
        check("power-ups: 200 Hint runs - exactly one choice, never the correct one",
                allRunsOk);

        // Hint on a question without choices is not applied and not consumed.
        IdentificationQuestion identification = new IdentificationQuestion(
                998, "Power-ups identification question", Topic.VARIABLES,
                Difficulty.BEGINNER, "placeholder explanation", List.of("answer"));
        GameSession idSession = newSession(master);
        PowerUpResult idResult = powerUps.use(idSession, PowerUp.HINT, identification);
        check("power-ups: Hint on a non-multiple-choice question is not applied",
                !idResult.applied() && idResult.removedChoices().isEmpty());
        check("power-ups: Hint on a non-multiple-choice question is not consumed",
                !idSession.hasUsed(PowerUp.HINT));

        // Time Stop waits for the Phase 7 timer: unused, not consumed.
        GameSession stopSession = newSession(master);
        PowerUpResult stopResult = powerUps.use(stopSession, PowerUp.TIME_STOP, hintable);
        check("power-ups: Time Stop is not applied until Phase 7",
                !stopResult.applied() && !stopSession.hasUsed(PowerUp.TIME_STOP));

        // Shield: the shielded mistake deals 0 damage but still counts as a
        // mistake (chain incremented, correct streak reset); the NEXT
        // unshielded mistake uses the escalated damage.
        Player shieldPlayer = new Player("Check", 1000);
        shieldPlayer.setMaxHp(13); // base 3, Beginner escalation 2
        shieldPlayer.restoreFullHp();
        GameSession shieldRun = newSession(shieldPlayer);
        playCorrect(shieldRun, 2); // build a streak so the reset is observable
        powerUps.use(shieldRun, PowerUp.SHIELD, hintable);
        int hpBeforeShield = shieldPlayer.getHp();
        runner.submitAnswer(shieldRun, shieldRun.getQuestions().get(2), ""); // shielded
        check("shield: a shielded mistake deals 0 damage",
                shieldPlayer.getHp() == hpBeforeShield
                        && shieldRun.getLastDamage() == 0);
        check("shield: the shielded mistake still counts and resets the streak",
                shieldRun.getConsecutiveMistakes() == 1
                        && shieldRun.getCurrentStreak() == 0);
        runner.submitAnswer(shieldRun, shieldRun.getQuestions().get(3), ""); // unshielded
        check("shield: the next unshielded mistake uses escalated damage (3 + 1*2 = 5)",
                shieldRun.getLastDamage() == 5
                        && shieldPlayer.getHp() == hpBeforeShield - 5);

        // Point Boost: survives a mistake, doubles only the next correct answer.
        Player boostPlayer = new Player("Check", 1000);
        GameSession boostRun = newSession(boostPlayer);
        powerUps.use(boostRun, PowerUp.POINT_BOOST, hintable);
        playWrong(boostRun, 1); // mistake; the boost must survive
        int beforeBoost = boostRun.getScoreEarned();
        runner.submitAnswer(boostRun, boostRun.getQuestions().get(1), "anything");
        check("boost: the next correct answer after a mistake pays 20 (doubled base)",
                boostRun.getScoreEarned() - beforeBoost == 20);
        boolean flaggedAfterBoost = boostRun.isLastAnswerBoosted();
        int beforePlain = boostRun.getScoreEarned();
        runner.submitAnswer(boostRun, boostRun.getQuestions().get(2), "anything");
        check("boost: only the next correct answer is boosted (12 afterwards)",
                boostRun.getScoreEarned() - beforePlain == 12);
        check("boost: no XP involved",
                boostPlayer.getXp() == 1000 && boostRun.getProvisionalXp() > 0);

        // Phase 6 supportsHint: the button-state hook, no random pick involved
        // (the randomness stays inside getHintChoicesToRemove).
        check("hint: a 4-choice multiple-choice question supports a hint",
                hintable.supportsHint());
        MultipleChoiceQuestion smallQuestion = new MultipleChoiceQuestion(
                997, "Power-ups small check question", Topic.VARIABLES,
                Difficulty.BEGINNER, "placeholder explanation",
                List.of("A. one", "B. two"), "B");
        check("hint: a 2-choice multiple-choice question does not support a hint",
                !smallQuestion.supportsHint());
        check("hint: the default (identification) does not support a hint",
                !identification.supportsHint());

        // Phase 6 isApplicable: display rules for the current question.
        GameSession probeSession = newSession(master);
        check("applicable: Hint applies to a 4-choice multiple-choice question",
                powerUps.isApplicable(PowerUp.HINT, probeSession, hintable));
        check("applicable: Hint does not apply to an identification question",
                !powerUps.isApplicable(PowerUp.HINT, probeSession, identification));
        check("applicable: Shield and Point Boost apply to any current question",
                powerUps.isApplicable(PowerUp.SHIELD, probeSession, identification)
                        && powerUps.isApplicable(PowerUp.POINT_BOOST, probeSession, hintable));
        check("applicable: Time Stop applies nowhere until Phase 7",
                !powerUps.isApplicable(PowerUp.TIME_STOP, probeSession, hintable));

        // Phase 6 display flag: which answer Point Boost actually produced.
        check("boost: the boosted answer is flagged for display",
                flaggedAfterBoost);
        check("boost: the plain answer clears the boost flag for display",
                !boostRun.isLastAnswerBoosted());
    }

    // A 4-choice multiple-choice question prepared like the real quiz does
    // (shuffled choices, updated answer letter).
    private static MultipleChoiceQuestion preparedHintableQuestion() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(
                999, "Power-ups check question", Topic.VARIABLES, Difficulty.BEGINNER,
                "placeholder explanation",
                List.of("A. one", "B. two", "C. three", "D. four"), "C");
        question.prepareForLesson();
        return question;
    }

    private static boolean isCorrectChoice(String choice, String answerLetter) {
        return choice.toUpperCase().startsWith(answerLetter.toUpperCase() + ".");
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
        check("settle pass: score = 12-answer streak run = " + FULL_RUN_STREAK_SCORE,
                player.getScore() == FULL_RUN_STREAK_SCORE);
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
        check("replay: attempt score still commits", player.getScore() == FULL_RUN_STREAK_SCORE);
    }

    // Failed finish (below 70% accuracy): score commits, zero XP, nothing completes.
    // The mistakes are isolated so the run fails by accuracy while still alive.
    private static void checkFailedAttemptEarnsNoXp() {
        Player player = new Player("Check", 0);
        GameSession session = newSession(player);
        playSpreadWrong(session, 12); // 8 correct, 4 isolated wrong: 66% accuracy, alive
        runner.settleAttempt(session);

        check("fail: zero XP", player.getXp() == 0);
        // 8 correct in streak runs of 1/2/2/2/1 between the isolated mistakes:
        // 10 + (10 + 12) + (10 + 12) + (10 + 12) + 10 = 86 streak points.
        check("fail: attempt score commits", player.getScore() == 86);
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
