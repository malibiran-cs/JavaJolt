package edu.data;

import java.util.ArrayList;
import java.util.List;

import edu.question.Question;
import edu.enums.Difficulty;
import edu.enums.Topic;
import edu.question.types.IdentificationQuestion;
import edu.question.types.MultipleChoiceQuestion;

public class OperatorsQuestions {
    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new MultipleChoiceQuestion(
                3,
                "What is an example of an operator in java?",
                Topic.OPERATORS,
                Difficulty.GGEZ,
                List.of("A. .add()", "B. .equals()", "C. ="),
                "C"));

        questions.add(new IdentificationQuestion(
                4,
                "Who was the first president of the Philippines?",
                Topic.OPERATORS,
                Difficulty.GGEZ,
                List.of("Emilio Aguinaldo")));

        return questions;
    }
}
