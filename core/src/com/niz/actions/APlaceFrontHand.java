package com.niz.actions;

import com.niz.component.SpriteAnimation;

public class APlaceFrontHand extends APlace {

	@Override
	public int getLimbIndex(SpriteAnimation anim) {
		//Gdx.app.log("action", "throwback"+ anim.back_arm);
		return anim.front_arm;
	}
	

	

	

	@Override
	public int getGuideLayer(SpriteAnimation anim) {
		
		return anim.hand_front_g;
	}

}
