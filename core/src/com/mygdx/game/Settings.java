package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Anonym on 2019/2/1.
 */

public class Settings {
    public static boolean Paused;
    public static boolean soundEnabled = true;
    public static int[] highScores = new int[] {1000, 800, 500, 300, 100};
    public final static String file = ".spaceglad";

    public static void load() {
        try {
            FileHandle fileHandle = Gdx.files.external(file);
            String[] strings = fileHandle.readString().split("\n");
            soundEnabled = Boolean.parseBoolean(strings[0]);
            for (int i = 0; i < 5; i++) {
                highScores[i] = Integer.parseInt(strings[i + 1]);
            }
        } catch (Throwable e) {

        }
    }

    public static void save() {
        try {
            System.out.println("save");
            FileHandle fileHandle = Gdx.files.external(file);
            fileHandle.writeString(Boolean.toString(soundEnabled) + "\n", false);
            for (int i = 0; i < 5; i++) {
                fileHandle.writeString(Integer.toString(highScores[i]) + "\n", true);
            }
        } catch (Throwable e) {
            System.out.println("save catch");
        }
    }

    public static void addScore(int score) {
        for (int i = 0; i < 5; i++) {
            if (highScores[i] < score) {
                for (int j = 4; j > i; j--) {
                    highScores[j] = highScores[j - 1];
                }
                highScores[i] = score;
                break;
            }
        }
    }
}
