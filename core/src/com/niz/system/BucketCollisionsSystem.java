package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.niz.component.Buckets;
import com.niz.component.Position;

public class BucketCollisionsSystem extends EntitySystem {

	private Family family;
	private ImmutableArray<Entity> entities;

	@Override
	public void addedToEngine(Engine engine) {
		family = Family.all(Buckets.class, Position.class).get();
		entities = engine.getEntitiesFor(family);
		
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
	}

}
