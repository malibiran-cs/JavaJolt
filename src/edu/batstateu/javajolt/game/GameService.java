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
    

    private final QuestionService questionService;
    private final LessonService lessonService;
    private final GameUI ui;

    public GameService(QuestionService questionService, LessonService lessonService, GameUI ui) {
        this.questionService = questionService;
        this.lessonService = lessonService;
        this.ui = ui;
    }

    public void run() {
        ui.showWelcome();
        Player player = new Player(ui.askName(), STARTING_HP, 0);
        Set<Topic> completedBeginnerTopics = EnumSet.noneOf(Topic.class);
        Difficulty difficulty = ui.askDifficulty();
        Topic selectedTopic = null;
        boolean firstLesson = true;
        boolean playing = true;

        while (playing) {
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
            } else if (firstLesson) {
                // Let the player choose from the currently available topics.
                topic = ui.askTopic(availableTopics);
            } else if (difficulty == Difficulty.BEGINNER) {
                // Continue automatically to the next unlocked topic.
                if (completedBeginnerTopics.size() == Topic.values().length) {
                    ui.showMessage("All Beginner topics are completed! "
                            + "You can replay a lesson or exit.");
                    int action = ui.askNextAction();
                    switch (action) {
                        case 3 -> playing = false;
                        case 2 -> selectedTopic = ui.askReplayTopic(new ArrayList<>(completedBeginnerTopics));
                        default -> { }
                    }
                    continue;
                }
                topic = availableTopics.get(availableTopics.size() - 1);
            } else {
                // For other difficulty levels, let the player choose.
                topic = ui.askTopic(availableTopics);
            }
            firstLesson = false;

            while (ui.askWantsToStudy(topic)) {
                study(topic, difficulty);
            }

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
