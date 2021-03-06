package com.niz;



import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.action.ActionList;
import com.niz.actions.ACameraFollowPlayer;
import com.niz.actions.AEnemy;
import com.niz.actions.AHold;
import com.niz.actions.AJumpCharSelect;
import com.niz.actions.ANotRun;
import com.niz.actions.APathfindingJumpAndHold;
import com.niz.actions.APathfindingPreRun;
import com.niz.actions.ATailControl;
import com.niz.actions.AUseInventory;
import com.niz.actions.mapgen.AAgentBuildMap;
import com.niz.actions.mapgen.AGenerateEntities;
import com.niz.actions.path.AFollowPath;
import com.niz.actions.path.AWaitForPath;
import com.niz.anim.Animations;
import com.niz.component.BitmaskedCollisions;
import com.niz.component.Body;
import com.niz.component.Buckets;
import com.niz.component.CameraControl;
import com.niz.component.CollidesWithMap;
import com.niz.component.Control;
import com.niz.component.DragOption;
import com.niz.component.Health;
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
import com.niz.observer.Subject;
import com.niz.system.InventorySystem;
import com.niz.system.OverworldSystem;
import com.niz.system.WorkerSystem;
import com.niz.ui.edgeUI.InventoryScreen;

public class PlatformerFactory extends Factory {

	private static final String TAG = "platformer factory";
	public static final int CHAR_SELECT_SPACING = 4;
	
	public Race[] charSelectRaces, pathfindingRaces;
	public Inventory[] charSelectInventories, pathfindingInventories;
	//private Map map;
	public static final int CHAR_SELECT_CHARACTERS = 6
			, PATHFINDING_COUNT = 32;
	private PooledEntity[] levelSelectionEntities;

