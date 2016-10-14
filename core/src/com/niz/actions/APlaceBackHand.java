package com.niz.actions;

import com.niz.component.SpriteAnimation;

public class APlaceBackHand extends APlace {

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
