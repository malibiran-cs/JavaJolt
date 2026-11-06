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

/** Additional Decisions advanced questions. Does not replace existing banks. */
public final class DecisionsAdvancedExpansion {
    private DecisionsAdvancedExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                352,
                "Which Java switch form avoids classic case fall-through by design?",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Arrow rules in modern switch do not fall through.",
                List.of(
                        "A. Arrow case labels using ->",
                        "B. Colon case labels without break",
                        "C. Nested if blocks",
                        "D. The default label"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                353,
                "What type must the condition in Java if evaluate to?",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Java requires a boolean expression; integers cannot be used as truth values.",
                List.of(
                        "A. double",
                        "B. boolean",
                        "C. int",
                        "D. String"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                354,
                "What does short-circuit && guarantee when the first operand is false?",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "&& omits the second evaluation once the result is determined.",
                List.of(
                        "A. Both operands become true",
                        "B. The program exits",
                        "C. The right operand is not evaluated",
                        "D. The right operand must be evaluated"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                355,
                "Which expression tests whether non-null text s has the same content as \"yes\" without relying on reference identity?",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "equals checks text content, and a constant receiver also tolerates null s.",
                List.of(
                        "A. s == \"yes\"",
                        "B. s = \"yes\"",
                        "C. s.compareTo(\"yes\") == 1",
                        "D. \"yes\".equals(s)"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                356,
                "When is a default case in a traditional switch executed?",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "default is selected if no case matches; fall-through can affect execution when there are no breaks.",
                List.of(
                        "A. When no case label matches",
                        "B. After every matching case automatically",
                        "C. Before every case",
                        "D. Only if a break appears"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                357,
                "Which syntax expresses a Java switch expression returning a value?",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "A switch expression can use arrow rules and assign the resulting value.",
                List.of(
                        "A. int n = switch { day: 10 };",
                        "B. int n = switch (day) { case 1 -> 10; default -> 0; };",
                        "C. int n = switch day -> 10;",
                        "D. int n = switch(day) case 1: 10;"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                358,
                "Which statement about a Java switch expression is correct?",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Switch expressions are exhaustive and yield a result.",
                List.of(
                        "A. It cannot use an int selector",
                        "B. It cannot use a default branch",
                        "C. It must produce a value for its selected path",
                        "D. It must use fall-through"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                359,
                "What prints?\nint x = 7;\nString label = switch (x) { case 1, 2 -> \"Low\"; case 7 -> \"Seven\"; default -> \"Other\"; };\nSystem.out.println(label);",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Case 7 matches and the switch expression yields Seven.",
                List.of(
                        "A. Low",
                        "B. Other",
                        "C. Compilation error",
                        "D. Seven"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                360,
                "What prints?\nString word = null;\nSystem.out.println(\"ok\".equals(word));",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "equals on non-null literal returns false for null input.",
                List.of(
                        "A. false",
                        "B. true",
                        "C. NullPointerException",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                361,
                "What number prints?\nint a = 1, b = 2, c = 3;\nint m = a > b ? a : (b > c ? b : c);\nSystem.out.println(m);",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "The nested conditional chooses the largest of 1, 2, and 3.",
                List.of("3")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                362,
                "What prints?\nint n = 4;\nif (n > 1 && n < 4) System.out.print(\"A\");\nelse if (n == 4) System.out.print(\"B\");\nelse System.out.print(\"C\");",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "The first range excludes four; the second condition matches.",
                List.of(
                        "A. C",
                        "B. Nothing",
                        "C. B",
                        "D. A"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                363,
                "What prints?\nint x = 0;\nif (true || (++x > 0)) { }\nSystem.out.println(x);",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Logical OR short-circuits and skips the increment.",
                List.of(
                        "A. 1",
                        "B. true",
                        "C. Compilation error",
                        "D. 0"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                364,
                "What prints?\nint x = -1;\nint sign = x < 0 ? -1 : (x == 0 ? 0 : 1);\nSystem.out.println(sign);",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Negative input selects the first branch of the ternary expression.",
                List.of(
                        "A. -1",
                        "B. 0",
                        "C. 1",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                365,
                "What letter prints?\nint x = 4;\nswitch (x) { case 2: System.out.print(\"A\"); break; case 4: System.out.print(\"B\"); break; default: System.out.print(\"C\"); }",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Case 4 matches, prints B, and breaks.",
                List.of("B")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                366,
                "What prints?\nint n = 2;\nint value = switch (n) { case 1 -> 10; case 2 -> 20; default -> 0; };\nSystem.out.println(value + 1);",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Case 2 yields 20, then one is added.",
                List.of(
                        "A. 11",
                        "B. 1",
                        "C. 21",
                        "D. 20"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                367,
                "What prints?\nint x = 4;\nboolean p = x++ == 4 || ++x == 6;\nSystem.out.println(x);",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "The left side is true so || skips the second increment; only x++ occurs.",
                List.of(
                        "A. 6",
                        "B. 4",
                        "C. 7",
                        "D. 5"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                368,
                "What number prints?\nint x = 9;\nint y = x % 2 == 0 ? x / 2 : x * 2;\nSystem.out.println(y);",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Nine is odd, selecting nine times two.",
                List.of("18")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                369,
                "What prints?\nint x = 6;\nif (x > 3) { if (x < 5) System.out.print(\"A\"); else System.out.print(\"B\"); }\nSystem.out.print(\"C\");",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "The nested condition is false, prints B, then the final statement prints C.",
                List.of(
                        "A. C",
                        "B. BC",
                        "C. AC",
                        "D. B"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                370,
                "What prints?\nint x = 2;\nswitch (x) { case 1: System.out.print(\"A\"); case 2: System.out.print(\"B\"); default: System.out.print(\"C\"); }",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "Classic colon-form switch falls from case 2 into default without a break.",
                List.of(
                        "A. ABC",
                        "B. C",
                        "C. BC",
                        "D. B"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                371,
                "Which Java statement fails to compile?",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "An int cannot serve directly as the boolean condition of an if statement.",
                List.of(
                        "A. if (true) { System.out.println(\"x\"); }",
                        "B. if (3 > 1) { System.out.println(\"x\"); }",
                        "C. if (Boolean.TRUE) { System.out.println(\"x\"); }",
                        "D. if (1) { System.out.println(\"x\"); }"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                372,
                "Write a complete Java program using a switch expression on int score = 85. Produce A for score >= 90, B for score >= 80, C otherwise, and print the result. Use a switch expression with arrow labels by first mapping score / 10.",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "score / 10 is 8, which can map to B using a switch expression.",
                "B",
                List.of("switch", "->", "default"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                373,
                "Write a complete Java program with int x = -4. Use a nested conditional ?: expression to print Negative, Zero, or Positive.",
                Topic.DECISIONS, Difficulty.ADVANCED,
                "x less than zero chooses Negative.",
                "Negative",
                List.of("?", ":", "x"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
