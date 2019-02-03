package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mygdx.game.Core;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Settings;
import com.mygdx.game.UI.GameUI;

/**
 * Created by Anonym on 2019/1/30.
 *
 * Game screen will contain everything our game needs to draw its game world and UI.
 */

public class GameScreen implements Screen {
    Core game;
    GameWorld gameWorld;
    GameUI gameUI;

    public GameScreen(Core game) {
        this.game = game;
        gameUI = new GameUI(game);
        gameWorld = new GameWorld(gameUI);
        Settings.Paused = false;
        Gdx.input.setInputProcessor(gameUI.stage);
        // catch the cursor (from our PC), so we don't see it while playing
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        /** Updates */
        gameUI.update(delta);
        /** Draw */
        gameWorld.render(delta);
        gameUI.render();
    }

    @Override
    public void resize(int width, int height) {
        gameUI.resize(width, height);
        gameWorld.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        gameUI.dispose();
        gameWorld.dispose();
    }
}
