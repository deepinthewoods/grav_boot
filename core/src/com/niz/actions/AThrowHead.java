package com.niz.actions;

import com.niz.action.Action;
import com.niz.component.SpriteAnimation;

public class AThrowHead extends AThrow {

	@Override
	public int getLimbIndex(SpriteAnimation anim) {
		
		return anim.head;
	}

	@Override
	public int getGuideLayer(SpriteAnimation anim) {
		
		return -1;
	}

	

}