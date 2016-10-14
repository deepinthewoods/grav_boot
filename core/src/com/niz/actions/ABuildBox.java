package com.niz.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.niz.action.Action;

public class ABuildBox extends BuildAction {
	private static final String TAG = "build box action";
	boolean l = true, r = true, t = true, b = true;
	boolean doneT, doneB, doneR, doneL;
	int w0 = 0, h0 = 10, w1 = 0, h1 = 6;
	float duration = 1f, time;
	private int totalBlocksTraversed;
	
	@Override
	public void update(float dt) {
		super.update(dt);;
		time += dt;
		//if (currentX > map.width)Gdx.app.log("tha", "buuild "+currentX + "," + currentY);
		//if (time > duration){
			//Gdx.app.log("kljl;", "done"+isFinished);
		//	isFinished = true;
		//	return;
		//}
		int left = (int) (start.x - w0 + 1), right = (int) (start.x + w1 - 1), top = (int) (start.y+h0), bottom = (int) (start.y - h1);;
		if (left < 0) left += map.width;
		if (right >= map.width) right -= map.width;
		if (top >= map.height) top -= map.height;
		if (bottom < 0) bottom += map.height;
		
		if (currentX < left || currentX > right || currentY < bottom-1 || currentY > top){
			isFinished = true;
			Gdx.app.log(TAG, "finished");
			return;
		}
		//int diffX = (int) (currentX - start.x - w), diffY = (int) (currentY - start.y - h);
		this.setHasBeen();
		if (changed){
			totalBlocksTraversed++;
			Vector2 vel = physM.get(parent.e).vel;
			if (currentX - left == -1 && l && !doneR){
				setSolid(currentX-1, currentY);
				doneL = true;
			} 
			if (currentX - right == 1 && r && !doneL){
				setSolid(currentX+1, currentY);
				doneR = true;

			}
			
			if (currentY - bottom == -1 && vel.y < .001f && b && !doneT){
				setSolid(currentX, currentY-1);
				doneB = true;
			}
			if (currentY - top == 1 && vel.y > -.001f&& t && !doneB){
				setSolid(currentX, currentY+1);
				doneT = true;
			}
			//
			
			
		}
		
		
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
		//max.add(1, 0);
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
