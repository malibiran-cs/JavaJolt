package edu.batstateu.javajolt.types;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.InputMode;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.service.CodeQuestionService;
import java.util.List;

// The player writes a whole program. It is compiled and run, and its output is compared to expectedOutput.
public class CodeQuestion extends Question {
    private String expectedOutput;
    private List<String> requiredConcepts; // text the real code must contain, e.g. "%" or "if"
    private List<String> requiredImports;  // shown to the player as a hint (the player must still write the import)

    // The starting point shown to the player
    private final String template = """
        public class Code {
            public static void main(String[] args) {
                // WRITE YOUR CODE HERE
            }
        }""";

    private static final CodeQuestionService JUDGE = new CodeQuestionService();

    // CONSTRUCTOR
    public CodeQuestion(int id, String question, Topic topic, Difficulty difficulty, String explanation,
            String expectedOutput, List<String> requiredConcepts, List<String> requiredImports) {
        super(id, question, topic, difficulty, explanation);
        this.expectedOutput = expectedOutput;
        this.requiredConcepts = requiredConcepts;
        this.requiredImports = requiredImports;
    }

    // ANSWER EVALUATOR: concepts -> compile -> run -> compare
    @Override
    public boolean isCorrect(String userInput) {
        return JUDGE.judge(userInput, expectedOutput, requiredConcepts);
    }

    // DISPLAY
    // NOTE: TEMPORARY DISPLAY BECAUSE THERE IS NO GUI YET
    @Override
    public String display() {
        StringBuilder sb = new StringBuilder();
        sb.append(getQuestion()).append("\n\n");

        sb.append("Expected output:\n");
        for (String line : expectedOutput.split("\n")) {
            sb.append("  ").append(line).append("\n");
        }

        if (!requiredConcepts.isEmpty()) {
            sb.append("\nYour code must use: ").append(String.join("  ", requiredConcepts)).append("\n");
        }
        if (!requiredImports.isEmpty()) {
            sb.append("Imports you will need: ").append(String.join(", ", requiredImports)).append("\n");
        }

        sb.append("\nTemplate (MUST BE FOLLOWED):\n").append(template).append("\n\n")
          .append("Type END on its own line to finish.\n")
          .append("Write your code:\n\n");

        return sb.toString();
    }

    // Code answers span many lines
    @Override
    public InputMode getInputMode() {
        return InputMode.MULTI_LINE;
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

    public List<String> getRequiredImports() {
        return requiredImports;
    }

    public void setRequiredImports(List<String> requiredImports) {
        this.requiredImports = requiredImports;
    }

    public String getTemplate() {
        return template;
    }
}
