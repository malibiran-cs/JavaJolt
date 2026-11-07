package edu.batstateu.javajolt.types;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import java.util.ArrayList;
import java.util.Collections;
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

    @Override
    public void prepareForLesson() {
        String correctChoice = null;
        for (String choice : choices) {
            if (choice.startsWith(answer + ".")) {
                correctChoice = choice.substring(choice.indexOf('.') + 1).trim();
                break;
            }
        }

        List<String> shuffledChoices = new ArrayList<>();
        for (String choice : choices) {
            shuffledChoices.add(choice.substring(choice.indexOf('.') + 1).trim());
        }
        Collections.shuffle(shuffledChoices);

        List<String> labeledChoices = new ArrayList<>();
        String[] labels = {"A", "B", "C", "D", "E", "F"};
        for (int i = 0; i < shuffledChoices.size(); i++) {
            String label = labels[i];
            String choice = shuffledChoices.get(i);
            labeledChoices.add(label + ". " + choice);
            if (choice.equals(correctChoice)) {
                answer = label;
            }
        }
        choices = labeledChoices;
    }

    // One randomly chosen WRONG choice, read from the current (already
    // shuffled) labels and answer; the correct choice is never offered for
    // removal, and two or fewer choices leave nothing to hint about.
    @Override
    public List<String> getHintChoicesToRemove() {
        if (choices.size() <= 2) {
            return List.of();
        }

        String correctPrefix = answer.trim().toUpperCase() + ".";
        List<String> wrongChoices = new ArrayList<>();
        for (String choice : choices) {
            if (!choice.toUpperCase().startsWith(correctPrefix)) {
                wrongChoices.add(choice);
            }
        }

        if (wrongChoices.isEmpty()) {
            return List.of();
        }

        int pick = java.util.concurrent.ThreadLocalRandom.current()
                .nextInt(wrongChoices.size());
        return List.of(wrongChoices.get(pick));
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
