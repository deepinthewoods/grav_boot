package com.niz.actions.mapgen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.niz.Blocks;
import com.niz.SimplexNoise;
import com.niz.action.Action;
import com.niz.component.Map;
import com.niz.system.OverworldSystem;

public class ABuildMap extends Action {
	//public int seed;
	int progress;
	public Map map;
	private OverworldSystem overworld;
	public int bit;
	public final static int ITERATIONS = 128;
	private static final String TAG = "build map action";
	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG, "tick "+overworld.getHeight(100));
		int x = progress / map.width;
		x += map.offset.x;

		float height = overworld.getHeight(x, z, 1f);
		for (int it = 0; it < ITERATIONS; it++){
			if (progress >= map.width * map.height){
				isFinished = true;

				return;
			};
			//Gdx.app.log(TAG, "active"+map.offset);
			int y = progress % map.width;
			y += map.offset.y;
			//if (height > y){
				//if (MathUtils.randomBoolean())
					map.set(x, y, Blocks.STONE + MathUtils.random(63)
							);
				//else map.set(x, y, Blocks.DIRT );
				
			//} else map.set(x, y, 0);
			
			progress++;
			
		}
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
