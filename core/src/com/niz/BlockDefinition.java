package com.niz;

import com.niz.component.CollisionComponent;
import com.niz.component.Map;

public abstract class BlockDefinition {
	public boolean isSeeThrough, isGas, isLiquid;
	public boolean isSolid = true;
	public boolean isLit;
	public boolean isSlope;
	public float destroyMultiplier = 1f;
	public int particleItemID;
	public int particleCount;
	public boolean breaksWithBigPieces;

	public abstract void randomUpdate(Map map, int x, int y, int b);
	
	public BlockDefinition(){
		
	}

	public float getYOffsetForMapCollision(float xc, float x0, float x1, CollisionComponent c, float y, int b, boolean left, boolean wasOnGround, boolean onGround) {//0..1
		if (isSolid){
			c.disabled = false;
			return 1;
		} else {
			c.disabled = true;
			return 0;
		}
		//return isSolid?1:0;
	}
	
}
