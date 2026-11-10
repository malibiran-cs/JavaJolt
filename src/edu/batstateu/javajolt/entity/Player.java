package edu.batstateu.javajolt.entity;

import java.util.EnumSet;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.batstateu.javajolt.enums.*;
import edu.batstateu.javajolt.service.PlayerProgressService;
public class Player {
    // NOTE: maxHp is derived per quiz from the player's level (LevelRewardService,
    // not saved); HP itself stays session-only and always restores to maxHp at quiz start.
    private static final int STARTING_HP = 10;
    private String name;
    private int hp = STARTING_HP;
    private int maxHp = STARTING_HP;
    private int xp;      // persistent: written to the save file and restored on load
    private int score;   // cumulative score across quiz attempts; persisted
    private boolean tutorialCompleted; // new players have not completed the tutorial
    private final Map<Topic, EnumMap<Difficulty, Integer>> highScores =
            new EnumMap<>(Topic.class);
    private PlayerProgressService progress = new PlayerProgressService();

    private final Map<Topic, EnumSet<Difficulty>> rewardedTextbooks =
        new EnumMap<>(Topic.class);

    public Player(String name, int xp) {
        this.name = name;
        this.xp = xp;
    }

    // HP Updater
    public int takeDamage(int damage) {
        hp = Math.max(0, hp - damage); 
        return hp;
    }

    public int heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
        return hp;
    }

    public void restoreFullHp() {
        hp = maxHp;
    }

    public int addScore(int amount) {
        return score += amount;
    }

    // Records the best score earned for one topic and difficulty.
    public boolean recordHighScore(Topic topic, Difficulty difficulty, int score) {
        int previousHighScore = getHighScore(topic, difficulty);
        if (score <= previousHighScore) {
            return false;
        }

        highScores.computeIfAbsent(topic, key -> new EnumMap<>(Difficulty.class))
                .put(difficulty, score);
        return true;
    }

    public int getHighScore(Topic topic, Difficulty difficulty) {
        Map<Difficulty, Integer> scores = highScores.get(topic);
        if (scores == null) {
            return 0;
        }
        return scores.getOrDefault(difficulty, 0);
    }

    // Returns a detached copy so save data cannot mutate the player's records.
    public Map<Topic, Map<Difficulty, Integer>> copyHighScores() {
        Map<Topic, Map<Difficulty, Integer>> copy = new EnumMap<>(Topic.class);
        highScores.forEach((topic, scores) -> {
            copy.put(topic, new EnumMap<>(scores));
        });
        return copy;
    }

    // Restores high scores from saved data; missing records remain empty for older saves.
    public void restoreHighScores(Map<Topic, ? extends Map<Difficulty, Integer>> data) {
        highScores.clear();
        data.forEach((topic, scores) -> {
            EnumMap<Difficulty, Integer> topicScores = new EnumMap<>(Difficulty.class);
            scores.forEach((difficulty, value) -> {
                if (value != null && value >= 0) {
                    topicScores.put(difficulty, value);
                }
            });
            if (!topicScores.isEmpty()) {
                highScores.put(topic, topicScores);
            }
        });
    }

    public int addXp(int amount) {
        return xp += amount;
    }

    public int deductScore(int deduction) {
        return score -= deduction;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    // PROGRESS METHODS
    // NOTE: use this to access PlayerProgress methods
    public PlayerProgressService getProgress() {
        return progress;
    }

    public boolean complete(Topic topic, Difficulty difficulty) {
        return progress.complete(topic, difficulty);
    }

    public boolean isComplete(Topic topic, Difficulty difficulty) {
        return progress.isComplete(topic, difficulty);
    }

    public List<Topic> getAvailableTopics(Difficulty difficulty) {
        return progress.getAvailableTopics(difficulty);
    }

    public boolean isTopicMastered(Topic topic) {
        return progress.isTopicMastered(topic);
    }

    // Restores progress from loaded save data (used once at game start).
    public void restoreProgress(Map<Topic, ? extends Set<Difficulty>> data) {
        progress.restore(data);
    }

    
    // Check whether a textbook's XP reward was claimed.
    public boolean hasTextbookReward(
            Topic topic,
            Difficulty difficulty) {

        return rewardedTextbooks.containsKey(topic)
                && rewardedTextbooks.get(topic).contains(difficulty);
    }

    // Mark a textbook reward as claimed.
    // Returns false when it was already claimed.
    public boolean markTextbookReward(
            Topic topic,
            Difficulty difficulty) {

        return rewardedTextbooks
                .computeIfAbsent(
                        topic,
                        key -> EnumSet.noneOf(Difficulty.class)
                )
                .add(difficulty);
    }

    // Create a detached copy for saving.
    public Map<Topic, EnumSet<Difficulty>> copyTextbookRewards() {

        Map<Topic, EnumSet<Difficulty>> copy =
                new EnumMap<>(Topic.class);

        rewardedTextbooks.forEach((topic, difficulties) ->
                copy.put(topic, EnumSet.copyOf(difficulties))
        );

        return copy;
    }

    // Restore textbook rewards from saved data.
    public void restoreTextbookRewards(
            Map<Topic, ? extends Set<Difficulty>> saved) {

        rewardedTextbooks.clear();

        saved.forEach((topic, difficulties) -> {

            if (difficulties != null && !difficulties.isEmpty()) {

                rewardedTextbooks.put(
                        topic,
                        EnumSet.copyOf(difficulties)
                );
            }
        });
    }


    // GETTERS AND SETTERS
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    // Guarded: a quiz always plays with at least 1 max HP.
    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isTutorialCompleted() {
        return tutorialCompleted;
    }

    public void setTutorialCompleted(boolean tutorialCompleted) {
        this.tutorialCompleted = tutorialCompleted;
    }

    public static int getStartingHp() {
        return STARTING_HP;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setProgress(PlayerProgressService progress) {
        this.progress = progress;
    }

    
}
