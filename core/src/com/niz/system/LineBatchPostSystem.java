package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.RenderSystem;

public class LineBatchPostSystem extends RenderSystem {

	private LineBatchSystem lineSys;

	@Override
	public void addedToEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.addedToEngine(engine);
		lineSys = engine.getSystem(LineBatchSystem.class);
	}

	@Override
	public void update(float deltaTime) {
		lineSys.drawLast(deltaTime);
	}

	
}
