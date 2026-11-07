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
    private Enemy enemy;      // the quiz opponent; supplied by startSession
    private int currentQuestionIndex;
    private int correctCount;
    private int mistakeCount;
    private int consecutiveMistakes; // uncorrected streak; reset by every correct answer
    private int currentStreak; // consecutive correct answers; distinct from the damage counter
    private int bestStreak;    // highest currentStreak reached this attempt
    private int lastDamage; // actual HP cost of the most recent mistake (after Shield, once Phase 5 exists)
    private int lastPoints; // points awarded for the most recent answer (0 on a mistake)
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
        consecutiveMistakes = 0;
        currentStreak++;
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
        scoreEarned += points;
        lastPoints = points;
    }

    // Runs once per mistake, after its damage was already applied.
    public void recordMistake() {
        mistakeCount++;
        consecutiveMistakes++;
        currentStreak = 0;
        lastPoints = 0;
    }

    public void addProvisionalXp(int amount) {
        provisionalXp += amount;
    }

    // Marks the attempt as rewarded so settlement can run exactly once.
    public void markSettled() {
        settled = true;
    }

    // Percentage over the questions that were actually answered (0 when none).
    public int getAccuracyPercent() {
        int answered = getAnsweredCount();
        if (answered == 0) {
            return 0;
        }
        return correctCount * 100 / answered;
    }

    // GETTERS AND SETTERS
    public Player getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
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

    public int getConsecutiveMistakes() {
        return consecutiveMistakes;
    }

    public int getAnsweredCount() {
        return correctCount + mistakeCount;
    }

    public int getLastDamage() {
        return lastDamage;
    }

    public void setLastDamage(int lastDamage) {
        this.lastDamage = lastDamage;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public int getLastPoints() {
        return lastPoints;
    }

    public boolean isSettled() {
        return settled;
    }
}
