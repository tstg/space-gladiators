package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

/**
 * Created by Anonym on 2019/1/31.
 *
 * Bullet has its own character controller that handles all collisions and movement.
 * This character controller will be embedded in `CharacterComponent` along with a few other fields.
 */

public class CharacterComponent extends Component {
    // `ghostShape` is the shape of the character and collisions will be calculated around this shape.
    public btConvexShape ghostShape;
    // The `ghostObject` component is used to keep track of all the collisions and adds a
    // possibility to filter out different collision classes
    public btPairCachingGhostObject ghostObject;
    // the `characterController` object requires references to the `ghostObject` component and the
    // `ghostShape` component and calculates movements.
    public btKinematicCharacterController characterController;

    public Vector3 characterDirection = new Vector3();
    public Vector3 walkDirection = new Vector3();
}
