package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.niz.component.Body;
import com.niz.component.CollidesWithMap;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;

public class Physics2dSystem extends EntitySystem implements EntityListener{
	private static final String TAG = "physics2d system";
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	
	Vector2 tmpV = new Vector2(), tmpW = new Vector2();
	//Vector2[] p = new Vector2[10000];
	//Bits bits = new Bits(10000/4), tmpBits = new Bits(10000/4);;
	//int li, progress;
	//Vector2 gravity = new Vector2(0,-30);
	private Family family;
	private ImmutableArray<Entity> entities;
	private MapCollisionSystem collisions;
	private ImmutableArray<Entity> entitiesOnMap;
	private ImmutableArray<Entity> entitiesNotOnMap;
	private ImmutableArray<Entity> entitiesNotOnMapNoBody;
	private ImmutableArray<Entity> entitiesOnMapNoBody;
	
	public void stepX(float timestep, Physics phys, Vector2 pos){
		//tmpBits.clear();
		//tmpBits.or(bits);
		//progress = 0;
		//do {

			//i = tmpBits.nextSetBit(progress);
			//progress = i+1;
			//if (i == -1) break;
			//i *= 4;
			//enforceSpeedLimit(phys.vel, p[i+3]);
			
			tmpV.x =phys.vel.x*timestep;
			tmpW.x = (phys.acc.x+phys.gravity.x)*timestep*timestep*.5f;
			tmpV.x += tmpW.x;
			pos.x += tmpV.x;
			
			tmpV.x = phys.acc.x + phys.gravity.x;
			tmpV.x *= timestep;
			phys.vel.x += tmpV.x;
			
			
		//} while (i != -1);
		
		
	}
	
	public void stepY(float timestep , Physics phys, Vector2 pos){
			
			tmpV.y =phys.vel.y*timestep;
			tmpW.y = (phys.acc.y+phys.gravity.y)*timestep*timestep*.5f;
			tmpV.y += tmpW.y;
			pos.y += tmpV.y;
			
			tmpV.y = phys.acc.y + phys.gravity.y;
			tmpV.y *= timestep;
			phys.vel.y += tmpV.y;
			phys.acc.set(0,0);
			//enforceSpeedLimit(phys.vel, p[i+3]);

	}

	
	public static final void enforceSpeedLimit(Vector2 v, Vector3 lim) {
		if (Math.abs(v.x) > lim.x){
			
				//limit x
				if (v.x<0)
					v.x = -lim.x;
				else
					v.x = lim.x;
				//Gdx.app.log(TAG,  "speed before x"+v.x);
				//Gdx.app.log(TAG,  "speed limit x"+lim.x);
		} 
		if (v.y > 0){
			if (v.y > lim.y){
				
					v.y = lim.y;
			}
		} else {
			if (v.y < -lim.z){
				
					v.y = -lim.z;
				
			}
		}
		
		
	}

	public Physics2dSystem(){
		
	}
	
	
	@Override
	public void addedToEngine(Engine engine) {
		family = Family.all(Physics.class, Position.class).get();
		entitiesOnMap = engine.getEntitiesFor(Family.all(Physics.class, Position.class, Body.class, CollidesWithMap.class, OnMap.class).get());
		entitiesOnMapNoBody = engine.getEntitiesFor(Family.all(Physics.class, Position.class, CollidesWithMap.class, OnMap.class).exclude(Body.class).get());
		entitiesNotOnMap = engine.getEntitiesFor(Family.all(Physics.class, Position.class, Body.class).exclude(OnMap.class, CollidesWithMap.class).get());
		entitiesNotOnMapNoBody = engine.getEntitiesFor(Family.all(Physics.class, Position.class).exclude(OnMap.class, CollidesWithMap.class, Body.class).get());
		
		//family = Family.one(Physics.class).get();
		entities = engine.getEntitiesFor(family);
		collisions = engine.getSystem(MapCollisionSystem.class);
		engine.addEntityListener(family, this);
		
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}
	

