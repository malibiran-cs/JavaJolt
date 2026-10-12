package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.CodeQuestion;
import edu.batstateu.javajolt.types.IdentificationQuestion;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;

/**
 * Question definitions for Java variable declarations, assignments, constants,
 * and the distinction between primitive values and immutable strings.
 */
public class VariablesQuestions {
    /**
     * Builds the variables question set in increasing difficulty.
     *
     * <p>Each question has a stable ID so it can be referenced by the quiz
     * system and checked for duplicates when the complete question bank loads.
     */
    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // Establish declaration syntax and the basic types used by later questions.
        // BEGINNER
        questions.add(new MultipleChoiceQuestion(
                101,
                "Which line correctly declares an integer variable named age?",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Java writes the type first, then the name: int age = 15; The type name int is lowercase.",
                List.of("A. int age = 15;", "B. integer age = 15;", "C. age int = 15;", "D. Int age = 15;"),
                "A"
        ));

        questions.add(new MultipleChoiceQuestion(
                102,
                "Which data type is best for storing a whole number like 42?",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "int stores whole numbers. String is for text, boolean is for true/false, and double is for numbers with decimals.",
                List.of("A. String", "B. int", "C. boolean", "D. double"),
                "B"
        ));

        questions.add(new MultipleChoiceQuestion(
                103,
                "Which of these is a valid variable name in Java?",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "A name can't start with a digit, contain spaces, or be a reserved word like class. playerScore is valid.",
                List.of("A. 2score", "B. my score", "C. playerScore", "D. class"),
                "C"
        ));

        questions.add(new IdentificationQuestion(
                104,
                "What data type do you use to store text such as \"Hello\"? (one word)",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "String (with a capital S) stores text. Text values are written inside double quotes.",
                List.of("String")
        ));

        questions.add(new CodeQuestion(
                105,
                "Declare an int variable named score with the value 10, then print it.",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Write the type, then the name, then = and the value. Printing with System.out.println(score); shows the value.",
                "10",
                List.of("int score"),
                List.of()
        ));

        // INTERMEDIATE
        questions.add(new MultipleChoiceQuestion(
                106,
                "What is the value of x after this code runs?\n    int x = 5;\n    x = x + 3;",
                Topic.VARIABLES,
                Difficulty.INTERMEDIATE,
                "x = x + 3 reads the current value (5), adds 3, and stores the result (8) back into x.",
                List.of("A. 5", "B. 3", "C. 8", "D. 53"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                107,
                "What happens when this line is compiled?\n    int count = 3.5;",
                Topic.VARIABLES,
                Difficulty.INTERMEDIATE,
                "Java won't squeeze a decimal into an int automatically, so it reports a compile error. Use double, or cast with (int) 3.5.",
                List.of("A. count becomes 3", "B. count becomes 3.5", "C. A compile error", "D. count becomes 4"),
                "C"
        ));

        // `final` changes the variable's assignment rules rather than its data type.
        questions.add(new IdentificationQuestion(
                108,
                "Which keyword makes a variable a constant that cannot be changed after it is assigned?",
                Topic.VARIABLES,
                Difficulty.INTERMEDIATE,
                "final locks the value: final int MAX = 5; Trying to reassign it is a compile error.",
                List.of("final")
        ));

        // ADVANCED
        // This checks whether learners know that String methods return a new value
        // instead of mutating the original String object.
        questions.add(new IdentificationQuestion(
                109,
                "What text does the variable s hold after this code runs?\n    String s = \"Java\";\n    s.toUpperCase();",
                Topic.VARIABLES,
                Difficulty.ADVANCED,
                "Strings are immutable: toUpperCase() returns a NEW String and does not change s. To keep it, write s = s.toUpperCase();",
                List.of("Java", "\"Java\"")
        ));

        // Keep the constant example after `final` has been introduced so the naming
        // convention and the read-only behavior are tested together.
        questions.add(new CodeQuestion(
                110,
                "Declare a constant int named MAX_HP with the value 10, then print it.",
                Topic.VARIABLES,
                Difficulty.ADVANCED,
                "Add the final keyword before the type. Constants are conventionally written in UPPER_CASE.",
                "10",
                List.of("final", "MAX_HP"),
                List.of()
        ));

        return questions;
    }
}
