package edu.batstateu.javajolt.preview;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.exception.SaveFileException;
import edu.batstateu.javajolt.service.SaveService;

/*
 * Throwaway save location for preview launchers (save-isolation rule).
 * A preview window behaves like a normally loaded game, but every write
 * goes to a temp directory that is deleted on exit. The real saves/ folder
 * is never read or written by a preview launcher.
 */
public final class PreviewSandbox {

    private PreviewSandbox() {
    }

    // Creates the sandbox and writes the seed save once, so MainWindow treats
    // the preview like a normal loaded game (no "could not save" warnings).
    public static SaveService createFor(Player player, Difficulty difficulty) {
        Path saveFile;
        try {
            Path tempDir = Files.createTempDirectory("javajolt-preview");
            saveFile = tempDir.resolve("player.properties");
        } catch (IOException e) {
            throw new IllegalStateException("Could not create preview sandbox: " + e.getMessage(), e);
        }

        // The sandbox file is the only thing to delete; the temp dir itself is left empty.
        saveFile.toFile().deleteOnExit();

        SaveService saveService = new SaveService(saveFile);
        player.setTutorialCompleted(true);
        try {
            saveService.save(player, difficulty);
        } catch (SaveFileException e) {
            throw new IllegalStateException("Could not seed preview sandbox save: " + e.getMessage(), e);
        }
        return saveService;
    }
}
