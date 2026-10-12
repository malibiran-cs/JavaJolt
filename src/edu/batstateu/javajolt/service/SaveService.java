package edu.batstateu.javajolt.service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.entity.SaveData;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.exception.SaveFileException;

public class SaveService {
    private static final Path SAVE_PATH = Path.of("Javajolt", "saves", "player.properties");
    // PROPERTIES IN THE PLAYER PROPERTIES FILE
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
        throw new UnsupportedOperationException("TODO: save");
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
        throw new UnsupportedOperationException("TODO: ensureSaveDirectory");
    }

    private String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing key: " + key);
        }
        return value;
    }
}
