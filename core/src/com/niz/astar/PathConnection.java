package com.niz.astar;

import com.badlogic.gdx.ai.pfa.Connection;

public abstract class PathConnection<N> implements Connection<N> {
	public N from, to;
	public float cost;
	@Override
	public float getCost() {
		
		return cost;
	}

	@Override
	public N getFromNode() {
		
		return from;
	}

	@Override
	public N getToNode() {
		// TODO Auto-generated method stub
		return to;
	}

	

}
