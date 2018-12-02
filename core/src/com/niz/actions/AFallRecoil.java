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

public class AFallRecoil extends Action{
	private static final String TAG = "Fall recoil action";
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
		
		
		
		AStand stand = Pools.obtain(AStand.class);
		addAfterMe(stand);
		if (held && parent.engine.tick - heldTime < EARLY_JUMP_THRESHOLD){
			stand.earlyJump = true;
			//Gdx.app.log(TAG, "early"+parent.engine.tick);
		}	
		isFinished = true;
		
			
		//Gdx.app.log(TAG, "active");
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub

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
