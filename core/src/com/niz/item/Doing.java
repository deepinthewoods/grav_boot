package com.niz.item;

import com.niz.action.Action;

/**
 * @author Niall Quinlan
 *
 * 
 */
public class Doing {
	public static final int ARROW_PLACE = 0, ARROW_THROW = 1;
	public Doing(String string, int limb, int doingType) {
		limbIndex = limb;
		doingTypeIndex = doingType
				;
		name = string;
	}
	
	public Doing(String string, int limb, int doingType, int arrow) {
		this(string, limb, doingType);
		arrowType = arrow;
	}
		
	
	public Doing(String string) {
		this(string, 0, 0);
	}
	
	public String name;
	
	//public Class<? extends Action> onTouchDown;
	//TODO result for ai planning
	public int limbIndex;
	public int doingTypeIndex;

	public int arrowType;
	
	public static final int TYPE_THROW = 0, TYPE_PLACE = 1;

	public static final int TYPE_NOTHING = 2;

	public static final int TYPE_SLASH = 3, TYPE_THRUST = 4;

	public static final int TYPE_DESTROY = 5;

}
