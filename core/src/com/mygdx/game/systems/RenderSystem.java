package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Core;
import com.mygdx.game.Settings;
import com.mygdx.game.components.AnimationComponent;
import com.mygdx.game.components.EnemyComponent;
import com.mygdx.game.components.GunComponent;
import com.mygdx.game.components.ModelComponent;
import com.mygdx.game.components.PlayerComponent;

/**
 * Created by Anonym on 2019/1/30.
 *
 * The render system will need to iterate over all entities containing
 * `ModelComponent` and draw these to the screen.
 *
 * we have moved all of the render classes inside `RenderSystem`.
 */

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private ModelBatch batch;
    private Environment environment;

    private static final float FOV = 67F;
    public static PerspectiveCamera perspectiveCamera =
            new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
    public PerspectiveCamera gunCamera;
    public Entity gun;

    private DirectionalShadowLight shadowLight;

    public static ParticleSystem particleSystem;

    private Vector3 position;

    public RenderSystem() {
//        perspectiveCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
//        perspectiveCamera.far = 10000f;

        setPerspectiveCamera();

        System.out.println(perspectiveCamera);

        shadowLight = new DirectionalShadowLight(1024 * 5, 1024 * 5, 200f, 200f, 1f, 300f);
        shadowLight.set(0.8f, 0.8f, 0.f, 0, -0.1f, 0.1f);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
        environment.add(shadowLight);
        environment.shadowMap = shadowLight;

        batch = new ModelBatch();
        gunCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        gunCamera.far = 100f;

        particleSystem = ParticleSystem.get();
        if (particleSystem.getBatches().size == 0) {
            BillboardParticleBatch billboardParticleBatch = new BillboardParticleBatch();
            billboardParticleBatch.setCamera(perspectiveCamera);
            particleSystem.add(billboardParticleBatch);
        }

        position = new Vector3();
    }

    private void setPerspectiveCamera() {
        perspectiveCamera.far = 10000f;
        PerspectiveCamera tempCam = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);

        perspectiveCamera.up.set(tempCam.up);
        perspectiveCamera.position.set(tempCam.position);
        perspectiveCamera.view.set(tempCam.view);
        perspectiveCamera.combined.set(tempCam.combined);
        perspectiveCamera.direction.set(tempCam.direction);
        perspectiveCamera.invProjectionView.set(tempCam.invProjectionView);
        perspectiveCamera.near = tempCam.near;
        perspectiveCamera.projection.set(tempCam.projection);
    }

//    public RenderSystem(ModelBatch batch, Environment environment) {
//        this.batch = batch;
//        this.environment = environment;
//    }

    private boolean isVisible(Camera cam, final ModelInstance instance) {
        return cam.frustum.pointInFrustum(instance.transform.getTranslation(position));
    }

    @Override
    public void addedToEngine(Engine engine) {
        // We want to get a list of all the entities containing `ModelComponent`.
        entities = engine.getEntitiesFor(Family.all(ModelComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        drawShadows(deltaTime);
        drawModels(deltaTime);
//        for (int i = 0; i < entities.size(); i++) {
//            ModelComponent mod = entities.get(i).getComponent(ModelComponent.class);
//            batch.render(mod.instance, environment);
//        }
    }

    private void drawShadows(float delta) {
        shadowLight.begin(Vector3.Zero, perspectiveCamera.direction);
        batch.begin(shadowLight.getCamera());
        for (int x = 0; x < entities.size(); x++) {
            if (entities.get(x).getComponent(PlayerComponent.class) != null
            || entities.get(x).getComponent(EnemyComponent.class) != null) {
                ModelComponent mod = entities.get(x).getComponent(ModelComponent.class);
                if (isVisible(perspectiveCamera, mod.instance)) {
                    batch.render(mod.instance);
                }
            }
            if (entities.get(x).getComponent(AnimationComponent.class) != null && !Settings.Paused) {
                entities.get(x).getComponent(AnimationComponent.class).update(delta);
            }
        }
        batch.end();
        shadowLight.end();
    }

    private void drawModels(float delta) {
        batch.begin(perspectiveCamera);
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getComponent(GunComponent.class) == null) {
                ModelComponent mod = entities.get(i).getComponent(ModelComponent.class);
                batch.render(mod.instance, environment);
                if (entities.get(i).getComponent(AnimationComponent.class) != null && !Settings.Paused) {
                    entities.get(i).getComponent(AnimationComponent.class).update(delta);
                }
            }
        }
        batch.end();
        renderParticleEffects();
        drawGun(delta);
    }

    private void renderParticleEffects() {
        batch.begin(perspectiveCamera);
        particleSystem.update();  /* technically not necessary for rendering */
        particleSystem.begin();
        particleSystem.draw();
        particleSystem.end();
        batch.render(particleSystem);
        batch.end();
    }

    private void drawGun(float delta) {
        // clear the depth buffer; this is needed in order to display the gun with a different camera.
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(gunCamera);
        batch.render(gun.getComponent(ModelComponent.class).instance);
//        gun.getComponent(AnimationComponent.class).update(delta);
        batch.end();
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
        gunCamera.viewportHeight = height;
        gunCamera.viewportWidth = width;
    }

    public void dispose() {
        batch.dispose();
        batch = null;
    }
}
