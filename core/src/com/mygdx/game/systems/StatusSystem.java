package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mygdx.game.GameWorld;
import com.mygdx.game.components.StatusComponent;

import java.util.Iterator;

/**
 * Created by Anonym on 2019/1/31.
 */

public class StatusSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private GameWorld gameWorld;

    public StatusSystem(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(StatusComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        // we changed the iterator, because it was creating an iterable object on every frame
//        Iterator iterator = entities.iterator();
//        while (iterator.hasNext()) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
//            Entity entity = (Entity) iterator.next();
            entity.getComponent(StatusComponent.class).update(deltaTime);
            if (entity.getComponent(StatusComponent.class).aliveStateTime >= 3.4f) {
                gameWorld.remove(entity);
            }
//            if (!entity.getComponent(StatusComponent.class).alive) {
//                gameWorld.remove(entity);
//            }
        }
    }
}
