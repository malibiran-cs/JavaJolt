package edu.batstateu.javajolt.types;

import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.*;

public class MultipleChoiceQuestion extends Question {
    private List<String> choices;
    private String answer; // the correct LETTER only, e.g. "C" (no period)
    
    // CONSTRUCTOR
    public MultipleChoiceQuestion(int id, String question, Topic topic, Difficulty difficulty, List<String> choices,
            String answer) {
        super(id, question, topic, difficulty);
        this.choices = choices;
        this.answer = answer;
    }

    // ANSWER EVALUATOR
    // Accepts "C", "c", "C." and "c)" as the same answer
    @Override 
    public boolean isCorrect(String userInput) {
        String in = userInput.trim().replaceAll("[.)]$", "");
        return in.equalsIgnoreCase(answer);
    }

    // DISPLAY
    @Override
    public String display() {
        StringBuilder sb = new StringBuilder();
        sb.append(getQuestion()).append("\n\n");
        for (String choice : choices) {
            sb.append(choice).append("\n");
        }
        sb.append("\nEnter your answer (letter): ");
        return sb.toString();
    }

    // GETTERS AND SETTERS
    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    
}
