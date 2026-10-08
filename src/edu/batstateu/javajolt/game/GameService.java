package edu.batstateu.javajolt.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

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
        

    private final Scanner input = new Scanner(System.in);
    // SERVICE METHODS
        // INITIALIZE PLAYER
    private Player createPlayer() {
        System.out.println("Enter your name: ");
        String name = input.nextLine();

        return new Player(name, 10, 0);
    }
    
        // DETERMINE USER LEVEL
    private Difficulty determineUserLevel() {
        System.out.println("Which level of proficiency do you currently have in Java? (1-3)");
        System.out.println("1. GGEZ     2. NORMAL    3. LUNATIC\n");
        int level = input.nextInt();
        input.nextLine();
        
        return switch (level) {
            case 1 -> Difficulty.GGEZ;
            case 2 -> Difficulty.NORMAL;
            case 3 -> Difficulty.LUNATIC;
            default -> throw new IllegalArgumentException(
                    "Invalid input was entered, please try again.");
        };
    }
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
        Player player = createPlayer();
        Difficulty difficulty = determineUserLevel();
        List<Question> questions = setSequence(setQuestionList(difficulty));
        GameSession gameSession = new GameSession(player, difficulty, questions);

        System.out.println("=== JAVA QUIZ GAME ===\n");
        int index = 1;
    

        for(Question question: questions) {
            gameSession.setCurrentQuestionIndex(index);
            
            // Each question type formats its own prompt (no instanceof needed)
            System.out.print(question.display());
            String userInput = input.nextLine();
            boolean isCorrect = questionService.isCorrect(question, userInput);

            if(isCorrect) {
                System.out.println("\nCORRECT!\n");
            }
            else {
                System.out.println("\nBZZZZT!\n");
            }

            index++;
        }
    }
    
}
