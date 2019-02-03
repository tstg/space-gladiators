package com.mygdx.game.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mygdx.game.bullet.MotionState;
import com.mygdx.game.components.AnimationComponent;
import com.mygdx.game.components.BulletComponent;
import com.mygdx.game.components.CharacterComponent;
import com.mygdx.game.components.DieParticleComponent;
import com.mygdx.game.components.EnemyComponent;
import com.mygdx.game.components.GunComponent;
import com.mygdx.game.components.ModelComponent;
import com.mygdx.game.components.PlayerComponent;
import com.mygdx.game.components.StatusComponent;
import com.mygdx.game.systems.BulletSystem;
import com.mygdx.game.systems.RenderSystem;

/**
 * Created by Anonym on 2019/1/30.
 */

public class EntityFactory {
    /**
     *
     *
     * The `createStaticEntity` function is used in the following way:
     * ```
     * engine.addEntity(createStaticEntity(model, 0, 0, 0));
     * ```
     * */

    private static ModelBuilder modelBuilder;
    private static Texture playerTexture;
    private static Model playerModel;
    private static Model enemyModel;
    public static RenderSystem renderSystem;

    private static ModelComponent enemyModelComponent;
//    private static Model boxModel;

    static {
        modelBuilder = new ModelBuilder();
        playerTexture = new Texture(Gdx.files.internal("badlogic.jpg"));
        Material material = new Material(TextureAttribute.createDiffuse(playerTexture),
                ColorAttribute.createSpecular(1, 1, 1, 1),
                FloatAttribute.createShininess(8f));
        playerModel = modelBuilder.createCapsule(2f, 6f, 16, material,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates);
//        boxModel = modelBuilder.createBox(1f, 1f, 1f,
//                new Material(ColorAttribute.createDiffuse(Color.WHITE),
//                        FloatAttribute.createShininess(64f)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    public static Entity createStaticEntity(Model model, float x, float y, float z) {
        // First, we will calculate the bounding box that is used for a collision shape,
        // which in turn is required for `btRigidBodyConstructionInfo`.
        final BoundingBox boundingBox = new BoundingBox();
        // This bounding box is calculated based on the model that's given.
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f,
                boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));

        // We will then create our Ashely `entity`.
        Entity entity = new Entity();
        // First, we will add `ModelComponent` so that we can see our physics object in the world.
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        entity.add(modelComponent);

        BulletComponent bulletComponent = new BulletComponent();
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(
                0, null, col, Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody)bulletComponent.body).setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);

        return entity;
    }

    private static Entity createCharacter(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = new Entity();

        ModelComponent modelComponent = new ModelComponent(playerModel, x, y, z);
        entity.add(modelComponent);

        // Then, we will instantiate the `ghostObject` component and align it to the position of the
        // model.
        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);

        // The `collisionFlags` object is used for collision filtering.
        characterComponent.ghostObject.setCollisionFlags(
                btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        // The `characterController` is where it all happens and makes use of our shape and object.
        characterComponent.characterController = new btKinematicCharacterController(
                characterComponent.ghostObject, characterComponent.ghostShape, .35f);
        // We set the `userData` to our entity so that we have access to it at collision checking.
        characterComponent.ghostObject.userData = entity;
        entity.add(characterComponent);

        // We then tell `BulletWorld` that we have a special kind of object, which takes external
        // forces. We also say that it is a character and should respond to any object in our
        // collision world.
        bulletSystem.collisionWorld.addCollisionObject(
                entity.getComponent(CharacterComponent.class).ghostObject,
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) btBroadphaseProxy.CollisionFilterGroups.AllFilter);
        bulletSystem.collisionWorld.addAction(
                entity.getComponent(CharacterComponent.class).characterController);

        return entity;
    }

    public static Entity createPlayer(BulletSystem bulletSystem, float x, float y, float z) {
        // a player also has to be controlled; therefore it is also in need of `CharacterComponent`.
        Entity entity = createCharacter(bulletSystem, x, y, z);
        entity.add(new PlayerComponent());
        return entity;
    }

    // Now we are able to spawn an enemy with the following creation function
    public static Entity createEnemy(BulletSystem bulletSystem, float x, float y, float z) {
//        Entity entity = createCharacter(bulletSystem, x, y, z);
//        entity.add(new EnemyComponent(EnemyComponent.STATE.HUNTING));
//        entity.add(new StatusComponent());
        Entity entity = new Entity();

        if (enemyModel == null) {
            ModelLoader<?> modelLoader = new G3dModelLoader(new JsonReader());
            ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("data/monster.g3dj"));
            enemyModel = new Model(modelData, new TextureProvider.FileTextureProvider());
            // the model is much bigger than we need it to be
            for (Node node : enemyModel.nodes) {
                node.scale.scl(0.0025f);
            }
            enemyModel.calculateTransforms();

            // add an attribute to the enemy's material to make it fade out.
            enemyModelComponent = new ModelComponent(enemyModel, x, y, z);

            Material material = enemyModelComponent.instance.materials.get(0);
            BlendingAttribute blendingAttribute;
            material.set(blendingAttribute = new BlendingAttribute(
                    GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            enemyModelComponent.blendingAttribute = blendingAttribute;
        }
        Material material = enemyModelComponent.instance.materials.get(0);
        ((BlendingAttribute) material.get(BlendingAttribute.Type)).opacity = 1;

//        ModelComponent modelComponent = new ModelComponent(enemyModel, x, y, z);
//        entity.add(modelComponent);
        enemyModelComponent.instance.transform.set(enemyModelComponent.matrix4.setTranslation(x, y, z));
        entity.add(enemyModelComponent);

        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(enemyModelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(
                characterComponent.ghostObject, characterComponent.ghostShape, .35f);
        characterComponent.ghostObject.userData = entity;
        entity.add(characterComponent);

        bulletSystem.collisionWorld.addCollisionObject(
                entity.getComponent(CharacterComponent.class).ghostObject,
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) btBroadphaseProxy.CollisionFilterGroups.AllFilter);
        bulletSystem.collisionWorld.addAction(
                entity.getComponent(CharacterComponent.class).characterController);
        entity.add(new EnemyComponent(EnemyComponent.STATE.HUNTING));

        AnimationComponent animationComponent = new AnimationComponent(enemyModelComponent.instance);
        animationComponent.animate(
                EnemyAnimations.id, EnemyAnimations.offsetRun1, EnemyAnimations.durationRun1, -1, 1);
        entity.add(animationComponent);

        entity.add(new StatusComponent(animationComponent));

        entity.add(new DieParticleComponent(renderSystem.particleSystem));

        return entity;
    }

    public static Entity loadGun(float x, float y, float z) {
        ModelLoader<?> modelLoader = new G3dModelLoader(new JsonReader());
        ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("data/GUNMODEL.g3dj"));
        Model model = new Model(modelData, new TextureProvider.FileTextureProvider());
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        modelComponent.instance.transform.rotate(0, 1, 0, 180);
//        modelComponent.instance.transform.scale(0.008f, 0.008f, 0.008f);
        modelComponent.instance.transform.scale(0.02f, 0.02f, 0.02f);
        Entity gunEntity = new Entity();
        gunEntity.add(modelComponent);
        gunEntity.add(new GunComponent());
        gunEntity.add(new AnimationComponent(modelComponent.instance));
        return gunEntity;
    }

    public static Entity loadScene(int x, int y, int z) {
        Entity entity = new Entity();
        ModelLoader<?> modelLoader = new G3dModelLoader(new JsonReader());
        ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("data/arena.g3dj"));
        Model model = new Model(modelData, new TextureProvider.FileTextureProvider());
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        entity.add(modelComponent);
        BulletComponent bulletComponent = new BulletComponent();
        btCollisionShape shape = Bullet.obtainStaticNodeShape(model.nodes);
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(
                0, null, shape, Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body).setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);
        return entity;
    }

    public static Entity loadDome(int x, int y, int z) {
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        Model model = modelLoader.loadModel((Gdx.files.getFileHandle(
                "data/spacedome.g3db", Files.FileType.Internal)));
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        Entity entity = new Entity();
        entity.add(modelComponent);
        return entity;
    }
}
