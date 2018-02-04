package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BinaryHeap;
import com.niz.Main;

public class Light extends BinaryHeap.Node implements Component {
	public static final int CHARACTER_SPRITES_LAYER_LEFT = 0, CHARACTER_SPRITES_LAYER_RIGHT = 1, MAP_BACK_LAYER = 2
			, MAP_FRONT_LAYER = 3, MAP_LIT_LAYER = 4, MAP_FOREGROUND_LAYER = 5, ITEM_LAYER = 6;
	public static final int MAX_LAYERS = 7;

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
			new Vector3(.4f, 3f, 20f)//character l
			, new Vector3(.4f, 3f, 20f)//character r
			, new Vector3(.94f, 9f, 10f)//map back layer
			, new Vector3(.14f, .3f, 5f)//map front
			, new Vector3(.4f, 3f, 20f)//map lit
			, new Vector3(.4f, 3f, 20f)//map fg
			, new Vector3(.4f, 3f, 20f)//map fg
			}
	;

	public float[] yOffset = {Main.PX*2, Main.PX*2, Main.PX*16, Main.PX*2, Main.PX*2, Main.PX*2, Main.PX*20};

	public float[] ambientIntensity = {.03f, .03f, .03f, .3f, .03f, .03f, 1f};

}
