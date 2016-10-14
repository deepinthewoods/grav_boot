package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.niz.Data;
import com.niz.room.BlockDistribution;
import com.niz.room.Dist;
import com.niz.room.Room;

public class RoomCatalogSystem extends EntitySystem {
	private static final String TAG = "room catalog system";
	IntMap<Array<Room>> roomsByTag = new IntMap<Array<Room>>();
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		//writeTemplates();
		readRooms(Gdx.files.external(Data.FILE_PATH_PREFIX).child("rooms"));
	}
	
	private void readRooms(FileHandle child) {
		Json json = Data.json;
		for (FileHandle f : child.list()){
			Room r = json.fromJson(Room.class, f);
			if (!r.calculatePoints()) throw new GdxRuntimeException("" + f.name());
			Room flip = new Room(r);
			flip.calculatePoints();
			
			//Gdx.app.log(TAG, "file " + f.name());
			for (String s : r.tags){
				//Gdx.app.log(TAG, "room " + s);
				int hash = Data.hash(s);
				if (!roomsByTag.containsKey(hash)) roomsByTag.put(hash, new Array<Room>());
				Array<Room> arr = roomsByTag.get(hash);
				arr.add(r);
				arr.add(flip);
				//Gdx.app.log(TAG, "room in " + s + arr.size + " " + arr);
				
			}
		}
	}
	
	public Array<Room> getRoomsForTag(int tag){
		return roomsByTag.get(tag);
	}
	
	public Array<Room> getRoomsForTags(Array<Room> returnArr, String... tags){
		returnArr.clear();
		Array<Room> arr = roomsByTag.get(Data.hash(tags[0]));
		for (Room r : arr){
			boolean found = true;
			for (int i = 0; i < tags.length; i++){
				if (!r.tags.contains(tags[i], false)) found = false;
			}
			if (found) returnArr.add(r);
		}
		return returnArr;
	}
	
	private void writeTemplates() {
		Json json = Data.json;
		Array<BlockDistribution> emptyArr = new Array<BlockDistribution>();
		BlockDistribution dr = new BlockDistribution();
		dr.value = Dist.EMPTY;
		
		emptyArr.add(dr);
		for (int x = 1; x < 80; x++)
			for (int y = 1; y < 80; y++){
				Room room = new Room();
				room.blocks = new int[x][y];
				Array<BlockDistribution> arr = new Array<BlockDistribution>();
				BlockDistribution d = new BlockDistribution();
				d.value = Dist.ENTRANCE;
				arr.add(d);
				d = new BlockDistribution();
				d.value = Dist.EMPTY;
				arr.add(d);
				room.distributions.put(0, emptyArr);
				room.distributions.put(8, arr);
				
				arr = new Array<BlockDistribution>();
				d = new BlockDistribution();
				d.value = Dist.EXIT;
				arr.add(d);
				d = new BlockDistribution();
				d.value = Dist.EMPTY;
				arr.add(d);
				room.distributions.put(9, arr);

				arr = new Array<BlockDistribution>();
				d = new BlockDistribution();
				d.value = Dist.BLOCKA;
				arr.add(d);
				room.distributions.put(1, arr);
				
				arr = new Array<BlockDistribution>();
				d = new BlockDistribution();
				d.value = Dist.BLOCKB;
				arr.add(d);
				room.distributions.put(2, arr);
				
				room.tags.add("easy");
				Gdx.files.external(Data.FILE_PATH_PREFIX).child("room" + y + "x" + x + ".json")
				.writeString(json.prettyPrint(room), false);

			}
		
	}
}
