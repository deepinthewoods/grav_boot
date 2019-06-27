package com.niz.system;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DataInput;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.niz.Data;
import com.niz.Main;
import com.niz.room.BlockDistribution;
import com.niz.room.BlockDistributionArray;
import com.niz.room.Dist;
import com.niz.room.Room;

public class RoomCatalogSystem extends EntitySystem {
	private static final String TAG = "room catalog system";
	IntMap<Array<Room>> roomsByTag = new IntMap<Array<Room>>();
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		//writeTemplates();
		//readRooms(Gdx.files.internal(Data.FILE_PATH_PREFIX).child("/rooms/"));
		
		readRooms(Gdx.files.internal("assets/rooms"));
	}
	
	private void readRooms(FileHandle child) {
		//child.mkdirs();
		/*FileHandle vfile = child.child("version.nfo");
		boolean copy = false;
		int version = 0;
		if (!vfile.exists()){
			copy = true;
		} else {
			DataInputStream vin = new DataInput(vfile.read());
			
			try {
				version = vin.readInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//ersion = 0;
			}
			if (version != Main.ROOMS_VERSION) copy = true;
		}
		if (copy){
			DataOutputStream fout = new DataOutputStream(vfile.write(false));
			try {
				fout.writeInt(Main.ROOMS_VERSION);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}*/
		//child.child("TESTTEST.TEST").writeString("test!", false);
		//try {
			//Gdx.app.log(TAG, "READ ROOMS" + child.file().getAbsolutePath() + " " + child.list().length);
		//} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
			
		if (Gdx.app.getType() == ApplicationType.Desktop && !Main.isRelease){
				FileHandle roomF = Gdx.files.internal("rooms");
				FileHandle roomEF = Gdx.files.absolute(roomF.file().getAbsolutePath());
				String s = "";
				for (FileHandle f : roomEF.list()){
					if (f.isDirectory()){
						for (FileHandle f2 : f.list()){
							s += "rooms\\"+ f2.name() + "\\" + f.name();
							s += "\n";
						}
					}
					s += "rooms\\"+f.name();
					s += "\n";
				}
				FileHandle out = Gdx.files.internal("roomslist.txt");
				FileHandle outA = Gdx.files.absolute(out.file().getAbsolutePath());
				outA.writeString(s, false);
				//Gdx.app.log(TAG, "write list of rooms " + roomF.file().getAbsolutePath());
		}
		
		Json json = Data.json;
		String fileList = Gdx.files.internal("roomslist.txt").readString();
		String[] names = fileList.split("\n");
		//Gdx.app.log(TAG, "ttitititi " + names.length);
		for (String name : names){
			FileHandle f = Gdx.files.internal(name);
			if (!f.extension().contains("json")){
				//Gdx.app.log(TAG, "skipping " + f.name() + "  extension " + f.extension());
				continue;
			}
			if (!f.exists()){
				//Gdx.app.log(TAG, "skipping  doesn't exist " + f);
				continue;
			}
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
				//Gdx.app.log(TAG, "room in " + s + arr.size + " " + r);
				
			}
		}
		//child.mkdirs();
		//child.child("TESTTEST.TEST").writeString("test!", false);
	}
	
	public Array<Room> getRoomsForTag(int tag){
		return roomsByTag.get(tag);
	}
	
	public Array<Room> getRoomsForTags(Array<Room> returnArr, String... tags){
		returnArr.clear();
		Array<Room> arr = roomsByTag.get(Data.hash(tags[0]));
		//if (arr == null) return returnArr;
		for (Room r : arr){
			boolean found = true;
			for (int i = 0; i < tags.length; i++){
				if (!r.tags.contains(tags[i], false)) found = false;
			}
			if (found) returnArr.add(r);
		}
		return returnArr;
	}
	
	private void writeTempla3tes() {
		Json json = Data.json;
		BlockDistributionArray emptyArr = new BlockDistributionArray();
		BlockDistribution dr = new BlockDistribution();
		dr.value = Dist.EMPTY;
		
		emptyArr.add(dr);
		for (int x = 1; x < 80; x++)
			for (int y = 1; y < 80; y++){
				Room room = new Room();
				room.blocks = new int[x][y];
				BlockDistributionArray arr = new BlockDistributionArray();
				BlockDistribution d = new BlockDistribution();
				d.value = Dist.ENTRANCE;
				arr.add(d);
				d = new BlockDistribution();
				d.value = Dist.EMPTY;
				arr.add(d);
				room.distributions.put(0, emptyArr);
				room.distributions.put(8, arr);
				
				arr = new BlockDistributionArray();
				d = new BlockDistribution();
				d.value = Dist.EXIT;
				arr.add(d);
				d = new BlockDistribution();
				d.value = Dist.EMPTY;
				arr.add(d);
				room.distributions.put(9, arr);

				arr = new BlockDistributionArray();
				d = new BlockDistribution();
				d.value = Dist.BLOCKA;
				arr.add(d);
				room.distributions.put(1, arr);
				
				arr = new BlockDistributionArray();
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
