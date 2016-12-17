package com.niz.astar;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.MathUtils;

public class PathConnection<N> implements Connection<N> {
	public N from, to;
	public float cost;
	public int key;
	@Override
	public float getCost() {
		return cost + MathUtils.random(.05f);
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

	public String toString(){
		return ""+ getClass().getSimpleName() +"from: " + from + "\n" + 
				"to " + to + "\n" +  
				" cost " + cost;
	}
	
}
