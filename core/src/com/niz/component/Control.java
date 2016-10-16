package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.Input;

public class Control implements Component , Poolable {
	//public IntMap<Object> pressed = new IntMap<Object>();
	public boolean[] pressed = new boolean[Input.NUMBER_OF_BUTTONS];
	public Vector2 rotation = new Vector2();
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	public void clear() {
		for (int i = 0; i < pressed.length; i++)
			pressed[i] = false;
		
	}

}
