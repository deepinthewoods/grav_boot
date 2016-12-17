package com.niz.actions.ai;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Physics;
import com.niz.component.Position;

public class AFinishPath extends Action {
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);


	public int x;
	public int y;
	private float time;
	public AFinishPath() {
		lanes = Action.LANE_PATH;
	}
	@Override
	public void update(float dt) {
		time += dt;
		Vector2 pos = posM.get(parent.e).pos;
		Control con = controlM.get(parent.e);
		Physics phys = physM.get(parent.e);
		float dx = x + .5f - pos.x;
		float dy = y + .5f - pos.y;
		if (dx < 0.1f){
			con.pressed[Input.WALK_LEFT] = true;
			con.pressed[Input.WALK_RIGHT] = false;
		} else  if (dx > 0.1f){
			con.pressed[Input.WALK_LEFT] = false;
			con.pressed[Input.WALK_RIGHT] = true;
		} else {
			if (phys.vel.x > 0){
				con.pressed[Input.WALK_LEFT] = true;
				con.pressed[Input.WALK_RIGHT] = false;
			} else {
				con.pressed[Input.WALK_LEFT] = false;
				con.pressed[Input.WALK_RIGHT] = true;
			}
			//if (dy > -.3f && dy < .3f)isFinished = true;
		}
		
		if (time > 1f && phys.onGround) isFinished = true;
	}

	@Override
	public void onEnd() {
		Control con = controlM.get(parent.e);

		con.pressed[Input.WALK_LEFT] = false;
		con.pressed[Input.WALK_RIGHT] = false;
	}

	@Override
	public void onStart() {
		time = 0f;
	}

}
