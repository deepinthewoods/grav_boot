package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.niz.component.Buckets;
import com.niz.component.Map;
import com.niz.component.OnMap;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class OnMapSystem extends EntitySystem implements Observer, EntityListener {

	private static final String TAG = "onmap system,";
	private Family family;
	private ImmutableArray<Entity> entities;
	private ComponentMapper<OnMap> mapM;
	public OverworldSystem overworld;
	private Map emptyMap;
	private BucketSystem bucketSystem;
	private ComponentMapper<Buckets> bucketM;
	private ComponentMapper<Position> posM;
	private EngineNiz engine;
	ShaderProgram shader;
	private TextureAtlas atlas;
	public ShaderProgram coeffsShader;
	public ShaderProgram posShader;

    public OnMapSystem(TextureAtlas atlas){
		
		this.atlas = atlas;
		
	}

	@Override
	public void addedToEngine(Engine engine) {
		family = Family.one(OnMap.class).get();
		entities = engine.getEntitiesFor(family);
		mapM = ComponentMapper.getFor(OnMap.class);
		bucketM = ComponentMapper.getFor(Buckets.class);
		posM = ComponentMapper.getFor(Position.class);
		
		bucketSystem = engine.getSystem(BucketSystem.class);
		((EngineNiz)engine).getSubject("changeLargeBuckets").add(this);;
		emptyMap = new Map(1, 1, atlas, shader, coeffsShader, posShader);
		engine.addEntityListener(this);
		this.engine = (EngineNiz) engine;
		

	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		//Gdx.app.log(TAG,  "update");
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			OnMap onMap = mapM.get(e);
			if (onMap.map == null){
				onMap.map = emptyMap;
			}
			
		}
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		OnMap onMap = mapM.get(e);
		Position pos = posM.get(e);
		if (onMap == null) return;
		onMap.map = overworld.getMapFor((int)pos.pos.x, (int)pos.pos.y);
		//Gdx.app.log(TAG, "init onmap"+(onMap.map == null) + overworld.size()+"  " + engine.tick);
		if (onMap.map == null){
			onMap.map = emptyMap;
			//Gdx.app.log(TAG, "empty map");
		}// else Gdx.app.log(TAG, "moved map"+onMap.map.offset + " " + engine.tick);//+bucket.x + ","+bucket.y);
	}

	@Override
	public void entityAdded(Entity entity) {
		onNotify(entity, null, null);
	}

	@Override
	public void entityRemoved(Entity entity) {
	}

	

}
