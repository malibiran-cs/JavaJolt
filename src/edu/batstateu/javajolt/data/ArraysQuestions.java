
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.IdentificationQuestion;
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

        // INTERMEDIATE
        questions.add(new MultipleChoiceQuestion(
                504,
                "Which property gives the number of elements in an array named values?",
                Topic.ARRAYS,
                Difficulty.INTERMEDIATE,
                "The length field stores the number of elements in an array.",
                List.of("A. values.size()", "B. values.length", "C. values.count()", "D. values.capacity"),
                "B"
        ));

        questions.add(new IdentificationQuestion(
                505,
                "What exception occurs when code accesses an invalid array index? (one class name)",
                Topic.ARRAYS,
                Difficulty.INTERMEDIATE,
                "Accessing outside the valid range throws ArrayIndexOutOfBoundsException.",
                List.of("ArrayIndexOutOfBoundsException")
        ));

        // ADVANCED
        questions.add(new MultipleChoiceQuestion(
                506,
                "What is printed?\n    int[] values = {2, 4, 6};\n    int total = 0;\n    for (int value : values) total += value;\n    System.out.println(total);",
                Topic.ARRAYS,
                Difficulty.ADVANCED,
                "The enhanced for loop adds every element: 2 + 4 + 6 equals 12.",
                List.of("A. 6", "B. 10", "C. 12", "D. 246"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                507,
                "Which declaration creates a two-dimensional integer array with 2 rows and 3 columns?",
                Topic.ARRAYS,
                Difficulty.ADVANCED,
                "The syntax int[2][3] creates an array with two rows, each sized for three integers.",
                List.of("A. int[2, 3] table", "B. int[][] table = new int[2][3]", "C. int table = new int(2, 3)", "D. array<int> table[2][3]"),
                "B"
        ));

        return questions;
    }
}
