package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.niz.action.Action;
import com.niz.component.OnDoor;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class APlayer extends Action {

	private Subject subject;
	static ComponentMapper<OnDoor>onDoorM = ComponentMapper.getFor(OnDoor.class);

	@Override
	public void update(float dt) {
		
	}
	@Override
	public void updateRender(float dt) {
		OnDoor door = onDoorM.get(parent.e);
		if (door != null && door.doors.size > 1){
			subject.notify(parent.e, Event.ON_DOOR, door);
		}
	}
	@Override
	public void onEnd() {

	}

	@Override
	public void onStart() {
		subject = parent.engine.getSubject("screen");
	}

}
