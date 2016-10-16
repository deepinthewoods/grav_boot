package com.niz.component;

import com.badlogic.ashley.core.Component;

public class CollisionComponent implements Component {

	public static final int RIGHT = 0, LEFT = 1, TOP = 2, BOTTOM = 3;
	public boolean disabled, onSlope;
	public int side;
	public int block;
	
}
