package com.niz.actions;

import com.badlogic.gdx.Gdx;
import com.niz.action.Action;
import com.niz.component.SpriteAnimation;

public class AThrowFrontHand extends AThrow {

	@Override
	public int getLimbIndex(SpriteAnimation anim) {
		//Gdx.app.log("action", "throwfds"+anim.front_arm);
		return anim.front_arm;
	}
	
	

	@Override
	public int getGuideLayer(SpriteAnimation anim) {
		
		return anim.hand_front_g;
	}
	

}
