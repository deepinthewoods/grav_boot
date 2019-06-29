package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.niz.Main;
import com.niz.PlatformerFactory;
import com.niz.component.DragController;
import com.niz.component.DragOption;
import com.niz.component.Position;
import com.niz.component.VectorInput2;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class DragControllerSystem extends EntitySystem implements Observer {
	private static final String TAG = "drag controller sys";
	private static float zoomOutTarget = 1f, zoomInTarget = Main.PPM;
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<DragController> dragM = ComponentMapper.getFor(DragController.class);
	private static ComponentMapper<DragOption> dragoM = ComponentMapper.getFor(DragOption.class);

	private ImmutableArray<Entity> entities;
	private EngineNiz engine;
	long dragID;
	Vector2 delta = new Vector2();
	private ImmutableArray<Entity> selectableEntities;
	private boolean moving;
	private Vector2 moveTarget = new Vector2();
	private float zoomTarget = zoomOutTarget;
	private boolean dragging;
	private Subject changeNotifier;

	@Override
	public void addedToEngine(Engine engine) {
		selectableEntities = engine.getEntitiesFor(Family.all(Position.class, DragOption.class).get());
		entities = engine.getEntitiesFor(Family.all(Position.class, DragController.class).get());
		EngineNiz en = (EngineNiz) engine;
		en.getSubject("screen").add(this);
		changeNotifier = en.getSubject("playerselect");
		this.engine = en;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		
		if (entities.size() == 0) return;
		Entity e = entities.get(0);
		dragID = e.getId();
		Position pos = posM.get(e);
		DragController drag = dragM.get(e);
		pos.pos.x -= delta.x * drag.scale * CameraSystem.onePixel;
		delta.set(0, 0);
		float dx = pos.pos.x - moveTarget.x;
		if (Math.abs(dx) < .05f){
			moving = false;
		}
		if (dx < 0) dx = 1; else dx = -1;
		float speed = .08f;
		if (moving){
			pos.pos.x += dx * speed ;
			//Gdx.app.log("t", "moving " + moving + moveTarget.x + " dx " + dx + "  pos " + pos.pos);
		}
		//pos.pos.x = Math.max(Math.min(pos.pos.x, drag.max), drag.min);
		float w = PlatformerFactory.CHAR_SELECT_SPACING * PlatformerFactory.CHAR_SELECT_CHARACTERS;
		if (pos.pos.x < 16) pos.pos.x += w;
		if (pos.pos.x >= w + 16) pos.pos.x -= w;
		OrthographicCamera camera = engine.getSystem(CameraSystem.class).camera;
		zoomOutTarget = Gdx.graphics.getWidth() / Main.PPM;
		zoomOutTarget = 1f/zoomOutTarget;
		zoomOutTarget *= PlatformerFactory.CHAR_SELECT_SPACING * (PlatformerFactory.CHAR_SELECT_CHARACTERS-1);
		zoomOutTarget = Math.min(1f,  zoomOutTarget);
		zoomInTarget = Gdx.graphics.getWidth() / Main.PPM ;
		zoomInTarget = 1f/zoomInTarget;
		zoomInTarget *= 16;//PlatformerFactory.CHAR_SELECT_SPACING * 1.5f;
		
				
		if (dragging) zoomTarget = zoomOutTarget;
		else zoomTarget = zoomInTarget;
		
		float dy = pos.pos.y - zoomTarget;
		if (Math.abs(dy) < .05f) dy = 0;
		else if (dy < 0) dy = 1f;
		else dy = -1f;
		float scale = .01f;
		pos.pos.y += dy * scale;
		
		//pos.pos.set(17, 0);
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		if (event == Event.BUTTON_IS_PRESSED){
			for (int i = 0; i < selectableEntities.size(); i++){
				Entity sel = selectableEntities.get(i);
				DragOption drag = dragoM.get(sel);
				drag.disabled = false;
				drag.multiDraw = true;
				//Gdx.app.log("t", "sakdl"+event);
				
			}
			
			if (entities.size() == 0) return;
			Entity dragger = entities.get(0);
			Vector2 dragPos = posM.get(dragger).pos;
			
			Entity closest = null;
			float dist = 1000000;
			for (int i = 0; i < selectableEntities.size(); i++){
				Entity sel = selectableEntities.get(i);
				Position poss = posM.get(sel);
				//Gdx.app.log("t", "try" + sel.getId());
				
				float d = Math.abs(poss.pos.x - dragPos.x); 
				if (d < dist){
					dist = d;
					closest = sel;
					//Gdx.app.log("t", "close"+sel.getId()); 
				}
				
			}
			
			if (closest != null){
				
				for (int i = 0; i < selectableEntities.size(); i++){
					Entity sel = selectableEntities.get(i);
					DragOption drag = dragoM.get(sel);
					drag.selected = false;
					//drag.multiDraw = false;
				}
				dragoM.get(closest).selected = true;
				changeNotifier.notify(closest, Event.CHANGE_SELECTED_CHARACTER, null);
				//Gdx.app.log(TAG,  "selected" + closest.getId());
			}
		}
		if (event == Event.ROTATION_CHANGE){
			VectorInput2 v = (VectorInput2) c;//.v is x,y, .v2 is deltas
			delta.add(v.v2.x, 0);
			moving = false;
			dragging = true;
		}
		if (event == Event.CANCEL_TOUCH){
			dragging = false;
			if (entities.size() == 0) return;
			Entity dragger = entities.get(0);
			Vector2 dragPos = posM.get(dragger).pos;
			
			Entity closest = null;
			float dist = 1000000;
			for (int i = 0; i < selectableEntities.size(); i++){
				Entity sel = selectableEntities.get(i);
				Position poss = posM.get(sel);
				//Gdx.app.log("t", "try" + poss.pos.x + "  " + dragPos.x + " = " +Math.abs(poss.pos.x - dragPos.x));
				
				float d = Math.abs(poss.pos.x - dragPos.x); 
				if (d < dist){
					dist = d;
					closest = sel;
					//Gdx.app.log("t", "close"+i); 
				}
				
			}
			
			if (closest != null){
				moving = true;
				moveTarget.set(posM.get(closest).pos);
				for (int i = 0; i < selectableEntities.size(); i++){
					Entity sel = selectableEntities.get(i);
					DragOption drag = dragoM.get(sel);
					drag.disabled = true;
					drag.multiDraw = false;
				}
				dragoM.get(closest).disabled = false;
				
			}
		}
	}

	public Entity getSelectedPlayerEntity() {
		for (int i = 0; i < selectableEntities.size(); i++){
			Entity sel = selectableEntities.get(i);
			DragOption drag = dragoM.get(sel);
			if (drag.selected)return sel; 
		}
		return null;
	}

}
