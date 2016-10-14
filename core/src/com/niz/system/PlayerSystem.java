package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.niz.component.Inventory;
import com.niz.component.Player;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;


public class PlayerSystem extends IteratingSystem implements Observer {
	

	private static final String TAG = "player sysytem,";
	private Subject invSubject;


	public PlayerSystem() {
		super(Family.one(Player.class).get());
		// TODO Auto-generated constructor stub
		
	}
	

	@Override
	public void addedToEngine(Engine engine) {
		EngineNiz en = (EngineNiz) engine;
		en.getSubject("inventoryToggle").add(this);
		invSubject = en.getSubject("inventoryRefresh");
		super.addedToEngine(engine);
	}


	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		
	}


	@Override
	public void onNotify(Entity e, Event event, Object c) {
		if (getEntities().size() == 0) return;
		Entity playerE = getEntities().get(0);
		if (playerE == null) return;
		if (event == Event.INVENTORY_TOGGLE){
			invSubject.notify(playerE, Event.INVENTORY_REFRESH, playerE.getComponent(Inventory.class));
			//invSubject.notify(playerE, Event.BELT_REFRESH, playerE.getComponent(Inventory.class));
			//Gdx.app.log(TAG, "resize notifying belt refresh");
		}
		
	}
	

}
