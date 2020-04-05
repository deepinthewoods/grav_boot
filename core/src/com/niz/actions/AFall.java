package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.Data;
import com.niz.Input;
import com.niz.Main;
import com.niz.action.Action;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.Map;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;
import com.niz.system.MapSystem;

public class AFall extends Action {
	private static final String TAG = "Fall action";
	static final long EARLY_JUMP_THRESHOLD = (long) (.2f / Main.timeStep);
	
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);

	//private MapSystem map;
	static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	public boolean held;
	public long heldTime;
	@Override
	public void update(float dt) {

		
		MovementData move = moveM.get(parent.e);
		Physics phys = physM.get(parent.e);
		Control con = controlM.get(parent.e);
		if (phys.onGround){
			AStand stand = Pools.obtain(AStand.class);
			addAfterMe(stand);
			if (held && parent.engine.tick - heldTime < EARLY_JUMP_THRESHOLD){
				stand.earlyJump = true;
				//Gdx.app.log(TAG, "early"+parent.engine.tick);
			}	
			isFinished = true;
		} else{
			Map map = onMapM.get(parent.e).map;
			Vector2 pos = posM.get(parent.e).pos;
			Body body = bodyM.get(parent.e);
			int lx = (int) (pos.x - body.width - AWallSlide.SMALL_AMOUNT), rx = (int) (pos.x + body.width + AWallSlide.SMALL_AMOUNT), y = (int) pos.y;
			if (move.hasWallSlide){
				
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
					}
				} else {}
			}
			
			if (con.pressed[Input.JUMP]){
				if (next instanceof AWallSlide){//wall jump
					AWallSlide wallSlide = (AWallSlide) next;
					AJump jump = Pools.obtain(AJump.class);
					addAfterMe(jump);
					MovementData mov = moveM.get(parent.e);
					
					//l or r
					float lr = (wallSlide.left? -1f:1f);
					//phys = physM.get(parent.e);
					phys.vel.y = 0;
					//phys.applyLinearImpulse(mov.walljump_x_impulse*lr, mov.walljump_y_impulse);
					phys.setLinearVelocity(mov.walljump_x_impulse*lr, mov.walljump_y_impulse);
					jump.xLimit = phys.limit.x;
					jump.resetXLimit = true;
					phys.limit.x = mov.walljump_x_impulse;
					Gdx.app.log(TAG, "walljump "+phys.limit.x*lr + "  ,  " + mov.walljump_y_impulse);
					isFinished = true;
					//if (parent.containsAction(AAutoRun.class))parent.actions.get(AAutoRun.class).flip();
				} else if (move.hasDoubleJump()){//double jump
					
					addAfterMe(Pools.obtain(AJump.class));
					MovementData mov = moveM.get(parent.e);
					//l or r
					physM.get(parent.e).vel.y = 0;
					//physM.get(parent.e).vel.x *= -1;
					physM.get(parent.e).applyLinearImpulse(0, mov.jump_impulse);
					isFinished = true;
					move.decrementDoubleJump();
					if (parent.containsAction(AAutoRun.class))parent.actions.get(AAutoRun.class).flip();
					
				} else {
					if (!held){
						held = true;
						//Gdx.app.log(TAG, "sdkljal"+parent.engine.tick);
						heldTime = parent.engine.tick;
					}
				}
			} else {//jump not pressed
				if (held) held = false;
			}
				
			
				
		}
//		if (Gdx.input.isKeyJustPressed(Keys.H)){
//			posM.get(parent.e).pos.set(128, 228);
//		}
		//Gdx.app.log(TAG, "active");
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		//Gdx.app.log(TAG, "end");
	}

	@Override
	public void onStart() {
		SpriteAnimation anim = animM .get(parent.e);
		if (next instanceof AWallSlide){} 
		else
			anim.start(Data.hash("fall"));
		//map = parent.engine.getSystem(MapSystem.class);
	}

}
