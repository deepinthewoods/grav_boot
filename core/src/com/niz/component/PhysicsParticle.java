package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PhysicsParticle implements Component{

	public int index;
	public Vector2 oldPos = new Vector2();
	public Vector2 acc = new Vector2();
	public Vector2 limit = new Vector2();;

}
