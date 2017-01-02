package com.niz.actions;

import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.math.MathUtils;
import com.niz.Blocks;
import com.niz.action.Action;
import com.niz.system.OverworldSystem;

public class APathFindingDestroyMap extends Action {

	private float time;

	@Override
	public void update(float dt) {
		time += dt;
		if (time > .15f){
			isFinished = true;
			OverworldSystem map = parent.engine.getSystem(OverworldSystem.class);
			for (int i = 0; i < AStar.PATHFINDING_X_START+1; i++){
				int x = i;
				int y = AStar.PATHFINDING_INITIAL_Y_OFFSET-1;
				map.getMapFor(x,  y).set(x, y, 0);;
			}
			for (int i = 0; i < AStar.PATHFINDING_WALL_HEIGHT; i++){
				map.getMapFor(0,  0).set(0,  i + AStar.PATHFINDING_INITIAL_Y_OFFSET, 0);
			}
		}
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		time = 0f;
	}

}
