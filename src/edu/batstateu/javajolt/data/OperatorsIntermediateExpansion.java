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

/** Additional Operators intermediate questions. Does not replace existing banks. */
public final class OperatorsIntermediateExpansion {
    private OperatorsIntermediateExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                231,
                "Which Java operator compares two primitive int values for equality?",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Java uses == to compare primitive equality; = assigns.",
                List.of(
                        "A. =",
                        "B. !=",
                        "C. ===",
                        "D. =="
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                232,
                "Which expression performs short-circuit logical AND?",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "&& evaluates the right-hand operand only if needed.",
                List.of(
                        "A. a && b",
                        "B. a & b",
                        "C. a || b",
                        "D. a ^ b"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                233,
                "Which assignment subtracts 3 from an int variable named count?",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "The -= compound operator subtracts and stores the result.",
                List.of(
                        "A. count == 3;",
                        "B. count -= 3;",
                        "C. count =- 3;",
                        "D. count *= 3;"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                234,
                "What does % compute for integer operands?",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "The remainder operator keeps what is left after integral division.",
                List.of(
                        "A. The percentage",
                        "B. The power",
                        "C. The remainder",
                        "D. The quotient"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                235,
                "Which expression evaluates logical NOT of boolean ready?",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "The ! operator negates a boolean expression.",
                List.of(
                        "A. ~ready",
                        "B. -ready",
                        "C. ready!",
                        "D. !ready"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                236,
                "Which Java operator is used for bitwise exclusive OR on ints?",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "The ^ operator is bitwise XOR for integral operands.",
                List.of(
                        "A. ^",
                        "B. ||",
                        "C. &&",
                        "D. !="
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                237,
                "What is the value of 18 / 4 + 18 % 4 with int operands?",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Integer division gives 4, remainder 2, total 6.",
                List.of(
                        "A. 6.5",
                        "B. 6",
                        "C. 4",
                        "D. 5"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                238,
                "What prints?\nint x = 5;\nx *= 3;\nx -= 4;\nSystem.out.println(x);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Five times three is 15, minus four is 11.",
                List.of(
                        "A. 8",
                        "B. 4",
                        "C. 11",
                        "D. 15"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                239,
                "What number prints?\nint x = 2;\nSystem.out.println(x++ + 4);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Post-increment contributes the original value 2 to the expression.",
                List.of("6")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                240,
                "What prints?\nint x = 3;\nSystem.out.println(++x * 2);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Pre-increment changes x to 4 before multiplication.",
                List.of(
                        "A. 8",
                        "B. 6",
                        "C. 7",
                        "D. 9"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                241,
                "What prints?\nboolean a = true;\nboolean b = false;\nSystem.out.println(a || b);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Logical OR is true if at least one operand is true.",
                List.of(
                        "A. Compilation error",
                        "B. true",
                        "C. false",
                        "D. 1"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                242,
                "What prints?\nSystem.out.println(5 + 2 + \"X\");",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Additions are evaluated left to right until String concatenation begins.",
                List.of(
                        "A. X7",
                        "B. 5X2",
                        "C. 7X",
                        "D. 52X"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                243,
                "What prints?\nint x = 8;\nSystem.out.println(x > 5 ? \"High\" : \"Low\");",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "The conditional operator selects High when x > 5.",
                List.of(
                        "A. Low",
                        "B. true",
                        "C. Compilation error",
                        "D. High"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                244,
                "What prints?\nint x = 1;\nboolean b = false && (++x > 1);\nSystem.out.println(x);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Short-circuit && does not evaluate ++x when its left operand is false.",
                List.of(
                        "A. 1",
                        "B. 2",
                        "C. 0",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                245,
                "What prints?\nint a = 2, b = 4;\nSystem.out.println(a++ + ++b);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "a++ contributes 2 and ++b contributes 5.",
                List.of(
                        "A. 9",
                        "B. 7",
                        "C. 6",
                        "D. 8"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                246,
                "What number prints?\nint result = 20 - 4 * 3 + 8 / 2;\nSystem.out.println(result);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Evaluate multiplication and division first: 20 - 12 + 4 = 12.",
                List.of("12")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                247,
                "What prints?\nint n = 6;\nSystem.out.println(n & 3);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Binary 110 AND 011 equals binary 010, or 2.",
                List.of(
                        "A. 3",
                        "B. 6",
                        "C. 7",
                        "D. 2"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                248,
                "What prints?\nint x = 10;\nx %= 4;\nx *= 3;\nSystem.out.println(x);",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "10 mod 4 is 2, then 2 times 3 equals 6.",
                List.of(
                        "A. 6",
                        "B. 2",
                        "C. 9",
                        "D. 12"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                249,
                "In the provided Code class, declare int a = 23 and b = 5. Print integer quotient then remainder on separate lines.",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "Java integer division produces 4 and remainder 3.",
                "4\n3",
                List.of("/", "%", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                250,
                "In the provided Code class, declare int x = 3. Use += then *= to make x equal 20, and print it.",
                Topic.OPERATORS, Difficulty.INTERMEDIATE,
                "For example, add 2 then multiply by 4.",
                "20",
                List.of("+=", "*=", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
