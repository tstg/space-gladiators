package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Assets;

/**
 * Created by Anonym on 2019/2/1.
 */

public class HealthWidget extends Actor {
    private ProgressBar healthBar;
    private ProgressBar.ProgressBarStyle progressBarStyle;
    private Label label;

    public HealthWidget() {
        // with white image (which is just a white pixel) contained on the default LibGDX's `Skin`
        // class, and we'll tint it with a red color for the background and green for the knob.
        progressBarStyle = new ProgressBar.ProgressBarStyle(
                Assets.skin.newDrawable("white", Color.RED),
                Assets.skin.newDrawable("white", Color.GREEN));
        // we will set the `knobBefore` drawable to be the same knob so as to get a feeling of fill
        // to the right.
        progressBarStyle.knobBefore = progressBarStyle.knob;
        healthBar = new ProgressBar(0, 100, 1, false, progressBarStyle);
        label = new Label("Health", Assets.skin);
        label.setAlignment(Align.center);
    }

    // We will override the `act` method from the `Actor` class to make our progress bar and label
    // update every frame, then override `draw` method to draw them with the `SetPosition()` method
    // for our internal widgets, set its size and set the value of the progress bar to update it on
    // every energy change.
    @Override
    public void draw(Batch batch, float parentAlpha) {
        healthBar.draw(batch, parentAlpha);
        label.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        healthBar.act(delta);
        label.act(delta);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        healthBar.setPosition(x, y);
        label.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        healthBar.setSize(width, height);
        progressBarStyle.background.setMinWidth(width);
        progressBarStyle.background.setMinHeight(height);
        progressBarStyle.knob.setMinWidth(healthBar.getValue());
        progressBarStyle.knob.setMinHeight(height);
    }

    public void setValue(float value) {
        healthBar.setValue(value);
    }
}
