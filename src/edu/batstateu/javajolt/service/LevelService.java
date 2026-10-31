package edu.batstateu.javajolt.service;

public class LevelService {

    public static int calculateLevel(int xp) {
        if (xp < 100) {
            return 1;
        }

        return 2 + (xp - 100) / 50;
    }
}