package com.niz.actions.mapgen;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.room.Dist;
import com.niz.room.Room;

public class RoomEntry implements Poolable{

	public Room room;
	public int entranceIndex;
	public boolean[] exitsUsed = new boolean[4], teleportOut = new boolean[4];
	public GridPoint2 offset = new GridPoint2();
	public RoomEntry[] next = new RoomEntry[4];;
	
	public int getBlock(int x, int y) {
		return 0;
	}
	@Override
	public void reset() {
		for (int i = 0; i < exitsUsed.length; i++){
			exitsUsed[i] = false;	
			teleportOut[i] = false;
		}
		for (int i = 0; i < next.length; i++){
			next[i] = null;
		}
	}
	public int getNextUnusedUnFilteredExitIndex() {
		for (int i = 0; i < room.exit.size; i++){
			if (!exitsUsed[i] && room.exitFilters.get(i).size == 0)
				return i;
		}
		return -1;
	}

	public void markAllExitsUsed() {
		for (int i = 0; i < room.exit.size; i++){
			exitsUsed[i] = true;

		}
	}
	/*
		just returns any exit
	 */
	public int getNextUnusedFilteredExitIndex(Array<Dist> filters) {
		for (int i = 0; i < room.exit.size; i++){
			if (!exitsUsed[i] ){

				for (int f = 0; f < filters.size; f++){

				}
				return i;
			}

		}
		return -1;
	}
}
