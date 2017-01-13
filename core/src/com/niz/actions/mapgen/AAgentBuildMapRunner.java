package com.niz.actions.mapgen;

import java.util.Random;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;
import com.niz.RunnerFactory;
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
import com.niz.room.BlockDistribution;
import com.niz.room.BlockDistributionArray;
import com.niz.room.Room;
import com.niz.system.OverworldSystem;
import com.niz.system.ProgressBarSystem;
import com.niz.system.RoomCatalogSystem;

public class AAgentBuildMapRunner extends ProgressAction {
	public enum DistanceType {
		START_TO_END_DIST
		, TOTAL_AREA
		
	}

	//public int seed;
	int progress;
	public Map map;
	private OverworldSystem overworld;
	public int bit;
	private static final String TAG = "build runner map action";

	private int totalIterations;
	private ProgressBarSystem progressSys;
	public RunnerFactory factory;
	@Override
	public void update(float dt) {
		//int x = progress / map.width;
		//x += map.offset.x;
		//Gdx.app.log(TAG, "tick "+x);

		//float height = overworld.getHeight(x, z, 1f);
		//for (int x = 0; x < map.width; x++)
		
		totalIterations++;
		
		float progressDelta = 0f;
		progressSys.setProgressBar(progressBarIndex, progressDelta);

		//if (progress > map.width)
		isFinished = true;
	}

	@Override
	public void onEnd() {
		float progressDelta = 1f;
		progressSys.setProgressBar(progressBarIndex, progressDelta);
		progressSys.deregisterProgressBar(progressBarIndex);
		overworld.onFinishedMap(bit, map);
		map = null;
		//addAfterMe(after);	
		createAgent(parent.engine);
	}

	@Override
	public void onStart() {
		progressSys = parent.engine.getSystem(ProgressBarSystem.class);
		this.overworld = parent.engine.getSystem(OverworldSystem.class);

	}

	private void createAgent(EngineNiz engine) {
		PooledEntity e = engine.createEntity();
		factory.createRunningAgent(engine);
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
