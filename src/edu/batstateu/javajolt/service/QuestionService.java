package edu.batstateu.javajolt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;

public class QuestionService {
    private final QuestionBank questionBank;

    public QuestionService(QuestionBank questionBank) {
        this.questionBank = questionBank;
    }

    // 1ST LAYER DIVISION
    public List<Question> getQuestionsByTopic(Topic topic) {
        return questionBank.getQuestions()
                           .stream()
                           .filter(question -> question.getTopic() == topic)
                           .toList();
    }

    // 2ND LAYER DIVISION
    public List<Question> getQuestionsByDifficulty(List<Question> questions, Difficulty difficulty) {
        return questions.stream()
                        .filter(question -> question.getDifficulty() == difficulty)
                        .toList();
    }

    // SELECTION: questions for one topic and difficulty, in random order
    public List<Question> selectQuestions(Topic topic, Difficulty difficulty) {
        List<Question> matching = getQuestionsByDifficulty(getQuestionsByTopic(topic), difficulty);

        if (matching.isEmpty()) {
                throw new IllegalStateException("No " + difficulty.getLabel() + " questions for "
                    + topic.getLabel() + " yet. Try another topic or level.");
        }

        List<Question> sequence = new ArrayList<>(matching);
        Collections.shuffle(sequence);
        return sequence;
    }

    // ANSWER EVALUATOR
    public boolean isCorrect(Question question, String userInput) {
        return question.isCorrect(userInput);
    }
}
