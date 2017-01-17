package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.Data;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.Inventory;
import com.niz.component.Map;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;

public class AJump extends Action{
	private static final String TAG = "jump action";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);;
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);

	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private float time, jumpForce;
	private boolean goingUp;
	public float xLimit;
	public boolean resetXLimit;
	public long heldTime;

	@Override
	public void update(float dt) {
		time += dt;
		heldTime++;
		Physics phys = physM.get(parent.e);
		//if (phys == null) return;
		//phys.body.applyForceToCenter(0, 1, true);
		Control con = controlM.get(parent.e);
		MovementData mov = moveM.get(parent.e);
		if (time < mov.jump_y_force_time && time > mov.jump_y_force_delay){
			phys.applyForceToCenter(0, jumpForce);
			jumpForce -= mov.jump_y_force_decrement_rate*dt;
			jumpForce = Math.max(0,  jumpForce);
		}
		
		if (phys.onGround){
			this.addBeforeMe(Pools.obtain(AStand.class));
			isFinished = true;
			
		} else if (mov.hasWallSlide){
			//Gdx.app.log(TAG, "least ");
			Map map = onMapM.get(parent.e).map;

			Vector2 pos = posM.get(parent.e).pos;
			Body body = bodyM.get(parent.e);
			int lx = (int) (pos.x - body.width - AWallSlide.SMALL_AMOUNT), rx = (int) (pos.x + body.width + AWallSlide.SMALL_AMOUNT), y = (int) pos.y;
			boolean 
			wallL = (map.isSolid(map.get(lx,  y)))
			, wallR = (map.isSolid(map.get(rx,  y)))
			, nextToWall = (wallR || wallL);
			if (wallL || wallR){
				if (next instanceof AWallSlide){
					AWallSlide ws = (AWallSlide) next;
					//this.addAfterMe(ws);
					if (wallR)ws.left = true;
					else if (wallL)ws.left = false;
				} else 
				{
					AWallSlide ws = Pools.obtain(AWallSlide.class);
					this.addAfterMe(ws);
					if (wallR)ws.left = true;
					else if (wallL)ws.left = false;
					
					if (con.pressed[Input.JUMP] && parent.engine.tick - heldTime < AFall.EARLY_JUMP_THRESHOLD){
						//stand.earlyJump = true;
						Gdx.app.log(TAG, "early"+parent.engine.tick);
						float lr = (ws.left? -1f:1f);
						//phys = physM.get(parent.e);
						phys.vel.y = 0;
						//phys.applyLinearImpulse(mov.walljump_x_impulse*lr, mov.walljump_y_impulse);
						phys.setLinearVelocity(mov.walljump_x_impulse*lr, mov.walljump_y_impulse);
						xLimit = phys.limit.x;
						resetXLimit = true;
						phys.limit.x = mov.walljump_x_impulse;
					}
				}
			} else {
				
			}
			
			
			if (con.pressed[Input.JUMP] == false){
				
				if (mov.recoilJump){
					this.addAfterMe(Pools.obtain(AFallRecoil.class));
					isFinished = true;
					if (mov.cancelLiftOnRelease) phys.vel.y = Math.min(0, phys.vel.y);
					Inventory inv = invM.get(parent.e);
				} else {//normal fall
					this.addAfterMe(Pools.obtain(AFall.class));
					isFinished = true;
					if (mov.cancelLiftOnRelease) phys.vel.y = Math.min(0, phys.vel.y);
					Inventory inv = invM.get(parent.e);
				}
				/*if (inv != null){
					Item item = inv.getActiveItem();
					ItemDef def = item.getDef();
					if (def.hasJump()){
						def.onStopJump(parent.e);
					}
				}*/
				
				//Gdx.app.log(TAG, "to fall");
			}
			
		}
		
		if (phys.vel.y < 0){
			if (goingUp){
				phys.apexTime = time;
			}
			goingUp = false;
		} else {
			
			goingUp = true;
		}
		
		//Gdx.app.log(TAG, "active"+jumpForce);
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		if (resetXLimit){
			resetXLimit = false;
			physM.get(parent.e).limit.x = xLimit;
		}
	}

	@Override
	public void onStart() {
		time = 0f;
		heldTime = 0;
		jumpForce = moveM.get(parent.e).jump_y_force;
		SpriteAnimation anim = animM .get(parent.e);
		anim.start(Data.hash("jump"));
	}

}
