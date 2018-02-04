package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.niz.Main;
import com.niz.ZoomInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class ZoomSystem extends EntitySystem implements Observer {

	private static final float ZOOM_DETENT_MAX = 1.3f;
	private Subject zoomNotifier;

	@Override
	public void addedToEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.addedToEngine(engine);
		((EngineNiz) engine).getSubject("zoominput").add(this);
        zoomNotifier = ((EngineNiz) engine).getSubject("zoom");
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		//if (zoom < ParallaxBackgroundSystem.ZOOM_OUT_MAX)
		float avAmount = 5f;
		if (zoom > .999f && zoom < ZOOM_DETENT_MAX){
			zoomAv = zoomAv * (avAmount - 1f) + Math.min(ParallaxBackgroundSystem.ZOOM_OUT_MAX, 1f);

		}	else 
		{
			zoomAv = zoomAv * (avAmount - 1f) + Math.min(ParallaxBackgroundSystem.ZOOM_OUT_MAX, zoom);

		}
		zoomAv /= avAmount;

		//Gdx.app.log("zoom", ""+zoomAv + "  / " + ParallaxBackgroundSystem.ZOOM_OUT_MAX + "  ( " + zoom);
		data.zoom = zoomAv;
		zoomNotifier.notify(null, null, data);
	}
	ZoomInput data = new ZoomInput();
	private float zoom = 1f, zoomAv;
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		ZoomInput z = (ZoomInput) c;
		zoom *= z.zoom;
		//zoom = Math.min(ParallaxBackgroundSystem.ZOOM_OUT_MAX, zoom);
		if (zoom > ParallaxBackgroundSystem.ZOOM_OUT_MAX){
			zoom = ParallaxBackgroundSystem.ZOOM_OUT_MAX;
		}
			
		
	}
	
	

}