	public PlatformerFactory(){
		charSelectRaces = new Race[CHAR_SELECT_CHARACTERS];
		for (int i = 0; i < charSelectRaces.length; i++){
			charSelectRaces[i] = new Race();
		}
/*		charSelectRaces[2].raceID[Race.BACK_ARM] = Race.RED_DRAGON;
		charSelectRaces[2].raceID[Race.FRONT_ARM] = Race.RED_DRAGON;
		
		charSelectRaces[1].raceID[Race.FRONT_ARM] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.BACK_ARM] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.BACK_LEG] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.FRONT_LEG] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.TORSO] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.NECK] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.HEAD] = Race.RED_DRAGON;
		charSelectRaces[1].raceID[Race.TAIL] = Race.RED_DRAGON;*/
		int n = 3;
		int race = Race.RAPTOR;

		charSelectRaces[n].raceID[Race.FRONT_ARM] = race;
		charSelectRaces[n].raceID[Race.BACK_ARM] = race;
		charSelectRaces[n].raceID[Race.BACK_LEG] = race;
		charSelectRaces[n].raceID[Race.FRONT_LEG] = race;
		charSelectRaces[n].raceID[Race.NECK] = race;
		charSelectRaces[n].raceID[Race.HEAD] = race;
		charSelectRaces[n].raceID[Race.TORSO] = race;
		charSelectRaces[n].raceID[Race.TAIL] = race;//*/

		race = Race.WOLF;
		n = 2;
		charSelectRaces[n].raceID[Race.FRONT_ARM] = race;
		charSelectRaces[n].raceID[Race.BACK_ARM] = race;
		charSelectRaces[n].raceID[Race.BACK_LEG] = race;
		charSelectRaces[n].raceID[Race.FRONT_LEG] = race;
		charSelectRaces[n].raceID[Race.NECK] = race;
		charSelectRaces[n].raceID[Race.HEAD] = race;
		charSelectRaces[n].raceID[Race.TORSO] = race;
		charSelectRaces[n].raceID[Race.TAIL] = race;//*/

		race = Race.RED_DRAGON;
		n = 1;
		charSelectRaces[n].raceID[Race.FRONT_ARM] = race;
		charSelectRaces[n].raceID[Race.BACK_ARM] = race;
		charSelectRaces[n].raceID[Race.BACK_LEG] = race;
		charSelectRaces[n].raceID[Race.FRONT_LEG] = race;
		charSelectRaces[n].raceID[Race.NECK] = race;
		charSelectRaces[n].raceID[Race.HEAD] = race;
		charSelectRaces[n].raceID[Race.TORSO] = race;
		charSelectRaces[n].raceID[Race.TAIL] = race;//*/



		charSelectInventories = new Inventory[CHAR_SELECT_CHARACTERS];
		for (int i = 0; i < charSelectInventories.length; i++){
			Inventory inv = new Inventory();
			charSelectInventories[i] = inv;
			//inv.addItem(16, 6);
			
			inv.addItem(Inventory.defs.get(14), 232);
			inv.addItem(Inventory.defs.get(15), 232);
			inv.addItem(Inventory.defs.get(16), 232);
			inv.addItem(Inventory.defs.get(17), 23);
			inv.addItem(Inventory.defs.get(18), 23);
			inv.addItem(Inventory.defs.get(19), 23);
			inv.addItem(Inventory.defs.get(20), 32);
			inv.addItem(Inventory.defs.get(24), 23);
			inv.addItem(Inventory.defs.get(22), 23);
			
			inv.addItem(Inventory.defs.get(32), 100);
			inv.addItem(Inventory.defs.get(33), 192);
			inv.addItem(Inventory.defs.get(34), 197);
		}
		
		//Inventory inv = charSelectInventories[0];
		pathfindingRaces = new Race[PATHFINDING_COUNT];
		pathfindingInventories = new Inventory[PATHFINDING_COUNT];
		for (int i = 0; i < pathfindingInventories.length; i++){
			Inventory inv = new Inventory();
			pathfindingInventories[i] = inv;
			inv.addItem(16, 6);
		}
		for (int i = 0; i < pathfindingRaces.length; i++){
			pathfindingRaces[i] = new Race();
		}
		//pathfindingRaces[2].raceID[Race.BACK_ARM] = Race.RED_DRAGON;
		//pathfindingRaces[2].raceID[Race.FRONT_ARM] = Race.RED_DRAGON;
		
	}


