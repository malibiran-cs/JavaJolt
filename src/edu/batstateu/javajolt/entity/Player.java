package edu.batstateu.javajolt.entity;

public class Player {
    private String name;
    private int hp;
    private int score;
    
    // CONSTRUCTOR
    public Player(String name, int hp, int score) {
        this.name = name;
        this.hp = hp;
        this.score = score;
    }

    // HP STATE UPDATER
    public int takeDamage(int damage) {
        return hp-=damage;
    }
    
    public int heal(int amount) {
        return hp+=amount;
    }

    // SCORE STATE UPDATER
    public int addScore(int amount) {
        return score+=amount;
    }

    public int deductScore(int deduction) {
        return score-=deduction;
    }

    // DEAD OR ALIVE
    public boolean isAlive() {
        return hp > 0;
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
