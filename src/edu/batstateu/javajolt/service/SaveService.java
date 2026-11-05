package edu.batstateu.javajolt.service;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.SaveFileException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SaveService {
    // Relative to the working directory (the project root when run from there).
    private static final Path SAVE_PATH = Path.of("saves", "player.properties");
    // Player Properties
    private static final String KEY_NAME = "name";
    private static final String KEY_XP = "xp";
    private static final String KEY_SCORE = "score";
    private static final String KEY_HIGH_SCORES = "highScores";
    private static final String KEY_DIFFICULTY = "difficulty";
    // Stored as comma-separated TOPIC:DIFFICULTY pairs, e.g. VARIABLES:BEGINNER,OPERATORS:BEGINNER.
    private static final String KEY_COMPLETED = "completedTopics";

    private final Path saveFile;
    
    public SaveService() {
        this(SAVE_PATH);
    }

    public SaveService(Path saveFile) {
        this.saveFile = saveFile;
    }
    
    // FILE EXISTS CHECK
    public Boolean hasSave() {
        return Files.exists(saveFile);
    }

    public void save(Player player, Difficulty difficulty) throws SaveFileException {
        ensureSaveDirectory();

        Properties props = new Properties();
        props.setProperty(KEY_NAME, player.getName());
        props.setProperty(KEY_XP, String.valueOf(player.getXp()));
        props.setProperty(KEY_SCORE, String.valueOf(player.getScore()));
        props.setProperty(KEY_HIGH_SCORES, encodeHighScores(player.copyHighScores()));
        props.setProperty(KEY_DIFFICULTY, difficulty.name());
        props.setProperty(KEY_COMPLETED, encode(player.getProgress().copyCompleted()));

        try (java.io.Writer writer = Files.newBufferedWriter(saveFile)) {
            props.store(writer, "JavaJolt player save");
        } catch (IOException e) {
            throw new SaveFileException("Could not write save file: " + saveFile, e);
        }
    }

    public SaveData load() throws SaveFileException {
        Properties props = new Properties();

        try (Reader reader = Files.newBufferedReader(saveFile)) {
            props.load(reader);
        } catch (IOException e) {
            throw new SaveFileException("Could not read save file: " + saveFile, e);
        }

        try {
            String name = require(props, KEY_NAME);
            int xp = Integer.parseInt(require(props, KEY_XP));
            int score = Integer.parseInt(props.getProperty(KEY_SCORE, "0"));
            Difficulty difficulty = Difficulty.valueOf(require(props, KEY_DIFFICULTY));
            // NOTE: default to "" so old saves without this key still load as empty progress.
            Map<Topic, Set<Difficulty>> completed = decode(props.getProperty(KEY_COMPLETED, ""));
            Map<Topic, Map<Difficulty, Integer>> highScores =
                    decodeHighScores(props.getProperty(KEY_HIGH_SCORES, ""));
            return new SaveData(name, xp, difficulty, score, completed, highScores);
        } catch (IllegalArgumentException e) {
            throw new SaveFileException("Save file is corrupted: " + saveFile, e);
        }
    }

    // Converts progress map to single-line property value.
    private static String encode(Map<Topic, EnumSet<Difficulty>> progress) {
        List<String> parts = new ArrayList<>();
        // NOTE: one entry per pair keeps parsing trivial (no nested delimiters).
        progress.forEach((topic, diffs) -> diffs.forEach(d -> parts.add(topic.name() + ":" + d.name())));
        return String.join(",", parts);
    }

    // Parses property value back to progress map; blank means nothing completed yet.
    private static Map<Topic, Set<Difficulty>> decode(String raw) throws SaveFileException {
        Map<Topic, Set<Difficulty>> out = new EnumMap<>(Topic.class);
        if (raw == null || raw.isBlank()) {
            return out;
        }
        try {
            for (String pair : raw.split(",")) {
                // NOTE: split on ":" then valueOf() validates both halves; bad names throw below.
                String[] kv = pair.trim().split(":");
                Topic topic = Topic.valueOf(kv[0]);
                Difficulty diff = Difficulty.valueOf(kv[1]);
                out.computeIfAbsent(topic, k -> EnumSet.noneOf(Difficulty.class)).add(diff);
            }
            return out;
        } catch (Exception e) {
            throw new SaveFileException("Save file is corrupted: " + KEY_COMPLETED, e);
        }
    }


    // Converts best scores to TOPIC:DIFFICULTY:SCORE entries.
    private static String encodeHighScores(
            Map<Topic, Map<Difficulty, Integer>> highScores) {
        List<String> parts = new ArrayList<>();
        highScores.forEach((topic, scores) ->
                scores.forEach((difficulty, score) ->
                        parts.add(topic.name() + ":" + difficulty.name() + ":" + score)));
        return String.join(",", parts);
    }

    // Parses saved high scores; a blank or missing key means no records yet.
    private static Map<Topic, Map<Difficulty, Integer>> decodeHighScores(String raw)
            throws SaveFileException {
        Map<Topic, Map<Difficulty, Integer>> out = new EnumMap<>(Topic.class);
        if (raw == null || raw.isBlank()) {
            return out;
        }

        try {
            for (String entry : raw.split(",")) {
                String[] parts = entry.trim().split(":", -1);
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid high-score entry: " + entry);
                }

                Topic topic = Topic.valueOf(parts[0]);
                Difficulty difficulty = Difficulty.valueOf(parts[1]);
                int score = Integer.parseInt(parts[2]);
                if (score < 0) {
                    throw new IllegalArgumentException("High score cannot be negative.");
                }

                out.computeIfAbsent(topic, key -> new EnumMap<>(Difficulty.class))
                        .merge(difficulty, score, Math::max);
            }
            return out;
        } catch (Exception e) {
            throw new SaveFileException("Save file is corrupted: " + KEY_HIGH_SCORES, e);
        }
    }

    private void ensureSaveDirectory() throws SaveFileException {
        Path parent = saveFile.getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new SaveFileException("Could not create save directory: " + parent, e);
        }
    }

    private String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing key: " + key);
        }
        return value;
    }
}
