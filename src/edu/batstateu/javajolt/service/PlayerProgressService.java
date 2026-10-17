package edu.batstateu.javajolt.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.batstateu.javajolt.enums.*;

public class PlayerProgressService {
    /*
    Map to cleanly keep track and store completed topics
    STRUCTURE: {Topic} : {Set<Difficulty>}
    EXAMPLE: Variables : (BEGINNER, INTERMEDIATE)
    NOTE: The added difficulty (value of the key Topic) is assumed to 
          have been completed already by the player
    */
    private final Map<Topic, EnumSet<Difficulty>> completedTopics = new EnumMap<>(Topic.class);

    /*
    PURPOSE: Method that adds the COMPLETED difficulty of the topic
    computeIfAbsent(key (automatically written to if null), {key variable} -> {value to be assigned to key in case it doesn't have one})
    .add() is called after the assignment to specifcally do the PURPOSE of the method
    .add() is why the returned value is boolean, true if successful, vice versa
     */ 
    public boolean complete(Topic topic, Difficulty difficulty) {
        return completedTopics.computeIfAbsent(topic, k -> EnumSet.noneOf(Difficulty.class)).add(difficulty);
    }

    public boolean isComplete(Topic topic, Difficulty difficulty) {
        return completedTopics.getOrDefault(topic, EnumSet.noneOf(Difficulty.class)).contains(difficulty);
    }


    public List<Topic> getAvailableTopics(Difficulty difficulty) {
        List<Topic> out = new ArrayList<>();

        for(Topic t: Topic.values()) {
            out.add(t);
            if(!isComplete(t, difficulty)) break;
        }
        return out;
    }

    public boolean isTopicMastered(Topic t) {
        return isComplete(t, Difficulty.BEGINNER)
            && isComplete(t, Difficulty.INTERMEDIATE)
            && isComplete(t, Difficulty.ADVANCED);
    }

    public boolean isDifficultyComplete(Difficulty difficulty) {
        for(Topic t: Topic.values()) {
            if(!isComplete(t, difficulty)) return false;
        }
        return true;
    }

    // Returns a detached copy so savers can't mutate internal state.
    public Map<Topic, EnumSet<Difficulty>> copyCompleted() {
        Map<Topic, EnumSet<Difficulty>> copy = new EnumMap<>(Topic.class);
        completedTopics.forEach((topic, diffs) -> {
            // NOTE: fresh EnumSet per topic; addAll avoids copyOf() crash on empty sets.
            EnumSet<Difficulty> fresh = EnumSet.noneOf(Difficulty.class);
            fresh.addAll(diffs);
            copy.put(topic, fresh);
        });
        return copy;
    }

    // Replaces current progress with loaded data (used on game load).
    // ? extends (Type) means that pass anything that is either of this (Type) or a subset/subclass of it
    public void restore(Map<Topic, ? extends Set<Difficulty>> data) {
        completedTopics.clear();
        data.forEach((topic, diffs) -> {
            EnumSet<Difficulty> fresh = EnumSet.noneOf(Difficulty.class);
            fresh.addAll(diffs);
            // NOTE: skip empties so map never holds meaningless Topic -> {} entries.
            if (!fresh.isEmpty()) {
                completedTopics.put(topic, fresh);
            }
        });
    }
}
