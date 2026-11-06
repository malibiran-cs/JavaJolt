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
    private int provisionalXp; // first-clear XP earned so far; commits at settlement
    private boolean settled;   // end-of-quiz settlement already ran for this attempt

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

    public void addProvisionalXp(int amount) {
        provisionalXp += amount;
    }

    // Marks the attempt as rewarded so settlement can run exactly once.
    public void markSettled() {
        settled = true;
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

    public int getProvisionalXp() {
        return provisionalXp;
    }

    public boolean isSettled() {
        return settled;
    }
}
