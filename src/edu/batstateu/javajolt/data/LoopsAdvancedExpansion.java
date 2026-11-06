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

/** Additional Loops advanced questions. Does not replace existing banks. */
public final class LoopsAdvancedExpansion {
    private LoopsAdvancedExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                452,
                "What does a labeled break exit?",
                Topic.LOOPS, Difficulty.ADVANCED,
                "A labeled break transfers control past the labeled statement, often an outer loop.",
                List.of(
                        "A. The statement named by its label",
                        "B. Only the innermost if condition",
                        "C. Only the current switch expression",
                        "D. The entire JVM"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                453,
                "Which loop kind checks its condition only after running its body?",
                Topic.LOOPS, Difficulty.ADVANCED,
                "do-while performs at least one iteration when reached.",
                List.of(
                        "A. enhanced for",
                        "B. do-while",
                        "C. while",
                        "D. for"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                454,
                "What is time complexity for two nested loops each making n iterations?",
                Topic.LOOPS, Difficulty.ADVANCED,
                "The inner work repeats n times for each of n outer iterations.",
                List.of(
                        "A. O(1)",
                        "B. O(log n)",
                        "C. O(n²)",
                        "D. O(n)"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                455,
                "Which condition is true for every possible Java int value i?",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Every int value is less than or equal to Integer.MAX_VALUE.",
                List.of(
                        "A. i < Integer.MAX_VALUE",
                        "B. i > Integer.MIN_VALUE",
                        "C. i == 0",
                        "D. i <= Integer.MAX_VALUE"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                456,
                "In an enhanced for over int[], what does changing the loop variable change?",
                Topic.LOOPS, Difficulty.ADVANCED,
                "An enhanced for element variable of primitive type holds a copy.",
                List.of(
                        "A. The local loop variable, not the array element",
                        "B. The array element automatically",
                        "C. The array length",
                        "D. The array reference"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                457,
                "What is the effect of a continue with a loop label?",
                Topic.LOOPS, Difficulty.ADVANCED,
                "continue label transfers control to the next iteration of the labeled loop.",
                List.of(
                        "A. Runs the inner loop a second time automatically",
                        "B. Advances the named enclosing loop to its next iteration",
                        "C. Exits the whole method",
                        "D. Throws a checked exception"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                458,
                "Which operation is useful for avoiding an off-by-one bug in a zero-based array loop?",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Valid indices end at length - 1, so less-than length is appropriate.",
                List.of(
                        "A. Start i at -1",
                        "B. Always use i <= 0",
                        "C. Use i < array.length",
                        "D. Use i <= array.length"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                459,
                "What prints?\nint x = 0;\nfor (int i = 0; i < 4; i++) for (int j = i; j < 4; j++) x++;\nSystem.out.println(x);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Counts are 4 + 3 + 2 + 1 = 10.",
                List.of(
                        "A. 8",
                        "B. 16",
                        "C. 6",
                        "D. 10"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                460,
                "What prints?\nint sum = 0;\nouter: for (int i = 0; i < 3; i++) { for (int j = 0; j < 3; j++) { if (j == 1) continue outer; sum++; } }\nSystem.out.println(sum);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Only j=0 increments once per outer loop; labeled continue skips the rest.",
                List.of(
                        "A. 3",
                        "B. 6",
                        "C. 9",
                        "D. 1"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                461,
                "What prints?\nint x = 1;\nfor (int i = 0; i < 4; i++) x = x * 2 + 1;\nSystem.out.println(x);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "The sequence is 1, 3, 7, 15, 31.",
                List.of("31")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                462,
                "What prints?\nint count = 0;\nfor (int i = 0; i < 5; i++) { if (i % 2 == 0) continue; for (int j = 0; j < i; j++) count++; }\nSystem.out.println(count);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Only i=1 and i=3 reach the inner loop, contributing 1+3.",
                List.of(
                        "A. 6",
                        "B. 9",
                        "C. 4",
                        "D. 3"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                463,
                "What prints?\nint n = 20;\nwhile (n > 2) n /= 2;\nSystem.out.println(n);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Integer division produces 20, 10, 5, 2.",
                List.of(
                        "A. 5",
                        "B. 1",
                        "C. 0",
                        "D. 2"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                464,
                "What prints?\nfor (int i = 1; i <= 4; i++) { if (i == 2) break; System.out.print(i); }",
                Topic.LOOPS, Difficulty.ADVANCED,
                "break on iteration two stops before printing two.",
                List.of(
                        "A. 1",
                        "B. 12",
                        "C. 1234",
                        "D. Nothing"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                465,
                "What prints?\nint total = 0;\nfor (int i = 1; i <= 3; i++) for (int j = 1; j <= 2; j++) total += i * j;\nSystem.out.println(total);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "The sum is (1+2+3)×(1+2)=18.",
                List.of("18")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                466,
                "What prints?\nint c = 0;\nouter: for (int i = 1; i <= 3; i++) for (int j = 1; j <= 3; j++) { c++; if (i == 2 && j == 2) break outer; }\nSystem.out.println(c);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "The first row has 3 steps and the second row ends after 2.",
                List.of(
                        "A. 6",
                        "B. 9",
                        "C. 5",
                        "D. 4"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                467,
                "What prints?\nint n = 0;\nfor (int i = 1; i <= 4; i++) for (int j = 1; j <= i; j++) if (j % 2 == 0) n++;\nSystem.out.println(n);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Even j occurrences contribute 0+1+1+2 = 4.",
                List.of(
                        "A. 6",
                        "B. 5",
                        "C. 10",
                        "D. 4"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                468,
                "What prints?\nint n = 0;\nfor (int i = 1; i <= 8; i *= 2) n += i;\nSystem.out.println(n);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Add powers of two: 1+2+4+8=15.",
                List.of("15")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                469,
                "What prints?\nint[] a = {2,4,6};\nfor (int value : a) value += 1;\nSystem.out.println(a[0]);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Enhanced for copies each primitive element into a local variable.",
                List.of(
                        "A. Compilation error",
                        "B. 2",
                        "C. 3",
                        "D. 1"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                470,
                "What prints?\nint c = 0;\nfor (int i = 0; i < 3; i++) { for (int j = 0; j < 3; j++) { if (i+j >= 3) break; c++; } }\nSystem.out.println(c);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "Row counts are 3, 3 and 2 for a total of eight.",
                List.of(
                        "A. 9",
                        "B. 7",
                        "C. 8",
                        "D. 6"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                471,
                "What prints?\nint count = 0;\nfor (int i = 1; i <= 4; i++) for (int j = 1; j <= 4; j++) if (i == j) count++;\nSystem.out.println(count);",
                Topic.LOOPS, Difficulty.ADVANCED,
                "The diagonal pairs are (1,1), (2,2), (3,3), (4,4).",
                List.of("4")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                472,
                "In the supplied Code class, use nested for loops to print a triangle containing one *, two **, then three *** on separate lines.",
                Topic.LOOPS, Difficulty.ADVANCED,
                "The outer loop controls rows; the inner prints that row number of stars.",
                "*\n**\n***",
                List.of("for", "System.out"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                473,
                "In the supplied Code class, use a while loop and an if condition to sum even numbers 2 through 10 and print the result.",
                Topic.LOOPS, Difficulty.ADVANCED,
                "The even values 2+4+6+8+10 total 30.",
                "30",
                List.of("while", "if", "%", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
