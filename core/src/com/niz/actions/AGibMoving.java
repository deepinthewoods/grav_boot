package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.niz.action.Action;
import com.niz.component.Physics;
import com.niz.component.Position;

public class AGibMoving extends Action {
	private static final String TAG = "gib block action";
	static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	//ComponentMapper<Body> colM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	@Override

	public void update(float dt) {
		Physics phys = physM.get(parent.e);
		if (phys.onGround){
			parent.e.remove(Physics.class);
			//parent.e.remove(OnMap.class);
			//parent.e.remove(Body.class);
			//parent.e.remove(CollidesWithMap.class);
			//Gdx.app.log(TAG, "done");
			isFinished = true;
			
			
			//act.delay(.05f);
		}//else 
			//Gdx.app.log(TAG, "upd");
	}

	@Override
	public void onEnd() {
		/*
		AGibStatic act = Pools.obtain(AGibStatic.class);
		addBeforeMe(act);*/
		
		//parent.engine.getSystem(PilesSystem.class)
		//.addItem(posM.get(parent.e), bpM.get(parent.e) );
		//parent.engine.removeEntity(parent.e);
	}

	@Override
	public void onStart() {
	}

}
