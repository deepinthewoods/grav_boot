package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.BeltButton;
import com.badlogic.gdx.utils.IntMap;
import com.niz.component.Inventory;
import com.niz.component.Player;
import com.niz.item.Item;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;
import com.niz.ui.edgeUI.InventoryScreen;

public class InventorySystem extends EntitySystem implements Observer {
	private static final String TAG = "inv sys ";
	ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private ImmutableArray<Entity> entities;
	private Subject subject;
	private ImmutableArray<Entity> playerEntities;
	private Subject invNotifier;
    private Subject beltRefreshSubject;

    @Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Inventory.class).get());
		playerEntities = engine.getEntitiesFor(Family.all(Inventory.class, Player.class).get());
		
		super.addedToEngine(engine);
		subject = ((EngineNiz) engine).getSubject("refreshpaperdoll");
		subject.add(this);
		invNotifier = ((EngineNiz) engine).getSubject("inventoryRefresh");
        beltRefreshSubject = ((EngineNiz) engine).getSubject("inventoryRefresh");

    }

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < playerEntities.size(); i++){
			Entity e = playerEntities.get(i);
			Inventory inv = invM.get(e);
			if (inv.dirty){
				
				invNotifier.notify(e, Event.BELT_REFRESH, inv);
            }
		}
		
		
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			Inventory inv = invM.get(e);
			if (inv.dirty){
				//Gdx.app.log(TAG, "inv "+e.getId() + "  active ");
				
				inv.dirty = false;
				inv.update();
			}
		}
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		//TODO override layers for items
		Inventory inv = invM.get(e);
		//inv.
		
	}

	public void equipStartItems(Entity e, InventoryScreen invScr) {
		Inventory inv = e.getComponent(Inventory.class);
		IntMap.Values<Item> iter = inv.items.values();
		int slot = 0;
		while (iter.hasNext() && slot < InventoryScreen.BELT_SLOTS -1){
			Item item = iter.next();
			Gdx.app.log(TAG, "button" + invScr.belt);
            BeltButton butt =  invScr.belt.buttons[slot++];
            //butt.setChecked(true);
            //int index = beltIndexOF(act2.item.hash, act2.index);

            beltRefreshSubject.notify(e, Event.BELT_REMOVE_DUPES, item);
            butt.setFrom(item, e);

            beltRefreshSubject.notify(e, Event.BELT_REFRESH, inv);
            butt.setChecked(true);
            //butt.setShaking();
           // act.setShaking();

            inv.setActiveItem(butt.hash, butt.doingSlot);
            inv.dirtyLimbs = true;
            invNotifier.notify(butt.e, Event.EQUIP_ITEM, null);
		}
		invNotifier.notify(e, Event.INVENTORY_REFRESH, e.getComponent(Inventory.class));
		//inv.equipAll(e);

	}
}
