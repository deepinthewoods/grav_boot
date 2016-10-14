package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;
import com.niz.BlockDefinition;
import com.niz.component.Map;

public class RandomMapUpdateSystem extends EntitySystem {

	private static final int NUMBER_OF_UPDATES = 1;
	private static final float INTERVAL = .00125f;
	private static final String TAG = "random update sys";
	private int progress;

	private IntArray randoms = new IntArray();
	float time;
	private ComponentMapper<Map> mapM;
	private ImmutableArray<Entity> entities;
	private static BlockDefinition[] defs;
	private static BlockDefinition def;
	@Override
	public void addedToEngine(Engine engine) {
		MapSystem map = engine.getSystem(MapSystem.class);
		super.addedToEngine(engine);
		for (int i = 0; i < 256; i++){
			randoms.add(i);
		}
		defs = map.defs;
		randoms.shuffle();
		mapM = ComponentMapper.getFor(Map.class);
		Family family = Family.one(Map.class).get();
		entities = engine.getEntitiesFor(family);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		for (int ind = 0; ind < entities.size(); ind++){
			Entity e = entities.get(ind);
			Map map = mapM.get(e);
			if (map == null) return;
			time += deltaTime;
			int interval, ix, iy;
			while (time > INTERVAL){
				time -= INTERVAL;
				int nx, ny, i, y, x;
				for (i = 0; i < NUMBER_OF_UPDATES; i++){
					interval = randoms.get(progress);
					ix = interval % 16;
					iy = interval / 16;;
					for (y = 0; y < map.height / 16; y++)
						for (x = 0; x < map.width / 16; x++){
							nx = ix+x*16;
							ny = iy+y*16;
							updateMap(nx, ny, map);
						}
					
					progress++;
					if (progress >= 128){
						randoms.shuffle();
						progress = 0;
					}
				}
			}
				
			}
		//Gdx.app.log(TAG, "sup");w
	}

	private static final void updateMap(int x, int y, Map map) {
		int b = map.tiles[y+x*map.width];
		int id = (b & map.ID_MASK) >> map.ID_BITS;
		//Gdx.app.log(TAG, "sdhfjsk#"+x+" , "+id);
		defs[id].randomUpdate(map, x+(int)map.offset.x, y+(int)map.offset.y, b);
		/*if (id == 0){
			if (y == Map.height-1){
				
				map.set(x,y,513);
				
				
			}
		}*/
	}

}
