package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class SliderNiz extends Slider {

	public SliderNiz(float min, float max, float stepSize, boolean vertical,
			Skin skin) {
		super(min, max, stepSize, vertical, skin);
		// TODO Auto-generated constructor stub
		
	}
	
	public int cutOff;
	
	@Override
	public Actor hit (float x, float y, boolean touchable) {
		if (touchable && this.getTouchable() != Touchable.enabled) return null;
		if ( cutOff == MomentaryButton.CUTOFF_TOP_LEFT){
			y = getHeight() - y;
			float len = Math.min(getWidth(), getHeight())/2;
			if (x + y < len) return null;
		} else if ( cutOff == MomentaryButton.CUTOFF_TOP_RIGHT){
			y = getHeight() - y;
			x = getWidth() - x;
			float len = Math.min(getWidth(), getHeight())/2;
			if (x + y < len) return null;
		}
		
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
	}
}
