package edu.service;

import java.util.List;

import edu.question.Question;
import edu.data.QuestionBank;
import edu.enums.Difficulty;
import edu.enums.Topic;

public class QuestionService {
    private QuestionBank questionBank;

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

    // ANSWER EVALUATOR
    // No instanceof needed: Java picks the right isCorrect() for each question type (polymorphism)
    public boolean isCorrect(Question question, String userInput) {
        return question.isCorrect(userInput);
    }
}