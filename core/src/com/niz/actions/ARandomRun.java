package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.niz.Input;
import com.niz.Main;
import com.niz.SimplexNoise;
import com.niz.action.Action;
import com.niz.component.Control;

public class ARandomRun extends Action {
	
	private static final String TAG = null;
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	public float time, leftBias = .55f;
	@Override
	public void update(float dt) {
		/*if (!engine.simulating){
			//isFinished = true;
			return;
		}*/
		time += dt;
		if (time < .1f){
			//Gdx.app.log(TAG, "time"+time);
			return;
		}
		float steer = noise.noise(time/4f, 1f) + leftBias;
		steer += noise.noise(time/16f, 3f) * .5f;
		float notSteer = noise.noise(time/2f, 2f);
		float jump = noise.noise(time*2f, 2f);
		
		if (notSteer < -.5f){
			controlM.get(parent.e).pressed[Input.WALK_RIGHT] = false;
			controlM.get(parent.e).pressed[Input.WALK_LEFT] = false;
		}
		else if (steer > 0){
			controlM.get(parent.e).pressed[Input.WALK_LEFT] = true;
			controlM.get(parent.e).pressed[Input.WALK_RIGHT] = false;;
		} else {
			controlM.get(parent.e).pressed[Input.WALK_RIGHT] = true;
			controlM.get(parent.e).pressed[Input.WALK_LEFT] = false;;
		}//*/
		
		if (jump > 0){
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
		

	}
	public int seed;
	private SimplexNoise noise = new SimplexNoise();
	@Override
	public void onStart() {
		time = 0f;
		noise.setSeed(seed);
	}
	
}
