package com.niz.actions.mapgen;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.niz.WorldDefinition;
import com.niz.action.Action;
import com.niz.component.Map;

public class AGenerateEntities extends Action {

	public int x, y, w, h;
	public Map map;
	public WorldDefinition def;
	private int progress;
	private int progressTarget;
	private GridPoint2 pt = new GridPoint2();
	@Override
	public void update(float dt) {
		pt.set(MathUtils.random(0, w-1)+x, MathUtils.random(0, h-1)+y);
		if (++progress >= progressTarget) isFinished = true;
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		progress = 0;
		progressTarget = 10;
	}

}
