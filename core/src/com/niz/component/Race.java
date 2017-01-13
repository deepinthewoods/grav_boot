package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.action.Action;
import com.niz.action.LimbAction;

public class Race implements Component, Poolable {
	public static final int BACK_ARM = 0, FRONT_ARM = 1, TAIL = 2, HEAD = 3, TORSO = 4
			, NECK = 5, BACK_LEG = 6, FRONT_LEG = 7
			, TOTAL_BODY_PARTS = 8;
	public static final int[] LIMB_LANES = {Action.LANE_BACK_ARM, Action.LANE_FRONT_ARM, Action.LANE_TAIL, Action.LANE_NECK};
	public static final int HUMAN = 0, RED_DRAGON = 1, GREEN_DRAGON = 2, WHITE_DRAGON = 3;
	
	public static final int LIMB_BACK_HAND = 0, LIMB_FRONT_HAND = 1, LIMB_TAIL = 2, LIMB_HEAD = 3;
	public static final int TOTAL_RACES = 2;
	public static final int PHYSICS_NOrMAL = 0, PHYSICS_RUNNER = 1;
	public static final String[] raceNames = {"Human", "Red Dragon", "Green Dragon", "White Dragon"}
	
	, limbNames = {"Back Arm", "Front Arm", "Tail", "Head", "Torso", "Neck", "Back Leg", "Front Leg"}
	;
	public static Class<? extends LimbAction>[][] limbThrowActions;
	
	public int[] limbTotals = new int[TOTAL_BODY_PARTS], oldLimbTotals = new int[TOTAL_BODY_PARTS];;
	public int[] raceID = new int[TOTAL_BODY_PARTS];
	public boolean[] enabledLimb = {true, true, false, false};
	
	//public boolean[] dirtyLimb = new boolean[TOTAL_BODY_PARTS];
	public boolean dirtyLayers;
	public int physicsID;
	

	public void setLimbActions(Class<? extends LimbAction>[][] actions){
		limbThrowActions = actions;
	}
	
	@Override
	public void reset(){
		dirtyLayers = false;
	}

}
