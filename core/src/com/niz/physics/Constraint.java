package com.niz.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Constraint implements Poolable{
	public Vector2 src;
	public Vector2 dst;
	public int id;
	public float strength;
	public float restLength;
	@Override
	public void reset() {
		src = null;
		dst = null;
		
	}
	public Constraint set(Vector2 src, Vector2 dst, int id, float strength, float restLength){
		this.src = src;
		this.dst = dst;
		this.id = id;
		this.strength = strength;
		this.restLength = restLength;
		return this;
	}
}
