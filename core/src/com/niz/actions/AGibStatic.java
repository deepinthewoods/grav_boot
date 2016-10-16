package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.component.Physics;

public class AGibStatic extends Action {
	private static final String TAG = "gib static action";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

	@Override
	public void update(float dt) {
		AGibMoving act = Pools.obtain(AGibMoving.class);
		addBeforeMe(act);
		Physics phys = parent.engine.createComponent(Physics.class);
		
		parent.e.add(phys);
		isFinished = true;
		Gdx.app.log(TAG, "update");
	}

	@Override
	public void onEnd() {
		
	}

	@Override
	public void onStart() {
		
	}

}
