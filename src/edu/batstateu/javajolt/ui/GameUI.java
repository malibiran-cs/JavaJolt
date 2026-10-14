package edu.batstateu.javajolt.ui;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.InvalidInputException;
import edu.batstateu.javajolt.exception.InvalidNameException;
import edu.batstateu.javajolt.game.GameSession;
import java.util.List;
import java.util.Scanner;

// Everything the player sees and types. No game rules live here.
public class GameUI {
    private static final String LINE = "==========================================";
    private static final String THIN = "------------------------------------------";
    private static final String NEW_LINE = System.lineSeparator();

    private final Scanner scanner = new Scanner(System.in);

    // Screens
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println("            J A V A   J O L T");
        System.out.println("   Learn Java, one level at a time.");
        System.out.println(LINE);
        System.out.println();
    }

    public void showMessage(String message) {
        System.out.println(NEW_LINE + message + NEW_LINE);
    }

    public void showGoodbye(Player player) {
        System.out.println(NEW_LINE + LINE);
        System.out.println(" Thanks for playing, " + player.getName() + "!");
        System.out.println(" Total XP: " + player.getScore());
        System.out.println(LINE);
    }

    // Questions
    // Shows the question and returns whatever the player typed
    public String askQuestion(Question question, int number, int total, Player player) {
        System.out.println(NEW_LINE + THIN);
        System.out.println(" Question " + number + " of " + total
                + "     HP: " + player.getHp() + "     XP: " + player.getScore());
        System.out.println(THIN);
        System.out.print(question.display());
        return switch (question.getInputMode()) {
            case SINGLE_LINE -> readLine();
            case MULTI_LINE -> readMultiLine();
        };
    }

    public void showCorrect(Question question) {
        System.out.println(NEW_LINE + " CORRECT!");
        System.out.println(" " + question.getExplanation());
    }

    public void showWrong(Question question, Player player) {
        System.out.println(NEW_LINE + " NOT QUITE.");
        System.out.println(" " + question.getExplanation());
        if (player.isAlive()) {
            System.out.println(" HP left: " + player.getHp());
        } else {
            System.out.println(" You're out of HP!");
        }
    }

    public void showResult(GameSession session, boolean passed) {
        System.out.println(NEW_LINE + LINE);
        System.out.println(" LESSON " + (passed ? "COMPLETE!" : "OVER"));
        System.out.println(LINE);
        System.out.println(" Topic:      " + session.getTopic().getLabel()
                + " (" + session.getDifficulty().getLabel() + ")");
        System.out.println(" Correct:    " + session.getCorrectCount() + " / " + session.getQuestions().size());
        System.out.println(" Mistakes:   " + session.getMistakeCount());
        System.out.println(" Accuracy:   " + session.getAccuracyPercent() + "%");
        System.out.println(" XP earned:  " + session.getXpEarned());
        System.out.println(" Result:     " + (passed ? "PASSED" : "NOT PASSED - try this lesson again!"));
        if (!session.getPlayer().isAlive()) {
            System.out.println(" You ran out of HP before finishing.");
        }
        System.out.println(LINE);
    }

    public boolean askWantsToStudy(Topic topic) {
        System.out.println("\n" + topic.getLabel() + ":");
        System.out.println(" [1] Play now");
        System.out.println(" [2] Study first");
        return askChoice("> ", 1, 2) == 2;
    }

    public void showLessonPages(List<LessonPage> pages) {
        for (int i = 0; i < pages.size(); i++) {
            LessonPage page = pages.get(i);
            System.out.println("\n" + LINE);
            System.out.println(" " + page.title() + "  (page " + (i + 1) + " of " + pages.size() + ")");
            System.out.println(LINE);
            page.body().forEach(System.out::println);

            if (i < pages.size() - 1) {
                System.out.print("\nPress ENTER for the next page (or type Q to stop reading): ");
                if (readLine().trim().equalsIgnoreCase("q")) {
                    return;
                }
            }
        }
        System.out.println("\nThat's the end of the textbook.");
    }

    // Menus
    public String askName() {
        while (true) {
            System.out.print("Enter your name: ");
            String name = readLine().trim();

            try {
                if (name.isEmpty()) {
                    throw new InvalidNameException("Name cannot be empty. Please type your name.");
                }
                return name;
            } catch (InvalidNameException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public Difficulty askDifficulty() {
        Difficulty[] levels = Difficulty.values();

        System.out.println("\nChoose your level:");
        for (int i = 0; i < levels.length; i++) {
            System.out.println(" [" + (i + 1) + "] " + levels[i].getLabel());
        }
        return levels[askChoice("> ", 1, levels.length) - 1];
    }

    
public Topic askTopic(List<Topic> availableTopics) {
    System.out.println("\nChoose a topic:");

    for (int i = 0; i < availableTopics.size(); i++) {
        System.out.println(" [" + (i + 1) + "] "
                + availableTopics.get(i).getLabel());
    }

    int choice = askChoice("> ", 1, availableTopics.size());
    return availableTopics.get(choice - 1);
}

    
public int askNextAction() {
    System.out.println("\nWhat would you like to do?");
    System.out.println(" [1] Continue to another lesson");
    System.out.println(" [2] Replay a lesson");
    System.out.println(" [3] Exit");

    return askChoice("> ", 1, 3);
}

public Topic askReplayTopic(List<Topic> completedTopics) {
    System.out.println("\nChoose a completed topic to replay:");

    for (int i = 0; i < completedTopics.size(); i++) {
        System.out.println(" [" + (i + 1) + "] "
                + completedTopics.get(i).getLabel());
    }

    int choice = askChoice("> ", 1, completedTopics.size());
    return completedTopics.get(choice - 1);
}

    // Input Helpers
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

    // Input Readers
    private String readLine() {
        return scanner.nextLine();
    }

    // Reads lines until the player types END (any capitalization)
    private String readMultiLine() {
        StringBuilder source = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("END")) {
                break;
            }
            source.append(line).append(NEW_LINE);
        }
        return source.toString();
    }
}
