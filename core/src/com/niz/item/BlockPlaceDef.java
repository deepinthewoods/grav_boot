package com.niz.item;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.action.ActionList;
import com.niz.actions.APlaceAnimation;
import com.niz.anim.Animations;
import com.niz.anim.LayerGuide;
import com.niz.anim.RotatedAnimationLayer;
import com.niz.component.Body;
import com.niz.component.PickUp;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;
import com.niz.observer.Subject.Event;

public class BlockPlaceDef extends ItemDef {

	private static final String TAG = "block place item def";
	public int random, blockID = -1;

	public BlockPlaceDef(String string) {
		super(string);
		Doing doPlaceBack = new Doing("place (back hand)", 0, Doing.TYPE_PLACE){
			
		};
		doings.add(doPlaceBack);
		isBlock = true;
	}

	@Override
	public void doPlace(LayerGuide guide, Entity parentE, EngineNiz engine,
			GridPoint2 placeTarget, int itemID, int frameIndex, Vector2 basePos) {
		

		if (blockID == -1) return;
		Entity an = engine.createEntity();

		Entity e = engine.createEntity();
		SpriteAnimation anim = engine.createComponent(SpriteAnimation.class);
		anim.alignWithBodyBottom = false;
		//anim.initFor((RotatedAnimationLayer)layer, guide.rotation);
		RotatedAnimationLayer layer = Animations.itemLayers[itemID];
		RotatedAnimationLayer layerSpin = Animations.itemSpinLayers[itemID];
		anim.initFor(layerSpin, guide.rotation);
		tmpV.set(layerSpin.offsets[frameIndex]);
		tmpV.sub(layer.offsets[frameIndex]);
		tmpV.add(guide).add(basePos);
		Position pos = engine.createComponent(Position.class);
		pos.pos.set(tmpV.x, tmpV.y);
		//Gdx.app.log(TAG, "pos "+tmpV);
		Body body = engine.createComponent(Body.class);
		body.width = .0005f;
		body.height = .0005f;
		ActionList action = engine.createComponent(ActionList.class);
		//if (e.getComponent(ActionList.class).size() > 0) throw new GdxRuntimeException("shouldb be 0"+e.getComponent(ActionList.class).size());
		APlaceAnimation projA = new APlaceAnimation();
		projA.itemID = itemID;
		projA.target.set(placeTarget);
		projA.start.set(pos.pos);
		projA.end.set(placeTarget.x+.5f, placeTarget.y+.5f);
		
		projA.val = blockID;
		projA.lerpTime = .25f;
		action.addToStart(projA);
		
		//Gdx.app.log("itemdef", "PLACE "+hashCode());

		e.add(action);
		//if (e.getComponent(ActionList.class).getAction(AItemStuckInWall.class) != null) throw new GdxRuntimeException("KJFD");
		e.add(pos);

		e.add(anim);
		
		engine.addEntity(e);
        if (e.getComponent(PickUp.class) != null) throw new GdxRuntimeException("KJFD");
		engine.getSubject("changeLargeBuckets").notify(e, Event.CHANGE_LARGE_BUCKET, null);
	
	}
	
	

}
