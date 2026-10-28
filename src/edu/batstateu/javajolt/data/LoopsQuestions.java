
package edu.batstateu.javajolt.data;

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
        ));

        questions.add(new MultipleChoiceQuestion(
                403,
                "Which keyword immediately stops a loop in Java?",
                Topic.LOOPS,
                Difficulty.BEGINNER,
                "The break keyword immediately exits the current loop.",
                List.of("A. stop", "B. exit", "C. break", "D. continue"),
                "C"
        ));

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
