package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.Inventory;
import com.niz.component.LineBody;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.PickUp;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;
import com.niz.item.Item;
import com.niz.system.BucketSystem;

public class AItemMaybeOnGround extends Action {
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);
	private static ComponentMapper<LineBody> lineM = ComponentMapper.getFor(LineBody.class);
	private static ComponentMapper<PickUp> pickupM = ComponentMapper.getFor(PickUp.class);
	private static Family itemFamBody = Family.all(Position.class, PickUp.class, Body.class).exclude(Physics.class).get();
	private static Family itemFamLiney = Family.all(Position.class, PickUp.class, LineBody.class).exclude(Physics.class).get();
	private static ComponentMapper<Item> itemM = ComponentMapper.getFor(Item.class);

	private static final String TAG = "stuck in wall action ";
	public GridPoint2 block = new GridPoint2();
	boolean first;
	private BucketSystem bucket;
	@Override
	public void update(float dt) {
		//TODO check for coming unstuck
		//Gdx.app.log(TAG, "maybe stuck on ground");
		Physics phys = physM.get(parent.e);
		Vector2 pos = posM.get(parent.e).pos;
		if (phys.onGround){
			
			AItemOnGround act = Pools.obtain(AItemOnGround.class);
			addBeforeMe(act);
			
			parent.e.remove(Physics.class);
			isFinished = true;
			if (itemFamBody.matches(parent.e)){
				//stack
				Array<LongArray> ret = bucket.getSmallBucketsAround(pos.x, pos.y);
				long selfID = parent.e.getId();
				for (int i = 0; i < ret.size; i++){
					LongArray r = ret.get(i);
					for (int j = 0; j < r.size; j++){
						long id = r.get(j);
						if (id == selfID) continue;
						Entity e = parent.engine.getEntity(id);
						
						if (itemFamBody.matches(e)){
							Body bodyA = bodyM.get(parent.e);
							Body bodyB = bodyM.get(e);
							Vector2 posB = posM.get(e).pos;
							//Gdx.app.log(TAG, "try stack" +posB);
							if (bodyA == null) throw new GdxRuntimeException("body a");
							if (bodyB == null) throw new GdxRuntimeException("body a");
							if (Math.abs(pos.x - posB.x) < (bodyA.width + bodyB.width)* 1.5f && Math.abs(pos.y - posB.y) < (bodyA.height + bodyB.height) * 1.5f){
								Item it = itemM.get(parent.e);
								Item otherIt = itemM.get(e);
								if (otherIt.count != 0 && it.canStack(otherIt)){
									if (bodyA.width * bodyA.height < bodyB.width * bodyB.height){
										
										otherIt.stack(it);
										parent.engine.removeEntity(parent.e);
										
										
										//return;
										//Gdx.app.log(TAG, "stackA "+pos+posB);
									} else {
										it.stack(otherIt);
										parent.engine.removeEntity(e);
										//Gdx.app.log(TAG, "stackB "+pos+posB);
										//return;
										
									}
									return;
								}
							}
							
						}
					}
				}
			}
			
			
		} else {
			//parent.e.remove(Physics.class);
			AItemFall act = Pools.obtain(AItemFall.class);
			addBeforeMe(act);
			
			parent.e.remove(PickUp.class);
		}
		isFinished = true;
		first = false;
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		first = true;
		bucket = parent.engine.getSystem(BucketSystem.class);
		//Gdx.app.log(TAG+itemID, "Stuck");
		
		/*PickUp pick = pickupM.get(parent.e);
		if (pick == null){
			pick = parent.engine.createComponent(PickUp.class);
			
			parent.e.add(pick);
		}*/
	}

}
