package com.niz.actions.mapgen;

import java.util.Random;

import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;
import com.niz.Blocks;
import com.niz.action.Action;
import com.niz.action.ProgressAction;
import com.niz.anim.Animations;
import com.niz.component.Body;
import com.niz.component.Door;
import com.niz.component.LevelEntrance;
import com.niz.component.Map;
import com.niz.component.Position;
import com.niz.component.RoomDefinition;
import com.niz.component.SpriteIsMapTexture;
import com.niz.component.SpriteStatic;
import com.niz.room.Room;
import com.niz.system.OverworldSystem;
import com.niz.system.ProgressBarSystem;
import com.niz.system.RoomCatalogSystem;

public class AAgentBuildMap extends ProgressAction {
	public enum DistanceType {
		START_TO_END_DIST
		, TOTAL_AREA
		
	}

	//public int seed;
	int progress;
	public Map map;
	private OverworldSystem overworld;
	public int bit;
	public final static int ITERATIONS = 128;
	private static final String TAG = "build map action";
	private static final int TOTAL_ROOMS_TARGET = 80;
	private int teleportDiameter = 50;
	private Array<RoomEntry> main = new Array<RoomEntry>(true, 16), branch = new Array<RoomEntry>(true, 16)
			, base = new Array<RoomEntry>(true, 16);
	private boolean expand = true, twoBlocksHigh = true;
	private int seed;
	private boolean skipResetSeed;
	private int[] pathDistance = new int[8];
	private boolean mainPathDone;
	private int retries;
	@Override
	public void update(float dt) {
		//int x = progress / map.width;
		//x += map.offset.x;
		//Gdx.app.log(TAG, "tick "+x);

		//float height = overworld.getHeight(x, z, 1f);
		//for (int x = 0; x < map.width; x++)
		
		RoomEntry re;
		switch (progress){
		case 0:
			retries++;
			
			for (int x = 0; x < map.width; x++)
				for (int y = 0; y < map.height; y++)
					map.set(x, y, 0);
			progress++;
			//rooms = smallRooms;
			if (skipResetSeed){
				skipResetSeed = false;
			} else {
				r.setSeed(seed + mainPathIndex + (sidePathIndex * 2323));
				teleportDiameter = 10;
			}
			break;
		case 1://first room
			writeToMap(base, 1024);
			main.clear();
			
			RoomEntry availRoom = getNextAvailableRoom();
			if (availRoom == null) throw new GdxRuntimeException("ar null ");
			main.add(availRoom );
			
			//main.add(re);
			//writeToMap(re, 1024);
			progress++;
			break;
		case 2://random walk
			rooms = smallRooms;
			boolean done = makeRooms(1000, 10);
		
			if (done){
				progress++;
			}
			else {
				//main.clear();
				progress = 0;
				skipResetSeed = true;
				//Gdx.app.log(TAG, "RETRY" + main.size + "  " + mainPathIndex + "  " + retries);
				if (retries > 20) {
					//Gdx.app.log(TAG, "cannot progress further, ending" + main.size);
					if (base.size < TOTAL_ROOMS_TARGET){
						//Gdx.app.log(TAG, "JUMP " + base.size);
						RoomEntry nextR = getNextAvailableRoom();
						int ind = nextR.getNextUnusedExitIndex();
						nextR.teleportOut[ind] = true;
						retries = 0;
					} else {
						progress = 5;
						
					}

				}
			}
			if (base.size > TOTAL_ROOMS_TARGET)
				progress = 5;
			break;
		case 3://boss room, back to 2 if too small
			//Gdx.app.log(TAG, "expand");
			rooms = bigRooms;
			done = true;
			done = makeRooms(100, 1);
			if (done){
				retries = 0;
				progress = 0;
				rooms = smallRooms;
				int dist = getDistance(main, DistanceType.TOTAL_AREA);
				boolean greater = false
						;
				pathDistance [mainPathIndex] = dist;
				//mainPathIndex++;
				if (mainPathDone){
					progress = 4;
					//Gdx.app.log(TAG, "main path done" + progress);
					break;
				}
				if (mainPathIndex++ >= pathDistance.length-1){
					int shortestIndex = 0;
					int shortestDistance = pathDistance[0];
					for (int i = 1; i < pathDistance.length; i++){
						if (pathDistance[i] > shortestDistance == greater){
							shortestIndex = i;
							shortestDistance = pathDistance[i];
						}
					}
					progress = 0;
					mainPathIndex = shortestIndex;
					mainPathDone = true;
					//retries = 0;
					//Gdx.app.log(TAG, "MAIN PATH FOUND" + mainPathIndex);
					
				}
				//progress++;											
			} else{
				//Gdx.app.log(TAG, "RETRY BIGROOM" + main.size);
				progress = 0;
				skipResetSeed = true;
				
			}
			//progress++;
			break;
		case 4://move current path to base
			RoomEntry ex = getNextAvailableRoom();
			int exInd = ex.getNextUnusedExitIndex();
			ex.exitsUsed[exInd] = true;
			main.removeIndex(0);
			base.addAll(main);
			progress = 0;
			mainPathIndex = 0;
			sidePathIndex++;
			rooms = smallRooms;
			retries = 0;
			break;
		
		case 5://finalize
			for (int x = 0; x < map.width; x++)
				for (int y = 0; y < map.height; y++)
					map.set(x, y, 0);
			writeToMap(base, 1024, true);
			//Gdx.app.log(TAG, "DONEFIFFNIFNIFNFINIFNIFNFINFNIODODODODONDONDONEONDOEOnDONEDOenDONEOEDNEDONEDONENOD");
			for (int x = 0; x < map.width; x++)
				for (int y = 0; y < map.height; y++){
					if (map.get(x, y) == 1024){
						map.set(x, y, 0);
						//Gdx.app.log(TAG, "air" + y);
					}
					else{
						//Gdx.app.log(TAG, "stne");
						map.set(x, y, Blocks.STONE+5);
					}
				}
			progress++;
			break;
		case 6:
			isFinished = true;
			PooledEntity en = parent.engine.createEntity();
			Position ePos = parent.engine.createComponent(Position.class);
			ePos.pos.set(map.width/2+.5f, map.height/2+.5f);
			Gdx.app.log(TAG, "DONEFIFFNIFNI" + ePos.pos + "  total iterations" + totalIterations);
			LevelEntrance entrance = parent.engine.createComponent(LevelEntrance.class);
			en.add(ePos).add(entrance);
			parent.engine.addEntity(en);;
			break;
		}
		totalIterations++;
		
		float progressDelta = (float)base.size / (float)TOTAL_ROOMS_TARGET;
		progressSys.setProgressBar(progressBarIndex, progressDelta);

		//if (progress > map.width)
		//isFinished = true;
	}
	private RoomEntry getNextAvailableRoom() {
		for (RoomEntry r : base){
			for (int i = 0; i < r.room.exit.size; i++){
				if (!r.exitsUsed[i]){
					//Gdx.app.log(TAG, "avail r " + i + r);
					return r;
				}
			}
		}
		if (true) throw new GdxRuntimeException("no available exits");
		return null;
		//return base.peek();
	}
	private void writeToMap(Array<RoomEntry> list, int i) {
		writeToMap(list, i, false);
	}
	private void writeToMap(Array<RoomEntry> list, int i, boolean passages) {
		for (RoomEntry r : list){
			writeToMap(r, i, passages);
		}
	}
	private int getDistance(Array<RoomEntry> main, DistanceType distanceType) {
		if (distanceType == DistanceType.START_TO_END_DIST){
			RoomEntry finalR = main.peek();
			RoomEntry firstR = main.get(0);
			int dx = Math.abs(finalR.offset.x - firstR.offset.x);
			int dy = Math.abs(finalR.offset.y - firstR.offset.y);
			return dx * dx + dy * dy;			
		} else if (distanceType == DistanceType.TOTAL_AREA){
			RoomEntry firstR = main.get(0);
			int x0 = firstR.offset.x;
			int x1 = firstR.offset.x + firstR.room.blocks[0].length;
			int y0 = firstR.offset.y;
			int y1 = firstR.offset.y + firstR.room.blocks.length;
			for (int i = 1; i < main.size; i++){
				RoomEntry rm = main.get(i);
				x0 = Math.min(x0,  rm.offset.x);
				x1 = Math.max(x1, rm.offset.x + rm.room.blocks[0].length);
				y0 = Math.min(y0, rm.offset.y);
				y1 = Math.max(y1, rm.offset.y + rm.room.blocks.length);
			}
			return (x1 - x0) * (y1 - y0);
		}
		return 0;
	}
	private boolean makeRooms(int tries, int maxRooms) {
		int count = 0, roomCount = 0;
		boolean done = false;
		while (!done && count < tries){
			int ind = r.nextInt(rooms.size);
			//Gdx.app.log(TAG, "ind " + rooms.size);
			
			Room room = rooms.get(ind);
			RoomEntry re = Pools.obtain(RoomEntry.class);
			RoomEntry pre = main.peek();
			if (room == null) throw new GdxRuntimeException("hklfsd");
			if (pre == null) throw new GdxRuntimeException("hklfsd" + main.size);
			int exitIndex = pre.getNextUnusedExitIndex();
			if (exitIndex == -1) throw new GdxRuntimeException("no exits left");
			if (pre.teleportOut[exitIndex]){
				room = startRooms.get(r.nextInt(startRooms.size-1));
			}
			re.room = room;
			GridPoint2 exit = pre.room.exit.get(exitIndex);
			int exDir = Room.getExitBitmask(pre.room, exitIndex);
			//int entDir = Room.getEntranceBitmask(re.room, re.exitIndex);
			re.offset.set(pre.offset);
			re.offset.x += exit.x;
			re.offset.y += exit.y;
			re.offset.x -= re.room.entrance.get(re.entranceIndex).x;
			re.offset.y -= re.room.entrance.get(re.entranceIndex).y;
			if (pre.teleportOut[exitIndex]){
				Gdx.app.log(TAG, "TELEPORTING " + teleportDiameter);
				re.offset.x += r.nextInt(teleportDiameter) - teleportDiameter/2;
				re.offset.y += r.nextInt(teleportDiameter) - teleportDiameter/2;
				if (r.nextBoolean()) teleportDiameter = Math.min(OverworldSystem.SCROLLING_MAP_WIDTH,  teleportDiameter+1);
			}
			if ((exDir & Room.LEFT) != 0){
				re.offset.x -= expand?2:1;
			} else if ((exDir & Room.RIGHT) != 0){
				re.offset.x += expand?2:1;
			}else
			if ((exDir & Room.UP) != 0){
				re.offset.y += expand?2:1;
			} else if ((exDir & Room.DOWN) != 0){
				re.offset.y -= expand?2:1;
			}				
			if (mapIsClear(re.offset.x, re.offset.y, re.room.blocks[0].length, re.room.blocks.length)){
				//Gdx.app.log(TAG, "map cllear, writing");
				writeToMap(re, 1024);
				roomCount++;
				int exind = main.peek().getNextUnusedExitIndex();
				if (main.size > 1)
					main.peek().exitsUsed[exind] = true;
				main.add(re);
				pre.next[exitIndex] = re;
			}								
			count++;
			if (roomCount >= maxRooms){
				done = true;
			}
			
		}
		return done;
	}
	private boolean mapIsClear(int ax, int ay, int w, int h) {
		//Gdx.app.log(TAG, "clear " + w + " " + h + "  x " + ax + " , " + ay);
		//if (ax < 1 || ay < 1 || ax + w >= map.width-1 || ay + h >= map.height-1) return false;
		if (ax < 1 || ay < 1 || ax + w >= map.width-2 || ay + h >= map.height-2) return false;
		for (int x = 0; x < w+2; x++)
			for (int y = 0; y < h+2; y++){
				if (map.get(x + ax - 1, y + ay - 1) != 0) return false;
			}
		return true;
	}
	private void writeToMap(RoomEntry entry, int i) {
		writeToMap(entry, i, false);
	}
	private void writeToMap(RoomEntry entry, int i, boolean passages) {

		//Gdx.app.log(TAG, "write " + entry.offset + " " + entry.room.blocks[0].length + " , " + entry.room.blocks.length);
		for (int x = 0; x < entry.room.blocks[0].length; x++)
			for (int y = 0; y < entry.room.blocks.length; y++){
				map.set(x+entry.offset.x, y + entry.offset.y, i);
			}
		if (passages){
			for (int x = 0; x < entry.room.blocks[0].length; x++)
				for (int y = 0; y < entry.room.blocks.length; y++){
					map.setBG(x+entry.offset.x, y + entry.offset.y, i);
				}
		}
		if (expand){
			int dx = 0, dy = 0;
			int exitIndex = entry.getNextUnusedExitIndex();
			exitIndex = 0;
			int exDir = Room.getExitBitmask(entry.room, exitIndex);
			if ((exDir & Room.LEFT) != 0){
				dx = -1;
			} else if ((exDir & Room.RIGHT) != 0){
				dx = 1;
			}else
			if ((exDir & Room.UP) != 0){
				dy = 1;
			} else if ((exDir & Room.DOWN) != 0){
				dy = -1;
			}
			
			GridPoint2 exit = entry.room.exit.get(exitIndex);
			if (passages){
				if (entry.teleportOut[exitIndex]){
					makeDoor(entry, exitIndex);
				} else {
					
				}
				map.set(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy, i);
				map.set(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy + 1, i);
				map.setBG(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy, i);
				map.setBG(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy + 1, i);
			}
		}
	}
	private void makeDoor(RoomEntry entry, int exitIndex) {
		int exDir = Room.getExitBitmask(entry.room, exitIndex);
		int dx = 0, dy = 0;
		if ((exDir & Room.LEFT) != 0){
			dx = -1;
		} else if ((exDir & Room.RIGHT) != 0){
			dx = 1;
		}else
		if ((exDir & Room.UP) != 0){
			dy = 1;
		} else if ((exDir & Room.DOWN) != 0){
			dy = -1;
		}
		
		GridPoint2 exit = entry.room.exit.get(exitIndex);

		{
			Entity e = parent.engine.createEntity();
			Position pos = parent.engine.createComponent(Position.class);
			pos.pos.set(entry.offset.x + exit.x + dx + .5f, entry.offset.y + exit.y + dy+1);
			e.add(pos);
			SpriteStatic sprite = parent.engine.createComponent(SpriteStatic.class);
			sprite.s = Animations.doors[0];
			e.add(sprite);
			Body body = parent.engine.createComponent(Body.class);
			body.width = .5f;
			body.height = 1f;
			e.add(body);
			e.add(parent.engine.createComponent(SpriteIsMapTexture.class));
			Door door = parent.engine.createComponent(Door.class);
			RoomEntry next = entry.next[exitIndex];
			door.endPoint.set(next.offset);
			e.add(door);
			parent.engine.addEntity(e);
		}
		{
			Entity e = parent.engine.createEntity();
			Position pos = parent.engine.createComponent(Position.class);
			exit = entry.next[exitIndex].room.exit.get(exitIndex);
			pos.pos.set(entry.next[exitIndex].offset.x + dx - .5f, entry.next[exitIndex].offset.y  + dy+1);
			e.add(pos);
			SpriteStatic sprite = parent.engine.createComponent(SpriteStatic.class);
			sprite.s = Animations.doors[1];
			e.add(sprite);
			Body body = parent.engine.createComponent(Body.class);
			body.width = .5f;
			body.height = 1f;
			e.add(body);
			e.add(parent.engine.createComponent(SpriteIsMapTexture.class));
			//Door door = parent.engine.createComponent(Door.class);
			RoomEntry next = entry.next[exitIndex];
			//door.endPoint.set(next.offset);
			//e.add(door);
			parent.engine.addEntity(e);
		}
		//map.set(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy + 1, i);										
		
	}
	public Action after;
	public int z;
	private Array<Room> rooms, bigRooms = new Array<Room>(), smallRooms = new Array<Room>(), startRooms = new Array<Room>();;
	private Random r;
	private int mainPathIndex;
	private int sidePathIndex;
	private int totalIterations;
	private ProgressBarSystem progressSys;

