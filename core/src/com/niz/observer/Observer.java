package com.niz.observer;

import com.badlogic.ashley.core.Entity;
import com.niz.observer.Subject.Event;

public interface Observer {


	void onNotify(Entity e, Event event, Object c);

}
