package com.niz.actions;

import com.niz.component.SpriteAnimation;

public class ADestroyFrontHand extends ADestroy {

	@Override
	public int getLimbIndex(SpriteAnimation anim) {

		return anim.front_arm;
	}

	@Override
	public int getGuideLayer(SpriteAnimation anim) {

		return anim.hand_front_g;
	}

}
