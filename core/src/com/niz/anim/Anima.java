package com.niz.anim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Anima extends Animation {
	Anima flipped;
	public boolean isVelocityDependant;
	public boolean randomStart;
	public int bitmask;
	public boolean drawOnMove = false;

    public Anima(float frameDuration, TextureRegion[] keyFrames) {
		super(frameDuration, keyFrames);
	}
	
	

	
	public Anima doFlip(){
		TextureRegion[] keyFrames = (TextureRegion[])this.getKeyFrames();
		TextureRegion[] keyframesFlipped = new TextureRegion[keyFrames.length];
		for (int i = 0; i < keyFrames.length; i++){
			AtlasSprite s = new AtlasSprite((AtlasSprite) keyFrames[i]);
			s.flip(true, false);
			keyframesFlipped[i] = s;
		}
		return new Anima(this.getFrameDuration(), keyframesFlipped);
	}

	@Override
	public TextureRegion getKeyFrame(float stateTime, boolean left) {
		if (left) return (TextureRegion)flipped.getKeyFrame(stateTime);
		//Gdx.app.log("flipped", "");
		return (TextureRegion)super.getKeyFrame(stateTime);
	}

}
