
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.IdentificationQuestion;
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
