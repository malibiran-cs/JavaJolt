
package edu.batstateu.javajolt.enums;

import edu.batstateu.javajolt.entity.Player;

import java.util.Objects;
import java.util.function.Predicate;

public enum Achievement {

    FIRST_STEPS(
            "First Steps",
            "Complete your first Beginner lesson",
            player -> completedAt(
                    player, Difficulty.BEGINNER
            ) >= 1
    ),

    STEADY_LEARNER(
            "Steady Learner",
            "Complete 3 Beginner lessons",
            player -> completedAt(
                    player, Difficulty.BEGINNER
            ) >= 3
    ),

    BEGINNER_MASTER(
            "Beginner Master",
            "Complete all 7 Beginner lessons",
            player -> completedAt(
                    player, Difficulty.BEGINNER
            ) == Topic.values().length
    ),

    INTERMEDIATE_MASTER(
            "Intermediate Master",
            "Complete all 7 Intermediate lessons",
            player -> completedAt(
                    player, Difficulty.INTERMEDIATE
            ) == Topic.values().length
    ),

    ADVANCED_MASTER(
            "Advanced Master",
            "Complete all 7 Advanced lessons",
            player -> completedAt(
                    player, Difficulty.ADVANCED
            ) == Topic.values().length
    ),

    TOPIC_SPECIALIST(
            "Topic Specialist",
            "Master one topic at all three difficulties",
            Achievement::hasMasteredTopic
    ),

    TOPIC_EXPLORER(
            "Topic Explorer",
            "Complete lessons in 5 different topics",
            player -> startedTopics(player) >= 5
    ),

    XP_ROOKIE(
            "XP Rookie",
            "Earn 100 total XP",
            player -> player.getXp() >= 100
    ),

    XP_VETERAN(
            "XP Veteran",
            "Earn 500 total XP",
            player -> player.getXp() >= 500
    ),

    XP_LEGEND(
            "XP Legend",
            "Earn 1,000 total XP",
            player -> player.getXp() >= 1000
    );


    private final String title;
    private final String description;
    private final Predicate<Player> requirement;

    
    public int getTarget() {

        return switch (this) {

            case FIRST_STEPS, TOPIC_SPECIALIST -> 1;

            case STEADY_LEARNER -> 3;

            case BEGINNER_MASTER,
                INTERMEDIATE_MASTER,
                ADVANCED_MASTER -> Topic.values().length;

            case TOPIC_EXPLORER -> 5;

            case XP_ROOKIE -> 100;

            case XP_VETERAN -> 500;

            case XP_LEGEND -> 1000;
        };
    }


    public int getProgress(Player player) {

        Objects.requireNonNull(player);

        int current = switch (this) {

            case FIRST_STEPS,
                STEADY_LEARNER,
                BEGINNER_MASTER ->
                    completedAt(player, Difficulty.BEGINNER);

            case INTERMEDIATE_MASTER ->
                    completedAt(player, Difficulty.INTERMEDIATE);

            case ADVANCED_MASTER ->
                    completedAt(player, Difficulty.ADVANCED);

            case TOPIC_SPECIALIST ->
                    hasMasteredTopic(player) ? 1 : 0;

            case TOPIC_EXPLORER ->
                    startedTopics(player);

            case XP_ROOKIE,
                XP_VETERAN,
                XP_LEGEND ->
                    player.getXp();
        };

        // Keep progress within the achievement's limits
        return Math.min(
                getTarget(),
                Math.max(0, current)
        );
    }

    Achievement(
            String title,
            String description,
            Predicate<Player> requirement) {

        this.title = title;
        this.description = description;
        this.requirement = requirement;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public boolean isUnlocked(Player player) {
        return requirement.test(
                Objects.requireNonNull(player)
        );
    }


    private static int completedAt(
            Player player,
            Difficulty difficulty) {

        int count = 0;

        for (Topic topic : Topic.values()) {

            if (player.isComplete(topic, difficulty)) {
                count++;
            }
        }

        return count;
    }


    private static boolean hasMasteredTopic(
            Player player) {

        for (Topic topic : Topic.values()) {

            if (player.isTopicMastered(topic)) {
                return true;
            }
        }

        return false;
    }


    private static int startedTopics(Player player) {

        int count = 0;

        for (Topic topic : Topic.values()) {

            for (Difficulty difficulty
                    : Difficulty.values()) {

                if (player.isComplete(topic, difficulty)) {
                    count++;
                    break;
                }
            }
        }

        return count;
    }
}
