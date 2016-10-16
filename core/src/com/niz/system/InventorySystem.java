package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.niz.component.Inventory;
import com.niz.component.Player;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class InventorySystem extends EntitySystem implements Observer {
	private static final String TAG = "inv sys ";
	ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private ImmutableArray<Entity> entities;
	private Subject subject;
	private ImmutableArray<Entity> playerEntities;
	private Subject invNotifier;
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Inventory.class).get());
		playerEntities = engine.getEntitiesFor(Family.all(Inventory.class, Player.class).get());
		
		super.addedToEngine(engine);
		subject = ((EngineNiz) engine).getSubject("refreshpaperdoll");
		subject.add(this);
		invNotifier = ((EngineNiz) engine).getSubject("inventoryRefresh");
		
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < playerEntities.size(); i++){
			Entity e = playerEntities.get(i);
			Inventory inv = invM.get(e);
			if (inv.dirty){
				
				invNotifier.notify(e, Event.BELT_REFRESH, inv);;
			}
		}
		
		
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			Inventory inv = invM.get(e);
			if (inv.dirty){
				//Gdx.app.log(TAG, "inv "+e.getId() + "  active ");
				
				inv.dirty = false;
				inv.update();
			}
		}
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		//TODO override layers for items
		Inventory inv = invM.get(e);
		//inv.
		
	}

}
