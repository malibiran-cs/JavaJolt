package edu.game;

import java.util.List;

import edu.entity.Player;
import edu.question.Question;
import edu.enums.Difficulty;

public class GameSession {
    private Player player;
    private Difficulty difficulty;
    private List<Question> questions;
    private int currentQuestionIndex;

    public GameSession(Player player, Difficulty difficulty, List<Question> questions) {
        this.player = player;
        this.difficulty = difficulty;
        this.questions = questions;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }
}
