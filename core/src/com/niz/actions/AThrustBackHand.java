package com.niz.actions;

import com.niz.component.SpriteAnimation;

public class AThrustBackHand extends AThrust {

	@Override
	public int getLimbIndex(SpriteAnimation anim) {

		return anim.back_arm;
	}

	@Override
	public int getGuideLayer(SpriteAnimation anim) {

		return anim.hand_back_g;
	}

}
