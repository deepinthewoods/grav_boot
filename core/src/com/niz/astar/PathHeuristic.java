package com.niz.astar;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.niz.astar.PathNode;

public class PathHeuristic implements Heuristic<PathNode> {

	@Override
	public float estimate(PathNode na, PathNode nb) {
		
		return Math.abs(na.x - nb.x) + Math.abs(na.y - nb.y);//manhattan distance
	}

}
