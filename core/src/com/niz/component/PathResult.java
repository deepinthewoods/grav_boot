package com.niz.component;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.astar.PathNode;

public class PathResult implements Component, Poolable {

	public GraphPath<Connection<PathNode>> path;

	@Override
	public void reset() {
		path = null;
	}

	public String toString(){
		String s = "";
		Iterator<Connection<PathNode>> i = path.iterator();
		while (i.hasNext()){
			Connection<PathNode> c = i.next();
			s += c;
			
		}
		
		return s;
	}
}
