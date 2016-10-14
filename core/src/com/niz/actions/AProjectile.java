package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.anim.Animations;
import com.niz.anim.LayerGuide;
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
import com.niz.system.Physics2dSystem;

public class AProjectile extends Action {
	private static final String TAG = "projectile action";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	private static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);
	private static ComponentMapper<LineBody> lineM = ComponentMapper.getFor(LineBody.class);
	private static ComponentMapper<Item> itemM = ComponentMapper.getFor(Item.class);

	public int rotationSpeed;
	
	private static Vector2 tmpV = new Vector2();
	private static Physics2dSystem physSys;
	//public Item item;
	@Override
	public void update(float dt) {
		Item item = itemM.get(parent.e);
		SpriteAnimation anim = animM.get(parent.e);
		int frame = anim.frameIndices[0];
		LayerGuide guide = anim.getGuide(0);
		Physics phys = physM.get(parent.e);
		//Gdx.app.log("a projectile", "pos"+parent.e.getComponent(Position.class).pos);
		{
			
			guide.rotation = (guide.rotation+dt*rotationSpeed)%360;
//		Gdx.app.log("a projectile", "pos"+parent.e.getComponent(Position.class).pos);
			//Gdx.app.log("a projectile", "pos"+parent.e.getComponent(Position.class).pos + " rot "+guide.rotation);
			LineBody line = lineM.get(parent.e);
			
			tmpV.set(Animations.itemLayers[item.id].offsets[frame]);
			tmpV.sub(Animations.itemSpinLayers[item.id].offsets[frame]);
			line.offsetA.set(tmpV);
			
			tmpV.set(Animations.itemTipLayers[item.id].offsets[frame]);
			tmpV.sub(Animations.itemSpinLayers[item.id].offsets[frame]);
			line.offsetB.set(tmpV);
			
			if (anim.adjustedLeft[0]){
				line.offsetA.x *= -1;
				line.offsetB.x *= -1;
			}
			Body body = bodyM.get(parent.e);
			if (phys.onGround){
				phys.setLinearVelocity(0, 0);		
				
				parent.e.remove(Physics.class);
				isFinished = true;
				AItemOnGround act = Pools.obtain(AItemOnGround.class);
				
				addBeforeMe(act);
				Gdx.app.log(TAG, "stuck in wall "+parent.e.getId());
			}
		}
		
		
		
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		Physics phys = physM.get(parent.e);
		physSys = parent.engine.getSystem(Physics2dSystem.class);;
		if (parent.e.getComponent(PickUp.class) != null) throw new GdxRuntimeException("KJFD");

	}

}
