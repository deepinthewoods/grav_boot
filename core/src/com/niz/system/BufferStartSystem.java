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
	public FrameBuffer currentBuffer, mapBuffer, lightDistanceBuffer;

	private ImmutableArray<Entity> lights;
	public boolean disabled = false;
	public boolean hasStarted;
	private float viewportHeight;
	private VectorInput tmpVecInput = new VectorInput();

	public BufferStartSystem() {
		tmpVecInput.v.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float ar = Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
		//Gdx.app.log(TAG, "buffer "+ar);
		//buffer = new FrameBuffer(Format.RGBA4444, 128, (int) (128), false);
		onNotify(null, Event.RESIZE, tmpVecInput);
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
		((EngineNiz) engine).getSubject("resize").add(this);;
		((EngineNiz) engine).getSubject("zoom").add(new Observer(){


			@Override
			public void onNotify(Entity e, Event event, Object c) {
				ZoomInput z = (ZoomInput) c;
				zoom = z.zoom;
			}
			
		});;
		//disabled = true;
		buffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		mapBuffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}
	Vector3 tmpV = new Vector3();
	private float viewportWidth;
	@Override
	public void update(float deltaTime) {
//		if (camSys.zoomedOut)
//			currentBuffer = mapBuffer;
//		else
			currentBuffer = buffer;

		hasStarted = true;
		//tmpV.set(camSys.camera.position);
//		camera.setToOrtho(false, viewportWidth, (int)(viewportHeight));
//		camera.position.set(tmpV);
//		camera.update();
//		camSys.adjustedCamera.position.set(tmpV);
//		camSys.adjustedCamera.zoom = camera.zoom;
//		camSys.adjustedCamera.update();
		if (disabled){
			hasStarted = false;
			return;
		}

		currentBuffer.begin();
//		if (!camSys.zoomedOut){
			Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			//Gdx.app.log(TAG, "buffer start");
//		}
		currentBuffer.end();
		/*lightDistanceBuffer.begin();
//		if (!camSys.zoomedOut){
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Gdx.app.log(TAG, "buffer start");
//		}
		lightDistanceBuffer.end();*/
		
	}
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in = (VectorInput) c;
		if (viewportWidth != in.v.x){
			viewportWidth = in.v.x;// * .25f;
			viewportHeight = in.v.y;
			if (buffer != null){
				buffer.dispose();
			}
			buffer = new FrameBuffer(Format.RGBA8888, (int) viewportWidth, (int) (viewportHeight), false){

			};

			lightDistanceBuffer = new FrameBuffer(Format.RGBA8888, (int) viewportWidth, (int) (viewportHeight), false){

			};
			//Gdx.app.log(TAG,  "new framebuffer");
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
