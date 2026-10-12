package edu.batstateu.javajolt.entity;

import edu.batstateu.javajolt.enums.Difficulty;

public record SaveData(String name, int score, Difficulty difficulty) {

}
