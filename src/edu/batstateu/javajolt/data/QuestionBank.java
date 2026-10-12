package edu.batstateu.javajolt.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.batstateu.javajolt.Question;

public class QuestionBank {
    private final List<Question> questions = new ArrayList<>();

    public QuestionBank() {
        // ADD ONE LINE HERE FOR EVERY NEW TOPIC FILE
        questions.addAll(VariablesQuestions.getQuestions());
        questions.addAll(OperatorsQuestions.getQuestions());
        questions.addAll(DecisionsQuestions.getQuestions());

        checkForDuplicateIds();
    }

    // Catches two teammates accidentally using the same question ID
    private void checkForDuplicateIds() {
        Set<Integer> seen = new HashSet<>();
        for (Question question : questions) {
            if (!seen.add(question.getId())) {
                throw new IllegalStateException("Duplicate question ID: " + question.getId());
            }
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
