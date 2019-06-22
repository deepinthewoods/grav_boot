package com.niz.anim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.niz.Data;


/**
 * @author deepinthewoods
 *Animations associated with different actions i.e. this has all of the different animations(walk, stand, jump) for one character. all chars now
 */
public class AnimSet {
	
	public static final int STAND = "stand".hashCode(), WALK = "walk".hashCode(), JUMP = "jump".hashCode();
	private static final String TAG = "animSet";
	public IntMap<Array<AnimationContainer>> anims = new IntMap<Array<AnimationContainer>>();
	
	public static void addLayerToContainer(float delta, AnimationCommand c,
			AtlasSprite[] frames, int bitmask, String string,
			AnimationContainer container, Vector2[] offsets) {
		//int hash = Data.hash(string);
		//if (frames == null) throw new GdxRuntimeException("null frames");
		//for (int i = 0; i < frames.length; i++){
		//	if (frames[i] == null) throw new GdxRuntimeException("frame is null  "+i);
		//}
		AnimationLayer anim = container.newLayer(delta, frames);
		anim.offsets = offsets;
		//anim.flipped = anim.doFlip();;
		if (c.loop) {
			anim.setPlayMode(PlayMode.LOOP);
			anim.flipped.setPlayMode(PlayMode.LOOP);
		}
		container.bitmask = bitmask;
		anim.isVelocityDependant = c.velocityDependant;
		anim.deltaMultiplier = c.deltaMultiplier;
		container.addLayer(Data.hash(string), anim);
		//Gdx.app.log(TAG, "add layer " + string + " fr" + frames[0].getV());
	}
	public void add(String string, AnimationContainer niz){
		int hash = Data.hash(string);
		Array<AnimationContainer> arr = anims.get(hash);
		if (arr == null){
			arr = new Array<AnimationContainer>();
		} //else 
		//Gdx.app.log("animset ", "array add"+niz.isVelocityDependant + "  "+hash);
		arr.add(niz);
		anims.put(hash, arr);
	}
	
	
	static void addGuideToContainer(String animName, boolean loop,
			boolean randomStart, boolean velocityDependant, float delta,
			int bitmask, String string, AnimationContainer container,
			Vector2[] offsets, float[] angles) {
		
		
		Guide guide = new Guide(offsets, angles);
		container.addGuide(Data.hash(string), guide );
	}
	@Override
	public String toString(){
		String s = "Animation Set contents: \n";
		for (IntMap.Entry<Array<AnimationContainer>> ent : this.anims.entries()){

			s += Data.getString(ent.key) + " ";
			for (AnimationContainer a : ent.value){

				//s += Data.getString(a) + "\n";
				s += a;

			}
		}

		return s;
	}
	
	
	
}
