package com.niz.room;

import java.util.Iterator;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.Pools;

public class Room {
	public static final int LEFT = 1, UP = 2, RIGHT = 4, DOWN = 8;
	public int[][] blocks;
	public IntMap<BlockDistributionArray> distributions = new IntMap<BlockDistributionArray>();
	public Array<String> tags = new Array<String>();
	public transient Array<GridPoint2> entrance = new Array<GridPoint2>(), exit = new Array<GridPoint2>();
	public transient boolean flipped = false;
	public Room(Room r) {//makes flipped one. just copies pointers since tags/distr doesn't change
		super();
		blocks = new int[r.blocks.length][r.blocks[0].length];
		for (int x = 0; x < blocks.length; x++)
			for (int y = 0; y < blocks[0].length; y++){
				blocks[x][y] = r.blocks[x][r.blocks[0].length - y-1];
				//Gdx.app.log("jfdkl", "move " + x + " " + y + " to " + (blocks[0].length - y-1));
			}
		distributions = r.distributions;
		tags = r.tags;
		flipped = true;
	}
	public Room(){
		
	}
	public boolean calculatePoints(){
		while (entrance.size > 0)Pools.free(entrance.pop());
		while (exit.size > 0)Pools.free(exit.pop());
		for (int x = 0; x < blocks.length; x++)
			for (int y = 0; y < blocks[0].length; y++){
				int b = blocks[x][y];
				Array<BlockDistribution> dista = distributions.get(b).val;
				for (int i = 0; i < dista.size; i++){
					BlockDistribution dist = dista.get(i);
					if (dist.value == Dist.ENTRANCE){
						entrance.add(Pools.obtain(GridPoint2.class).set(y, blocks.length-1-x));
					} else if (dist.value == Dist.EXIT){
						exit.add(Pools.obtain(GridPoint2.class).set(y, blocks.length-1-x));
					}
					
				}
			}
		if (entrance.size == 0 || exit.size == 0) return false;
		return true;
	}
	public int isCorner(){
		boolean up = false, down = false, left = false, right = false;
		for (GridPoint2 p : entrance){
			if (p.x == 0 )left = true;
			if (p.x == blocks[0].length-1)right = true;
			if (p.y == 0 )down = true;
			if (p.y == blocks.length-1)up = true;
			
		}
		int r = 0;
		if (left) r += LEFT;
		if (right) r += RIGHT;
		if (up) r += UP;
		if (down) r += DOWN;
		return r;
	}
	public static int getExitBitmask(Room room, int index) {
		GridPoint2 p = room.exit.get(index);
		boolean up = false, down = false, left = false, right = false;

		if (p.x == 0 )left = true;
		if (p.x == room.blocks[0].length-1)right = true;
		if (p.y == 0 )down = true;
		if (p.y == room.blocks.length-1)up = true;
		int r = 0;
		if (left) r += LEFT;
		if (right) r += RIGHT;
		if (up) r += UP;
		if (down) r += DOWN;
		return r;
	}
	public static int getEntranceBitmask(Room room, int index) {
		GridPoint2 p = room.entrance.get(index);
		boolean up = false, down = false, left = false, right = false;

		if (p.x == 0 )left = true;
		if (p.x == room.blocks[0].length-1)right = true;
		if (p.y == 0 )down = true;
		if (p.y == room.blocks.length-1)up = true;
		int r = 0;
		if (left) r += LEFT;
		if (right) r += RIGHT;
		if (up) r += UP;
		if (down) r += DOWN;
		return r;
	}
	
	public String toString(){
		String s = "\n";
		Iterator<Entry<BlockDistributionArray>> iter = distributions.iterator();
		while (iter.hasNext()){
			//Array<?> d = distributions.get(i).val;
			Array<BlockDistribution> d = iter.next().value.val;
			s += d;
			s += "\n";
		}
		return s;
	}
}
