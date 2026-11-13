package edu.batstateu.javajolt.service; //Angelo

import edu.batstateu.javajolt.entity.LessonPage;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.exception.LessonFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Loads a topic's textbook (data/lessons/<topic>.txt) and picks the pages that fit the player's level
public class LessonService {
    private static final Pattern PAGE_HEADER = Pattern.compile("^## PAGE (\\d+) \\[(\\w+)\\]: (.+)$");

    private final Path lessonFolder;

    public LessonService() {
        this(findLessonFolder());
    }

    public LessonService(Path lessonFolder) {
        this.lessonFolder = lessonFolder;
    }

    // Returns the pages to show, or an empty list if this topic has no textbook yet
    public List<LessonPage> loadPages(Topic topic, Difficulty difficulty) throws LessonFileException {
        Path file = lessonFolder.resolve(fileNameFor(topic));
        if (!Files.exists(file)) {
            return List.of();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new LessonFileException("Could not read the textbook file: " + file, e);
        }

        return parse(lines, file).stream()
                                 .filter(page -> isShownAt(page, difficulty))
                                 .toList();
    }

    private static Path findLessonFolder() {
        Path workingDirectory = Path.of("").toAbsolutePath().normalize();
        Path[] candidates = {
                workingDirectory.resolve("data").resolve("lessons"),
                workingDirectory.resolve("JavaJolt").resolve("data").resolve("lessons"),
                workingDirectory.resolve("JavaJolt-main").resolve("data").resolve("lessons")
        };

        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }

        return Path.of("data", "lessons");
    }

    private String fileNameFor(Topic topic) {
        return switch (topic) {
            case EXCEPTION_HANDLING -> "exception.txt";
            default -> topic.name().toLowerCase() + ".txt";
        };
    }

    // A page is shown if it is for every level, or for this level or an easier one
    private boolean isShownAt(LessonPage page, Difficulty difficulty) {
        return page.level() == null || page.level().ordinal() <= difficulty.ordinal();
    }

    private List<LessonPage> parse(List<String> lines, Path file) throws LessonFileException {
        List<LessonPage> pages = new ArrayList<>();
        int number = 0;
        String title = null;
        Difficulty level = null;
        List<String> body = new ArrayList<>();

        for (String line : lines) {
            Matcher header = PAGE_HEADER.matcher(line);
            if (header.matches()) {
                if (title != null) pages.add(buildPage(number, level, title, body));
                number = Integer.parseInt(header.group(1));
                level = parseLevel(header.group(2), number, file);
                title = header.group(3);
                body = new ArrayList<>();
            } else if (title != null) {
                body.add(line);
            }
            // lines before the first page header are ignored
        }
        if (title != null) pages.add(buildPage(number, level, title, body));
        return pages;
    }

    private LessonPage buildPage(int number, Difficulty level, String title, List<String> body) {
        List<String> trimmed = new ArrayList<>(body);
        while (!trimmed.isEmpty() && trimmed.get(0).isBlank()) trimmed.remove(0);
        while (!trimmed.isEmpty() && trimmed.get(trimmed.size() - 1).isBlank()) trimmed.remove(trimmed.size() - 1);
        return new LessonPage(number, level, title, trimmed);
    }

    private Difficulty parseLevel(String text, int pageNumber, Path file) throws LessonFileException {
        if (text.equals("ALL")) return null;
        try {
            return Difficulty.valueOf(text);
        } catch (IllegalArgumentException e) {
            throw new LessonFileException("Unknown level [" + text + "] on page " + pageNumber + " of " + file);
        }
    }
}