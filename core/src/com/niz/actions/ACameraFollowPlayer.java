package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.Main;
import com.niz.ZoomInput;
import com.niz.action.Action;
import com.niz.component.DragBlock;
import com.niz.component.Physics;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;
import com.niz.system.CameraSystem;

public class ACameraFollowPlayer extends Action {
	private static final float _SPEED_FACTOR = 0.03f;
	private static final String TAG = "cam follow action";
	transient Entity player;
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	public static final float MOVE_SPEED = 8f * Main.timeStep;
	private static final float FAST_THRESHOLD = 8;
	
	private static final int Y_DISTANCE_ON_GROUND_THRESHOLD = 2;
	
	private static final float FAST_TIME = 2f;
	private static final float CAMERA_OFFSET = 6;
	private float range = 8;
	boolean moving;
	float targetY;
	private boolean free;
	private float time;
	private boolean fast;
	private float fastTimeout;
	private static Family family;
	private static Family playerFamily = Family.all(Position.class, Player.class).get();
	private static Family dragBlockFamily = Family.all(Position.class, DragBlock.class).get();

	private static Vector2 tmpV = new Vector2(), p = new Vector2();
	@Override
	public void update(float dt) {}
	

	@Override
	public void updateRender(float dt) {
		ImmutableArray<Entity> dragBlockEntities = parent.engine.getEntitiesFor(dragBlockFamily);
		if (dragBlockEntities.size() > 0){
			posM.get(parent.e).pos.set(posM.get(dragBlockEntities.get(0)).pos);
			//Gdx.app.log(TAG, "following drag block" + posM.get(dragBlockEntities.get(0)).pos);
			return;
		}
		ImmutableArray<Entity> entities = parent.engine.getEntitiesFor(playerFamily);
		if (entities.size() == 0) return;
		
		player = entities.get(0);
		//Gdx.app.log(TAG, "camera update");
		Physics phys = physM.get(player);
		if (phys == null) return;
		Vector2 pos = posM.get(parent.e).pos;
		Vector2 playerPos = p;
		p.set(posM.get(player).pos);//.add(0, CAMERA_OFFSET * zoom);
		
		
		pos.y = Math.max(pos.y, playerPos.y - range * zoom);
		pos.y = Math.min(pos.y, playerPos.y + range * zoom);
		
		float dst = Math.abs(targetY - playerPos.y );
		if ((phys.onGround //&& dst > Y_DISTANCE_STAND_THRESHOLD
				)
				
				&&
				dst > Y_DISTANCE_ON_GROUND_THRESHOLD
				){
				//Gdx.app.log(TAG, "target set "+targetY);
				targetY = playerPos.y;
			
		}
		
		time += dt;
		if (fast & fastTimeout < time) fast = false;

		float dist = Math.abs(targetY - playerPos.y);
		/*if (dist > FAST_THRESHOLD){
			fast = true;
			fastTimeout = time + FAST_TIME;
			targetY = playerPos.y;
			//Gdx.app.log("camfollowplayer", "fast "+dist + " " + targetY + pos.y);
			
		} else {
		}*/
		float dy = pos.y - targetY;
		pos.x = playerPos.x;
		boolean up = dy < 0;
		float diy = Math.abs(dy);
				
		float v = dy;
		if (diy < .1f) return;
		
		v *= -_SPEED_FACTOR;
		pos.y += v;
		if (v != v) throw new GdxRuntimeException("nan"+targetY + pos);
		
		if (zoomedOut){
			//pos.set(OverworldSystem.SCROLLING_MAP_WIDTH/2, OverworldSystem.SCROLLING_MAP_HEIGHT/2);
			//pos.scl(Main.PPM);
		}
		//pos.set(playerPos);
		//playerPos = posM.get(player).pos;
		
	}


	@Override
	public void onEnd() {
		
	}
	private float zoom;

	private boolean zoomedOut;
	private OrthographicCamera cam;
	@Override
	public void onStart() {
		fast = false;
		((EngineNiz) parent.engine).getSubject("zoom").add(new Observer(){

			@Override
			public void onNotify(Entity e, Event event, Object c) {
				ZoomInput z = (ZoomInput) c;
				
				if (z.zoom > 1.01f){
					zoomedOut = true;
					zoom = z.zoom;
				} else {
					zoomedOut = false;
					zoom = z.zoom;
				}
			}
		}
		);
		cam = parent.engine.getSystem(CameraSystem.class).adjustedCamera;
		range = Gdx.graphics.getHeight() / Main.PPM;
		range /= 6;
		
	}

}
