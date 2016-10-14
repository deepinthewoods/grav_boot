package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.niz.astar.PathNode;

public class PathResult implements Component {

	public GraphPath<Connection<PathNode>> path;

}
