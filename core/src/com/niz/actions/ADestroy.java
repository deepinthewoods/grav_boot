package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;
import com.niz.BlockDefinition;
import com.niz.Data;
import com.niz.GameInstance;
import com.niz.Input;
import com.niz.Main;
import com.niz.RayCaster;
import com.niz.action.LimbAction;
import com.niz.anim.AnimationContainer;
import com.niz.anim.AnimationLayer;
import com.niz.anim.LayerGuide;
import com.niz.component.BlockLine;
import com.niz.component.BlockOutline;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.Inventory;
import com.niz.component.Map;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;
import com.niz.item.Item;
import com.niz.system.MapSystem;

public abstract class ADestroy extends LimbAction {
	private static final String TAG = "place action";
	private static final float EXTRA_COOLDOWN_TIME = .15f;
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);
	private static ComponentMapper<BlockLine> lineBodyM = ComponentMapper.getFor(BlockLine.class);
	Family playerFamily = Family.all(Player.class).get();
	private static Vector2 tmp = new Vector2(), p1 = new Vector2(), p2 = new Vector2(), p3 = new Vector2(), p4 = new Vector2();
	int angleIndex;
	private int limb_index;
	
	public boolean started;
	
	private int guide_layer;
	public int item_layer_index;
	private boolean cooldown;
	//private Item item;
	private float throwAngle;
	private GridPoint2 placeTarget = new GridPoint2(), prevPlaceTarget = new GridPoint2();
	//private transient MapSystem map;
	//private transient Entity showEntity;
	private boolean valid;
	//private transient Entity lineEntity;
	private int itemID;
	private long showEntityID;
	private long lineEntityID;
	private long cooldownEndTick;

	@Override
	public void update(float dt) {
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
			if (valid && !started){
				started = true;
				Item item = invM.get(parent.e).getItem(itemID);
				
				MapSystem map = parent.engine.getSystem(MapSystem.class);
				Map mapc = map.getMapFor(placeTarget.x, placeTarget.y);
				if (mapc != null) {
					int b = mapc.get(placeTarget.x, placeTarget.y);
					BlockDefinition def = MapSystem.getDef(b);
					SpriteAnimation spr = animM.get(parent.e);
					AnimationLayer layer = spr.getLayer(limb_index);
					layer.deltaMultiplier = item.destroyBlockBaseAlpha;
					layer.deltaMultiplier *= def.destroyMultiplier;
					//Gdx.app.log(TAG,  "destroy at "+placeTarget + "  " + b);
					
				}
			}
		}
		if (started){
			if (playerFamily.matches(parent.e))GameInstance.unPause = true;
			AnimationContainer layer = anim.overriddenAnimationLayers[limb_index];
			if (layer == null || layer.layers.get(0).isAnimationFinished(anim.time[limb_index])){
				if (cooldown ) {
					if (parent.engine.tick > cooldownEndTick)
						isFinished = true;
				}
				else {
					cooldown = true;
					cooldownEndTick = (long)(parent.engine.tick + EXTRA_COOLDOWN_TIME / Main.timeStep);
					Item item = invM.get(parent.e).getItem(itemID);
					//item.durability -= durabilityDelta;
					invM.get(parent.e).dirty = true;
					AnimationLayer itemLayer = anim.getLayer(item_layer_index);
					LayerGuide guide = anim.getGuide(guide_layer);
					//item.getDef().doThrow(guide, parent.e, parent.engine, throwAngle, item.id, anim.frameIndices[item_layer_index]);
					//item.getDef().doPlace(guide, parent.e, parent.engine, placeTarget, item.id, anim.frameIndices[item_layer_index], pos.pos);
					//Gdx.app.log(TAG, "placeplace "+item.getDef().id);
					item.getDef().doDestroy(guide, parent.e, parent.engine, placeTarget, item.id, anim.frameIndices[item_layer_index], pos.pos);

					
					anim.disableAnimationOverride(item_layer_index);
					int animID = Data.hash("throwcooldown"+angleIndex);
					anim.overrideAnimationForLayer(limb_index, animID).layers.get(0);
					if (guide_layer != -1){
						anim.overrideGuide(guide_layer, animID);
					}
					
				}
				
			}
			
		} else {//!started
			updateAim(control, anim);
			OnMap onMap = onMapM.get(parent.e);
			
			start.set((int)pos.pos.x, (int)pos.pos.y);
			
			tmpV.set(16, 0);
			tmpV.rotate(throwAngle);
			tmpV.add(pos.pos).add(anim.guides.get(anim.head_g));
			end.set((int)tmpV.x, (int)tmpV.y);
			
			tmp.set(pos.pos).add(anim.guides.get(anim.head_g));
			cast.trace(tmp, tmpV);
			GridPoint2 p = Pools.obtain(GridPoint2.class);
			boolean done = false;
			MapSystem map = parent.engine.getSystem(MapSystem.class);
			Entity showEntity = parent.engine.getEntity(showEntityID);
			Entity lineEntity = parent.engine.getEntity(lineEntityID);
			int prevFace = 0, face = 0;
			while (cast.hasNext){
				cast.next();
				p.set(cast.x, cast.y);
				face = cast.face;
				prevFace = face;
				Map mapc = map.getMapFor(p.x, p.y);
				if (mapc == null) continue;
				int b = mapc.get(p.x, p.y);
				
				placeTarget.set(p);
				prevPlaceTarget.set(placeTarget);
				if (b != 0){
					//p.x += RayCaster.normalOffsets[cast.face].x;
					//p.y += RayCaster.normalOffsets[cast.face].y;
					if (showEntity != null) {
						//throw new GdxRuntimeException("null e "+showEntityID);
						
						posM.get(showEntity).pos.set(placeTarget.x, placeTarget.y).add(.5f, .5f);
						
						
					}

					//
					p1.set(pos.pos).add(anim.guides.get(anim.head_g));
					p2.set(100, 0).rotate(throwAngle).add(pos.pos).add(anim.guides.get(anim.head_g));
					tmpV.set(p1);
					
					p3.set(cast.normal2LineOffsets[(prevFace)%4]).add(prevPlaceTarget.x, prevPlaceTarget.y);
					p4.set(cast.normal2Line2Offsets[(prevFace)%4]).add(prevPlaceTarget.x, prevPlaceTarget.y);
					Intersector.intersectLines(p1, p2, p3, p4, tmpV);
					//tmpV.set(tmpV2);
					//Gdx.app.log(TAG, " intercept "+face);
					
					
					
					
					if (lineEntity != null){
						
						posM.get(lineEntity).pos.set(p1);
						BlockLine line = lineBodyM.get(lineEntity);
						if (line == null) return;//throw new GdxRuntimeException("null line c "+lineEntityID);
						line.end.set(tmpV);
					}
					done = true;
					break;
				}
			}
			if (valid != done){
				valid = done;
				if (valid){
					//create highlight
				} else {
					//destroy#s
					posM.get(showEntity).pos.set(-99, -99);
					
					posM.get(lineEntity).pos.set(-99, -99);
					lineBodyM.get(lineEntity).end.set(-99, -99);
				}
			}
			Pools.free(p);
			
			
			
			
		}
	}
	static RayCaster cast = new RayCaster();
	static Bresenham2 ray = new Bresenham2();
	static GridPoint2 start = new GridPoint2(), end = new GridPoint2();
	static Vector2 tmpV = new Vector2(), tmpV2 = new Vector2(), tmpV3 = new Vector2(), len = new Vector2(), len2 = new Vector2();;
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
		//Gdx.app.log(TAG, "end "+hashCode());
		parent.engine.removeEntity(showEntityID);
		parent.engine.removeEntity(lineEntityID);
	}

	@Override
	public void onStart() {
		valid = false;

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
		itemID = invM.get(parent.e).getActiveItem(limb).hash;
		
		PooledEntity showEntity = parent.engine.createEntity();
		Position pos = parent.engine.createComponent(Position.class);
		BlockOutline block = parent.engine.createComponent(BlockOutline.class);
		pos.pos.set(-1000, -1000);
		showEntity.add(pos);
		showEntity.add(block);
		parent.engine.addEntity(showEntity);
		showEntityID = showEntity.getId();
		
		PooledEntity lineEntity = parent.engine.createEntity();
		pos = parent.engine.createComponent(Position.class);
		pos.pos.set(-1000, -1000);
		BlockLine line = parent.engine.createComponent(BlockLine.class);
		lineEntity.add(pos);
		lineEntity.add(line);
		parent.engine.addEntity(lineEntity);
		lineEntityID = lineEntity.getId();
		//Gdx.app.log(TAG, "start " + lineEntityID);
		
	}

}