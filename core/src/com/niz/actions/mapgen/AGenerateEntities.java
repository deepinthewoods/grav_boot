package com.niz.actions.mapgen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.niz.BlockDefinition;
import com.niz.Blocks;
import com.niz.PlatformerFactory;
import com.niz.WorldDefinition;
import com.niz.action.Action;
import com.niz.component.Map;
import com.niz.component.Position;
import com.niz.system.OverworldSystem;

public class AGenerateEntities extends Action {

	public int x, y, w, h;
	public Map map;
	public WorldDefinition def;
	private int progress;
	private int progressTarget;
	private GridPoint2 pt = new GridPoint2();
	public int z;
	public Array<GridPoint2> mobs = new Array<GridPoint2>();
	private OverworldSystem overworld;

	@Override
	public void update(float dt) {
    	int count = 0;
    	int targetMobs = 25;
    	int mobCount = 0;
    	mobs.clear();
    	boolean done = false;
    	while (0 < 10000 && mobCount < targetMobs){
			pt.set(MathUtils.random(map.width-1), MathUtils.random(map.height-AAgentBuildMap.TOP_FREE_SPACE-2));
			if (isValidBlock(map, pt)){
				GridPoint2 n = Pools.obtain(GridPoint2.class);
				n.set(pt);
				mobs.add(n);
				mobCount++;
				Entity mob = overworld.generateMob(z, PlatformerFactory.MobSpawnType.REGULAR, parent.engine);
				mob.getComponent(Position.class).pos.set(pt.x+ .5f, pt.y + .25f);
				parent.engine.addEntity(mob);
			}
			count++;
		}
		//TODO spawn points from room definitions




		//if (++progress >= progressTarget)
			isFinished = true;
	}

	private boolean isValidBlock(Map map, GridPoint2 pt) {

		boolean valid = false;
		int block = map.get(pt.x, pt.y);
		int blockDown = map.get(pt.x, pt.y-1);
		int blockUp = map.get(pt.x, pt.y+1);
		block &= Map.ID_MASK;
		blockDown &= Map.ID_MASK;
		blockUp &= Map.ID_MASK;
		block >>= Map.ID_BITS;
		blockUp >>= Map.ID_BITS;
		blockDown >>= Map.ID_BITS;
		BlockDefinition d = map.defs[blockDown];
		BlockDefinition u = map.defs[blockUp];
		BlockDefinition b = map.defs[block];
		if (block == Blocks.AIR && d.isSolid && blockUp == Blocks.AIR) valid = true;

		return valid;
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		progress = 0;

		overworld = parent.engine.getSystem(OverworldSystem.class);
	}

}
