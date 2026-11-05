package edu.batstateu.javajolt;

import edu.batstateu.javajolt.enums.ChallengeTier;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.InputMode;
import edu.batstateu.javajolt.enums.Topic;
import java.util.List;

public abstract class Question {
    private int id;
    private String question;
    private Topic topic;
    private Difficulty difficulty;
    private String explanation; // shown after every answer, right or wrong
    private ChallengeTier challengeTier =
        ChallengeTier.FOUNDATIONAL;

    public ChallengeTier getChallengeTier() {
    return challengeTier;
}

    public Question withChallengeTier(ChallengeTier tier) {
        this.challengeTier =
                java.util.Objects.requireNonNull(tier);

        return this;
    }

    // CONSTRUCTOR
    public Question(int id, String question, Topic topic, Difficulty difficulty, String explanation) {
        this.id = id;
        this.question = question;
        this.topic = topic;
        this.difficulty = difficulty;
        this.explanation = explanation;
    }

    // ANSWER EVALUATOR (each question type decides what "correct" means)
    public abstract boolean isCorrect(String userInput);

    // DISPLAY (each question type formats its own prompt)
    public abstract String display();

    // INPUT MODE: most questions take one line; a question type can override this (CodeQuestion does)
    public InputMode getInputMode() {
        return InputMode.SINGLE_LINE;
    }

    // Optional answer-editor details used by the Swing question screen.
    public List<String> getChoices() {
        return List.of();
    }

    public String getTemplate() {
        return "";
    }

    public String getExpectedOutput() {
        return "";
    }

    public List<String> getRequiredConcepts() {
        return List.of();
    }

    public List<String> getRequiredImports() {
        return List.of();
    }

    // GETTERS AND SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
