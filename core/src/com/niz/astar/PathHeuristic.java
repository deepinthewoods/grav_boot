package com.niz.astar;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.niz.astar.PathNode;

public class PathHeuristic implements Heuristic<PathNode> {

	@Override
	public float estimate(PathNode na, PathNode nb) {
		
		return (Math.max((float)(Math.abs(na.x - nb.x)) , Math.abs(na.y - nb.y))) / 20f;//manhattan distance
	}

}