package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Anonym on 2019/1/31.
 *
 * The player component is specifically tailored toward the player object.
 */

public class PlayerComponent extends Component {
    public float energy;
    public float oxygen;
    public float health;
    public static int score;

    public PlayerComponent() {
        energy = 100;
        oxygen = 100;
        health = 100;
        score = 0;
    }
}
