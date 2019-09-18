package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.niz.Data;
import com.niz.Input;
import com.niz.Main;
import com.niz.action.LimbAction;
import com.niz.anim.AnimationContainer;
import com.niz.anim.AnimationLayer;
import com.niz.anim.Animations;
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

public class AThrowTail extends LimbAction {
	private static final String TAG = "tiaal contorl action";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	private static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);

	float angle;
	//private IntArray dir;
	private float targetDelta;
	private int limb_index;
	private float startDelta;
	private float interval;
	private boolean on;
	private int moveProgress;
	private int moveTotal;
	private int moveCoarse;
	private boolean reverse;
	private boolean under;
	private int guide_layer;
	private int item_layer_index;
	private float throwAngle;
	private int itemKey;
	//private Item item;
	static Vector2 tmpV = new Vector2();
	@Override
	public void update(float dt) {
		SpriteAnimation anim = animM.get(parent.e);
		
		
		AnimationLayer layer = anim.getLayer(anim.tail);
		int totalFrames = (int) layer.getNumberOfFrames();
		//Gdx.app.log(TAG, "got dirs"+Data.hash("dragontailguidetip"));
		Control con = controlM.get(parent.e);
		if (con.pressed[Input.SCREEN_TOUCH] ){
			if (!on) {
				IntArray dir = Animations.directions.get(anim.guideIDs.get(anim.tail_tip_g));
				tmpV.set(con.rotation);
				//tmpV.x *= -1;
				throwAngle = ( tmpV.angle() + 360 ) % 360;
				//tmpV.x *= -1;
				if (!anim.left)tmpV.x *= -1;
				angle = tmpV.angle();
				int iangle = (int)angle;
				iangle = (iangle ) % 360;
				
				//if (iangle == 360) iangle = 0;
				int index = dir.get(iangle);
				int altIndex = dir.get((iangle+180)%360);
				if (index > totalFrames/2){
					index = altIndex;
					reverse = true;
				} else reverse = false;
				
				targetDelta = (float)index / (float)(totalFrames);
				startDelta = targetDelta -.3f * (reverse?-1:1);
				//startDelta = Math.max(startDelta,  0);
				moveCoarse = 0;
				moveProgress = 0;
				moveTotal = (int) (.25f / Main.timeStep);

				//Gdx.app.log(TAG, "aim "+index + "  @ "+targetDelta);
				anim.time[anim.tail] = targetDelta;
			};
		} else {//not touched
			if (!on){
				on = true;
				
			}
			//
		}
		
		if (on){
			
			
			Position pos = posM.get(parent.e);
			if ((moveProgress >= moveTotal) ^ under){
				if (moveCoarse == 0){
					Item item = invM.get(parent.e).getItem(itemKey);
					moveCoarse = 1;
					moveProgress = 0;
					moveTotal = (int) (.15f / Main.timeStep);
					startDelta = targetDelta;
					targetDelta = startDelta + .26f * (reverse?-1:1);
					//Gdx.app.log(TAG, "SHOOOOOT "+delta);
					LayerGuide guide = anim.getGuide(guide_layer);
					Item newItem = item.separateOne();
					item.getDef().doThrow(guide, parent.e, parent.engine, throwAngle, newItem, anim.frameIndices[item_layer_index], pos.pos);
					anim.disableAnimationOverride(item_layer_index);
					
				} else if (moveCoarse == 1){
					moveCoarse = 2;
					moveProgress = 0;
					moveTotal = (int) (.15f / Main.timeStep);
					startDelta = targetDelta;
					targetDelta = .5f;
				} else if (moveCoarse == 2){
					
					//Gdx.app.log(TAG, "fin"+startDelta);
					isFinished = true;
				}
				//isFinished = true;
				//SHOOT
			}
			float delta = MathUtils.lerp(startDelta, targetDelta, moveProgress++/(float)moveTotal);
			delta = Math.max(delta,  0);
			delta = Math.min(delta,  1);
			//Gdx.app.log(TAG, "delta pre limiting "+delta + " stage "+moveCoarse);
			anim.time[anim.tail] = delta;
		} 
		
		
		
		//Gdx.app.log(TAG, "update");
	}
	@Override
	public int getLimbIndex(SpriteAnimation anim) {
		
		return anim.tail;
	}
	@Override
	public void onStart() {
		SpriteAnimation anim = animM .get(parent.e);
		
		//dir = Animations.directions.get(Data.hash("dragontailtipguide"));
		//if (true){
		//	throw new GdxRuntimeException("dir "+Data.getString(hash))
		//}
		limb_index = getLimbIndex(anim);
		item_layer_index = anim.itemLayersByLimbIndex[limb];
		on = false;
		guide_layer = getGuideLayer(anim);
		itemKey = invM.get(parent.e).getActiveItem(limb).hash;
		
		AnimationContainer l = anim.overrideAnimationForLayer(anim.tail, Data.hash("tail"));		
		anim.overrideGuide(guide_layer, Data.hash("tail"));
		anim.time[anim.tail] = .5f;
	}
	@Override
	public void onEnd() {
		SpriteAnimation anim = animM .get(parent.e);

		anim.disableAnimationOverride(limb_index);
		if (guide_layer != -1){
			anim.disableGuideOverride(guide_layer);
		}
		//anim.disableAnimationOverride(limb_index);
		//secondary_limb_index = getSecondaryLimbIndex(anim);
		//if (secondary_limb_index != -1)anim.disableAnimationOverride(secondary_limb_index);
		raceM.get(parent.e).limbTotals[limb]--;
		invM.get(parent.e).dirty = true;
		//Gdx.app.log(TAG, "END");
	}
	@Override
	public int getGuideLayer(SpriteAnimation anim) {
		
		return anim.tail_tip_g;
	}
	

}