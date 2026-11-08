
import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.preview.PreviewSandbox;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.ui.GameUI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProgressConsistencyCheck {

    private static final Topic TOPIC = Topic.VARIABLES;
    private static final Difficulty LEVEL = Difficulty.BEGINNER;

    public static void main(String[] args) throws Exception {

        Player player = new Player("Test Player", 0);
        require(!player.isTutorialCompleted(), "New player must start with tutorial incomplete");
        System.out.println("PASS: New player tutorial flag defaults to false");

        LessonRunnerService runner = new LessonRunnerService(
                new QuestionService(new QuestionBank()),
                new LessonService(),
                new edu.batstateu.javajolt.data.EnemyRoster(),
                new edu.batstateu.javajolt.service.ScoreService(),
                new GameUI()
        );

        // TEST 1: Failed attempt
        GameSession failed = attempt(player, runner, 8);

        require(!runner.hasPassed(failed),
                "Failed quiz should not pass");

        require(!player.isComplete(TOPIC, LEVEL),
                "Failed quiz cannot complete lesson");

        require(
                player.getXp() == 0,
                "Failed attempt must award zero XP"
        );

        // 8 correct in streak runs of 1/1/1/1/4 between the spread mistakes:
        // 10 + 10 + 10 + 10 + (10 + 12 + 14 + 16) = 92 streak points.
        require(
                failed.getScoreEarned() == 92,
                "Failed attempt score must still commit"
        );

        require(player.getHighScore(TOPIC, LEVEL) == 92,
                "Failed attempt high score incorrect");

        System.out.println("PASS: Failed attempt");


        // TEST 2: First successful completion
        int beforePass = player.getXp();

        GameSession passed = attempt(player, runner, 9);

        require(runner.hasPassed(passed),
                "9/12 should pass");

        require(player.isComplete(TOPIC, LEVEL),
                "Passed lesson not marked complete");

        int expectedXp = 9 * runner.xpPerCorrect(12)
                + runner.lessonCompleteBonus(LEVEL);

        require(player.getXp() - beforePass == expectedXp,
                "First-pass XP incorrect");

        // 9 correct in streak runs of 1/1/1/6: 10 * 3 + (10..20) = 120 streak points.
        require(player.getHighScore(TOPIC, LEVEL) == 120,
                "High score did not improve");

        System.out.println("PASS: First completion");


        // TEST 3: Save and reload
        Path tempDir = Files.createTempDirectory(
                "javajolt-progress-test-"
        );

        Path saveFile = tempDir.resolve("player.properties");

        try {

            SaveService saves = new SaveService(saveFile);

            saves.save(player, LEVEL);

            SaveData saved = saves.load();

            Player restored = new Player(
                    saved.name(),
                    saved.xp()
            );

            restored.setScore(saved.score());
            restored.setTutorialCompleted(saved.tutorialCompleted());

            restored.restoreProgress(
                    saved.completedTopics()
            );

            restored.restoreHighScores(
                    saved.highScores()
            );

            require(restored.getXp() == player.getXp(),
                    "XP was not restored");

            require(restored.getScore() == player.getScore(),
                    "Score was not restored");
            require(!saved.tutorialCompleted() && !restored.isTutorialCompleted(),
                    "Incomplete tutorial flag was not restored");

            require(restored.isComplete(TOPIC, LEVEL),
                    "Completion was not restored");

            require(restored.getHighScore(TOPIC, LEVEL) == 120,
                    "High score was not restored");

            System.out.println("PASS: Save and load");


            // TEST 4: Replaying a completed lesson
            int beforeReplay = restored.getXp();

            attempt(restored, runner, 12);

            require(restored.getXp() == beforeReplay,
                    "Replay awarded duplicate XP");

            // 12 correct in one uninterrupted run = 252 streak points, a new best.
            require(restored.getHighScore(TOPIC, LEVEL) == 252,
                    "Perfect replay did not improve high score");

            System.out.println("PASS: No duplicate XP");


            // TEST 5: Lower scores must not overwrite records
            attempt(restored, runner, 9);

            // The 9/12 replay scores 120 and must not overwrite the 252 record.
            require(restored.getHighScore(TOPIC, LEVEL) == 252,
                    "Lower score overwrote previous best");

            require(restored.getXp() == beforeReplay,
                    "Later replay awarded XP");

            System.out.println("PASS: High score preservation");

            checkTutorialRoundTrips(saves, restored);

            // Old saves have no power-up fields; only quiz state holds usage.
            Files.writeString(saveFile,
                    "name=Legacy Player\nxp=750\nscore=52\ndifficulty=INTERMEDIATE\n"
                            + "completedTopics=VARIABLES:BEGINNER\nhighScores=VARIABLES:BEGINNER:52\n");
            SaveData legacy = saves.load();
            require(legacy.name().equals("Legacy Player") && legacy.xp() == 750
                            && legacy.score() == 52 && legacy.difficulty() == Difficulty.INTERMEDIATE
                            && legacy.completedTopics().get(TOPIC).contains(LEVEL)
                            && legacy.highScores().get(TOPIC).get(LEVEL) == 52,
                    "Existing save must load without power-up data");
            System.out.println("PASS: Legacy save without power-up data");
            require(legacy.tutorialCompleted(), "Legacy save without tutorial key must default to true");
            System.out.println("PASS: Legacy save tutorial flag defaults to true");

            String legacyContents = Files.readString(saveFile);
            Player preview = new Player("Preview flag check", 750);
            SaveService sandbox = PreviewSandbox.createFor(preview, Difficulty.INTERMEDIATE);
            require(preview.isTutorialCompleted() && sandbox.load().tutorialCompleted(),
                    "Preview player and sandbox save must mark tutorial complete");
            preview.addScore(123);
            sandbox.save(preview, Difficulty.INTERMEDIATE);
            require(sandbox.load().score() == 123 && Files.readString(saveFile).equals(legacyContents),
                    "Preview writes must stay isolated from existing save data");
            require(saves.load().tutorialCompleted(), "Legacy reads must preserve their default");
            System.out.println("PASS: Preview flags and isolated sandbox writes");

            Files.writeString(saveFile, "name=Oldest Player\nxp=0\ndifficulty=BEGINNER\n");
            SaveData oldest = saves.load();
            require(oldest.tutorialCompleted() && oldest.score() == 0
                            && oldest.completedTopics().isEmpty() && oldest.highScores().isEmpty(),
                    "Oldest save must retain existing defaults and count tutorial complete");
            System.out.println("PASS: Minimal legacy save defaults");

            Files.writeString(saveFile, legacyContents + "tutorialCompleted=unfinished\n");
            boolean rejected = false;
            try {
                saves.load();
            } catch (SaveFileException e) {
                rejected = true;
            }
            require(rejected, "Malformed tutorial flag must use the corrupted-save error path");
            System.out.println("PASS: Invalid tutorial flag rejected");

            System.out.println(
                    "\nALL PROGRESSION CHECKS PASSED"
            );

        } finally {

            Files.deleteIfExists(saveFile);
            Files.deleteIfExists(tempDir);
        }
    }

    private static void checkTutorialRoundTrips(SaveService saves, Player player)
            throws SaveFileException {
        for (boolean completed : new boolean[] {true, false}) {
            player.setTutorialCompleted(completed);
            saves.save(player, LEVEL);
            SaveData saved = saves.load();
            Player restored = new Player(saved.name(), saved.xp());
            restored.setTutorialCompleted(saved.tutorialCompleted());
            require(saved.tutorialCompleted() == completed && restored.isTutorialCompleted() == completed,
                    "Tutorial flag must survive save/load and restoration: " + completed);
            require(saved.xp() == player.getXp() && saved.score() == player.getScore()
                            && saved.completedTopics().get(TOPIC).contains(LEVEL)
                            && saved.highScores().get(TOPIC).get(LEVEL) == player.getHighScore(TOPIC, LEVEL),
                    "Tutorial persistence must preserve other progress");
            System.out.println("PASS: Tutorial flag round-trip " + completed);
        }
    }


    private static GameSession attempt(
            Player player,
            LessonRunnerService runner,
            int correctCount) {

        player.setHp(Player.getStartingHp());

        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < 12; i++) {

            questions.add(new Question(
                    i,
                    "Test question",
                    TOPIC,
                    LEVEL,
                    ""
            ) {

                @Override
                public boolean isCorrect(String answer) {
                    return "yes".equals(answer);
                }

                @Override
                public String display() {
                    return "Test question";
                }
            });
        }

        GameSession session = new GameSession(
                player,
                TOPIC,
                LEVEL,
                questions
        );

        session.setEnemy(new edu.batstateu.javajolt.data.EnemyRoster().getEnemy(TOPIC, LEVEL));

        // Wrong answers alternate between correct ones so every mistake costs
        // base damage only (isolated). Escalated chains of three mistakes defeat
        // any band, so clustered mistakes would turn these accuracy scenarios
        // into HP-defeat scenarios instead.
        int wrongCount = questions.size() - correctCount;

        for (int i = 0;
                i < questions.size() && player.isAlive();
                i++) {

            boolean wrong = wrongCount > 0
                    && i < wrongCount * 2
                    && i % 2 == 1;

            runner.submitAnswer(
                    session,
                    questions.get(i),
                    wrong ? "no" : "yes"
            );
        }

        runner.settleAttempt(session);

        player.recordHighScore(
                TOPIC,
                LEVEL,
                session.getScoreEarned()
        );

        return session;
    }


    private static void require(
            boolean condition,
            String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
