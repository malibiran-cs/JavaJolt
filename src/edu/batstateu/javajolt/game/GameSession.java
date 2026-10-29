package edu.batstateu.javajolt.game; // Angelo

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import java.util.List;

public class GameSession {
    private Player player;
    private Topic topic;
    private Difficulty difficulty;
    private List<Question> questions;
    private int currentQuestionIndex;
    private int correctCount;
    private int mistakeCount;
    private int scoreEarned;

    // Constructor
    public GameSession(Player player, Topic topic, Difficulty difficulty, List<Question> questions) {
        this.player = player;
        this.topic = topic;
        this.difficulty = difficulty;
        this.questions = questions;
    }

    // State Updaters
    public void recordCorrect(int points) {
        correctCount++;
        scoreEarned += points;
    }

    public void recordMistake() {
        mistakeCount++;
    }

    // Percentage
    public int getAccuracyPercent() {
        if (questions.isEmpty()) {
            return 0;
        }
        return correctCount * 100 / questions.size();
    }

    // GETTERS AND SETTERS
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
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

    public int getCorrectCount() {
        return correctCount;
    }

    public int getMistakeCount() {
        return mistakeCount;
    }

    public int getScoreEarned() {
        return scoreEarned;
    }
}
