package edu.batstateu.javajolt.question.types;

import java.util.List;

import edu.batstateu.javajolt.question.Question;
import edu.batstateu.javajolt.question.enums.Difficulty;
import edu.batstateu.javajolt.question.enums.Topic;

public class IdentificationQuestion extends Question {
    private List<String> acceptedAnswers;

    // CONSTRUCTOR
    public IdentificationQuestion(int id, String question, Topic topic, Difficulty difficulty,
            List<String> acceptedAnswers) {
        super(id, question, topic, difficulty);
        this.acceptedAnswers = acceptedAnswers;
    }

    // ANSWER EVALUATOR
    @Override 
    public boolean isCorrect(String userInput) {
        String in = userInput.trim();
        return acceptedAnswers.stream()
                              .anyMatch(answer -> answer.equalsIgnoreCase(in));
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
