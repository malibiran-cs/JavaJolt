package edu.batstateu.javajolt.game;

import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;

public class GameSession {
    private Player player;
    private Difficulty difficulty;
    private List<Question> questions;
    private int currentQuestionIndex;
    
    // CONSTRUCTOR
    public GameSession(Player player, Difficulty difficulty, List<Question> questions) {
        this.player = player;
        this.difficulty = difficulty;
        this.questions = questions;
    }


    // GETTERS AND SETTERS
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }
}
