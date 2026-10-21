package edu.batstateu.javajolt.game;
import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.LessonFileException;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.service.SaveService;
import edu.batstateu.javajolt.ui.GameUI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameService {
    // NOTE: Add a LessonRunnerService, it may or may not change the other current attributes/dependencies
    private final QuestionService questionService;
    private final LessonService lessonService;
    private final LessonRunnerService lessonRunnerService;
    private final SaveService saveService;
    private final GameUI ui;

    public GameService(QuestionService questionService, LessonService lessonService, 
                LessonRunnerService lessonRunnerService, SaveService saveService, GameUI ui) {
        this.questionService = questionService;
        this.lessonService = lessonService;
        this.lessonRunnerService = lessonRunnerService;
        this.saveService = saveService;
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
    public void run() throws SaveFileException {
        ui.showWelcome();
        
        // Load data
        Player player;
        Difficulty difficulty;

        SaveData saveData = loadSave();
        if(saveData != null) {
            player = new Player(saveData.name(), saveData.xp());

            // Get completed topics
            Map<Topic, Set<Difficulty>> completedTopics = saveData.completedTopics();
            player.getProgress().restore(completedTopics);

            difficulty = saveData.difficulty();
        }
        else {
            player = new Player(ui.askName(), 0);
            difficulty = Difficulty.BEGINNER;
        }

        /*
            Ideally, after player inputs their name, that would serve as their identifier on which data to LOAD,
            This presents another problem which is that a duplicate name checker might be required as player enters a name

            Notice the use of player.getProgress(), soon if multiple save data is desired, objects such as savedata
            may also require to exist in each instance of player, like PlayerProgress

            NOTE: THIS IS A POSSIBLE MAJOR IMPLEMENTATION IN THE FUTURE, DIFFERENT SAVE FILES FOR EACH NAME
        */

        // Difficulty checker
        difficulty = checkDifficulty(player, difficulty);

        Topic selectedTopic = null;

        // Flags
        boolean playing = true;

        while (playing) {
            // Flags (might be useful, can add more,)
            /* 
                E.g. The isLessonComplete (meaning if all questions for a specific topic and difficulty)
                     can be used to add more XP than XP gained per question, or check next player action!
            */
            boolean isLessonComplete = false;

            // Ask topic from available topics (first, or completed topics)
            List<Topic> availableTopics = player.getProgress().getAvailableTopics(difficulty);
            Topic topic;
            if (selectedTopic != null) {
                topic = selectedTopic;
                selectedTopic = null;
            } else {
                topic = ui.askTopic(availableTopics);
            }

            /*
                True means the player chose to study, options to move from the textbook UI can be implemented later
                (some logic regarding switching ui likely already exists and can be reused)
                False means quiz starts
            */
            // Study logic (revise for when textbook implementation is robust)
            while (ui.askPlayOrStudy(topic)) {
                lessonRunnerService.study(topic, difficulty);
            }
        
            // Load questions based on chosen topic and currently saved difficulty, create new session
            List<Question> questions = questionService.selectQuestions(topic, difficulty);
            GameSession session = new GameSession(player, topic, difficulty, questions);

            // Question loop for a lesson
            lessonRunnerService.playLesson(session);

            // Show results
            boolean passed = lessonRunnerService.hasPassed(session);
            ui.showResult(session, passed);
            
            // Add topic to completed topics in current difficulty and check progress
            if(passed) {
                player.getProgress().complete(topic, difficulty);
                isLessonComplete = true;
                if(player.getProgress().isDifficultyComplete(difficulty)) {
                    ui.showMessage("Congratulations! You completed all topics for " +
                        difficulty.getLabel() + " difficulty!"
                    );
                }
                else {
                    List<Topic> updatedTopics = player.getProgress().getAvailableTopics(difficulty);
                    Topic nextTopic = updatedTopics.get(updatedTopics.size() - 1);
                    ui.showMessage("Next topic unlocked: " + nextTopic.getLabel());
                }
            }

            // Bonus XP once the whole lesson is cleared
            if(isLessonComplete) {
                player.addXp(lessonRunnerService.lessonCompleteBonus(difficulty));
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
                    for (Topic replayableTopic : Topic.values()) {
                        if (player.getProgress().isComplete(replayableTopic, difficulty)) {
                            replayableTopics.add(replayableTopic);
                        }
                    }
                    if (replayableTopics.isEmpty()) {
                        ui.showMessage("No completed lessons available to replay yet.");
                    } else {
                        selectedTopic = ui.askTopic(replayableTopics);
                    }
                }
                default -> {
                    selectedTopic = null;
                }
            }

            // Finally, save data for next play
            saveService.save(player, difficulty);
        }
        ui.showGoodbye(player);
    }

    // Loads the save when one exists. A missing, empty, or corrupt save starts a new game instead of crashing.
    private SaveData loadSave() {
        if(!saveService.hasSave()) {
            return null;
        }
        try {
            return saveService.load();
        } catch (SaveFileException e) {
            ui.showMessage("Could not read your save file. Starting a new game.");
            return null;
        }
    }

    // Difficulty checker method
    private Difficulty checkDifficulty(Player player, Difficulty difficulty) {
    // A null difficulty means a brand new player
        if(difficulty == null) difficulty = Difficulty.BEGINNER;
        // Subsequent checks to progess -> INTERMEDIATE -> ADVANCED
        if(player.getProgress().isDifficultyComplete(difficulty)) difficulty = Difficulty.INTERMEDIATE;
        if(player.getProgress().isDifficultyComplete(difficulty)) difficulty = Difficulty.ADVANCED;
        // ENDGAME ?...
        if(player.getProgress().isDifficultyComplete(Difficulty.BEGINNER) &&
            player.getProgress().isDifficultyComplete(Difficulty.INTERMEDIATE) &&
            player.getProgress().isDifficultyComplete(Difficulty.ADVANCED)) 
            difficulty = ui.askDifficulty();
        return difficulty;
    }
}
