package com.niz.actions.ai;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.astar.RunConnection;
import com.niz.component.Control;
import com.niz.component.PathResult;
import com.niz.component.Position;

public class aFollowRunConnection extends Action implements Poolable{
	private static final String TAG = "follow runn conn action";
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	ComponentMapper<PathResult> resultM = ComponentMapper.getFor(PathResult.class);
	public RunConnection conn;
	public aFollowRunConnection() {
		reset();
	}

	@Override
	public void update(float dt) {
		Control control = controlM.get(parent.e);
		control.pressed[Input.WALK_LEFT] = false;
		control.pressed[Input.WALK_RIGHT] = false;
		Vector2 pos = posM.get(parent.e).pos;
		PathResult result = resultM.get(parent.e);
		if (result == null){
			isFinished = true;
			Gdx.app.log(TAG, "NO RESULT");
			return;
		}
		float dx = pos.x - (conn.to.x + .5f);
		int pathDx = conn.from.x - conn.to.x;
		
		if (pathDx < 0){
			Gdx.app.log(TAG, "right");
			control.pressed[Input.WALK_RIGHT] = true;
		} else if (pathDx > 0){
			Gdx.app.log(TAG, "left");
			control.pressed[Input.WALK_LEFT] = true;
		} else {//fall, this is a fail state
			isFinished = true;
			Gdx.app.log(TAG, "RESULT IS FALLING, SHOULD BE SIDEWAYS");
			return;
		}
	}

	@Override
	public void onEnd() {
		
	}

	@Override
	public void onStart() {
		
	}
	
@Override
public void reset() {
	super.reset();
	lanes = Action.LANE_PATH;
}
}
