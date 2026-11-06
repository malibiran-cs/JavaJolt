package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.ChallengeTier;
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
        ).withChallengeTier(ChallengeTier.APPLICATION));

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
        ).withChallengeTier(ChallengeTier.APPLICATION));

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
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // =====================================
        // ADDITIONAL OPERATORS BEGINNER QUESTIONS
        // =====================================

        // FOUNDATIONAL - 212
        questions.add(new MultipleChoiceQuestion(
                212,
                "Which Java operator performs multiplication?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "The * operator multiplies numeric values.",
                List.of(
                        "A. +",
                        "B. *",
                        "C. %",
                        "D. /"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL - 213
        questions.add(new MultipleChoiceQuestion(
                213,
                "Which operator checks whether two values are different?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "!= means not equal to; == checks equality.",
                List.of(
                        "A. ==",
                        "B. =",
                        "C. !=",
                        "D. <="
                ),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION - 214
        questions.add(new MultipleChoiceQuestion(
                214,
                "What does this code print?\n"
                + "int x = 6;\n"
                + "x += 4;\n"
                + "System.out.println(x);",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "x += 4 adds 4 to the existing value of 6.",
                List.of(
                        "A. 6",
                        "B. 4",
                        "C. 10",
                        "D. 64"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION - 215
        questions.add(new MultipleChoiceQuestion(
                215,
                "What is the value of 8 - 3 * 2 in Java?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Multiplication takes precedence: "
                + "3 * 2 is 6; 8 - 6 is 2.",
                List.of(
                        "A. 10",
                        "B. 2",
                        "C. 4",
                        "D. 5"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE - 216
        questions.add(new MultipleChoiceQuestion(
                216,
                "What does this code print?\n"
                + "int value = 9;\n"
                + "value /= 2;\n"
                + "value += 3;\n"
                + "System.out.println(value);",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Integer division gives 9 / 2 = 4. "
                + "Adding 3 gives 7.",
                List.of(
                        "A. 7",
                        "B. 7.5",
                        "C. 6",
                        "D. 12"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE - 217
        questions.add(new IdentificationQuestion(
                217,
                "What does this code print?\n"
                + "int x = 3;\n"
                + "x += 2;\n"
                + "x *= 4;\n"
                + "System.out.println(x);",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "x becomes 5 after addition, "
                + "then 20 after multiplication.",
                List.of("20")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE - 218
        questions.add(new CodeQuestion(
                218,
                "Declare two int variables a = 6 and b = 4. "
                + "Print their sum, then their product "
                + "on the next line using + and *.",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Use System.out.println(a + b); "
                + "followed by System.out.println(a * b);",
                "10\n24",
                List.of("+", "*"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));


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

        
        // =====================================
        // OPERATORS BEGINNER — FINAL 12 QUESTIONS
        // =====================================

        // FOUNDATIONAL - 219
        questions.add(new MultipleChoiceQuestion(
                219,
                "Which operator subtracts one numeric value from another?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "The - operator performs subtraction.",
                List.of(
                        "A. /",
                        "B. -",
                        "C. %",
                        "D. *"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL - 220
        questions.add(new MultipleChoiceQuestion(
                220,
                "Which comparison operator means greater than or equal to?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                ">= is true when the left value is greater than or equal to the right value.",
                List.of(
                        "A. >",
                        "B. ==",
                        "C. >=",
                        "D. <="
                ),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL - 221
        questions.add(new MultipleChoiceQuestion(
                221,
                "Which statement increases an existing int variable named count by one?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "count += 1 adds one to count and stores the updated value.",
                List.of(
                        "A. count--;",
                        "B. count += 1;",
                        "C. count =- 1;",
                        "D. count *= 1;"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL - 222
        questions.add(new MultipleChoiceQuestion(
                222,
                "What does the Java expression !true evaluate to?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "The logical NOT operator ! reverses true to false.",
                List.of(
                        "A. true",
                        "B. false",
                        "C. 0",
                        "D. null"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));


        // =====================================
        // APPLICATION QUESTIONS
        // =====================================

        // APPLICATION - 223
        questions.add(new IdentificationQuestion(
                223,
                "What will this code print?\n"
                + "int points = 14;\n"
                + "points -= 5;\n"
                + "System.out.println(points);",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "points -= 5 is equivalent to points = points - 5, resulting in 9.",
                List.of("9")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION - 224
        questions.add(new MultipleChoiceQuestion(
                224,
                "What will this code print?\n"
                + "int a = 6;\n"
                + "int b = 4;\n"
                + "System.out.println(a > b);",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Because 6 is greater than 4, the comparison evaluates to true.",
                List.of(
                        "A. true",
                        "B. false",
                        "C. 2",
                        "D. Compilation error"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION - 225
        questions.add(new MultipleChoiceQuestion(
                225,
                "What is the value of 17 / 5 + 17 % 5 when all operands are ints?",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Integer division gives 3 and remainder gives 2, so the sum is 5.",
                List.of(
                        "A. 3",
                        "B. 4",
                        "C. 5",
                        "D. 7"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION - 226
        questions.add(new MultipleChoiceQuestion(
                226,
                "What will this code print?\n"
                + "System.out.println(\"Total: \" + (2 + 3));",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Parentheses force addition first; the number 5 is then joined to the text.",
                List.of(
                        "A. Total: 23",
                        "B. Total: 5",
                        "C. 5",
                        "D. Compilation error"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // =====================================
        // CHALLENGE QUESTIONS
        // =====================================

        // CHALLENGE - 227
        questions.add(new MultipleChoiceQuestion(
                227,
                "What will this code print?\n"
                + "int value = 12;\n"
                + "value = (value + 6) / 3;\n"
                + "System.out.println(value % 4);",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "(12 + 6) / 3 gives 6, then 6 % 4 gives 2.",
                List.of(
                        "A. 0",
                        "B. 1",
                        "C. 2",
                        "D. 3"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE - 228
        questions.add(new MultipleChoiceQuestion(
                228,
                "What will this code print?\n"
                + "int a = 5;\n"
                + "int b = 2;\n"
                + "System.out.println(a > b && a % 2 == 1);",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Both comparisons are true: 5 > 2 and 5 % 2 == 1.",
                List.of(
                        "A. true",
                        "B. false",
                        "C. 1",
                        "D. Compilation error"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE - 229
        questions.add(new IdentificationQuestion(
                229,
                "What number will this code print?\n"
                + "int x = 9;\n"
                + "int y = 4;\n"
                + "int result = x / y * y + x % y;\n"
                + "System.out.println(result);",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "Integer division is 9 / 4 = 2. Then 2 * 4 + 1 = 9.",
                List.of("9")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE - 230
        questions.add(new CodeQuestion(
                230,
                "Declare int variables candies = 29 and boxSize = 6. "
                + "Print the number of complete boxes on the first line, "
                + "then the number of leftover candies on the second line. "
                + "Use integer division (/) and remainder (%).",
                Topic.OPERATORS,
                Difficulty.BEGINNER,
                "29 / 6 gives 4 complete boxes, and 29 % 6 leaves 5 candies.",
                "4\n5",
                List.of("/", "%"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        return questions;
    }
}
