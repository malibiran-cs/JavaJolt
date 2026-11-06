
import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.game.GameSession;
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

        LessonRunnerService runner = new LessonRunnerService(
                new QuestionService(new QuestionBank()),
                new LessonService(),
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

        require(
                failed.getScoreEarned() == 80,
                "Failed attempt score must still commit"
        );

        require(player.getHighScore(TOPIC, LEVEL) == 80,
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

        require(player.getHighScore(TOPIC, LEVEL) == 90,
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

            require(restored.isComplete(TOPIC, LEVEL),
                    "Completion was not restored");

            require(restored.getHighScore(TOPIC, LEVEL) == 90,
                    "High score was not restored");

            System.out.println("PASS: Save and load");


            // TEST 4: Replaying a completed lesson
            int beforeReplay = restored.getXp();

            attempt(restored, runner, 12);

            require(restored.getXp() == beforeReplay,
                    "Replay awarded duplicate XP");

            require(restored.getHighScore(TOPIC, LEVEL) == 120,
                    "Perfect replay did not improve high score");

            System.out.println("PASS: No duplicate XP");


            // TEST 5: Lower scores must not overwrite records
            attempt(restored, runner, 9);

            require(restored.getHighScore(TOPIC, LEVEL) == 120,
                    "Lower score overwrote previous best");

            require(restored.getXp() == beforeReplay,
                    "Later replay awarded XP");

            System.out.println("PASS: High score preservation");

            System.out.println(
                    "\nALL PROGRESSION CHECKS PASSED"
            );

        } finally {

            Files.deleteIfExists(saveFile);
            Files.deleteIfExists(tempDir);
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

        for (int i = 0;
                i < questions.size() && player.isAlive();
                i++) {

            runner.submitAnswer(
                    session,
                    questions.get(i),
                    i < correctCount ? "yes" : "no"
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
