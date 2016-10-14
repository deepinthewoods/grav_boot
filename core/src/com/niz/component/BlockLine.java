package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BlockLine implements Component, Poolable {

	public Vector2 end = new Vector2();

	@Override
	public void reset() {
		end.set(-1000, -1000);
		
	}

}
