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
    public boolean judge(String source, String expectedOutput, List<String> requiredConcepts) {
        if (!hasRequiredConcepts(source, requiredConcepts)) return false;

        Path dir = null;
        try {
            dir = Files.createTempDirectory("javajolt");
            if (!compile(source, dir)) return false;

            String output = run(dir);             
            return output != null
                    && normalize(output).equals(normalize(expectedOutput));
        } catch (IOException | InterruptedException e) {
            return false;
        } finally {
            if (dir != null) deleteRecursively(dir);  
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
        Process process = new ProcessBuilder("java", "-cp", dir.toString(), "Code")
                .redirectErrorStream(true)
                .start();
        if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            return null;
        }
        return new String(process.getInputStream().readAllBytes());
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