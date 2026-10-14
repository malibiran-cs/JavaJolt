package edu.batstateu.javajolt.entity;

import java.util.List;

import edu.batstateu.javajolt.enums.*;
public class Player {
    private String name;
    private int hp;
    private int score; 
    private PlayerProgress progress = new PlayerProgress();

    public Player(String name, int hp, int score) {
        this.name = name;
        this.hp = hp;
        this.score = score;
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

    public int deductScore(int deduction) {
        return score -= deduction;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    // PROGRESS METHODS
    // NOTE: use this to access PlayerProgress methods
    public PlayerProgress getProgress() {
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
}
