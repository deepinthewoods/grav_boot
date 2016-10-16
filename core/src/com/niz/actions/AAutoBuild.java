package com.niz.actions;

import java.util.Random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Physics;

public class AAutoBuild extends BuildAction {
	
	private static int targetTraversed = 3000;
	private static final String TAG = "action auto build";
	int totalBlocksTraversed;
	Random random;
	ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

	private int iteration;

	@Override
	public void update(float dt) {
		
		ARandomRunTowards runA = parent.getAction(ARandomRunTowards.class);
		if (runA.up){
			Physics phys = physM.get(parent.e);
			//if (phys.vel.y > 0) return;
			//box.b = false;
			ABuildUnder act = Pools.obtain(ABuildUnder.class);
			addBeforeMe(act);
			//act.duration = random.nextFloat() * .3f;
			act.duration = .154f;
			return;
		}
		
		
		ABuildBox box = Pools.obtain(ABuildBox.class);
		
		box.h0 = random.nextInt(10)+1;
		box.h1 = 0;
		box.w0 = random.nextInt(17)+5;
		box.w1 = random.nextInt(10)+1;
		box.l = random.nextInt() > 0;
		box.r = random.nextInt() > 0;
		box.l = false;
		box.r = false;
		box.t = false;
		
		//box.t = MathUtils.randomBoolean();
		//box.b = MathUtils.randomBoolean();
		//MiniMapSystem mini = parent.engine.getSystem(MiniMapSystem.class);
		//if (mini != null) mini.dirty = true;
		
		
		box.duration = random.nextFloat() * .5f + 1f;
		
		
		
		int totalH = box.h0 + box.h1, totalW = box.w0 + box.w1 , total = totalW + totalH;
		if (total < 10){
			if (totalW > totalH){//change W
				box.w0 -= random.nextInt(5)+5;
				box.w1 += random.nextInt(5)+5;
			} else {//change H
				box.h0 -= random.nextInt(5)+5;
				box.h1 += random.nextInt(5)+5;
			}
		}
		
		
		if (totalBlocksTraversed >= targetTraversed){
			switch (iteration){
			case 0:
				posM.get(parent.e).pos.set(startPos);
				ARandomRun runa = parent.getAction(ARandomRun.class);
				runa.time = 0f;
				physM.get(parent.e).setLinearVelocity(0, 0);
				//Gdx.app.log("TAGGA", "RETURNRNN");
				targetTraversed = totalBlocksTraversed;
				totalBlocksTraversed = 0;
				break;
			case 1:
				//Gdx.app.log("TAGGA", "RETURdsfsdNRNN");
				isFinished = true;
				//Main.simulating = false;
				parent.engine.removeEntity(parent.e);
				//Gdx.app.log("TAGGA", "RETURNRNN");
				posM.get(parent.e).pos.set(startPos);
				ARandomRun run = parent.getAction(ARandomRun.class);
				run.time = 0f;
				break;
			}
			
			iteration++;
			return;
			}
		addBeforeMe(box);
		//Gdx.app.log("autob", "bul"+box.l+box.r+box.t+box.b);
	}

	@Override
	public void onEnd() {
		
		physM.get(parent.e).acc.set(0,0);
		physM.get(parent.e).vel.set(0,0);
		physM.get(parent.e).onGround = false;
		physM.get(parent.e).onWall = false;
		
		//physM.get(parent.e).
		controlM.get(parent.e).clear();
		//parent.clear();
		//parent.addToStart(new ARandomRun());;
		//parent.addToStart(new AHold());
		//parent.addToStart(new ANotRun());
		parent.getAction(ARandomRun.class).time = 0f;
		//Gdx.app.log(TAG,  "end"+posM.get(parent.e).pos);;

	}
	Vector2 startPos = new Vector2();
	@Override
	public void onStart() {
		lanes = Action.LANE_BUILD;
		random = new Random();
		random.setSeed(2);
		startPos.set(posM.get(parent.e).pos);
		//Gdx.app.log("TAGGA", "start" +startPos);
		
	}

}
