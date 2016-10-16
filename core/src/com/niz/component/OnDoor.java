package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pool.Poolable;

public class OnDoor implements Component, Poolable {

	public LongArray doors = new LongArray(true, 1);

	@Override
	public void reset() {
		doors.clear();		
	};

}
