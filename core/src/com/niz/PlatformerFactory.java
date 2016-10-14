package com.niz;



import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.ActionList;
import com.niz.actions.ACameraFollowPlayer;
import com.niz.actions.AHold;
import com.niz.actions.AJumpCharSelect;
import com.niz.actions.ANotRun;
import com.niz.actions.ATailControl;
import com.niz.actions.AUseInventory;
import com.niz.actions.mapgen.AMapPlanner;
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
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;
import com.niz.component.TransientComponent;
import com.niz.item.ItemDef;
import com.niz.observer.Subject.Event;
import com.niz.system.OverworldSystem;
import com.niz.system.WorkerSystem;

public class PlatformerFactory extends Factory {

	private static final String TAG = "platformer factory";
	public static final int CHAR_SELECT_SPACING = 4;
	
	public Race[] charSelectRaces;
	public Inventory[] charSelectInventories;
	//private Map map;
	public static final int CHAR_SELECT_CHARACTERS = 8;
	public PlatformerFactory(){
		charSelectRaces = new Race[CHAR_SELECT_CHARACTERS];
		for (int i = 0; i < charSelectRaces.length; i++){
			charSelectRaces[i] = new Race();
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
			
//			inv.addItem(Inventory.defs.get(14), 232);
//			inv.addItem(Inventory.defs.get(15), 232);
//			inv.addItem(Inventory.defs.get(16), 232);
//			inv.addItem(Inventory.defs.get(17), 23);
//			inv.addItem(Inventory.defs.get(18), 23);
//			inv.addItem(Inventory.defs.get(19), 23);
//			inv.addItem(Inventory.defs.get(20), 32);
//			inv.addItem(Inventory.defs.get(25), 23);
//			inv.addItem(Inventory.defs.get(22), 23);
//			
//			inv.addItem(Inventory.defs.get(32), 100);
//			inv.addItem(Inventory.defs.get(33), 192);
//			inv.addItem(Inventory.defs.get(34), 197);
		}
		
		//Inventory inv = charSelectInventories[0];
		
	}
	@Override
	public void createPlayer(EngineNiz engine, Array<PooledEntity> arr, WorldDefinition def) {
		//Gdx.app.log(TAG, "create player");
		arr.clear();
		
		
		
		//agent
		//PooledEntity e = engine.createEntity();
		//ActionList act = engine.createComponent(ActionList.class);
		//AMapPlanner mapPlanner = Pools.obtain(AMapPlanner.class);
		//mapPlanner.factory = this;
		//act.addToStart(mapPlanner);
		
		//arr.add(e);
		
		
		
		
		
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
		//act.addToStart(new AAutoBuild());
		//act.addToStart(new ARandomRun());;
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
	public void startMap(EngineNiz engine) {
		
		
		
		
		//if(true) return;

		//Entity e = engine.createEntity();
		//createStandardAgent(e, engine);
		//engine.addEntity(e);
		
		//engine.getSystem(MapSystem.class).worldDef = def;
	}

	
	
	@Override
	public void startPlayer(EngineNiz engine, Entity e) {

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
		
		
		
		
		PooledEntity selLight = engine.createEntity();
		Light light = engine.createComponent(Light.class);
		selLight.add(light);
		ActionList act = engine.createComponent(ActionList.class);
		ASelectedLight aSel = new ASelectedLight();
		act.addToStart(aSel);
		selLight.add(act);
		selLight.add(engine.createComponent(Position.class));
		engine.addEntity(selLight);
		
		
		//PooledEntity pl = engine.createEntity();
		//pl.add(new Player());
		//engine.addEntity(pl);;
		
	}

}
