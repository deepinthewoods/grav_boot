package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.Data;
import com.niz.anim.AnimSet;
import com.niz.anim.AnimationContainer;
import com.niz.anim.AnimationLayer;
import com.niz.anim.Animations;
import com.niz.anim.Guide;
import com.niz.anim.LayerGuide;
import com.niz.anim.RotatedAnimationLayer;

public class SpriteAnimation implements Component, Poolable{
private static final String TAG = "sprite animation";
public static AnimSet anims = Animations.anim.get(Animations.PLAYER);
public Bits overriddenAnim = new Bits(), overriddenGuides = new Bits();


public transient AnimationContainer[] overriddenAnimationLayers = new AnimationContainer[Animations.TOTAL_ANIMS];
public int[] overriddenAnimationLayerIDs = new int[16], overriddenGuideLayerIDs = new int[16];
public transient Guide[] overriddenGuideLayers = new Guide[16];
public boolean[] isAngleFlipGuideLayer = new boolean[16], isAngleFlipLayer = new boolean[16];;
public int current;
public transient AnimationContainer currentAnim = new AnimationContainer();
public float[] time = new float[Animations.TOTAL_ANIMS];
public transient boolean hasStarted = false;
//private int hash;
public transient Array<LayerGuide> guides = new Array<LayerGuide>();
public int[] guideFrameSources;// = {0, 2, 2, 2, 1, 1, 1, 2};//animation layer that the guide takes its's frame from

public int[] guideSources;// = {-1, 0, 1, 2, 1, 4, 4, 1};//refers to the other guides it inherits position from

public int[] layerSources;// = {Animations.ARMS_G, Animations.LEGS_G, Animations.TORSO_G, Animations.LEGS_G, 
		//Animations.HEAD_G, Animations.ARMS_G};//guide layer that layers use to render
public int[] frameIndices = new int[Animations.TOTAL_ANIMS];
public int[] colors = new int[16], colorFX = new int[16];
public transient boolean hasMoved;
public transient int lastDrawnHash;
public transient Vector3 position = new Vector3();;
public boolean left, wasLeft;
public int prevBitmask;
public transient IntArray layerIDs = new IntArray(), guideIDs = new IntArray();

public int front_arm, back_arm, front_leg, back_leg, torso, neck, head, tail;
public int back_item, front_item, tail_item, head_item;
public float[] angles = new float[Animations.TOTAL_ANIMS];
public int legs_g ,tail_g ,hand_front_g ,hand_back_g ,arms_g ,head_g ,neck_g ,torso_g;
public final int[] itemLayersByLimbIndex = new int[4], guideLayersByLimbIndex = new int[4];
public boolean[] adjustedLeft = new boolean[Animations.TOTAL_ANIMS];
public int tail_tip_g;
public boolean alignWithBodyBottom = true;
static IntArray tmpLayerIDs = new IntArray(), tmpGuideIDs = new IntArray();

public String toString(){
	String s = "";

	return s;
}


	public SpriteAnimation(){
		reset();
	}

	public Component set(int id){
		
		return this;
	}
	
	public void start(int animID){
		for (int i = 0; i < frameIndices.length; i++){
			//frameIndices[i] = 9012920;
		}
		if (anims.anims.containsKey(animID)){
			prevBitmask = currentAnim.bitmask;
			current = animID;
			Array<AnimationContainer> arr = anims.anims.get(animID);
			currentAnim.set(arr.get(0), layerIDs, guideIDs);
			//Gdx.app.log(TAG, "start anim " + Data.getString(animID));
			{
				
				if ((currentAnim.bitmask & prevBitmask) != 0){
					//Gdx.app.log("", "keep time");
					for (int i = 0; i < time.length; i++){
						//time[i] = 0f;
					}				}
				else {
					for (int i = 0; i < time.length; i++){
						time[i] = 0f;
					}
				}
				
				if (currentAnim.randomStart){//random start with same bitmask
					//Gdx.app.log(TAG, "random start anim");
					for (int i = 0; i < time.length; i++){
						//time[i] = MathUtils.random(currentAnim.getAnimationDuration());

					}
				}
				
			}
			hasStarted = true;
			
		}
		//Gdx.app.log(TAG, "start anim"+overriddenAnim.nextSetBit(1));
	}
	
	public AnimationContainer overrideSpriteForLayer(int layerID , RotatedAnimationLayer layer){
		//Gdx.app.log(TAG, "overide sprite for layer "+layerID);
		//if (layerID == back_arm) throw new GdxRuntimeException("");
		overriddenAnim.set(layerID);
		//frameIndices[layerID] = -1;
		if (overriddenAnimationLayers[layerID] == null){
			overriddenAnimationLayers[layerID] = new AnimationContainer();
		}
		overriddenAnimationLayerIDs[layerID] = -1;
		overriddenAnimationLayers[layerID].set(layerID, layer);
		return overriddenAnimationLayers[layerID];
	}
	
