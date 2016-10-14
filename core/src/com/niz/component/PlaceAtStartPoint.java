package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PlaceAtStartPoint implements Component, Poolable {
public float delay;
public int index;

@Override
public void reset() {
	delay = 0f;
	
}

}
