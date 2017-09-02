package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.niz.component.Agent;
/**
 * 
 * @author niz
 *	Stores Agents to be added to the map. Removes finished agents and stores them.
 */
public class AgentSystem extends EntitySystem {
	
	private ImmutableArray<Entity> entities;
	private EngineNiz engine;

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Agent.class).get());
		this.engine = (EngineNiz) engine;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
        //Gdx.app.log("agentsy", "sim");
        engine.simulating = entities.size() > 0;
		
	}

}
