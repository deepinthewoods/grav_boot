package com.niz.item;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.AutoGibSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.action.ActionList;
import com.niz.actions.AItemFall;
import com.niz.actions.AProjectile;
import com.niz.anim.Animations;
import com.niz.anim.LayerGuide;
import com.niz.anim.RotatedAnimationLayer;
import com.niz.anim.SpriteCacheNiz;
import com.niz.component.Body;
import com.niz.component.Buckets;
import com.niz.component.CollidesWithMap;
import com.niz.component.Light;
import com.niz.component.LineBody;
import com.niz.component.Map;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.PickUp;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;
import com.niz.component.WeaponSensor;
import com.niz.observer.Subject.Event;
import com.niz.system.MapSystem;

public abstract class ItemDef {
private static final String TAG = "Item def";
public String name;
	public boolean isBlock = false;

	public ItemDef(String string) {
		name = string;
		
	}


public int id;
public String description = "test description"+MathUtils.random(100) + "  and more "+"\nNew Line";
public Array<Doing> doings = new Array<Doing>();

public void doThrow(LayerGuide guide, Entity parent,
		EngineNiz engine, float angle, Item item, int frameIndex, Vector2 basePos) {
	Entity e = engine.createEntity();
	SpriteAnimation anim = engine.createComponent(SpriteAnimation.class);
	anim.alignWithBodyBottom = false;
	//anim.initFor((RotatedAnimationLayer)layer, guide.rotation);
	RotatedAnimationLayer layer = Animations.itemLayers[item.id];
	RotatedAnimationLayer layerSpin = Animations.itemSpinLayers[item.id];
	anim.initFor(layerSpin, guide.rotation);
	tmpV.set(layerSpin.offsets[frameIndex]);
	tmpV.sub(layer.offsets[frameIndex]);
	tmpV.add(guide).add(basePos);
	Position pos = engine.createComponent(Position.class);
	pos.pos.set(tmpV.x, tmpV.y);
	//pos.pos.add(layer.toThrowOffsets[layer.getKeyFrameIndex(0f, guide)]);
	
	
	Physics phys = engine.createComponent(Physics.class);
	phys.limit.set(10000, 10000, 1000);
	Body body = engine.createComponent(Body.class);
	body.width = .0005f;
	body.height = .0005f;
	ActionList action = engine.createComponent(ActionList.class);
	//if (e.getComponent(ActionList.class).size() > 0) throw new GdxRuntimeException("shouldb be 0"+e.getComponent(ActionList.class).size());
	AProjectile projA = new AProjectile();
	e.add(item);
	action.addToStart(projA);
	projA.rotationSpeed = 1200;
	
	CollidesWithMap coll = engine.createComponent(CollidesWithMap.class);
	OnMap onMap = engine.createComponent(OnMap.class);

    Buckets bucket = engine.createComponent(Buckets.class);
	
	LineBody line = engine.createComponent(LineBody.class);
	
	e.add(action);
	//if (e.getComponent(ActionList.class).getAction(AItemStuckInWall.class) != null) throw new GdxRuntimeException("KJFD");
	e.add(pos);
	e.add(phys);
	tmpV.set(25, 0).rotate(angle);
	phys.setLinearVelocity(tmpV.x,  tmpV.y);
    e.add(anim);
	e.add(bucket);
	//e.add(body);
	e.add(line);
	e.add(coll);
	//BitmaskedCollisions bitm = new BitmaskedCollisions();
	//e.add(bitm);
	e.add(onMap);

	Light light = engine.createComponent(Light.class);
	e.add(light);
	
	engine.addEntity(e);
    Gdx.app.log(TAG, "doThrow " + e.getId());
	if (e.getComponent(PickUp.class) != null) throw new GdxRuntimeException("KJFD");
	engine.getSubject("changeLargeBuckets").notify(e, Event.CHANGE_LARGE_BUCKET, null);
}

static Vector2 tmpV = new Vector2();

public void doPlace(LayerGuide guide, Entity parentE, EngineNiz engine,
		GridPoint2 placeTarget, int itemID, int frameIndex, Vector2 basePos) {}

public long startSlashSensor(LayerGuide guide, Entity e, EngineNiz engine,
		 Vector2 pos, int guideLayer, int limbIndex, int itemID) {
	e = engine.createEntity();
	
	WeaponSensor sens = engine.createComponent(WeaponSensor.class);
	sens.parent = e.getId();
	sens.guideLayer = guideLayer;
	sens.limbIndex = limbIndex;
	sens.itemID = itemID;
	e.add(sens);
	Position position = engine.createComponent(Position.class);
	e.add(position);
	LineBody line = engine.createComponent(LineBody.class);

	e.add(line);
	
	engine.addEntity(e);
    return e.getId();
}

public void doDestroy(LayerGuide guide, Entity e, EngineNiz engine,
		GridPoint2 placeTarget, int iid, int i, Vector2 pos) {
	MapSystem map = engine.getSystem(MapSystem.class);
	Map mapc = map.getMapFor(placeTarget.x, placeTarget.y);
	if (mapc != null) {
		int b = mapc.get(placeTarget.x, placeTarget.y);
		mapc.set(placeTarget.x, placeTarget.y, 0);
		//Gdx.app.log(TAG, "destroy"+b);
		Sprite s = SpriteCacheNiz.findSprite(b & Map.TILE_MASK);
		AutoGibSystem gibSys = engine.getSystem(AutoGibSystem.class);
		gibSys.makeGibs(s, true, tmpV.set(placeTarget.x, placeTarget.y).sub(.5f, .5f), true, AItemFall.class, b, (int) (e.getId() + engine.tick));
	}
}

}
