package edu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import edu.entity.Player;

import edu.question.Question;
import edu.enums.Difficulty;
import edu.enums.Topic;
import edu.service.QuestionService;

public class GameService {
    private QuestionService questionService;

    private List<Question> variablesQuestions;
    private List<Question> operatorsQuestions;

    private List<Question> ggezVariablesQuestions;
    private List<Question> normalVariablesQuestions;
    private List<Question> lunaticVariablesQuestions;

    private List<Question> ggezOperatorsQuestions;
    private List<Question> normalOperatorsQuestions;
    private List<Question> lunaticOperatorsQuestions;
    // NOTE: EVENTUALLY MAKE A BIGGER CONTAINER THAT CONTAINS ALL TOPIC, DIVIDE BY DIFFICULTY

    public GameService(QuestionService questionService) {

        this.questionService = questionService;

        variablesQuestions =
                questionService.getQuestionsByTopic(Topic.VARIABLES);

        operatorsQuestions =
                questionService.getQuestionsByTopic(Topic.OPERATORS);

        ggezVariablesQuestions =
                questionService.getQuestionsByDifficulty(
                        variablesQuestions, Difficulty.GGEZ);

        normalVariablesQuestions =
                questionService.getQuestionsByDifficulty(
                        variablesQuestions, Difficulty.NORMAL);

        lunaticVariablesQuestions =
                questionService.getQuestionsByDifficulty(
                        variablesQuestions, Difficulty.LUNATIC);

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
        

    Scanner input = new Scanner(System.in);
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
        
        switch (level) {
            case 1:
                return Difficulty.GGEZ;
            case 2:
                return Difficulty.NORMAL;
                // NOTE: TYPE 3 QUESTIONS IN PROGRESS
            case 3:
                return Difficulty.LUNATIC;
            default:
                throw new IllegalArgumentException("Invalid input was entered, please try again.");
        }
    }
        // DIFFICULTY-BASED QUESTIONS SETTER
    private List<Question> setQuestionList(Difficulty difficulty) {
        List<Question> selectedQuestions = new ArrayList<>();

        switch (difficulty) {
            case GGEZ:
                selectedQuestions.addAll(ggezVariablesQuestions);
                selectedQuestions.addAll(ggezOperatorsQuestions);
                break;
            case NORMAL:
                selectedQuestions.addAll(normalVariablesQuestions);
                selectedQuestions.addAll(normalOperatorsQuestions);
                break;
            case LUNATIC:
                selectedQuestions.addAll(lunaticVariablesQuestions);
                selectedQuestions.addAll(lunaticOperatorsQuestions);
                break;
            default:
                throw new IllegalArgumentException("Invalid input was entered, please try again.");
        }

        return selectedQuestions;
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
