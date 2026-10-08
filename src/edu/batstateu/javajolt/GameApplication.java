package edu.batstateu.javajolt;

import edu.batstateu.javajolt.game.GameService;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.service.QuestionService;

public class GameApplication {
    public static void main(String[] args) {
        QuestionBank questionBank = new QuestionBank();

        QuestionService questionService =
                new QuestionService(questionBank);

        GameService gameService =
                new GameService(questionService);

        gameService.startSession();
    }
}
