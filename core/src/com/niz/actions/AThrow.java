package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.Data;
import com.niz.GameInstance;
import com.niz.Input;
import com.niz.action.LimbAction;
import com.niz.anim.AnimationContainer;
import com.niz.anim.AnimationLayer;
import com.niz.anim.LayerGuide;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.Inventory;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;
import com.niz.item.Item;

public abstract class AThrow extends LimbAction {
	private static final String TAG = "throw action";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	private static  ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);

	private static Vector2 tmp = new Vector2();
	int angleIndex;
	private int limb_index;
	;
	public boolean started;
	;
	private int guide_layer;
	public int item_layer_index;
	private boolean cooldown;
	//private transient Item item;
	private float throwAngle;
	private int itemKey;

	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG, "update");

		SpriteAnimation anim = animM .get(parent.e);
		Position pos = posM.get(parent.e);
		limb_index = getLimbIndex(anim);
		Control control = controlM.get(parent.e);
		tmp.set(control.rotation);
		Control cont = controlM.get(parent.e);
		if (cont.pressed[Input.SCREEN_TOUCH]){
			if (!started){
				anim.time[limb_index] = 0f;
			} 
		} else {
			started = true;
		}
		if (started){
			GameInstance.unPause = true;
			AnimationContainer layer = anim.overriddenAnimationLayers[limb_index];
			if (layer == null || layer.layers.get(0).isAnimationFinished(anim.time[limb_index])){
				if (cooldown)
					isFinished = true;
				else {
					cooldown = true;
					Item item = invM.get(parent.e).getItem(itemKey);
					if (item == null) throw new GdxRuntimeException("item no longer held");
					//item.count--;
					invM.get(parent.e).dirty = true;
					AnimationLayer itemLayer = anim.getLayer(item_layer_index);
					LayerGuide guide = anim.getGuide(guide_layer);
					Item newItem = item.separateOne();
					item.getDef().doThrow(guide, parent.e, parent.engine, throwAngle, newItem, anim.frameIndices[item_layer_index], pos.pos);
					anim.disableAnimationOverride(item_layer_index);
					int animID = Data.hash("throwcooldown"+angleIndex);
					anim.overrideAnimationForLayer(limb_index, animID).layers.get(0);
					if (guide_layer != -1){
						anim.overrideGuide(guide_layer, animID);
					}
					//anim.updateGuides(1, anim.left);
				}
				
			}
			
		} else {//!started
			updateAim(control, anim);
		}
	}

	private void updateAim(Control control, SpriteAnimation anim) {
		float angle = control.rotation.angle();
		throwAngle = angle;
		angle += 10;
		if (angle > 360) angle -= 360;
		int newIndex = (int)angle/20;

		if (newIndex != angleIndex){
			Gdx.app.log(TAG, "update aim " + newIndex);
			//Gdx.app.log(TAG, "klj  "+newIndex);
			angleIndex = newIndex;
			int animID = Data.hash("throw"+angleIndex);
			AnimationLayer layer = anim.overrideAnimationForLayer(limb_index, animID).layers.get(0);
			anim.time[item_layer_index] = 0f;
			anim.frameIndices[item_layer_index] = 0;
            anim.time[limb_index] = 0f;
            anim.frameIndices[limb_index] = 0;
			if (guide_layer != -1){
				anim.overrideGuide(guide_layer, animID);
			}
			angle -= 10;
			if (angle < 0) angle += 360;
			anim.angles[limb_index] = angle;
			//anim.angles[item_layer_index] = angle;
			if (item_layer_index != -1)anim.angles[item_layer_index] = angle;
			anim.updateGuides(1, anim.left);
		}
	}


	@Override
	public void updateRender(float dt) {
		//Gdx.app.log(TAG, "updaterender");
		/*SpriteAnimation anim = animM .get(parent.e);
		Control control = controlM.get(parent.e);
		if (!started)
			updateAim(control, anim);*/
		update(0f);
	}

	@Override
	public void onEnd() {
		SpriteAnimation anim = animM .get(parent.e);
		anim.disableAnimationOverride(limb_index);
		if (guide_layer != -1){
			anim.disableGuideOverride(guide_layer);
		}
		Race race = raceM.get(parent.e);
		race.limbTotals[limb]--;
		anim.resetItemLimbAngleFlipped(limb);
		anim.resetLayerAngleFlipped(limb_index);
		//anim.syncAllTimesWithLayer(anim.back_leg);
		//Gdx.app.log(TAG, "end "+hashCode());
		anim.time[limb_index] = anim.time[anim.back_leg];
	}

	@Override
	public void onStart() {
		cooldown = false;
		SpriteAnimation anim = animM .get(parent.e);
		limb_index = getLimbIndex(anim);
		guide_layer = getGuideLayer(anim);
		angleIndex = -1;
		isBlocking = true;
		started = false;
		item_layer_index = anim.itemLayersByLimbIndex[limb];
		//AnimationContainer layer = anim.overrideSpriteForLayer(item_layer_index, Animations.itemLayers[1]);
		anim.switchItemLimbToAngleFlipped(limb);
		anim.switchLayerToAngleFlipped(limb_index);
		itemKey = invM.get(parent.e).getActiveItem(limb).hash;
		
	}



}