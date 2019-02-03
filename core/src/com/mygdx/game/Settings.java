package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by Anonym on 2019/2/1.
 */

public class Settings {
    public static boolean Paused;
    public static boolean soundEnabled = true;
    public static int[] highScores = new int[] {1000, 800, 500, 300, 100};
    public final static String file = ".spaceglad";
    private static final String leaderURL =
            "http://dreamlo.com/lb/PLfBGtHgG02wU0lSzVNrPAG0uQf3J3-UGzK1i7mXmmxA";
    private static final String request5 = "/pipe/5";

    public static void load(final Label[] leaderboardItems) {
        Net.HttpRequest requestBests = new Net.HttpRequest(Net.HttpMethods.GET);
        requestBests.setUrl(leaderURL + request5);
        Gdx.net.sendHttpRequest(requestBests, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                System.out.println(httpResponse);
                String string = new String(httpResponse.getResultAsString());
                String scores[] = string.split("\n");
                if (scores.length > 0) {
                    for (int i = 0; i < scores.length; i++) {
                        String score[] = scores[i].split("\\|");
                        if (i == 0) {
                            leaderboardItems[i].setText(String.valueOf(Integer.valueOf(
                                    score[score.length - 1]) + 1 + ")" + score[0] + ": " + score[1]));
                        } else {
                            String labelText = String.valueOf(Integer.valueOf(
                                    score[score.length - 1]) + 1) + ")" + score[0] + ": " + score[1];
                            leaderboardItems[i] = new Label(labelText, Assets.skin);
                        }
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                leaderboardItems[0].setText("Failed.");
                System.out.println(t);
            }

            @Override
            public void cancelled() {
                System.out.println("Cancel");
            }
        });
    }

    public static void load() {
        try {
            FileHandle fileHandle = Gdx.files.external(file);
            String[] strings = fileHandle.readString().split("\n");
            soundEnabled = Boolean.parseBoolean(strings[0]);
//            for (int i = 0; i < 5; i++) {
//                highScores[i] = Integer.parseInt(strings[i + 1]);
//            }
        } catch (Throwable e) {

        }
    }

    public static void save() {
        try {
//            System.out.println("save");
            FileHandle fileHandle = Gdx.files.external(file);
            fileHandle.writeString(Boolean.toString(soundEnabled) + "\n", false);
//            for (int i = 0; i < 5; i++) {
//                fileHandle.writeString(Integer.toString(highScores[i]) + "\n", true);
//            }
        } catch (Throwable e) {
        }
    }

    public static void sendScore(int score) {
        Net.HttpRequest request = new Net.HttpRequest("GET");
        request.setUrl("http://dreamlo.com/lb/PLfBGtHgG02wU0lSzVNrPAG0uQf3J3-UGzK1i7mXmmxA/add/"
                + "SpaceGladiator" + "/" + score);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {

            }

            @Override
            public void failed(Throwable t) {

            }

            @Override
            public void cancelled() {

            }
        });
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
