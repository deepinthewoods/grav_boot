package com.niz;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.system.OverworldSystem;

public class LayerData {
	public int[] heights = new int[OverworldSystem.SCROLLING_MAP_TOTAL_SIZE+2];
	
	
	public void setOverworld(int layerIndex, int nextInt) {
		//if (heights.length < 3) throw new GdxRuntimeException("jdskl");

		for (int i = 0; i < heights.length; i++){
			heights[i] = MathUtils.random(64);
		}
		for (int i = 0; i < heights.length/4; i++){
			float alpha = i / (heights.length/4-1);
			heights[i] = (int) MathUtils.lerp(0, heights[i], alpha);
			heights[heights.length - i - 1] = (int) MathUtils.lerp(0, heights[heights.length - i - 1], alpha);
		}
		
		
	}

	public void displaceFrom(int layerIndex, LayerData from, Random r){
		//if (heights.length < 3) throw new GdxRuntimeException("jdskl");
		for (int i = 0; i < heights.length; i++){
			heights[i] = from.heights[i];
			heights[i] += r.nextInt(64) - 32;
			heights[i] = Math.max(1,  heights[i]);
			
		}
	}
	
	
}
