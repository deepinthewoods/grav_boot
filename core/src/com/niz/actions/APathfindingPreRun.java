package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.utils.Pools;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Physics;
import com.niz.component.Position;

public class APathfindingPreRun extends Action {
	private static final String TAG = "path pre run";
	public int index;
	private float time;
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

	public APathfindingPreRun() {
		reset();
	}
	@Override
	public void update(float dt) {//run to the right then jump after x == 10
		time += dt;
		Gdx.app.log(TAG,  "update " + index);
		Position pos = posM.get(parent.e);
		Control con = controlM.get(parent.e);
		int typeIndex = index / APathfindingJumpAndHold.THRESHOLD.length;
		Physics phys;
		switch (typeIndex){
		case APathfindingJumpAndHold.RUN:
			con.pressed[Input.WALK_LEFT] = false;
			con.pressed[Input.WALK_RIGHT] = true;
			if (pos.pos.x > AStar.PATHFINDING_X_START + .5f){
				APathfindingJumpAndHold pathfindingJumpAndHold = Pools.obtain(APathfindingJumpAndHold.class);
				pathfindingJumpAndHold.index = index;
				addAfterMe(pathfindingJumpAndHold);
				isFinished = true;
				APathfindingLogBlocks log = Pools.obtain(APathfindingLogBlocks.class);
				addAfterMe(log);
				
			}
			break;
		case APathfindingJumpAndHold.WALLJUMP:
			phys = physM.get(parent.e);
			con.pressed[Input.WALK_LEFT] = true;
			con.pressed[Input.WALK_RIGHT] = false;
			con.pressed[Input.JUMP] = true;
			if (phys.vel.y < 0 && time > .1f && pos.pos.y < AStar.PATHFINDING_INITIAL_Y_OFFSET+2.5f){
				//Gdx.app.log(TAG,  "apex" + pos.pos);
				APathfindingJumpAndHold pathfindingJumpAndHold = Pools.obtain(APathfindingJumpAndHold.class);
				pathfindingJumpAndHold.index = index;
				addBeforeMe(pathfindingJumpAndHold);
				isFinished = true;
				APathfindingLogBlocks log = Pools.obtain(APathfindingLogBlocks.class);
				addBeforeMe(log);
				con.pressed[Input.JUMP] = false;

			}
			break;
		}
		
	}

	@Override
	public void onEnd() {
		
	}

	@Override
	public void onStart() {
		time = 0f;
	}
	@Override
	public void reset() {
		super.reset();
		index = -1;
	}
}
