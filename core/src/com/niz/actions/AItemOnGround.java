package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.BlockDefinition;
import com.niz.RayCaster;
import com.niz.action.Action;
import com.niz.component.Body;
import com.niz.component.Buckets;
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
import com.niz.system.MapSystem;
import com.niz.system.OverworldSystem;

public class AItemOnGround extends Action {
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
	private static ComponentMapper<Buckets> bucketM = ComponentMapper.getFor(Buckets.class);

	private static final String TAG = "item on ground ation";
	
	private static final int SCAN_R = 4;
	private static final int[] SCAN_RADIUS_DX = {0, SCAN_R, SCAN_R, SCAN_R, 0, -SCAN_R, -SCAN_R, -SCAN_R};
	private static final int[] SCAN_RADIUS_DY = {SCAN_R, SCAN_R, 0, -SCAN_R, -SCAN_R, -SCAN_R, 0, SCAN_R};
	public GridPoint2 block = new GridPoint2();
	boolean first;
	int count;
	private int limit;
	private OverworldSystem map;
	@Override
	public void update(float dt) {
		
		//Gdx.app.log(TAG, "Stuck on ground"+first);
		//delay(.2f);
		if (count++ > limit){
			Vector2 pos = posM.get(parent.e).pos;
			int x = MathUtils.round( pos.x);
			int y = MathUtils.round( pos.y);
			int dx = pos.x - x < 0?-1:1;
			int dy = pos.y - y < 0?-1:1;
			BlockDefinition c = MapSystem.getDef(map.getMapFor(x, y).get(x, y));
			BlockDefinition cx = MapSystem.getDef(map.getMapFor(x+dx, y).get(x+dx, y));
			BlockDefinition cy = MapSystem.getDef(map.getMapFor(x, y+dy).get(x, y+dy));
			BlockDefinition cxy = MapSystem.getDef(map.getMapFor(x+dx, y+dy).get(x+dx, y+dy));
			if (c.isSolid && cx.isSolid && cy.isSolid && cxy.isSolid){
				
				//stuck
				//cast rays in 8 directions for a few blocks
				RayCaster ray = Pools.obtain(RayCaster.class);
				float shortest = 1000000000;
				int shortestX = 0, shortestY = 0;
				boolean found = false;
				for (int i = 0; i < 8; i++){
					
					ray.trace(pos.x, pos.y, pos.x + SCAN_RADIUS_DX[i], pos.y + SCAN_RADIUS_DY[i]);
					while (ray.hasNext){
						ray.next();
						BlockDefinition b = MapSystem.getDef(map.getMapFor(ray.x, ray.y).get(ray.x, ray.y));
						if (!b.isSolid){
							float dist = pos.dst2(ray.x + .5f, ray.y + .5f);
							if (dist < shortest){
								shortest = dist;
								shortestX = ray.x;
								shortestY = ray.y;
								found = true;
							}
							break;
						}
					}
				}//8dir
				if (found){
					Gdx.app.log(TAG, "FOUND PLACE");
					pos.set(shortestX + .5f, shortestY + .5f);
				} else {
					count = 0;
				}
				isFinished = true;
				AItemMaybeOnGround act = Pools.obtain(AItemMaybeOnGround.class);
				Physics phys = parent.engine.createComponent(Physics.class);			
				parent.e.add(phys);
				addBeforeMe(act);
			}
			else{
				isFinished = true;
				AItemMaybeOnGround act = Pools.obtain(AItemMaybeOnGround.class);
				Physics phys = parent.engine.createComponent(Physics.class);			
				parent.e.add(phys);
				addBeforeMe(act);
			}
			//parent.engine.removeEntity(parent.e);
		}
		first = false;
	}

	@Override
	public void onEnd() {
		Body body = bodyM.get(parent.e);
		LineBody line = lineM.get(parent.e);
		if (line != null){
			parent.e.remove(Body.class);
		}
	}

	@Override
	public void onStart() {
		count = 0;
		first = true;
		limit = (int) (parent.e.getId() + parent.engine.tick);
		limit &= 255;
		map = parent.engine.getSystem(OverworldSystem.class);
		
		//Gdx.app.log(TAG+itemID, "Stuck");
		/*Body body = bodyM.get(parent.e);
		if (body == null){
			body = parent.engine.createComponent(Body.class);
			body.width = .5f;
			body.height = .5f;
			parent.e.add(body);
		}*/
		Body body = bodyM.get(parent.e);
		if (body == null ){
			body = parent.engine.createComponent(Body.class);
			body.width = .5f;
			body.height = .5f;
			parent.e.add(body);
		}
		
		PickUp pick = pickupM.get(parent.e);
		if (pick == null){
			pick = parent.engine.createComponent(PickUp.class);
			
			parent.e.add(pick);
		}
		
		Buckets buck = bucketM.get(parent.e);
		if (buck == null){
			buck = parent.engine.createComponent(Buckets.class);
			parent.e.add(buck);
		}
		
		
	}

}
