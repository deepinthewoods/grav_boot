package com.niz.actions.mapgen;

import java.util.Random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pools;
import com.niz.PlatformerFactory;
import com.niz.action.Action;
import com.niz.action.ActionList;
import com.niz.actions.AAutoBuild;
import com.niz.actions.ARandomRunTowards;
import com.niz.component.Agent;
import com.niz.component.BitmaskedCollisions;
import com.niz.component.Body;
import com.niz.component.Inventory;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.Race;

public class AMapPlanner extends Action{
	private static final String TAG = "map planner";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);
	private static ComponentMapper<Agent> agentM = ComponentMapper.getFor(Agent.class);

	public int seed, level, subLevel;
	private Random r;
	private int progressCoarse;
	private int progressFine, progressFineTotal;
	public PlatformerFactory factory;
	@Override
	public void update(float dt) {
		Gdx.app.log(TAG, "update "+progressCoarse);
		Agent agent;
		switch (progressCoarse){
		case 0://minimal tunnels
		break;case 1://insert rooms, one branch at a time
		

		break;case 3:
		break;case 4:
		break;case 5:
		break;case 6:
		break;case 7:
		break;case 8:
		break;case 9:
		break;case 10:
			
		break;default:
			isFinished = true;
			
		
				
		}
		
		
	}

	private void makeSideWanderer(Entity e, int index) {

		Race race = parent.engine.createComponent(Race.class);
		race.raceID[Race.HEAD] = Race.GREEN_DRAGON;
		e.add(race);
		ActionList act = e.getComponent(ActionList.class);
		act.addToStart(Pools.obtain(AAutoBuild.class));
		ARandomRunTowards run = Pools.obtain(ARandomRunTowards.class);
		run.target.set(10, 10 + 20*index);
		act.addToStart(run);;
		BitmaskedCollisions bitm = Pools.obtain(BitmaskedCollisions.class);
		e.add(bitm);
		bitm.startBit = 10;
		e.add(parent.engine.createComponent(Agent.class));
	
	}

	private void makeWanderer(PooledEntity e) {
		Race race = parent.engine.createComponent(Race.class);
		race.raceID[Race.BACK_ARM] = Race.GREEN_DRAGON;
		e.add(race);
		ActionList act = e.getComponent(ActionList.class);
		act.addToStart(Pools.obtain(AAutoBuild.class));
		ARandomRunTowards run = Pools.obtain(ARandomRunTowards.class);
		run.target.set(10, 10);
		act.addToStart(run);;
		BitmaskedCollisions bitm = Pools.obtain(BitmaskedCollisions.class);
		e.add(bitm);
		bitm.startBit = 10;
		e.add(parent.engine.createComponent(Agent.class));
	}

	@Override
	public void onEnd() {
		
		Pools.free(r);
		r = null;
	}

	@Override
	public void onStart() {
		
		r = Pools.obtain(Random.class);
		r.setSeed(seed);
		progressCoarse = 0;
		progressFine = 0;
		
		
	}

}
