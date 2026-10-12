package edu.batstateu.javajolt.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class CodeQuestionService {
    private static final int TIMEOUT_SECONDS = 5;

    // concepts -> compile -> run -> compare
    
public boolean judge(String source, String expectedOutput,
                     List<String> requiredConcepts) {
    if (!hasRequiredConcepts(source, requiredConcepts)) {
        return false;
    }

    Path dir = null;

    try {
        dir = Files.createTempDirectory("javajolt");

        if (!compile(source, dir)) {
            return false;
        }

        String output = run(dir);

        return output != null
                && normalize(output).equals(normalize(expectedOutput));

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;

    } catch (IOException e) {
        return false;

    } finally {
        if (dir != null) {
            deleteRecursively(dir);
        }
    }
}

    private boolean hasRequiredConcepts(String source, List<String> requiredConcepts) {
        for (String concept : requiredConcepts) {
            if (!source.contains(concept)) return false;
        }
        return true;
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

   
private String run(Path dir) throws IOException, InterruptedException {
    Path outputFile = dir.resolve("output.txt");

    Process process = new ProcessBuilder(
            "java", "-cp", dir.toString(), "Code"
    )
            .redirectErrorStream(true)
            .redirectOutput(outputFile.toFile())
            .start();

    try {
        if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            process.waitFor();
            return null;
        }

        if (Files.size(outputFile) > 64 * 1024) {
            return null;
        }

        if (process.exitValue() != 0) {
            return null;
        }

        return Files.readString(outputFile);

    } finally {
        if (process.isAlive()) {
            process.destroyForcibly();
        }
    }
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