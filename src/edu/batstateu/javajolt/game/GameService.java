package edu.batstateu.javajolt.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.*;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.ui.GameUI;

public class GameService {
    private final QuestionService questionService;
    private final GameUI ui;

    private final List<Question> operatorsQuestions;

    private final List<Question> ggezOperatorsQuestions;
    private final List<Question> normalOperatorsQuestions;
    private final List<Question> lunaticOperatorsQuestions;
    // NOTE: EVENTUALLY MAKE A BIGGER CONTAINER THAT CONTAINS ALL TOPIC, DIVIDE BY DIFFICULTY

    public GameService(QuestionService questionService) {
    this(questionService, new GameUI());
    }

    public GameService(QuestionService questionService, GameUI ui) {
    this.questionService = questionService;
    this.ui = ui;

    operatorsQuestions =
            questionService.getQuestionsByTopic(Topic.OPERATORS);

    ggezOperatorsQuestions =
            questionService.getQuestionsByDifficulty(
                    operatorsQuestions, Difficulty.GGEZ);

    normalOperatorsQuestions =
            questionService.getQuestionsByDifficulty(
                    operatorsQuestions, Difficulty.NORMAL);

    lunaticOperatorsQuestions =
            questionService.getQuestionsByDifficulty(
                    operatorsQuestions, Difficulty.LUNATIC);
}

    public void run() {
        startSession();
    }
        

    // SERVICE METHODS
        // DIFFICULTY-BASED QUESTIONS SETTER
    private List<Question> setQuestionList(Difficulty difficulty) {
        return switch (difficulty) {
            case GGEZ -> ggezOperatorsQuestions;
            case NORMAL -> normalOperatorsQuestions;
            case LUNATIC -> lunaticOperatorsQuestions;
        };
    }


        // QUESTION SEQUENCE SETTER
    private List<Question> setSequence(List<Question> questions) {
        if(questions == null) throw new NullPointerException("Failed to get lists of question, null value.");

        List<Question> questionSequence = new ArrayList<>(questions);
        Collections.shuffle(questionSequence);
        return questionSequence;
    }

        // START SESSION
    public void startSession() {
         ui.showWelcome();
        Player player = new Player(ui.askName(), 10, 0);
        Difficulty difficulty = ui.askDifficulty();
        List<Question> questions = setSequence(setQuestionList(difficulty));
        GameSession gameSession = new GameSession(player, difficulty, questions);

        int index = 1;
        for (Question question : questions) {
            gameSession.setCurrentQuestionIndex(index);

            String userInput = ui.askQuestion(question, index, questions.size(), player);

            if (questionService.isCorrect(question, userInput)) {
                ui.showCorrect(question);
            } else {
                ui.showWrong(question, player);
            }
            index++;
        }
        ui.showGoodbye(player);
    }
    
}
