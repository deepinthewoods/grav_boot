package com.niz.actions.ai;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.astar.FallPathConnection;
import com.niz.astar.PathConnection;
import com.niz.astar.PathNode;
import com.niz.component.Control;
import com.niz.component.Physics;
import com.niz.component.Position;

public class AFollowFall extends Action {
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

	private static final String TAG = "fall path action";

	public PathConnection conn;
	public AFollowFall() {
		lanes = LANE_PATH;
		isBlocking = true;
	}
	@Override
	public void update(float dt) {
		Vector2 pos = posM.get(parent.e).pos;
		Control con = controlM.get(parent.e);
		Physics phys = physM.get(parent.e);
		if (pos.y < ((PathNode)(conn.to)).y || phys.onGround){
			isFinished = true;
		}
		PathNode to = (PathNode) conn.to;
		PathNode from = (PathNode) conn.from;
		float toX = Math.abs(to.x + .5f - pos.x), toY = Math.abs(to.y + .5f - pos.y );
		
		FallPathConnection jconn = (FallPathConnection) conn;
		//lr
		con.pressed[Input.JUMP] = false;
		float dx = from.x + .5f - pos.x;
		if (dx < -.005F){
			con.pressed[Input.WALK_LEFT] = true;
			con.pressed[Input.WALK_RIGHT] = false;
		} else if (dx > .005f){
			con.pressed[Input.WALK_LEFT] = false;
			con.pressed[Input.WALK_RIGHT] = true;
		} else {}
	}

	@Override
	public void onEnd() {
		Control con = controlM.get(parent.e);
		con.pressed[Input.WALK_LEFT] = false;
		con.pressed[Input.WALK_RIGHT] = false;
	}

	@Override
	public void onStart() {
		Gdx.app.log(TAG,  "start" + posM.get(parent.e).pos + (PathNode)conn.from);
	}

}
