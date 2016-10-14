package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.niz.component.BitmaskedCollisions;
import com.niz.component.CollisionComponent;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class BitmaskedCollisionsSystem extends EntitySystem implements Observer {
	ComponentMapper<BitmaskedCollisions> bitM = ComponentMapper.getFor(BitmaskedCollisions.class);

	private Subject collisionNotifier;
	private Family family;

	@Override
	public void addedToEngine(Engine engine) {
		collisionNotifier = ((EngineNiz) engine).getSubject("map collision");
		collisionNotifier.add(this);
		family = Family.all(BitmaskedCollisions.class).get();
		
		super.addedToEngine(engine);
	}
	

	@Override
	public void removedFromEngine(Engine engine) {
		collisionNotifier.remove(this);
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		CollisionComponent coll = (CollisionComponent) c;
		if (!family.matches(e)) return;
		BitmaskedCollisions bit = bitM.get(e);
		int bits = coll.block << (bit.startBit*3);
		bits &= 7;
		
		if ((bits & 1) == 1){
			coll.disabled = true;
		}
		//Gdx.app.log("map coll bitmasked", "notify");
		//coll.disabled = true;
	}

}
