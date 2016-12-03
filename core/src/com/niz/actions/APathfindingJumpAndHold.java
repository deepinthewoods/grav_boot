package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Physics;
import com.niz.component.Position;

public class APathfindingJumpAndHold extends Action {
	public static final float[] THRESHOLD = {2f, 1f, .5f, .25f, .1f};
	private static final String TAG = "jump and hold path a";
	public static final int NORMAL_JUMP = 1<<3, WALLJUMP = 1<<4;
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

	public int index;
	private float time;
	private float timeThreshold;
	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG,  "update" + index);
		time += dt;
		Position pos = posM.get(parent.e);
		Control con = controlM.get(parent.e);
		con.pressed[Input.WALK_LEFT] = false;
		con.pressed[Input.WALK_RIGHT] = true;
		int typeIndex = index & AStar.TYPE_MASK;
		int aindex = (index & AStar.INDEX_MASK);
		if (typeIndex == WALLJUMP){
			if (aindex == 0){
				con.pressed[Input.WALK_LEFT] = false;
				con.pressed[Input.WALK_RIGHT] = true;
				
			} else {
				
				con.pressed[Input.WALK_LEFT] = true;
				con.pressed[Input.WALK_RIGHT] = false;
			}
			con.pressed[Input.JUMP] = false;
			if (time > .1f) con.pressed[Input.JUMP] = true;	
		}
		
		
		/*if (index < 1){
			boolean left = pos.pos.x > AStar.PATHFINDING_X_START+1.5f;
			if (left){
				con.pressed[Input.WALK_LEFT] = true;
				con.pressed[Input.WALK_RIGHT] = false;
			} else {
				con.pressed[Input.WALK_LEFT] = false;
				con.pressed[Input.WALK_RIGHT] = true;
			}
		}*/
		
		if (time < timeThreshold)
			con.pressed[Input.JUMP] = true;
		else 
			con.pressed[Input.JUMP] = false;
		if (typeIndex == NORMAL_JUMP || (typeIndex == WALLJUMP && aindex == 0)){
			if (pos.pos.y < AStar.PATHFINDING_INITIAL_Y_OFFSET - AStar.PATHFINDING_DOWN_Y_OFFSET - 1 
					||
					pos.pos.x > AStar.PATHFINDING_X_START + AStar.PATHFINDING_WIDTH-1) {
				//Gdx.app.log(TAG,  "outside of border" + pos.pos + parent.getAction(APathfindingLogBlocks.class).blocks.size);
				parent.engine.removeEntity(parent.e);
			}			
		} else if (typeIndex == WALLJUMP){
			Physics phys = physM.get(parent.e);
			if (phys.vel.y < 0 && time > .11f){
				//Gdx.app.log(TAG,  "apex" + pos.pos);
				parent.engine.removeEntity(parent.e);
			}
		}//*/
	}

	@Override
	public void onEnd() {
		
	}

	@Override
	public void onStart() {
		time = 0f;
		timeThreshold = THRESHOLD[index % THRESHOLD.length];
		int typeIndex = index / APathfindingJumpAndHold.THRESHOLD.length;
		int aindex = (index % THRESHOLD.length);
		
	}

}
