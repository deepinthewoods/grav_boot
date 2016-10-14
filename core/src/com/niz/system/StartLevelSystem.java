package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class StartLevelSystem extends EntitySystem {

	private ImmutableArray<Entity> entities;

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all().get());
		super.addedToEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		
		
	}

}
