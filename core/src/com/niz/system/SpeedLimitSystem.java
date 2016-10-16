package com.niz.system;

import java.util.Iterator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.niz.component.Physics;
import com.niz.component.Position;

public class SpeedLimitSystem extends EntitySystem {

	private ImmutableArray<Entity> entities;
	ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Position.class, Physics.class).get());
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
	}

	@Override
	public void update(float deltaTime) {
		Iterator<Entity> i = entities.iterator();
		while (i.hasNext()){
			Entity e = i.next();
			Physics phys = physM.get(e);
			Position pos = posM.get(e);
			Physics2dSystem.enforceSpeedLimit(phys.vel, phys.limit);
		}
	}

}
