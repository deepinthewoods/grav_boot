package com.niz.room;

import com.badlogic.gdx.utils.Array;
import com.niz.room.BlockDistribution;

public class BlockDistributionArray {
	public Array<BlockDistribution> val = new Array<BlockDistribution>();
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
				case SPAWN_SMALL:
				case SPAWN_MEDIUM:
				case SPAWN_MINOR_BOSS:
					break;

				default:
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
