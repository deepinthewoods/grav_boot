package com.niz.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.niz.BlockDefinition;
import com.niz.Blocks;
import com.niz.component.Map;
import com.niz.system.MapSystem;

public class LiquidSlopeBlockDef extends BlockDefinition {
	public LiquidSlopeBlockDef() {
		this.isSolid = false;
		this.isSeeThrough = true;
		this.isLit = true;
		this.isSlope = true;
	}
	public static int LEFT_MASK = 1 << Map.DATA_BITS, LEFT_BITS = Map.DATA_BITS;
	static final int[] dxa = {-1, 1, 0, 0}, dya = {0, 0, -1, 1};
	private static final String TAG = "liquid slop block def";
	private static int HALF_BLOCKS[] = {Blocks.WATER, Blocks.LAVA};
	private static final boolean CLUMPS = false;
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
		{//full block
			bottom = map.get(x, y-1);
			bottomDef = MapSystem.getDef(bottom);
			
			
			if ((b & LEFT_MASK) == 0){//left
				dx = -1;
			} else dx = 1;
			
			side = map.get(x+dx, y);
			sideDef = MapSystem.getDef(side);
			if (side == 0){
				int liquidIndex = b & 7;
				int HALF_BLOCK = HALF_BLOCKS[liquidIndex];
				
				map.set(x+dx, y, HALF_BLOCK| (b&LEFT_MASK));
				int newBlock = (b & Map.DATA_MASK) >> Map.DATA_BITS;
				map.set(x, y, newBlock);
				return;
			} else if ((side & Map.ID_MASK) == Blocks.SLOPE){//slope side
				//make 
				int liquidIndex = b & 7;
				int halfBlock = (side & (Map.VARIANT_MASK | Map.ID_MASK));
				halfBlock <<= Map.DATA_BITS;
				halfBlock |= Blocks.SLOPE_WITH_LIQUID;
				int variant = (side & Map.VARIANT_MASK) / 64;
				int slopeType = variant / 8;
				Gdx.app.log(TAG,  "slope liquid"+variant+"  "+slopeType);
				halfBlock += variant*64 + slopeType*8;
				halfBlock += liquidIndex;
				map.set(x+dx, y, halfBlock);
				int newBlock = (b & Map.DATA_MASK) >> Map.DATA_BITS;
				map.set(x, y, newBlock);
				return;
			}
			
		}
		b = map.get(x,  y);
		//Gdx.app.log(TAG, "flip bit" + b + "  => " + (b ^ LEFT_MASK));
		b = b ^ LEFT_MASK;//flip left bit...
		map.set(x, y, b);
	}

}
