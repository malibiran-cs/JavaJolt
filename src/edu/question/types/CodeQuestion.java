package edu.question.types;

import java.util.List;

import edu.question.Question;
import edu.enums.Difficulty;
import edu.enums.Topic;

public class CodeQuestion extends Question {
    private String expectedOutput;
    private List<String> requiredConcepts;
    
    // CONSTRUCTOR
    public CodeQuestion(int id, String question, Topic topic, Difficulty difficulty, String expectedOutput,
            List<String> requiredConcepts) {
        super(id, question, topic, difficulty);
        this.expectedOutput = expectedOutput;
        this.requiredConcepts = requiredConcepts;
    }

    // ANSWER EVALUATOR (STILL A SIMPLE VERSION)
    // For now: compares the player's code to the expected answer, ignoring extra spaces.
    // requiredConcepts is not used yet; it can be checked here later.
    @Override 
    public boolean isCorrect(String userInput) {
        return normalize(userInput).equals(normalize(expectedOutput));
    }

    private String normalize(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    // DISPLAY
    @Override
    public String display() {
        return getQuestion() + "\n\nWrite your code: ";
    }

    // GETTERS AND SETTERS
    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public List<String> getRequiredConcepts() {
        return requiredConcepts;
    }

    public void setRequiredConcepts(List<String> requiredConcepts) {
        this.requiredConcepts = requiredConcepts;
    }

    
}