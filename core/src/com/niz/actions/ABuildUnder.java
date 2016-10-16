package com.niz.actions;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.niz.action.Action;
import com.niz.component.Physics;

public class ABuildUnder extends BuildAction {
	boolean l = true, r = true, t = true, b = true;
	boolean doneT, doneB, doneR, doneL;
	int w0 = 0, h0 = 10, w1 = 0, h1 = 6;
	float duration = 1f, time;
	private int totalBlocksTraversed;
	
	@Override
	public void update(float dt) {
		super.update(dt);;
		ARandomRunTowards runA = parent.getAction(ARandomRunTowards.class);
		
		time += dt;
		Physics phys = physM.get(parent.e);
		//if (currentX > map.width)Gdx.app.log("tha", "buuild "+currentX + "," + currentY);
		//if (time > duration && runA.jumping){
		if (time > duration && !runA.jumping){
			//Gdx.app.log("kljl;", "done"+isFinished);
			isFinished = true;
			return;
		}
		
		
		
		//if (!runA.jumping){
		if (phys.vel.y < 0f){
			//if (phys.vel.y > 0) return;
			//box.b = false;
			setSolid(currentX, currentY-1);
		}
		
		
		this.setHasBeen();
			
			
		
		
		
	}

	@Override
	public void onEnd() {
		if (next instanceof AAutoBuild){
			AAutoBuild build = (AAutoBuild) next;
			build.totalBlocksTraversed += totalBlocksTraversed;
			
		}
		super.onEnd();
		
		
	}

	@Override
	public void onStart() {
		doneT = false;
		doneB = false;;
		doneR = false;
		doneL = false;
		super.onStart();
		lanes = Action.LANE_BUILD;
		time = 0f;
		isBlocking = true;
		//Gdx.app.log("build", "onStrat");;
		totalBlocksTraversed = 0;
		ARandomRunTowards runA = parent.getAction(ARandomRunTowards.class);
		if (!runA.up){
			int f = MathUtils.random(8);
			start.y -= f;
			h0 += f;
			h1 += f;
		}
	}

}
