package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;
import com.niz.Data;
import com.niz.action.Action;
import com.niz.anim.AnimationContainer;
import com.niz.anim.Animations;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;

public class ATailControl extends Action {
	private static final String TAG = "tiaal contorl action";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	private static  ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	@Override
	public void update(float dt) {
		
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		
		
	}

}
