package com.mygdx.game.bullet;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 * Created by Anonym on 2019/1/30.
 *
 * This class will hold the position and angle of any static or dynamic physics object
 * and will extend the `btMotionState` class from the `Bullet` library
 */

public class MotionState extends btMotionState {
    private final Matrix4 transform;

    public MotionState(final Matrix4 transform) {
        this.transform = transform;
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
