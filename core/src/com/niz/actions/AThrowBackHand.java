package com.niz.actions;

import com.badlogic.gdx.Gdx;
import com.niz.action.Action;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;

public class AThrowBackHand extends AThrow {

	@Override
	public int getLimbIndex(SpriteAnimation anim) {
		//Gdx.app.log("action", "throwback"+ anim.back_arm);
		return anim.back_arm;
	}
	

	

	

	@Override
	public int getGuideLayer(SpriteAnimation anim) {
		
		return anim.hand_back_g;
	}
	
	
	

}
