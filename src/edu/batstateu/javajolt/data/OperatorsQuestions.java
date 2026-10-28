package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.CodeQuestion;
import edu.batstateu.javajolt.types.IdentificationQuestion;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;

public class OperatorsQuestions {
    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // BEGINNER
        questions.add(new MultipleChoiceQuestion(
                201,
                "Which operator gives the remainder of a division?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "% is the modulus operator: 10 % 3 is 1. The / operator divides, * multiplies, and ^ is not \"power\" in Java.",
                List.of("A. /", "B. %", "C. *", "D. ^"),
                "B"
        ));

        questions.add(new MultipleChoiceQuestion(
                202,
                "What does 7 / 2 give when both numbers are ints?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Dividing two ints drops the decimal part, so 7 / 2 is 3, not 3.5.",
                List.of("A. 3.5", "B. 4", "C. 3", "D. 3.0"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                203,
                "Which operator checks whether two values are equal?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "== compares two values. A single = assigns a value to a variable.",
                List.of("A. =", "B. ==", "C. :=", "D. equals"),
                "B"
        ));

        questions.add(new IdentificationQuestion(
                204,
                "What is the result of 10 % 4? (type the number)",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "10 divided by 4 is 2 with a remainder of 2, and % gives that remainder.",
                List.of("2")
        ));

        // (the team's original code question, moved into the Operators ID range)
        questions.add(new CodeQuestion(
                205,
                "Using operators, print the sum of 7 and 5, then on the next line "
                        + "print the remainder of 17 divided by 5.",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Use + to add and % to get a remainder: System.out.println(7 + 5); then System.out.println(17 % 5);",
                "12\n2",
                List.of("+", "%"),
                List.of()
        ));

        // INTERMEDIATE
        questions.add(new MultipleChoiceQuestion(
                206,
                "What is the value of 2 + 3 * 4?",
                Topic.OPERATORS,
                Difficulty.INTERMEDIATE,
                "Multiplication happens before addition: 3 * 4 is 12, then 2 + 12 is 14.",
                List.of("A. 20", "B. 14", "C. 24", "D. 9"),
                "B"
        ));

        questions.add(new MultipleChoiceQuestion(
                207,
                "What does true && false evaluate to?",
                Topic.OPERATORS,
                Difficulty.INTERMEDIATE,
                "&& (AND) is true only when both sides are true. Here one side is false, so the result is false.",
                List.of("A. true", "B. false", "C. null", "D. An error"),
                "B"
        ));

        questions.add(new IdentificationQuestion(
                208,
                "What is the value of 5 * (2 + 3)? (type the number)",
                Topic.OPERATORS,
                Difficulty.INTERMEDIATE,
                "Parentheses are evaluated first: 2 + 3 is 5, then 5 * 5 is 25.",
                List.of("25")
        ));

        questions.add(new MultipleChoiceQuestion(
                209,
                "Which operator means logical OR in Java?",
                Topic.OPERATORS,
                Difficulty.INTERMEDIATE,
                "The || operator is true when at least one of its conditions is true.",
                List.of("A. &&", "B. ||", "C. !", "D. |="),
                "B"
        ));

        // ADVANCED
        questions.add(new MultipleChoiceQuestion(
                210,
                "What does 3 + 4 * 2 == 11 evaluate to?",
                Topic.OPERATORS,
                Difficulty.ADVANCED,
                "Multiplication happens first, so the expression becomes 3 + 8 == 11, which is true.",
                List.of("A. true", "B. false", "C. 11", "D. A compile error"),
                "A"
        ));

        questions.add(new IdentificationQuestion(
                211,
                "What is the value of 17 % 5? (type the number)",
                Topic.OPERATORS,
                Difficulty.ADVANCED,
                "17 divided by 5 leaves a remainder of 2.",
                List.of("2")
        ));

        return questions;
    }
}
