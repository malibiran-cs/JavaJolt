package edu.batstateu.javajolt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;

import edu.batstateu.javajolt.enums.ChallengeTier;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumMap;
import java.util.Comparator;

public class QuestionService {
    private final QuestionBank questionBank;

    public QuestionService(QuestionBank questionBank) {
        this.questionBank = questionBank;
    }

    private static final int QUESTIONS_PER_QUIZ = 12;
    private static final int QUESTIONS_PER_TIER = 4;

    private final Map<Topic, Map<Difficulty, Set<Integer>>>
        previousAttempts = new EnumMap<>(Topic.class);

    // 1ST LAYER DIVISION
    private List<Question> getQuestionsByTopic(Topic topic) {
        return questionBank.getQuestions()
                           .stream()
                           .filter(question -> question.getTopic() == topic)
                           .toList();
    }

    // 2ND LAYER DIVISION
    private static List<Question> getQuestionsByDifficulty(List<Question> questions, Difficulty difficulty) {
        return questions.stream()
                        .filter(question -> question.getDifficulty() == difficulty)
                        .toList();
    }

    // SELECTION: questions for one topic and difficulty, in random order

    public List<Question> selectQuestions(
            Topic topic,
            Difficulty difficulty) {

        // STEP 1: Find questions matching the topic and difficulty
        List<Question> matching = getQuestionsByDifficulty(
                getQuestionsByTopic(topic),
                difficulty
        );

        if (matching.isEmpty()) {
            throw new IllegalStateException(
                    "No " + difficulty.getLabel()
                    + " questions for " + topic.getLabel()
                    + " yet."
            );
        }

        // Get questions used in the previous attempt
Map<Difficulty, Set<Integer>> topicHistory =
        previousAttempts.computeIfAbsent(
                topic,
                t -> new EnumMap<>(Difficulty.class)
        );

Set<Integer> recentlyUsed =
        topicHistory.getOrDefault(
                difficulty,
                Set.of()
        );

        // STEP 2: Organize questions by challenge tier
        EnumMap<ChallengeTier, List<Question>> byTier =
                new EnumMap<>(ChallengeTier.class);

        for (ChallengeTier tier : ChallengeTier.values()) {
            byTier.put(tier, new ArrayList<>());
        }

        for (Question question : matching) {
            byTier.get(question.getChallengeTier())
                    .add(question);
        }

        // STEP 3: Randomly select four questions from each tier
        List<Question> selected = new ArrayList<>();

        for (ChallengeTier tier : ChallengeTier.values()) {

            List<Question> pool = byTier.get(tier);

            Collections.shuffle(pool);

            // Prefer questions not used in the previous attempt
            pool.sort(
                    Comparator.comparing(
                            q -> recentlyUsed.contains(q.getId())
                    )
            );

            int amount = Math.min(
                    QUESTIONS_PER_TIER,
                    pool.size()
            );

            selected.addAll(pool.subList(0, amount));
        }

        // STEP 4: Fill missing slots if a tier has too few questions
        int target = Math.min(
                QUESTIONS_PER_QUIZ,
                matching.size()
        );

        if (selected.size() < target) {

            List<Question> remaining =
                    new ArrayList<>(matching);

            remaining.removeAll(selected);

            Collections.shuffle(remaining);

            remaining.sort(
        Comparator.comparing(
                q -> recentlyUsed.contains(q.getId())
        )
);

            int needed = target - selected.size();

            selected.addAll(
                    remaining.subList(0, needed)
            );
        }

        // STEP 5: Randomize within tiers, then arrange easy to hard

    Collections.shuffle(selected);

    selected.sort(
            Comparator.comparing(
                    Question::getChallengeTier
            )
    );

    // Remember the questions used in this attempt
    Set<Integer> selectedIds = new HashSet<>();

    for (Question question : selected) {
        selectedIds.add(question.getId());
        question.prepareForLesson();
    }

    topicHistory.put(difficulty, selectedIds);

    return selected;

        }


    // ANSWER EVALUATOR
    public boolean isCorrect(Question question, String userInput) {
        return question.isCorrect(userInput);
    }
}
