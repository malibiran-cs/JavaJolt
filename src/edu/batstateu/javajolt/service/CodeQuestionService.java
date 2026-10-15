package edu.batstateu.javajolt.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

// Judges a player's program: required concepts -> compile -> run -> compare output.
// NOTE: the player's code runs on this computer with no sandbox, so only use this for the player's own
// practice on their own machine (do not host it for strangers).
public class CodeQuestionService {
    private static final int TIMEOUT_SECONDS = 5;
    private static final int MAX_OUTPUT_CHARS = 64 * 1024;


    // Checks the player's code by validating concepts, compiling and running it, comparing outputs, and cleaning up temporary files.
    public boolean judge(String source, String expectedOutput, List<String> requiredConcepts) {

        // Returns false if the submitted code does not contain the required concepts.
        if (!hasRequiredConcepts(source, requiredConcepts)) return false;

        // Stores the path of the temporary directory used to compile and run the code.
        Path dir = null;

        try {
            // Creates a temporary directory to store files needed to run the submitted code.
            dir = Files.createTempDirectory("javajolt");

            // Returns false if the submitted code cannot be compiled successfully.
            if (!compile(source, dir)) return false;

            // Runs the compiled program and collects its output.
            String output = run(dir);

            // Returns true only if the output is not null and matches the expected output after normalization.
            return output != null
                    && normalize(output).equals(normalize(expectedOutput));

        } catch (IOException e) {

            // Displays an error message if a file operation or other input/output operation fails.
            System.err.println("[JavaJolt] Could not run your program: " + e.getMessage());
            return false;

        } catch (InterruptedException e) {

            // Restores the interrupted status if the program execution is interrupted.
            Thread.currentThread().interrupt();

            // Returns false because the program could not finish running normally.
            return false;

        } finally {

            // Deletes the temporary directory and its contents if the directory was created.
            if (dir != null) deleteRecursively(dir);
        }
    }


    // Looks for each concept in the real code only. Words inside comments and strings don't count,
    // so "// + %" or "\"+ %\"" can't be used to fake it.
    private boolean hasRequiredConcepts(String source, List<String> requiredConcepts) {
        String code = stripCommentsAndStrings(source);
        for (String concept : requiredConcepts) {
            if (!code.contains(concept)) return false;
        }
        return true;
    }

    // Replaces comments with a space and the inside of string/char literals with nothing
    private static String stripCommentsAndStrings(String src) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        int n = src.length();

        while (i < n) {
            char c = src.charAt(i);
            char next = (i + 1 < n) ? src.charAt(i + 1) : '\0';

            if (c == '/' && next == '/') {              // line comment
                while (i < n && src.charAt(i) != '\n') i++;
                out.append(' ');
            } else if (c == '/' && next == '*') {       // block comment
                int end = src.indexOf("*/", i + 2);
                i = (end < 0) ? n : end + 2;
                out.append(' ');
            } else if (c == '"' || c == '\'') {         // string or char literal
                char quote = c;
                i++;
                while (i < n && src.charAt(i) != quote) {
                    if (src.charAt(i) == '\\') i++;     // skip the escaped character
                    i++;
                }
                i++;                                    // the closing quote
                out.append(quote).append(quote);        // keep an empty literal so the code keeps its shape
            } else {
                out.append(c);
                i++;
            }
        }
        return out.toString();
    }

    private boolean compile(String source, Path dir) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Code questions need a JDK, not just a JRE.");
        }
        Path file = dir.resolve("Code.java");
        Files.writeString(file, source);
        return compiler.run(null, null, null, "-d", dir.toString(), file.toString()) == 0;
    }

    // Runs the compiled program with a time limit. Returns null if it took too long.
    private String run(Path dir) throws IOException, InterruptedException {
        // An empty file as input, so a program that waits for typing ends instead of hanging
        File noInput = Files.createFile(dir.resolve("input.txt")).toFile();

        Process process = new ProcessBuilder(javaCommand(), "-cp", dir.toString(), "Code")
                .redirectErrorStream(true)
                .redirectInput(noInput)
                .start();

        // Read the output while the program runs, keeping only the first part of it
        StringBuilder output = new StringBuilder();
        AtomicBoolean tooLong = new AtomicBoolean(false);
        Thread reader = new Thread(() -> {
            try (InputStreamReader in = new InputStreamReader(process.getInputStream())) {
                char[] buffer = new char[4096];
                int count;
                while ((count = in.read(buffer)) != -1) {
                    if (output.length() < MAX_OUTPUT_CHARS) {
                        output.append(buffer, 0, count);
                    } else {
                        tooLong.set(true); // keep draining so the program isn't blocked, but stop storing
                    }
                }
            } catch (IOException ignored) { }
        });
        reader.setDaemon(true);
        reader.start();

        if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            process.waitFor();
            return null;
        }
        reader.join();

        // A program that ended with an error (for example an exception) is not a correct answer,
        // and neither is one that printed far more than any expected output
        if (process.exitValue() != 0 || tooLong.get() || output.length() > MAX_OUTPUT_CHARS) {
            return null;
        }
        return output.toString();
    }

    // The same Java that is running JavaJolt, so it doesn't depend on what is on the PATH
    private String javaCommand() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private String normalize(String text) {
        return text.trim().replace("\r\n", "\n");
    }

    private void deleteRecursively(Path dir) {
        try (var paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder())
                 .forEach(p -> p.toFile().delete());
        } catch (IOException ignored) { }
    }
}
