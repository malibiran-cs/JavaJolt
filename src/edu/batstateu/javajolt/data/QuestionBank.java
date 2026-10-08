package edu.batstateu.javajolt.data;

import java.util.ArrayList;
import java.util.List;

import edu.batstateu.javajolt.Question;

public class QuestionBank {
    private final List<Question> questions = new ArrayList<>();

    public QuestionBank() {
        questions.addAll(VariablesQuestions.getQuestions());
        questions.addAll(OperatorsQuestions.getQuestions());
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
