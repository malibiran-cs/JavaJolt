
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.enums.ChallengeTier;
import edu.batstateu.javajolt.types.CodeQuestion;
import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.IdentificationQuestion;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;

public class LoopsQuestions {

    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // BEGINNER
        questions.add(new MultipleChoiceQuestion(
                401,
                "Which loop is commonly used when you know how many times to repeat an action?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "A for loop is commonly used when the number of repetitions is known.",
                List.of("A. if statement", "B. for loop", "C. switch statement", "D. try block"),
                "B"
        ));

        questions.add(new MultipleChoiceQuestion(
                402,
                "How many times does this loop print Hello?\n    for (int i = 0; i < 3; i++) {\n        System.out.println(\"Hello\");\n    }",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The loop runs for i = 0, 1, and 2, so it prints Hello three times.",
                List.of("A. 2 times", "B. 3 times", "C. 4 times", "D. 0 times"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                403,
                "Which keyword immediately stops a loop in Java?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The break keyword immediately exits the current loop.",
                List.of("A. stop", "B. exit", "C. break", "D. continue"),
                "C"
        ));

        
        // =====================================
        // LOOPS BEGINNER — ADDITIONAL QUESTIONS
        // =====================================

        // FOUNDATIONAL — 408
        questions.add(new MultipleChoiceQuestion(
                408,
                "Which Java keyword starts a loop that repeats "
                + "while a specified condition remains true?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The while loop evaluates its condition "
                + "before each iteration.",
                List.of(
                        "A. switch",
                        "B. while",
                        "C. catch",
                        "D. return"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 409
        questions.add(new MultipleChoiceQuestion(
                409,
                "What happens if a while loop's condition "
                + "is false before its first iteration?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "A while loop checks its condition before "
                + "executing its body. A false condition "
                + "means the body does not execute.",
                List.of(
                        "A. The body runs once",
                        "B. The body runs twice",
                        "C. The body does not run",
                        "D. The loop runs forever"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));


        // =====================================
        // APPLICATION QUESTIONS
        // =====================================

        // APPLICATION — 410
        questions.add(new MultipleChoiceQuestion(
                410,
                "What numbers will this loop print?\n"
                + "for (int i = 2; i <= 6; i += 2) {\n"
                + "    System.out.println(i);\n"
                + "}",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The loop starts at 2 and increases by 2. "
                + "The printed values are 2, 4, and 6.",
                List.of(
                        "A. 2, 3, 4, 5, 6",
                        "B. 2, 4, 6",
                        "C. 0, 2, 4, 6",
                        "D. 2, 4"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 411
        questions.add(new IdentificationQuestion(
                411,
                "What number will this code print?\n"
                + "int i = 1;\n"
                + "int total = 0;\n"
                + "while (i <= 3) {\n"
                + "    total += i;\n"
                + "    i++;\n"
                + "}\n"
                + "System.out.println(total);",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The loop adds 1, 2, and 3 to total. "
                + "The final result is 6.",
                List.of("6")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 412
        questions.add(new MultipleChoiceQuestion(
                412,
                "What will this code print?\n"
                + "int count = 5;\n"
                + "do {\n"
                + "    count++;\n"
                + "} while (count < 3);\n"
                + "System.out.println(count);",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "A do-while loop executes its body at least "
                + "once. count becomes 6 before the "
                + "condition is evaluated.",
                List.of(
                        "A. 3",
                        "B. 5",
                        "C. 6",
                        "D. 7"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // =====================================
        // CHALLENGE QUESTIONS
        // =====================================

        // CHALLENGE — 413
        questions.add(new MultipleChoiceQuestion(
                413,
                "How many times will Hello be printed?\n"
                + "for (int i = 1; i <= 2; i++) {\n"
                + "    for (int j = 1; j <= 3; j++) {\n"
                + "        System.out.println(\"Hello\");\n"
                + "    }\n"
                + "}",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The outer loop runs 2 times. For each "
                + "outer iteration, the inner loop runs "
                + "3 times. Therefore, Hello prints 6 times.",
                List.of(
                        "A. 2 times",
                        "B. 3 times",
                        "C. 5 times",
                        "D. 6 times"
                ),
                "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 414
        questions.add(new IdentificationQuestion(
                414,
                "What number will this code print?\n"
                + "int sum = 0;\n"
                + "for (int i = 1; i <= 5; i++) {\n"
                + "    if (i == 3) {\n"
                + "        continue;\n"
                + "    }\n"
                + "    sum += i;\n"
                + "}\n"
                + "System.out.println(sum);",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The continue statement skips adding 3. "
                + "The sum is 1 + 2 + 4 + 5 = 12.",
                List.of("12")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 415
        questions.add(new MultipleChoiceQuestion(
                415,
                "What happens when this code runs?\n"
                + "int i = 0;\n"
                + "while (i < 3) {\n"
                + "    System.out.println(i);\n"
                + "}",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The value of i never changes. The "
                + "condition remains true, so the loop "
                + "continuously prints 0.",
                List.of(
                        "A. It prints 0, 1, 2 and stops",
                        "B. It prints 0 indefinitely",
                        "C. It prints 0 once",
                        "D. It causes a compilation error"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 416
        questions.add(new CodeQuestion(
                416,
                "Write a Java program using a for loop "
                + "that prints the numbers 1 through 4, "
                + "each on a separate line.",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "Use a for loop starting at 1 and "
                + "continuing while the counter is <= 4. "
                + "Print the counter inside the loop.",
                "1\n2\n3\n4",
                List.of("for", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        
        // =====================================
        // LOOPS BEGINNER — FINAL 12 QUESTIONS
        // =====================================

        // FOUNDATIONAL — 417
        questions.add(new MultipleChoiceQuestion(
                417,
                "What is the correct order of the three "
                + "sections in a standard Java for loop?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "A for loop follows the structure: "
                + "initialization; condition; update.",
                List.of(
                        "A. Condition; update; initialization",
                        "B. Initialization; condition; update",
                        "C. Update; initialization; condition",
                        "D. Initialization; update; condition"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));


        // FOUNDATIONAL — 418
        questions.add(new MultipleChoiceQuestion(
                418,
                "Given int i = 0;, which while loop "
                + "correctly increments i until it reaches 3?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "A while loop needs parentheses around its "
                + "condition and must update the counter "
                + "to eventually terminate.",
                List.of(
                        "A. while (i < 3) { i++; }",
                        "B. while i < 3 { i++; }",
                        "C. while [i < 3] { i++; }",
                        "D. while (i < 3); { i++; }"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));


        // FOUNDATIONAL — 419
        questions.add(new MultipleChoiceQuestion(
                419,
                "Which update statement increases "
                + "the integer variable i by 2?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The compound assignment i += 2 "
                + "adds 2 to the current value of i.",
                List.of(
                        "A. i++;",
                        "B. i *= 2;",
                        "C. i += 2;",
                        "D. i -= 2;"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));


        // FOUNDATIONAL — 420
        questions.add(new MultipleChoiceQuestion(
                420,
                "Which is the correct structure "
                + "of a Java do-while loop?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "A do-while loop starts with do, "
                + "executes its body, and checks "
                + "the condition afterward.",
                List.of(
                        "A. do { statements; } while (condition);",
                        "B. while (condition) do { statements; }",
                        "C. do (condition) { statements; }",
                        "D. while { statements; } do (condition);"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));


        // =====================================
        // APPLICATION QUESTIONS
        // =====================================

        // APPLICATION — 421
        questions.add(new MultipleChoiceQuestion(
                421,
                "What numbers will this code print?\n"
                + "for (int i = 1; i < 6; i += 2) {\n"
                + "    System.out.println(i);\n"
                + "}",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The counter starts at 1 and increases "
                + "by 2, producing 1, 3, and 5.",
                List.of(
                        "A. 1, 2, 3, 4, 5",
                        "B. 1, 3, 5",
                        "C. 1, 3, 5, 7",
                        "D. 2, 4, 6"
                ),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // APPLICATION — 422
        questions.add(new IdentificationQuestion(
                422,
                "What number will this code print?\n"
                + "int number = 10;\n"
                + "while (number > 3) {\n"
                + "    number -= 3;\n"
                + "}\n"
                + "System.out.println(number);",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The variable changes from 10 to 7, "
                + "then 4, then 1. The loop stops "
                + "because 1 is not greater than 3.",
                List.of("1")
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // APPLICATION — 423
        questions.add(new MultipleChoiceQuestion(
                423,
                "What will this code print?\n"
                + "for (int i = 1; i <= 6; i++) {\n"
                + "    if (i == 4) {\n"
                + "        break;\n"
                + "    }\n"
                + "    System.out.print(i);\n"
                + "}",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The loop prints 1, 2, and 3. "
                + "When i reaches 4, break exits "
                + "the loop before printing 4.",
                List.of(
                        "A. 123456",
                        "B. 1234",
                        "C. 123",
                        "D. 456"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // APPLICATION — 424
        questions.add(new MultipleChoiceQuestion(
                424,
                "What will this code print?\n"
                + "int count = 0;\n"
                + "for (int i = 7; i >= 1; i -= 3) {\n"
                + "    count++;\n"
                + "}\n"
                + "System.out.println(count);",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The loop runs with i equal to 7, "
                + "4, and 1. That is three iterations.",
                List.of(
                        "A. 1",
                        "B. 2",
                        "C. 4",
                        "D. 3"
                ),
                "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));


        // =====================================
        // CHALLENGE QUESTIONS
        // =====================================

        // CHALLENGE — 425
        questions.add(new IdentificationQuestion(
                425,
                "What number will this code print?\n"
                + "int count = 0;\n"
                + "for (int i = 1; i <= 3; i++) {\n"
                + "    for (int j = 1; j <= i; j++) {\n"
                + "        count++;\n"
                + "    }\n"
                + "}\n"
                + "System.out.println(count);",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The inner loop runs 1 time when i = 1, "
                + "2 times when i = 2, and 3 times "
                + "when i = 3. The total is 6.",
                List.of("6")
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // CHALLENGE — 426
        questions.add(new MultipleChoiceQuestion(
                426,
                "What will this code print?\n"
                + "for (int i = 1; i <= 5; i++) {\n"
                + "    if (i % 2 == 0) {\n"
                + "        continue;\n"
                + "    }\n"
                + "    System.out.print(i + \"-\");\n"
                + "}",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The continue statement skips even "
                + "numbers. Only 1, 3, and 5 "
                + "are printed with hyphens.",
                List.of(
                        "A. 1-2-3-4-5-",
                        "B. 2-4-",
                        "C. 1-3-5-",
                        "D. 1-3-5"
                ),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // CHALLENGE — 427
        questions.add(new MultipleChoiceQuestion(
                427,
                "What will this code print?\n"
                + "int sum = 0;\n"
                + "for (int i = 1; i <= 6; i++) {\n"
                + "    if (i == 5) {\n"
                + "        break;\n"
                + "    }\n"
                + "    if (i % 2 == 0) {\n"
                + "        continue;\n"
                + "    }\n"
                + "    sum += i;\n"
                + "}\n"
                + "System.out.println(sum);",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "Even values are skipped, and break "
                + "stops the loop when i reaches 5. "
                + "Only 1 and 3 are added, giving 4.",
                List.of(
                        "A. 4",
                        "B. 6",
                        "C. 9",
                        "D. 16"
                ),
                "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // CHALLENGE — 428
        questions.add(new CodeQuestion(
                428,
                "Write a Java program using a for loop "
                + "that prints the squares of the numbers "
                + "1 through 4, each on a separate line.\n"
                + "Compute each square inside the loop "
                + "using multiplication.",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "Example logic:\n"
                + "for (int i = 1; i <= 4; i++) {\n"
                + "    System.out.println(i * i);\n"
                + "}",
                "1\n4\n9\n16",
                List.of(
                        "for",
                        "*",
                        "System.out.println"
                ),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));


        // INTERMEDIATE
        questions.add(new MultipleChoiceQuestion(
                404,
                "Which loop always runs its body at least once?",
                Topic.LOOPS,
                Difficulty.INTERMEDIATE,
                "A do-while loop checks its condition after running the body.",
                List.of("A. for", "B. while", "C. do-while", "D. enhanced for"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                405,
                "What does continue do inside a loop?",
                Topic.LOOPS,
                Difficulty.INTERMEDIATE,
                "continue skips the rest of the current iteration and moves to the next one.",
                List.of("A. Exits the program", "B. Skips to the next iteration", "C. Stops the loop permanently", "D. Restarts the application"),
                "B"
        ));

        // ADVANCED
        questions.add(new IdentificationQuestion(
                406,
                "How many times does this loop print? for (int i = 1; i <= 5; i++) (type the number)",
                Topic.LOOPS,
                Difficulty.ADVANCED,
                "The values 1, 2, 3, 4, and 5 satisfy the condition, so the loop runs five times.",
                List.of("5")
        ));

        questions.add(new MultipleChoiceQuestion(
                407,
                "What is the usual purpose of a nested loop?",
                Topic.LOOPS,
                Difficulty.ADVANCED,
                "A loop inside another loop is useful for processing rows and columns or other repeated combinations.",
                List.of("A. Repeating combinations of operations", "B. Declaring a class", "C. Catching exceptions", "D. Comparing only two values"),
                "A"
        ));

        return questions;
    }
}
