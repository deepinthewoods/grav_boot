package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.Input;

public class InputDigest implements Component {
	private static final String TAG = "input digest c ";
	public long startTick;
	public int[] pressed = new int[Input.NUMBER_OF_BUTTONS];
	public boolean isPressed(int d, long tick) {
		int line = pressed[d];
		long offset = tick - startTick;
		Gdx.app.log(TAG, "offset"+offset);
		if (offset < 0) throw new GdxRuntimeException("offset < 0");
		if (offset > 31) throw new GdxRuntimeException("offset > 32");
		line >>= offset;
		line &= 1;
		return line != 0;
	}
	public boolean isEmpty(long tick) {
		long offset = tick - startTick;
		return offset < 0 || offset >= 32;
	}
	
	public void add(Control control, long tick) {
		long offset = tick - startTick;
		if (offset < 0) throw new GdxRuntimeException("offset < 0");
		if (offset > 31) throw new GdxRuntimeException("offset > 32");
		int line = 1 << offset;
		int notline = line ^ 0;
		Gdx.app.log(TAG, "offset"+offset);
		for (int i = 0; i < control.pressed.length; i++){
			if (!control.pressed[i]){
				pressed[i] &= notline;
				
			} else{ 
				pressed[i] |= line;
			}
			
		}
		
		
	}
	public void reset(long newStartTick) {
		for (int i = 0; i < pressed.length; i++){
			pressed[i] = 0;
		}
		startTick = newStartTick;
		
	}

}
