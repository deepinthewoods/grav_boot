package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Pools;
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
	public GridPoint2 block = new GridPoint2();
	boolean first;
	int count;
	private int limit;
	@Override
	public void update(float dt) {
		//TODO check for coming unstuck
		//Gdx.app.log(TAG, "Stuck on ground"+first);
		//delay(.2f);
		if (count++ > limit){
			
			isFinished = true;
			AItemMaybeOnGround act = Pools.obtain(AItemMaybeOnGround.class);
			Physics phys = parent.engine.createComponent(Physics.class);
			
			parent.e.add(phys);
			addBeforeMe(act);
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
