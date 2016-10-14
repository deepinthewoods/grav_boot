package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;

public class AAutoRun extends Action {
	private boolean left = true;
	ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);

	@Override
	public void update(float dt) {
		if (left){
			controlM.get(parent.e).pressed[Input.WALK_LEFT] = true;
		} else {
			controlM.get(parent.e).pressed[Input.WALK_RIGHT] = true;
		}
		
	}

	@Override
	public void onEnd() {
		

	}

	@Override
	public void onStart() {
		

	}
	public void flip() {
		if (left){
			controlM.get(parent.e).pressed[Input.WALK_LEFT] = false;
		} else {
			controlM.get(parent.e).pressed[Input.WALK_RIGHT] = false;
		}
		left = !left;
	}
}
