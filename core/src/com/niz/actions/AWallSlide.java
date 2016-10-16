package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.niz.Data;
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

public class AWallSlide extends Action {
	private static final String TAG = "wall slide actiion";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);

	private static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private static final float TICK_THRESHOLD = .1f;
	static final float SMALL_AMOUNT = 0.15f;
	boolean left = true;
	float tick;
	//private MapSystem map;
	@Override
	public void update(float dt) {
		//set phys.onWall
		Map map = onMapM.get(parent.e).map;

		Physics phys = physM.get(parent.e);
		Body body = bodyM.get(parent.e);
		Vector2 pos = posM.get(parent.e).pos;
		int lx = (int) (pos.x - body.width - SMALL_AMOUNT), rx = (int) (pos.x + body.width + SMALL_AMOUNT), y = (int) pos.y;
		boolean 
		wallL = (map.isSolid(map.get(lx,  y)))
		, wallR = (map.isSolid(map.get(rx,  y)))
		, nextToWall = (wallR || wallL);
		
		if (nextToWall){
			tick = 0;
		} else {
			tick += dt;
			if (tick > TICK_THRESHOLD){
				isFinished = true;
				phys.onWall = false;
				
			}
		}
		if (phys.onGround ){
			isFinished = true; 
			phys.onWall = false;
			
		}
		
		
		//Gdx.app.log(TAG, "on"+phys.onWallRight + phys.onWallLeft);

	}

	@Override
	public void onEnd() {
		if (this.prev instanceof AJump){
			animM .get(parent.e).start(Data.hash("jump"));
		} else if (this.prev instanceof AFall){
	
			animM .get(parent.e).start(Data.hash("fall"));
		}
		else if (this.prev instanceof AStand){
			
			animM .get(parent.e).start(Data.hash("stand"));
		}

	}

	@Override
	public void onStart() {
		SpriteAnimation anim = animM .get(parent.e);
		anim.start(Data.hash("wallslide"));
		//map = parent.engine.getSystem(MapSystem.class);
	}

}
