package com.niz.actions.ai;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.BlockDefinition;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.actions.AStopRunning;
import com.niz.astar.JumpPathConnection;
import com.niz.component.Control;
import com.niz.component.Map;
import com.niz.component.Position;
import com.niz.system.OverworldSystem;

public class ARunupForJump extends Action {

	public JumpPathConnection conn;
	private boolean hasReachedTurn;
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	public ARunupForJump() {
		lanes = LANE_PATH;
		isBlocking = true;
	}
	@Override
	public void update(float dt) {
		Control con = controlM.get(parent.e);
		Vector2 pos = posM.get(parent.e).pos;

		if (!hasReachedTurn){
			OverworldSystem over = parent.engine.getSystem(OverworldSystem.class);
			int x = (int) pos.x;int y = (int) pos.y;
			Map map = over.getMapFor(x, y);
			int b = map.get(x , y-1 );
			int id = (b & map.ID_MASK) >> map.ID_BITS;
			BlockDefinition def = map.defs[id];
			if (!def.isSolid){
				hasReachedTurn = true;
				addBeforeMe(Pools.obtain(AStopRunning.class));
			}
			
			if (conn.isLeft){
				//run right
				con.pressed[Input.WALK_LEFT] = false;
				con.pressed[Input.WALK_RIGHT] = true;
				if (pos.x > conn.from.x + .5f + 3){
					hasReachedTurn = true;
					addBeforeMe(Pools.obtain(AStopRunning.class));
				}
			} else {
				//run left
				con.pressed[Input.WALK_LEFT] = true;
				con.pressed[Input.WALK_RIGHT] = false;
				if (pos.x < conn.from.x + .5f - 3){
					hasReachedTurn = true;
					addBeforeMe(Pools.obtain(AStopRunning.class));
				}
			}
			
			
		} else {
			if (conn.isLeft){
				//run left
				con.pressed[Input.WALK_LEFT] = true;
				con.pressed[Input.WALK_RIGHT] = false;
				if (pos.x < conn.from.x + .5f){
					isFinished = true;
				}
			} else {
				//run right
				con.pressed[Input.WALK_LEFT] = false;
				con.pressed[Input.WALK_RIGHT] = true;
				if (pos.x > conn.from.x + .5f){
					isFinished = true;
				}
			}
		}
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		hasReachedTurn = false;
	}

}
