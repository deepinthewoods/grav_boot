package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;

public class AStopRunning extends Action {
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private int runs;

	public AStopRunning() {
		lanes = LANE_PATH;
		isBlocking = true;
	}
	@Override
	public void update(float dt) {
		Control con = controlM.get(parent.e);
		con.pressed[Input.WALK_LEFT] = false;
		con.pressed[Input.WALK_RIGHT] = false;
		con.pressed[Input.JUMP] = false;
		//Gdx.app.log("stop running", "" + runs);
		if (runs++ > 1) isFinished = true;
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		runs = 0;
	}

}
