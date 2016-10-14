package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongArray;
import com.niz.component.Body;
import com.niz.component.Inventory;
import com.niz.component.PickUp;
import com.niz.component.Position;
import com.niz.item.Item;

public class PickUpCollisionsSystem extends IteratingSystem {
	
	private static final String TAG = "pick up coll sys";
	private static final float OVER_COLLIDE_AMOUNT = .72f;
	private ComponentMapper<Position> posM;
	private ComponentMapper<PickUp> pickM;
	private ComponentMapper<Body> bodyM;
	private BucketSystem buckets;
	private EngineNiz engine;
	private Family collideFamily;
	private ComponentMapper<Inventory> invM;
	private static ComponentMapper<Item> itemM = ComponentMapper.getFor(Item.class);

	public PickUpCollisionsSystem() {
		super(Family.all(Position.class, Body.class, PickUp.class).get());
		collideFamily = Family.all(Position.class, Body.class, Inventory.class).exclude(PickUp.class).get();
		posM = ComponentMapper.getFor(Position.class);
		pickM = ComponentMapper.getFor(PickUp.class);
		bodyM = ComponentMapper.getFor(Body.class);
		invM = ComponentMapper.getFor(Inventory.class);
	}
	
	

	@Override
	public void addedToEngine(Engine engine) {
		
		super.addedToEngine(engine);
		this.engine = (EngineNiz) engine;
		buckets = engine.getSystem(BucketSystem.class);
	}



	@Override
	protected void processEntity(Entity e, float deltaTime) {
		Vector2 pos = posM.get(e).pos;
		PickUp pick = pickM.get(e);
		Body body = bodyM.get(e);
		Array<LongArray> ent = buckets.getSmallBucketsAround(pos.x, pos.y);
		for (int i = 0; i < ent.size; i++){
			LongArray a = ent.get(i);
			for (int x = 0; x < a.size; x++){
				long id = a.get(x);
				Entity colle = engine.getEntity(id);
				if (colle == null) continue;
				if (!collideFamily.matches(colle)) continue;
				Vector2 collPos = posM.get(colle).pos;
				Body collBody = bodyM.get(colle);
				float w = collBody.width + body.width, h = collBody.height + body.height;;
				if (Math.abs(pos.x - collPos.x) < w + OVER_COLLIDE_AMOUNT && Math.abs(pos.y - collPos.y) < h + OVER_COLLIDE_AMOUNT){
					//Gdx.app.log(TAG, "PICKUPKPCKDSPKcsadOU");
					Inventory inv = invM.get(colle);
					//inv.addItem(pick.itemID, 1);
					
					//e.removeAll();
					//engine.removeEntity(e);
					engine.removeEntityNoPool((PooledEntity) e);
					Item item = 
							(Item) e.remove(Item.class);
					inv.addItem(item);
					engine.freeEntity((PooledEntity) e);
					
					return;
				}
			}
		}
		
		
	}

}
