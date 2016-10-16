package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.niz.component.CameraControl;
import com.niz.component.Position;

public class SimpleCameraSystem extends EntitySystem {

	private static final float CAMERA_SPEED = 3.5f;

	private static final float CAMERA_THRESHOLD = 0.3f;

	private Vector2 target, v = new Vector2(); 
	
	private OrthographicCamera camera;

	private ImmutableArray<Entity> entities;
	
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	private ComponentMapper<CameraControl> camC = ComponentMapper.getFor(CameraControl.class);

	public SimpleCameraSystem(OrthographicCamera gameCamera) {
		camera = gameCamera;
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Position.class, CameraControl.class).get());
		super.addedToEngine(engine);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		if (entities.size() == 0) return;
		Entity e = entities.get(0);
		Vector2 pos = posM.get(e).pos;
		CameraControl cam = camC.get(e);
		if (cam.offset.len2() > 1){
			camera.position.add(cam.offset.x, cam.offset.y, 0);
			cam.offset.set(0,0);
		}
		target = cam.target;
		if (pos.dst2(target) > 10f){
			target.set( pos);
		}
		v.set(target);
		v.sub(camera.position.x, camera.position.y);
		if (v.len2() > CAMERA_THRESHOLD){
			v.nor();
			v.scl(deltaTime * CAMERA_SPEED);
			camera.position.add(v.x, v.y, 0);
		}
		camera.update();
	}

}
