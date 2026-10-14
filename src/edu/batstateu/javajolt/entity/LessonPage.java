package edu.batstateu.javajolt.entity;

import java.util.List;

import edu.batstateu.javajolt.enums.Difficulty;
public record LessonPage(int number, Difficulty level, String title, List<String> body) {
}
