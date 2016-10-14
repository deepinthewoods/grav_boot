package com.niz.actions.mapgen;

import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.niz.Blocks;
import com.niz.action.Action;
import com.niz.component.LevelEntrance;
import com.niz.component.Map;
import com.niz.component.Position;
import com.niz.component.RoomDefinition;
import com.niz.system.OverworldSystem;

public class AAgentBuildMap2 extends Action {
	//public int seed;
	int progress;
	public Map map;
	private OverworldSystem overworld;
	public int bit;
	public final static int ITERATIONS = 128;
	private static final String TAG = "build map action";
	@Override
	public void update(float dt) {
		int x = progress / map.width;
		//x += map.offset.x;
		//Gdx.app.log(TAG, "tick "+x);

		//float height = overworld.getHeight(x, z, 1f);
		//for (int x = 0; x < map.width; x++)
		for (int y = 0; y < map.height; y++){
						
				//if (y != OverworldSystem.SCROLLING_MAP_WIDTH/2 || x != OverworldSystem.SCROLLING_MAP_HEIGHT/2)map.set(x, y, Blocks.STONE 
				//			);
				//else 
					map.set(x, y, 0);
			
			progress++;			
		}
		PooledEntity en = parent.engine.createEntity();
		Position ePos = parent.engine.createComponent(Position.class);
		ePos.pos.set(10, 10);
		LevelEntrance entrance = parent.engine.createComponent(LevelEntrance.class);
		
		en.add(ePos).add(entrance);
		parent.engine.addEntity(en);;
		
		if (overworld.worldDef.isRoomEditor){
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
		//if (progress > map.width)
		isFinished = true;
	}
	public Action after;
	public int z;
	@Override
	public void onEnd() {
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
		this.overworld = parent.engine.getSystem(OverworldSystem.class);
		
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
