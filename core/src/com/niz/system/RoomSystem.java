package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.niz.component.DragBlock;
import com.niz.component.Grid2dInput;
import com.niz.component.Position;
import com.niz.component.RoomDefinition;
import com.niz.component.VectorInput2;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class RoomSystem extends RenderSystem implements Observer {

	private static final String TAG = "Room System";
	private ImmutableArray<Entity> entities;
	private Subject notifier;
	private EngineNiz engine;
	private Subject invNotifier;
	private ImmutableArray<Entity> dragBlockEntities;

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(RoomDefinition.class).get());
		dragBlockEntities = engine.getEntitiesFor(Family.all(DragBlock.class, Position.class).get());

		this.engine = ((EngineNiz)engine);
		notifier = this.engine.getSubject("roomeditor");
		notifier.add(this);;
		invNotifier = this.engine.getSubject("screen");;
		invNotifier.add(this);;
		//Gdx.app.log(TAG, "djfkljafskljkld);sfja;dksjf;sdlkajfd;lsj;sdakfjsdl;kjsd;ljsfdlk");
		
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
		this.engine = (EngineNiz) engine;
		//notifier = this.engine.getSubject("inventoryRefresh");
	}

	VectorInput2 c = new VectorInput2();
	@Override
	public void update(float deltaTime) {
		if (entities.size() == 0) return;
		
		for (Entity e : entities){
			notifier.notify(e, Event.ROOM_DEFINITION_CHANGE, e.getComponent(RoomDefinition.class));			
		}

		RoomDefinition def = entities.get(0).getComponent(RoomDefinition.class);
		//Gdx.app.log(TAG, "change gridpt active"+dragBlockEntities.size());

		for (Entity be : dragBlockEntities){
			Vector2 p = be.getComponent(Position.class).pos;
			DragBlock drag = be.getComponent(DragBlock.class);
			//Gdx.app.log(TAG, "change gridpt active"+drag.type);
			switch (drag.type){
			case DragBlock.MIN:def.min.set((int)p.x, (int)p.y);
			break;case DragBlock.MAX:def.max.set((int)p.x, (int)p.y);
			break;case DragBlock.MIN9:def.min9.set((int)p.x, (int)p.y);
			break;case DragBlock.MAX9:def.max9.set((int)p.x, (int)p.y);
			}
			if (def.min.x > def.max.x){
				int t = def.min.x;
				def.min.x = def.max.x;
				def.max.x = t;
			}
			if (def.min.y > def.max.y){
				int t = def.min.y;
				def.min.y = def.max.y;
				def.max.y = t;
			}
			if (def.min9.x > def.max9.x){
				int t = def.min9.x;
				def.min9.x = def.max9.x;
				def.max9.x = t;
			}
			if (def.min9.y > def.max9.y){
				int t = def.min9.y;
				def.min9.y = def.max9.y;
				def.max9.y = t;
			}
		
			//engine.removeEntity(be);
		}
		
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {

		if (event == Event.ROOM_BORDER_CHANGE){
			//Gdx.app.log(TAG,  "notified" + event);
			if (entities.size() == 0) {
				Gdx.app.log(TAG, "NO ROOM_DEFINITION");
				return;
			}
			//remove current
			for (Entity be : dragBlockEntities){
				//Vector2 p = be.getComponent(Position.class).pos;
				engine.removeEntity(be);
			}
			
			RoomDefinition def = entities.get(0).getComponent(RoomDefinition.class);
			invNotifier.notify(null, Event.OK_BUTTON_ENABLE, null);
			Entity be = engine.createEntity();
			DragBlock drag = engine.createComponent(DragBlock.class);
			be.add(drag);
			Position pos = engine.createComponent(Position.class);
			Grid2dInput grid = (Grid2dInput) c;
			
			if (grid.p.x == -1){
				pos.pos.set(def.min.x, def.min.y);//border min
				drag.type = DragBlock.MIN;
			}
			else if (grid.p.x == 1){
				pos.pos.set(def.max.x, def.max.y);//border max
				drag.type = DragBlock.MAX;
			}
			
			
			be.add(pos);
			engine.addEntity(be);
			//Gdx.app.log(TAG,  "change border " + pos.pos + grid.p);
		} else if (event == Event.ROOM_PATCH_CHANGE){
			///Gdx.app.log(TAG,  "notified" + event);
			if (entities.size() == 0) {
				Gdx.app.log(TAG, "NO ROOM_DEFINITION");
				return;
			}
			//remove current
			for (Entity be : dragBlockEntities){
				//Vector2 p = be.getComponent(Position.class).pos;
				engine.removeEntity(be);
			}
			
			RoomDefinition def = entities.get(0).getComponent(RoomDefinition.class);
			invNotifier.notify(null, Event.OK_BUTTON_ENABLE, null);
			Entity be = engine.createEntity();
			DragBlock drag = engine.createComponent(DragBlock.class);
			be.add(drag);
			Position pos = engine.createComponent(Position.class);
			Grid2dInput grid = (Grid2dInput) c;
			
			if (grid.p.x == -1){
				pos.pos.set(def.min9.x, def.min9.y);//border min
				drag.type = DragBlock.MIN9;
			}
			else if (grid.p.x == 1){
				pos.pos.set(def.max9.x, def.max9.y);//border max
				drag.type = DragBlock.MAX9;
			}
			
			
			be.add(pos);
			engine.addEntity(be);
			//Gdx.app.log(TAG,  "change border " + pos.pos + grid.p);
		} else if (event == Event.OK_BUTTON){
			//Gdx.app.log(TAG,  "OKOKOKOKOKOK");
			//throw new GdxRuntimeException("fjklds");
			if (entities.size() == 0) {
				Gdx.app.log(TAG, "NO ROOM_DEFINITION");
				return;
			}
			//hide ok button
			invNotifier.notify(null, Event.OK_BUTTON_DISABLE, null);
			RoomDefinition def = entities.get(0).getComponent(RoomDefinition.class);

			//destroy entities
			for (Entity be : dragBlockEntities){
				
				engine.removeEntity(be);
			}
			
		}
		
	}

}

