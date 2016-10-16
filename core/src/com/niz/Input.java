package com.niz;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.sun.org.apache.bcel.internal.classfile.Code;
/**
 * Created by niz on 29/06/2014.
 */
public class Input {

	public static final int JUMP = 0, WALK_LEFT = 1, WALK_RIGHT = 2;
	public static final int SCREEN_TOUCH = 3;;
	public static final int MAX_PLAYERS =4
			;
	public static final int NUMBER_OF_BUTTONS = 4;
	
    public static IntIntMap[] keys = new IntIntMap[MAX_PLAYERS];




    static {
    	for (int i = 0; i < MAX_PLAYERS; i++){
    		keys[i] = new IntIntMap();
    	}

       
        keys[0].put(Keys.W, JUMP);
        keys[0].put(Keys.A, WALK_LEFT);
        keys[0].put(Keys.D, WALK_RIGHT);
        
}
}
