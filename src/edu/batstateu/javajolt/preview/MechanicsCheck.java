package edu.batstateu.javajolt.preview;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.PowerUpResult;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.InputMode;
import edu.batstateu.javajolt.enums.JoltMessage;
import edu.batstateu.javajolt.service.AnswerTimerService;
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

// Dev tool: in-memory checks for Phase 2-7 mechanics - D1 level boundaries,
// D3 max HP bands, end-of-quiz settlement rules, the Phase 3 enemy damage
// model (lethality, survivability, independent defeat categories), Phase 4
// streak scoring, the Phase 5-6 power-ups, and Phase 7 timeout rules. Everything
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
        checkJoltMessages();
        checkPowerUps();
        checkHeal();
        checkAnswerTimer();
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
    // a mistake resets the streak and the next award to the base, and the session records the
    // running/best streak and the points of the most recent answer (UI feedback).
    private static void checkStreakScoring() {
        ScoreService scoreService = new ScoreService();

        check("score: streak 1 = 10", scoreService.pointsForCorrect(1) == 10);
        check("score: streak 3 = 14", scoreService.pointsForCorrect(3) == 14);

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

    private static void checkJoltMessages() {
        JoltMessage[] expected = {JoltMessage.NONE, JoltMessage.NONE,
                JoltMessage.SPARK, JoltMessage.SPARK, JoltMessage.SPARK,
                JoltMessage.JOLT, JoltMessage.JOLT, JoltMessage.JOLT,
                JoltMessage.SURGE, JoltMessage.SURGE, JoltMessage.SURGE, JoltMessage.JAVAJOLT};
        for (int streak = 1; streak <= QUIZ_SIZE; streak++) {
            check("Jolt: message at streak " + streak,
                    JoltMessage.afterAnswer(streak - 1, streak, QUIZ_SIZE) == expected[streak - 1]);
        }
        check("Jolt: perfect nine beats SURGE", JoltMessage.afterAnswer(8, 9, 9) == JoltMessage.JAVAJOLT);
        check("Jolt: perfect five beats SPARK", JoltMessage.afterAnswer(4, 5, 5) == JoltMessage.JAVAJOLT);
        check("Jolt: a two-question perfect run has no milestone", JoltMessage.afterAnswer(1, 2, 2) == JoltMessage.NONE);
        check("Jolt: lost streak four short circuits", JoltMessage.afterAnswer(4, 0, QUIZ_SIZE) == JoltMessage.SHORT_CIRCUIT);
        check("Jolt: a mistake without a streak is silent", JoltMessage.afterAnswer(0, 0, QUIZ_SIZE) == JoltMessage.NONE);
        check("Jolt: shortest configured lost streak short circuits",
                JoltMessage.afterAnswer(JoltMessage.SHORT_CIRCUIT_MIN_STREAK, 0, QUIZ_SIZE) == JoltMessage.SHORT_CIRCUIT);

        GameSession session = timerSession(Difficulty.INTERMEDIATE, 15);
        check("Jolt: new session starts empty", session.getLastJoltMessage() == JoltMessage.NONE);
        for (int streak = 1; streak <= QUIZ_SIZE; streak++) {
            session.recordCorrect(new ScoreService().pointsForCorrect(streak));
            check("Jolt: session records correct message " + streak,
                    session.getLastJoltMessage() == expected[streak - 1]);
        }
        check("Jolt: score and best streak unchanged",
                session.getScoreEarned() == FULL_RUN_STREAK_SCORE && session.getBestStreak() == QUIZ_SIZE);
        session.recordTimeout(1);
        check("Jolt: timeout preserves message", session.getLastJoltMessage() == JoltMessage.JAVAJOLT);
        session.getPlayer().takeDamage(5);
        check("Jolt: Heal preserves message",
                powerUps.use(session, PowerUp.HEAL, session.getQuestions().get(0)).applied()
                        && session.getLastJoltMessage() == JoltMessage.JAVAJOLT);
        session.recordMistake(0);
        check("Jolt: session records a short circuit", session.getLastJoltMessage() == JoltMessage.SHORT_CIRCUIT);
        session.recordCorrect(new ScoreService().pointsForCorrect(1));
        check("Jolt: first correct answer after reset clears message",
                session.getLastJoltMessage() == JoltMessage.NONE && session.getCurrentStreak() == 1
                        && session.getLastPoints() == 10 && session.getBestStreak() == QUIZ_SIZE);
        session.recordMistake(0);
        session.recordMistake(0);
        check("Jolt: repeated mistake clears short circuit", session.getLastJoltMessage() == JoltMessage.NONE);
        check("Jolt: replacement session starts empty",
                newSession(session.getPlayer()).getLastJoltMessage() == JoltMessage.NONE);
    }

    // Phase 5 power-ups: fixed unlock levels, one use of each unlocked
    // power-up per quiz, Hint only ever removes a wrong choice (never the
    // correct one, verified over 200 runs; non-multiple-choice use is not
    // consumed), Shield zeroes the next mistake without stopping the
    // escalation chain, Heal restores HP, and Time Stop freezes the current question.
    private static void checkPowerUps() {
        // The level curve maps XP/50 to level, so the probe XP values are exact.
        Player level4 = new Player("Check", 200);
        for (PowerUp powerUp : PowerUp.values()) {
            check("power-ups: level 4 does not unlock " + powerUp.getLabel(),
                    !powerUps.isUnlocked(level4, powerUp));
        }
        Player level5 = new Player("Check", 250);
        check("power-ups: level 5 unlocks Hint", powerUps.isUnlocked(level5, PowerUp.HINT));
        check("power-ups: level 5 does not unlock Shield/Heal/Time Stop",
                !powerUps.isUnlocked(level5, PowerUp.SHIELD)
                        && !powerUps.isUnlocked(level5, PowerUp.TIME_STOP)
                        && !powerUps.isUnlocked(level5, PowerUp.HEAL));
        Player level10 = new Player("Check", 500);
        check("power-ups: level 10 unlocks Hint and Shield, not Heal/Time Stop",
                powerUps.isUnlocked(level10, PowerUp.HINT)
                        && powerUps.isUnlocked(level10, PowerUp.SHIELD)
                        && !powerUps.isUnlocked(level10, PowerUp.TIME_STOP)
                        && !powerUps.isUnlocked(level10, PowerUp.HEAL));
        check("power-ups: enum order and exact levels are Hint 5 / Shield 10 / Heal 15 / Time Stop 20",
                List.of(PowerUp.values()).equals(List.of(
                        PowerUp.HINT, PowerUp.SHIELD, PowerUp.HEAL, PowerUp.TIME_STOP))
                        && PowerUp.HINT.getUnlockLevel() == 5 && PowerUp.SHIELD.getUnlockLevel() == 10
                        && PowerUp.HEAL.getUnlockLevel() == 15 && PowerUp.TIME_STOP.getUnlockLevel() == 20);
        Player level15 = new Player("Check", 750);
        check("power-ups: level 15 unlocks Heal, not Time Stop",
                powerUps.isUnlocked(level15, PowerUp.HEAL)
                        && !powerUps.isUnlocked(level15, PowerUp.TIME_STOP));
        Player level20 = new Player("Check", 1000);
        for (PowerUp powerUp : PowerUp.values()) {
            check("power-ups: level 20 unlocks " + powerUp.getLabel(),
                    powerUps.isUnlocked(level20, powerUp));
        }
        check("power-ups: unlockedBetween(2, 5) = [Hint]",
                powerUps.unlockedBetween(2, 5).equals(List.of(PowerUp.HINT)));
        check("power-ups: unlockedBetween(5, 10) = [Shield]",
                powerUps.unlockedBetween(5, 10).equals(List.of(PowerUp.SHIELD)));
        check("power-ups: unlockedBetween(10, 15) = [Heal]",
                powerUps.unlockedBetween(10, 15).equals(List.of(PowerUp.HEAL)));
        check("power-ups: unlockedBetween(15, 20) = [Time Stop]",
                powerUps.unlockedBetween(15, 20).equals(List.of(PowerUp.TIME_STOP)));
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

        // Time Stop freezes the active question and consumes its quiz use.
        GameSession stopSession = newSession(master);
        stopSession.setCurrentQuestionIndex(1);
        PowerUpResult stopResult = powerUps.use(stopSession, PowerUp.TIME_STOP, hintable);
        check("power-ups: Time Stop freezes the current question",
                stopResult.applied() && stopSession.hasUsed(PowerUp.TIME_STOP)
                        && stopSession.isQuestionTimerFrozen());

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
        check("applicable: Shield applies to any current question",
                powerUps.isApplicable(PowerUp.SHIELD, probeSession, identification));
        probeSession.setCurrentQuestionIndex(1);
        check("applicable: Time Stop applies to a running question timer",
                powerUps.isApplicable(PowerUp.TIME_STOP, probeSession, hintable));

    }

    private static void checkHeal() {
        for (PowerUp powerUp : PowerUp.values()) {
            Player below = new Player("Check", (int) LevelService.totalXpForLevel(powerUp.getUnlockLevel()) - 1);
            Player at = new Player("Check", (int) LevelService.totalXpForLevel(powerUp.getUnlockLevel()));
            check("power-ups: exact unlock boundary for " + powerUp.getLabel(),
                    !powerUps.isUnlocked(below, powerUp) && powerUps.isUnlocked(at, powerUp));
        }

        int[][] expectedAmounts = {{2, 3}, {3, 5}, {5, 7}};
        int[] maxHpValues = {10, 15};
        int[] expectedPercents = {20, 35, 50};
        for (Difficulty difficulty : Difficulty.values()) {
            check("heal: percentage " + difficulty,
                    powerUps.getHealPercent(difficulty) == expectedPercents[difficulty.ordinal()]);
            check("heal: minimum recovery is 1 HP " + difficulty,
                    powerUps.getHealAmount(1, difficulty) == 1);
            for (int i = 0; i < maxHpValues.length; i++) {
                int maxHp = maxHpValues[i];
                int expected = expectedAmounts[difficulty.ordinal()][i];
                GameSession run = timerSession(difficulty, maxHp);
                Player player = run.getPlayer();
                Question current = run.getQuestions().get(0);
                check("heal: full HP is not applicable " + difficulty + " HP " + maxHp,
                        !powerUps.isApplicable(PowerUp.HEAL, run, current));
                PowerUpResult full = powerUps.use(run, PowerUp.HEAL, current);
                check("heal: full HP rejected with message, unconsumed " + difficulty + " HP " + maxHp,
                        !full.applied() && !full.message().isBlank()
                                && !run.hasUsed(PowerUp.HEAL) && player.getHp() == maxHp);

                player.takeDamage(maxHp - 1);
                PowerUpResult healed = powerUps.use(run, PowerUp.HEAL, current);
                check("heal: integer recovery and notice " + difficulty + " HP " + maxHp,
                        powerUps.getHealAmount(maxHp, difficulty) == expected
                                && healed.applied() && player.getHp() == 1 + expected
                                && healed.message().equals("Heal restored " + expected + " HP.")
                                && run.hasUsed(PowerUp.HEAL));
                int hpAfter = player.getHp();
                check("heal: second use rejected while injured " + difficulty + " HP " + maxHp,
                        !powerUps.use(run, PowerUp.HEAL, current).applied() && player.getHp() == hpAfter);

                GameSession capped = timerSession(difficulty, maxHp);
                capped.getPlayer().takeDamage(1);
                PowerUpResult cappedResult = powerUps.use(capped, PowerUp.HEAL, current);
                check("heal: capped recovery reports actual HP restored " + difficulty + " HP " + maxHp,
                        cappedResult.applied() && capped.getPlayer().getHp() == maxHp
                                && cappedResult.message().equals("Heal restored 1 HP."));
            }
        }

        Player locked = new Player("Check", (int) LevelService.totalXpForLevel(15) - 1);
        GameSession lockedRun = newSession(locked);
        locked.takeDamage(5);
        check("heal: locked below level 15 and unconsumed",
                !powerUps.use(lockedRun, PowerUp.HEAL, lockedRun.getQuestions().get(0)).applied()
                        && !lockedRun.hasUsed(PowerUp.HEAL) && locked.getHp() == 5);

        // Exercise both a live correct streak and a live mistake chain.
        for (boolean afterMistake : new boolean[] {false, true}) {
            GameSession run = timerSession(Difficulty.INTERMEDIATE, 15);
            playCorrect(run, 2);
            if (afterMistake) {
                playWrong(run, 1);
            } else {
                run.getPlayer().takeDamage(5);
            }
            powerUps.use(run, PowerUp.SHIELD, run.getQuestions().get(0));
            int score = run.getScoreEarned();
            int xp = run.getProvisionalXp();
            int streak = run.getCurrentStreak();
            int chain = run.getConsecutiveMistakes();
            int accuracy = run.getAccuracyPercent();
            int answered = run.getAnsweredCount();
            int lastPoints = run.getLastPoints();
            int lastDamage = run.getLastDamage();
            PowerUpResult result = powerUps.use(run, PowerUp.HEAL, run.getQuestions().get(0));
            check("heal: only HP and usage change, after mistake = " + afterMistake,
                    result.applied() && run.getScoreEarned() == score && run.getProvisionalXp() == xp
                            && run.getCurrentStreak() == streak && run.getBestStreak() == 2
                            && run.getConsecutiveMistakes() == chain && run.getAccuracyPercent() == accuracy
                            && run.getAnsweredCount() == answered && run.getLastPoints() == lastPoints
                            && run.getLastDamage() == lastDamage && run.isShieldArmed()
                            && run.getPlayer().getXp() == 1000 && run.getPlayer().getScore() == 0
                            && run.getPlayer().getProgress().copyCompleted().isEmpty()
                            && run.getPlayer().copyHighScores().isEmpty());
            runner.submitAnswer(run, run.getQuestions().get(0), "anything");
            check("heal: subsequent scoring follows the same streak, after mistake = " + afterMistake,
                    run.getLastPoints() == (afterMistake ? 10 : 14));
        }
        GameSession previous = timerSession(Difficulty.INTERMEDIATE, 15);
        Player returning = previous.getPlayer();
        returning.takeDamage(5);
        powerUps.use(previous, PowerUp.HEAL, previous.getQuestions().get(0));
        GameSession fresh = newSession(returning);
        returning.takeDamage(5);
        check("heal: a new quiz restores availability",
                previous.hasUsed(PowerUp.HEAL) && !fresh.hasUsed(PowerUp.HEAL)
                        && powerUps.use(fresh, PowerUp.HEAL, fresh.getQuestions().get(0)).applied());
    }

    private static void checkAnswerTimer() {
        AnswerTimerService timer = new AnswerTimerService();
        int[][] limits = {{30, 120}, {20, 90}, {15, 75}};
        for (Difficulty difficulty : Difficulty.values()) {
            for (InputMode mode : InputMode.values()) {
                check("timer: limit " + difficulty + " " + mode,
                        timer.getTimeLimitSeconds(difficulty, mode)
                                == limits[difficulty.ordinal()][mode.ordinal()]);
            }
            for (int hp = HP_BAND_MIN; hp <= HP_BAND_MAX; hp++) {
                GameSession run = timerSession(difficulty, hp);
                run.recordMistake(0);
                int expected = difficulty == Difficulty.BEGINNER ? 0
                        : difficulty == Difficulty.INTERMEDIATE ? 1
                        : Enemy.baseDamageForMaxHp(hp);
                check("timer: base-only damage " + difficulty + " HP " + hp,
                        timer.getTimeoutDamage(run) == expected);
                timer.applyTimeout(run);
                timer.applyTimeout(run);
                check("timer: applied once, mistake chain unchanged " + difficulty + " HP " + hp,
                        run.getPlayer().getHp() == hp - expected
                                && run.isQuestionTimedOut() && run.getLastDamage() == expected
                                && run.getConsecutiveMistakes() == 1 && run.getAnsweredCount() == 1);
                run.setCurrentQuestionIndex(1);
                timer.applyTimeout(run);
                check("timer: same index cannot re-arm timeout " + difficulty + " HP " + hp,
                        run.getPlayer().getHp() == hp - expected);
                run.setCurrentQuestionIndex(2);
                check("timer: next question resets timeout " + difficulty + " HP " + hp,
                        !run.isQuestionTimedOut());
                timer.applyTimeout(run);
                check("timer: next question can expire " + difficulty + " HP " + hp,
                        run.getPlayer().getHp() == hp - 2 * expected);
            }
            GameSession shield = timerSession(difficulty, 15);
            powerUps.use(shield, PowerUp.SHIELD, shield.getQuestions().get(0));
            timer.applyTimeout(shield);
            check("timer: Shield interaction " + difficulty,
                    shield.getPlayer().getHp() == 15 && shield.getLastDamage() == 0
                            && shield.isShieldArmed() == (difficulty == Difficulty.BEGINNER));
            timer.applyTimeout(shield);
            check("timer: repeated expiry leaves Shield state unchanged " + difficulty,
                    shield.isShieldArmed() == (difficulty == Difficulty.BEGINNER));
        }
        GameSession streak = timerSession(Difficulty.ADVANCED, 15);
        playCorrect(streak, 2);
        int score = streak.getScoreEarned();
        int xp = streak.getProvisionalXp();
        timer.applyTimeout(streak);
        check("timer: correct streak, accuracy, score, and XP preserved",
                streak.getCurrentStreak() == 2 && streak.getBestStreak() == 2
                        && streak.getAnsweredCount() == 2 && streak.getMistakeCount() == 0
                        && streak.getAccuracyPercent() == 100 && streak.getConsecutiveMistakes() == 0
                        && streak.getScoreEarned() == score && streak.getProvisionalXp() == xp
                        && streak.getLastPoints() == 12);
        runner.submitAnswer(streak, streak.getQuestions().get(0), "anything");
        check("timer: correct answer after expiry still earns streak points",
                streak.getAnsweredCount() == 3 && streak.getCurrentStreak() == 3
                        && streak.getLastPoints() == 14);
        GameSession frozen = timerSession(Difficulty.ADVANCED, 15);
        powerUps.use(frozen, PowerUp.SHIELD, frozen.getQuestions().get(0));
        PowerUpResult stop = powerUps.use(frozen, PowerUp.TIME_STOP, frozen.getQuestions().get(0));
        timer.applyTimeout(frozen);
        check("timer: Time Stop freezes without expiry, damage, or Shield consumption",
                stop.applied() && frozen.isQuestionTimerFrozen() && !frozen.isQuestionTimedOut()
                        && frozen.getPlayer().getHp() == 15 && frozen.isShieldArmed());
        frozen.setCurrentQuestionIndex(1);
        check("timer: same question preserves frozen state", frozen.isQuestionTimerFrozen());
        frozen.setCurrentQuestionIndex(2);
        check("timer: next question unfreezes but cannot reuse Time Stop",
                !frozen.isQuestionTimerFrozen()
                        && !powerUps.use(frozen, PowerUp.TIME_STOP, frozen.getQuestions().get(1)).applied());
        timer.applyTimeout(frozen);
        check("timer: Shield still absorbs next question timeout",
                frozen.isQuestionTimedOut() && !frozen.isShieldArmed()
                        && frozen.getPlayer().getHp() == 15);
        GameSession expired = timerSession(Difficulty.ADVANCED, 15);
        timer.applyTimeout(expired);
        check("timer: expired countdown does not consume Time Stop",
                !powerUps.use(expired, PowerUp.TIME_STOP, expired.getQuestions().get(0)).applied()
                        && !expired.hasUsed(PowerUp.TIME_STOP));
        GameSession unstarted = timerSession(Difficulty.ADVANCED, 15);
        unstarted.setCurrentQuestionIndex(0);
        timer.applyTimeout(unstarted);
        check("timer: unstarted question cannot expire or consume Time Stop",
                !unstarted.isQuestionTimedOut()
                        && !powerUps.use(unstarted, PowerUp.TIME_STOP, unstarted.getQuestions().get(0)).applied()
                        && !unstarted.hasUsed(PowerUp.TIME_STOP));
        GameSession defeat = timerSession(Difficulty.ADVANCED, 15);
        playCorrect(defeat, 2);
        defeat.getPlayer().setHp(3);
        timer.applyTimeout(defeat);
        check("timer: defeat is not a mistake and fails despite perfect accuracy",
                !defeat.getPlayer().isAlive() && defeat.getAccuracyPercent() == 100
                        && defeat.getAnsweredCount() == 2 && defeat.getMistakeCount() == 0
                        && defeat.getCurrentStreak() == 2 && !runner.hasPassed(defeat));
        runner.settleAttempt(defeat);
        runner.settleAttempt(defeat);
        check("timer: defeat settles score once and gives no XP or completion",
                defeat.getPlayer().getScore() == 22 && defeat.getPlayer().getXp() == 1000
                        && !defeat.getPlayer().isComplete(Topic.VARIABLES, Difficulty.ADVANCED));
        GameSession settled = timerSession(Difficulty.ADVANCED, 15);
        settled.markSettled();
        timer.applyTimeout(settled);
        check("timer: settled attempt ignores expiry", !settled.isQuestionTimedOut());
        GameSession fresh = timerSession(Difficulty.ADVANCED, 15);
        check("timer: new quiz resets Time Stop and per-question state",
                !fresh.hasUsed(PowerUp.TIME_STOP) && !fresh.isQuestionTimedOut()
                        && !fresh.isQuestionTimerFrozen());
    }

    private static GameSession timerSession(Difficulty difficulty, int maxHp) {
        Player player = new Player("Timer check", 1000);
        player.setMaxHp(maxHp);
        player.restoreFullHp();
        GameSession session = newSession(player);
        session.setDifficulty(difficulty);
        session.setEnemy(roster.getEnemy(Topic.VARIABLES, difficulty));
        session.setCurrentQuestionIndex(1);
        return session;
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
