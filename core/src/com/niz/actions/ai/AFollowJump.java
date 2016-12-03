package com.niz.actions.ai;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.actions.APathfindingJumpAndHold;
import com.niz.astar.JumpPathConnection;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.Physics;
import com.niz.component.Position;

public class AFollowJump extends Action {
	private static final String TAG = "follow jump action";

	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	
	private boolean jumping;
	private float jumpTime;
	

	public JumpPathConnection conn;

	private float time;
	public AFollowJump() {
		lanes = Action.LANE_PATH;
		isBlocking = true;
	}
	@Override
	public void update(float dt) {
		time += dt;
		Physics phys = physM.get(parent.e);
		Control con = controlM.get(parent.e);
		Vector2 pos = posM.get(parent.e).pos;
		float jumpTimeTarget = APathfindingJumpAndHold.THRESHOLD[conn.index];
		//jump
		if (phys.onGround && !jumping){
			Gdx.app.log(TAG,  "start jumping");
			jumping = true;
			jumpTime = 0f;
		}
		if (jumping){
			jumpTime+= dt;
			//Gdx.app.log(TAG,  "jumping " +conn.index + "  " + jumpTimeTarget + " " + jumpTime + posM.get(parent.e).pos + conn.to.x + "," + conn.to.y);
			con.pressed[Input.JUMP] = true;
			if (jumpTime > jumpTimeTarget){
				jumping = false;
				con.pressed[Input.JUMP] = false;
				//Gdx.app.log(TAG,  "stop time jumping");
				isFinished = true;
			}
		} else {
			con.pressed[Input.JUMP] = false;
		}
		if (time > conn.cost + .1f){
			isFinished = true;
			//Gdx.app.log(TAG,  "time over STOP");
		}
		float dx = conn.to.x + .5f - pos.x;

		if (dx < -.15F){
			con.pressed[Input.WALK_LEFT] = true;
			con.pressed[Input.WALK_RIGHT] = false;
		} else if (dx > .15f){
			con.pressed[Input.WALK_LEFT] = false;
			con.pressed[Input.WALK_RIGHT] = true;
		}
	}

	@Override
	public void onEnd() {
		Control con = controlM.get(parent.e);
		con.pressed[Input.WALK_LEFT] = false;
		con.pressed[Input.WALK_RIGHT] = false;
		con.pressed[Input.JUMP] = false;
	}

	@Override
	public void onStart() {
		jumping = false;
		time = 0f;
	}

}
