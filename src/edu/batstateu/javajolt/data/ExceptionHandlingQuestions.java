
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.ChallengeTier;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.IdentificationQuestion;
import edu.batstateu.javajolt.types.CodeQuestion;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;

public class ExceptionHandlingQuestions {

    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // BEGINNER
        questions.add(new MultipleChoiceQuestion(
                601,
                "Which keyword is used to handle an exception in Java?",
                Topic.EXCEPTION_HANDLING,
                Difficulty.BEGINNER,
                "The catch keyword handles an exception thrown from the associated try block.",
                List.of("A. try", "B. catch", "C. throw", "D. final"),
                "B"
        ));

        questions.add(new MultipleChoiceQuestion(
                602,
                "Which block contains code that may cause an exception?",
                Topic.EXCEPTION_HANDLING,
                Difficulty.BEGINNER,
                "The try block contains code that might throw an exception.",
                List.of("A. catch", "B. finally", "C. try", "D. import"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(   
                603,
                "Which block normally executes whether an exception occurs or not?",
                Topic.EXCEPTION_HANDLING,
                Difficulty.BEGINNER,
                "The finally block normally executes whether an exception occurs or not.",
                List.of("A. finally", "B. class", "C. return", "D. package"),
                "A"
        ));

        // FOUNDATIONAL — 608
        questions.add(new MultipleChoiceQuestion(
                608,
                "Which exception occurs when Java attempts integer division by zero?",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "Integer division by zero throws ArithmeticException.",
                List.of("A. NullPointerException", "B. NumberFormatException", "C. ArithmeticException", "D. ArrayIndexOutOfBoundsException"),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION — 609
        questions.add(new MultipleChoiceQuestion(
                609,
                "What does the following print?\ntry {\n    int result = 12 / 0;\n    System.out.println(\"A\");\n} catch (ArithmeticException e) {\n    System.out.println(\"B\");\n}\nSystem.out.println(\"C\");",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "The catch prints B and execution continues after it to print C.",
                List.of("A. A then C", "B. B then C", "C. A then B", "D. B only"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 610
        questions.add(new IdentificationQuestion(
                610,
                "What single word will this code print?\ntry {\n    int value = Integer.parseInt(\"twelve\");\n    System.out.println(value);\n} catch (NumberFormatException e) {\n    System.out.println(\"Invalid\");\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "Parsing nonnumeric text triggers NumberFormatException; the handler prints Invalid.",
                List.of("Invalid")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 611
        questions.add(new MultipleChoiceQuestion(
                611,
                "What will this code print?\ntry {\n    System.out.println(\"Start\");\n} catch (Exception e) {\n    System.out.println(\"Error\");\n} finally {\n    System.out.println(\"Finish\");\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "No error is thrown, so Start and Finish print.",
                List.of("A. Start only", "B. Error then Finish", "C. Start then Error", "D. Start then Finish"),
                "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 612
        questions.add(new IdentificationQuestion(
                612,
                "What number will this code print?\nint[] values = {3, 6};\ntry {\n    System.out.println(values[5]);\n} catch (ArrayIndexOutOfBoundsException e) {\n    System.out.println(99);\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "Accessing index 5 throws an exception, and the handler prints 99.",
                List.of("99")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE — 613
        questions.add(new MultipleChoiceQuestion(
                613,
                "What will this code print?\ntry {\n    throw new IllegalArgumentException();\n} catch (RuntimeException e) {\n    System.out.println(\"Handled\");\n} finally {\n    System.out.println(\"End\");\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "IllegalArgumentException is a RuntimeException, so it is caught before finally executes.",
                List.of("A. Handled only", "B. End only", "C. Handled then End", "D. Compilation error"),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 614
        questions.add(new MultipleChoiceQuestion(
                614,
                "What number will this code print?\nint x = 4;\ntry {\n    int y = 10 / x;\n    x += y;\n} catch (ArithmeticException e) {\n    x = 0;\n} finally {\n    x++;\n}\nSystem.out.println(x);",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "Integer division gives 2; x becomes 6, then finally increments to 7.",
                List.of("A. 4", "B. 6", "C. 7", "D. 0"),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 615
        questions.add(new IdentificationQuestion(
                615,
                "What number will this code print?\nint value = 1;\ntry {\n    value = Integer.parseInt(\"oops\");\n} catch (NumberFormatException e) {\n    value += 4;\n} finally {\n    value += 2;\n}\nSystem.out.println(value);",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "Parsing fails, so the catch adds 4 to 1; finally adds 2, producing 7.",
                List.of("7")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 616
        questions.add(new CodeQuestion(
                616,
                "Write a Java program declaring int numerator = 8 and int denominator = 0. Use try to divide them, catch ArithmeticException and print Recovered, then use finally to print Finished on the next line.",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "A catch for ArithmeticException handles division by zero, then finally prints Finished.",
                "Recovered\nFinished",
                List.of("try", "catch", "finally", "ArithmeticException"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // FOUNDATIONAL — 617
        questions.add(new MultipleChoiceQuestion(
                617,
                "Which Java keyword explicitly throws an exception object?",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "throw raises a specific exception instance.",
                List.of("A. throw", "B. throws", "C. catch", "D. try"),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 618
        questions.add(new MultipleChoiceQuestion(
                618,
                "What does throws IOException in a method declaration indicate?",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "throws declares a checked exception that may propagate to a caller.",
                List.of("A. The method always throws an IOException", "B. The method may pass an IOException to its caller", "C. The method handles every exception automatically", "D. The method cannot throw other exceptions"),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 619
        questions.add(new MultipleChoiceQuestion(
                619,
                "Which of these is an unchecked exception in Java?",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "NullPointerException is a RuntimeException, hence unchecked.",
                List.of("A. IOException", "B. FileNotFoundException", "C. NullPointerException", "D. SQLException"),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 620
        questions.add(new MultipleChoiceQuestion(
                620,
                "In catch (ArithmeticException e), what does e represent?",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "e is the variable holding the caught exception object.",
                List.of("A. The number that caused the error", "B. The caught exception object", "C. The return value of try", "D. The method that caused the exception"),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION — 621
        questions.add(new MultipleChoiceQuestion(
                621,
                "What does this code print?\ntry {\n    System.out.print(\"A\");\n    int result = 8 / 2;\n    System.out.print(\"B\");\n} catch (ArithmeticException e) {\n    System.out.print(\"X\");\n} finally {\n    System.out.print(\"C\");\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "No exception occurs. The try block prints A and B; finally prints C.",
                List.of("A. AB", "B. AXC", "C. ABC", "D. XC"),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 622
        questions.add(new MultipleChoiceQuestion(
                622,
                "What does this code print?\ntry {\n    Integer.parseInt(\"4x\");\n    System.out.print(\"A\");\n} catch (NumberFormatException e) {\n    System.out.print(\"B\");\n} catch (Exception e) {\n    System.out.print(\"C\");\n}\nSystem.out.print(\"D\");",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "NumberFormatException is caught by the first catch, which prints B; then D prints.",
                List.of("A. AD", "B. BCD", "C. CD", "D. BD"),
                "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 623
        questions.add(new IdentificationQuestion(
                623,
                "What single word will this code print?\nString text = null;\ntry {\n    System.out.println(text.length());\n} catch (NullPointerException e) {\n    System.out.println(\"Missing\");\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "Invoking a method on null throws NullPointerException, so the handler prints Missing.",
                List.of("Missing")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 624
        questions.add(new MultipleChoiceQuestion(
                624,
                "What does this code print?\ntry {\n    System.out.print(\"A\");\n    int[] items = {2, 4};\n    System.out.print(items[5]);\n    System.out.print(\"B\");\n} catch (ArrayIndexOutOfBoundsException e) {\n    System.out.print(\"C\");\n} finally {\n    System.out.print(\"D\");\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "A prints before the exception; C prints in catch and D in finally.",
                List.of("A. ABCD", "B. ACD", "C. ABD", "D. CD"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE — 625
        questions.add(new MultipleChoiceQuestion(
                625,
                "What does this code print?\ntry {\n    try {\n        Integer.parseInt(\"x\");\n        System.out.print(\"A\");\n    } catch (NumberFormatException e) {\n        System.out.print(\"B\");\n    }\n    System.out.print(\"C\");\n} finally {\n    System.out.print(\"D\");\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "The inner catch prints B; then execution continues with C and the finally prints D.",
                List.of("A. ABCD", "B. AD", "C. BCD", "D. BD"),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 626
        questions.add(new MultipleChoiceQuestion(
                626,
                "What happens when Java compiles this code?\ntry {\n    int result = 4 / 2;\n} catch (Exception e) {\n    System.out.println(\"General\");\n} catch (ArithmeticException e) {\n    System.out.println(\"Math\");\n}",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "The later ArithmeticException catch is unreachable because Exception catches that subtype first.",
                List.of("A. The program prints General", "B. The program prints Math", "C. The program prints nothing", "D. The program fails to compile"),
                "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 627
        questions.add(new IdentificationQuestion(
                627,
                "What number will this code print?\nint total = 0;\nString[] data = {\"4\", \"x\", \"6\"};\nfor (String value : data) {\n    try { total += Integer.parseInt(value); }\n    catch (NumberFormatException e) { total += 1; }\n    finally { total += 2; }\n}\nSystem.out.println(total);",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "4 adds 6 including finally; x adds 3 including finally; 6 adds 8 including finally, totaling 17.",
                List.of("17")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 628
        questions.add(new CodeQuestion(
                628,
                "Write a Java program declaring int score = -2. Within try, use if to throw new IllegalArgumentException when score is negative. Catch IllegalArgumentException to print Invalid score, and use finally to print Finished on a separate line.",
                Topic.EXCEPTION_HANDLING, Difficulty.BEGINNER,
                "Check score < 0 and throw the exception; the catch and finally blocks print the two messages.",
                "Invalid score\nFinished",
                List.of("try", "if", "throw", "catch", "IllegalArgumentException", "finally"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // INTERMEDIATE
        questions.add(new MultipleChoiceQuestion(
                604,
                "Which keyword manually sends an exception to be handled?",
                Topic.EXCEPTION_HANDLING,
                Difficulty.INTERMEDIATE,
                "throw creates and sends a specific exception from the current code.",
                List.of("A. throws", "B. throw", "C. catch", "D. error"),
                "B"
        ));

        questions.add(new MultipleChoiceQuestion(
                605,
                "What does a method declaration with 'throws IOException' indicate?",
                Topic.EXCEPTION_HANDLING,
                Difficulty.INTERMEDIATE,
                "throws declares that the method may pass an IOException to its caller.",
                List.of("A. The method catches every exception", "B. The method may pass IOException to its caller", "C. The method cannot fail", "D. The method always throws immediately"),
                "B"
        ));

        // ADVANCED
        questions.add(new MultipleChoiceQuestion(
                606,
                "Which catch block should appear first when handling both IOException and Exception?",
                Topic.EXCEPTION_HANDLING,
                Difficulty.ADVANCED,
                "The more specific IOException catch must come first; Exception would otherwise catch it first.",
                List.of("A. Exception", "B. IOException", "C. finally", "D. Throwable only"),
                "B"
        ));

        questions.add(new IdentificationQuestion(
                607,
                "What kind of exception must be caught or declared with throws? (one word)",
                Topic.EXCEPTION_HANDLING,
                Difficulty.ADVANCED,
                "A checked exception must be caught or declared.",
                List.of("checked")
        ));

        return questions;
    }
}
