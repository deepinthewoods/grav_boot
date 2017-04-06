package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.IDisposeable;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.niz.ZoomInput;
import com.niz.component.Light;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class BufferStartSystem extends RenderSystem implements Observer, IDisposeable{
	//public static int BUFFER_SIZE = 256;
	private static final String TAG = "buffer start system";
	private FrameBuffer buffer;
	public FrameBuffer currentBuffer, mapBuffer;

	private ImmutableArray<Entity> lights;
	private OrthographicCamera camera;
	public boolean disabled = false;
	public boolean hasStarted;
	public BufferStartSystem() {
		
		float ar = Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
		//Gdx.app.log(TAG, "buffer "+ar);
		buffer = new FrameBuffer(Format.RGBA4444, 128, (int) (128), false);
		mapBuffer = new FrameBuffer(Format.RGBA4444, OverworldSystem.SCROLLING_MAP_WIDTH, OverworldSystem.SCROLLING_MAP_HEIGHT, false);
		Texture tex =  buffer.getColorBufferTexture();
		tex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

	}
	float zoom = 1f;
	private CameraSystem camSys;
	@Override
	public void addedToEngine(Engine engine) {
		lights = engine.getEntitiesFor(Family.all(Light.class).get());
		camSys = engine.getSystem(CameraSystem.class);
		camera = camSys.camera;
		((EngineNiz) engine).getSubject("resize").add(this);;
		((EngineNiz) engine).getSubject("zoom").add(new Observer(){


			@Override
			public void onNotify(Entity e, Event event, Object c) {
				ZoomInput z = (ZoomInput) c;
				zoom = z.zoom;
			}
			
		});;
		//disabled = true;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}
	Vector3 tmpV = new Vector3();
	private float viewportSize;
	@Override
	public void update(float deltaTime) {
		if (disabled){
			hasStarted = false;
			return;
		}
		if (camSys.zoomedOut)
			currentBuffer = mapBuffer;
		else currentBuffer = buffer;
		
		hasStarted = true;
		tmpV.set(camera.position);
		camera.setToOrtho(false, viewportSize, (int)(viewportSize));
		camera.position.set(tmpV);
		camera.update();
		camSys.adjustedCamera.position.set(tmpV);
		camSys.adjustedCamera.zoom = camera.zoom;
		camSys.adjustedCamera.update();
		
		currentBuffer.begin();
		if (!camSys.zoomedOut){
			Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		}
		
		
		
	}
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in = (VectorInput) c;
		if (viewportSize != in.v.x){
			viewportSize = in.v.x;
			if (buffer != null){
				buffer.dispose();
			}
			buffer = new FrameBuffer(Format.RGBA8888, (int)viewportSize, (int) (viewportSize), false){

				@Override
				protected Texture createColorTexture() {
					Texture tex = super.createColorTexture();
					tex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
					return tex;
				}
				
			};
			Gdx.app.log(TAG,  "new framebuffer");
			Texture tex =  buffer.getColorBufferTexture();
			tex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		}
		
	}
	@Override
	public void dispose() {
		buffer.dispose();
	}

//	shader.setUniformf("LightPos[0]", LIGHT_POS);
//	shader.setUniformi("u_texture", 0);
//	shader.setUniformi("u_normals", 1); //GL_TEXTURE1
//	shader.setUniformf("LightColor[0]", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
//	//shader.s
//	shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
//	shader.setUniformf("Falloff[0]", FALLOFF);
}
