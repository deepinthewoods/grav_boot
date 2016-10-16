package com.niz.actions;

import java.util.Random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.Input;
import com.niz.SimplexNoise;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.system.OverworldSystem;

public class ARandomRunTowards extends Action {
	
	private static final String TAG = "random run twards action";
	private static final float SIDE_BUFfER = 5f;
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

	public float time;
	public Vector2 target = new Vector2(20, 100);
	public boolean jumping, skipRun;
	public float jumpTime, maxJumpTime, jumpTimeTarget, notJumpingOnGroundTime, skipRunMaxTime, skipRunTime;
	public boolean up;
	public float dist = 100, dist2 = dist * dist;
	@Override
	public void update(float dt) {
		
		/*if (!engine.simulating){
			//isFinished = true;
			return;
		}*/
		Vector2 pos = posM.get(parent.e).pos;
		Physics phys = physM.get(parent.e);
		up = pos.y < target.y;
		boolean left = pos.x > target.x;
		
		time += dt;
		//if (time < .1f){
			//Gdx.app.log(TAG, "time"+time);
			//return;
		//}
		
		if (target.dst2(pos) > 100){
			if (noise.noise(time*2f, 1f) < -.215f){
				left = !left;
			}
			if (noise.noise(time, 2f) < -.3215f){
				//up = !up;
			}
		}//*/
		
		boolean notSteer = false;
		boolean steerLeft = left;
		boolean jump = jumping;
		jumpTime += dt;
		notJumpingOnGroundTime += dt;
		
		if (!phys.onGround || jumping){
			notJumpingOnGroundTime = 0f;
		}
		//float slope = Math.abs((pos.y - target.y) / (pos.x - target.x));
		
		if (jumpTime > jumpTimeTarget){
			jumpTime = 0f;
			jumping = !jumping;
			if (up){
				
				if (jumping){
					//Gdx.app.log(TAG, "jumping up "+slope);
					jumpTimeTarget = 0.4296875f - r.nextFloat() * .25f;
					
					if (noise.noise(time, 10f) < -.0215f){
						// steeper
						skipRun = true;
						skipRunMaxTime = .15f;
						skipRunTime = 0f;
						
					}
					
				}
				else 
					jumpTimeTarget = 0.2f;// + r.nextFloat() * .2f;
			} else {
				//Gdx.app.log(TAG, "down");
				if (jumping)
					jumpTimeTarget = MathUtils.random(0.4296875f);
				else 
					jumpTimeTarget = MathUtils.random(2f);
			}
			
		}
		if (skipRun){
			notSteer = true;
			skipRunTime += dt;
			if (skipRunTime > skipRunMaxTime)
				skipRun = false;
		}
		
		if (pos.x < SIDE_BUFfER){
			steerLeft = false;
		} else if (pos.x > OverworldSystem.SCROLLING_MAP_WIDTH - SIDE_BUFfER){
			steerLeft = true;
		}

		if (pos.y < SIDE_BUFfER){
			
		} else if (pos.y > OverworldSystem.SCROLLING_MAP_HEIGHT - SIDE_BUFfER){
			
		}
		
		
		if (notSteer){
			controlM.get(parent.e).pressed[Input.WALK_RIGHT] = false;
			controlM.get(parent.e).pressed[Input.WALK_LEFT] = false;
		}
		else if (steerLeft){
			controlM.get(parent.e).pressed[Input.WALK_LEFT] = true;
			controlM.get(parent.e).pressed[Input.WALK_RIGHT] = false;;
		} else {
			controlM.get(parent.e).pressed[Input.WALK_RIGHT] = true;
			controlM.get(parent.e).pressed[Input.WALK_LEFT] = false;;
		}//*/
		
		if (jump){
			controlM.get(parent.e).pressed[Input.JUMP] = true;
		} else {
			controlM.get(parent.e).pressed[Input.JUMP] = false;
		}
		if (next instanceof AAutoBuild){
			//isFinished = true;
			
		} else {
		}
		
		
	}

	@Override
	public void onEnd() {
		Pools.free(r);
		r = null;
	}
	public int seed;
	private SimplexNoise noise = new SimplexNoise();
	private Random r;
	@Override
	public void onStart() {
		time = 0f;
		noise.setSeed(seed);
		r = Pools.obtain(Random.class);
		r.setSeed(1234);
	}
	
}


		
		
		
		