package com.niz.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

public class FloatSlider extends Slider {

	private FloatButton b;

	public FloatSlider(
			Skin skin) {
		super(0f, 1f, .001f, true, skin);
		
	}

	public void set(float min, float max, FloatButton b) {
		this.setRange(min, max);
		this.b = b;
		
	}
	

}
