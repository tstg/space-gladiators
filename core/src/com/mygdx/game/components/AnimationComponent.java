package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

/**
 * Created by Anonym on 2019/2/2.
 */

public class AnimationComponent extends Component {
    private AnimationController animationController;

    public AnimationComponent(ModelInstance instance) {
        animationController = new AnimationController(instance);
        // If we don't do this, our animation will be called once if we are repeating it.
        animationController.allowSameAnimation = true;
    }

    // The last parameters of the controller method are `AnimationListener` and transition time
    public void animate(final String id, final int loops, final int speed) {
        animationController.animate(id, loops, speed, null, 0);
    }

    public void animate(String id, float offset, float duration, int loopCount, int speed) {
        animationController.animate(id, offset, duration, loopCount, speed, null, 0);
    }

    public void update(float delta) {
        animationController.update(delta);
    }
}