	@Override
	public void onEnd() {
		if (overworld.worldDef.isRoomEditor){
			Position ePos = parent.engine.createComponent(Position.class);
			ePos.pos.set(OverworldSystem.SCROLLING_MAP_WIDTH/2+.5f, OverworldSystem.SCROLLING_MAP_HEIGHT/2+.5f);
			PooledEntity room = parent.engine.createEntity();
			RoomDefinition rd = parent.engine.createComponent(RoomDefinition.class);
			rd.min.set(OverworldSystem.SCROLLING_MAP_WIDTH/2, OverworldSystem.SCROLLING_MAP_WIDTH/2);
			rd.max.set(OverworldSystem.SCROLLING_MAP_WIDTH/2+1, OverworldSystem.SCROLLING_MAP_WIDTH/2+1);
			rd.min9.set(OverworldSystem.SCROLLING_MAP_WIDTH/2, OverworldSystem.SCROLLING_MAP_WIDTH/2);
			rd.max9.set(OverworldSystem.SCROLLING_MAP_WIDTH/2+1, OverworldSystem.SCROLLING_MAP_WIDTH/2+1);
			room.add(rd);
			parent.engine.addEntity(room);			
		}
		//Gdx.app.log(TAG, "end"+map.offset + " " + bit);
		parent.engine.removeEntity(parent.e);
		overworld.onFinishedMap(bit, map);
		map = null;
		addAfterMe(after);	
	}

