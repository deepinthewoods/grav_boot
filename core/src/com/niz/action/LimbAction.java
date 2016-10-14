package com.niz.action;

import com.niz.component.SpriteAnimation;

public abstract class LimbAction extends Action {
	public int limb;

	@Override
	public void update(float dt) {
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
	}

	public int getLanes() {
		
		return 0;
	}
	
	public abstract int getLimbIndex(SpriteAnimation anim);

	public abstract int getGuideLayer(SpriteAnimation anim);
	
}
