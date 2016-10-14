package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.niz.component.CollidesWithMap;
import com.niz.component.CollisionComponent;
import com.niz.component.LineBody;
import com.niz.component.Map;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.observer.Subject;

public class LineMapCollisionSystem extends EntitySystem {
	private static final float SMALL_NUMBER = .002f, SMALLER_NUMBER = .001f;;
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	ComponentMapper<LineBody> colM = ComponentMapper.getFor(LineBody.class);
	ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	
	
	private ImmutableArray<Entity> entities;
	private Subject collisionNotifier;
	private EngineNiz engine;
	private ComponentMapper<OnMap> onMapM;
	
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (EngineNiz)engine;
		entities = engine.getEntitiesFor(Family.all(Physics.class, Position.class, LineBody.class, CollidesWithMap.class, OnMap.class).get());
		collisionNotifier = ((EngineNiz) engine).getSubject("map collision");
		onMapM = ComponentMapper.getFor(OnMap.class);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}
	@Override
	public void update(float deltaTime) {
		//dox and doy are called by physics2dstsyem
		doY();
	}
	CollisionComponent collisionComponent = new CollisionComponent();
	

	

	public void doY() {
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			OnMap onMap = onMapM.get(e);
			Map map = onMap.map;
			for (int m = 0; m < onMap.maps.size; m++){
				
			}
			collideY(e, map);
			
			
			
		}
		
	}
	Bresenham2 ray = new Bresenham2();
	Vector2 v = new Vector2(), v2 = new Vector2();
	
	private void collideY(Entity e, Map map) {
		Vector2 pos = posM.get(e).pos;
		LineBody col = colM.get(e);
		Physics phys = physM.get(e);
		//if (map == null) continue;
		boolean left = phys.vel.x < 0, up = phys.vel.y > 0;
		
		
		v.set(pos).add(col.offsetA);
		v2.set(pos).add(col.offsetB);
		Array<GridPoint2> returnArray = ray.line((int)v.x,  (int)v.y, (int)v2.x, (int)v2.y);
		
		
		
		
		//if (v.y < 0) v.y += map.height;
		///int y = (int) v.y;
		//float offset = 0;// = -SMALL_NUMBER;
		boolean hasFoundCollision = false;
		//Gdx.app.log("collide", ": "+e.getId() + " size "+returnArray.size);
		for (int i = 0; i < returnArray.size; i++){
			GridPoint2 pt = returnArray.get(i);
			//Gdx.app.log("collide", ": "+e.getId() + "   " + pt.x + " , " + pt.y);
			//find offset for x,y
			int b = map.get(pt.x, pt.y);
			if (map.isSolid(b))
				collisionComponent.disabled = false;
			else collisionComponent.disabled = true;
			//offset = pt.y-SMALL_NUMBER;
			collisionComponent.block = b;
			collisionComponent.side = CollisionComponent.BOTTOM;
			//collisionNotifier.notify(e, Event.MAP_COLLISION, collisionComponent);
			if (!collisionComponent.disabled){
				hasFoundCollision = true;
				//Gdx.app.log("collide", ": "+e.getId() + "  @ "+ pt.x+","+pt.y);

			}
			
			
		}
		
		if (hasFoundCollision){
			phys.onGround = true;
			//pos.y += offset - v.y;
			//Gdx.app.log("collide", ": "+e.getId());
			
		} else phys.onGround = false;
		
		//if (true) return;
		
		
		
	}
	
	

}
