package edu.batstateu.javajolt.question.data;

import java.util.ArrayList;
import java.util.List;

import edu.batstateu.javajolt.question.Question;

public class QuestionBank {
    private List<Question> questions = new ArrayList<>();

    public QuestionBank() {
        questions.addAll(VariablesQuestions.getQuestions());
        questions.addAll(OperatorsQuestions.getQuestions());
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
