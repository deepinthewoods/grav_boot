package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BinaryHeap;
import com.niz.Main;

public class Light extends BinaryHeap.Node implements Component {
	public static final int CHARACTER_SPRITES_LAYER_LEFT = 0, CHARACTER_SPRITES_LAYER_RIGHT = 1, MAP_BACK_LAYER = 2
			, MAP_FRONT_LAYER = 3, MAP_LIT_LAYER = 4, MAP_FOREGROUND_LAYER = 5;

	/**
	 * 
	 */
	//public Vector3 falloff = new Vector3(.053f, .13f, 35f);
	
	public Light() {
		super(1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * world position of light
	 */
	public Vector3 position = new Vector3(.5f,.5f, 0);

	/**
	 * maximum number of squares this light cann affect
	 */
	public int range = 8;
	//public Vector3[] ambientColor ={ new Vector3(1f, 1f, 1f), new Vector3(.007f, .007f, .007f), new Vector3(.01f, .01f, .01f)};
	/**
	 * light intensities for point light and ambient
	 */

	public boolean isOn = true;

	public Vector3[] falloff = {
			new Vector3(.17953f, .00093f, 20f)
			, new Vector3(.17953f, .00093f, 20f)
			, new Vector3(.4f, 3f, 20f)//map back layer
			, new Vector3(.17953f, .00093f, 20f)//map front
			, new Vector3(.17953f, .00093f, 20f)//map lit
			, new Vector3(.17953f, .00093f, 20f)}//map fg
	;
	public float[] yOffset = {Main.PX*2, Main.PX*2, Main.PX*4.5f, Main.PX*2, Main.PX*2, Main.PX*2};

	public float[] ambientIntensity = {0f, 0f, 0f, .3f, .3f, .3f};

}
