
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
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

        return questions;
    }
}
