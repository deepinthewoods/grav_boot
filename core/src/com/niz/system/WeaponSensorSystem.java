package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.niz.anim.Animations;
import com.niz.anim.LayerGuide;
import com.niz.component.Inventory;
import com.niz.component.LineBody;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;
import com.niz.component.WeaponSensor;

public class WeaponSensorSystem extends EntitySystem {

	private Family family;
	private ImmutableArray<Entity> entities;
	private ComponentMapper<Position> posM;
	private ComponentMapper<WeaponSensor> sensM;
	private EngineNiz engine;
	private ComponentMapper<SpriteAnimation> animM;
	private ComponentMapper<Inventory> invM;
	private Family otherFamily;
	private ComponentMapper<LineBody> lineM;

	@Override
	public void addedToEngine(Engine engine) {
		family = Family.all(Position.class, WeaponSensor.class, LineBody.class).get();
		otherFamily = Family.all(Position.class, SpriteAnimation.class, Inventory.class).get();
		entities = engine.getEntitiesFor(family);
		posM = ComponentMapper.getFor(Position.class);
		sensM = ComponentMapper.getFor(WeaponSensor.class);
		//animM = ComponentMapper.getFor(SpriteAnimation.class);
		animM = ComponentMapper.getFor(SpriteAnimation.class);
		invM = ComponentMapper.getFor(Inventory.class);
		lineM = ComponentMapper.getFor(LineBody.class);
		this.engine = (EngineNiz) engine;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}
	private Vector2 tmpV = new Vector2();
	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			WeaponSensor sens = sensM.get(e);
			Position pos = posM.get(e);
			Entity otherE = engine.getEntity(sens.parent);
			if (otherE == null) continue;
			if (!otherFamily.matches(otherE)) continue;
			SpriteAnimation anim = animM.get(otherE);
			
			Position otherPos = posM.get(otherE);
			
			Inventory inv = invM.get(otherE);
			LineBody line = lineM.get(e);
			
			sens.prevOffsetA.set(line.offsetA);
			sens.prevOffsetB.set(line.offsetB);
			
			LayerGuide guide = anim.guides.get(sens.guideLayer);
			//sens.offsetA.set(otherPos.pos);
			//sens.offsetA.add(guide);
			
			int itemLayer = anim.itemLayersByLimbIndex[sens.limbIndex]; 
			int frame = anim.frameIndices[itemLayer];
			
			tmpV.set(Animations.itemLayers[sens.itemID].offsets[frame]);
			tmpV.sub(Animations.itemSpinLayers[sens.itemID].offsets[frame]);
			line.offsetA.set(tmpV);
			
			tmpV.set(Animations.itemTipLayers[sens.itemID].offsets[frame]);
			tmpV.sub(Animations.itemSpinLayers[sens.itemID].offsets[frame]);
			line.offsetB.set(tmpV);
			if (anim.adjustedLeft[anim.guideFrameSources[sens.guideLayer]]){
				line.offsetA.x *= -1;
				line.offsetB.x *= -1;
			}
			
			pos.pos.set(otherPos.pos).add(guide);
			
		}
		
		
		super.update(deltaTime);
	}

}
