package edu.batstateu.javajolt.data;

import java.util.ArrayList;
import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.*;
import edu.batstateu.javajolt.types.*;

public class OperatorsQuestions {
    // OPERATORS QUESTIONS
    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new MultipleChoiceQuestion(
                    3,
                    "What is an example of an operator in java?",
                    Topic.OPERATORS,
                    Difficulty.GGEZ,
                    List.of("A. .add()", "B. .equals()", "C. ="),
                    "C"
            )
        );

        questions.add(new IdentificationQuestion(
                    4,
                    "Who was the first president of the Philippines?",
                    Topic.OPERATORS,
                    Difficulty.GGEZ,
                    List.of("Emilio Aguinaldo")
            )
        );

        return questions;
    }
}
