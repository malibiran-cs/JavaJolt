package edu.batstateu.javajolt.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.batstateu.javajolt.enums.*;
import edu.batstateu.javajolt.service.PlayerProgressService;
public class Player {
    private static final int STARTING_HP = 10;
    private String name;
    private int hp = STARTING_HP;
    private int xp;
    private int score; 
    private PlayerProgressService progress = new PlayerProgressService();

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
        return hp += amount;
    }

    public int addScore(int amount) {
        return score += amount;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
