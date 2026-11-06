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

/** Additional ExceptionHandling advanced questions. Does not replace existing banks. */
public final class ExceptionHandlingAdvancedExpansion {
    private ExceptionHandlingAdvancedExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                652,
                "What is try-with-resources designed to do?",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "Resources declared in try(...) are automatically closed.",
                List.of(
                        "A. Automatically close AutoCloseable resources",
                        "B. Convert unchecked to checked exceptions",
                        "C. Skip resource initialization",
                        "D. Avoid compiling catch blocks"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                653,
                "Which keyword rethrows an already caught exception object e?",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "throw is the statement used to throw an exception instance.",
                List.of(
                        "A. catch e;",
                        "B. throw e;",
                        "C. throws e;",
                        "D. return e;"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                654,
                "What is the cause of an exception object?",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "Throwable supports chaining a cause to explain underlying failure.",
                List.of(
                        "A. Only the catch parameter",
                        "B. The thread priority",
                        "C. Another throwable that triggered it",
                        "D. Only a line number"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                655,
                "What is multi-catch syntax for two unrelated exception classes A and B?",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "Multi-catch uses | to join unrelated exception alternatives.",
                List.of(
                        "A. catch (A, B e)",
                        "B. catch (A && B e)",
                        "C. catch (A or B e)",
                        "D. catch (A | B e)"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                656,
                "Which exception is checked?",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "SQLException is checked and requires handling or declaration.",
                List.of(
                        "A. java.sql.SQLException",
                        "B. IllegalStateException",
                        "C. IndexOutOfBoundsException",
                        "D. NumberFormatException"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                657,
                "What happens to exceptions thrown from finally when one is already pending?",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "An exception thrown in finally can mask an existing pending exception.",
                List.of(
                        "A. The JVM refuses to compile finally",
                        "B. The finally exception can replace the pending exception",
                        "C. The earlier exception must win",
                        "D. Both are always printed automatically"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                658,
                "What prints?\ntry {throw new IllegalStateException();} catch(Exception e) {System.out.print(\"C\");} finally {System.out.print(\"F\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "IllegalStateException is caught as Exception, then finally runs.",
                List.of(
                        "A. F",
                        "B. Compilation error",
                        "C. CF",
                        "D. C"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                659,
                "What prints?\nint n=0;\ntry { n=4/2; } catch(ArithmeticException e){n=8;} finally{n++;}\nSystem.out.println(n);",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "Division succeeds setting two, then finally increments to three.",
                List.of(
                        "A. 2",
                        "B. 8",
                        "C. 9",
                        "D. 3"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                660,
                "What prints?\nint x=10;\ntry { x=Integer.parseInt(\"bad\"); }catch(NumberFormatException e){x-=3;}finally{x/=7;}\nSystem.out.println(x);",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "The catch reduces ten to seven, then finally divides by seven.",
                List.of("1")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                661,
                "Which output occurs?\ntry { try {throw new RuntimeException();} finally{System.out.print(\"I\");} } catch(RuntimeException e){System.out.print(\"O\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "The inner finally runs during propagation before the outer catch.",
                List.of(
                        "A. O",
                        "B. IO",
                        "C. OI",
                        "D. I"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                662,
                "What prints?\ntry { Integer.parseInt(\"2\"); System.out.print(\"A\"); } catch(NumberFormatException e){System.out.print(\"B\");} finally{System.out.print(\"C\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "Parsing succeeds, so A and then finally C print.",
                List.of(
                        "A. ABC",
                        "B. A",
                        "C. AC",
                        "D. BC"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                663,
                "What prints?\ntry {throw new NumberFormatException();} catch(IllegalArgumentException e){System.out.print(\"X\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "NumberFormatException extends IllegalArgumentException, so it is caught.",
                List.of(
                        "A. Nothing",
                        "B. Compilation error",
                        "C. Uncaught exception",
                        "D. X"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                664,
                "What prints?\nint a=0;\nfor(int i=0;i<2;i++){try{a+=Integer.parseInt(i==0?\"x\":\"4\");}catch(NumberFormatException e){a+=1;}finally{a+=2;}}\nSystem.out.println(a);",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "First iteration adds one plus two; second adds four plus two, totaling nine.",
                List.of("9")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                665,
                "What prints?\ntry {throw new IllegalArgumentException();} catch(RuntimeException e){System.out.print(\"R\");} finally {System.out.print(\"F\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "IllegalArgumentException is a RuntimeException and finally still executes.",
                List.of(
                        "A. Compilation error",
                        "B. RF",
                        "C. R",
                        "D. F"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                666,
                "Which code is invalid because a later catch is unreachable?",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "A general Exception catch makes any later subtype catch unreachable.",
                List.of(
                        "A. catch(NumberFormatException e) {} followed by catch(RuntimeException e) {}",
                        "B. catch(ArithmeticException e) {} followed by catch(Exception e) {}",
                        "C. catch(Exception e) {} followed by catch(IOException e) {}",
                        "D. catch(IOException e) {} followed by catch(Exception e) {}"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                667,
                "What prints?\ntry { throw new RuntimeException(\"x\"); } catch(RuntimeException e){System.out.print(\"A\");throw e;} finally {System.out.print(\"B\");}",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "Catch prints A, rethrows, and finally prints B while unwinding.",
                List.of(
                        "A. A only, no propagation",
                        "B. B only, no propagation",
                        "C. Compilation error",
                        "D. AB before the exception propagates"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                668,
                "What prints?\nint x=1;\ntry { x+=2; throw new IllegalStateException(); } catch(RuntimeException e){ x*=3; } finally { x+=4; }\nSystem.out.println(x);",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "The try makes three, catch makes nine, finally makes thirteen.",
                List.of("13")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                669,
                "What prints?\ntry { throw new IllegalArgumentException(); } finally { System.out.print(\"F\"); }",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "finally executes while the unchecked exception propagates to the caller.",
                List.of(
                        "A. Compilation error",
                        "B. F then an uncaught exception",
                        "C. Nothing",
                        "D. Only F with normal exit"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                670,
                "What prints?\ntry { System.out.print(\"A\"); } finally { System.out.print(\"B\"); }\nSystem.out.print(\"C\");",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "Normal execution prints A, executes finally B, and continues to C.",
                List.of(
                        "A. AC",
                        "B. BC",
                        "C. ABC",
                        "D. AB"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                671,
                "What prints?\nint n=0;\ntry {throw new NumberFormatException();}catch(IllegalArgumentException e){n=5;}finally{n+=3;}\nSystem.out.println(n);",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "NumberFormatException is an IllegalArgumentException, and finally adds three.",
                List.of("8")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                672,
                "In the supplied Code class, use try-with-resources with java.io.StringReader reading the text Hi, then print its first character via read(). Print its integer character code.",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "The first character H has code 72 and StringReader is AutoCloseable.",
                "72",
                List.of("try (", "StringReader", "read()", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                673,
                "In the supplied Code class, throw a new IllegalArgumentException inside try, catch RuntimeException, and print Handled. Use finally to print End on the next line.",
                Topic.EXCEPTION_HANDLING, Difficulty.ADVANCED,
                "The broader RuntimeException handler catches IllegalArgumentException.",
                "Handled\nEnd",
                List.of("throw", "catch", "finally", "IllegalArgumentException"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
