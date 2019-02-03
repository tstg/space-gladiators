package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Settings;
import com.mygdx.game.UI.GameUI;
import com.mygdx.game.components.AnimationComponent;
import com.mygdx.game.components.CharacterComponent;
import com.mygdx.game.components.EnemyComponent;
import com.mygdx.game.components.ModelComponent;
import com.mygdx.game.components.PlayerComponent;
import com.mygdx.game.components.StatusComponent;

/**
 * Created by Anonym on 2019/1/31.
 *
 * The player system requires a reference to this player in order to manipulate the correct
 * character component. This is done by making the system listen for all entities containing
 * `PlayerComponent`. As soon as an entity containing PlayerComponent gets added, the references of
 * the `PlayerSystem` will be set. This can be seen in the `entityAdded` function.
 */

public class PlayerSystem extends EntitySystem implements EntityListener {
    private  Entity player;
    private PlayerComponent playerComponent;
    private CharacterComponent characterComponent;
    private ModelComponent modelComponent;
    private final Vector3 tmp = new Vector3();
    private final Camera camera;
    private GameWorld gameWorld;
    private GameUI gameUI;
    public Entity gun;
    public Entity dome;

    ClosestRayResultCallback rayTestCB;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();

    public PlayerSystem(GameWorld gameWorld, Camera camera, GameUI gameUI) {
        this.gameWorld = gameWorld;
        this.camera = camera;
        this.gameUI = gameUI;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    @Override
    public void update(float deltaTime) {
        if (player == null) {
            return;
        }
        updateMovement(deltaTime);
        updateStatus();

        checkGameOver();
    }

    @Override
    public void entityAdded(Entity entity) {
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        characterComponent = entity.getComponent(CharacterComponent.class);
        modelComponent = entity.getComponent(ModelComponent.class);
    }

    @Override
    public void entityRemoved(Entity entity) {

    }

    private void updateMovement(float delta) {
        float deltaX = -Gdx.input.getDeltaX() * 0.5f;
        float deltaY = -Gdx.input.getDeltaY() * 0.5f;
        tmp.set(0, 0, 0);
        camera.rotate(camera.up, deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);
        tmp.set(0, 0, 0);
        // Move

        // The movement requires some math.
        // First, we will set the `characterDirection` vector to `1, 0, 0`, which makes it rotate
        // along the x axis. Then, we rotate it with the transform of the model.
        characterComponent.characterDirection.set(-1, 0, 0).rot(modelComponent.instance.transform).nor();
        characterComponent.walkDirection.set(0, 0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            characterComponent.walkDirection.add(camera.direction);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            characterComponent.walkDirection.sub(camera.direction);
        }
        tmp.set(0, 0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(1);
        }
        characterComponent.walkDirection.add(tmp);
        characterComponent.walkDirection.scl(10f * delta);
        characterComponent.characterController.setWalkDirection(characterComponent.walkDirection);

        Matrix4 ghost = new Matrix4();
        Vector3 translation = new Vector3();
        characterComponent.ghostObject.getWorldTransform(ghost);
        ghost.getTranslation(translation);
        modelComponent.instance.transform.set(translation.x, translation.y, translation.z,
                camera.direction.x, camera.direction.y, camera.direction.z, 0);

        // Lastly, we would like the camera position to be set to the player so that it becomes
        // first person.
        camera.position.set(translation);
        camera.update(true);

        dome.getComponent(ModelComponent.class).instance.transform.setToTranslation(translation);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            characterComponent.characterController.setJumpSpeed(25);
            characterComponent.characterController.jump();
        }

        if (Gdx.input.justTouched()) {
            fire();
        }
    }

    private void updateStatus() {
        gameUI.healthWidget.setValue(playerComponent.health);
    }

    private void fire() {
        Ray ray = camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom);  // 50 meters max from the origin
        /* Because we reuse the ClosestRayResultCallBack, we need reset its values */
        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.setRayFromWorld(rayFrom);
        rayTestCB.setRayToWorld(rayTo);

        gameWorld.bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);

        if (rayTestCB.hasHit()) {
            final btCollisionObject obj = rayTestCB.getCollisionObject();
            if (((Entity) obj.userData).getComponent(EnemyComponent.class) != null) {
//                ((Entity) obj.userData).getComponent(StatusComponent.class).alive = false;
                if (((Entity) obj.userData).getComponent(StatusComponent.class).alive) {
                    ((Entity) obj.userData).getComponent(StatusComponent.class).setAlive(false);
                    PlayerComponent.score += 100;
                }
            }
        }

        gun.getComponent(AnimationComponent.class).animate("Armature|shoot", 1, 3);
    }

    private void checkGameOver() {
        if (playerComponent.health <= 0 && !Settings.Paused) {
            Settings.Paused = true;
            gameUI.gameOverWidget.gameOver();
        }
    }
}