	@Override
	public void update(float deltaTime) {
		//Gdx.app.log(TAG,  "update");
		//Gdx.app.log(TAG,  "pb "+p[0] + "  1 "+p[1] + "  2 "+p[2]);
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			Physics phys = physM.get(e);
			if (Math.abs(phys.vel.x) > SpriteAnimationUpdateSystem.VELOCITY_DIRECTION_THRESHOLD){
				phys.left = phys.vel.x < 0;
				//Gdx.app.log(TAG, "left"+spr.left);
			}
		}
		for (int i = 0; i < entitiesOnMap.size(); i++){
			Entity e = entitiesOnMap.get(i);
			Physics phys = physM.get(e);
			phys.wasOnGround2 = phys.wasOnGround;
			phys.wasOnGround = phys.onGround;	
			Position pos = posM.get(e);
			stepX(deltaTime, phys, pos.pos);
			collisions.doX(phys, e);
			stepY(deltaTime, phys, pos.pos);
			collisions.doY(phys, e);;
			
		}
		
		for (int i = 0; i < entitiesOnMapNoBody.size(); i++){
			Entity e = entitiesOnMapNoBody.get(i);
			Physics phys = physM.get(e);
			phys.wasOnGround2 = phys.wasOnGround;
			phys.wasOnGround = phys.onGround;	
			Position pos = posM.get(e);
			stepX(deltaTime, phys, pos.pos);
			//collisions.doX(phys, e);
			stepY(deltaTime, phys, pos.pos);
			//collisions.doY(phys, e);;
		}
		
		for (int i = 0; i < entitiesNotOnMap.size(); i++){
			Entity e = entitiesNotOnMap.get(i);
			Physics phys = physM.get(e);
			//phys.wasOnGround2 = phys.wasOnGround;
			//phys.wasOnGround = phys.onGround;	
			Position pos = posM.get(e);
			stepX(deltaTime, phys, pos.pos);
			//collisions.doX(phys, e);
			stepY(deltaTime, phys, pos.pos);
			//collisions.doY(phys, e);;
		}
		
		for (int i = 0; i < entitiesNotOnMapNoBody.size(); i++){
			Entity e = entitiesNotOnMapNoBody.get(i);
			Physics phys = physM.get(e);
			//phys.wasOnGround2 = phys.wasOnGround;
			//phys.wasOnGround = phys.onGround;	
			Position pos = posM.get(e);
			stepX(deltaTime, phys, pos.pos);
			//collisions.doX(phys, e);
			stepY(deltaTime, phys, pos.pos);
			//collisions.doY(phys, e);;
		}
		
		//Gdx.app.log(TAG,  "pafyer "+p[0]  + "  1 "+p[1] + "  2 "+p[2]);
		//
		//collisions.doY();;
		
	}


	@Override
	public void entityAdded(Entity entity) {
		
		Physics phys = physM.get(entity);

		//Gdx.app.log(TAG, "entity added"+pos.pos);
		
		
	}



	@Override
	public void entityRemoved(Entity entity) {
		
		
	}
	/*public void step(float timestep){
		tmpBits.clear();
		tmpBits.or(bits);
		progress = 0;
		do {
			i = tmpBits.nextSetBit(progress);
			progress = i+1;
			if (i == -1) break;
			i *= 4;
			enforceSpeedLimit(phys.vel, p[i+3]);
			
			tmpV.set(phys.vel).scl(timestep);
			tmpW.set(phys.acc).add(gravity).scl(timestep*timestep*.5f);
			tmpV.add(tmpW);
			pos.add(tmpV);
			
			tmpV.set(phys.acc).add(gravity);
			tmpV.scl(timestep);
			phys.vel.add(tmpV);
			
			phys.acc.set(0,0);
			
			
		} while (i != -1);
		
		
	}*/

	
}
