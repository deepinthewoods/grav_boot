package com.niz.actions.mapgen;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
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
import com.niz.component.*;
import com.niz.room.BlockDistribution;
import com.niz.room.BlockDistributionArray;
import com.niz.room.Dist;
import com.niz.room.Room;
import com.niz.system.OverworldSystem;
import com.niz.system.ProgressBarSystem;
import com.niz.system.RoomCatalogSystem;

public class AAgentBuildMap extends ProgressAction {
	private long startTime;
	private RoomEntry baseStartRoom;

	public enum DistanceType {
		START_TO_END_DIST
		, TOTAL_AREA
		
	}

	//public int seed;
	int progress;

	public Map map;
	private OverworldSystem overworld;
	public int bit;
	public final static int ITERATIONS = 64;
	private static final String TAG = "build map action";
	private static final int TOTAL_ROOMS_TARGET = 10;
	private static final int TOP_FREE_SPACE = 40;
	private int teleportDiameter = 50;
	private Array<RoomEntry> main = new Array<RoomEntry>(true, 16), branch = new Array<RoomEntry>(true, 16)
			, base = new Array<RoomEntry>(true, 16);
	private boolean expand = true, twoBlocksHigh = true;
	private int seed;
	private boolean skipResetSeed;
	private int[] pathDistance = new int[18];
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
					map.setLocal(x, y, 0);
			progress++;
			//rooms = smallRooms;
			if (skipResetSeed){
				skipResetSeed = false;
			} else {
				r.setSeed(seed + mainPathIndex + (sidePathIndex * 2323));
				teleportDiameter = 10;
			}
			break;
		case 1://write previously finalized
			writeToMap(base, 1024);
			main.clear();
			baseStartRoom = getNextAvailableBaseRoom();
			if (baseStartRoom == null) throw new GdxRuntimeException("ar null ");
			main.add(baseStartRoom);
			progress++;
			break;
		case 2://make small rooms
			rooms = smallRooms;
			boolean done = makeRooms(30, 10, null);
			if (done){
				progress++;
			}
			else {
				//main.clear();
				progress = 0;
				skipResetSeed = true;
				//Gdx.app.log(TAG, "RETRY" + main.size + "  " + mainPathIndex + "  " + retries);
				/*if (retries > 10) {
					//Gdx.app.log(TAG, "cannot progress further, ending" + main.size);
					if (base.size < TOTAL_ROOMS_TARGET){//teleporting
						//Gdx.app.log(TAG, "JUMP " + base.size);
						RoomEntry roo = baseStartRoom;
						int ind = roo.getNextUnusedExitIndex();
						roo.teleportOut[ind] = true;
						retries = 0;
					}

				}*/
			}
			/*if (base.size > TOTAL_ROOMS_TARGET) {
				progress = 5;
				base.peek().markAllExitsUsed();
			}*/
			break;
		case 3://big room + distance heuristic
			//Gdx.app.log(TAG, "expand");
			rooms = bigRooms;
			done = true;
			done = makeRooms(30, 1, null);
			if (!done){

			}
			if (done){
				retries = 0;
				progress = 0;
				rooms = smallRooms;
				int dist = getDistance(main, DistanceType.START_TO_END_DIST);
				boolean greater = false;
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
						if (pathDistance[i] > shortestDistance ){
							shortestIndex = i;
							shortestDistance = pathDistance[i];
						}
					}
					progress = 0;
					mainPathIndex = shortestIndex;
					mainPathDone = true;
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
			RoomEntry ex = baseStartRoom;
			int exInd = ex.getNextUnusedUnFilteredExitIndex();
			ex.exitsUsed[exInd] = true;
			main.removeIndex(0);
			base.addAll(main);
			progress = 0;
			mainPathIndex = 0;
			sidePathIndex++;
			retries = 0;
			if (base.size > TOTAL_ROOMS_TARGET) {
				progress = 5;
				for (int x = 0; x < map.width; x++)
					for (int y = 0; y < map.height; y++)
						map.setLocal(x, y, 0);
				writeToMap(base, 1024);
				main.clear();
				baseStartRoom = getNextAvailableBaseRoom();
				if (baseStartRoom == null) throw new GdxRuntimeException("ar null ");
				main.add(baseStartRoom);
			}
			break;

