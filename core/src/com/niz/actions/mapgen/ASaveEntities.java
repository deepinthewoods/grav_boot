package com.niz.actions.mapgen;

import java.io.DataOutputStream;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pools;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.niz.Data;
import com.niz.WorldDefinition;
import com.niz.action.ProgressAction;
import com.niz.component.Map;
import com.niz.component.Player;
import com.niz.component.TransientComponent;
import com.niz.system.ProgressBarSystem;


public class ASaveEntities extends ProgressAction {

	public static final int BUFFER_SIZE = 128;
	private static final String TAG = "save e actin";
	public Array<PooledEntity> entities;
	public Map map;
	public WorldDefinition def;
	public int bit;
	private FileHandle file;
	private DataOutputStream stream;
	public Json json;
	private Kryo kryo;
	private Output output = new Output(BUFFER_SIZE);
	private int progress;
	static ComponentMapper<TransientComponent>transientM = ComponentMapper.getFor(TransientComponent.class);
	private ProgressBarSystem progressSys;
	static ComponentMapper<Player>playerM = ComponentMapper.getFor(Player.class);

	@Override
	public void update(float dt) {
		if (progress >= entities.size) {
			isFinished = true;
			//Gdx.app.log("save e action", "finished");
			return;
		}
		PooledEntity e = entities.get(progress++);
		if (playerM.get(e) == null){
			
			writeEntity(e, output, kryo);
		}
		float progressDelta = progress / (float)entities.size;
		progressSys.setProgressBar(progressBarIndex, progressDelta * .5f + .5f);
	}

	public static void writeEntity(PooledEntity e, Output output, Kryo kryo) {
		if (transientM.get(e) == null){
			ImmutableArray<Component> components = e.getComponents();
			int size = components.size();
			
			output.writeLong(e.getId());
			output.writeInt(size);
			for (int i = 0; i < size; i++){
				Component c = components.get(i);
				//Gdx.app.log(TAG, "writing" + c.getClass());
				kryo.writeClassAndObject(output, c);
			}
			
		}
	}

	@Override
	public void onEnd() {
		output.close();
		for (int i = 0; i < entities.size; i++){
			parent.engine.freeEntity(entities.get(i));
		}
		entities.clear();
		Data.kryoPool.release(kryo);
		Pools.free(entities);
		entities = null;
		progressSys.deregisterProgressBar(progressBarIndex);
	}
	
	@Override
	public void onStart() {
		
		progress = 0;
		file = def.folder.child("entities"+bit);
		kryo = Data.kryoPool.borrow();
		output.setOutputStream(file.write(false));
		progressSys = parent.engine.getSystem(ProgressBarSystem.class);
		output.writeInt(entities.size);
	}

}
