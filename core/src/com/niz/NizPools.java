package com.niz;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pool;

public class NizPools {
public static Pool<Vector2> vec2 = new Pool<Vector2>(){

	@Override
	protected Vector2 newObject() {
		// TODO Auto-generated method stub
		return new Vector2();
	}
	
};

public static Pool<LongArray> bucket = new Pool<LongArray>(){

	@Override
	public void free(LongArray object) {
		object.clear();
		super.free(object);
	}

	@Override
	protected LongArray newObject() {
		// TODO Auto-generated method stub
		return new LongArray();
	}
	
};
}
