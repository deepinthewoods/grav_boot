package com.niz;

import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class PreRenderSystem extends RenderSystem {
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Gdx.app.log("pre rend sys"	,  "clear");
	}
}