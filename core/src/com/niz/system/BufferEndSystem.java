package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.niz.Main;
import com.niz.ZoomInput;
import com.niz.component.Light;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class BufferEndSystem extends RenderSystem implements Observer{

	
	private static final String TAG = "buffer end sys";
	float zoom = 1f;
	private MapRenderSystem map;
	private SpriteBatchN batch;
	private BufferStartSystem startBuffer;
	private float viewportSize;
	private CameraSystem camSys;
	private LightRenderSystem lightSys;
	private ShaderProgram shader;
	private Texture blankNormalTexture;
	private SpriteAnimationSystem spriteSys;
	public BufferEndSystem(SpriteBatchN batch, Texture blankNormalTexture){
		this.batch = batch;
		this.blankNormalTexture = blankNormalTexture;
	}

	@Override
	public void addedToEngine(Engine engine) {
		map = engine.getSystem(MapRenderSystem.class);
		startBuffer = engine.getSystem(BufferStartSystem.class);
		((EngineNiz) engine).getSubject("resize").add(this);
        ((EngineNiz) engine).getSubject("zoom").add(new Observer(){

			@Override
			public void onNotify(Entity e, Event event, Object c) {
				ZoomInput z = (ZoomInput) c;
				zoom = z.zoom;
				if (z.zoom > 1.01f){
					//zoom = Main.PPM;
				}
				//zoom = 1f;
			}
			
		});
        camSys = engine.getSystem(CameraSystem.class);
		lightSys = engine.getSystem(LightRenderSystem.class);
		shader = engine.getSystem(ShaderSystem.class).shader;
		spriteSys = engine.getSystem(SpriteAnimationSystem.class);

	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}
	
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in = (VectorInput) c;
		viewportSize = in.v.x;
		
	}
	
	@Override
	public void update(float deltaTime) {
		if (!startBuffer.hasStarted){
			Gdx.app.log(TAG , "no buffer " +zoom);

			return;
		}
		batch.setShader(null);
		//startBuffer.currentBuffer.end();
		//Gdx.app.log(TAG , "zoomed " +zoom);

		if (camSys.zoomedOut && false){


			//spriteSys.drawLowLOD();
		} else {
			//if (true) return;
			float w =  (Gdx.graphics.getWidth() ), h =  (Gdx.graphics.getHeight()  ), max = (Main.ar > 1?Math.max(w,  h):Math.min(w, h));
			batch.getProjectionMatrix().setToOrtho2D(max / 2 - w / 2, max/2+h/2, w,  -h	);

			//batch.getProjectionMatrix().scale(zoom,  zoom,  zoom);
			//batch.getProjectionMatrix().scale(zoom, zoom, 1f);
			batch.getProjectionMatrix().setToOrtho2D(-w/2, -h/2, w,  h	);

			startBuffer.currentBuffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			batch.setShader(null);
			//batch.disableBlending();
			batch.begin();
			float dw = w - max;//Gdx.graphics.getWidth();
			float dh = h - max;//Gdx.graphics.getHeight();
			//Gdx.app.log(TAG , "zoomed " +zoom);
			w /= zoom;
			h /= zoom;
			w = (int)w;
			h = (int)h;
			batch.draw(startBuffer.currentBuffer.getColorBufferTexture(), -w/2, h/2, w, -h);
			batch.end();
		}
		
	}

}