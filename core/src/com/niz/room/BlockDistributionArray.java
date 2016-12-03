package com.niz.room;

import com.badlogic.gdx.utils.Array;
import com.niz.room.BlockDistribution;

public class BlockDistributionArray {
	Array<BlockDistribution> val = new Array<BlockDistribution>();
	public float getTotalWeight(){
		float total = 0;
		for (BlockDistribution d : val){
			switch (d.value){
			case ENTRANCE:
			case EXIT:
	
				break;
			case BLOCKA:
			case BLOCKB:
			case EMPTY:
			case LADDER:
				total += d.weight;
				break;
			}
		}
		return total;
	}

	public BlockDistribution getItemAtWeight(float targetWeight) {
		float total = 0;
		for (BlockDistribution d : val){
			switch (d.value){
			case ENTRANCE:
			case EXIT:
	
				break;
			case BLOCKA:
			case BLOCKB:
			case EMPTY:
			case LADDER:
				total += d.weight;
				if (total >= targetWeight)
					return d;
				break;
			}
		}
		return val.get(val.size-1);
		
	}

	public void add(BlockDistribution d) {
		val.add(d);
	}
	
}
