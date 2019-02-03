package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.Assets;
import com.mygdx.game.components.PlayerComponent;

/**
 * Created by Anonym on 2019/2/1.
 */

public class ScoreWidget extends Actor {
    Label label;

    public ScoreWidget() {
        label = new Label("", Assets.skin);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        label.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        label.act(delta);
        label.setText("Score : " + PlayerComponent.score);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        label.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        label.setSize(width, height);
    }
}
