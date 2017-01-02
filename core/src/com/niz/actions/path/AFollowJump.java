package com.niz.actions.path;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.actions.APathfindingJumpAndHold;
import com.niz.actions.AStand;
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
	
	public JumpPathConnection conn;

	private float time, jumpTime;

	private float heightGoal;

	private float startHeight;

	private float targetTime;

	private boolean hasJumped;

	private int runups;

	private boolean reversed;

	private float moveHeightGoal;

	private boolean startedRunning;
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
		//float jumpTimeTarget = APathfindingJumpAndHold.THRESHOLD[conn.index];
		//jump
		if (hasJumped){
			
			
		}

		if (phys.onGround && !jumping && parent.containsAction(AStand.class)){
			boolean fastEnough = false;
			//if (conn.stand) fastEnough = true;
			
			if (conn.isLeft){
				if (phys.vel.x < -phys.limit.x * .68f) fastEnough = true;
			} else {
				if (phys.vel.x > phys.limit.x * .68f) fastEnough = true;
			}
			if (fastEnough || conn.stand){
				tmpV.set(conn.from.x + .5f, conn.from.y + .5f).sub(pos);
				Gdx.app.log(TAG,  "start jumping" + conn.from + pos + tmpV + " : " + parent.engine.tick + con.pressed[Input.JUMP]);
				parent.getAction(AStand.class).pathJump = true;
				jumping = true;
				jumpTime = 0f;
				if (!hasJumped){
					hasJumped = true;
					time = 0f;
				}
			} else {//not fast enough, do runup
				Gdx.app.log(TAG,  "start runup" + conn.from);
				runups++;
				ARunupForJump runup = Pools.obtain(ARunupForJump.class);
				runup.conn = conn;
				addBeforeMe(runup);
			}
		}
		if (hasJumped){
			jumpTime += dt;
			if (startedRunning){
				if ((conn.isLeft == !reversed)){
					con.pressed[Input.WALK_LEFT] = true;
					con.pressed[Input.WALK_RIGHT] = false;
				} else {
					con.pressed[Input.WALK_LEFT] = false;
					con.pressed[Input.WALK_RIGHT] = true;
				}
				
			}
		}
		if (jumping){
			
			//Gdx.app.log(TAG,  "jumping " +conn.index + "  " + jumpTimeTarget + " " + jumpTime + posM.get(parent.e).pos + conn.to.x + "," + conn.to.y);
			Gdx.app.log(TAG,  "jumping "+ " : " + parent.engine.tick);
			con.pressed[Input.JUMP] = true;
			
			if (
					pos.y > heightGoal //||
					
					//jumpTime > targetTime
					//||
					//(phys.vel.y < .1f && pos.y > startHeight + .25f)
					){
				jumping = false;
				con.pressed[Input.JUMP] = false;
				//Gdx.app.log(TAG,  "stop time jumping");
				
			}
			
		} else {
			con.pressed[Input.JUMP] = false;
			//Gdx.app.log(TAG,  "not jumping "+ " : " + parent.engine.tick);
		}
		if (//!jumping && 
				hasJumped){//steer towards goal
			/*float dx = conn.to.x + .5f - pos.x;
			if (dx < -.24f){//r
				Gdx.app.log(TAG,  "R");
				con.pressed[Input.WALK_LEFT] = true;
				con.pressed[Input.WALK_RIGHT] = false;
			} else if (dx > .24f){//l
				Gdx.app.log(TAG,  "L");
				con.pressed[Input.WALK_LEFT] = false;
				con.pressed[Input.WALK_RIGHT] = true;
			} else {//on target
				Gdx.app.log(TAG,  "dorp");
				if (phys.vel.x < 0){
					con.pressed[Input.WALK_LEFT] = false;
					con.pressed[Input.WALK_RIGHT] = true;
				} else {
					con.pressed[Input.WALK_LEFT] = true;
					con.pressed[Input.WALK_RIGHT] = false;
				}
			}*/
			//CORRECT OVERSHOOT
			/**/
			if ((int)pos.x == conn.to.x && time > targetTime * .98f){
				Gdx.app.log(TAG,  "OVERSHOOT ");
				if (phys.vel.x > 0){
					con.pressed[Input.WALK_LEFT] = true;
					con.pressed[Input.WALK_RIGHT] = false;
				} else {
					con.pressed[Input.WALK_LEFT] = false;
					con.pressed[Input.WALK_RIGHT] = true;
				}
					
			}//*/
		}
		if (time > conn.cost + .005f){
			isFinished = true;
			Gdx.app.log(TAG,  "time over STOP" + conn.cost + " : " + parent.engine.tick);
		}
		//float dx = conn.to.x + .5f - pos.x;
		if ((conn.key & APathfindingJumpAndHold.DELAYED_REVERSE_JUMP) != 0){
			if (hasJumped && pos.y > moveHeightGoal){
				reversed = true;
			}
		}
		if ((conn.key & APathfindingJumpAndHold.STANDING_DELAYED_RUN_JUMP) != 0){
			if (hasJumped && pos.y > moveHeightGoal){
				startedRunning = true;
			}
		}
	}
	
	@Override
	public void onEnd() {
		Control con = controlM.get(parent.e);
		con.pressed[Input.WALK_LEFT] = false;
		con.pressed[Input.WALK_RIGHT] = false;
		con.pressed[Input.JUMP] = false;
		Vector2 pos = posM.get(parent.e).pos;

		tmpV.set(pos).add(-conn.to.x - .5f, -conn.to.y - .5f);
		//Gdx.app.log(TAG,  "end" + targetTime + conn.to + pos + "\n  = " + tmpV);
	}
	
	static Vector2 tmpV = new Vector2();
	@Override
	public void onStart() {
		runups = 0;
		hasJumped = false;
		jumping = false;
		hasJumped = false;
		time = 0f;
		Vector2 pos = posM.get(parent.e).pos;
		int index = conn.key & AStar.INDEX_MASK;
		heightGoal = APathfindingJumpAndHold.HEIGHT_GOAL[index] + pos.y + .0001f;
		startHeight = pos.y;
		targetTime = conn.cost - .05f;// + .2f;
		tmpV.set(pos).add(-conn.from.x - .5f, -conn.from.y - .5f);
		if (Math.abs(tmpV.x) < .1f) pos.x -= tmpV.x;
		Gdx.app.log(TAG,  "start" + targetTime + conn.from + pos + "\n  = " + tmpV);
		reversed = false;
		moveHeightGoal = pos.y + APathfindingJumpAndHold.MOVE_HEIGHT_GOAL[index];
		startedRunning = true;
		if ((conn.key & APathfindingJumpAndHold.STANDING_DELAYED_RUN_JUMP) != 0){
			startedRunning = false;
		}
	}

}
