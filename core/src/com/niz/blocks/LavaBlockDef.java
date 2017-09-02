package com.niz.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.niz.BlockDefinition;
import com.niz.Blocks;
import com.niz.component.Map;
import com.niz.system.MapSystem;

public class LavaBlockDef extends BlockDefinition {
	public LavaBlockDef() {
		this.isSolid = false;
		this.isSeeThrough = true;
		this.isLit = true;
		
	}
	public static int LEFT_MASK = 1 << Map.DATA_BITS, LEFT_BITS = Map.DATA_BITS;
	static final int[] dxa = {-1, 1, 0, 0}, dya = {0, 0, -1, 1};
	private static final String TAG = "Lava block def";
	private static final int HALF_BLOCK = Blocks.WATER , FULL_BLOCK = Blocks.WATER + 32;
    private static final boolean CLUMPS = false;
	private static final int LIQUID_INDEX = 1;
	int bottom;
	private BlockDefinition bottomDef;
	private BlockDefinition sideDef;
	private int side;
	private int dx;
	private int sideBottom;
	private BlockDefinition sideBottomDef;
	@Override
	public void randomUpdate(Map map, int x, int y, int b) {
		//Gdx.app.log(TAG, "update");
		BlockDefinition def = MapSystem.getDef(b);
		if ((b & 63) < 32){//half block
			bottom = map.get(x, y-1);
			bottomDef = MapSystem.getDef(bottom);
			if (bottom == 0){
				map.set(x, y-1, HALF_BLOCK | (b&LEFT_MASK));
				map.set(x, y, 0);
				return;
			} else if (bottomDef.isGas){
				//swap
			} else if ( (b & Map.ID_MASK) >> Map.ID_BITS == (bottom & Map.ID_MASK) >> Map.ID_BITS){
				if ((bottom & 63) < 32){//half block same bottom
					map.set(x, y-1, bottom+32);
					map.set(x, y, 0);
					return;
					
				} else {//full block same bottom
					
				}
			} else if ((bottom & Map.ID_MASK) == Blocks.SLOPE){//slope bottom
				
				int halfBlock = (bottom & (Map.VARIANT_MASK | Map.ID_MASK));
				halfBlock <<= Map.DATA_BITS;
				halfBlock |= Blocks.SLOPE_WITH_LIQUID;
				int variant = (bottom & Map.VARIANT_MASK) / 64;
				int slopeType =((bottom & Map.VARIANT_MASK) >> 3 ) & 7;
				
				Gdx.app.log(TAG,  "slope liquid"+variant+"  "+slopeType);
				halfBlock += variant*64 + slopeType*8;
				halfBlock += LIQUID_INDEX;//lava is the second
				map.set(x, y-1, halfBlock);
				map.set(x, y, 0);
				return;
			}
			
			if ((b & LEFT_MASK) == 0){//left
				dx = -1;
			} else dx = 1;
			side = map.get(x+dx, y);
			sideDef = MapSystem.getDef(side);
			if (side == 0){
				map.set(x+dx, y, HALF_BLOCK | (b&LEFT_MASK));
				map.set(x, y, 0);
				return;
			} else if ( (b & Map.ID_MASK) >> Map.ID_BITS == (side & Map.ID_MASK) >> Map.ID_BITS){
				if ((side & 63) < 32){//half block side
					
				} else {
					//full block side
				}
				
			}
			
		} else {//full block
			bottom = map.get(x, y-1);
			bottomDef = MapSystem.getDef(bottom);
			if (bottom == 0){
				map.set(x, y-1, FULL_BLOCK);
				map.set(x, y, 0);
				return;
			} else if (bottomDef.isGas){
				//swap
			} else if ( (b & Map.ID_MASK) >> Map.ID_BITS == (bottom & Map.ID_MASK) >> Map.ID_BITS){
				if ((bottom & 63) < 32){//half block same bottom
					map.set(x, y-1, bottom+32);
					map.set(x, y, HALF_BLOCK | (b&LEFT_MASK));
					return;
					
				} else {//full block same bottom
					
				}
			}else if ((bottom & Map.ID_MASK) == Blocks.SLOPE){//slope bottom
				
				int halfBlock = (bottom & (Map.VARIANT_MASK | Map.ID_MASK));
				halfBlock <<= Map.DATA_BITS;
				halfBlock |= Blocks.SLOPE_WITH_LIQUID;
				int variant = (bottom & Map.VARIANT_MASK) / 64;
				int slopeType =((bottom & Map.VARIANT_MASK) >> 3 ) & 7;
				//Gdx.app.log(TAG,  "slope liquid"+variant+"  "+slopeType);
				halfBlock += variant*64 + slopeType*8;
				halfBlock += LIQUID_INDEX;//lava is the second
				map.set(x, y-1, halfBlock);
				map.set(x, y, b-32);
				return;
			}
			
			if ((b & LEFT_MASK) == 0){//left
				dx = -1;
			} else dx = 1;
			side = map.get(x+dx, y);
			sideDef = MapSystem.getDef(side);
			if (side == 0){
				map.set(x+dx, y, HALF_BLOCK| (b&LEFT_MASK));
				map.set(x, y, HALF_BLOCK| (b&LEFT_MASK));
				return;
			} else if ( (b & Map.ID_MASK) >> Map.ID_BITS == (side & Map.ID_MASK) >> Map.ID_BITS){
				if ((side & 63) < 32){//half block side
					map.set(x+dx, y, FULL_BLOCK | (b&LEFT_MASK));
					map.set(x, y, HALF_BLOCK | (b&LEFT_MASK));
				} else {
					//full block side
				}
			
			}else if ((side & Map.ID_MASK) == Blocks.SLOPE){//slope side
				//make 
				int halfBlock = (side & (Map.VARIANT_MASK | Map.ID_MASK));
				halfBlock <<= Map.DATA_BITS;
				halfBlock |= Blocks.SLOPE_WITH_LIQUID;
				int variant = (side & Map.VARIANT_MASK) / 64;
				int slopeType =((side & Map.VARIANT_MASK) >> 3 ) & 7;
				//Gdx.app.log(TAG,  "slope liquid"+variant+"  "+slopeType);
				halfBlock += variant*64 + slopeType*8;
				halfBlock += LIQUID_INDEX;//lava is the second
				map.set(x+dx, y, halfBlock);
				map.set(x, y, b-32);
				return;
			}
			
		}
		b = map.get(x,  y);
		//Gdx.app.log(TAG, "flip bit" + b + "  => " + (b ^ LEFT_MASK));
		b = b ^ LEFT_MASK;//flip left bit...
		map.set(x, y, b);
	}

}