	public AnimationContainer overrideAnimationForLayer(int index, int animID){
		//if (index == back_arm) throw new GdxRuntimeException("");
		//Gdx.app.log(TAG, "overide anim for layer "+index);
		if (anims.anims.containsKey(animID)){
			overriddenAnim.set(index);
			//frameIndices[index] = -1;

			if (overriddenAnimationLayers[index] == null){
				overriddenAnimationLayers[index] = new AnimationContainer();
			}
			AnimationContainer other = anims.anims.get(animID).get(0);
			
			tmpLayerIDs.clear();
			tmpLayerIDs.add(layerIDs.get(index));
			//
			//Gdx.app.log(TAG	, "override layer, id:"+layerIDs.get(index));
			tmpGuideIDs.clear();
			//tmpGuideIDs.add(guideIDs.get(this.layerSources[layer]));
			;
			overriddenAnimationLayers[index].set(other, tmpLayerIDs, tmpGuideIDs);;
			//time[index] = 0f;
			
			if ((currentAnim.bitmask & prevBitmask) != 0){
				//Gdx.app.log("", "keep time");
			}
			else {				
				time[index] = 0f;
			}
			//Gdx.app.log(TAG, "override anim layer "+layer);
			overriddenAnimationLayerIDs[index] = animID;
			return overriddenAnimationLayers[index];
		}
		return null;
	}
	
	public void disableAnimationOverride(int index){
		//frameIndices[index] = -1;
		overriddenAnim.clear(index);
		//overriddenAnimationLayers[index] = null;
		time[index] = time[back_leg];
	}
	
	public void overrideGuide(int index, int animID){
		//frameIndices[guideFrameSources[index]] = -1;
		overriddenGuides.set(index);
		
		if (overriddenGuideLayers[index] == null){
			overriddenGuideLayers[index] = new Guide();
		}
		AnimationContainer other = anims.anims.get(animID).get(0);

		overriddenGuideLayers[index].set(other.getGuide(guideIDs.get(index)));;
		overriddenGuideLayerIDs[index] = animID;
	}
	

	@Override
	public void reset() {
		hasStarted = false;
		layerIDs.clear();
		guideIDs.clear();
		alignWithBodyBottom = true;
		for (int i = 0; i < frameIndices.length; i++){
			frameIndices[i] = 0;
		}
		currentAnim.clear();
		currentAnim.bitmask = 0;
		//while (guides.size > 0)
			//Pools.free(guides.pop());
		for (int i = 0; i < time.length; i++){
			time[i] = 0f;
		}
		overriddenAnim.clear();
		overriddenGuides.clear();
		
	}
	
	
	public void updateGuides(int smallest, boolean left) {
		
		for (int i = Math.max(1,  smallest); i < guides.size; i++){
			Guide g = null;
			int total = 0;
			//if (){
			if (overriddenGuides.get(i)){
				g = overriddenGuideLayers[i];

				Gdx.app.log(TAG, "OVERRIDDEN");
			} else {
				g = currentAnim.guides.get(i);
			}
			/*if (currentAnim.layers.get(guideFrameSources[i]) != null)
				Gdx.app.log(TAG, "g "+currentAnim.layers.get(guideFrameSources[i]).getNumberOfFrames() +
                "  " + Data.getString(layerIDs.get(guideFrameSources[i]))

                );//*/
			//total = g.offsets.length;
			//} else {
				
			//}
			LayerGuide actual = guides.get(i);
			int frame = frameIndices[guideFrameSources[i]];

			actual.set(guides.get(guideSources[i]));
			boolean adjLeft = left;
			if (isAngleFlipGuideLayer[i]){
				adjLeft = !(angles[guideFrameSources[i]] < 90 || angles[guideFrameSources[i]] > 270);
					//adjLeft = true ;
				adjLeft = adjLeft == left;
				//adjLeft = !adjLeft;
				//Gdx.app.log(TAG, "FLPI0 "+angles[guideFrameSources[i]]);
				if (!left) adjLeft = !adjLeft;;
			}
			if (adjLeft){
				//Gdx.app.log(TAG, "ADJJJKDSFJSDLKDJSFAKL");
			}
			if (adjLeft) {
				actual.add(-g.offsets[frame].x, g.offsets[frame].y);
				actual.rotation = (540 - g.angles[frame])%360;
			}
			else {
				//Gdx.app.log(TAG, "RRRRRRRRRRR "+Data.getString(guideIDs.get(i)) + " framesrc:"+Data.getString(layerIDs.get(guideFrameSources[i]))+overriddenGuides.get(i)+g.offsets.length);
				/*if (g == null || frame >= g.offsets.length)Gdx.app.log(TAG, "RRRRRRRRRRR "+Data.getString(guideIDs.get(i)) + "  " + frame + " hash:"+guideIDs.get(i)
						+ Animations.guides.containsKey(guideIDs.get(i)) + (currentAnim.getGuide(i) == null)
						+ "  guideT "+currentAnim.guides.size + " / " + i + "  "
						+ currentAnim
				);//*/
				if (frame >= total){
					//Gdx.app.log(TAG, "frame index too big");
					//frame %= total;
					//frameIndices[guideFrameSources[i]] = frame;
				}
				actual.add(g.offsets[frame]);

				actual.rotation = (360 + 180 - g.angles[frame]) % 360;
				
				//actual.rotation = g.angles[frame];
				//Gdx.app.log(TAG, "guide "+i+" source:"+guideSources[i] + "  actual:"+g.offsets[frame] + "  angle:"+g.angles[frame]);
			}
			
		}
	}

