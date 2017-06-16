package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.BeltButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.niz.Data;
import com.niz.WorldDefinition;
import com.niz.action.ActionList;
import com.niz.actions.mapgen.ALoadEntities;
import com.niz.actions.mapgen.ASaveEntities;
import com.niz.component.Inventory;
import com.niz.component.Player;
import com.niz.component.SpriteAnimation;
import com.niz.item.Item;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;
import com.niz.ui.elements.BeltTable;

public class EntitySerializationSystem extends EntitySystem {
	private static final String TAG = "e serialization system";

	ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	ComponentMapper<ActionList> actM = ComponentMapper.getFor(ActionList.class);
	ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);

	private EngineNiz engine;
	private ImmutableArray<Entity> entities;
	public WorldDefinition worldDef;

	private Subject beltRefreshSubject;

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (EngineNiz) engine;
		Family family = Family.one(Player.class).get();
		entities = engine.getEntitiesFor(family);
		this.engine.getSubject("savegame");
		beltRefreshSubject = ((EngineNiz) engine).getSubject("inventoryRefresh");

	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		
		super.update(deltaTime);
	}
	//private Output output = new Output(ASaveEntities.BUFFER_SIZE);
	private Input input = new Input(ASaveEntities.BUFFER_SIZE);
	public void saveAllNow(BeltTable belt) {
		if (worldDef == null) return;
		Output output = new Output(ASaveEntities.BUFFER_SIZE);
		FileHandle file = worldDef.folder.child("player.e");
		Kryo kryo = Data.kryoPool.borrow();
		output.setOutputStream(file.write(false));
		//Gdx.app.log(TAG, "write Player"+entities.size());

		output.writeInt(entities.size());
		//Gdx.app.log(TAG, "writing player entities");
		for (int i = 0; i < entities.size(); i++){
			PooledEntity e = (PooledEntity) entities.get(i);
			ASaveEntities.writeEntity(e, output, kryo);
		}
		output.writeInt(belt.buttons.length);
		for (int i = 0; i < belt.buttons.length; i++){
			BeltButton butt = belt.buttons[i];
			output.writeInt(butt.hash);
			output.writeInt(butt.doingSlot);
		}
		
		output.close();
		
		
		Data.kryoPool.release(kryo);
	}
	
	public void loadGame(BeltTable beltTable, Array<PooledEntity> playerArr){
		playerArr.clear();
		FileHandle file = worldDef.folder.child("player.e");
		Kryo kryo = Data.kryoPool.borrow();
		input.setInputStream(file.read());
		int size = input.readInt();
		Gdx.app.log(TAG, "Read Player"+size);
		PooledEntity e = null;
		for (int i = 0; i < size; i++){
			e = ALoadEntities.readEntity(input, kryo, engine);
			playerArr.add(e);
			//actM.get(e).addToStart(Pools.obtain(A));
			//engine.addEntityNoID(e);
			
		}
		
		int beltSize = input.readInt();
		
		Inventory inv = invM.get(e);
		inv.dirtyLimbs = true;
		for (int i = 0; i < beltSize; i++){
			int hash = input.readInt();
			int doing = input.readInt();
			if (inv == null) throw new GdxRuntimeException("null inventory");
			Item item = inv.getItem(hash);
			//if (!inv.items.containsKey(hash)) throw new GdxRuntimeException("j"+hash);
			beltTable.buttons[i].setFrom(item, e);
			beltRefreshSubject.notify(e, Event.BELT_REFRESH, inv);
			beltRefreshSubject.notify(e, Event.INVENTORY_REFRESH, inv);
			//engine.getSubject("equipitem").notify(e, Event.EQUIP_ITEM, inv);
		}
		//for (int i = 0; i < entities.size(); i++){
		//}
		
		input.close();
		
		Data.kryoPool.release(kryo);
		
		//animM.get(e).updateGuides(0, animM.get(e).left);
		//animM.get(e).resume();
		
	}

}