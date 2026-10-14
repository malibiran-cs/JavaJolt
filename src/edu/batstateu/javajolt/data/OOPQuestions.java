
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;

public class OOPQuestions {

    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // BEGINNER
        questions.add(new MultipleChoiceQuestion(
                701,
                "Which keyword is used to create an object in Java?",
                Topic.OOP,
                Difficulty.BEGINNER,
                "The new keyword creates an object by allocating memory and calling a constructor.",
                List.of("A. class", "B. this", "C. new", "D. void"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                702,
                "What is a class in Java?",
                Topic.OOP,
                Difficulty.BEGINNER,
                "A class is a blueprint that defines the data and behavior of objects.",
                List.of("A. A blueprint for creating objects", "B. A loop structure", "C. A data type for decimals only", "D. An exception handler"),
                "A"
        ));

        questions.add(new MultipleChoiceQuestion(
                703,
                "Which OOP concept allows a class to inherit features from another class?",
                Topic.OOP,
                Difficulty.BEGINNER,
                "Inheritance allows a class to acquire accessible fields and methods from another class.",
                List.of("A. Encapsulation", "B. Inheritance", "C. Compilation", "D. Iteration"),
                "B"
        ));

        return questions;
    }
}
