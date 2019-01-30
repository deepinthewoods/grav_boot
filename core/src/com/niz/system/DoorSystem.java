package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.niz.component.Body;
import com.niz.component.Door;
import com.niz.component.OnDoor;
import com.niz.component.Player;
import com.niz.component.Position;

public class DoorSystem extends EntitySystem {

	private static final String TAG = "door sys";
	private ImmutableArray<Entity> entities;
	private ImmutableArray<Entity> doorEntities;
	private BucketSystem buckets;
	private EngineNiz engine;
	static ComponentMapper<Door>doorM = ComponentMapper.getFor(Door.class);
	static ComponentMapper<Position>posM = ComponentMapper.getFor(Position.class);
	static ComponentMapper<Body>bodyM = ComponentMapper.getFor(Body.class);
	static ComponentMapper<OnDoor>onDoorM = ComponentMapper.getFor(OnDoor.class);
	private Family doorCollisionFamily;

	@Override
	public void addedToEngine(Engine engine) {
		Family family = Family.all( OnDoor.class).get();
		entities = engine.getEntitiesFor(family );
		Family familyDoors = Family.all(Door.class, Position.class, Body.class).get();
		doorEntities = engine.getEntitiesFor(familyDoors);
		doorCollisionFamily = Family.all( Position.class, Body.class, Player.class).get();
		buckets = engine.getSystem(BucketSystem.class);
		this.engine = (EngineNiz) engine;
	}
	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			OnDoor currentDoor = onDoorM.get(e);
			if (currentDoor.doors.size > 1) continue;
			long id = currentDoor.doors.get(0);
			Entity door = engine.getEntity(id);
			Entity player = e;
			Position pPos = player.getComponent(Position.class);
			Door doorC = door.getComponent(Door.class);
			GridPoint2 dPos = doorC.endPoint;
			Body body = player.getComponent(Body.class);
			Body dBody = door.getComponent(Body.class);
			pPos.pos.set(dPos.x + .5f, dPos.y +1);
			e.remove(OnDoor.class);

		}
		Body colBody;
		Vector2 colPos;
		Entity ce;
		long id;
		OnDoor onDoor;
		for (int i = 0; i < doorEntities.size(); i++){
			Entity e = doorEntities.get(i);
			Vector2 pos = posM.get(e).pos;
			Body body = bodyM.get(e);
			Door door = doorM.get(e);
			Array<LongArray> broad = buckets.getSmallBucketsAround(pos.x, pos.y);
			for (LongArray ar : broad){
				for (int j = 0; j < ar.size; j++){
					id = ar.get(j);
					ce = engine.getEntity(id);
					if (ce == null || !doorCollisionFamily.matches(ce)) return;
					colPos = posM.get(ce).pos;
					colBody = bodyM.get(ce);
					if (Math.abs(colPos.x - pos.x) < colBody.width //+ body.width
							&& Math.abs(colPos.y - pos.y) < colBody.height + body.height
					){
						onDoor = onDoorM.get(ce);
						if (onDoor == null){
							onDoor = engine.createComponent(OnDoor.class);
							ce.add(onDoor);							
						}
						onDoor.doors.add(e.getId());
						
						//Gdx.app.log(TAG, "ON DOOR");
					}
					
				}
			}
		}
	}
	
}
