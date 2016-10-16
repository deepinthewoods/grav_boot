package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class CameraControl implements Component {

	public Vector2 target = new Vector2(), offset = new Vector2();

	public void setOffset(int w, int h) {
		offset.add(w, h);
		
	}

}
