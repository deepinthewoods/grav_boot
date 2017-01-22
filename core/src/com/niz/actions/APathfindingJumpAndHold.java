package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.math.Vector2;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Physics;
import com.niz.component.Position;

public class APathfindingJumpAndHold extends Action {
	//public static final float[] THRESHOLD = {2f, 1f, .5f, .25f, .1f};
	public static final float[] HEIGHT_GOAL = 		{6f, 3f,       5f,   4f,   6f,    5f, 4f, 2f};
	public static final float[] MOVE_HEIGHT_GOAL = 	{4.5f, 2.75f,   4.2f, 3.3f, 3.5f,  2f, 2f, 1f};

	private static final String TAG = "jump and hold path a";
	public static final int NORMAL_JUMP = 1<<4, WALLJUMP = 1<<5;

	public static final int STANDING_JUMP = 1<<6;

	public static final int STANDING_DELAYED_RUN_JUMP = 1<<7;
	public static final int DELAYED_REVERSE_JUMP = 1<<8;
	
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

	public int index;
	private float time;
	private float heightGoal;
	private boolean jump;
	private float startHeight;
	private float moveHeightGoal;
	private boolean hasStartedMoving;
	public APathfindingJumpAndHold() {
		lanes = LANE_PATH;
	}
	//private float timeThreshold;
	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG,  "update" + index);
		time += dt;
		Vector2 pos = posM.get(parent.e).pos;
		Control con = controlM.get(parent.e);
		Physics phys = physM.get(parent.e);
		//con.pressed[Input.WALK_LEFT] = false;
		//con.pressed[Input.WALK_RIGHT] = true;
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
		} else if (typeIndex == STANDING_DELAYED_RUN_JUMP){
			con.pressed[Input.WALK_LEFT] = false;
			con.pressed[Input.WALK_RIGHT] = false;
			if (pos.y > moveHeightGoal){hasStartedMoving = true;}
			if (hasStartedMoving)con.pressed[Input.WALK_RIGHT] = true;

		} else if (typeIndex == DELAYED_REVERSE_JUMP){
			con.pressed[Input.WALK_LEFT] = false;
			con.pressed[Input.WALK_RIGHT] = true;
			if (pos.y > moveHeightGoal){hasStartedMoving = true;}
			if (hasStartedMoving){
				con.pressed[Input.WALK_RIGHT] = false;
				con.pressed[Input.WALK_LEFT] = true;
			}
			//con.pressed[Input.JUMP] = true;
		} else if (typeIndex == STANDING_JUMP){
			con.pressed[Input.WALK_LEFT] = false;
			con.pressed[Input.WALK_RIGHT] = true;
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
		
		if (pos.y > heightGoal || (phys.vel.y < .5f && pos.y < startHeight - .5f)){
			jump = false;
		}
		if (jump)
			con.pressed[Input.JUMP] = true;
		else 
			con.pressed[Input.JUMP] = false;
		
		
		if (typeIndex == NORMAL_JUMP || typeIndex == STANDING_JUMP|| typeIndex == STANDING_DELAYED_RUN_JUMP || typeIndex == DELAYED_REVERSE_JUMP || (typeIndex == WALLJUMP //&& aindex == 0
				)){
			if (pos.y < AStar.PATHFINDING_INITIAL_Y_OFFSET - AStar.PATHFINDING_DOWN_Y_OFFSET - 1 
					||
					pos.x > AStar.PATHFINDING_X_START + AStar.PATHFINDING_WIDTH-1) {
				//Gdx.app.log(TAG,  "outside of border" + pos + parent.getAction(APathfindingLogBlocks.class).blocks.size);
				parent.engine.removeEntity(parent.e);
			}			
		} else if (typeIndex == WALLJUMP){
			if (phys.vel.y < 0 && time > .11f){
				//Gdx.app.log(TAG,  "apex" + pos.pos);
				parent.engine.removeEntity(parent.e);
			}
		}//*/
		else {
			//Gdx.app.log(TAG,  "no cat" + pos);
		}
	}

	@Override
	public void onEnd() {
		
	}

	@Override
	public void onStart() {
		jump = true;
		time = 0f;
		//timeThreshold = THRESHOLD[index % THRESHOLD.length];
		Vector2 pos = posM.get(parent.e).pos;
		int aindex = (index & AStar.INDEX_MASK);
		heightGoal = HEIGHT_GOAL[aindex] + pos.y;
		startHeight = pos.y;
		moveHeightGoal = MOVE_HEIGHT_GOAL[aindex] + pos.y;
		hasStartedMoving = false;
	}

}
