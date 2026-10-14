
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;

public class ArraysQuestions {

    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // BEGINNER
        questions.add(new MultipleChoiceQuestion(
                501,
                "Which symbol is used to access an array element by index?",
                Topic.ARRAYS,
                Difficulty.BEGINNER,
                "Square brackets [] are used to access an array element using its index.",
                List.of("A. ()", "B. {}", "C. []", "D. <>"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                502,
                "What is the index of the first element in a Java array?",
                Topic.ARRAYS,
                Difficulty.BEGINNER,
                "Java arrays use zero-based indexing, so the first element is at index 0.",
                List.of("A. 0", "B. 1", "C. -1", "D. 2"),
                "A"
        ));

        questions.add(new MultipleChoiceQuestion(
                503,
                "What does this code print?\n    int[] numbers = {10, 20, 30};\n    System.out.println(numbers[1]);",
                Topic.ARRAYS,
                Difficulty.BEGINNER,
                "The element at index 1 is 20 because array indexing starts at 0.",
                List.of("A. 10", "B. 20", "C. 30", "D. 1"),
                "B"
        ));

        return questions;
    }
}
