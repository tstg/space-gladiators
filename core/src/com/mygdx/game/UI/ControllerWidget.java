package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Anonym on 2019/2/3.
 */

public class ControllerWidget {
    private static Touchpad movementPad;
    private static Touchpad watchPad;
    private static Vector2 movementVector;
    private static Vector2 watchVector;

    public ControllerWidget() {
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.knob = new TextureRegionDrawable(new TextureRegion(new Texture(
                Gdx.files.internal("data/touchKnob.png"))));
        touchpadStyle.knob.setMinWidth(64);
        touchpadStyle.knob.setMinHeight(64);
        touchpadStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture(
                Gdx.files.internal("data/touchBackground.png"))));
        touchpadStyle.background.setMinWidth(64);
        touchpadStyle.background.setMinHeight(64);

        // with a deadzone radius of 10
        movementPad = new Touchpad(10, touchpadStyle);
        watchPad = new Touchpad(10, touchpadStyle);

        movementPad.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        watchPad.setColor(0.5f, 0.5f, 0.5f, 0.5f);

        movementVector = new Vector2();
        watchVector = new Vector2();
    }

    public void addToStage(Stage stage) {
        movementPad.setBounds(15, 15, 300, 300);
        watchPad.setBounds(stage.getWidth() - 315, 15, 300, 300);
        stage.addActor(movementPad);
        stage.addActor(watchPad);
    }

    public static Vector2 getMovementVector() {
        movementVector.set(movementPad.getKnobPercentX(), movementPad.getKnobPercentY());
        return movementVector;
    }

    public static Vector2 getWatchVector() {
        watchVector.set(watchPad.getKnobPercentX(), watchPad.getKnobPercentY());
        return watchVector;
    }

}
