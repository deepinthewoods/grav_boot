package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.RenderSystem;

public class ParallaxBackgroundRenderNoBufferSystem extends RenderSystem {
private CameraSystem cameraSystem;
public ParallaxBackgroundSystem par;
@Override
public void addedToEngine(Engine engine) {
	cameraSystem = engine.getSystem(CameraSystem.class);
	super.addedToEngine(engine);
}
@Override
	public void update(float deltaTime) {
		if (cameraSystem.zoomedOut){
			par.draw();
		}
	}
}
