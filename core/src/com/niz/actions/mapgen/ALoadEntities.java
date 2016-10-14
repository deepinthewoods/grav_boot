package com.niz.actions.mapgen;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.niz.Data;
import com.niz.WorldDefinition;
import com.niz.action.ProgressAction;
import com.niz.component.Map;
import com.niz.component.TransientComponent;
import com.niz.system.ProgressBarSystem;
import com.niz.system.WorkerSystem;

public class ALoadEntities extends ProgressAction {

	private static final String TAG = "load entities action";
	public int bit;
	public Map map;
	public WorldDefinition def;
	Array<PooledEntity> entities;
	ComponentMapper<TransientComponent>transientM = ComponentMapper.getFor(TransientComponent.class);

	@Override
	public void update(float dt) {
		//PooledEntity e = entities.get(progress++);
		
		if (input.eof()){
			isFinished = true;
			return;
		}
		
		
		entities.add(readEntity(input, kryo, parent.engine));
			
		
		
		float progressDelta = progress / (float)entityTotal;
		progressSys.setProgressBar(progressBarIndex, progressDelta * .5f + .5f);
		
		progress++;
	}

	public static final PooledEntity readEntity(Input input, Kryo kryo, EngineNiz engine) {

		long eID = input.readLong();
		int size = input.readInt();
		//Gdx.app.log(TAG, "read entity"+size);
		PooledEntity e = engine.createEntity();
		e.setUUID(eID);
		for (int i = 0; i < size; i++){
			try {
				Registration cl = kryo.readClass(input);
				
				Gdx.app.log(TAG,  "reading " + cl.getType());
				Component c = (Component) kryo.readObject( input, cl.getType());
				e.add(c);
				
			} catch (Exception ex){
				Gdx.app.log(TAG,  "Exception "+ex.getClass() + ex.toString());
				throw new GdxRuntimeException("ex");
			}
			//Gdx.app.log(TAG, "read component"+c.getClass());
		}
		return e;
	}
	

	@Override
	public void onEnd() {
		input.close();
		//entities.clear();
		Data.kryoPool.release(kryo);
		progressSys.deregisterProgressBar(progressBarIndex);
		parent.engine.getSystem(WorkerSystem.class).addEntities(entities);
	}
	Input input = new Input(ASaveEntities.BUFFER_SIZE);
	private Kryo kryo;
	private FileHandle file;
	private int progress;
	private ProgressBarSystem progressSys;
	private int entityTotal;

	@Override
	public void onStart() {
		entities = Data.entityArrayPool.obtain();
		progress = 0;
		file = def.folder.child("entities"+bit);
		input.setInputStream(file.read());
		kryo = Data.kryoPool.borrow();
		progressSys = parent.engine.getSystem(ProgressBarSystem.class);
		entityTotal = input.readInt();
		
		
	}

}
