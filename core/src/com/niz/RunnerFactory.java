package com.niz;



import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.action.ActionList;
import com.niz.actions.ACameraFollowPlayer;
import com.niz.actions.AHold;
import com.niz.actions.AJumpCharSelect;
import com.niz.actions.ALoopAroundMap;
import com.niz.actions.ANotRun;
import com.niz.actions.ATailControl;
import com.niz.actions.AUseInventory;
import com.niz.actions.mapgen.AAgentBuildMapRunner;
import com.niz.actions.mapgen.AAgentRandomBuildPlatforms;
import com.niz.actions.mapgen.AGenerateEntities;
import com.niz.anim.Animations;
import com.niz.component.BitmaskedCollisions;
import com.niz.component.Body;
import com.niz.component.Buckets;
import com.niz.component.CameraControl;
import com.niz.component.CollidesWithMap;
import com.niz.component.Control;
import com.niz.component.DragOption;
import com.niz.component.Inventory;
import com.niz.component.Light;
import com.niz.component.Map;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.PathfinderPreLog;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SelectedPlayer;
import com.niz.component.SpriteAnimation;
import com.niz.component.TransientComponent;
import com.niz.item.ItemDef;
import com.niz.system.OverworldSystem;
import com.niz.system.WorkerSystem;
import com.niz.ui.edgeUI.InventoryScreen;

public class RunnerFactory extends Factory {

	private static final String TAG = "platformer factory";
	public static final int CHAR_SELECT_SPACING = 4;
	
