package edu.batstateu.javajolt.question;

import edu.batstateu.javajolt.question.enums.Difficulty;
import edu.batstateu.javajolt.question.enums.Topic;

public abstract class Question {
    private int id;
    private String question;
    private Topic topic;
    private Difficulty difficulty;
    
    // CONSTRUCTOR
    public Question(int id, String question, Topic topic, Difficulty difficulty) {
        this.id = id;
        this.question = question;
        this.topic = topic;
        this.difficulty = difficulty;
    }

    // ANSWER EVALUATOR (each question type decides what "correct" means)
    public abstract boolean isCorrect(String userInput);

    // DISPLAY (each question type formats its own prompt)
    public abstract String display();

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

}
