package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pools;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.MovementData;
import com.niz.component.Physics;
import com.niz.component.SpriteAnimation;

public class ARun extends Action {
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private float time;
	@Override
	public void update(float dt) {
		//time += dt;
		Control con = controlM.get(parent.e);
		Physics phys = physM.get(parent.e);
		MovementData mov = moveM.get(parent.e);
		if (con.pressed[Input.WALK_LEFT]){
			if (con.pressed[Input.WALK_RIGHT]){
				isFinished = true;
				this.addAfterMe(Pools.obtain(ANotRun.class));
				return;
			}
			boolean change = phys.vel.x > 0;
			if (phys.onGround){
				phys.applyForceToCenter(-mov.run_force*(change?mov.changeDirectionForceMultiplier:1f), 0);
			}else {
				phys.applyForceToCenter(-mov.jump_x_force*(change?mov.changeDirectionForceMultiplier:1f), 0);
			}
		} else if (con.pressed[Input.WALK_RIGHT]){
			boolean change = phys.vel.x < 0;
			if (phys.onGround){
				phys.applyForceToCenter(mov.run_force*(change?mov.changeDirectionForceMultiplier:1f), 0);
			}else{
				phys.applyForceToCenter(mov.jump_x_force*(change?mov.changeDirectionForceMultiplier:1f), 0);
			}
		} else {
			isFinished = true;
			this.addAfterMe(Pools.obtain(ANotRun.class));
		}
		/*if (phys.vel.x > mov.speed_limit_x){
			phys.setLinearVelocity(mov.speed_limit_x, phys.vel.y);
		} else if (phys.vel.x < -mov.speed_limit_x){
			phys.setLinearVelocity(-mov.speed_limit_x, phys.vel.y);

		}*/
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
