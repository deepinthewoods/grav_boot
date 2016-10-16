package com.niz;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.niz.component.Race;
import com.niz.system.EntitySerializationSystem;
import com.niz.system.OverworldSystem;
import com.niz.system.WorkerSystem;
import com.niz.ui.elements.BeltTable;

public abstract class Factory {
	public int level;
	public int sideLevel = 0;
	public WorldDefinition def;
	public abstract void createPlayer(EngineNiz engine, Array<PooledEntity> playerArr, WorldDefinition def);
	
	
	
	public abstract void startMap(EngineNiz engine);
	
	public abstract void startPlayer(EngineNiz engine, Entity e);

	

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
	
}
