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

/** Additional Operators advanced questions. Does not replace existing banks. */
public final class OperatorsAdvancedExpansion {
    private OperatorsAdvancedExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                251,
                "Which operator shifts a signed int to the right and fills high bits with the sign bit?",
                Topic.OPERATORS, Difficulty.ADVANCED,
                ">> is arithmetic right shift; >>> fills high bits with zeros.",
                List.of(
                        "A. >>>",
                        "B. <<",
                        "C. <",
                        "D. >>"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                252,
                "Which operator performs an unsigned right shift on int?",
                Topic.OPERATORS, Difficulty.ADVANCED,
                ">>> shifts right and inserts zeros in high bits.",
                List.of(
                        "A. >>>",
                        "B. >>",
                        "C. <<",
                        "D. ^"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                253,
                "Which operator has higher precedence than logical &&?",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Unary logical NOT has higher precedence than &&.",
                List.of(
                        "A. Conditional ?:",
                        "B. Logical !",
                        "C. Logical ||",
                        "D. Assignment ="
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                254,
                "Which expression accesses an element without a possible null boolean expression evaluation?",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "&& short-circuits when the left operand is false.",
                List.of(
                        "A. true | riskyCheck()",
                        "B. true ^ riskyCheck()",
                        "C. false && riskyCheck()",
                        "D. false & riskyCheck()"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                255,
                "What data type is the result of the expression 5 / 2 when both literals are int?",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Both operands are ints, so / performs integer division.",
                List.of(
                        "A. double",
                        "B. float",
                        "C. long",
                        "D. int"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                256,
                "What is the purpose of the ternary operator ?:?",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "condition ? left : right chooses between two expressions.",
                List.of(
                        "A. Select a value based on a boolean condition",
                        "B. Repeat code until a condition becomes false",
                        "C. Catch an exception",
                        "D. Define a constructor"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                257,
                "What is true of the operator & on boolean operands?",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Boolean & computes AND without short-circuiting.",
                List.of(
                        "A. It is illegal for booleans",
                        "B. It evaluates both operands",
                        "C. It always skips the right operand",
                        "D. It performs assignment"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                258,
                "What prints?\nint x = -8;\nSystem.out.println(x >> 2);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Arithmetic right shift retains the sign: negative eight shifted twice gives negative two.",
                List.of(
                        "A. -4",
                        "B. 0",
                        "C. -2",
                        "D. 2"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                259,
                "What prints?\nint x = 5;\nSystem.out.println(x << 2);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Left shifting five by two bits multiplies it by four when no relevant overflow occurs.",
                List.of(
                        "A. 10",
                        "B. 7",
                        "C. 1",
                        "D. 20"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                260,
                "What prints?\nSystem.out.println(true || false && false);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "&& binds more tightly than ||; true || false is true.",
                List.of(
                        "A. true",
                        "B. false",
                        "C. Compilation error",
                        "D. 0"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                261,
                "What integer prints?\nint x = 7;\nSystem.out.println(x ^ 3);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Binary 111 XOR 011 is 100, decimal four.",
                List.of("4")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                262,
                "What prints?\nint x = 2;\nint y = x++ * ++x;\nSystem.out.println(y);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "x++ yields 2, then ++x yields 4. Product is 8.",
                List.of(
                        "A. 12",
                        "B. 4",
                        "C. 8",
                        "D. 6"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                263,
                "What prints?\nint n = 15;\nSystem.out.println(n >>> 2);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Unsigned right shift by two discards the low bits; 15 becomes 3.",
                List.of(
                        "A. -4",
                        "B. 7",
                        "C. 60",
                        "D. 3"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                264,
                "What prints?\nboolean flag = true;\nSystem.out.println(flag ^ true);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Boolean XOR is true only when operands differ.",
                List.of(
                        "A. false",
                        "B. true",
                        "C. 0",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                265,
                "What prints?\nint n = 3;\nSystem.out.println(n++ + ++n + n);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "The terms evaluate to 3, 5, 5 for a total of 13.",
                List.of(
                        "A. 14",
                        "B. 13",
                        "C. 11",
                        "D. 12"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                266,
                "What prints?\nint x = 12;\nint y = 10;\nSystem.out.println(x & y);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "1100 AND 1010 equals 1000, decimal eight.",
                List.of(
                        "A. 6",
                        "B. 2",
                        "C. 8",
                        "D. 14"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                267,
                "What prints?\nint x = 4;\nboolean result = (x++ > 4) && (++x > 5);\nSystem.out.println(x);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "The first comparison is false after returning 4, so the second increment is skipped.",
                List.of(
                        "A. 6",
                        "B. 4",
                        "C. 7",
                        "D. 5"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                268,
                "What number prints?\nint x = 11;\nint y = (x % 4 == 3) ? x / 2 : x * 2;\nSystem.out.println(y);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "The remainder condition is true and integer division 11/2 is 5.",
                List.of("5")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                269,
                "What prints?\nint x = 2;\nint y = x++ + x++ + ++x;\nSystem.out.println(y);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "The three operand values are 2, 3 and 5; the sum is ten.",
                List.of(
                        "A. 8",
                        "B. 10",
                        "C. 9",
                        "D. 11"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                270,
                "What prints?\nString s = \"X\";\nSystem.out.println(s + (2 + 3) * 2);",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "Parentheses and multiplication occur before String concatenation.",
                List.of(
                        "A. X5*2",
                        "B. X232",
                        "C. X10",
                        "D. X52"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                271,
                "Write a complete Java program that declares int flags = 12 and int mask = 10, computes flags & mask, and prints the result.",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "12 AND 10 is 8.",
                "8",
                List.of("&", "int flags", "int mask"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                272,
                "Write a complete Java program that declares int score = 73. Use ?: to print Pass if score >= 60, or Retry otherwise.",
                Topic.OPERATORS, Difficulty.ADVANCED,
                "The conditional operator selects Pass for 73.",
                "Pass",
                List.of("?", ":", "score"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
