package edu.batstateu.javajolt;

import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.game.GameService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.ui.GameUI;
import java.util.NoSuchElementException;

public class GameApplication {
    public static void main(String[] args) {
        QuestionBank questionBank = new QuestionBank();

        QuestionService questionService =
                new QuestionService(questionBank);

        GameUI ui = new GameUI();

        GameService gameService =
                new GameService(questionService, ui);

        try {
            gameService.run();
        } catch (NoSuchElementException e) {

            System.out.println("\nInput closed. Thank you!");
        }
    }
}
