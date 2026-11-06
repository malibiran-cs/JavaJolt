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

/** Additional Loops intermediate questions. Does not replace existing banks. */
public final class LoopsIntermediateExpansion {
    private LoopsIntermediateExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                430,
                "What does break do in a loop?",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "break terminates the nearest enclosing loop.",
                List.of(
                        "A. Restarts the loop",
                        "B. Closes the JVM",
                        "C. Exits the nearest enclosing loop",
                        "D. Skips only the current statement"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                431,
                "When does a while loop check its condition?",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "while is a pretest loop.",
                List.of(
                        "A. Only after all iterations",
                        "B. Exactly once at the end",
                        "C. Only when break occurs",
                        "D. Before each iteration"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                432,
                "Which loop is most convenient for iterating over every element in an array without indices?",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The enhanced for loop visits each element directly.",
                List.of(
                        "A. Enhanced for",
                        "B. do-while",
                        "C. switch",
                        "D. try-catch"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                433,
                "Which loop is usually best when the number of iterations is predetermined?",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "A for loop groups initialization, condition, and update.",
                List.of(
                        "A. throw",
                        "B. for",
                        "C. switch",
                        "D. catch"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                434,
                "What does continue do in a for loop?",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "continue skips the rest of the current body and advances the loop.",
                List.of(
                        "A. Exits the JVM",
                        "B. Skips all remaining loop iterations",
                        "C. Skips to the update step and next condition check",
                        "D. Stops the entire method"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                435,
                "How often is a for-loop initializer executed normally?",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "Initialization is performed once before the first condition check.",
                List.of(
                        "A. Once per iteration",
                        "B. Only after the condition becomes false",
                        "C. Never",
                        "D. Once"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                436,
                "What is a nested loop?",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "Nested loops allow repeated inner work for each outer iteration.",
                List.of(
                        "A. A loop within the body of another loop",
                        "B. A loop without any condition",
                        "C. Two loops in separate methods",
                        "D. A loop that always throws"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                437,
                "What prints?\nfor (int i = 3; i > 0; i--) System.out.print(i);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The counter decreases through 3, 2, and 1.",
                List.of(
                        "A. 3",
                        "B. 321",
                        "C. 123",
                        "D. 32"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                438,
                "What prints?\nint sum = 0;\nfor (int i = 2; i <= 8; i += 2) sum += i;\nSystem.out.println(sum);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The values 2, 4, 6, 8 total 20.",
                List.of("20")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                439,
                "What prints?\nint i = 0;\nwhile (i < 5) { i++; if (i == 3) break; }\nSystem.out.println(i);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The break occurs after incrementing i to 3.",
                List.of(
                        "A. 2",
                        "B. 5",
                        "C. 4",
                        "D. 3"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                440,
                "What prints?\nint count = 0;\nfor (int i = 0; i < 5; i++) { if (i % 2 == 0) continue; count++; }\nSystem.out.println(count);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "Only indices one and three increment count.",
                List.of(
                        "A. 2",
                        "B. 3",
                        "C. 5",
                        "D. 1"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                441,
                "What prints?\nint x = 6;\ndo { x -= 2; } while (x > 2);\nSystem.out.println(x);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The loop decreases 6 to 4 then 2 and stops.",
                List.of(
                        "A. 6",
                        "B. 2",
                        "C. 4",
                        "D. 0"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                442,
                "How many times does the inner statement run?\nfor (int i = 0; i < 2; i++)\n    for (int j = 0; j < 4; j++) System.out.print(\"x\");",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "Two outer iterations each perform four inner iterations.",
                List.of("8")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                443,
                "What prints?\nfor (int i = 1; i <= 5; i += 2) System.out.print(i);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The increment of 2 selects odd values 1, 3 and 5.",
                List.of(
                        "A. 12345",
                        "B. 1357",
                        "C. 24",
                        "D. 135"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                444,
                "What prints?\nint x = 0;\nfor (int i = 1; i <= 3; i++) for (int j = 1; j <= i; j++) x++;\nSystem.out.println(x);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The inner loop runs 1 + 2 + 3 times.",
                List.of(
                        "A. 6",
                        "B. 3",
                        "C. 9",
                        "D. 4"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                445,
                "What prints?\nint sum = 0;\nfor (int i = 1; i <= 6; i++) { if (i == 5) break; if (i % 2 == 0) continue; sum += i; }\nSystem.out.println(sum);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "Only 1 and 3 contribute before the break at five.",
                List.of(
                        "A. 16",
                        "B. 4",
                        "C. 6",
                        "D. 9"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                446,
                "What prints?\nint n = 1;\nwhile (n < 20) n *= 2;\nSystem.out.println(n);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "Doubling goes 1, 2, 4, 8, 16, 32 before terminating.",
                List.of("32")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                447,
                "What prints?\nint n = 0;\nouter: for (int i = 0; i < 3; i++) { for (int j = 0; j < 3; j++) { n++; if (i == 1 && j == 1) break outer; } }\nSystem.out.println(n);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The first outer iteration contributes 3, and second contributes 2 before labeled break.",
                List.of(
                        "A. 4",
                        "B. 6",
                        "C. 9",
                        "D. 5"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                448,
                "What prints?\nint a = 0;\nfor (int i = 0; i < 3; i++) { for (int j = 0; j < 2; j++) { if (i == j) continue; a++; } }\nSystem.out.println(a);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "Of six pairs, two have i == j, leaving four increments.",
                List.of(
                        "A. 4",
                        "B. 2",
                        "C. 3",
                        "D. 6"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                449,
                "What prints?\nint product = 1;\nfor (int i = 1; i <= 4; i++) product *= i;\nSystem.out.println(product);",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The loop computes factorial four: 1×2×3×4 = 24.",
                List.of("24")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                450,
                "In the supplied Code class, use a for loop to compute 1+2+3+4+5 and print the sum.",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "A loop can accumulate all five integers.",
                "15",
                List.of("for", "+=", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                451,
                "In the supplied Code class, use nested for loops to print two lines, each containing exactly three asterisks with no spaces.",
                Topic.LOOPS, Difficulty.INTERMEDIATE,
                "The outer loop produces two rows; the inner produces three stars per row.",
                "***\n***",
                List.of("for", "System.out"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
