package edu.batstateu.javajolt.entity;

import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import java.util.Map;
import java.util.Set;

public record SaveData(String name, int xp, Difficulty difficulty, int score,
        // Snapshot of progress: Topic -> set of completed Difficulties.
        // Stored as structured data here; SaveService handles string encoding.
        Map<Topic, Set<Difficulty>> completedTopics,
        // Best quiz score for each Topic -> Difficulty pair.
        Map<Topic, Map<Difficulty, Integer>> highScores,
        boolean tutorialCompleted) {

}
