package com.niz.anim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AnimationLayer extends Animation {
	
	AnimationLayer flipped;
	public Vector2[] offsets;//base offsets for each sprite
	public boolean isVelocityDependant;
	public float deltaMultiplier;

	public AnimationLayer(float frameDuration, TextureRegion[] keyFrames) {
		super(frameDuration, keyFrames);
	}
	
	public AnimationLayer doFlip(){
		TextureRegion[] keyFrames = (TextureRegion[])this.getKeyFrames();
		TextureRegion[] keyframesFlipped = new TextureRegion[keyFrames.length];
		for (int i = 0; i < keyFrames.length; i++){
			AtlasSprite s = new AtlasSprite(new AtlasRegion(((AtlasSprite) keyFrames[i]).getAtlasRegion()));
			s.setFlip(true, false);
			
			//keyFrames[i].flip(true, false);
			keyframesFlipped[i] = s;
		}
		return new AnimationLayer(this.getFrameDuration(), keyframesFlipped);
	}

	
	public TextureRegion getKeyFrame(int index, boolean left) {
		if (left) return ((TextureRegion[])flipped.getKeyFrames())[index];
		//Gdx.app.log("flipped", "");
		
		return ((TextureRegion[])getKeyFrames())[index];
	}

	public int getNumberOfFrames() {
		
		return this.getKeyFrames().length;
	}

	public int getKeyFrameIndex(float f, LayerGuide guide, int size) {

		int keyFrameIndex = getKeyFrameIndex(f);
		if (keyFrameIndex >= getKeyFrames().length){
			throw new GdxRuntimeException("frame error");
			//keyFrameIndex = 0;
		}

		return keyFrameIndex;
	}

}
