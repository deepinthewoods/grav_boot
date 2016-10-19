package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pools;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Physics;

public class ANotRun extends Action {
	private static final String TAG = "not running action";
	private static final float WALL_SLIDE_MOVE_AWAY_DELAY = .2f;
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private float time;
	@Override
	public void update(float dt) {
		time += dt;

		Control con = controlM.get(parent.e);
		if (con == null){
			isFinished = true;
			//Gdx.app.log(TAG, "no control comp " + parent.e.getComponents().size() + parent.e.getComponent(Player.class) + parent.e.getId());
			//parent.engine.removeEntity(parent.e);
			return;
		}
		if (con.pressed[Input.WALK_LEFT] || con.pressed[Input.WALK_RIGHT]){
			boolean hasWallJump = parent.containsAction(AWallSlide.class);
			if (hasWallJump && time < WALL_SLIDE_MOVE_AWAY_DELAY){
				//Gdx.app.log(TAG, "wallstick " + time);
				return;
			}
			//Gdx.app.log(TAG, "faillllstick " + time);
			isFinished = true;
			this.addAfterMe(Pools.obtain(ARun.class));
		} else {
			Physics phys = physM.get(parent.e);
			//if (phys == null) return;
			if (phys.onGround)phys.vel.x = 0;
		}
		
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		time = 0f;
		

	}

}
