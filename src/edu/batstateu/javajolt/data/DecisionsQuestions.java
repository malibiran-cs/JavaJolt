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

public class DecisionsQuestions {
    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // BEGINNER
        questions.add(new MultipleChoiceQuestion(
                301,
                "Which keyword starts a decision in Java?",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "An if statement runs its block only when its condition is true.",
                List.of("A. when", "B. if", "C. check", "D. decide"),
                "B"
        ));

        questions.add(new MultipleChoiceQuestion(
                302,
                "Which condition is written correctly?",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "Conditions go inside parentheses and use == to compare. A single = would assign a value instead of comparing.",
                List.of("A. if (x = 5)", "B. if (x == 5)", "C. if x == 5", "D. if [x == 5]"),
                "B"
        ));

        questions.add(new MultipleChoiceQuestion(
                303,
                "What does this code print?\n    int n = 4;\n    if (n > 5) {\n        System.out.println(\"big\");\n    } else {\n        System.out.println(\"small\");\n    }",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "4 > 5 is false, so the else block runs and prints small.",
                List.of("A. big", "B. small", "C. bigsmall", "D. nothing"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                304,
                "Which keyword runs a block of code when the if condition is false? (one word)",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "else is the fallback branch of an if statement.",
                List.of("else")
        ));

        questions.add(new CodeQuestion(
                305,
                "Create an int variable named hp set to 0. Using an if statement, print Game over when hp is less than 1.",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "The condition goes in parentheses: if (hp < 1) { System.out.println(\"Game over\"); }",
                "Game over",
                List.of("if", "hp"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        
        // =====================================
        // DECISIONS BEGINNER — NEW QUESTIONS
        // =====================================

        // FOUNDATIONAL — 310
        questions.add(new MultipleChoiceQuestion(
                310,
                "Given boolean ready = true;, which Java if statement has valid syntax?",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "An if statement places a boolean condition inside parentheses.",
                List.of(
                        "A. if ready { System.out.println(\"Go\"); }",
                        "B. if [ready] { System.out.println(\"Go\"); }",
                        "C. if (ready) { System.out.println(\"Go\"); }",
                        "D. if {ready} { System.out.println(\"Go\"); }"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION — 311
        questions.add(new MultipleChoiceQuestion(
                311,
                                """
                                What is printed?
                                int age = 18;
                                if (age >= 18) {
                                        System.out.println("Adult");
                                } else {
                                        System.out.println("Minor");
                                }""",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "18 meets the >= 18 condition, so the if branch prints Adult.",
                List.of(
                        "A. Adult",
                        "B. Minor",
                        "C. AdultMinor",
                        "D. Nothing"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 312
        questions.add(new MultipleChoiceQuestion(
                312,
                                """
                                What is printed?
                                boolean hasKey = false;
                                if (!hasKey) {
                                        System.out.println("Locked");
                                } else {
                                        System.out.println("Open");
                                }""",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "!false is true, so the first branch prints Locked.",
                List.of(
                        "A. Open",
                        "B. Locked",
                        "C. false",
                        "D. Nothing"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 313
        questions.add(new IdentificationQuestion(
                313,
                                """
                                What single word is printed?
                                int temperature = 24;
                                if (temperature >= 30) {
                                        System.out.println("Hot");
                                } else {
                                        System.out.println("Cool");
                                }""",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "24 is below 30, so the else branch prints Cool.",
                List.of("Cool")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE — 314
        questions.add(new MultipleChoiceQuestion(
                314,
                                """
                                What is printed?
                                int x = 8;
                                if (x > 5) {
                                        if (x % 2 == 0) {
                                                System.out.println("A");
                                        } else {
                                                System.out.println("B");
                                        }
                                } else {
                                        System.out.println("C");
                                }""",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "8 > 5 is true; 8 is even, so the nested if prints A.",
                List.of(
                        "A. A",
                        "B. B",
                        "C. C",
                        "D. Nothing"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 315
        questions.add(new MultipleChoiceQuestion(
                315,
                                """ 
                                What is printed?
                                int grade = 75;
                                if (grade >= 90) {
                                        System.out.println("Excellent");
                                } else if (grade >= 60) {
                                        System.out.println("Pass");
                                } else {
                                        System.out.println("Retry");
                                }""",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "75 is below 90 but at least 60, so the else-if branch prints Pass.",
                List.of(
                        "A. Excellent",
                        "B. Retry",
                        "C. Pass",
                        "D. Nothing"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 316
        questions.add(new CodeQuestion(
                316,
                "Declare int score = 72. Use if and else to print Pass "
                + "if score is at least 60, or Retry otherwise.",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "For example: int score = 72; if (score >= 60) { "
                + "System.out.println(\"Pass\"); } else { "
                + "System.out.println(\"Retry\"); }",
                "Pass",
                List.of("if", "else", "score"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // =====================================
        // DECISIONS BEGINNER — FINAL QUESTIONS
        // =====================================

        // FOUNDATIONAL — 317
        questions.add(new MultipleChoiceQuestion(
                317,
                "In Java, what must the condition of an if statement evaluate to?",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "An if statement requires a boolean condition, "
                + "which evaluates to true or false.",
                List.of(
                        "A. An integer",
                        "B. A boolean value",
                        "C. A String value",
                        "D. A character"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 318
        questions.add(new MultipleChoiceQuestion(
                318,
                "Which syntax correctly checks another condition "
                + "after an if statement?",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "Java uses else if followed by a condition "
                + "inside parentheses.",
                List.of(
                        "A. else if (score >= 90) { }",
                        "B. elseif (score >= 90) { }",
                        "C. else (score >= 90) if { }",
                        "D. if else (score >= 90) { }"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 319
        questions.add(new MultipleChoiceQuestion(
                319,
                "Which expression is true only when both "
                + "hasTicket and isAdult are true?",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "The logical AND operator && requires both "
                + "conditions to be true.",
                List.of(
                        "A. hasTicket || isAdult",
                        "B. !hasTicket && isAdult",
                        "C. hasTicket && isAdult",
                        "D. hasTicket == false"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 320
        questions.add(new MultipleChoiceQuestion(
                320,
                "Which symbols enclose the statements "
                + "inside a Java if block?",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "Curly braces { } define a block of statements.",
                List.of(
                        "A. Parentheses ( )",
                        "B. Curly braces { }",
                        "C. Square brackets [ ]",
                        "D. Angle brackets < >"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));


        // =====================================
        // APPLICATION QUESTIONS
        // =====================================

        // APPLICATION — 321
        questions.add(new MultipleChoiceQuestion(
                321,
                "What will this code print?\n"
                + "int score = 60;\n"
                + "if (score > 60) {\n"
                + "    System.out.println(\"Pass\");\n"
                + "} else {\n"
                + "    System.out.println(\"Retry\");\n"
                + "}",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "The condition uses > rather than >=. "
                + "Since 60 is not greater than 60, "
                + "the else branch prints Retry.",
                List.of(
                        "A. Pass",
                        "B. Retry",
                        "C. 60",
                        "D. Nothing"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 322
        questions.add(new MultipleChoiceQuestion(
                322,
                "What will this code print?\n"
                + "int x = 5;\n"
                + "if (x < 2) {\n"
                + "    System.out.println(\"Low\");\n"
                + "}\n"
                + "System.out.println(\"Done\");",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "The if condition is false, but the statement "
                + "after the if block still executes.",
                List.of(
                        "A. Low",
                        "B. Low then Done",
                        "C. Done",
                        "D. Nothing"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 323
        questions.add(new MultipleChoiceQuestion(
                323,
                "What will this code print?\n"
                + "int age = 20;\n"
                + "boolean hasPass = false;\n"
                + "if (age >= 18 && hasPass) {\n"
                + "    System.out.println(\"Enter\");\n"
                + "} else {\n"
                + "    System.out.println(\"Wait\");\n"
                + "}",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "The age requirement is met, but hasPass "
                + "is false. Logical AND requires both "
                + "conditions to be true.",
                List.of(
                        "A. Enter",
                        "B. Wait",
                        "C. true",
                        "D. Compilation error"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 324
        questions.add(new IdentificationQuestion(
                324,
                "What number will this code print?\n"
                + "int points = 7;\n"
                + "if (points >= 5) {\n"
                + "    points += 3;\n"
                + "}\n"
                + "System.out.println(points);",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "Since 7 >= 5 is true, the code adds 3 "
                + "to points. The final value is 10.",
                List.of("10")
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // =====================================
        // CHALLENGE QUESTIONS
        // =====================================

        // CHALLENGE — 325
        questions.add(new MultipleChoiceQuestion(
                325,
                "What will this code print?\n"
                + "int a = 7;\n"
                + "if (a > 5)\n"
                + "    if (a > 10)\n"
                + "        System.out.println(\"High\");\n"
                + "    else\n"
                + "        System.out.println(\"Medium\");",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "Without braces, else belongs to the nearest "
                + "unmatched if statement. Since 7 > 10 "
                + "is false, Medium is printed.",
                List.of(
                        "A. High",
                        "B. Medium",
                        "C. Nothing",
                        "D. Compilation error"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 326
        questions.add(new MultipleChoiceQuestion(
                326,
                "What will this code print?\n"
                + "int temperature = 32;\n"
                + "boolean raining = true;\n"
                + "if (temperature > 30 && !raining) {\n"
                + "    System.out.println(\"Sunny\");\n"
                + "} else if (raining) {\n"
                + "    System.out.println(\"Rainy\");\n"
                + "} else {\n"
                + "    System.out.println(\"Cloudy\");\n"
                + "}",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "The first condition is false because "
                + "!raining is false. The else-if condition "
                + "is true, so Rainy is printed.",
                List.of(
                        "A. Sunny",
                        "B. Rainy",
                        "C. Cloudy",
                        "D. Nothing"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 327
        questions.add(new IdentificationQuestion(
                327,
                "What single letter will this code print?\n"
                + "int n = 6;\n"
                + "if (n % 2 == 0 && n > 10) {\n"
                + "    System.out.println(\"A\");\n"
                + "} else if (n % 2 == 0 || n > 10) {\n"
                + "    System.out.println(\"B\");\n"
                + "} else {\n"
                + "    System.out.println(\"C\");\n"
                + "}",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "6 is even but is not greater than 10. "
                + "The AND condition is false, while the "
                + "OR condition is true. The output is B.",
                List.of("B")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 328
        questions.add(new CodeQuestion(
                328,
                "Write a Java program that:\n"
                + "1. Declares int age = 20.\n"
                + "2. Declares boolean hasTicket = true.\n"
                + "3. Uses if, else, and the && operator.\n"
                + "4. Prints Allowed only when age is at "
                + "least 18 AND hasTicket is true.\n"
                + "5. Prints Denied otherwise.",
                Topic.DECISIONS,
                Difficulty.BEGINNER,
                "Example:\n"
                + "int age = 20;\n"
                + "boolean hasTicket = true;\n"
                + "if (age >= 18 && hasTicket) {\n"
                + "    System.out.println(\"Allowed\");\n"
                + "} else {\n"
                + "    System.out.println(\"Denied\");\n"
                + "}",
                "Allowed",
                List.of("if", "else", "&&"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // INTERMEDIATE
        questions.add(new MultipleChoiceQuestion(
                306,
                "Which structure is best for selecting one of several exact constant values?",
                Topic.DECISIONS,
                Difficulty.INTERMEDIATE,
                "A switch statement clearly selects a branch based on a matching value.",
                List.of("A. switch", "B. for", "C. try", "D. import"),
                "A"
        ));

        questions.add(new MultipleChoiceQuestion(
                307,
                "What does an else-if branch do?",
                Topic.DECISIONS,
                Difficulty.INTERMEDIATE,
                "else if checks another condition only when the earlier if condition was false.",
                List.of("A. Repeats the first condition", "B. Checks another condition", "C. Ends the program", "D. Declares a variable"),
                "B"
        ));

        // ADVANCED
        questions.add(new MultipleChoiceQuestion(
                308,
                "What is printed?\n    int score = 85;\n    if (score >= 90) {\n        System.out.println(\"A\");\n    } else if (score >= 80) {\n        System.out.println(\"B\");\n    } else {\n        System.out.println(\"C\");\n    }",
                Topic.DECISIONS,
                Difficulty.ADVANCED,
                "85 is not at least 90, but it is at least 80, so the else-if branch prints B.",
                List.of("A. A", "B. B", "C. C", "D. Nothing"),
                "B"
        ));

        questions.add(new IdentificationQuestion(
                309,
                "What keyword exits a switch case so execution does not fall through? (one word)",
                Topic.DECISIONS,
                Difficulty.ADVANCED,
                "break exits the current switch case.",
                List.of("break")
        ));

        return questions;
    }
}
