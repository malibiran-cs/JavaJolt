
package edu.batstateu.javajolt.game;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.ui.GameUI;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GameService {

    private static final int STARTING_HP = 10;
    private static final int DAMAGE_PER_MISTAKE = 3;
    private static final int POINTS_PER_CORRECT = 10;
    private static final int PASS_PERCENT = 70;

    private final QuestionService questionService;
    private final GameUI ui;

    public GameService(QuestionService questionService, GameUI ui) {
        this.questionService = questionService;
        this.ui = ui;
    }

    public void run() {
        ui.showWelcome();

        Player player = new Player(
                ui.askName(),
                STARTING_HP,
                0
        );

        Set<Topic> completedBeginnerTopics =
                EnumSet.noneOf(Topic.class);

        Difficulty difficulty = ui.askDifficulty();

        Topic selectedTopic = null;
        boolean firstLesson = true;
        boolean playing = true;

        while (playing) {

            List<Topic> availableTopics;

            if (difficulty == Difficulty.BEGINNER) {
                availableTopics =
                        getAvailableBeginnerTopics(
                                completedBeginnerTopics
                        );
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
                if (completedBeginnerTopics.size()
                        == Topic.values().length) {

                    ui.showMessage(
                            "All Beginner topics are completed! "
                                    + "You can replay a lesson or exit."
                    );

                    int action = ui.askNextAction();

                    if (action == 3) {
                        playing = false;
                    } else if (action == 2) {
                        selectedTopic = ui.askReplayTopic(
                                new ArrayList<>(completedBeginnerTopics)
                        );
                    }

                    continue;
                }

                topic = availableTopics.get(
                        availableTopics.size() - 1
                );

            } else {
                // For other difficulty levels, let the player choose.
                topic = ui.askTopic(availableTopics);
            }

            firstLesson = false;

            try {
                List<Question> questions =
                        questionService.selectQuestions(
                                topic,
                                difficulty
                        );

                GameSession session = new GameSession(
                        player,
                        topic,
                        difficulty,
                        questions
                );

                playLesson(session);

                boolean passed = hasPassed(session);

                ui.showResult(session, passed);

                if (difficulty == Difficulty.BEGINNER && passed) {

                    boolean newlyCompleted =
                            completedBeginnerTopics.add(topic);

                    if (newlyCompleted) {
                        ui.showMessage("Topic completed!");
                    }

                    if (completedBeginnerTopics.size()
                            == Topic.values().length) {

                        ui.showMessage(
                                "Congratulations! You completed "
                                        + "all Beginner topics!"
                        );

                    } else {
                        
                        List<Topic> updatedTopics =
                                getAvailableBeginnerTopics(completedBeginnerTopics);

                        Topic nextTopic = updatedTopics.get(
                                updatedTopics.size() - 1
                        );

                        ui.showMessage(
                                "Next topic unlocked: " + nextTopic.getLabel()
                        );
                    }
                }

            } catch (Exception e) {
                ui.showMessage(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "An unexpected error occurred."
                );
            }

            int action = ui.askNextAction();

            if (action == 3) {
                // Exit the game.
                playing = false;

            } else if (action == 2) {
                // Replay a completed topic.
                List<Topic> replayableTopics = new ArrayList<>();

                if (difficulty == Difficulty.BEGINNER) {
                    replayableTopics.addAll(
                            getCompletedTopicsInOrder(
                                    completedBeginnerTopics
                            )
                    );
                } else {
                    replayableTopics.addAll(
                            List.of(Topic.values())
                    );
                }

                if (replayableTopics.isEmpty()) {
                    ui.showMessage(
                            "No completed Beginner lessons available "
                                    + "to replay yet."
                    );
                } else {
                    selectedTopic =
                            ui.askReplayTopic(replayableTopics);
                }

            } else {
                // Continue: the next loop selects the next unlocked
                // Beginner topic automatically.
                selectedTopic = null;
            }
        }

        ui.showGoodbye(player);
    }

    /**
     * Returns completed Beginner topics and the first incomplete topic.
     * This allows completed topics to remain replayable while preventing
     * future topics from being unlocked prematurely.
     */
    private List<Topic> getAvailableBeginnerTopics(
            Set<Topic> completedTopics) {

        List<Topic> availableTopics = new ArrayList<>();

        for (Topic topic : Topic.values()) {

            if (completedTopics.contains(topic)) {
                availableTopics.add(topic);

            } else {
                availableTopics.add(topic);
                break;
            }
        }

        return availableTopics;
    }

    /**
     * Returns completed topics in the original enum order.
     */
    private List<Topic> getCompletedTopicsInOrder(
            Set<Topic> completedTopics) {

        List<Topic> orderedTopics = new ArrayList<>();

        for (Topic topic : Topic.values()) {
            if (completedTopics.contains(topic)) {
                orderedTopics.add(topic);
            }
        }

        return orderedTopics;
    }

    private void playLesson(GameSession session) {

        Player player = session.getPlayer();
        List<Question> questions = session.getQuestions();

        // Restore HP at the beginning of each lesson.
        player.setHp(STARTING_HP);

        for (int i = 0;
             i < questions.size() && player.isAlive();
             i++) {

            Question question = questions.get(i);

            session.setCurrentQuestionIndex(i + 1);

            String answer = ui.askQuestion(
                    question,
                    i + 1,
                    questions.size(),
                    player
            );

            if (questionService.isCorrect(question, answer)) {

                player.addScore(POINTS_PER_CORRECT);

                session.recordCorrect(POINTS_PER_CORRECT);

                ui.showCorrect(question);

            } else {

                player.takeDamage(DAMAGE_PER_MISTAKE);

                session.recordMistake();

                ui.showWrong(question, player);
            }
        }
    }

    private boolean hasPassed(GameSession session) {

        return session.getPlayer().isAlive()
                && session.getAccuracyPercent() >= PASS_PERCENT;
    }   
}