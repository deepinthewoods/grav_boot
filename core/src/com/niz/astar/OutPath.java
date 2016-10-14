package com.niz.astar;

import java.util.Iterator;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class OutPath implements GraphPath<Connection<PathNode>>{

	private Array<Connection<PathNode>> connections = new Array<Connection<PathNode>>(true, 32);

	@Override
	public Iterator<Connection<PathNode>> iterator() {
		return connections.iterator();
	}

	@Override
	public int getCount() {
		return connections.size;
	}

	@Override
	public Connection<PathNode> get(int index) {
		
		return connections.get(index);
	}

	@Override
	public void add(Connection<PathNode> node) {
		connections.add(node);
	}

	@Override
	public void clear() {
		while (connections.size > 0){
			Pools.free(connections.pop());
		}
		connections.clear();
	}

	@Override
	public void reverse() {
		connections.reverse();
	}
	
	

}
