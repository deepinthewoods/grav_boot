package com.niz;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.action.ActionList;
import com.niz.actions.mapgen.AAgentBuildMap;
import com.niz.actions.mapgen.AGenerateEntities;
import com.niz.component.Map;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SelectedPlayer;
import com.niz.system.EntitySerializationSystem;
import com.niz.system.OverworldSystem;
import com.niz.system.WorkerSystem;
import com.niz.ui.elements.BeltTable;

public abstract class Factory {
	public int level;
	public int sideLevel = 0;
	public WorldDefinition def;

	
	

	


	

	public void save(EngineNiz engine, BeltTable beltTable) {
		engine.getSystem(OverworldSystem.class).saveAllNow();;
		engine.getSystem(WorkerSystem.class).saveAllNow();;
		engine.getSystem(EntitySerializationSystem.class).saveAllNow(beltTable);
	}

	public void loadPlayer(EngineNiz engine, FileHandle playerFile, BeltTable beltTable, Array<PooledEntity> playerArr){
		engine.getSystem(EntitySerializationSystem.class).loadGame(beltTable, playerArr);
		//playerArr.add(createCamera(engine));
	}



	public abstract PooledEntity createCamera(EngineNiz engine);



	public abstract void makeLevelSelection(EngineNiz engine, WorldDefinition worldDef);



	public abstract void selected(EngineNiz engine, SelectedPlayer sel, Entity e);



	public void createMapGenerationAgent() {}



	public abstract void createMapGenerationAgent(PooledEntity e, EngineNiz engine, Map map, int bit, int z) ;



	
	
}
