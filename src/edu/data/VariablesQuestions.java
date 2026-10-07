package edu.data;

import java.util.ArrayList;
import java.util.List;

import edu.question.Question;
import edu.question.types.*;
import edu.enums.*;

public class VariablesQuestions {
    // VARIABLES QUESTIONS
    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new MultipleChoiceQuestion(
                    1,
                    "What is an example of a variable in java?",
                    Topic.VARIABLES,
                    Difficulty.GGEZ,
                    List.of("A. public class Variables {}", "B. package question.data", "C. String variable"),
                    "C"
            )
        );

        questions.add(new IdentificationQuestion(
                    2,
                    "How many letters are in the alphabet?",
                    Topic.VARIABLES,
                    Difficulty.GGEZ,
                    List.of("26", "twenty-six", "twenty six")
            )
        );

        return questions;
    }
}