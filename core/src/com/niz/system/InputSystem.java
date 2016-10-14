package com.niz.system;

import java.util.Iterator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntIntMap;
import com.niz.Input;
import com.niz.component.ButtonInput;
import com.niz.component.Player;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

/**
 * Created by niz on 10/07/2014.
 */
public class InputSystem  extends EntitySystem implements Observer {
    private static final String TAG = "Input System";
    private Subject playerControl;
    private ButtonInput c = new ButtonInput();
    Entity[] players = new Entity[Input.MAX_PLAYERS];
    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     */
    public InputSystem() {
        
    }

   
    ComponentMapper<Player> playerM = ComponentMapper.getFor(Player.class);
    private IntIntMap pressed = new IntIntMap(), currentlyPressed = new IntIntMap();
	private ImmutableArray<Entity> entities;

   

	

	@Override
	public void addedToEngine(Engine engine) {
		playerControl = ((EngineNiz) engine).getSubject("playerControl");
        //playerControl.add(this);
		entities = engine.getEntitiesFor(Family.all(Player.class).get());
		((EngineNiz) engine).getSubject("screen").add(this);;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		if (entities.size() == 0) return;
		for (int n = 0; n < Input.MAX_PLAYERS; n++){
			players[n] = null;
		}
		Iterator<Entity> eiter = entities.iterator();
		while (eiter.hasNext()){
			Entity e = eiter.next();
			Player player = playerM.get(e);
			players[player.index] = e;
		}
		
        IntIntMap tmp = pressed;
        pressed = currentlyPressed;
        currentlyPressed = tmp;
        currentlyPressed.clear();
        for (int n = 0; n < Input.MAX_PLAYERS; n++){
        	
        	Iterator<IntIntMap.Entry> i = Input.keys[n].iterator();
        	while (i.hasNext()){
        		IntIntMap.Entry entry = i.next();
        		
        		if (Gdx.input.isKeyPressed(entry.key)){
        			currentlyPressed.put(entry.key, entry.value);
        			c.code = entry.value;
        			
        			Entity playerEntity = players[n];
        			//Gdx.app.log(TAG, "player pos "+playerEntity.getComponent(Position.class).pos.y);
        			if (playerEntity != null)
        				playerControl.notify(playerEntity, Subject.Event.BUTTON_IS_PRESSED, c);
        		} else {
        			if (pressed.containsKey(entry.key)){//unpressed
        				Entity playerEntity = players[n];
        				c.code = entry.value;
        				if (playerEntity != null)
        					playerControl.notify(playerEntity, Subject.Event.BUTTON_RELEASE, c);
        				
        			}
        		}
        		
        	}
        }
	}

	/**
	 * handling screen touches here and passing them on for the time being
	 */
	
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		
		Entity playerEntity = players[0];
		if (playerEntity != null)
			playerControl.notify(playerEntity, event, c);
	}


}
