package com.niz;

import java.util.Random;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.system.ParallaxBackgroundSystem;

public class WorldDefinition {

	private static final int TOTAL_LAYERS = ParallaxBackgroundSystem.PARALLAX_LAYERS;
	public String name;
	public int seed;
	transient public FileHandle folder;
	public LayerData[] overworldLayers = new LayerData[TOTAL_LAYERS];
	public boolean[] unlockedStartingCharacters = new boolean[PlatformerFactory.CHAR_SELECT_CHARACTERS];

	transient Random r = new Random();
	public boolean isRoomEditor = false;
	public WorldDefinition(){
		unlockedStartingCharacters[0] = true;
	}
	
	
	public void set(int seed, String worldName){
		//if (true) throw new GdxRuntimeException("jdskl");

		this.seed = seed;
		name = worldName;
		r.setSeed(seed);
		
		int index = TOTAL_LAYERS / 2;
		
		LayerData layer = new LayerData();
		layer.setOverworld(index, r.nextInt());
		overworldLayers[index] = layer;
		LayerData fromLayer = layer;
		
		for (int i = index+1; i < TOTAL_LAYERS; i++){
			LayerData newLayer = new LayerData();
			newLayer.displaceFrom(i, fromLayer, r);
			overworldLayers[i] = newLayer;
			fromLayer = newLayer;
		}
		fromLayer = layer;
		for (int i = index-1, n = 0; i >= 0; i--, n++){
			LayerData newLayer = new LayerData();
			newLayer.displaceFrom(i, fromLayer, r);
			overworldLayers[i] = newLayer;
			fromLayer = newLayer;
		}
		
	}

}
