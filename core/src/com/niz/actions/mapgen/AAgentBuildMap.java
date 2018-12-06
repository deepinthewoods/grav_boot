package com.niz.actions.mapgen;

import java.util.Comparator;
import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
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

	private static final int TOTAL_ROOMS_TARGET = 10;
	private static final int MAIN_PATH_COMPARES = 2;


	public static final int SECONDARY_ROOM_SEGMENT_SIZE = 4;
	public static final int SECONDARY_ROOM_TRIES = 10;

	private static final int NUMBER_OF_ROOMS_TO_COMPARE = 3;
	private long startTime;
	private RoomEntry baseStartRoom;
	private int baseStartExitIndex;
	private RoomEntry endRoom;
	private int shortestDistance;
	private RoomEntry oldBaseStartRoom;

	public enum DistanceType {
		START_TO_END_DIST
		, TOTAL_AREA
		, CLOSEST_TO_END_ROOM
	}

	//public int seed;
	int progress;

	public Map map;
	private OverworldSystem overworld;
	public int bit;
	public final static int ITERATIONS = 64;
	private static final String TAG = "build map action";

	private static final int TOP_FREE_SPACE = 40;
	private int teleportDiameter = 50;
	private Array<RoomEntry> main = new Array<RoomEntry>(true, 16), branch = new Array<RoomEntry>(true, 16)
			, base = new Array<RoomEntry>(true, 16);
	private boolean expand = true, twoBlocksHigh = true;
	private int seed;
	private boolean skipResetSeed;
	private int[] pathDistance = new int[18];
	private int[] sidePathIndices = new int[18];

	private boolean mainPathDone;
	private int retries;
	@Override
	public void update(float dt) {
		//int x = progress / map.width;
		//x += map.offset.x;
		//Gdx.app.log(TAG, "tick "+progress);

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
				baseStartExitIndex = baseStartRoom.getNextUnusedUnFilteredExitIndex();
				if (baseStartRoom == null) throw new GdxRuntimeException("ar null ");
				main.add(baseStartRoom);
				progress++;
				break;
			case 2://make small rooms
				rooms = smallRooms;
				boolean done = makeRooms(4 * TOTAL_ROOMS_TARGET, TOTAL_ROOMS_TARGET/3+1, false, false);
				if (done){
					progress++;
				}
				else {
					//main.clear();
					progress = 0;
					skipResetSeed = true;

				}

				break;
			case 3://big room + distance heuristic
				//Gdx.app.log(TAG, "expand");
				rooms = bigRooms;
				done = false;

				done = makeRooms(30, 1, false, false);
				if (done){
					retries = 0;
					progress = 0;
					rooms = smallRooms;
					int dist = getDistance(main, AAgentBuildMap.DistanceType.TOTAL_AREA);
					boolean greater = false;
					pathDistance [mainPathIndex] = dist;
					//mainPathIndex++;
					if (mainPathDone){
						progress = 4;
						//Gdx.app.log(TAG, "main path done" + progress);
						break;
					}
					if (mainPathIndex++ >= MAIN_PATH_COMPARES-1){
						int shortestIndex = 0;
						int shortestDistance = pathDistance[0];
						for (int i = 1; i < MAIN_PATH_COMPARES; i++){
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
				//RoomEntry ex = baseStartRoom;
				//int exInd = ex.getNextUnusedUnFilteredExitIndex();
				baseStartRoom.exitsUsed[baseStartExitIndex] = true;
				main.removeIndex(0);
				base.addAll(main);
				progress = 0;
				mainPathIndex = 0;
				sidePathIndex++;
				retries = 0;
				if (base.size > TOTAL_ROOMS_TARGET) {
					progress = 5;

				}
				break;

			case 5://end room
				Gdx.app.log(TAG, "end room try");
				for (int x = 0; x < map.width; x++)
					for (int y = 0; y < map.height; y++)
						map.setLocal(x, y, 0);
				writeToMap(base, 1024);
				main.clear();
				baseStartRoom = getNextAvailableBaseRoom();
				baseStartExitIndex = baseStartRoom.getNextUnusedUnFilteredExitIndex();
				if (baseStartRoom == null) throw new GdxRuntimeException("ar null ");
				main.add(baseStartRoom);

				rooms = endRooms;
				done = makeRooms(60, 1, false, false);
				if (done){
					progress++;
					RoomEntry end = main.peek();
					Gdx.app.log(TAG, "end room done "+ end.room.blocks.length);
					main.peek().markAllExitsUsed();
					endRoom = main.peek();
				}
				else {
					/*main.clear();
					baseStartRoom = getNextAvailableBaseRoom();
					if (baseStartRoom == null) throw new GdxRuntimeException("ar null ");
					main.add(baseStartRoom);
					main.peek().teleportOut[main.peek().getNextUnusedUnFilteredExitIndex()] = true;//*/
					//Gdx.app.log(TAG, "RETRY" + main.size + "  " + mainPathIndex + "  " + retries);
				}


				break;
			case 6://move current path to base

				main.removeIndex(0);
				base.addAll(main);
				baseStartRoom.exitsUsed[baseStartExitIndex] = true;
				progress = 7;//55;//
				mainPathDone = false;
				mainPathIndex = 0;
				sidePathIndex = 0;
				break;
			case 7://secondary/filtered rooms start
				for (int x = 0; x < map.width; x++)
					for (int y = 0; y < map.height; y++)
						map.setLocal(x, y, 0);
				writeToMap(base, 1024);
				main.clear();
				baseStartRoom = getNextAvailableRoom();

				if (baseStartRoom == null) {
					progress = 55;
					Gdx.app.log(TAG, "done with secondary rooms");
					break;
				}
				if (baseStartRoom != oldBaseStartRoom){
					Gdx.app.log(TAG, "change start room" + baseStartRoom.stepsFromMainPath);
					oldBaseStartRoom = baseStartRoom;
				}
				//Gdx.app.log(TAG, "start room = " + baseStartRoom.offset + " end room +" + endRoom.offset);
				baseStartExitIndex = baseStartRoom.getNextUnusedExitIndex();
				main.add(baseStartRoom);
				if (skipResetSeed){
					skipResetSeed = false;
					throw new GdxRuntimeException("should never be skipped");
				} else
				{
					int sed = seed + mainPathIndex*37 + (sidePathIndex * 2323);
					r.setSeed(sed);
					//if (mainPathDone)
					//Gdx.app.log(TAG, "start room = " + mainPathIndex + "  seed " + sed+baseStartRoom.offset);
				}
				teleportDiameter = 10;
				progress++;
				break;
			case 8://secondary rooms
				rooms = smallRooms;
				done = makeRooms(SECONDARY_ROOM_TRIES, SECONDARY_ROOM_SEGMENT_SIZE, mainPathDone, true);
				//if (!done) Gdx.app.log(TAG, "failed secondary room" + mainPathIndex);
				//if (done){
					progress++;

				//} else {
				//	sidePathIndex++;
				//	progress = 7;
				//}
				retries++;
				break;
			case 9://end of secondary room + dist heuristic

				retries = 0;
				progress = 7;
				rooms = smallRooms;
				int dist = roomDistance(endRoom, main.peek());;//getDistance(main, DistanceType.CLOSEST_TO_END_ROOM);
				if (main.size < SECONDARY_ROOM_SEGMENT_SIZE-1) dist = 20000;
				//Gdx.app.log(TAG, "made sec rooms " + baseStartRoom.offset + roomDistance(endRoom, main.peek()) + main.peek().offset);
				boolean greater = false;
				pathDistance [mainPathIndex] = dist;

				sidePathIndices[mainPathIndex] = sidePathIndex;
				//mainPathIndex++;
				if (mainPathDone){
					baseStartRoom.exitsUsed[baseStartExitIndex] = true;
					int currentDistance = roomDistance(endRoom, main.peek());

					progress = 10;
					Gdx.app.log(TAG, "side path done" + currentDistance + "  " + main.size);
					if (shortestDistance != currentDistance) throw new GdxRuntimeException("reproduce with seed error" + (shortestDistance - currentDistance));
					break;
				}
				if (mainPathIndex++ >= pathDistance.length-1){
					int shortestIndex = -1, shortestSidePathIndex = 0;
					shortestDistance = 10000;
					for (int i = 0; i < pathDistance.length; i++){
						if (pathDistance[i] < shortestDistance){
							shortestIndex = i;
							shortestDistance = pathDistance[i];
							shortestSidePathIndex = sidePathIndices[i];
						}
					}
					progress = 7;
					mainPathIndex = shortestIndex;
					sidePathIndex = shortestSidePathIndex;
					mainPathDone = true;

					int currentDistance = roomDistance(endRoom, baseStartRoom);
					//Gdx.app.log(TAG, "shortest " + shortestDistance + " current " + currentDistance + " path ind " + mainPathIndex + " "+baseStartRoom.offset);
					if ((shortestDistance >= currentDistance && baseStartRoom.stepsFromMainPath > TOTAL_ROOMS_TARGET) || mainPathIndex == -1){
						//teleport straight to end room
						//RoomEntry room = main.peek();
						baseStartRoom.exitsUsed[baseStartExitIndex] = true;
						baseStartRoom.next[baseStartExitIndex] = endRoom;
						baseStartRoom.teleportOut[baseStartExitIndex] = true;
						Gdx.app.log(TAG, "FINISH SECONDARY PATH" + currentDistance + baseStartRoom.offset + (mainPathIndex == -1?" CUT SHORT":""));
						main.clear();
						main.add(baseStartRoom);
						progress = 10;
						mainPathDone = false;
					}
				}

				break;
			case 10://move current path to base
				main.removeIndex(0);
				base.addAll(main);
				//if (main.size > 0)	Gdx.app.log(TAG, "move path to base " + main.size + " end " + main.peek().offset);
				//else Gdx.app.log(TAG, "move path to base " + main.size);
			/*if (main.size == 0){

				baseStartRoom.exitsUsed[baseStartExitIndex] = true;
				baseStartRoom.next[baseStartExitIndex] = endRoom;
				Gdx.app.log(TAG, "FINISH SECONDARY PATH 0 " + main.size + baseStartRoom.offset);
				//progress = 10;
			}*/
				mainPathDone = false;
				sidePathIndex = 0;
				mainPathIndex = 0;
				progress = 7;
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
				ePos.pos.set(map.width/2+.5f, map.height/2+1.5f);
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

	private int roomDistance(RoomEntry r1, RoomEntry r2) {
		RoomEntry finalR = main.peek();
		tmp.set(r1.offset).add(r1.room.blocks[0].length * (r1.room.flipped?-1:1), r1.room.blocks.length);
		endRoomPoint.set(r2.offset).add(r2.room.blocks[0].length * (r2.room.flipped?-1:1), r2.room.blocks.length);
		//return Math.abs(tmp.x - endRoomPoint.x) + Math.abs(tmp.y - endRoomPoint.y);
		return (int)tmp.dst(endRoomPoint);

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
	private RoomEntry getNextAvailableRoom() {
		for (RoomEntry room : base){
			int index = room.getNextUnusedExitIndex();
			if (index != -1)
				return room;
		}

		return null;
	}

	private void writeToMap(Array<RoomEntry> list, int i) {
		writeToMap(list, i, false);
	}
	private void writeToMap(Array<RoomEntry> list, int i, boolean finalPass) {
		for (RoomEntry r : list){
			writeToMap(r, i, finalPass);
		}
	}
	private int getDistance(Array<RoomEntry> main, AAgentBuildMap.DistanceType distanceType) {
		if (distanceType == AAgentBuildMap.DistanceType.START_TO_END_DIST){
			RoomEntry finalR = main.peek();
			RoomEntry firstR = main.get(0);
			int dx = Math.abs(finalR.offset.x - firstR.offset.x);
			int dy = Math.abs(finalR.offset.y - firstR.offset.y);
			return dx * dx + dy * dy;
		} else if (distanceType == AAgentBuildMap.DistanceType.TOTAL_AREA){
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
		} else if (distanceType == AAgentBuildMap.DistanceType.CLOSEST_TO_END_ROOM){
			RoomEntry finalR = main.peek();
			//tmp.set(finalR.offset).add(finalR.room.blocks.length * (finalR.room.flipped?-1:1), finalR.room.blocks[0].length);
			//endRoomPoint.set(endRoom.offset).add(endRoom.room.blocks.length * (endRoom.room.flipped?-1:1), endRoom.room.blocks[0].length);

			return roomDistance(finalR, endRoom);
		}
		return 0;
	}
	GridPoint2 tmp = new GridPoint2(), endRoomPoint = new GridPoint2();
	Array<Room> validRooms = new Array<Room>();
	RoomComparator roomComparator = new RoomComparator();
	private boolean makeRooms(int tries, int maxRooms, boolean finalPass, boolean filters) {
		int count = 0, roomCount = 0, iterationsCount = 0;
		boolean done = false;
		int triesWithoutSuccess = 0;
		while (!done && count < tries && iterationsCount < 500) {


			RoomEntry re = Pools.obtain(RoomEntry.class);
			RoomEntry pre = null;
			pre = main.peek();
			//pre = getNextAvailableRoomMain();
			//if (room == null) throw new GdxRuntimeException("hklfsd");
			if (pre == null) throw new GdxRuntimeException("hklfsd" + main.size);
			int exitIndex = 0;
			if (filters) {
				exitIndex = pre.getNextUnusedExitIndex();
			} else {
				exitIndex = pre.getNextUnusedUnFilteredExitIndex();
			}
			if (exitIndex == -1) throw new GdxRuntimeException("no exits left");
			roomComparator.setPre(pre, exitIndex);
			validRooms.clear();
			Room room = null;
			if (filters){
				int ind = 0;
				Room rm = null;
				for (int i = 0; i < NUMBER_OF_ROOMS_TO_COMPARE; i++){
					ind = r.nextInt(rooms.size);
					rm = rooms.get(ind);
					while (rm.exit.size > 1 | !rm.isCompatibleWith(pre, exitIndex)) {
						ind = r.nextInt(rooms.size);
						rm = rooms.get(ind);
					}
					validRooms.add(rm);
				}
				validRooms.sort(roomComparator);
				room = validRooms.peek();
                //Gdx.app.log(TAG, "room filters " + pre.filters);
			} else {
				Room rm = null;
				do {
					int ind = r.nextInt(rooms.size);
					rm = rooms.get(ind);
				} while (!rm.isCompatibleWith(pre, exitIndex));
				room = rm;
			}

			//Gdx.app.log(TAG, "ind " + rooms.size + "  " + room.flipped + "  " + pre.teleportOut[exitIndex]);

			/*if (pre.teleportOut[exitIndex]){
				room = startRooms.get(r.nextInt(startRooms.size-1));
			}*/
			re.room = room;
			//if (r.nextBoolean()) re.teleportOut[re.getNextUnusedExitIndex()] = true;
			GridPoint2 exit = pre.room.exit.get(exitIndex);
			int exDir = Room.getExitBitmask(pre.room, exitIndex);
			//int entDir = Room.getEntranceBitmask(re.room, re.exitIndex);

			boolean found = false;
			boolean teleported = false;

			int entranceIndex = 0;
			int w = re.room.blocks[0].length;
			int h = re.room.blocks.length;
			while (!found && re.room.entrance.size > entranceIndex){

				re.offset.set(pre.offset);
				re.offset.x += exit.x;
				re.offset.y += exit.y;
				re.offset.x -= re.room.entrance.get(entranceIndex).x;
				re.offset.y -= re.room.entrance.get(entranceIndex).y;
				//Gdx.app.log(TAG, "entrance " + re.room.entrance.get(re.entranceIndex) + exit);
				count++;
				if ((exDir & Room.LEFT) != 0) {
					re.offset.x -= expand ? 2 : 1;
					//w++;
				} else if ((exDir & Room.RIGHT) != 0) {
					re.offset.x += expand ? 2 : 1;
					//w--;
				} else if ((exDir & Room.UP) != 0) {
					re.offset.y += expand ? 2 : 1;
					//h--;
				} else if ((exDir & Room.DOWN) != 0) {
					re.offset.y -= expand ? 2 : 1;
					//h++;
				}
				found = mapIsClear(re.offset.x, re.offset.y, w, h);
				entranceIndex++;
			}

			if (!found && triesWithoutSuccess > tries / 2) {
				re.offset.x += r.nextInt(teleportDiameter) - teleportDiameter / 2;
				re.offset.y += r.nextInt(teleportDiameter) - teleportDiameter / 2;
				if (r.nextBoolean())
					teleportDiameter = Math.min(OverworldSystem.SCROLLING_MAP_WIDTH, teleportDiameter + 1);
				found = mapIsClear(re.offset.x, re.offset.y, w, h);
				teleported = true;
			}
			if (found) {
				triesWithoutSuccess = 0;
				//if (finalPass && teleported) Gdx.app.log(TAG, "teleported");

				writeToMap(re, 1024);

				re.filters.putAll(pre.filters);
				re.filters.putAll(pre.room.exitFilters.get(exitIndex));
				if (filters) re.stepsFromMainPath = pre.stepsFromMainPath + 1;
				roomCount++;


				//if (main.size > 1 || finalPass)


				if (finalPass || main.size > 1) {
					pre.exitsUsed[exitIndex] = true;
				}
				if (filters){
					if (finalPass){
						pre.teleportOut[exitIndex] = teleported;
					}
					pre.next[exitIndex] = re;

					//if (teleported) Gdx.app.log(TAG, "TELE");

				} else {
					pre.teleportOut[exitIndex] = teleported;
					//if (teleported) Gdx.app.log(TAG, "TELE");
					pre.next[exitIndex] = re;
				}



				main.add(re);

			} else {
				triesWithoutSuccess++;
				//if (triesWithoutSuccess > tries/2)
					//pre.teleportOut[exitIndex] = true;gdf
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
//		for (int x = 0; x < w; x++)
//			for (int y = 0; y < h; y++){
//				if (map.get(x + ax , y + ay ) != 0) return false;
//			}
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

		if (finalPass){

			for (int x = 0; x < entry.room.exit.size; x++){//exit doors
				if (expand){
					int dx = 0, dy = 0;
					int exitIndex = x;
					//exitIndex = 0;
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

					if (entry.teleportOut[exitIndex]){
						makeDoor(entry, exitIndex);
					} else {
					}
					if (entry.getNextUnusedUnFilteredExitIndex() != -1){
						Gdx.app.log(TAG, "unused exit" + entry.getNextUnusedUnFilteredExitIndex() +"  " + + entry.room.blocks.length + "," + entry.room.blocks[0].length);
					}
					if (entry.teleportOut[exitIndex]){

					} else {
						map.setLocal(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy, 0);
						map.setLocal(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy + 1, 0);
					}

				}

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
		/*if ((exDir & Room.LEFT) != 0){
			dx = -1;
		} else if ((exDir & Room.RIGHT) != 0){
			dx = 1;
		}else
		if ((exDir & Room.UP) != 0){
			dy = 1;
		} else if ((exDir & Room.DOWN) != 0){
			dy = -1;
		}*/

		GridPoint2 exit = entry.room.exit.get(exitIndex);
		GridPoint2 entrance = Pools.obtain(GridPoint2.class);
		RoomEntry nextRoom = entry.next[exitIndex];
		for (int i = 0; i < nextRoom.room.entrance.size; i++){
			GridPoint2 theEntrance = nextRoom.room.entrance.get(i);
			entrance.set(nextRoom.offset).add(theEntrance);
		}

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
			door.endPoint.set(entrance);
			e.add(door);
			parent.engine.addEntity(e);
		}
		{
			Entity e = parent.engine.createEntity();
			Position pos = parent.engine.createComponent(Position.class);
			Gdx.app.log(TAG, "door " + exitIndex);
			//exit = entry.next[exitIndex].room.exit.get(exitIndex);
			pos.pos.set(entrance.x+.5f, entrance.y+1);
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
			//door.endPoint.setLocal(next.offset);
			//e.add(door);
			parent.engine.addEntity(e);
		}
		//map.setLocal(entry.offset.x + exit.x + dx, entry.offset.y + exit.y + dy + 1, i);
		Pools.free(entrance);
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
		endRoom = null;
		rooms = smallRooms;
		RoomEntry re = Pools.obtain(RoomEntry.class);
		int index = r.nextInt(startRooms.size);
		re.room = startRooms.get(index);

		totalIterations = 0;
		re.offset.set(map.width/2, map.height/2);
		base.add(re);
		//re.teleportOut[0] = true;
		progressSys = parent.engine.getSystem(ProgressBarSystem.class);
		blockAid = 1024;
		blockBid = 1024+64;
	}

	private class RoomComparator implements Comparator<Room> {
		private RoomEntry pre;
		private int preExitIndex;

		public void setPre(RoomEntry room, int exitIndex){
			this.pre = room;
			this.preExitIndex = exitIndex;
		}
		@Override
		public int compare(Room r1, Room r2) {
			int c1 = 0, c2 = 0;
			IntMap<Dist> exitF = pre.room.exitFilters.get(preExitIndex);
			IntMap.Entries<Dist> entries = exitF.entries();
			while (entries.hasNext()){
				IntMap.Entry<Dist> entry = entries.next();
				if (r1.entranceFilters.get(0).containsKey(entry.key))
					c1++;
				if (r2.entranceFilters.get(0).containsKey(entry.key))
					c2++;
			}
			entries = pre.filters.entries();
			while (entries.hasNext()){
				IntMap.Entry<Dist> entry = entries.next();
				if (r1.entranceFilters.get(0).containsKey(entry.key))
					c1++;
				if (r2.entranceFilters.get(0).containsKey(entry.key))
					c2++;
			}

			return c1 - c2;
		}
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
