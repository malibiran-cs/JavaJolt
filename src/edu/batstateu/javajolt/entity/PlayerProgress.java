package edu.batstateu.javajolt.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import edu.batstateu.javajolt.enums.*;

public class PlayerProgress {
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
        // Temporary check, for beginner case only, can implement other difficulties later easily
        if(difficulty != Difficulty.BEGINNER) return List.of(Topic.values());
        List<Topic> out = new ArrayList<>();

        for(Topic t: Topic.values()) {
            if(!isComplete(t, difficulty)) break;
            out.add(t);
        }
        return out;
    }

    public boolean isTopicMastered(Topic t) {
        return isComplete(t, Difficulty.BEGINNER)
            && isComplete(t, Difficulty.INTERMEDIATE)
            && isComplete(t, Difficulty.ADVANCED);
    }
}
