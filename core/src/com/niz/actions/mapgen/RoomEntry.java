package com.niz.actions.mapgen;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.room.Dist;
import com.niz.room.Room;

public class RoomEntry implements Poolable{

	public Room room;
	public boolean[] exitsUsed = new boolean[4], teleportOut = new boolean[4];
	public GridPoint2 offset = new GridPoint2();
	public RoomEntry[] next = new RoomEntry[4];;
	public IntMap<Dist> filters = new IntMap<Dist>();
	public int getBlock(int x, int y) {
		return 0;
	}
	public int stepsFromMainPath = 0;
	@Override
	public void reset() {
		for (int i = 0; i < exitsUsed.length; i++){
			exitsUsed[i] = false;	
			teleportOut[i] = false;
		}
		for (int i = 0; i < next.length; i++){
			next[i] = null;
		}
		filters.clear();
		stepsFromMainPath = 0;
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
	public int getNextUnusedExitIndex() {
		for (int i = 0; i < room.exit.size; i++){
			if (!exitsUsed[i] ){


				return i;
			}

		}
		return -1;
	}


	public boolean overlaps(int rx, int ry, int rw, int rh) {
		rx -= 1;
		ry -= 1;
		rw += 2;
		rh += 2;
		return offset.x < rx + rw && offset.x + room.blocks[0].length > rx && offset.y < ry + rh && offset.y + room.blocks.length > ry;

	}

//	public boolean overlaps(RoomEntry entry) {//used for inheriting preserveWalls
//		int rw = entry.room.blocks[0].length + room.blocks[0].length;
//		int rh = entry.room.blocks.length + room.blocks.length;
//		int rx = entry.offset.x - room.blocks[0].length/2;
//		int ry = entry.offset.y - room.blocks.length/2;
//		//rx -= 5;
//		//ry -= 5;
//		//rw += 10;
//		//rh += 10;
//		rw+=2;
//		rh+=2;
//		return offset.x < rx + rw && offset.x  >= rx && offset.y < ry + rh && offset.y >= ry;
//
//	}
static GridPoint2
		aBL = new GridPoint2(),
		bBL = new GridPoint2(),
		aTR = new GridPoint2(),
		bTR = new GridPoint2();
public boolean overlaps(RoomEntry entry) {//used for inheriting preserveWalls
	int extra = 1;

	aBL.set(offset).sub(extra, extra);
	aTR.set(offset).add(room.blocks[0].length, room.blocks.length).add(extra, extra);;
	bBL.set(entry.offset);
	bTR.set(entry.offset).add(entry.room.blocks[0].length, entry.room.blocks.length);


	if (aTR.x < bBL.x
			||
			aBL.x > bTR.x
			||
			aTR.y < bBL.y
			||
			aBL.y > bTR.y

			) return false;


	return true;

}

	public boolean overlaps(int x, int y, int h) {
		int extra = 1;

		aBL.set(offset).sub(extra, 0);
		aTR.set(offset).add(room.blocks[0].length, room.blocks.length).add(extra, 0);;
		bBL.set(x, y);
		bTR.set(x, y).add(0, h);


		if (aTR.x < bBL.x
				||
				aBL.x > bTR.x
				||
				aTR.y < bBL.y
				||
				aBL.y > bTR.y

				) return false;


		return true;
	}
}
