package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Anonym on 2019/1/31.
 */

public class EnemyComponent extends Component {
    public enum STATE {
        IDLE,
        FLEEING,
        HUNTING,
    }
    public STATE state = STATE.IDLE;
    public EnemyComponent(STATE state) {
        this.state = state;
    }
}
