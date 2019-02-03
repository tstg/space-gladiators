package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.mygdx.game.UI.GameUI;
import com.mygdx.game.components.CharacterComponent;
import com.mygdx.game.components.EnemyComponent;
import com.mygdx.game.components.ModelComponent;
import com.mygdx.game.components.StatusComponent;
import com.mygdx.game.managers.EntityFactory;
import com.mygdx.game.systems.BulletSystem;
import com.mygdx.game.systems.EnemySystem;
import com.mygdx.game.systems.PlayerSystem;
import com.mygdx.game.systems.RenderSystem;
import com.mygdx.game.systems.StatusSystem;

/**
 * Created by Admin on 2019/1/30.
 *
 * It handles all the elements inside the world,
 * such as obstacles, enemies, collisions, AI, and so on.
 */

public class GameWorld {
    private static final boolean debug = false;
    private DebugDrawer debugDrawer;
    private Engine engine;
    private Entity character;
    private Entity gun;
    private Entity dome;
    public BulletSystem bulletSystem;

    public PlayerSystem playerSystem;
    private RenderSystem renderSystem;

    private static final float FOV = 67f;
    private ModelBatch batch;
    private Environment environment;
    private PerspectiveCamera cam;

    private GameUI gameUI;

//    public ModelBuilder modelBuilder = new ModelBuilder();
//    Model wallHorizontal = modelBuilder.createBox(40, 20, 1,
//            new Material(ColorAttribute.createDiffuse(Color.WHITE),
//                    ColorAttribute.createSpecular(Color.RED),
//                    FloatAttribute.createShininess(16f)),
//                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//    Model wallVertical = modelBuilder.createBox(1, 20, 40,
//            new Material(ColorAttribute.createDiffuse(Color.GREEN),
//                    ColorAttribute.createSpecular(Color.WHITE),
//                    FloatAttribute.createShininess(16f)),
//            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//    Model groundModel = modelBuilder.createBox(40, 1, 40,
//            new Material(ColorAttribute.createDiffuse(Color.YELLOW),
//                    ColorAttribute.createSpecular(Color.BLUE),
//                    FloatAttribute.createShininess(16f)),
//            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

    public GameWorld(GameUI gameUI) {
        Bullet.init();
        setDebug();
//        initEnvironment();
//        initModelBatch();
        initPersCamera();
        addSystems(gameUI);
        addEntities();
    }

    private void setDebug() {
        if (debug) {
            debugDrawer = new DebugDrawer();
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        }
    }

    private void initPersCamera() {
        cam = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        cam.position.set(30f, 40f, 30f);
        cam.lookAt(0f, 0f, 0f);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
    }

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void initModelBatch() {
        batch = new ModelBatch();
    }

    private void addEntities() {
//        createGround();
//        createPlayer(5, 3, 5);
//        engine.addEntity(EntityFactory.createEnemy(bulletSystem, 5, 3, 5));
        loadLevel();
        createPlayer(0, 6, 0);
    }

    private void loadLevel() {
        engine.addEntity(EntityFactory.loadScene(0, 0, 0));
        // We will then pass that instance to `PlayerSystem` because we need the dome to follow the
        // player, to be centered on the player, or else it will look weird.
        engine.addEntity(dome = EntityFactory.loadDome(0, 0, 0));
        playerSystem.dome = dome;
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
//        engine.addSystem(renderSystem = new RenderSystem(batch, environment));
        engine.addSystem(renderSystem = new RenderSystem());
        engine.addSystem(bulletSystem = new BulletSystem());
//        engine.addSystem(playerSystem = new PlayerSystem(this, cam, gameUI));
        engine.addSystem(playerSystem = new PlayerSystem(this,
                renderSystem.perspectiveCamera, gameUI));
        engine.addSystem(new EnemySystem(this));
        engine.addSystem(new StatusSystem(this));
        if (debug) {
            bulletSystem.collisionWorld.setDebugDrawer(this.debugDrawer);
        }
    }

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }

    /* The Model Batch is one of the objects, which require disposing,
    hence we add it to the dispose function. */
    public void dispose() {
        bulletSystem.collisionWorld.removeAction(character.getComponent(
                CharacterComponent.class).characterController);
        bulletSystem.collisionWorld.removeCollisionObject(
                character.getComponent(CharacterComponent.class).ghostObject);
        bulletSystem.dispose();
        bulletSystem = null;
        renderSystem.dispose();

        character.getComponent(CharacterComponent.class).characterController.dispose();;
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();

//        wallHorizontal.dispose();
//        wallVertical.dispose();
//        groundModel.dispose();
//        batch.dispose();
//        batch = null;
    }

    /* With the camera set we can now fill in the resize function as well */
    public void resize(int width, int height) {
//        cam.viewportHeight = height;
//        cam.viewportWidth = width;
        renderSystem.resize(width, height);
    }

    // and set up the render function with the modelbatch
    public void render(float delta) {
        renderWorld(delta);
        checkPause();
    }

    protected  void renderWorld(float delta) {
//        batch.begin(cam);
//        engine.update(delta);
//        batch.end();
        engine.update(delta);
        if (debug) {
            debugDrawer.begin(renderSystem.perspectiveCamera);
            bulletSystem.collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
    }

    private void checkPause() {
        if (Settings.Paused) {
            engine.getSystem(PlayerSystem.class).setProcessing(false);
            engine.getSystem(EnemySystem.class).setProcessing(false);
            engine.getSystem(StatusSystem.class).setProcessing(false);
            engine.getSystem(BulletSystem.class).setProcessing(false);
        } else {
            engine.getSystem(PlayerSystem.class).setProcessing(true);
            engine.getSystem(EnemySystem.class).setProcessing(true);
            engine.getSystem(StatusSystem.class).setProcessing(true);
            engine.getSystem(BulletSystem.class).setProcessing(true);
        }
    }

//    private void drawBox() {
//        ModelBuilder modelBuilder = new ModelBuilder();
//        Material boxMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN),
//                ColorAttribute.createSpecular(Color.RED),
//                FloatAttribute.createShininess(16f));
//        Model box = modelBuilder.createBox(5, 5, 5, boxMaterial,
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//
//        Entity entity = new Entity();
//        entity.add(new ModelComponent(box, 10, 10, 10));
//        engine.addEntity(entity);
//        engine.addSystem(new RenderSystem(batch, environment));
//    }

//    private void createGround() {
//        engine.addEntity(EntityFactory.createStaticEntity(groundModel, 0, 0, 0));
//        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 10, -20));
//        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 10, 20));
//        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, 20, 10, 0));;
//        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, -20, 10, 0));
//    }

    private void createPlayer(float x, float y, float z) {
        character = EntityFactory.createPlayer(bulletSystem, x, y, z);
        engine.addEntity(character);
//        engine.addEntity(gun = EntityFactory.loadGun(2.5f, -1.9f, -4f));
        engine.addEntity(gun = EntityFactory.loadGun(3f, -2f, -4f));
        playerSystem.gun = gun;
        renderSystem.gun = gun;
    }
}
