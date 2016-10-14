package com.niz;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.niz.system.OverworldSystem;

public class LayerData {
	public int[] heights = new int[OverworldSystem.SCROLLING_MAP_TOTAL_SIZE+1];
	
	
	public void setOverworld(int layerIndex, int nextInt) {
		for (int i = 0; i < heights.length; i++){
			heights[i] = 128;
		}
		for (int i = 0; i < heights.length/4; i++){
			float alpha = i / (heights.length/4-1);
			heights[i] = (int) MathUtils.lerp(0, heights[i], alpha);
			heights[heights.length - i - 1] = (int) MathUtils.lerp(0, heights[heights.length - i - 1], alpha);
		}
		
		
	}

	public void displaceFrom(int layerIndex, LayerData from, Random r){
		
		for (int i = 0; i < heights.length; i++){
			heights[i] = from.heights[i];
			heights[i] += r.nextInt(8) - 8;
			heights[i] = Math.max(1,  heights[i]);
			
		}
	}
	
	
}
