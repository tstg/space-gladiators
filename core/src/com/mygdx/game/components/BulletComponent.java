package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.game.bullet.MotionState;

/**
 * Created by Anonym on 2019/1/30.
 *
 * This component will contain all data that is required to function within a bullet environment
 */

public class BulletComponent extends Component {
    // It contains a `motionState` object for information about the position and angle of the
    // physical object.
    public MotionState motionState;
    // Citing straight from the Bullet API, `btRigidBodyConstructionInfo` provides information to
    // create a rigid body.
    public btRigidBody.btRigidBodyConstructionInfo bodyInfo;
    // The `btCollisionObject` is the object that can eventually be added to our bullet environment
    // and is used to manage collision detection objects.
    public btCollisionObject body;
    // To show how it's put to use, we will create a function that accepts a model and a position,
    // and returns an Ashley `entity`.
}
