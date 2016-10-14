package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Position;

public class AJumpCharSelect extends Action {
	private float waitOnGroundTime = .5f;
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private float xStart;
	private float time;
	@Override
	public void update(float dt) {
		AStand stand = parent.getAction(AStand.class);
		Control control = controlM.get(parent.e);
		if (stand != null){//standing
			time += dt;
			control.pressed[Input.JUMP] = false;
			if (time > waitOnGroundTime){
				control.pressed[Input.JUMP] = true;
				waitOnGroundTime = MathUtils.random(.25f, .5f);
			}
		} else {//jumping/falling
			time = 0f;
			control.pressed[Input.JUMP] = true;
		}
		
		Vector2 pos = posM.get(parent.e).pos;
		pos.x = xStart;
		
		
	}

	@Override
	public void onEnd() {
		Control control = controlM.get(parent.e);
		if (control != null) control.pressed[Input.JUMP] = false;
	}

	@Override
	public void onStart() {
		time = 0f;
		xStart = posM.get(parent.e).pos.x;
	}

}
