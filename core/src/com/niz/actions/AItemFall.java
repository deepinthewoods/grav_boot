package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
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

public class AItemFall extends Action {
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

	private static final String TAG = "stuck in wall action ";
	public GridPoint2 block = new GridPoint2();
	boolean first;
	@Override
	public void update(float dt) {
		//TODO check for coming unstuck
		//Gdx.app.log(TAG, "item fall");
		Physics phys = physM.get(parent.e);
		if (phys.onGround){
			AItemOnGround act = Pools.obtain(AItemOnGround.class);
			addBeforeMe(act);
			isFinished = true;
			parent.e.remove(Physics.class);
		} 
			
		
		first = false;
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		first = true;
		//Gdx.app.log(TAG+itemID, "Stuck");
		/*Body body = bodyM.get(parent.e);
		if (body == null){
			body = parent.engine.createComponent(Body.class);
			body.width = .5f;
			body.height = .5f;
			parent.e.add(body);
		}
		PickUp pick = pickupM.get(parent.e);
		if (pick == null){
			pick = parent.engine.createComponent(PickUp.class);
			
			parent.e.add(pick);
		}*/
	}

}