			case 5://end room
				Gdx.app.log(TAG, "end room try");
				rooms = endRooms;
				done = makeRooms(30, 1, null);
				if (done){
					progress++;
					RoomEntry end = main.peek();
					Gdx.app.log(TAG, "end room done "+ end.room.blocks.length);
					base.peek().markAllExitsUsed();
				}
				else {
					main.clear();
					baseStartRoom = getNextAvailableBaseRoom();
					if (baseStartRoom == null) throw new GdxRuntimeException("ar null ");
					main.add(baseStartRoom);
					main.peek().teleportOut[main.peek().getNextUnusedUnFilteredExitIndex()] = true;
					//main.clear();
					//progress = 0;
					//skipResetSeed = true;
					//Gdx.app.log(TAG, "RETRY" + main.size + "  " + mainPathIndex + "  " + retries);
				}


				break;
		case 6://move current path to base
			//ex = baseStartRoom;
			//exInd = ex.getNextUnusedUnFilteredExitIndex();
			//ex.exitsUsed[exInd] = true;
			main.removeIndex(0);
			base.addAll(main);
			//progress = 0;
			//mainPathIndex = 0;
			//sidePathIndex++;
			//retries = 0;
			/*if (base.size > TOTAL_ROOMS_TARGET) {
				progress = 5;

			}*/
			progress = 55;
			break;
		case 7://small secondary path rooms

			break;
		case 55://finalize
			//Gdx.app.log(TAG, "DONEFIFFNIFNIFNFINIFNIFNFINFNIODODODODONDONDONEONDOEOnDONEDOenDONEOEDNEDONEDONENOD");
			for (int x = 0; x < map.width; x++)
				for (int y = 0; y < map.height; y++)
					map.setLocal(x, y, 1024 + r.nextInt(64));
			
