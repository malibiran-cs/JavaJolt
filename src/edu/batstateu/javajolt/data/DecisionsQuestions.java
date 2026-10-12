package edu.batstateu.javajolt.data;

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
        ));

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
        ));

        return questions;
    }
}
