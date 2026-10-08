package edu.batstateu.javajolt.service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import edu.batstateu.javajolt.types.CodeQuestion;

public class CodeQuestionService {
    private CodeQuestion codeQuestion;

    public CodeQuestionService(CodeQuestion codeQuestion) {
        this.codeQuestion = codeQuestion;
    }

    public void compileUserProgram(String source) throws IOException {
        Path dir = Files.createTempDirectory("javajolt");
        try {
            Path file = dir.resolve("Code.java");
            Files.writeString(file, source);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            int result = compiler.run(null, null, null, "-d", dir.toString(), file.toString());
            if (result != 0) return;

        } finally {
            deleteRecursively(dir); 
        }
    }

    public Process runPlayerProgram() throws IOException {
        Process process = new ProcessBuilder("java", "Code")
                                .redirectErrorStream(true)
                                .start();
        return process;
    }

    private String getProgramOutput(Process process) throws IOException, InterruptedException{
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        process.waitFor();
        return output.toString();
    }

    public Boolean compareToExpectedOutput(String output, Process process) throws IOException, InterruptedException {
        String userOutput = getProgramOutput(process);
        String expectedOutput = codeQuestion.getExpectedOutput();
        if(userOutput.equals(expectedOutput)) return true;
        return false;
    }

    public Boolean checkRequiredConcepts() {
        List<String> requiredConcepts = codeQuestion.getRequiredConcepts();
        String playerProgram = codeQuestion.getPlayerProgram();
        for(String concept : requiredConcepts) {
            if(!playerProgram.contains(concept)) return false;
        }
        return true;
    }

    private void deleteRecursively(Path dir) {
    try (var paths = Files.walk(dir)) {
        paths.sorted(Comparator.reverseOrder())    // files before their folder
             .forEach(p -> p.toFile().delete());
    } catch (IOException ignored) { }
}
}
