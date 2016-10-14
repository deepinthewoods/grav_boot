package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.niz.action.LimbAction;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;

public class ADoNothing extends LimbAction {
	static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);

	@Override
	public int getLimbIndex(SpriteAnimation anim) {
		
		return 0;
	}

	@Override
	public int getGuideLayer(SpriteAnimation anim) {
		
		return 0;
	}

	@Override
	public void update(float dt) {
		
		super.update(dt);
		isFinished = true;
	}

	@Override
	public void onEnd() {
		Race race = raceM.get(parent.e);
		race.limbTotals[limb]--;
		super.onEnd();
	}

	@Override
	public void onStart() {
		
		super.onStart();
	}

	
	
}
