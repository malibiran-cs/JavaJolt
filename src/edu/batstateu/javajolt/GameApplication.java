package edu.batstateu.javajolt;

import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.game.GameService;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.ui.GameUI;
import java.util.NoSuchElementException;

public class GameApplication { //Angelo
    public static void main(String[] args) {
         try {
            QuestionBank questionBank = new QuestionBank();
            QuestionService questionService = new QuestionService(questionBank);
            LessonService lessonService = new LessonService();
            GameUI ui = new GameUI();
            LessonRunnerService lessonRunnerService =
                    new LessonRunnerService(questionService, lessonService, ui);
            SaveService saveService = new SaveService();
            GameService gameService =
                    new GameService(questionService, lessonService, lessonRunnerService, saveService, ui);

            gameService.run();
        } catch (NoSuchElementException e) {
                System.out.println("\nInput closed. Thank you!");
        } catch (SaveFileException e) {
                System.out.println("\n" + e.getMessage());
        } catch (IllegalStateException e) {
                System.out.println("\nThe question data could not be loaded: " + e.getMessage());
        }
    }
    
}
