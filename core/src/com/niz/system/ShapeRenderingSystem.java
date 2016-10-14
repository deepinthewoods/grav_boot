package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.niz.Main;
import com.niz.component.Map;

public class ShapeRenderingSystem extends RenderSystem {
	public ShapeRenderer rend;
	private LightRenderSystem lights;
	private Family family;
	private ImmutableArray<Entity> entities;
	private CameraSystem cameraSys;
	@Override
	public void addedToEngine(Engine engine) {
		//engine.getSystem(ParallaxBackgroundSystem.class).shaper = this;
		rend = new ShapeRenderer();
		lights = engine.getSystem(LightRenderSystem.class);
		//buffer = engine.getSystem(BufferStartSystem.class);
		family = Family.one(Map.class).get();
		//engine.addEntityListener(family, this);
		//mapM = ComponentMapper.getFor(Map.class);
		entities = engine.getEntitiesFor(family);
		cameraSys = engine.getSystem(CameraSystem.class);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		
		//rend.setTransformMatrix(cameraSys.camera.combined);
		//rend.getProjectionMatrix().set(cameraSys.camera.projection);
		cameraSys.camera.update();
		float s = 1f/(float)Main.PPM;
		rend.setProjectionMatrix(cameraSys.camera.combined);
		//rend.getProjectionMatrix().scale(s, s, s);
		super.update(deltaTime);
	}

}
