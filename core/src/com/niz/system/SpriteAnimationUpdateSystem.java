package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.math.Vector2;
import com.niz.anim.AnimationContainer;
import com.niz.anim.AnimationLayer;
import com.niz.component.Body;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;
import com.niz.component.SpriteStatic;

public class SpriteAnimationUpdateSystem extends EntitySystem {
	public ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	public ComponentMapper<SpriteAnimation> spriteM = ComponentMapper.getFor(SpriteAnimation.class);
	public ComponentMapper<SpriteStatic> spriteStaticM = ComponentMapper.getFor(SpriteStatic.class);
	protected ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private ImmutableArray<Entity> allEntities;
	private ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	static final float VELOCITY_DIRECTION_THRESHOLD = .00001f;
	private static final String TAG = "spr anim update sys";;
	private Vector2 tmpV = new Vector2(), zeroVector = new Vector2();;

	@Override
	public void addedToEngine(Engine engine) {
		Family allFam = Family.all(Position.class, SpriteAnimation.class).exclude(SpriteStatic.class).get();
		allEntities = engine.getEntitiesFor(allFam);
		super.addedToEngine(engine);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		
		float dt = deltaTime;
		int smallestChangedIndex;
		for (int i = 0; i < allEntities.size(); i++){
			smallestChangedIndex = Integer.MAX_VALUE;
			Entity e = allEntities.get(i);
			SpriteAnimation spr = spriteM.get(e);
			if (!spr.hasStarted) continue;
			//if (spr.left != spr.wasLeft) smallestChangedIndex = 0;
			//spr.wasLeft = spr.left;
			
			Physics phys = physM.get(e);
			Vector2 vel = null;
			if (phys != null){
				vel = phys.vel;
			} else vel = zeroVector;
			AnimationContainer parent =  spr.currentAnim;
			Body body = bodyM.get(e);
			if (Math.abs(vel.x) > VELOCITY_DIRECTION_THRESHOLD){
				spr.left = vel.x < 0;
				//Gdx.app.log(TAG, "left"+spr.left);
			}
			
			
			
			for (int index = 0; index < parent.layers.size; index++)
			{
				AnimationContainer container = parent;
				AnimationLayer layer;// = parent.layers[index];
				//boolean angleDependantFlip;
				if (spr.overriddenAnim.get(index)){
					container = spr.overriddenAnimationLayers[index]; 
					layer = container.layers.get(0);
					//angleDependantFlip = container.angleDependantFlip.get(0);
				}
				else {
					layer = parent.layers.get(index);
					//angleDependantFlip = container.angleDependantFlip.get(index);
					
				}
				
				if (layer == null){
					spr.frameIndices[index] = 0;
					if (index < smallestChangedIndex)
					smallestChangedIndex = index;
					continue;
				}
				if (layer.isVelocityDependant){
					
					spr.time[index] += dt 
							* Math.abs(vel.x)
							* layer.deltaMultiplier;
					//Gdx.app.log("sprite", "vel"+vel.x);
				} 
				else {
					spr.time[index] += dt * layer.deltaMultiplier;
				}	
				//Gdx.app.log(TAG, "time "+spr.time[index]);
				//Gdx.app.log(TAG,  "before "+spr.adjustedLeft[index]+spr.left);;
				if (spr.isAngleFlipLayer[index]) {
					
					//if 
					//;
					//else 
					spr.adjustedLeft[index] = spr.left == (spr.angles[index] < 90 || spr.angles[index] > 270);
					//Gdx.app.log(TAG,  "FLIPFLIPFLIPFLIPFLIPFLPIFLPIPFIPLFIPFLKFIFPFLFIP "+spr.adjustedLeft[index]+spr.left + index + "  " +spr.itemLayersByLimbIndex[0]);;
					if (spr.left) spr.adjustedLeft[index] = !spr.adjustedLeft[index];
					//left = !left ;
					//if (left)
				} else spr.adjustedLeft[index] = spr.left;
				
				//Gdx.app.log(TAG,  "after "+spr.adjustedLeft[index]+spr.left);;

				AtlasSprite s;// = (AtlasSprite) spriteInstance.getKeyFrame(spr.time, spr.left);
				//Gdx.app.log(TAG, "drawpre"+spriteInstance.getKeyFrame(0f).getRegionHeight());
				//if (s == null) continue;
				int fr = layer.getKeyFrameIndex(spr.time[index], spr.guides.get(spr.layerSources[index]));
				if (spr.frameIndices[index] != fr){
					if (index < smallestChangedIndex)
						smallestChangedIndex = index;
					
					spr.frameIndices[index] = fr;
					//Gdx.app.log(TAG, "update frame"+fr);
				}
				//int frame = spr.frames[index];
			}
			
			Vector2 v = posM.get(e).pos;
			//float yOffset = 0;
			//if (spr.guides.size == 0) continue;//return;
			if (spr.guides.size != 0){
				if (body != null && spr.alignWithBodyBottom) spr.guides.get(0).set(0, -body.height);
				else spr.guides.get(0).set(0,0);
				
			}
			//smallestChangedIndex = 0;
			spr.updateGuides(1, spr.left);
			
		}
}

}
