package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap.Values;
import com.niz.Factory;
import com.niz.action.ActionList;
import com.niz.actions.AJumpCharSelect;
import com.niz.actions.APlayer;
import com.niz.actions.path.AWaitForPath;
import com.niz.component.DragOption;
import com.niz.component.Inventory;
import com.niz.component.Light;
import com.niz.component.Player;
import com.niz.component.SelectedPlayer;
import com.niz.item.ItemDef;

public class SelectedPlayerSystem extends EntitySystem {
	ComponentMapper<SelectedPlayer> playerM = ComponentMapper.getFor(SelectedPlayer.class);
	private static ComponentMapper<DragOption> dragM = ComponentMapper.getFor(DragOption.class);

	private ImmutableArray<Entity> entities;
	private WorkerSystem work;
	private EngineNiz engine;
	private ImmutableArray<Entity> dragEntities;
	private Factory factory;
	public SelectedPlayerSystem(Factory factory) {
		this.factory = factory;
	}
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(SelectedPlayer.class).get());
		work = engine.getSystem(WorkerSystem.class);
		super.addedToEngine(engine);
		this.engine = (EngineNiz) engine;
		dragEntities = engine.getEntitiesFor(Family.all(DragOption.class).exclude(Player.class).get());
		
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		if (!work.allPaused) return;
		if (entities.size() > 1) throw new GdxRuntimeException("muiltiple selected entities");
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			e.getComponent(ActionList.class).remove(AJumpCharSelect.class);
			e.getComponent(ActionList.class).addToStart(APlayer.class);
			e.getComponent(ActionList.class).addToStart(AWaitForPath.class);
			
			Player pl = engine.createComponent(Player.class);
			e.add(pl);
			e.add(new Light());
			//Gdx.app.log("sel pl sys", "do" + e.getId());
			for (int j = 0; j < dragEntities.size(); j++){
				Entity re = dragEntities.get(j);
				DragOption drag = dragM.get(re);
				
				engine.removeEntity(re);
			}
			e.remove(DragOption.class);
			//e.remove(Physics.class);
			SelectedPlayer sel = e.getComponent(SelectedPlayer.class);
			e.remove(SelectedPlayer.class);
			factory.selected(engine, sel);
			
			//start loading next level
			
			
			
		}
	}

}
