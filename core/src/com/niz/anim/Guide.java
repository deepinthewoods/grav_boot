package com.niz.anim;

import com.badlogic.gdx.math.Vector2;

public class Guide {

	public Vector2[] offsets;
	public float[] angles;

	public Guide(Vector2[] offsets, float[] angles) {
		this.offsets = offsets;
		this.angles = angles;
	}

	public Guide() {
		// TODO Auto-generated constructor stub
	}

	public void set(Guide g){
		offsets = g.offsets;
		angles = g.angles;
	}
}
