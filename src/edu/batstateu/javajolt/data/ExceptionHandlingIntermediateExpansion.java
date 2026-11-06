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

/** Additional ExceptionHandling intermediate questions. Does not replace existing banks. */
public final class ExceptionHandlingIntermediateExpansion {
    private ExceptionHandlingIntermediateExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                630,
                "Which type is a checked exception?",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "IOException is checked and must be caught or declared.",
                List.of(
                        "A. ArithmeticException",
                        "B. IllegalArgumentException",
                        "C. java.io.IOException",
                        "D. NullPointerException"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                631,
                "What does finally usually do?",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "finally is designed for cleanup after a try statement.",
                List.of(
                        "A. Catches an exception automatically",
                        "B. Prevents all errors",
                        "C. Always runs before try",
                        "D. Executes cleanup after try/catch"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                632,
                "What is the superclass of most Java exception objects?",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "Throwable is the root of Java exception and error hierarchies.",
                List.of(
                        "A. Throwable",
                        "B. String",
                        "C. Object[]",
                        "D. Runnable"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                633,
                "What does e.getMessage() return on an Exception e?",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "getMessage returns the throwable detail text.",
                List.of(
                        "A. The exception class loader",
                        "B. Its detail message, possibly null",
                        "C. The current line number always",
                        "D. The stack trace as an array"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                634,
                "Which exception is raised by Integer.parseInt(\"abc\")?",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "Invalid decimal text causes NumberFormatException.",
                List.of(
                        "A. IOException",
                        "B. ClassCastException",
                        "C. NumberFormatException",
                        "D. ArithmeticException"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                635,
                "Which exception often occurs on a null reference field access?",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "Dereferencing a null reference causes NullPointerException.",
                List.of(
                        "A. NumberFormatException",
                        "B. IndexOutOfBoundsException",
                        "C. IllegalAccessException",
                        "D. NullPointerException"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                636,
                "What prints?\ntry { System.out.print(\"A\"); int n=1/0; System.out.print(\"B\"); } catch(ArithmeticException e){ System.out.print(\"C\"); }",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The divide-by-zero skips B and enters catch after A.",
                List.of(
                        "A. AC",
                        "B. ABC",
                        "C. AB",
                        "D. C"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                637,
                "What prints?\ntry { System.out.print(\"A\"); } finally { System.out.print(\"B\"); }",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The try succeeds and finally still executes.",
                List.of(
                        "A. Compilation error",
                        "B. AB",
                        "C. A",
                        "D. B"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                638,
                "What prints?\ntry { Integer.parseInt(\"x\"); } catch(NumberFormatException e){ System.out.print(\"N\"); } catch(Exception e){ System.out.print(\"E\"); }",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The first matching specific catch handles the parsing error.",
                List.of(
                        "A. NE",
                        "B. Nothing",
                        "C. N",
                        "D. E"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                639,
                "What prints?\nint n=3;\ntry{ n /= 0; }catch(ArithmeticException e){ n+=4; }\nSystem.out.println(n);",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The integer divide throws before assignment; catch adds four to the original three.",
                List.of("7")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                640,
                "What prints?\ntry { System.out.print(\"1\"); throw new IllegalArgumentException(); } catch(RuntimeException e){System.out.print(\"2\");} finally{System.out.print(\"3\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "IllegalArgumentException is a RuntimeException; catch and finally both execute.",
                List.of(
                        "A. 123",
                        "B. 13",
                        "C. 23",
                        "D. 12"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                641,
                "What prints?\ntry { int[] a={1}; System.out.print(a[1]); } catch(ArrayIndexOutOfBoundsException e){ System.out.print(\"X\"); }",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "Index one is outside the one-element array.",
                List.of(
                        "A. Compilation error",
                        "B. X",
                        "C. 1",
                        "D. 0"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                642,
                "What prints?\nint x=0;\ntry { Integer.parseInt(\"bad\"); } catch(NumberFormatException e) { x=5; } finally { x++; }\nSystem.out.println(x);",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "Catch assigns five and finally increments it.",
                List.of("6")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                643,
                "What prints?\nString text = null;\ntry { System.out.print(text.length()); } catch(NullPointerException e) {System.out.print(\"N\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "Accessing length() on a null String throws NullPointerException.",
                List.of(
                        "A. 0",
                        "B. null",
                        "C. Compilation error",
                        "D. N"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                644,
                "What happens if catch(Exception e) appears before catch(ArithmeticException e) on the same try?",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The second catch is unreachable because Exception already handles its subtype.",
                List.of(
                        "A. Compilation error",
                        "B. ArithmeticException catch always runs",
                        "C. The first catch is skipped",
                        "D. Both catches always run"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                645,
                "What prints?\ntry { throw new IllegalArgumentException(); } catch(RuntimeException e){System.out.print(\"R\");} finally{System.out.print(\"F\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The exception is caught as RuntimeException and finally also runs.",
                List.of(
                        "A. Compilation error",
                        "B. RF",
                        "C. R",
                        "D. F"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                646,
                "What prints?\nint result=1;\ntry { int x=5/0; result=7; } catch(ArithmeticException e){result+=2;} finally{result*=3;}\nSystem.out.println(result);",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The failed division leaves result at one; catch makes it three and finally triples it.",
                List.of("9")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                647,
                "What prints?\ntry { System.out.print(\"T\"); } catch(Exception e) { System.out.print(\"C\"); } finally { System.out.print(\"F\"); }",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "No exception is thrown; catch is skipped but finally runs.",
                List.of(
                        "A. TCF",
                        "B. T",
                        "C. CF",
                        "D. TF"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                648,
                "Which declaration correctly handles a checked IOException thrown by readFile()?",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "A checked exception must be handled with try/catch or declared with throws.",
                List.of(
                        "A. try { readFile(); } catch (java.io.IOException e) { }",
                        "B. readFile(); // always fine",
                        "C. if (readFile()) { }",
                        "D. catch(java.io.IOException e) { readFile(); }"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                649,
                "What prints?\nint n=0;\nString[] data={\"2\",\"bad\",\"3\"};\nfor(String s:data){try{n+=Integer.parseInt(s);}catch(NumberFormatException e){n++;}}\nSystem.out.println(n);",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The valid values add 2 and 3; invalid text adds 1 in catch.",
                List.of("6")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                650,
                "In the supplied Code class, parse String text = \"oops\" inside try, catch NumberFormatException, and print Invalid.",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "A failed parse triggers the matching catch.",
                "Invalid",
                List.of("try", "catch", "NumberFormatException", "parseInt"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                651,
                "In the supplied Code class, declare int a=9 and b=0; use try/catch/finally to print Zero division when arithmetic fails, and Done afterward.",
                Topic.EXCEPTION_HANDLING, Difficulty.INTERMEDIATE,
                "The catch handles division by zero and finally prints Done.",
                "Zero division\nDone",
                List.of("try", "catch", "finally", "ArithmeticException"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
