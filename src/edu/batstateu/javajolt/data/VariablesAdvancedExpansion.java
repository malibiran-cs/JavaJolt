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

/** Additional Variables advanced questions. Does not replace existing banks. */
public final class VariablesAdvancedExpansion {
    private VariablesAdvancedExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                151,
                "Which Java numeric promotion occurs in the expression byteValue + 1 when byteValue is byte?",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Binary numeric promotion converts byte to int before addition.",
                List.of(
                        "A. The result is a byte",
                        "B. The result is a long",
                        "C. The result is a boolean",
                        "D. The result is an int"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                152,
                "Which local declaration requires an explicit cast to compile?",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Converting a double literal to int is narrowing and requires a cast.",
                List.of(
                        "A. int n = (int) 8.8;",
                        "B. double n = 8;",
                        "C. long n = 8;",
                        "D. float n = 8;"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                153,
                "What is true about String objects in Java?",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "String methods that appear to modify text return other String objects.",
                List.of(
                        "A. Their length can be reassigned",
                        "B. They are immutable",
                        "C. They are mutable character arrays",
                        "D. They are primitive values"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                154,
                "Which expression is guaranteed to compare the text contents of two non-null String variables a and b?",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "equals checks contents, whereas == checks whether references designate the same object.",
                List.of(
                        "A. a = b",
                        "B. a.compareTo(b) == 1",
                        "C. a.equals(b)",
                        "D. a == b"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                155,
                "Which declaration creates a compile-time constant usable in a switch case label?",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "A static final primitive initialized with a constant expression can be a compile-time constant.",
                List.of(
                        "A. static int LIMIT = 4;",
                        "B. final int LIMIT;",
                        "C. int LIMIT = 4;",
                        "D. static final int LIMIT = 4;"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                156,
                "What happens to an int variable when its value exceeds Integer.MAX_VALUE by adding 1?",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Java int arithmetic wraps modulo 2^32 without throwing on normal overflow.",
                List.of(
                        "A. It wraps to Integer.MIN_VALUE",
                        "B. It becomes a long automatically",
                        "C. It becomes infinity",
                        "D. An exception is always thrown"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                157,
                "Which Java method converts the String \"42\" to the primitive int 42?",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "parseInt returns the int value represented by valid decimal digits.",
                List.of(
                        "A. (int) \"42\"",
                        "B. Integer.parseInt(\"42\")",
                        "C. Integer.valueOf(\"42\").toString()",
                        "D. String.toInt(\"42\")"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                158,
                "What value does a newly allocated instance field of type boolean have by default?",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Instance fields have default values; local variables do not. A boolean field defaults to false.",
                List.of(
                        "A. null",
                        "B. Uninitialized error",
                        "C. false",
                        "D. true"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                159,
                "What prints?\nint a = 4;\nint b = a++;\nSystem.out.println(a + \":\" + b);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Post-increment yields the old value for b, then increments a.",
                List.of(
                        "A. 4:5",
                        "B. 5:5",
                        "C. 4:4",
                        "D. 5:4"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                160,
                "What prints?\nint a = 4;\nint b = ++a;\nSystem.out.println(a + \":\" + b);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Pre-increment changes a before the assignment to b.",
                List.of(
                        "A. 5:5",
                        "B. 4:5",
                        "C. 5:4",
                        "D. 4:4"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                161,
                "What prints?\nString s = \"Java\";\ns = s.concat(\"Jolt\");\nSystem.out.println(s.length());",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Concatenation returns JavaJolt, whose length is eight.",
                List.of(
                        "A. 9",
                        "B. 8",
                        "C. 4",
                        "D. 7"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                162,
                "What integer prints?\nint n = Integer.MAX_VALUE;\nSystem.out.println(n + 1);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Adding one to the maximum signed 32-bit int wraps to the minimum.",
                List.of("-2147483648")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                163,
                "What prints?\nint x = 6;\n{ int y = x * 2; x = y; }\nSystem.out.println(x);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "The inner block can modify x declared in the enclosing block.",
                List.of(
                        "A. 6",
                        "B. 18",
                        "C. Compilation error",
                        "D. 12"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                164,
                "What prints?\nchar c = 'A';\nint code = c + 1;\nSystem.out.println(code);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "The char A has Unicode value 65, and arithmetic promotes it to int.",
                List.of(
                        "A. 66",
                        "B. 65",
                        "C. A1",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                165,
                "What prints?\ndouble amount = 7.8;\nint whole = (int) amount;\nSystem.out.println(whole);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "An explicit cast from a positive finite double to int truncates toward zero.",
                List.of("7")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                166,
                "What prints?\nint a = 7;\nint b = 3;\ndouble result = (double)(a / b);\nSystem.out.println(result);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Integer division takes place inside the parentheses before the cast.",
                List.of(
                        "A. 2",
                        "B. Compilation error",
                        "C. 2.0",
                        "D. 2.3333333333333335"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                167,
                "What prints?\nString a = new String(\"hi\");\nString b = new String(\"hi\");\nSystem.out.println(a == b);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Two separate new String instances are different references.",
                List.of(
                        "A. true",
                        "B. hi",
                        "C. Compilation error",
                        "D. false"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                168,
                "What happens?\nfinal int x;\nx = 3;\nx = 4;",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "A final local variable may only be assigned once.",
                List.of(
                        "A. Compilation error",
                        "B. Prints 4",
                        "C. Prints 3",
                        "D. Runtime exception"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                169,
                "What prints?\nint x = 5;\nint y = x++ + ++x;\nSystem.out.println(y);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "The first operand evaluates to 5, x increments to 6, then ++x yields 7; total 12.",
                List.of("12")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                170,
                "What prints?\nString s = \"hello\";\ns.replace(\"h\", \"H\");\nSystem.out.println(s);",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "replace returns a new String. The result was not assigned.",
                List.of(
                        "A. hHello",
                        "B. Compilation error",
                        "C. hello",
                        "D. Hello"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                171,
                "In the supplied Code class, declare final int BASE = 7 and int multiplier = 6; print their product.",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "A constant and a regular variable can participate in multiplication.",
                "42",
                List.of("final", "BASE", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                172,
                "In the supplied Code class, declare String word = \"Java\"; use a String method to convert it to uppercase, store the returned value back in word, and print it.",
                Topic.VARIABLES, Difficulty.ADVANCED,
                "Assign the result returned by toUpperCase because strings are immutable.",
                "JAVA",
                List.of("toUpperCase", "word", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
