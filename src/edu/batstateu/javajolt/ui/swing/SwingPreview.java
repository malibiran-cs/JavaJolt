package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.entity.Player;
import edu.batstateu.javajolt.data.QuestionBank;
import edu.batstateu.javajolt.service.LessonRunnerService;
import edu.batstateu.javajolt.service.LessonService;
import edu.batstateu.javajolt.service.QuestionService;
import edu.batstateu.javajolt.ui.GameUI;
import javax.swing.SwingUtilities;

// Temporary launcher so the UI can be viewed without touching GameApplication.
public class SwingPreview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuestionService questionService = new QuestionService(new QuestionBank());
            GameUI consoleUI = new GameUI();
            LessonRunnerService lessonRunner = new LessonRunnerService(
                    questionService, new LessonService(), consoleUI);
            MainWindow window = new MainWindow(new Player("Preview", 0), questionService,
                    lessonRunner, edu.batstateu.javajolt.enums.Difficulty.BEGINNER);
            window.setVisible(true);
        });
    }
}
