package com.niz.actions;

import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;

public class AHold extends Action {

	@Override
	public void update(float dt) {
		this.addBeforeMe(Pools.obtain(AStand.class));
		isFinished = true;

	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

}
