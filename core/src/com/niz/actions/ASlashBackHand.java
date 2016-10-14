package com.niz.actions;

import com.niz.action.LimbAction;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;

public class ASlashBackHand extends ASlash {

	

	@Override
	public int getLimbIndex(SpriteAnimation anim) {
		
		return anim.back_arm;
	}

	@Override
	public int getGuideLayer(SpriteAnimation anim) {
		
		return anim.hand_back_g;
	}

}
