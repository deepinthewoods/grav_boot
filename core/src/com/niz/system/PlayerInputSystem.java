package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.niz.Input;
import com.niz.ZoomInput;
import com.niz.component.ButtonInput;
import com.niz.component.Control;
import com.niz.component.Player;
import com.niz.component.VectorInput2;
import com.niz.component.VectorInput4;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;
import com.niz.ui.edgeUI.InventoryScreen;

public class PlayerInputSystem extends RenderSystem implements Observer {
	private static final String TAG = "player input ys";
	ComponentMapper<Control> conC = ComponentMapper.getFor(Control.class);
    transient VectorInput2 vec2 = new VectorInput2();
	private ImmutableArray<Entity> entities;
	private ShapeRenderingSystem shape;
	private CameraSystem cameraSys;
	private InventoryScreen invScreen;
	private ZoomInput zoomInput = new ZoomInput();
	private Subject zoomSubject;

	public PlayerInputSystem(EngineNiz engine, InventoryScreen invScreen){
		engine.getSubject("playerControl").add(this);
		this.invScreen = invScreen;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Control.class, Player.class).get());
		shape = engine.getSystem(ShapeRenderingSystem.class);
		cameraSys = engine.getSystem(CameraSystem.class);
		EngineNiz engineNiz = (EngineNiz) engine;

		zoomSubject = engineNiz.getSubject("zoominput");

	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}
	Vector2 v = new Vector2();
	@Override
	public void update(float deltaTime) {
		if (entities.size() == 0) return;
		Entity e = entities.get(0);
		Control con = conC.get(e);
		
		if (con.pressed[Input.SCREEN_TOUCH]){
			
			invScreen.setDrawArrow(e, con.rotation);
			
			
			
		}
		super.update(deltaTime);
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		
		if (event == Event.SLIDER_PRESSED){
			
			return;
		}
		
		
		
		Control con = conC.get(e);
		if (con == null) return;
		if (event == Event.BUTTON_IS_PRESSED){
			ButtonInput b = (ButtonInput) c;
			con.pressed[b.code] = true;
			//Gdx.app.log("", "pressed"+b.code);

		}
		else if (event == Event.BUTTON_RELEASE){
			ButtonInput b = (ButtonInput) c;
			con.pressed[b.code] = false;
			//Gdx.app.log("", "relesed"+b.code);

		}
		
		if (event == Event.ROTATION_CHANGE){
		    VectorInput2 vec = (VectorInput2) c;
		    tmpV2.set(vec.v).add(-Gdx.graphics.getWidth()/2, -Gdx.graphics.getHeight()/2);
			con.rotation.set(tmpV2).nor();
			//Gdx.app.log("", "notified"+con.rotation);
		}
		
		if (event == Event.PINCH){
			VectorInput4 vec = (VectorInput4) c;
			float d = vec.v3.dst(vec.v4);
		    if (vec.v.equals(oldInitial1) && vec.v2.equals(oldInitial2)) {
            } else {
		        zoomDist = d;

			}

			zoomInput.zoom = zoomDist/d;
			zoomDist = d;


			zoomSubject.notify(null, null, zoomInput);
			Gdx.app.log(TAG, "NOTRIFY ZOOM " + zoomInput.zoom);
            oldInitial1.set(vec.v);
            oldInitial2.set(vec.v2);
		}
	}
	float zoomDist = 1f;
	Vector3 tmpV = new Vector3();
	Vector2 tmpV2 = new Vector2(), oldInitial1 = new Vector2(), oldInitial2 = new Vector2();

}
