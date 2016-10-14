package com.niz.observer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class Subject {
    private static final String TAG = "subject";
    private Array<Observer> observers = new Array<Observer>();
	public enum Event {TEST, POSITION_CHANGE, BUTTON_IS_PRESSED, SLIDER_PRESSED, 
		ROTATION_CHANGE, BUTTON_RELEASE, ACCUMULATE_INFLUENCE, NEED_CHUNK, 
		MAP_COLLISION, CHANGE_SMALL_BUCKET, CHANGE_LARGE_BUCKET, RESIZE, 
		BELT_REFRESH, PINCH, CANCEL_TOUCH, INVENTORY_REFRESH, INVENTORY_TOGGLE, 
		EQUIP_ITEM, CHANGE_DOING_SLOT, STOP_TOAST, BELT_TOUCH_START, BELT_REMOVE_DUPES,
		PAPERDOLL, WORLD_DEFINITION_SET, ROOM_DEFINITION_CHANGE, ROOM_BORDER_CHANGE, ROOM_PATCH_CHANGE, DRAG_BLOCK, DRAG_SCREEN, OK_BUTTON, OK_BUTTON_ENABLE, OK_BUTTON_DISABLE, SAVE_ROOM, ON_DOOR

		};
	public void add(Observer obs){
		observers.add(obs);
	}
	public void remove(Observer obs){
		observers.removeValue(obs, true);
	}
	public void notify(Entity e, Event event, Object c){
		for (int i = 0; i < observers.size; i++){
			Observer obs = observers.get(i);
			obs.onNotify(e, event, c);

		}
		
	}
	
	
}

