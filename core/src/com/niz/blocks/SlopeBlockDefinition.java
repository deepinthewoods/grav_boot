package com.niz.blocks;

import com.badlogic.gdx.math.MathUtils;
import com.niz.BlockDefinition;
import com.niz.component.CollisionComponent;
import com.niz.component.Map;
import com.niz.system.MapSystem;

public class SlopeBlockDefinition extends BlockDefinition {

	protected static final float SMALL_AMOUNT = .00001f;
	private static final float[] 
			X0 = {1f, -0.01f, .5f, -0.01f, .5f, 1f, .5f, 1f}
			, X1 = {-0.01f, 1f, -0.01f, .5f, 1f, .5f, .5f, 1f};
	private static final int VARIANT_MASK = 0xFFFFFFC7;
    private static final int RIGHT_45 = 0, LEFT_45 = 1
			, RIGHT_30_BOTTOM = 2, LEFT_30_BOTTOM = 3
			, LEFT_30_TOP = 4, RIGHT_30_TOP = 5
			, HALF = 6, FULL = 7 ;
	public SlopeBlockDefinition() {
		isSolid = false;
		isSeeThrough = true;
		isSlope = true;
	}
	@Override
	public void randomUpdate(Map map, int x, int y, int b) {
		int left = map.get(x-1, y);
		int right = map.get(x+1, y);
		BlockDefinition lDef = MapSystem.getDef(left);
		BlockDefinition rDef = MapSystem.getDef(right);
		int slopeVariant ;
		if (lDef.isSolid){
			if (rDef.isSolid){
				slopeVariant = FULL;
			} else if (rDef.isSlope){
				slopeVariant = RIGHT_30_TOP;
			} else {
				slopeVariant = RIGHT_45;
			}
		} else if (lDef.isSlope){
			if (rDef.isSolid){
				slopeVariant = LEFT_30_TOP;
			} else if (rDef.isSlope){
				slopeVariant = HALF;
			} else {
				slopeVariant = RIGHT_30_BOTTOM;
			}
		} else {//left empty
			if (rDef.isSolid){
				slopeVariant = LEFT_45;
			} else if (rDef.isSlope){
				slopeVariant = LEFT_30_BOTTOM;
			} else {
				slopeVariant = HALF;
			}
		}
		int actualSlopeVariant = (b >> 3) & 7;
		if (slopeVariant != actualSlopeVariant){
			//int variant = b & 7;
			b &= VARIANT_MASK;
			b |= slopeVariant << 3;
			map.set(x, y, b);
		}
		
	}

	@Override
	public float getYOffsetForMapCollision(float xc, float x0,
			float x1, CollisionComponent c, float y, int b, boolean left, boolean wasOnGround, boolean onGround) {
		//if (xc < 0 || xc > 1) return 0;
		b &= 63;
		
		float off = Math.max(MathUtils.lerp(X0[b/8], X1[b/8], x0), MathUtils.lerp(X0[b/8], X1[b/8], x1));
		if (y < off+SMALL_AMOUNT){
			c.disabled = false; 
			c.onSlope = true;
			return off;					
		} else{
			c.onSlope = true;
			if (wasOnGround)c.disabled = false;
			return off;
		}
		
	}

}
