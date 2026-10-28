
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

        // INTERMEDIATE
        questions.add(new MultipleChoiceQuestion(
                704,
                "Which access modifier allows a member to be used only inside its declaring class?",
                Topic.OOP,
                Difficulty.INTERMEDIATE,
                "private restricts access to the class that declares the member.",
                List.of("A. public", "B. protected", "C. private", "D. static"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                705,
                "What is method overloading?",
                Topic.OOP,
                Difficulty.INTERMEDIATE,
                "Overloading defines methods with the same name but different parameter lists.",
                List.of("A. Replacing a parent method", "B. Same method name with different parameters", "C. Hiding every field", "D. Creating multiple packages"),
                "B"
        ));

        // ADVANCED
        questions.add(new MultipleChoiceQuestion(
                706,
                "Which OOP principle hides internal data and requires access through methods?",
                Topic.OOP,
                Difficulty.ADVANCED,
                "Encapsulation protects an object's state behind a controlled public interface.",
                List.of("A. Encapsulation", "B. Iteration", "C. Compilation", "D. Recursion"),
                "A"
        ));

        questions.add(new MultipleChoiceQuestion(
                707,
                "What annotation commonly marks a method that overrides a superclass method?",
                Topic.OOP,
                Difficulty.ADVANCED,
                "@Override tells the compiler that the method is intended to replace an inherited method.",
                List.of("A. @Inherited", "B. @Override", "C. @Super", "D. @Method"),
                "B"
        ));

        return questions;
    }
}
