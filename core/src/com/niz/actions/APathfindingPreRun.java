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
import com.niz.system.OverworldSystem;

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
		Position pos = posM.get(parent.e);
		Control con = controlM.get(parent.e);
		int typeIndex = index & AStar.TYPE_MASK;
		Physics phys;
		//Gdx.app.log(TAG,  "update " + index + " : " + typeIndex);
		boolean stand = false;
		switch (typeIndex){
		case APathfindingJumpAndHold.STANDING_DELAYED_RUN_JUMP:
		case APathfindingJumpAndHold.STANDING_JUMP:
		case APathfindingJumpAndHold.DELAYED_REVERSE_JUMP:
			stand = true;
		case APathfindingJumpAndHold.NORMAL_JUMP:
			//Gdx.app.log(TAG,  "apex" + pos.pos);
			con.pressed[Input.WALK_LEFT] = false;
			con.pressed[Input.WALK_RIGHT] = true;
			if (pos.pos.x > AStar.PATHFINDING_X_START + .5f){
				APathfindingJumpAndHold pathfindingJumpAndHold = Pools.obtain(APathfindingJumpAndHold.class);
				pathfindingJumpAndHold.index = index;
				addAfterMe(pathfindingJumpAndHold);
				isFinished = true;
				APathfindingLogBlocks log = Pools.obtain(APathfindingLogBlocks.class);
				log.index = index;
				
				addAfterMe(log);
				if (stand){
					AStopRunning stop = Pools.obtain(AStopRunning.class);
					addAfterMe(stop);
				}
			}
			break;
		case APathfindingJumpAndHold.WALLJUMP:
			phys = physM.get(parent.e);
			con.pressed[Input.WALK_LEFT] = true;
			con.pressed[Input.WALK_RIGHT] = false;
			con.pressed[Input.JUMP] = true;
			if (pos.pos.y < AStar.PATHFINDING_INITIAL_Y_OFFSET+AStar.PATHFINDING_WALL_HEIGHT){
				//Gdx.app.log(TAG,  "apex" + pos.pos);
				APathfindingJumpAndHold pathfindingJumpAndHold = Pools.obtain(APathfindingJumpAndHold.class);
				pathfindingJumpAndHold.index = index;
				addBeforeMe(pathfindingJumpAndHold);
				isFinished = true;
				APathfindingLogBlocks log = Pools.obtain(APathfindingLogBlocks.class);
				log.index = index;
				addBeforeMe(log);
				con.pressed[Input.JUMP] = false;

			}
			break;
		}
		
	}

	@Override
	public void onEnd() {
		OverworldSystem map = parent.engine.getSystem(OverworldSystem.class);
		for (int i = 0; i < AStar.PATHFINDING_X_START+1; i++){
			int x = i;
			int y = AStar.PATHFINDING_INITIAL_Y_OFFSET-1;
			//map.getMapFor(x,  y).set(x, y, 0);;
		}
		int typeIndex = index & AStar.TYPE_MASK;
		int ind = index & AStar.INDEX_MASK;
		//Gdx.app.log(TAG, "FDJSKAJLSDFKLSJDFKLDFJSSDFJLDFSJKLSDFJsdfaaaaaaaaaaaaaaaaaaaaaaaaaaaaaKL" + typeIndex + " " + ind);
		if (typeIndex ==  APathfindingJumpAndHold.DELAYED_REVERSE_JUMP && ind == 0){
			APathFindingDestroyMap dest = Pools.obtain(APathFindingDestroyMap.class);
			addBeforeMe(dest);
		}
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
