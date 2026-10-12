package edu.batstateu.javajolt.types;

import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;

public class IdentificationQuestion extends Question {
    private List<String> acceptedAnswers;

    // CONSTRUCTOR
    public IdentificationQuestion(int id, String question, Topic topic, Difficulty difficulty,
            String explanation, List<String> acceptedAnswers) {
        super(id, question, topic, difficulty, explanation);
        this.acceptedAnswers = acceptedAnswers;
    }

    // ANSWER EVALUATOR
    // Ignores capitalization and extra spaces
    @Override
    public boolean isCorrect(String userInput) {
        String in = normalize(userInput);
        return acceptedAnswers.stream()
                              .anyMatch(answer -> normalize(answer).equals(in));
    }

    private String normalize(String text) {
        return text.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    // DISPLAY
    @Override
    public String display() {
        return getQuestion() + "\n\nEnter your answer: ";
    }

    // GETTERS AND SETTERS
    public List<String> getAcceptedAnswers() {
        return acceptedAnswers;
    }

    public void setAcceptedAnswers(List<String> acceptedAnswers) {
        this.acceptedAnswers = acceptedAnswers;
    }
}