	public PooledEntity makePlayer(EngineNiz engine) {
		PooledEntity e = engine.createEntity();
		
		Position pos = engine.createComponent(Position.class);
		e.add(pos);
		
		Physics phys = engine.createComponent(Physics.class);
		phys.bodyTypeID = 1;
		e.add(phys);
		SpriteAnimation sprite = engine.createComponent(SpriteAnimation.class);
		//SpriteAnimation sprite = new SpriteAnimation();
		e.add(sprite.set(Animations.PLAYER));
		ActionList act = Pools.obtain(ActionList.class);
		//act.addToStart(new AAutoBuild());
		act.addToStart(new AWaitForPath());;
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

		Health health = new Health();

		e.add(health);
		
		e.add(new Inventory());
		OnMap onMap = new OnMap();
		onMap.map = null;
		e.add(onMap);
		
		//e.getComponent(Inventory.class).addItem(Inventory.defs.get(3), 1);;
		
		
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

		levelSelectionEntities = new PooledEntity[CHAR_SELECT_CHARACTERS];
		for (int i = 0; i < CHAR_SELECT_CHARACTERS; i++){
			PooledEntity e = makePlayer(engine);
			levelSelectionEntities[i] = e;
			Race race = engine.createComponent(Race.class);
			for (int r = 0; r < race.raceID.length; r++){
				race.raceID[r] = charSelectRaces[i].raceID[r];
			}

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
			
			Inventory inv = engine.createComponent(Inventory.class);
			inv.copyFrom(charSelectInventories[i]);
			e.add(inv);
			e.add(race);
			//inv.equipAll(e);
			if (i == 0) engine.getSubject("playerselect").notify(e, Subject.Event.CHANGE_SELECTED_CHARACTER, null);

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
		

		for (int i = 0; i < 4; i++){
			makePathfinder(engine, i, APathfindingJumpAndHold.NORMAL_JUMP);
			//makePathfinder(engine, i, APathfindingJumpAndHold.STANDING_JUMP);
			
		}
		for (int i = 0; i < 4; i++){
			//makePathfinder(engine, i, APathfindingJumpAndHold.STANDING_DELAYED_RUN_JUMP);
			//makePathfinder(engine, i, APathfindingJumpAndHold.DELAYED_REVERSE_JUMP);
			//makePathfinder(engine, i, APathfindingJumpAndHold.WALLJUMP);
			makePathfinder(engine, i, APathfindingJumpAndHold.WALLJUMP_FORWARD);
		}
		//*/

		//postLevelSelection(engine, def);
	}
	private void makePathfinder(EngineNiz engine, int i, int type) {
		PooledEntity e = makePlayer(engine);
		Race race = engine.createComponent(Race.class);
		for (int r = 0; r < race.raceID.length; r++){
			race.raceID[r] = pathfindingRaces[i % pathfindingRaces.length].raceID[r];
		}
		e.add(race);
		race.dirtyLayers = true;
		e.getComponent(Position.class).pos.set(1.5f, AStar.PATHFINDING_INITIAL_Y_OFFSET+4);
		ActionList act = e.getComponent(ActionList.class);
		
		APathfindingPreRun preRun = Pools.obtain(APathfindingPreRun.class);
		preRun.index = i | type;
		act.addToStart(preRun);
		Inventory inv = engine.createComponent(Inventory.class);
		inv.copyFrom(pathfindingInventories[i % pathfindingInventories.length]);
		e.add(inv);
		e.add(PathfinderPreLog.class);
		Light light = engine.createComponent(Light.class);
		//if (MathUtils.random(4) == 0)
			e.add(light);
		engine.addEntity(e);
		Body body = e.getComponent(Body.class);
		body.height = .01f;
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
			engine.getSystem(OverworldSystem.class).changeZLevel(0);
			engine.getSystem(InventorySystem.class).equipStartItems(e, invScr);
		}
	}
	
	public void createMapGenerationAgent(PooledEntity e, EngineNiz engine, Map map, int bit, int z) {
		//Gdx.app.log(TAG,  "create gen agent " + bit);
		Position pos = engine.createComponent(Position.class);
		e.add(pos);
		pos.pos.set(0,0);
		//e.add(engine.createComponent(SpriteAnimation.class).set(Animations.PLAYER));
		ActionList act = Pools.obtain(ActionList.class);
		AAgentBuildMap abuild = new AAgentBuildMap();
		abuild.bit = bit;
		abuild.map = map;
		abuild.z = z;
		abuild.after = createEntityGenerationAgent(map, def, z);
		//map.e = e;
		act.addToStart(abuild);
		e.add(act);
	}

	@Override
	public Entity generateMob(int z, MobSpawnType type, EngineNiz engine) {
		Entity e = makePlayer(engine);
		ActionList action = e.getComponent(ActionList.class);
		action.addToStart(AFollowPath.class);
		action.addToStart(AEnemy.class);
		Race race = engine.createComponent(Race.class);
		//Race race = new Race();
		for (int r = 0; r < race.raceID.length; r++){
			race.raceID[r] = Race.HUMAN;
		}
		e.add(race);

		return e;
	}



	public Action createEntityGenerationAgent(
			Map map, WorldDefinition worldDef, int z) {
		AGenerateEntities abuild = new AGenerateEntities();
		abuild.z = z;
		abuild.map = map;
		abuild.def = worldDef;
		return abuild;
	}

	public enum
	MobSpawnType {SMALL, MEDIUM, MINOR_BOSS, BOSS, SIDE_BOSS}
}