			writeToMap(base, 1024, true);
			for (int x = 0; x < map.width; x++)
				for (int y = 0; y < map.height; y++){
					map.setBGLocal(x, y, Blocks.STONE+r.nextInt(64));
				}
			for (int x = 0; x < map.width; x++){
				//Gdx.app.log(TAG, "DFJHSKAFJDSKLFJASDKFJDSDLSKJSFDLKJFDSKLJFSDDKLF " + (int) overworld.getHeight((int) (x + map.offset.x)));
				for (int y = (int) overworld.getHeight((int) (x + map.offset.x) )+1; y < map.height; y++){
					map.setLocal(x, y, 0);
					map.setBGLocal(x, y, 0);
				}
			}
			progress++;
			break;
		case 56://end
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
	}
	/*private RoomEntry getNextAvailableBaseRoom() {
		for (RoomEntry r : base){
			for (int i = 0; i < r.room.exit.size; i++){
				if (!r.exitsUsed[i] && r.room.exitFilters.get(i).size == 0){
					//Gdx.app.log(TAG, "avail r " + i + r);
					return r;
				}
			}
		}
		if (true) throw new GdxRuntimeException("no available exits");
		return null;
		//return base.peek();
	}*/
	private RoomEntry getNextAvailableBaseRoom() {
		RoomEntry r = base.peek();
		int index = r.getNextUnusedUnFilteredExitIndex();
		if (index == -1) throw new GdxRuntimeException("no exit");
		return r;
	}


	private void writeToMap(Array<RoomEntry> list, int i) {
		writeToMap(list, i, false);
	}
	private void writeToMap(Array<RoomEntry> list, int i, boolean finalPass) {
		for (RoomEntry r : list){
			writeToMap(r, i, finalPass);
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
	private boolean makeRooms(int tries, int maxRooms, Array<Dist> filters) {
		int count = 0, roomCount = 0, iterationsCount = 0;
		boolean done = false;
		int triesWithoutSuccess = 0;
		while (!done && count < tries && iterationsCount < 500){
			int ind = r.nextInt(rooms.size);
			//Gdx.app.log(TAG, "ind " + rooms.size);
			Room room = rooms.get(ind);
			RoomEntry re = Pools.obtain(RoomEntry.class);
			RoomEntry pre = null;
			pre = main.peek();
			if (room == null) throw new GdxRuntimeException("hklfsd");
			if (pre == null) throw new GdxRuntimeException("hklfsd" + main.size);
			int exitIndex = 0;
			if (filters == null){
				exitIndex= pre.getNextUnusedUnFilteredExitIndex();
			} else {
				exitIndex= pre.getNextUnusedFilteredExitIndex(filters);
			}

			if (exitIndex == -1) throw new GdxRuntimeException("no exits left");
			if (pre.teleportOut[exitIndex]){
				room = startRooms.get(r.nextInt(startRooms.size-1));
			}
			re.room = room;
			//if (r.nextBoolean()) re.teleportOut[re.getNextUnusedExitIndex()] = true;
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
			} else {
				count++;
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
				triesWithoutSuccess = 0;
				//Gdx.app.log(TAG, "map cllear, writing");
				writeToMap(re, 1024);
				roomCount++;
				int exind = main.peek().getNextUnusedUnFilteredExitIndex();
				if (main.size > 1)
					main.peek().exitsUsed[exind] = true;
				main.add(re);
				pre.next[exitIndex] = re;
			} else {
				triesWithoutSuccess++;
				if (triesWithoutSuccess > tries/2)
					pre.teleportOut[exitIndex] = true;
			}
			iterationsCount++;
			if (roomCount >= maxRooms){
				done = true;
			}
			
		}
		return done;
	}



	private boolean mapIsClear(int ax, int ay, int w, int h) {
		//Gdx.app.log(TAG, "clear " + w + " " + h + "  x " + ax + " , " + ay);
		//if (ax < 1 || ay < 1 || ax + w >= map.width-1 || ay + h >= map.height-1) return false;
		if (ax < 1 || ay < 1 || ax + w >= map.width-2 || ay + h >= map.height-TOP_FREE_SPACE-1) return false;
		for (int x = 0; x < w+2; x++)
			for (int y = 0; y < h+2; y++){
				if (map.get(x + ax - 1, y + ay - 1) != 0) return false;
			}
		return true;
	}
	private void writeToMap(RoomEntry entry, int i) {
		writeToMap(entry, i, false);
	}
	private void writeToMap(RoomEntry entry, int i, boolean finalPass) {

		//Gdx.app.log(TAG, "write " + entry.offset + " " + entry.room.blocks[0].length + " , " + entry.room.blocks.length);
		for (int x = 0; x < entry.room.blocks[0].length; x++)
			for (int y = 0; y < entry.room.blocks.length; y++){
				map.setLocal(x+entry.offset.x, y + entry.offset.y, i);
			}
		if (finalPass){
			for (int x = 0; x < entry.room.blocks[0].length; x++)
				for (int y = 0; y < entry.room.blocks.length; y++){
					//map.setBGLocal(x+entry.offset.x, y + entry.offset.y, i);
				}
		}
		if (expand){
			int dx = 0, dy = 0;
			int exitIndex = entry.getNextUnusedUnFilteredExitIndex();
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
			if (finalPass){
				if (entry.teleportOut[exitIndex]){
					makeDoor(entry, exitIndex);
				} else {
					
				}
				if (entry.getNextUnusedUnFilteredExitIndex() != -1){
					Gdx.app.log(TAG, "unused exit" + entry.getNextUnusedUnFilteredExitIndex() +"  " + + entry.room.blocks.length + "," + entry.room.blocks[0].length);
				}
				//write actual room blocks
				for (int x = 0; x < entry.room.blocks[0].length; x++)
					for (int y = 0; y < entry.room.blocks.length; y++){
						int b = entry.room.blocks[entry.room.blocks.length-y-1][entry.room.blocks[0].length-1-x];
						
						BlockDistributionArray d = entry.room.distributions.get(b);
					//Gdx.app.log(TAG, "dist " + d);
						
						BlockDistributionArray srcDist = entry.room.distributions.get(b);
						float total = srcDist.getTotalWeight();
						
						float targetWeight = r.nextFloat() * total;
						total = 0f;
						BlockDistribution result = srcDist.getItemAtWeight(targetWeight);
						boolean done = false;
						int blockID = 0;
						switch (result.value){
						case ENTRANCE:
						case EXIT:
							break;
						case BLOCKA:
						case BLOCKB:
							blockID = blockAid + r.nextInt(64);
							break;
						case EMPTY:
							blockID = 0;
							break;
						case LADDER:
							blockID = 1024;
							break;

						}



						map.setLocal(x+entry.offset.x, y + entry.offset.y, blockID);
						//map.setLocal(x+entry.offset.x, y + entry.offset.y, i);
						for (BlockDistribution dd : srcDist.val){
							switch (dd.value){
								case SPAWN_SMALL:
									makeSpawnMarker(x+entry.offset.x, y + entry.offset.y, MonsterSpawn.SMALL);
									break;
								case SPAWN_MEDIUM:
									makeSpawnMarker(x+entry.offset.x, y + entry.offset.y, MonsterSpawn.MEDIUM);
									break;
								case SPAWN_MINOR_BOSS:
									makeSpawnMarker(x+entry.offset.x, y + entry.offset.y, MonsterSpawn.MINOR_BOSS);
									break;
							}
						}
						//special blocks


					}
				map.setLocal(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy, 0);
				map.setLocal(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy + 1, 0);
				//map.setBGLocal(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy, 0);
				//map.setBGLocal(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy + 1, 0);
				
			}
		}
	}

	private void makeSpawnMarker(int x, int y, int type) {
		Entity e = parent.engine.createEntity();
		Position pos = parent.engine.createComponent(Position.class);
		pos.pos.set(x + .5f, y);
		e.add(pos);
		MonsterSpawn spawn = parent.engine.createComponent(MonsterSpawn.class);
		spawn.type = type;


		parent.engine.addEntity(e);
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
			Gdx.app.log(TAG, "door " + pos);
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
			Gdx.app.log(TAG, "door " + pos);
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
			//door.endPoint.setLocal(next.offset);
			//e.add(door);
			parent.engine.addEntity(e);
		}
		//map.setLocal(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy + 1, i);										
		
	}
	public Action after;
	public int z;
	private Array<Room> rooms, bigRooms = new Array<Room>(), smallRooms = new Array<Room>(), startRooms = new Array<Room>(), endRooms = new Array<Room>();;
	private Random r;
	private int mainPathIndex;
	private int sidePathIndex;
	private int totalIterations;
	private ProgressBarSystem progressSys;
	private int blockAid;
	private int blockBid;

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
		
		//progressSys.setProgressBar(progressBarIndex, 1f);
		progressSys.deregisterProgressBar(progressBarIndex);

		map = null;
		addAfterMe(after);
		Gdx.app.log(TAG, " time to gen = " + (System.currentTimeMillis() - startTime)/1000f + "  " + endRooms.size);
	}


	@Override
	public void onStart() {
		startTime = System.currentTimeMillis();
		//Gdx.app.log(TAG, "start"+map.offset + bit);
		progress = 0;
		sidePathIndex = 0;
		this.overworld = parent.engine.getSystem(OverworldSystem.class);
		parent.engine.getSystem(RoomCatalogSystem.class).getRoomsForTags(smallRooms, "easy");
		parent.engine.getSystem(RoomCatalogSystem.class).getRoomsForTags(bigRooms, "easybig");
		parent.engine.getSystem(RoomCatalogSystem.class).getRoomsForTags(startRooms, "easystart");
		parent.engine.getSystem(RoomCatalogSystem.class).getRoomsForTags(endRooms, "easyend");
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
		blockAid = 1024;
		blockBid = 1024+64;
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
