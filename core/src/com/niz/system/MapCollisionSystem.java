package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.niz.BlockDefinition;
import com.niz.component.Body;
import com.niz.component.CollidesWithMap;
import com.niz.component.CollisionComponent;
import com.niz.component.Map;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class MapCollisionSystem extends EntitySystem {
	private static final float SMALL_NUMBER = .002f, SMALLER_NUMBER = .001f;
	private static final String TAG = "map coll sys";;
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	ComponentMapper<Body> colM = ComponentMapper.getFor(Body.class);
	ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);

	
	private ImmutableArray<Entity> entities;
	private Subject collisionNotifier;
	private EngineNiz engine;
	private ComponentMapper<OnMap> onMapM;
	
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (EngineNiz)engine;
		entities = engine.getEntitiesFor(Family.all(Physics.class, Position.class, Body.class, CollidesWithMap.class, OnMap.class).get());
		collisionNotifier = ((EngineNiz) engine).getSubject("map collision");
		onMapM = ComponentMapper.getFor(OnMap.class);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}
	Vector2 v = new Vector2();
	@Override
	public void update(float deltaTime) {
		//dox and doy are called by physics2dstsyem
		
	}
	CollisionComponent collisionComponent = new CollisionComponent();
	public final void doX(Physics phys, Entity e) {
		OnMap onMap = onMapM.get(e);
		Map map = onMap.map;
		
		collideX(e, map, phys);
		
		
		
	}

	private final void collideX(Entity e, Map map, Physics phys) {
		Vector2 pos = posM.get(e).pos;
		Body col = colM.get(e);
		if (phys.onSlope) return;
		//if (map == null) continue;
		boolean left = phys.left, up = phys.vel.y > 0;

		if (left){
			v.set(pos.x - col.width, pos.y - col.height);
			if (v.y < 0) v.y += map.height;
			float offset = -1f;
			boolean hasFoundCollision = false;
			for (int y = (int) v.y; y <= (int)(v.y+col.height*2); y++){
				for (int x = (int) v.x; x >= (int)(v.x); x--){
					//find offset for x,y
					int b = map.get(x, y);
					int id = (b & Map.ID_MASK)>>Map.ID_BITS;
					BlockDefinition def = MapSystem.defs[id];
					if (def.isSolid)
						collisionComponent.disabled = false;
					else collisionComponent.disabled = true;
					offset = x+1+SMALL_NUMBER;
					collisionComponent.block = b;
					collisionComponent.side = CollisionComponent.LEFT;
					collisionNotifier.notify(e, Event.MAP_COLLISION, collisionComponent);
					if (!collisionComponent.disabled)
						hasFoundCollision = true;
					
						
									
				}
			}
			
			
			if (hasFoundCollision){
				pos.x += offset - v.x;
				
				//if (!phys.onGround && !phys.onWall)phys.vel.y += (phys.vel.len() - Math.abs(phys.vel.y))/1f;
				
				phys.vel.x = 0;
				//Gdx.app.log("collide", "jljklas");
			} else {
				
			}
		}
		else {
			v.set(pos.x + col.width, pos.y - col.height);
			if (v.y < 0) v.y += map.height;
			float offset = -1f;
			boolean hasFoundCollision = false;
			for (int y = (int) v.y; y <= (int)(v.y+col.height*2); y++){
				for (int x = (int) v.x; x >= (int)(v.x); x--){
				//find offset for x,y
				int b = map.get(x, y);
				int id = (b & Map.ID_MASK)>>Map.ID_BITS;
				BlockDefinition def = MapSystem.defs[id];
				if (def.isSolid)
					collisionComponent.disabled = false;
				else collisionComponent.disabled = true;
				offset = x-SMALL_NUMBER;;
				collisionComponent.block = b;
				collisionComponent.side = CollisionComponent.RIGHT;
				collisionNotifier.notify(e, Event.MAP_COLLISION, collisionComponent);
				if (!collisionComponent.disabled)
					hasFoundCollision = true;
				
				
			}
		}
			
			
			if (hasFoundCollision){
				pos.x += offset - v.x;
				//if (!phys.onGround && !phys.onWall)phys.vel.y += (phys.vel.len() - Math.abs(phys.vel.y))/1f;
				phys.vel.x = 0;
				//Gdx.app.log("collide", "jljklas");
				
			} else {
				//Gdx.app.log("collide", "jljklas");
				
			}
		}
		
	}

	public final void doY(Physics phys, Entity e) {
		
		OnMap onMap = onMapM.get(e);
		Map map = onMap.map;
		for (int m = 0; m < onMap.maps.size; m++){
			
		}
		collideY(e, map, false, phys, false);
		collideY(e, map, false, phys, true);
		//collideY(e, map);
			
			
			
		
		
	}

	private final void collideY(Entity e, Map map, boolean wasOnGround, Physics phys, boolean forceDown) {
		Vector2 pos = posM.get(e).pos;
		Body col = colM.get(e);
		//if (map == null) continue;
		boolean left = phys.left, up = phys.vel.y > 0.0001f;
		//Gdx.app.log("collide", "jljklas "+e.getId() + up);
		if (!forceDown){
			
			phys.onSlope = false;
			collisionComponent.onSlope = false;
		}
		if (up && !forceDown){
			v.set(pos.x - col.width, pos.y + col.height);
			if (v.y < 0) v.y += map.height;
			int y = (int) v.y;
			float offset = 0;// = -SMALL_NUMBER;
			boolean hasFoundCollision = false;
			for (int x = (int) v.x; x <= (int)(v.x+col.width*2); x++){
				//find offset for x,y
				int b = map.get(x, y);
				int id = (b & Map.ID_MASK)>>Map.ID_BITS;
				BlockDefinition def = MapSystem.defs[id];
				if (def.isSolid)
					collisionComponent.disabled = false;
				else collisionComponent.disabled = true;
				offset = y-SMALL_NUMBER;
				collisionComponent.block = b;
				collisionComponent.side = CollisionComponent.BOTTOM;
				collisionNotifier.notify(e, Event.MAP_COLLISION, collisionComponent);
				if (!collisionComponent.disabled)
					hasFoundCollision = true;
				
				
			}
			
			if (hasFoundCollision){
				pos.y += offset - v.y;
				//Gdx.app.log("collide", "jljklas"+e.getId());
			}//else phys.onGround = false;
		} else {
			v.set(pos.x - col.width, pos.y - col.height);
			if (v.y < 0) v.y += map.height;
			float vx2 = pos.x + col.width;
			int y = (int) v.y;
			float offset = 0;// = -1f-SMALL_NUMBER;
			boolean hasFoundCollision = false;
			for (int x = (int) v.x; x <= (int)(vx2); x++){
				//find offset for x,y
				int b = map.get(x, y);
				int id = (b & Map.ID_MASK)>>Map.ID_BITS;
				BlockDefinition def = MapSystem.defs[id];
				
				float x0 = Math.max(Math.min(1, v.x - x), 0);
				float x1 = Math.max(Math.min(1, vx2 - x), 0);
				float xc =  pos.x - x;

				collisionComponent.block = b;
				collisionComponent.side = CollisionComponent.TOP;
				float dy = def.getYOffsetForMapCollision(xc, x0, x1, collisionComponent, v.y - y, b, left, phys.wasOnGround, phys.onGround);
				//Gdx.app.log(TAG, "id "+x0 + "  " + x1 + "diff"+dy);
				
				float off = y + dy;
//				if (map.isSolid(b))
//					collisionComponent.disabled = false;
//				else collisionComponent.disabled = true;
//				offset = y+1;
				
				collisionNotifier.notify(e, Event.MAP_COLLISION, collisionComponent);
				if (!collisionComponent.disabled){
					hasFoundCollision = true;
					//if (dy < .5f) 
						//Gdx.app.log(TAG, "id "+x0 + "  " + x1 + "diff"+dy);
					if (off > offset) offset = off;
					
				}
			}
			
			if (hasFoundCollision){
				pos.y += offset - v.y;

				//Gdx.app.log("collide", "jljklas"+e.getId());
				phys.onGround = true;
				phys.onGroundTime = engine.tick;
				MovementData move = moveM.get(e);
				if (move != null){
					
					move.resetDoubleJumps();
				}
				if (collisionComponent.onSlope) phys.onSlope = true;
				else phys.vel.y = -.01f;
				collideYSlopes(e, map);
			} else if (!forceDown){
				phys.onGround = false;
			}
		}
		//if (true) return;
		
		
		
	}

	private void collideYSlopes(Entity e, Map map) {
		Vector2 pos = posM.get(e).pos;
		Body col = colM.get(e);
		Physics phys = physM.get(e);
		//if (map == null) continue;
		collisionComponent.onSlope = false;
		boolean left = phys.left, up = phys.vel.y > 0;
		
		
		if (up){
			v.set(pos.x - col.width, pos.y + col.height);
			if (v.y < 0) v.y += map.height;
			int y = (int) v.y;
			float offset = 0;// = -SMALL_NUMBER;
			boolean hasFoundCollision = false;
			for (int x = (int) v.x; x <= (int)(v.x+col.width*2); x++){
				//find offset for x,y
				int b = map.get(x, y);
				int id = (b & Map.ID_MASK)>>Map.ID_BITS;
				BlockDefinition def = MapSystem.defs[id];
				if (def.isSolid)
					collisionComponent.disabled = false;
				else collisionComponent.disabled = true;
				offset = y-SMALL_NUMBER;
				collisionComponent.block = b;
				collisionComponent.side = CollisionComponent.BOTTOM;
				collisionNotifier.notify(e, Event.MAP_COLLISION, collisionComponent);
				if (!collisionComponent.disabled)
					hasFoundCollision = true;
				
				
			}
			
			if (hasFoundCollision){
				pos.y += offset - v.y;
				//Gdx.app.log("collide", "jljklas");
			}//else phys.onGround = false;
		} else {
			v.set(pos.x - col.width, pos.y - col.height);
			if (v.y < 0) v.y += map.height;
			float vx2 = pos.x + col.width;
			int y = (int) v.y;
			float offset = 0;// = -1f-SMALL_NUMBER;
			boolean hasFoundCollision = false;
			for (int x = (int) v.x; x <= (int)(vx2); x++){
				//find offset for x,y
				int b = map.get(x, y);
				int id = (b & Map.ID_MASK)>>Map.ID_BITS;
				BlockDefinition def = MapSystem.defs[id];
				if (def.isSolid) return;
				float x0 = Math.max(Math.min(1, v.x - x), 0);
				float x1 = Math.max(Math.min(1, vx2 - x), 0);
				float xc =  pos.x - x;
				
				collisionComponent.block = b;
				collisionComponent.side = CollisionComponent.TOP;
				float dy = def.getYOffsetForMapCollision(xc, x0, x1, collisionComponent, v.y - y, b, left, phys.wasOnGround, phys.onGround);
				//Gdx.app.log(TAG, "id "+x0 + "  " + x1 + "diff"+dy);
				
				float off = y + dy;
//				if (map.isSolid(b))
//					collisionComponent.disabled = false;
//				else collisionComponent.disabled = true;
//				offset = y+1;
				
				collisionNotifier.notify(e, Event.MAP_COLLISION, collisionComponent);
				//if ((collisionComponent.onSlope)){// && (phys.wasOnGround2 || phys.wasOnGround2 || true))){
				//	Gdx.app.log(TAG, "SLOPEGR");
				//}
				if (!collisionComponent.disabled ){
					hasFoundCollision = true;
					//if (dy < .5f) 
						//Gdx.app.log(TAG, "id "+x0 + "  " + x1 + "diff"+dy);
					if (off > offset) offset = off;
					
				}
			}
			
			if (hasFoundCollision){
				pos.y += offset - v.y;
				//phys.vel.y = 0f;
				//Gdx.app.log("collide", "jljklas");
				phys.onGround = true;
				phys.onGroundTime = engine.tick;
				MovementData move = moveM.get(e);
				if (move != null){
					
					move.resetDoubleJumps();
				}
				if (collisionComponent.onSlope) phys.onSlope = true;
			} else {
				//phys.onGround = wasOnGround;
			}
		}
		//if (true) return;
		
		
		
	}

	public void doYBottom(Physics phys, Entity e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
