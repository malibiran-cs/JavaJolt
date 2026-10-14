
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
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

        return questions;
    }
}