	@Override
	public void onStart() {
		//Gdx.app.log(TAG, "start"+map.offset + bit);
		progress = 0;
		sidePathIndex = 0;
		this.overworld = parent.engine.getSystem(OverworldSystem.class);
		parent.engine.getSystem(RoomCatalogSystem.class).getRoomsForTags(smallRooms, "easy");
		parent.engine.getSystem(RoomCatalogSystem.class).getRoomsForTags(bigRooms, "easybig");
		parent.engine.getSystem(RoomCatalogSystem.class).getRoomsForTags(startRooms, "easystart");

		r = Pools.obtain(Random.class);
		r.setSeed(overworld.worldDef.seed);
		seed = MathUtils.random(10000);
		mainPathIndex = 0;
		mainPathDone = false;
		
		rooms = smallRooms;
		RoomEntry re = Pools.obtain(RoomEntry.class);
		int index = r.nextInt(startRooms.size);
		re.room = startRooms.get(index);
		re.entranceIndex = 0;
		totalIterations = 0;
		re.offset.set(map.width/2, map.height/2);
		base.add(re);
		re.teleportOut[0] = true;
		progressSys = parent.engine.getSystem(ProgressBarSystem.class);

	}

	/*private int generateBlock(int bx, int by) {
		float x = bx;
		float y = by;
		float factor = .1f;
		x *= factor;
		y *= factor;
		float noise = SimplexNoise.noise(x, y);
		if (noise > 0) return Blocks.STONE;
		return 0;
	}*/

}
