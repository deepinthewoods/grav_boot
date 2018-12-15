package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BinaryHeap;
import com.niz.action.Action;
import com.niz.action.ActionList;


public class ActionSystem extends EntitySystem implements EntityListener{
	private static final String TAG = "action system";
	ComponentMapper<ActionList> actionMap = ComponentMapper.getFor(ActionList.class);
    public BinaryHeap<Action> actions = new BinaryHeap<Action>();
	private ImmutableArray<Entity> entities;
	private EngineNiz engine;

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(ActionList.class).get());
		
		engine.addEntityListener(Family.all(ActionList.class).get(), this);
		this.engine = (EngineNiz) engine;
	}
	@Override
	public void removedFromEngine(Engine engine) {
		
	}
	
	@Override
	public void update(float deltaTime) {
		while (actions.size > 0 ){// del.actions.peek().getValue()<del.currentTime){
            Action a = actions.pop();
            if (a.getValue() >= a.parent.currentTime){
                actions.add(a);
                break;
            }
            a.unDelay();
        }
        //Gdx.app.log(TAG, "update");

        for (int i = 0, s = entities.size(); i < s; i++) {
          
            ActionList actionC = actionMap.get(entities.get(i));
    		actionC.update(deltaTime);
        }
	}
	
	@Override
	public void entityAdded(Entity e) {
		ActionList actionC = actionMap.get(e);
		if (actionC == null) return;
        actionC.delayedActions = actions;
        actionC.engine = engine;
        actionC.inserted(e);
		
	}
	
	@Override
	public void entityRemoved(Entity e) {
		ActionList actionC = actionMap.get(e);
		if (actionC == null) return;
        actionC.removed();
        //Gdx.app.log(TAG, "ENTITY REMOVED");
	}
	public void updateRender(float deltaTime) {
		while (actions.size > 0 ){// del.actions.peek().getValue()<del.currentTime){
            Action a = actions.pop();
            if (a.getValue() >= a.parent.currentTime){
                actions.add(a);
                break;
            }
            a.unDelay();
        }
        //Gdx.app.log(TAG, "update render");

        for (int i = 0, s = entities.size(); i < s; i++) {
          
            ActionList actionC = actionMap.get(entities.get(i));
    		actionC.updateRender(deltaTime);
        }
	}
	
	
	

}
