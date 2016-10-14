package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.NizPools;

public class Position implements Component, Poolable{
	public Vector2 pos = new Vector2();
	
	@Override
	public void reset() {
		
	}
	

}
