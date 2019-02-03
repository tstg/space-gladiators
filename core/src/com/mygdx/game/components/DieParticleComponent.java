package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.mygdx.game.Assets;

/**
 * Created by Anonym on 2019/2/3.
 */

public class DieParticleComponent extends Component {
    public ParticleEffect originalEffect;
    public boolean used = false;

    public DieParticleComponent(ParticleSystem particleSystem) {
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam =
                new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        if (!Assets.assetManager.isLoaded("point.pfx")) {
            Assets.assetManager.load("point.pfx", ParticleEffect.class, loadParam);
            Assets.assetManager.finishLoading();
        }
        originalEffect = Assets.assetManager.get("point.pfx");
    }
}
