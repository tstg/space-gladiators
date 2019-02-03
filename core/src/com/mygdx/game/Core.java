package com.mygdx.game;

import com.mygdx.game.screens.GameScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.screens.MainMenuScreen;

/**
 * Created by Anonym on 2019/1/30.
 */

public class Core extends ApplicationAdapter {
	public static final float VIRTUAL_WIDTH = 960;
	public static final float VIRTUAL_HEIGHT = 540;
	Screen screen;

	@Override
	public void create() {
		new Assets();
		new Settings();
		Settings.load();

		// we'll catch the software-back button from Android devices,
		// so the app will not close when it's pressed.
		Gdx.input.setCatchBackKey(true);
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		screen.resize(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		screen.render(Gdx.graphics.getDeltaTime());
	}

	public void setScreen(Screen screen) {
		if (this.screen != null) {
			this.screen.hide();
			this.screen.dispose();
		}
		this.screen = screen;
		if (this.screen != null) {
			this.screen.show();
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	@Override
	public void dispose() {
		Assets.dispose();
		Settings.save();
	}
}
