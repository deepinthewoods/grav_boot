package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.niz.Main;
import com.niz.ZoomInput;
import com.niz.component.CameraControl;
import com.niz.component.DragController;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class CameraSystem extends RenderSystem implements Observer {

	private static final float CAMERA_SPEED = 3.5f;

	private static final float CAMERA_THRESHOLD = 0.3f;

	private static final String TAG = "Camera System";

	private Vector2 target, v = new Vector2();

	private Vector3 vA = new Vector3(), vB = new Vector3();

	OrthographicCamera camera;

	private ImmutableArray<Entity> entities;

	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	private ComponentMapper<CameraControl> camC = ComponentMapper.getFor(CameraControl.class);

	float zoom = 1f;

	public ParallaxBackgroundSystem parallaxSys;

	private ImmutableArray<Entity> playerEntities;

	private ImmutableArray<Entity> dragEntities;

    public OrthographicCamera adjustedCamera;

	public OrthographicCamera mapDrawCamera;

	public static float onePixel;
	private float menuZoom;


	public CameraSystem() {
		camera = new OrthographicCamera();
		mapDrawCamera = new OrthographicCamera();
		adjustedCamera = new OrthographicCamera();
	}

//	public boolean zoomedOut;

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Position.class, CameraControl.class).get());
		dragEntities = engine.getEntitiesFor(Family.all(Position.class, DragController.class).get());

		playerEntities = engine.getEntitiesFor(Family.all(Position.class, Player.class).get());
		super.addedToEngine(engine);
		((EngineNiz)engine).getSubject("resize").add(this);
		((EngineNiz)engine).getSubject("zoom").add(new Observer(){


			@Override
			public void onNotify(Entity e, Event event, Object c) {
				ZoomInput z = (ZoomInput) c;
				//zoom = Math.max(1,  z.zoom);
				if (z.zoom < 1.001f){
					zoom  = z.zoom;//
//					zoomedOut = false;
				}
				else{
//					zoomedOut = true;
					zoom = z.zoom;
				}
				//zoom = 1f;
				//Gdx.app.log(TAG , "zoomed " +zoom + "  " );

			}

		});

	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}

Vector2 tmpV = new Vector2();


	@Override
	public void update(float deltaTime) {
		if (entities.size() == 0) {
			return;
		}
		Entity e = entities.get(0);
		Vector2 pos = posM.get(e).pos;
		Vector2 playerPos = tmpV;

		if (playerEntities.size() == 0){
			if (dragEntities.size() == 0)
				return;
			//if (true) return;
			Vector2 pos2 = posM.get(dragEntities.get(0)).pos;
			vA.set(0,0, 0);
			vB.set(1, 0, 0);
			camera.unproject(vA);
			camera.unproject(vB);
			vA.sub(vB);
			onePixel = -vA.x;
			onePixel /= Main.PPM;
			//camera.zoom = pos2.y;
			float camHeight = camera.viewportHeight * camera.zoom * Main.PX;
			camHeight /= 3f;
			//vA is delta

            pos.set(pos2);

            //Gdx.app.log(TAG , "zoomed " +zoom + "  " + camera.position);
            camera.position.set((int)(pos.x*Main.PPM), (Gdx.graphics.getHeight()/4) * menuZoom, 0);//*/
            camera.zoom = menuZoom;
            camera.update();

            mapDrawCamera.zoom = camera.zoom;
            //if (!zoomedOut)
            mapDrawCamera.zoom = 1f;
            mapDrawCamera.position.set(camera.position.x, camera.position.y, 0);
            mapDrawCamera.update();
			//Gdx.app.log(TAG, "drag controller" + camera.zoom);
			return;
		}

		playerPos = posM.get(playerEntities.get(0)).pos;
		pos.set(playerPos);

		//Gdx.app.log(TAG , "zoomed " +zoom + "  " + camera.position);
		camera.position.set((int)(pos.x*Main.PPM), (int)(pos.y*Main.PPM), 0);//*/
		camera.zoom = zoom;
		camera.update();

		mapDrawCamera.zoom = camera.zoom;
		//if (!zoomedOut)
			mapDrawCamera.zoom = 1f;
		mapDrawCamera.position.set((int)(pos.x*Main.PPM), (int)(pos.y*Main.PPM), 0);
		mapDrawCamera.update();


	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in = (VectorInput) c;
		float VIEWPORT_SIZE = in.v.x;
        camera.setToOrtho(false, Gdx.graphics.getWidth()
				, Gdx.graphics.getHeight()
		);
		mapDrawCamera.setToOrtho(false, Gdx.graphics.getWidth() //* Main.PPM
				, Gdx.graphics.getHeight() //* Main.PPM
		);
		adjustedCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//gameCamera = new OrthographicCamera(10, 10);//Main.PPM*Main.VIEWPORT_SIZE, (int)(Main.PPM*Main.VIEWPORT_SIZE/Main.ar));
		menuZoom = 300f / Gdx.graphics.getHeight();
		Gdx.app.log(TAG, "menuZ " + menuZoom);
	}

}
