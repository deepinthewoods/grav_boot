package com.niz.astar;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class PathNode {

	public int x;
	public int y;
	public int index;
	public Array<Connection<PathNode>> connections = new Array<Connection<PathNode>>();

	public PathNode(int x, int y) {
		this.x = x;
		this.y = y;
	}


}
