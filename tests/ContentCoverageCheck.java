import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.EnemyRoster;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.enums.ChallengeTier;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Verifies the complete 7-topic, 3-difficulty lesson and question coverage. */
public class ContentCoverageCheck {
    public static void main(String[] args) throws Exception {
        QuestionBank bank = new QuestionBank();
        QuestionService questions = new QuestionService(bank);
        LessonService lessons = new LessonService();
        EnemyRoster enemies = new EnemyRoster();
        int checked = 0;

        for (Topic topic : Topic.values()) {
            for (Difficulty difficulty : Difficulty.values()) {
                List<Question> sample = questions.selectQuestions(topic, difficulty);
                require(sample.size() == 12, topic + " / " + difficulty + " must provide 12 questions");
                Set<Integer> ids = new HashSet<>();
                EnumMap<ChallengeTier, Integer> tiers = new EnumMap<>(ChallengeTier.class);
                for (Question question : sample) {
                    require(ids.add(question.getId()), "Duplicate question in a selected lesson");
                    require(question.getTopic() == topic && question.getDifficulty() == difficulty,
                            "Question belongs to the wrong lesson");
                    tiers.merge(question.getChallengeTier(), 1, Integer::sum);
                }
                for (ChallengeTier tier : ChallengeTier.values()) {
                    require(tiers.getOrDefault(tier, 0) == 4,
                            "Expected four questions per tier for " + topic + "/" + difficulty);
                }
                require(!lessons.loadPages(topic, difficulty).isEmpty(),
                        "Missing textbook: " + topic + "/" + difficulty);
                require(enemies.getEnemy(topic, difficulty) != null,
                        "Missing enemy: " + topic + "/" + difficulty);
                checked++;
            }
        }
        System.out.println("ALL CONTENT COVERAGE CHECKS PASSED (" + checked
                + " topic/difficulty combinations, " + bank.getQuestions().size()
                + " questions in bank)");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
