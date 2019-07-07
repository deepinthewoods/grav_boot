package com.niz.anim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.niz.Data;

public class AnimationContainer {
	private static final String TAG = "animation containewr";
	public Array<AnimationLayer> layers = new Array<AnimationLayer>();
	public Array<Guide> guides = new Array<Guide>();
	
	public boolean randomStart;
	public int bitmask;
	//public boolean drawOnMove = false;;
	public IntIntMap keyToIndex = new IntIntMap(), guideKeyToIndex = new IntIntMap();
	private int totalLayers = 0, totalGuides = 0;
	//public BitSet angleDependantFlip = new BitSet();;
	
	//public int frameSourceIndex; //index of layer in spritebatch to use for frameindex
	
	
	
	public AnimationLayer newLayer(float time, AtlasSprite[] frames) {
		// TODO Auto-generated method stub
		AnimationLayer layer = new AnimationLayer(time,frames);
		layer.flipped = layer.doFlip();
		return layer;
		
	}
	//other will have all layer ids, we're just seleting the ones teat this spriteanimj has
	public void set(AnimationContainer other, IntArray layerIDs, IntArray guideIDs){
		layers.clear();
		guides.clear();
		totalLayers = 0;
		totalGuides = 0;
		keyToIndex.clear();
		guideKeyToIndex.clear();
		//Gdx.app.log("container ","DDDDDDDDDDDD "+other.directions + "  \n");
		for (int i = 0; i < layerIDs.size; i++){
			int id = layerIDs.get(i);
			AnimationLayer layer = other.getLayer(id);
			addLayer(id, layer);
			//if (layer != null){
				//TextureRegion kf = layer.getKeyFrame(0, true);
				//if (kf != null)
					//Gdx.app.log("container ", "add layer " + kf.getRegionY()+ Data.getString(id));
				//else
					//Gdx.app.log("container ", "null blah " + Data.getString(id));
			//}
			//else Gdx.app.log("container ", "null blah " + Data.getString(id));


		}
		for (int i = 0; i < guideIDs.size; i++){
			Guide otherGuide = other.getGuide(guideIDs.get(i));
			addGuide(guideIDs.get(i), otherGuide );
		}
		
		
		randomStart = other.randomStart;
		bitmask = other.bitmask;
	}
	
	public Guide getGuide(int i) {
		int key = guideKeyToIndex.get(i, -1);
		if (key == -1) return null;//throw new GdxRuntimeException("guide not found"+i+" !   ");
		return guides.get(key);
	}
	public AnimationLayer getLayer(int i) {
		if (keyToIndex.get(i, -1) == -1) 
			return null;
			//throw new GdxRuntimeException("jkl" + i + "   \n"+keyToIndex);
		return layers.get(keyToIndex.get(i, -1));
	}
	public AnimationContainer(){//
		
		
	}
	
	
	public int addLayer(int key, AnimationLayer layer){
		/*if (layer == null && !Data.getString(key).contains("item")){
			Gdx.app.log(TAG, "null guide added" + key + " " + Data.getString(key));
			throw new GdxRuntimeException("null layer");
		}//*/
		//if (layer == null) return -1;
//		if (keyToIndex.containsKey(key)) throw new GdxRuntimeException("already contains" + Data.getString(key));
		keyToIndex.put(key, totalLayers++);
		layers.add(layer);
		
		//Gdx.app.log("container ","add "+key);
		return totalLayers-1;
	}
	public int addGuide(int key, Guide guide){
		//if (guide == null){
			//Gdx.app.log(TAG, "null guide added" + key + " " + Data.getString(key));
			//throw new GdxRuntimeException("null guide");
		//}
		guideKeyToIndex.put(key, totalGuides++);
		guides.add(guide);
		return totalGuides-1;
	}

	public float getAnimationDuration() {
		for (int i = 0; i < layers.size; i++){
			AnimationLayer layer = layers.get(i);
			if (layer != null) return layer.getAnimationDuration();
		}
		return 1f;
	}
	public void set(int id, RotatedAnimationLayer layer) {
		layers.clear();
		guides.clear();
		totalLayers = 0;
		totalGuides = 0;
		keyToIndex.clear();
		guideKeyToIndex.clear();
		//Gdx.app.log("container ","DDDDDDDDDDDD "+other.directions + "  \n");
		
		addLayer(id, layer);
		
		
		
		
		//Guide otherGuide = other.getGuide(guideIDs.get(i));
		//addGuide(guideID, layer.guide);
		
		
		
		//randomStart = other.randomStart;
		//bitmask = other.bitmask;
	}
	public void clear() {
		layers.clear();
		guides.clear();
		totalLayers = 0;
		totalGuides = 0;
		keyToIndex.clear();
		guideKeyToIndex.clear();
	}
	
	@Override
	public String toString(){
		String s = "Animation Container contents: \n";
		for (IntIntMap.Entry ent : this.keyToIndex.entries()){
			s += Data.getString(ent.key) + "\n";
		}

		return s;
	}
	

}
