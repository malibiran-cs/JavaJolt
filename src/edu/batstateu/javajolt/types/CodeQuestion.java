package edu.batstateu.javajolt.types;

import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.*;

public class CodeQuestion extends Question {
    private String expectedOutput;
    private List<String> requiredConcepts;
    
    // NEW ATTRIBUTES
    // Note: May be useful later for instructions, as Editor UI may not have intellisense/autocomplete capabilities
    // user needs all the help they need if that's the case
    private List<String> requiredImports;
    // FIXED TEMPLATE STRING
    // For further instructions, assumes a Code.java file will be written beforehand (handled somewhere else not here)
    String template = "public class Code {\n" +
                          "\tpublic static void main(String[] args) {\n" +
                          "\t\t// WRITE YOUR CODE HERE\n" +
                          "\t}\n}";

    // CONSTRUCTOR
    public CodeQuestion(int id, String question, Topic topic, Difficulty difficulty, String expectedOutput,
            List<String> requiredConcepts, List<String> requiredImports) {
        super(id, question, topic, difficulty);
        this.expectedOutput = expectedOutput;
        this.requiredConcepts = requiredConcepts;
        this.requiredImports = requiredImports;
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
    // NOTE: TEMPORARY DISPLAY BECAUSE THERE IS NO GUI YET
    @Override
    public String display() {
        StringBuilder sb = new StringBuilder();
        sb.append(getQuestion()).append("\n\n")
          .append("Expected Output: " + getExpectedOutput()).append("\n")
          .append("Required Concepts: " + getRequiredConcepts()).append("\n")
          .append("Required Imports: " + getRequiredImports()).append("\n\n")
          .append("Template (MUST BE FOLLOWED):\n" + getTemplate()).append("\n\n")
          .append("Write your code:\n\n");

        return sb.toString();
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

    public void setTemplate(String template) {
        this.template = template;
    }

    
}
