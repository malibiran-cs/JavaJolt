package edu.batstateu.javajolt.entity;

import edu.batstateu.javajolt.enums.TutorialTrigger;

// Questions use one-based indices; session-start and results steps use zero.
public record TutorialStep(TutorialTrigger trigger, int questionIndex, String title, String body) {
}
