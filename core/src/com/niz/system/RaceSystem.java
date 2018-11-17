package com.niz.system;

import java.util.Random;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pools;
import com.niz.Data;
import com.niz.action.LimbAction;
import com.niz.actions.ADestroyBackHand;
import com.niz.actions.ADoNothing;
import com.niz.actions.APlaceBackHand;
import com.niz.actions.APlaceFrontHand;
import com.niz.actions.ASlashBackHand;
import com.niz.actions.AThrowBackHand;
import com.niz.actions.AThrowFrontHand;
import com.niz.actions.AThrowHead;
import com.niz.actions.AThrowTail;
import com.niz.actions.AThrustBackHand;
import com.niz.anim.AnimationContainer;
import com.niz.anim.Animations;
import com.niz.anim.LayerGuide;
import com.niz.component.Inventory;
import com.niz.component.MovementData;
import com.niz.component.Physics;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class RaceSystem extends EntitySystem implements Observer, EntityListener {
	
	
	private static final String TAG = "race system"
			;
	ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);
	ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private Subject subject;
	private ImmutableArray<Entity> entities;

	@Override
	public void addedToEngine(Engine engine) {
		subject = ((EngineNiz) engine).getSubject("populatepaperdoll");
		subject.add(this);
		Family family = Family.all(Race.class, SpriteAnimation.class, Inventory.class).get();
		entities = engine.getEntitiesFor(family);
		engine.addEntityListener(family, this);
		Subject changedSubject = ((EngineNiz) engine).getSubject("equipitem");
		changedSubject.add(new Observer(){

			@Override
			public void onNotify(Entity e, Event event, Object c) {
				if (e == null) return;
				Race r = raceM.get(e);
				SpriteAnimation anim = animM.get(e);
				Inventory inv = invM.get(e);
				Gdx.app.log(TAG, "EQUIP ITEM NOTIFICATION");

				for (int i = 0; i < anim.itemLayersByLimbIndex.length; i++){
					if (r.limbTotals[i] == 0 ){
						int layerID = anim.itemLayersByLimbIndex[i];
						int itemID = inv.getItemIDByLimb(i);
						if (layerID == -1) continue;
						AnimationContainer layer = anim.overrideSpriteForLayer(layerID, Animations.itemLayers[itemID]);
						if (Animations.itemLayers[itemID] == null) return;
						if (layer.layers == null) throw new GdxRuntimeException("jhkl");
						if (layer.layers.size == 0) throw new GdxRuntimeException("dfjhkl");
						//layer.layers.get(0).angleDependantFlip = false;
						//anim.isAngleFlipLayer[layerID] = false;
						//anim.adjustedLeft[layerID] = true;
						//anim.resetItemLimbAngleFlipped(i);
						//anim.syncAllTimesWithLayer(anim.back_leg);
						
					}
					r.oldLimbTotals[i] = r.limbTotals[i];
				}
			}
			
		});
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
	}

	@Override
	public void update(float deltaTime) {
		for (int m = 0; m < entities.size(); m++){
			Entity e = entities.get(m);
			Race r = raceM.get(e);
			SpriteAnimation anim = animM.get(e);
			Inventory inv = invM.get(e);
			boolean didSomething = false;
			for (int i = 0; i < anim.itemLayersByLimbIndex.length; i++){
				if (( r.oldLimbTotals[i] != r.limbTotals[i]) || inv.dirtyLimbs){
					didSomething = true;
					int layerID = anim.itemLayersByLimbIndex[i];
					if (layerID == -1) continue;
					int itemID = inv.getItemIDByLimb(i);
					//	Gdx.app.log(TAG, "DRITY"+layerID + " " + i);
					AnimationContainer layer = anim.overrideSpriteForLayer(layerID, Animations.itemLayers[itemID]);
					if (layer.layers.get(0) == null) continue;
					//layer.layers.get(0).angleDependantFlip = false;
					//anim.isAngleFlipLayer[layerID] = false;
					//anim.disableAnimationOverride(anim.itemLayersByLimbIndex[i]-1);
					//anim.disableGuideOverride(anim.back_arm);
					
					
					//anim.switchItemLimbToAngleFlipped(i);
					//anim.switchLayerToAngleFlipped(anim.getLimbIndex(i));
					//anim.resetItemLimbAngleFlipped(i);
					//anim.resetLayerAngleFlipped(anim.getLimbIndex(i));
					//anim.time[anim.getLimbIndex(i)] = anim.time[anim.back_leg];
				}
				r.oldLimbTotals[i] = r.limbTotals[i];
			}
			if (didSomething){
				//anim.resetAllTimes();
			}
			inv.dirtyLimbs = false;
			
			
			if (r.dirtyLayers){
				//Gdx.app.log(TAG, "dirty layers");
				//anim.reset();
				//entityAdded(e);
				//anim.start(anim.current);
				
				e.remove(SpriteAnimation.class);
				SpriteAnimation sp = (SpriteAnimation) new SpriteAnimation().set(Animations.PLAYER);
				e.add(sp);
				sp.resume();
				
				//if (true) throw new GdxRuntimeException("jk;d);");
				r.dirtyLayers = false;
			}
		}
	}

	Random rand = new Random();
	private IntArray guideFrameSources = new IntArray();
    IntArray guideSources = new IntArray();
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		//if (true) throw new GdxRuntimeException("");
		//entityAdded(e);
		//Gdx.app.log(TAG, "notify"+event);
		//if (true) throw new GdxRuntimeException("n");
		raceM.get(e).dirtyLayers = true;
		invM.get(e).dirtyLimbs = true;
	}

	@Override
	public void entityAdded(Entity e) {
		//Gdx.app.log(TAG, "ADDED");
		//Entity pe = e;	
		//rand.setSeed(pe.seed);
		SpriteAnimation anim = e.getComponent(SpriteAnimation.class);
		Race race = raceM.get(e);
		
		anim.layerSources =new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};//guide layer that layers use to render
		
		//int armIDBack = 0, legID = 0, torsoID = 0, tailID = 0, armIDFront = 0;
		guideSources.clear();


        //race.raceID[Race.TAIL] = Race.RED_DRAGON;
		//race.raceID[Race.BACK_ARM] = Race.RED_DRAGON;
		//race.raceID[Race.BACK_LEG] = Race.RED_DRAGON;
		//race.raceID[Race.FRONT_ARM] = Race.RED_DRAGON;
		//race.raceID[Race.FRONT_LEG] = Race.RED_DRAGON;
		//race.raceID[Race.HEAD] = Race.RED_DRAGON;
		//race.raceID[Race.TORSO] = Race.RED_DRAGON;
		//race.raceID[Race.NECK] = Race.RED_DRAGON;
		
		
		
		//int LEGS_G = 0, TORSO_G = 0, NECK_G = 0, HEAD_G = 0, 
						//ARMS_G = 0, HAND_BACK_G = 0, HAND_FRONT_G = 0, TAIL_G = 0;
		String[] racePrefixes = {"player", "reddragon", "greeddragon", "whitedragon"};
		String[] guidePrefixes = {"player", "dragon", "dragon", "dragon"};
		int[] armBack = new int[racePrefixes.length], armFront = new int[racePrefixes.length], legBack = new int[racePrefixes.length], legFront = new int[racePrefixes.length],
				torso = new int[racePrefixes.length], head = new int[racePrefixes.length], tail = new int[racePrefixes.length], neck = new int[racePrefixes.length]
						
				, handBackG  = new int[racePrefixes.length], handFrontG = new int[racePrefixes.length]
						, legG = new int[racePrefixes.length], torsoG = new int[racePrefixes.length], torso2G = new int[racePrefixes.length]
								, armsG = new int[racePrefixes.length], tailG = new int[racePrefixes.length]
										, tailTipG = new int[racePrefixes.length], headG = new int[racePrefixes.length]
												, neckG = new int[racePrefixes.length]
						;


        for (int i = 0; i < racePrefixes.length; i++){
			armBack[i] = Data.hash(racePrefixes[i] + "armback");
			armFront[i] = Data.hash(racePrefixes[i] + "armfront");
			legFront[i] = Data.hash(racePrefixes[i] + "legfront");
			legBack[i] = Data.hash(racePrefixes[i] + "legback");
			torso[i] = Data.hash(racePrefixes[i] + "torso");
			neck[i] = Data.hash(racePrefixes[i] + "neck");
			head[i] = Data.hash(racePrefixes[i] + "head");
			tail[i] = Data.hash(racePrefixes[i] + "tail");
			handBackG[i] = Data.hash(guidePrefixes[i] + "handbackguide");
			handFrontG[i] = Data.hash(guidePrefixes[i] + "handfrontguide");
			legG[i] = Data.hash(guidePrefixes[i] + "legguide");
			torsoG[i] = Data.hash(guidePrefixes[i] + "torsoguide");
			torso2G[i] = Data.hash(guidePrefixes[i] + "torso2guide");
			armsG[i] = Data.hash(guidePrefixes[i] + "armsguide");
			tailG[i] = Data.hash(guidePrefixes[i] + "tailguide");
			tailTipG[i] = Data.hash(guidePrefixes[i] + "tailtipguide");
			neckG[i] = Data.hash(guidePrefixes[i] + "neckguide");
			headG[i] = Data.hash(guidePrefixes[i] + "headguide");
			
		}
		int item = Data.hash("item");
		
		int i = 0;
		
			anim.layerIDs.add(armBack[race.raceID[Race.BACK_ARM]]);
			anim.back_arm = i;
			i++;
			anim.layerIDs.add(item);
			anim.itemLayersByLimbIndex[Race.LIMB_BACK_HAND] = i;
			
		
		i++;
		
		
		anim.layerIDs.add(legBack[race.raceID[Race.BACK_LEG]]);
		anim.back_leg = i;
			
		
		i++;
		
			anim.layerIDs.add(torso[race.raceID[Race.TORSO]]);
			anim.torso = i;
			
		
		i++;
		
			anim.layerIDs.add(neck[race.raceID[Race.NECK]]);
			anim.neck = i;
			
		i++;
		
			anim.layerIDs.add(legFront[race.raceID[Race.FRONT_LEG]]);
			anim.front_leg = i;
			
		i++;
		
			anim.layerIDs.add(head[race.raceID[Race.HEAD]]);
			anim.head = i;
			i++;
			anim.layerIDs.add(Data.hash("item"));
			anim.itemLayersByLimbIndex[Race.LIMB_HEAD] = i;
		
		i++;
		switch (race.raceID[Race.TAIL]){
		case Race.HUMAN:i--;
		anim.itemLayersByLimbIndex[Race.LIMB_TAIL] = -1;

		break;
		case Race.RED_DRAGON:
			anim.layerIDs.add(tail[race.raceID[Race.TAIL]]);
			anim.tail = i;
			i++;
			anim.layerIDs.add(Data.hash("item"));
			anim.itemLayersByLimbIndex[Race.LIMB_TAIL] = i;
			break;
		}
		i++;
		
			anim.layerIDs.add(armFront[race.raceID[Race.FRONT_ARM]]);
			anim.front_arm = i;
			i++;
			anim.layerIDs.add(Data.hash("item"));
			anim.itemLayersByLimbIndex[Race.LIMB_FRONT_HAND] = i;
			//Gdx.app.log(TAG,  "BACKKK AAAAAAAAAAAARM"+i);
			
		i++;
		
		//anim.guideFrameSources = new int[]{armID, torsoID, torsoID, torsoID, legID, legID, legID, torsoID};//animation layer that the guide takes its's frame from
		//anim.guideFrameSources = new int[]{0, 2, 2, 2, 1, 1, 1, 2};
		guideFrameSources.clear();
		/////////////////////////////////////GUIDES
		//new int[]{-1, 0, 1, 2, 1, 4, 4, 1};//refers to the other guides it inherits position from

		
		//Gdx.app.log(TAG, "INIT RACE");
		//anim.
		int hash;
		anim.guideIDs.clear();
		i = 0;
		switch (race.raceID[Race.BACK_LEG]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.guideIDs.add(legG[race.raceID[Race.BACK_LEG]]);//
			guideFrameSources.add(0);
			guideSources.add(-1);
			//Gdx.app.log(TAG, "playerlegsguide");
			anim.legs_g = i;
			
			
		}
		i++;
		switch (race.raceID[Race.BACK_LEG]){
		case Race.HUMAN:
			hash = torsoG[race.raceID[Race.BACK_LEG]];
			anim.guideIDs.add(hash);
			guideFrameSources.add(anim.back_leg);
			anim.torso_g = i;
			guideSources.add(anim.legs_g);
			break;
		case Race.RED_DRAGON:
			hash = torsoG[race.raceID[Race.BACK_LEG]];
			if (race.raceID[Race.TORSO] == Race.HUMAN)
				hash = torso2G[race.raceID[Race.BACK_LEG]];
			
			anim.guideIDs.add(hash);
			anim.torso_g = i;
			guideFrameSources.add(anim.back_leg);
			guideSources.add(anim.legs_g);
			break;
		}
		i++;
		switch (race.raceID[Race.TORSO]){
		case Race.HUMAN:
			anim.guideIDs.add(neckG[race.raceID[Race.TORSO]]);
			guideFrameSources.add(anim.torso);
			//Gdx.app.log(TAG, "playerneckguide");
			anim.neck_g = i;
			guideSources.add(anim.torso_g);
			break;
		case Race.RED_DRAGON:
			anim.guideIDs.add(neckG[race.raceID[Race.TORSO]]);
			guideFrameSources.add(anim.torso);
			anim.neck_g = i;
			guideSources.add(anim.torso_g);
			break;
		}
		i++;
		switch (race.raceID[Race.NECK]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.guideIDs.add(headG[race.raceID[Race.NECK]]);
			guideFrameSources.add(anim.neck);
			//Gdx.app.log(TAG, "playerheadguide");
			anim.head_g = i;
			guideSources.add(anim.neck_g);
			break;
			
		}
		i++;
		switch (race.raceID[Race.TORSO]){
		case Race.HUMAN:
		case Race.RED_DRAGON:			
			anim.guideIDs.add(armsG[race.raceID[Race.TORSO]]);
			guideFrameSources.add(anim.torso);
			//Gdx.app.log(TAG, "playerarmsguide");
			anim.arms_g = i;
			guideSources.add(anim.torso_g);
			break;
			
		}
		i++;
		switch (race.raceID[Race.BACK_ARM]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.guideIDs.add(handBackG[race.raceID[Race.BACK_ARM]]);
			guideFrameSources.add(anim.back_arm);
			anim.hand_back_g = i;
			anim.guideLayersByLimbIndex[Race.LIMB_BACK_HAND] = i;
			guideSources.add(anim.arms_g);
			//Gdx.app.log(TAG, "playerhandbackguide");
			break;
			
		}
		i++;
		switch (race.raceID[Race.TORSO]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.guideIDs.add(tailG[race.raceID[Race.TORSO]]);
			anim.tail_g = i;
			guideFrameSources.add(anim.torso);
			guideSources.add(anim.torso_g);
			//Gdx.app.log(TAG, "playertailguide");
			break;
			
		}
		i++;
		switch (race.raceID[Race.TAIL]){
		case Race.HUMAN:
			i--;
			
			break;
		case Race.RED_DRAGON:
			anim.guideIDs.add(tailTipG[race.raceID[Race.TAIL]]);
			anim.tail_tip_g = i;
			guideFrameSources.add(anim.tail);
			anim.guideLayersByLimbIndex[Race.LIMB_TAIL] = i;
			guideSources.add(anim.tail_g);
			break;
		}
		i++;
		switch (race.raceID[Race.FRONT_ARM]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.guideIDs.add(handFrontG[race.raceID[Race.FRONT_ARM]]);
			//Gdx.app.log(TAG, "playerhandfrontguide");
			anim.hand_front_g = i;
			guideFrameSources.add(anim.front_arm);
			anim.guideLayersByLimbIndex[Race.LIMB_FRONT_HAND] = i;
			guideSources.add(anim.arms_g);
			break;
			
		}
		i++;
		//////////////////////////////////LAYER SOURCES
		
		i = 0;
		switch (race.raceID[Race.BACK_ARM]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			
			anim.layerSources[i] = anim.arms_g;
			i++;
			anim.layerSources[i] = anim.hand_back_g;
			break;
			
		}
		i++;
		switch (race.raceID[Race.BACK_LEG]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.layerSources[i] = anim.legs_g;
			break;
		}
		i++;
		switch (race.raceID[Race.TORSO]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.layerSources[i] = anim.torso_g;
			break;
		}
		i++;
		switch (race.raceID[Race.NECK]){
			
		case Race.HUMAN://i--;
		case Race.RED_DRAGON:
			anim.layerSources[i] = anim.neck_g;
			break;
		}
		i++;
		switch (race.raceID[Race.FRONT_LEG]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.layerSources[i] = anim.legs_g;
			break;
		}
		i++;
		switch (race.raceID[Race.HEAD]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			anim.layerSources[i] = anim.head_g;
			i++;
			anim.layerSources[i] = anim.head_g;
			break;
		}
		i++;
		switch (race.raceID[Race.TAIL]){
		
		case Race.HUMAN:i--;
		break;
		case Race.RED_DRAGON:
			
			anim.layerSources[i] = anim.tail_g;
			i++;
			anim.layerSources[i] = anim.tail_tip_g;
			break;
		}
		i++;
		switch (race.raceID[Race.FRONT_ARM]){
		case Race.HUMAN:
		case Race.RED_DRAGON:
			
			anim.layerSources[i] = anim.arms_g;
			i++;
			anim.layerSources[i] = anim.hand_front_g;
			break;
		}
		i++;
		
		anim.guides.clear();
		for (int z = 0; z < anim.guideIDs.size; z++){
			anim.guides.add(Pools.obtain(LayerGuide.class));
		}
		
		
		race.setLimbActions(actions );

        //anim.colors[anim.back_arm] = Data.BLUE_INDEX;
		//anim.colors[anim.back_leg] = Data.BLUE_INDEX;
		//anim.colors[anim.torso] = Data.BLUE_INDEX;
		//anim.colors[anim.head] = Data.BLUE_INDEX;
		
			
		anim.guideFrameSources = guideFrameSources.toArray();
		anim.guideSources = guideSources.toArray();
			
		anim.resume();
		
		Physics phys = physM.get(e);
		MovementData mov = moveM.get(e);
		///////////movement data
		switch (race.physicsID){
		case Race.PHYSICS_NOrMAL:
			phys.limit.set(10,162, 17);
			phys.gravity.set(0, Physics.STANDARD_GRAVITY);
			mov.jump_impulse = 17.2f;
			mov.run_force = 25f;
			mov.jump_y_force = .44f;
			mov.jump_x_force = 65f;
			mov.jump_y_force_decrement_rate = 59.4f;
			mov.jump_y_force_time = 0.5f;
			mov.jump_y_force_delay = .1f;
			mov.walljump_x_impulse = 15;
			mov.walljump_y_impulse = 16.2f;
			mov.cancelLiftOnRelease = true;
			mov.changeDirectionForceMultiplier = 1.6f;
			break;
		case Race.PHYSICS_RUNNER:
			phys.limit.set(20,11162, 112997);
			phys.gravity.set(0, -70);
			mov.jump_impulse = 17.2f;
			mov.run_force = 225f;
			mov.jump_y_force = 44f;
			mov.jump_x_force = 65f;
			mov.jump_y_force_decrement_rate = 0f;
			mov.jump_y_force_time = 1111111111110.5f;
			mov.jump_y_force_delay = .001f;
			mov.walljump_x_impulse = 15;
			mov.walljump_y_impulse = 16.2f;
			mov.cancelLiftOnRelease = true;
			mov.changeDirectionForceMultiplier = 1.6f;
			
			break;
			
		}
		
		
	}
	
	Class<? extends LimbAction>[][] actions = new Class[][]{
			{AThrowBackHand.class, AThrowFrontHand.class, AThrowTail.class, AThrowHead.class},
			{APlaceBackHand.class, APlaceFrontHand.class},
			{ADoNothing.class},
			{ASlashBackHand.class},
			{AThrustBackHand.class},
			{ADestroyBackHand.class}
	};
	@Override
	public void entityRemoved(Entity entity) {
		//Gdx.app.log(TAG, "entity removed");
	}

	
}
