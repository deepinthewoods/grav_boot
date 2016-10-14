package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.niz.Main;
import com.niz.component.DragBlock;
import com.niz.component.Grid2dInput;
import com.niz.component.Position;
import com.niz.component.VectorInput2;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class DragBlockSystem extends EntitySystem implements Observer {

	private static final String TAG = "dragblocksystem";
	private Subject subject;
	private EngineNiz engine;
	private ImmutableArray<Entity> entities;
	private CameraSystem camSys;
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	@Override
	public void addedToEngine(Engine engine) {
		
		this.engine = (EngineNiz) engine;
		subject = this.engine.getSubject("dragblock");
		entities = engine.getEntitiesFor(Family.all(DragBlock.class, Position.class).get());
		this.engine.getSubject("screen").add(this);
		camSys = engine.getSystem(CameraSystem.class);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}
	Grid2dInput c = new Grid2dInput();
	private Vector3 screenCoordsA = new Vector3();
	private Vector3 screenCoordsB = new Vector3();

	@Override
	public void update(float deltaTime) {
		if (entities.size() == 0) return;
		for (Entity e : entities){
			Vector2 v = posM.get(e).pos;
			c.p.set((int)v.x, (int)v.y);
			subject.notify(e, Event.DRAG_BLOCK, c);
		}
	}

	@Override
	public void onNotify(Entity be, Event event, Object c) {
		if (event == Event.DRAG_SCREEN){
			VectorInput2 v = (VectorInput2) c;
			screenCoordsB.set(v.v.x, v.v.y, 0);
			screenCoordsA.set(0,0,0);
			camSys.adjustedCamera.unproject(screenCoordsA );
			camSys.adjustedCamera.unproject(screenCoordsB );
			screenCoordsB.sub(screenCoordsA).scl(Main.PX);
			//Gdx.app.log(TAG, "delta v in m " + screenCoordsB + " from " + v.v.x + " , " + v.v.y);;
			if (entities.size() == 0) return;
			for (Entity e : entities){
				posM.get(e).pos.sub(screenCoordsB.x, screenCoordsB.y);
			}
		}
	}

}
