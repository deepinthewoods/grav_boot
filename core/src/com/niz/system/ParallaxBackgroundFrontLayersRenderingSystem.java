package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.RenderSystem;

public class ParallaxBackgroundFrontLayersRenderingSystem extends RenderSystem {

	private ParallaxBackgroundSystem px;

	@Override
	public void addedToEngine(Engine engine) {
		px = engine.getSystem(ParallaxBackgroundSystem.class);
		// TODO Auto-generated method stub
		super.addedToEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		if (px.checkProcessing())
			;//px.drawFront();
		// TODO Auto-generated method stub
		super.update(deltaTime);
	}

}
