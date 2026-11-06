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

/** Additional Decisions intermediate questions. Does not replace existing banks. */
public final class DecisionsIntermediateExpansion {
    private DecisionsIntermediateExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                330,
                "What does a switch statement choose among?",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "A switch selects a matching case based on its selector.",
                List.of(
                        "A. The thrown exception type only",
                        "B. The current thread",
                        "C. Cases matching a selector value",
                        "D. The number of loop iterations"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                331,
                "Which keyword designates the fallback branch in a traditional switch?",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "default runs if no matching case exists.",
                List.of(
                        "A. else",
                        "B. otherwise",
                        "C. finally",
                        "D. default"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                332,
                "What can follow else in an if/else chain?",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "else executes a statement or block when the preceding conditions are false.",
                List.of(
                        "A. A statement or a block",
                        "B. A required boolean expression in parentheses",
                        "C. A case label only",
                        "D. A mandatory return keyword"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                333,
                "Which Java operator combines two boolean conditions requiring both true?",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Logical AND requires both boolean expressions to be true.",
                List.of(
                        "A. ==",
                        "B. &&",
                        "C. ||",
                        "D. !"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                334,
                "In an if/else if chain, what happens after a true branch runs?",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "At most one branch executes in a single if/else-if/else chain.",
                List.of(
                        "A. The method immediately exits",
                        "B. The program throws an exception",
                        "C. Later branches in that chain are skipped",
                        "D. Every later condition must run"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                335,
                "Which switch case label is valid for an int selector?",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "An int switch accepts compatible integral compile-time constant case labels.",
                List.of(
                        "A. case 3.5:",
                        "B. case true:",
                        "C. case null:",
                        "D. case 3:"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                336,
                "Which expression is true if exactly one of boolean flags a and b is true?",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Boolean XOR returns true when operands differ.",
                List.of(
                        "A. a ^ b",
                        "B. a && b",
                        "C. a || b",
                        "D. !a && !b"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                337,
                "What prints?\nint n = 4;\nif (n < 3) System.out.print(\"A\");\nelse if (n < 5) System.out.print(\"B\");\nelse System.out.print(\"C\");",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "The second condition n < 5 is the first true condition.",
                List.of(
                        "A. Nothing",
                        "B. B",
                        "C. A",
                        "D. C"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                338,
                "What prints?\nint x = 3;\nif (x > 2) x += 5;\nif (x > 6) x *= 2;\nSystem.out.println(x);",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Both independent if statements execute, changing x to 8 then 16.",
                List.of(
                        "A. 6",
                        "B. 3",
                        "C. 16",
                        "D. 8"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                339,
                "What number prints?\nint score = 79;\nif (score >= 80) score += 10;\nelse score -= 9;\nSystem.out.println(score);",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "The else branch subtracts nine because 79 is below 80.",
                List.of("70")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                340,
                "What prints?\nint day = 2;\nswitch (day) { case 1: System.out.print(\"A\"); break; case 2: System.out.print(\"B\"); break; default: System.out.print(\"X\"); }",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Case 2 matches and the break exits the switch.",
                List.of(
                        "A. B",
                        "B. A",
                        "C. X",
                        "D. AB"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                341,
                "What prints?\nint x = 7;\nSystem.out.println(x >= 5 && x < 10 ? \"Inside\" : \"Outside\");",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Both range comparisons are true.",
                List.of(
                        "A. Compilation error",
                        "B. Inside",
                        "C. Outside",
                        "D. true"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                342,
                "What prints?\nint n = 0;\nif (n == 0 || 10 / n > 2) System.out.println(\"Safe\");",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "The || operator short-circuits after n == 0 is true.",
                List.of(
                        "A. Nothing",
                        "B. false",
                        "C. Safe",
                        "D. ArithmeticException"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                343,
                "What letter prints?\nint n = 9;\nif (n % 3 == 0 && n % 2 == 0) System.out.print(\"A\");\nelse if (n % 3 == 0) System.out.print(\"B\");\nelse System.out.print(\"C\");",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Nine is divisible by three but not by two, so the second branch runs.",
                List.of("B")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                344,
                "What prints?\nint x = 2;\nswitch (x) { case 1: System.out.print(\"A\"); case 2: System.out.print(\"B\"); case 3: System.out.print(\"C\"); break; default: System.out.print(\"D\"); }",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Without a break at case 2, control falls through case 3.",
                List.of(
                        "A. BC",
                        "B. B",
                        "C. BCD",
                        "D. ABC"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                345,
                "What prints?\nint a = 5;\nif (a > 2) if (a > 10) System.out.print(\"X\"); else System.out.print(\"Y\");",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "The else pairs with the nearest unmatched if, a > 10.",
                List.of(
                        "A. Compilation error",
                        "B. Y",
                        "C. X",
                        "D. Nothing"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                346,
                "What prints?\nint grade = 95;\nif (grade >= 60) System.out.print(\"Pass\");\nif (grade >= 90) System.out.print(\"Top\");",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Two independent if statements both have true conditions.",
                List.of(
                        "A. Pass",
                        "B. Nothing",
                        "C. PassTop",
                        "D. Top"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                347,
                "What number prints?\nint x = 5;\nif (x++ > 5) x += 10; else x += 2;\nSystem.out.println(x);",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "The comparison uses 5 and is false, but post-increment makes x 6 before else adds 2.",
                List.of("8")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                348,
                "What prints?\nint a = 4, b = 7;\nSystem.out.println(a > b ? a : (b > 5 ? b : 0));",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "The outer condition is false; the inner b > 5 condition selects b.",
                List.of(
                        "A. 7",
                        "B. 4",
                        "C. 0",
                        "D. true"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                349,
                "What prints?\nString s = \"go\";\nswitch(s) { case \"stop\": System.out.print(\"S\"); break; case \"go\": System.out.print(\"G\"); break; default: System.out.print(\"?\"); }",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Java switch supports String selectors and go matches the second case.",
                List.of(
                        "A. Compilation error",
                        "B. G",
                        "C. S",
                        "D. ?"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                350,
                "Write a complete Java program with int age = 19 and boolean verified = true. Print Allowed only when both conditions age >= 18 and verified hold, otherwise Denied. Use if and else.",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "The two conditions are true, so Allowed is printed.",
                "Allowed",
                List.of("if", "else", "&&", "verified"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                351,
                "Write a complete Java program using switch on int day = 3. Print Wed for case 3, Mon for case 1, and Other otherwise.",
                Topic.DECISIONS, Difficulty.INTERMEDIATE,
                "Case 3 selects Wed.",
                "Wed",
                List.of("switch", "case 3", "default"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
