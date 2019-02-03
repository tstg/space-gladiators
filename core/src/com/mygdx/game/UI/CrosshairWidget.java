package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.Settings;

/**
 * Created by Anonym on 2019/2/1.
 */

public class CrosshairWidget extends Actor {
    private Image crosshairDot;
    private Image crosshairInnerRing;

    public CrosshairWidget() {
        crosshairDot = new Image(new Texture(Gdx.files.internal(
                "crosshair/crossHairPoint.png")));
        crosshairInnerRing = new Image(new Texture(Gdx.files.internal(
                "crosshair/crossHairInnerRing.png")));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Settings.Paused) {
            return;
        }
        crosshairDot.draw(batch, parentAlpha);
        crosshairInnerRing.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if (Settings.Paused) {
            return;
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        crosshairDot.setPosition(x - 16, y - 16);
        crosshairInnerRing.setPosition(x - 16, y - 16);
        // This is useful if you want to make the ring rotate around the point that we experimented
        // with.
        crosshairInnerRing.setOrigin(
                crosshairInnerRing.getWidth() / 2, crosshairInnerRing.getHeight() / 2);
//        Logger.log(Logger.ANDREAS, Logger.INFO, "Setting origin to " + x + ", " + y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        crosshairDot.setSize(width * 2, height * 2);
        crosshairInnerRing.setSize(width * 2, height * 2);
    }
}
