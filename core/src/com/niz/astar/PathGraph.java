package com.niz.astar;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.niz.system.OverworldSystem;

public class PathGraph implements IndexedGraph<PathNode> {
	Array<Connection<PathNode>> arr = new Array<Connection<PathNode>>();
	private static final int w = OverworldSystem.SCROLLING_MAP_WIDTH , h = OverworldSystem.SCROLLING_MAP_HEIGHT;
	public PathNode[] nodes = new PathNode[w * h];
	
	public PathGraph(){
		for (int i = 0; i < nodes.length; i++){
			nodes[i] = new PathNode(i % w, i / w);
		}
	}
	
	
	@Override
	public Array<Connection<PathNode>> getConnections(PathNode fromNode) {
		return fromNode.connections;
	}

	@Override
	public int getIndex(PathNode node) {
		return node.index;
	}

	@Override
	public int getNodeCount() {
		return nodes.length;
	}

	public PathNode getNode(int x, int y){
		return nodes[x + y * w];
	}


	public static int getIndex(int x, int y) {
		return x + y * w;
		
	}
}
