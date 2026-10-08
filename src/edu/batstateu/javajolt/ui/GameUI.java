package edu.batstateu.javajolt.ui;

import java.util.Scanner;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.InvalidInputException;
import edu.batstateu.javajolt.game.GameSession;


public class GameUI {
    private static final String LINE = "==========================================";
    private static final String THIN = "------------------------------------------";

    private final Scanner scanner = new Scanner(System.in);

    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(" J A V A  J O L T");
        System.out.println(" Learn Java, one level at a time.");
        System.out.print(LINE);
        System.out.println();
    }

    public void showGoodbye(Player player) {
        System.out.println("\n" + LINE);
        System.out.println(" Thanks for playing, " + player.getName() + "!");
        System.out.println(" Total XP: " + player.getScore());
        System.out.println(LINE);
    }

    // - QUESTIONS -

    public String askQuestion(Question question, int number, int total, Player player) {
        System.out.println("\n" + THIN);
        System.out.println(" Question " + number + " of " + total
            + " HP: " + player.getHp() + " XP: " + player.getScore());
        System.out.println(THIN);
        System.out.println(question.display());
        return switch(question.getInputMode()) {
            case SINGLE_LINE -> readLine();
            case MULTI_LINE -> readMultiLine();
        };
    }

    public void showCorrect(Question question) {
        System.out.println("\n Correct !");
    }

    public void showWrong(Question question, Player player) {
        System.out.println("\n NOT QUITE.");
        if (player.isAlive()) {
            System.out.println(" HP left: " + player.getHp());
        } else {
            System.out.println(" You're out of HP!");
        }
    }

    public void showResult(GameSession session, boolean passed) {
        System.out.println("\n" + LINE);
        System.out.println(" LESSON " + (passed ? "COMPLETE!" : "OVER"));
        System.out.println(LINE);
        System.out.println(" Result:     " + (passed ? "PASSED" : "NOT PASSED - try this lesson again!"));
        if (!session.getPlayer().isAlive()) {
            System.out.println(" You ran out of HP before finishing.");
        }
        System.out.println(LINE);
    }
 
    // ---------- MENUS ----------
    public String askName() {
        while (true) {
            System.out.print("Enter your name: ");
            String name = readLine().trim();
            if (!name.isEmpty()) {
                return name;
            }
            System.out.println("Please type a name.");
        }
    }
 
    public Difficulty askDifficulty() {
        Difficulty[] levels = Difficulty.values();
 
        System.out.println("\nChoose your level:");
        for (int i = 0; i < levels.length; i++) {
            System.out.println(" [" + (i + 1) + "] " + levels[i]);
        }
        return levels[askChoice("> ", 1, levels.length) - 1];
    }
 
    public Topic askTopic() {
        Topic[] topics = Topic.values();
 
        System.out.println("\nChoose a topic:");
        for (int i = 0; i < topics.length; i++) {
            System.out.println(" [" + (i + 1) + "] " + topics[i]);
        }
        return topics[askChoice("> ", 1, topics.length) - 1];
    }
 
    public boolean askPlayAgain() {
        System.out.println("\nWhat next?");
        System.out.println(" [1] Play another lesson");
        System.out.println(" [2] Exit");
        return askChoice("> ", 1, 2) == 1;
    }
 
    // ---------- INPUT HELPERS ----------
    // Keeps asking until the player types a whole number between min and max
    private int askChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                return parseChoice(readLine(), min, max);
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }
 
    private int parseChoice(String text, int min, int max) throws InvalidInputException {
        int choice;
        try {
            choice = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Please type a number from " + min + " to " + max + ".");
        }
 
        if (choice < min || choice > max) {
            throw new InvalidInputException("Please choose a number from " + min + " to " + max + ".");
        }
        return choice;
    }
 
    // INPUT READERS

    private String readLine() {
        return scanner.nextLine();
    }

    private String readMultiLine() {
        StringBuilder source = new StringBuilder();
        while(true) {
            String line = scanner.nextLine();
            if(line.equals("END")) break;
            source.append(line).append("\n");
        }
        return source.toString();
    }
}
