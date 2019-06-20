package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.niz.component.LevelEntrance;
import com.niz.component.OnDoor;
import com.niz.component.Physics;
import com.niz.component.PlaceAtStartPoint;
import com.niz.component.Position;

public class PlaceAtStartSystem extends EntitySystem {

	private static final String TAG = "place at start system";
	private ImmutableArray<Entity> entities;
	private EngineNiz engine;
	private WorkerSystem workSys;
	private float time;
	private ComponentMapper<PlaceAtStartPoint> placeM;
	private ComponentMapper<Position> posM;
	private ImmutableArray<Entity> startEntities;
	private ComponentMapper<LevelEntrance> entM;
	private ComponentMapper<Physics> physM;

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(PlaceAtStartPoint.class, Position.class).get());
		startEntities = engine.getEntitiesFor(Family.all(Position.class, LevelEntrance.class).get());
		this.engine = (EngineNiz) engine;
		workSys = engine.getSystem(WorkerSystem.class);
		placeM = ComponentMapper.getFor(PlaceAtStartPoint.class);
		posM = ComponentMapper.getFor(Position.class);
		entM = ComponentMapper.getFor(LevelEntrance.class);
		physM = ComponentMapper.getFor(Physics.class);

	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		//Gdx.app.log(TAG,  "update " + startEntities.size() + workSys.allPaused);

		if (!workSys.allPaused || startEntities.size() == 0){
			time = 0f;
			return;
		}
		time += deltaTime;
		
		for (Entity e : entities){
			//Gdx.app.log(TAG,  "PLACEUPD");

			PlaceAtStartPoint place = placeM.get(e);
			if (place.delay < time){
				Vector2 startPos = null;
				for (Entity entranceE : startEntities){
					if (place.index == entM.get(entranceE).index){
						startPos = posM.get(entranceE).pos;

						break;
					}
				}
				if (startPos == null){
					Gdx.app.log(TAG,  "didn't find start point, using index 0");
					startPos = posM.get(startEntities.get(0)).pos;
					//return;
				}
				e.remove(PlaceAtStartPoint.class);
				e.remove(OnDoor.class);
				//engine.removeEntity(e);
				posM.get(e).pos.set(startPos.x, startPos.y);
				//Gdx.app.log(TAG,  "place " + posM.get(e).pos);
				Physics phys = physM.get(e);
				if (phys != null){
					phys.vel.set(0,0);
				} else {
					e.add(engine.createComponent(Physics.class));
				}
				
			}
		}
	}

}
