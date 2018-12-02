package com.niz.anim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class RotatedAnimationLayer extends AnimationLayer {
	private static final String TAG = "rotated animation layer";
	int[] indicesByAngle;
	public Vector2[] toThrowOffsets;
	public RotatedAnimationLayer(TextureRegion[] keyFrames, Vector2[] offsets, int[] angleToIndex) {
		super(100000f, keyFrames);
		indicesByAngle = angleToIndex;
		this.offsets = offsets;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getKeyFrameIndex(float f, LayerGuide guide, int size) {
		int deg = (int) guide.rotation;
		//Gdx.app.log(TAG, "keyframe ");
		return indicesByAngle[deg];
		//return super.getKeyFrameIndex(f, guide);
	}
	

}
