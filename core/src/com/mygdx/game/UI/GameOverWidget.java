package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Assets;
import com.mygdx.game.Core;
import com.mygdx.game.Settings;
import com.mygdx.game.components.PlayerComponent;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.LeaderboardsScreen;

/**
 * Created by Anonym on 2019/2/1.
 */

public class GameOverWidget extends Actor {
    private Core game;
    private Stage stage;
    private Image image;
    private TextButton retryButton;
    private TextButton leaderButton;
    private TextButton quitButton;

    public GameOverWidget(Core game, Stage stage) {
        this.game = game;
        this.stage = stage;
        setWidgets();
        setListeners();
    }

    private void setWidgets() {
        image = new Image(new Texture(Gdx.files.internal("data/gameOver.png")));
        retryButton = new TextButton("Retry", Assets.skin);
        leaderButton = new TextButton("Leaderboards", Assets.skin);
        quitButton = new TextButton("Quit", Assets.skin);
    }

    private void setListeners() {
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        leaderButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LeaderboardsScreen(game));
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(0, 0);
        image.setPosition(x, y + 32);
        retryButton.setPosition(x - 45, y - 96);
        leaderButton.setPosition(x + retryButton.getWidth() - 25, y - 96);
        quitButton.setPosition(x + retryButton.getWidth() + leaderButton.getWidth(), y - 96);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        image.setSize(width, height);
        retryButton.setSize(width / 2.5f, height / 2);
        leaderButton.setSize(width / 2.5f, height / 2);
        quitButton.setSize(width / 2.5f, height / 2);
    }

    public void gameOver() {
        stage.addActor(image);
        stage.addActor(retryButton);
        stage.addActor(leaderButton);
        stage.addActor(quitButton);
        // It will also take away the keyboard focus of the stage so we can't open the pause window
        // again.
        stage.unfocus(stage.getKeyboardFocus());
        Gdx.input.setCursorCatched(false);
        Settings.addScore(PlayerComponent.score);
    }
}
