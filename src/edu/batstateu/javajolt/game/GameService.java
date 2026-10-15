package edu.batstateu.javajolt.game;
import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.LessonFileException;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.ui.GameUI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GameService {
    // NOTE: Add a LessonRunnerService, it may or may not change the other current attributes/dependencies
    private final QuestionService questionService;
    private final LessonService lessonService;
    private final GameUI ui;

    public GameService(QuestionService questionService, LessonService lessonService, GameUI ui) {
        this.questionService = questionService;
        this.lessonService = lessonService;
        this.ui = ui;
    }

    /*
        The sole purpose of this method is to ORCHESTRATE, it simply should call logic from other methods
        
        NOTE: For future implementations, if any code here is written that seems to stray away from the main
              function of run, which is just to orchestrate, that's a sign it should either live in its own
              method or even in another class (this depends on the class at that point, which is the GameService)

        REMINDER: Always look at the NAME or the intended PURPOSE of a method (extends to classes too), check code
                  inside the method and ask: does this match or adhere to the NAME/PURPOSE?
    */
    public void run() {
        ui.showWelcome();

        /*
            This will be highly related to the following comments regarding the firstLesson logic,
            The current implementation of player is incorrect as it creates a new player every run,
            In summary for this and the firstLogic, RELY on the EXISTING data persistence logic
        */
        Player player = new Player(ui.askName(), STARTING_HP, 0);

        /*
            Ideally, after player inputs their name, that would serve as their identifier on which data to LOAD,
            This presents another problem which is that a duplicate name checker might be required as player enters a name
        */

        Set<Topic> completedBeginnerTopics = EnumSet.noneOf(Topic.class);
        Difficulty difficulty = ui.askDifficulty();
        Topic selectedTopic = null;

        /*
            NOTE: This variable may be a dispensable variable, it seems to serve no logic,
                It seems to be a mockup for data persistence, which wrongly lives inside the run() method,
                There is a dedicated data persistence logic already somewhere in the program so this is not needed
        */
        boolean firstLesson = true;

        // RECOMMENDATION: For the variables that have been noted, the LOAD operation could be used for better design

        boolean playing = true;

        while (playing) {
            /*
                IF playing, GET all available topics (the rule is that topic must be either the first, or completed topics)
                Currently, the logic only handles BEGINNER difficulty for all topics...

                RECOMMENDATION: Update the logic to obtain availableTopics instead from the method getAvailableTopics (from PlayerProgress)
            */
            List<Topic> availableTopics;
            if (difficulty == Difficulty.BEGINNER) {
                availableTopics = getAvailableBeginnerTopics(completedBeginnerTopics);
            } else {
                availableTopics = List.of(Topic.values());
            }

            Topic topic;
            if (selectedTopic != null) {
                // Play the topic selected for replay.
                topic = selectedTopic;
                selectedTopic = null;

                // NOTE: Same concern as with the firstLesson variable logic, may not be needed

            } else if (firstLesson) {
                /*
                    This is the behavior currently controlled by firstLesson logic, can be taken out,
                    
                    RECOMMENDATION: Rely on the existing data persistence logic, also it does not make sense to only ask topics if its the player's FIRST LESSON,
                                    the program should ALWAYS ask topics upon every new GAME SESSION, and when player chooses to REPLAY a topic MID SESSION,
                                    the LOAD 
                */
                topic = ui.askTopic(availableTopics);


                /*
                    Currently, only the beginner case is handled, but other difficulties can be implemented later,
                    Notice the design, a different logic is not really required as this could be reused for all difficulties
                */
            } else if (difficulty == Difficulty.BEGINNER) {
                // Continue automatically to the next unlocked topic.
                if (completedBeginnerTopics.size() == Topic.values().length) {
                    ui.showMessage("All Beginner topics are completed! "
                            + "You can replay a lesson or exit.");
                    int action = ui.askNextAction();
                    switch (action) {
                        case 3 -> playing = false;

                        // Redundant method, can use the same askTopic() method for simplicity
                        case 2 -> selectedTopic = ui.askReplayTopic(new ArrayList<>(completedBeginnerTopics));
                        default -> { }
                    }
                    continue;
                }
                topic = availableTopics.get(availableTopics.size() - 1);
            } else {
                // Note: Placeholder for unhandled difficulties, in reality all difficulty should have this
                topic = ui.askTopic(availableTopics);
            }
            firstLesson = false;

            /*
                True means the player chose to study, options to move from the textbook UI can be implemented later
                (some logic regarding switching ui likely already exists and can be reused)
                False means quiz starts
            */
            while (ui.askPlayOrStudy(topic)) {
                study(topic, difficulty);
            }


            /*
                Another logic that should not live inside run(), can be put in LessonRunnerService as it fits well 
            */
            try {
                List<Question> questions = questionService.selectQuestions(topic, difficulty);
                GameSession session = new GameSession(player, topic, difficulty, questions);
                playLesson(session);

                boolean passed = hasPassed(session);
                ui.showResult(session, passed);
                if (difficulty == Difficulty.BEGINNER && passed) {
                    boolean newlyCompleted = completedBeginnerTopics.add(topic);
                    if (newlyCompleted) {
                        ui.showMessage("Topic completed!");
                    }
                    if (completedBeginnerTopics.size() == Topic.values().length) {
                        ui.showMessage("Congratulations! You completed "
                                + "all Beginner topics!");
                    } else {
                        List<Topic> updatedTopics = getAvailableBeginnerTopics(completedBeginnerTopics);
                        Topic nextTopic = updatedTopics.get(updatedTopics.size() - 1);
                        ui.showMessage("Next topic unlocked: " + nextTopic.getLabel());
                    }
                }
            } catch (Exception e) {
                ui.showMessage(e.getMessage() != null
                        ? e.getMessage()
                        : "An unexpected error occurred.");
            }

            /*
                This can be put into a separate method, it should not live inside the run()
                Since it's just logic for checking player action, it can still be inside GameService
            */
            int action = ui.askNextAction();
            switch (action) {
                case 3 -> {
                    // Exit the game.
                    playing = false;
                }
                case 2 -> {
                    // Replay a completed topic.
                    List<Topic> replayableTopics = new ArrayList<>();
                    if (difficulty == Difficulty.BEGINNER) {
                        replayableTopics.addAll(getCompletedTopicsInOrder(completedBeginnerTopics));
                    } else {
                        replayableTopics.addAll(List.of(Topic.values()));
                    }
                    if (replayableTopics.isEmpty()) {
                        ui.showMessage("No completed Beginner lessons available "
                                + "to replay yet.");
                    } else {
                        selectedTopic = ui.askReplayTopic(replayableTopics);
                    }
                }
                default -> {
                    // Continue: the next loop selects the next unlocked
                    // Beginner topic automatically.
                    selectedTopic = null;
                }
            }
        }
        ui.showGoodbye(player);
    }

    // NOTE: Rely on the existing logic to get the available and completed topics

    // /**
    //  * Returns completed Beginner topics and the first incomplete topic.
    //  * This allows completed topics to remain replayable while preventing
    //  * future topics from being unlocked prematurely.
    //  */
    // private List<Topic> getAvailableBeginnerTopics(Set<Topic> completedTopics) {
    //     List<Topic> availableTopics = new ArrayList<>();
    //     for (Topic topic : Topic.values()) {
    //         if (completedTopics.contains(topic)) {
    //             availableTopics.add(topic);
    //         } else {
    //             availableTopics.add(topic);
    //             break;
    //         }
    //     }
    //     return availableTopics;
    // }

    // /**
    //  * Returns completed topics in the original enum order.
    //  */
    // private List<Topic> getCompletedTopicsInOrder(Set<Topic> completedTopics) {
    //     List<Topic> orderedTopics = new ArrayList<>();
    //     for (Topic topic : Topic.values()) {
    //         if (completedTopics.contains(topic)) {
    //             orderedTopics.add(topic);
    //         }
    //     }
    //     return orderedTopics;
    // }

    

    

    
}
