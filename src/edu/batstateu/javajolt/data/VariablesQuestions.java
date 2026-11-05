package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.enums.ChallengeTier;
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
                ).withChallengeTier(ChallengeTier.CHALLENGE));


        // ADDITIONAL BEGINNER QUESTIONS

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                111,
                "Which line correctly declares a boolean variable?",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "The boolean data type stores true or false values.",
                List.of(
                        "A. String isActive = true;",
                        "B. boolean isActive = true;",
                        "C. bool isActive = true;",
                        "D. boolean isActive = \"true\";"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                112,
                "Which line correctly declares a char variable named grade?",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "A char stores a single character inside single quotes.",
                List.of(
                        "A. char grade = \"A\";",
                        "B. String grade = 'A';",
                        "C. char grade = A;",
                        "D. char grade = 'A';"
                ),
                "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                113,
                "What is the final value of points?\n"
                + "int points = 4;\n"
                + "points = points + 2;",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "The original value 4 increases by 2, producing 6.",
                List.of(
                        "A. 4",
                        "B. 2",
                        "C. 6",
                        "D. 42"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                114,
                "What is the final value of stock?\n"
                + "int stock = 8;\n"
                + "stock = stock - 3;",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Subtracting 3 from the current value 8 gives 5.",
                List.of("5")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                115,
                "What will this code print?\n"
                + "String first = \"Java\";\n"
                + "String second = \"Jolt\";\n"
                + "System.out.println(first + second);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "The + operator joins the two strings without adding spaces.",
                List.of(
                        "A. Java Jolt",
                        "B. JavaJolt",
                        "C. firstsecond",
                        "D. Java+Jolt"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                116,
                "What will this code print?\n"
                + "int a = 2;\n"
                + "int b = a + 5;\n"
                + "a = 4;\n"
                + "System.out.println(b);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "b stores 7 when it is initialized. Changing a afterward does not change b.",
                List.of(
                        "A. 4",
                        "B. 7",
                        "C. 9",
                        "D. 2"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                117,
                "Declare a String variable named language "
                + "with the value Java, then print its value.",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Use String language = \"Java\"; "
                + "and System.out.println(language);",
                "Java",
                List.of("String language"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // =====================================
        // ADDITIONAL VARIABLES BEGINNER QUESTIONS
        // =====================================

        // FOUNDATIONAL - Question 118
        questions.add(new MultipleChoiceQuestion(
                118,
                "Which declaration correctly stores a decimal value?",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "The double data type can store decimal numbers.",
                List.of(
                        "A. int temperature = 36.5;",
                        "B. double temperature = 36.5;",
                        "C. boolean temperature = 36.5;",
                        "D. char temperature = 36.5;"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL - Question 119
        questions.add(new MultipleChoiceQuestion(
                119,
                "Which line correctly declares a String variable "
                + "named city containing the text Batangas?",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "String uses a capital S, and text values "
                + "must be enclosed in double quotation marks.",
                List.of(
                        "A. String city = \"Batangas\";",
                        "B. string city = \"Batangas\";",
                        "C. String city = 'Batangas';",
                        "D. String city = Batangas;"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION - Question 120
        questions.add(new MultipleChoiceQuestion(
                120,
                "What will the following code print?\n"
                + "int lives = 3;\n"
                + "lives = 1;\n"
                + "System.out.println(lives);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "The second assignment replaces the original "
                + "value of lives. The final value is 1.",
                List.of(
                        "A. 3",
                        "B. 2",
                        "C. 1",
                        "D. 31"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION - Question 121
        questions.add(new MultipleChoiceQuestion(
                121,
                "Which code correctly declares an integer variable "
                + "first and assigns the value 7 afterward?",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "A variable can be declared first and assigned "
                + "a value in a separate statement.",
                List.of(
                        "A. int total =; total = 7;",
                        "B. total = 7; int total;",
                        "C. int total; = 7;",
                        "D. int total; total = 7;"
                ),
                "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION - Question 122
        questions.add(new IdentificationQuestion(
                122,
                "What will this code print?\n"
                + "boolean online = false;\n"
                + "online = true;\n"
                + "System.out.println(online);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "The value of online was reassigned from "
                + "false to true before printing.",
                List.of("true")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION - Question 123
        questions.add(new MultipleChoiceQuestion(
                123,
                "What will the following code print?\n"
                + "int score = 4;\n"
                + "int Score = 9;\n"
                + "System.out.println(score);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Java variable names are case-sensitive. "
                + "score and Score are two different variables.",
                List.of(
                        "A. 13",
                        "B. 4",
                        "C. 9",
                        "D. Compilation error"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // =====================================
        // FINAL VARIABLES BEGINNER QUESTIONS
        // =====================================

        // APPLICATION - Question 124
        questions.add(new MultipleChoiceQuestion(
                124,
                "What will this code print?\n"
                + "int lives = 3;\n"
                + "String message = \"Lives: \" + lives;\n"
                + "System.out.println(message);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Java converts lives to text when it is joined "
                + "with the String. The result is Lives: 3.",
                List.of(
                        "A. Lives: lives",
                        "B. 3Lives:",
                        "C. Lives: 3",
                        "D. Compilation error"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // CHALLENGE - Question 125
        questions.add(new MultipleChoiceQuestion(
                125,
                "What will this code print?\n"
                + "int a = 3;\n"
                + "int b = 8;\n"
                + "int temp = a;\n"
                + "a = b;\n"
                + "b = temp;\n"
                + "System.out.println(a + \",\" + b);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "The temporary variable stores the original "
                + "value of a. After swapping, a is 8 and b is 3.",
                List.of(
                        "A. 3,8",
                        "B. 8,8",
                        "C. 3,3",
                        "D. 8,3"
                ),
                "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // CHALLENGE - Question 126
        questions.add(new MultipleChoiceQuestion(
                126,
                "What will this code print?\n"
                + "int original = 5;\n"
                + "int copy = original;\n"
                + "copy += 4;\n"
                + "System.out.println(original + \" \" + copy);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Assigning an int variable to another int "
                + "variable copies its value. Updating copy "
                + "does not change original.",
                List.of(
                        "A. 9 9",
                        "B. 5 9",
                        "C. 5 5",
                        "D. 9 5"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // CHALLENGE - Question 127
        questions.add(new IdentificationQuestion(
                127,
                "What is the final number printed?\n"
                + "int x = 4;\n"
                + "int y = x * 2;\n"
                + "x = x + 3;\n"
                + "System.out.println(x + y);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Initially y becomes 8. Then x becomes 7. "
                + "Adding the final values gives 15.",
                List.of("15")
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // CHALLENGE - Question 128
        questions.add(new MultipleChoiceQuestion(
                128,
                "What happens when these statements appear "
                + "in the same method block?\n"
                + "int score = 10;\n"
                + "int score = 20;\n"
                + "System.out.println(score);",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "Java does not allow two local variables "
                + "with the same name to be declared "
                + "in the same scope.",
                List.of(
                        "A. Prints 10",
                        "B. Prints 20",
                        "C. Compilation error",
                        "D. Prints 30"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // CHALLENGE - Question 129
        questions.add(new CodeQuestion(
                129,
                "Write Java statements that:\n"
                + "1. Declare an int variable named coins "
                + "and initialize it to 12.\n"
                + "2. Increase coins by 3.\n"
                + "3. Print the final value of coins.",
                Topic.VARIABLES,
                Difficulty.BEGINNER,
                "One possible solution is:\n"
                + "int coins = 12;\n"
                + "coins += 3;\n"
                + "System.out.println(coins);",
                "15",
                List.of("int coins", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

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
