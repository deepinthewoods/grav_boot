package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.Data;
import com.niz.Input;
import com.niz.Main;
import com.niz.action.Action;
import com.niz.component.AutoGib;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.Inventory;
import com.niz.component.Map;
import com.niz.component.MovementData;
import com.niz.component.Physics;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;
import com.niz.item.Item;
import com.niz.item.ItemDef;
import com.niz.observer.Subject.Event;
import com.niz.system.MapSystem;
import com.niz.system.OverworldSystem;

public class AStand extends Action {
	private static final String TAG = "stand action";
	private static final long FALL_DELAY = (long)(.2f / Main.timeStep);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);

	boolean run = false;
	private boolean canJump;
	private transient MapSystem map;
	public boolean earlyJump;
	public boolean pathJump;
	private long debugTick;
	@Override
	public void update(float dt) {

		//Gdx.app.log(TAG, "update"+earlyJump);
		Physics phys = physM.get(parent.e);
		//if (phys == null) return;
		Control con = controlM.get(parent.e);
		//Gdx.app.log(TAG, "update" + canJump + con.pressed[Input.JUMP]);
		
		if (run){
			if (this.prev instanceof ANotRun){
				run = false;
				animM .get(parent.e).start(Data.hash("stand"));
				//Gdx.app.log(TAG, "switch to STAND");
			}
		} else {
			if (this.prev instanceof ARun){
				run = true;
				animM .get(parent.e).start(Data.hash("walk"));
				//Gdx.app.log(TAG, "switch to WALK");
			}
		}

		if (!phys.onGround && phys.onGroundTime < parent.engine.tick-FALL_DELAY){
			addBeforeMe(Pools.obtain(AFall.class));
			//Gdx.app.log(TAG, "switch to fall");
			isFinished = true;
		} else {
			Vector2 pos = posM.get(parent.e).pos;
			Body body = bodyM.get(parent.e);
			int lx = (int) (pos.x - body.width - AWallSlide.SMALL_AMOUNT), rx = (int) (pos.x + body.width + AWallSlide.SMALL_AMOUNT), y = (int) pos.y;
						
			if (con.pressed[Input.JUMP]){
				if (canJump || earlyJump || pathJump){
					earlyJump = false;
					MovementData mov = moveM.get(parent.e);
					//Vector2 pos = posM.get(parent.e).pos;

					phys.vel.y = 0f;
					phys.applyLinearImpulse(0, mov.jump_impulse);
					addBeforeMe(Pools.obtain(AJump.class));
					isFinished = true;
					//Gdx.app.log(TAG, "switch to jump"+physM.get(parent.e).vel);
					phys.onGround = false;
					canJump = false;
					Inventory inv = invM.get(parent.e);
				} else {
					//Gdx.app.log(TAG, "cant ju,p" + canJump);
				}
				
			} else {
				canJump = true;
				//Gdx.app.log(TAG, "can jump");
			}
			
		}
		//Gdx.app.log(TAG, "active"+phys.onGround);
	}



	@Override
	public void updateRender(float dt) {

		Physics phys = physM.get(parent.e);
		Control con = controlM.get(parent.e);
		if (!con.pressed[Input.JUMP])canJump = true;
	}



	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		//Gdx.app.log(TAG, "end");
	}

	@Override
	public void onStart() {
		if (this.prev instanceof ANotRun){
			run = true;
		} else run = false;
		//SpriteAnimation anim = animM .get(parent.e);
		//anim.start(Data.hash("walk"));
		Control con = controlM.get(parent.e);
		if (con.pressed[Input.JUMP]){
			canJump = false;
		} else 
			canJump = true;
		//earlyJump = false;
		//Gdx.app.log(TAG, "START");
		map = parent.engine.getSystem(MapSystem.class);
	}

}
