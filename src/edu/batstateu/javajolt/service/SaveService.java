package edu.batstateu.javajolt.service;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.exception.SaveFileException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SaveService {
    private static final Path SAVE_PATH = Path.of("Javajolt", "saves", "player.properties");
    // Player Properties
    private static final String KEY_NAME = "name";
    private static final String KEY_SCORE = "score";
    private static final String KEY_DIFFICULTY = "difficulty";

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
        props.setProperty(KEY_SCORE, String.valueOf(player.getScore()));
        props.setProperty(KEY_DIFFICULTY, difficulty.name());

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
            int score = Integer.parseInt(require(props, KEY_SCORE));
            Difficulty difficulty = Difficulty.valueOf(require(props, KEY_DIFFICULTY));
            return new SaveData(name, score, difficulty);
        } catch (IllegalArgumentException e) {
            throw new SaveFileException("Save file is corrupted: " + saveFile, e);
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
