package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.niz.action.Action;
import com.niz.component.Position;
import com.niz.system.OverworldSystem;

public class ALoopAroundMap extends Action {
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	@Override
	public void update(float dt) {
		Vector2 pos = posM.get(parent.e).pos;
		int w = OverworldSystem.SCROLLING_MAP_WIDTH * OverworldSystem.SCROLLING_MAP_TOTAL_SIZE;
		if (pos.x > w){
			pos.x -= w;
		}
		if (pos.x < 0){
			pos.x += w;
		}
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
	}

}
