package edu.batstateu.javajolt.types;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import java.util.List;

public class MultipleChoiceQuestion extends Question {
    private List<String> choices; // written as "A. ...", "B. ...", and so on
    private String answer;        // the correct LETTER only, e.g. "C" (no period)

    // CONSTRUCTOR
    public MultipleChoiceQuestion(int id, String question, Topic topic, Difficulty difficulty,
            String explanation, List<String> choices, String answer) {
        super(id, question, topic, difficulty, explanation);
        this.choices = choices;
        this.answer = answer;
    }

    // ANSWER EVALUATOR
    // Accepts "A", "a", "A." and "a)" as the same answer
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