	public void disableGuideOverride(int index) {
		overriddenGuides.clear(index);
		time[index] = time[back_leg];

		//frameIndices[index] = -1;
	}

	public void switchItemLimbToAngleFlipped(int limb) {
		int item_layer_index = itemLayersByLimbIndex[limb];

		//int itemID = inv.getItemIDByLimb(limb);
		//AnimationContainer layer = overrideSpriteForLayer(item_layer_index, Animations.weaponLayers[itemID]);
		//AnimationContainer layer = getLayer(this.itemLayersByLimbIndex[limb]));
		//getLayer(itemLayersByLimbIndex[limb]).angleDependantFlip = true;
		isAngleFlipLayer[item_layer_index] = true;
		isAngleFlipGuideLayer[guideLayersByLimbIndex[limb]] = true;
		//frameIndices[item_layer_index] = -1;
	}
	public void switchLayerToAngleFlipped(int index){
		isAngleFlipLayer[index] = true;
		//frameIndices[index] = -1;
	}
	
	public void resetItemLimbAngleFlipped(int limb){
		int item_layer_index = itemLayersByLimbIndex[limb];
		//frameIndices[item_layer_index] = -1;
		//int itemID = inv.getItemIDByLimb(limb);
		//AnimationContainer layer = overrideSpriteForLayer(item_layer_index, Animations.weaponLayers[itemID]);
		//AnimationContainer layer = getLayer(this.itemLayersByLimbIndex[limb]));
		//getLayer(itemLayersByLimbIndex[limb]).angleDependantFlip = true;
		isAngleFlipLayer[item_layer_index] = false;
		isAngleFlipGuideLayer[guideLayersByLimbIndex[limb]] = false;
	}
	
	public void resetLayerAngleFlipped(int index){
		//frameIndices[index] = -1;
		isAngleFlipLayer[index] = false;
	}

	public AnimationLayer getLayer(int i) {
		if (overriddenAnim.get(i)){
			return overriddenAnimationLayers[i].layers.get(0);
		}
		return currentAnim.layers.get(0);
	}

	public LayerGuide getGuide(int i) {
		
		return guides.get(i);
	}

	public void initFor(RotatedAnimationLayer layer, float rotation) {
		int id = 0;
		currentAnim.addLayer(0, null);
		layerIDs.clear();
		guideIDs.clear();
		layerIDs.add(0);
		guideIDs.add(0);
		
		if (guideFrameSources == null) guideFrameSources = new int[1];
		guideFrameSources[0] = 0;;// = {0, 2, 2, 2, 1, 1, 1, 2};//animation layer that the guide takes its's frame from
		if (guideSources == null) guideSources = new int[1];
		guideSources[0] = 0;// = {-1, 0, 1, 2, 1, 4, 4, 1};//refers to the other guides it inherits position from
		if (layerSources == null) layerSources = new int[1];
		layerSources[0] = 0;
		
		this.overrideSpriteForLayer(0, layer);
		
		guides.clear();
		guides.add(new LayerGuide());
		guides.get(0).rotation = rotation;
		//if (layer.angleDependantFlip) guides.get(0).rotation = (180 - guides.get(0).rotation + 360)%360;
		
		hasStarted = true;
		//this.adjustedLeft[0] = true;
		//this.isAngleFlipGuideLayer[0] = true;
		
	}

	public void syncAllTimesWithLayer(int layerIndex) {
		for (int i = 0; i < time.length; i++){
			time[i] = time[layerIndex];
		}
	}

	public int getLimbIndex(int i) {
		switch (i){
		case 0: return back_arm;
		case 1 : return front_arm;
		case 2: return tail;
		case 3: return neck;
		default: return torso;
		}
		
	}

	public void resetAllTimes() {
		for (int i = 0; i < time.length; i++){
			time[i] = 0f;
			
		}
	}

	public void resume() {
		Array<AnimationContainer> arr = Animations.anim.get(Animations.PLAYER).anims.get(current);
		if (arr == null) return;//throw new GdxRuntimeException("nill");
		currentAnim.set(arr.get(0), layerIDs, guideIDs);
		//Gdx.app.log(TAG, "resume "+layerIDs + guideIDs + current);
		
		int gi = overriddenGuides.nextSetBit(0);
		while (gi != -1){
			int guideID = overriddenGuideLayerIDs[gi];
			overrideGuide(gi, guideID);
			gi = overriddenGuides.nextSetBit(gi+1);
		}
		
		int si = overriddenAnim.nextSetBit(0);
		while (si != -1){
			int animID = overriddenAnimationLayerIDs[si];
			overrideAnimationForLayer(si, animID);
			si = overriddenAnim.nextSetBit(si+1);
		}
		hasStarted = true;
	}

	
	
}