	public Race[] charSelectRaces, pathfindingRaces;
	public Inventory[] charSelectInventories, pathfindingInventories;
	public static final int CHAR_SELECT_CHARACTERS = 8, PATHFINDING_COUNT = 32;
	public RunnerFactory(){
		charSelectRaces = new Race[CHAR_SELECT_CHARACTERS];
		for (int i = 0; i < charSelectRaces.length; i++){
			charSelectRaces[i] = new Race();
			charSelectRaces[i].physicsID = Race.PHYSICS_RUNNER;
		}
		charSelectRaces[2].raceID[Race.BACK_ARM] = Race.RED_DRAGON;
		charSelectRaces[2].raceID[Race.FRONT_ARM] = Race.RED_DRAGON;
		
		charSelectRaces[1].raceID[Race.FRONT_ARM] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.BACK_ARM] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.BACK_LEG] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.FRONT_LEG] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.TORSO] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.NECK] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.HEAD] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.TAIL] = Race.RED_DRAGON;

		charSelectInventories = new Inventory[CHAR_SELECT_CHARACTERS];
		for (int i = 0; i < charSelectInventories.length; i++){
			Inventory inv = new Inventory();
			charSelectInventories[i] = inv;
			inv.addItem(16, 6);

		}
		
		pathfindingRaces = new Race[PATHFINDING_COUNT];
		pathfindingInventories = new Inventory[PATHFINDING_COUNT];
		for (int i = 0; i < pathfindingInventories.length; i++){
			Inventory inv = new Inventory();
			pathfindingInventories[i] = inv;
			inv.addItem(16, 6);
		}
		for (int i = 0; i < pathfindingRaces.length; i++){
			pathfindingRaces[i] = new Race();
			pathfindingRaces[i].physicsID = Race.PHYSICS_RUNNER;

		}
		
	}


	public PooledEntity makePlayer(EngineNiz engine) {
		PooledEntity e = engine.createEntity();
		
		Position pos = engine.createComponent(Position.class);
		e.add(pos);
		
		Physics phys = engine.createComponent(Physics.class);
		phys.bodyTypeID = 1;
		
		e.add(phys);
		e.add(engine.createComponent(SpriteAnimation.class).set(Animations.PLAYER));
		ActionList act = Pools.obtain(ActionList.class);

		act.addToStart(new AUseInventory());
		act.addToStart(new AHold());
		act.addToStart(new ANotRun());
		act.addToStart(new ATailControl());
		
		
		//act.addToStart(new AAutoRun());
		e.add(act);
		e.add(new Control());
		MovementData mov = Pools.obtain(MovementData.class);
		mov.hasWallSlide = true;
		
		e.add(mov);
		
		//e.add(new Light());
		e.add(new Buckets());
		Body col = new Body();
		e.add(col);
		col.width = .325f;
		col.height = .375f;
		e.add(new CollidesWithMap());
		BitmaskedCollisions bitm = new BitmaskedCollisions();
		e.add(bitm);
		bitm.startBit = 10;
		
		
		e.add(new Inventory());
		OnMap onMap = new OnMap();
		onMap.map = null;
		e.add(onMap);

		
		pos.pos.set(250, 4);
		return e;
	}

	@Override
	public PooledEntity createCamera(EngineNiz engine) {
		//if (true) throw new GdxRuntimeException("jkl");
		PooledEntity cam = engine.createEntity();
		cam.add(new CameraControl());
		Position camPos = engine.createComponent(Position.class);
		cam.add(camPos);
		ActionList camActions = engine.createComponent(ActionList.class);
		camActions.addToEnd(new ACameraFollowPlayer());
		cam.add(camActions);
		cam.add(Pools.obtain(TransientComponent.class));
		return cam;
	}



	@Override
	public void makeLevelSelection(EngineNiz engine, WorldDefinition worldDef) {
		WorkerSystem workSys = engine.getSystem(WorkerSystem.class);

		for (int i = 0; i < CHAR_SELECT_CHARACTERS; i++){
			PooledEntity e = makePlayer(engine);
			Race race = engine.createComponent(Race.class);
			for (int r = 0; r < race.raceID.length; r++){
				race.raceID[r] = charSelectRaces[i].raceID[r];
			}
			race.physicsID = charSelectRaces[i].physicsID;
			e.add(race);
			race.dirtyLayers = true;
			e.getComponent(Position.class).pos.set(i*CHAR_SELECT_SPACING + 16 + CHAR_SELECT_SPACING / 2, 2);
			ActionList act = e.getComponent(ActionList.class);
			act.addToStart(AJumpCharSelect.class);
			DragOption drag = engine.createComponent(DragOption.class);
			drag.spacing = CHAR_SELECT_CHARACTERS * CHAR_SELECT_SPACING;
			drag.disabled = true;
			drag.multiDraw = false;
			if (i == 0) {
				drag.selected = true;
				drag.disabled = false;
			}
			e.add(drag);
			//if (worldDef.unlockedStartingCharacters[i])
			
			Inventory inv = engine.createComponent(Inventory.class);
			inv.copyFrom(charSelectInventories[i]);
			e.add(inv);
				//engine.freeEntity(e);
			
			//e.add(new Light());
			
			engine.addEntity(e);
		}
		
		
		
		{
			
			PooledEntity selLight = engine.createEntity();
			Light light = engine.createComponent(Light.class);
			selLight.add(light);
			ActionList act = engine.createComponent(ActionList.class);
			ASelectedLight aSel = new ASelectedLight();
			act.addToStart(aSel);
			selLight.add(act);
			selLight.add(engine.createComponent(Position.class));
			engine.addEntity(selLight);
		}
		

	}

	
	
	@Override
	public void selected(EngineNiz engine, SelectedPlayer sel, Entity e, InventoryScreen invScr) {
		if (sel.def.isRoomEditor){
			Inventory inv = e.getComponent(Inventory.class);
			Values<ItemDef> ie = Inventory.defs.values();
			while (ie.hasNext){
				ItemDef item = ie.next();
				inv.addItem(item, 1000000);
				
			}
			engine.getSystem(OverworldSystem.class).stopNewGameScreen();
			engine.getSystem(OverworldSystem.class).changeToRoomEditor(sel.def);;
		} else {
			engine.getSystem(OverworldSystem.class).stopNewGameScreen();
			engine.getSystem(OverworldSystem.class).changeZLevel(1);;
			
		}
		ActionList act = e.getComponent(ActionList.class);
		ALoopAroundMap loop = Pools.obtain(ALoopAroundMap.class);
		act.addToStart(loop);
	}
	
	public void createMapGenerationAgent(PooledEntity e, EngineNiz engine, Map map, int bit, int z) {
		//Gdx.app.log(TAG,  "create gen agent " + bit);
		Position pos = engine.createComponent(Position.class);
		e.add(pos);
		pos.pos.set(0,0);
		
		//e.add(engine.createComponent(SpriteAnimation.class).set(Animations.PLAYER));
		ActionList act = Pools.obtain(ActionList.class);
		
		AAgentBuildMapRunner abuild = new AAgentBuildMapRunner();
		abuild.bit = bit;
		abuild.map = map;

		if (map == null) throw new GdxRuntimeException("null map");
		//abuild.z = z;
		//abuild.after = createEntityGenerationAgent(map, (int)map.offset.x, (int)map.offset.y, map.width, map.height, def);
		//map.e = e;
		act.addToStart(abuild);
		
		e.add(act);
		abuild.factory = this;
	
	}

	@Override
	public Entity generateMob(int z, PlatformerFactory.MobSpawnType type, EngineNiz engine) {
        return null;
	}




	public Action createEntityGenerationAgent(
			 Map map, int x, int y, int w, int h, WorldDefinition worldDef) {
		AGenerateEntities abuild = new AGenerateEntities();
		abuild.x = x;
		abuild.y = y;
		abuild.w = w;
		abuild.h = h;
		abuild.map = map;
		abuild.def = worldDef;
		return abuild;
	}
	public void createRunningAgent(EngineNiz engine) {
		PooledEntity e = makePlayer(engine);
		ActionList act = e.getComponent(ActionList.class);
		int raceIndex = 0;
		Race race = engine.createComponent(Race.class);
		for (int r = 0; r < race.raceID.length; r++){
			race.raceID[r] = charSelectRaces[raceIndex % charSelectRaces.length].raceID[r];
		}
		race.physicsID = charSelectRaces[raceIndex % charSelectRaces.length].physicsID;
		e.add(race);
		race.dirtyLayers = true;
		e.getComponent(Position.class).pos.set(1.5f, AStar.PATHFINDING_INITIAL_Y_OFFSET+2);
//		APathfindingPreRun preRun = Pools.obtain(APathfindingPreRun.class);
		//preRun.index = i | type;
		//act.addToStart(preRun);
		Inventory inv = engine.createComponent(Inventory.class);
		inv.copyFrom(pathfindingInventories[raceIndex % pathfindingInventories.length]);
		e.add(inv);
		e.add(PathfinderPreLog.class);
		//Light light = engine.createComponent(Light.class);
		//if (MathUtils.random(4) == 0)
			//e.add(light);
		engine.addEntity(e);
		Body body = e.getComponent(Body.class);
		body.height = .01f;
		
		
		
		
		
		AAgentRandomBuildPlatforms builder = Pools.obtain(AAgentRandomBuildPlatforms.class);
		
		act.addToEnd(builder);
		
		
		
		
		
		
		
		engine.addEntity(e);;
		
		
	}

}
