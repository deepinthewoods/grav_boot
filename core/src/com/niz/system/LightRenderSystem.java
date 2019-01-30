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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BinaryHeap;
import com.niz.Main;
import com.niz.ZoomInput;
import com.niz.anim.Animations;
import com.niz.component.CameraControl;
import com.niz.component.Light;
import com.niz.component.Position;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class LightRenderSystem extends RenderSystem implements Observer{
	public static final int NUM_LIGHTS = 4;
	public static final int N_LAYERS = 7;

	public static final int BUFFER_SIZE = 512;

	private static final String TAG = "light render system";
	private final SpriteBatchN batch;

	ComponentMapper<Light> lightM = ComponentMapper.getFor(Light.class);
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	private ImmutableArray<Entity> lights;
	//String lightStr[] = new String[8], colorStr[] = new String[8], falloffStr[] = new String[8];



	private BufferStartSystem startBuffer;

	private ShaderSystem shaderSys;



	private float viewportSize;

	private ImmutableArray<Entity> playerEntities;

	private CameraSystem camSys;

	private MapRenderSystem mapR;


	public LightRenderSystem(SpriteBatchN batch) {
		this.batch = batch;
		//this.batch = new SpriteBatch();
		//posLoc[0] = shader.getUniformLocation("LightPos[0]");
		//colorLoc[0] = shader.getUniformLocation("LightColor[0]");
		//falloffLoc[0] = shader.getUniformLocation("Falloff[0]");
	}
	
	private float zoom;


	@Override
	public void addedToEngine(Engine engine) {
		shaderSys = engine.getSystem(ShaderSystem.class);
		


		
		lights = engine.getEntitiesFor(Family.all(Light.class, Position.class).get());
		startBuffer = engine.getSystem(BufferStartSystem.class);
		playerEntities = engine.getEntitiesFor(Family.all(CameraControl.class, Position.class).get());
		((EngineNiz) engine).getSubject("resize").add(this);;
		camSys = engine.getSystem(CameraSystem.class);
		
		((EngineNiz) engine).getSubject("zoom").add(new Observer(){



			@Override
			public void onNotify(Entity e, Event event, Object c) {
				ZoomInput z = (ZoomInput) c;
				zoom = z.zoom;
				zoom = 1f;
				//zoom = (float) Math.sqrt(zoom);



				//zoom = z.zoom;
			}
			
		});;

		mapR = engine.getSystem(MapRenderSystem.class);
		//mapR.lights = this;

	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}
	BinaryHeap<Light> heap = new BinaryHeap<Light>(16, false);
	
	@Override
	public void update(float deltaTime){


		   //TODO render lights
		startBuffer.lightDistanceBuffer.begin();
		camSys.mapDrawCamera.update();
		batch.setShader(null);
		batch.setProjectionMatrix(camSys.mapDrawCamera.combined);
		batch.enableTextureBinding();
		batch.enableBlending();
		batch.begin();
		for (int i = 0; i < lights.size(); i++) {
			Entity e = lights.get(i);
			Vector2 pos = posM.get(e).pos;
			Light light = lightM.get(e);
			//light.position is screen space
			Sprite s = Animations.lightFalloffSprites[light.falloffIndex][light.height];
			s.setPosition((pos.x* Main.PPM-Animations.HALF_LIGHT_SIZE) , (pos.y* Main.PPM-Animations.HALF_LIGHT_SIZE) );
			s.draw(batch);
		}
		batch.end();
		startBuffer.lightDistanceBuffer.end();//*/
		/*startBuffer.lightDistanceBuffer.begin();
		camSys.mapDrawCamera.update();
		batch.setTransformMatrix(camSys.mapDrawCamera.combined);
		batch.begin();
		batch.end();
		startBuffer.lightDistanceBuffer.end();//*/
	}
	
	Vector3 v = new Vector3();
	Vector2 unprojectedPlayerPos = new Vector2();

	float[] resolutionArr = {0,0}, ambient = new float[N_LAYERS];

	float[] pos = new float[NUM_LIGHTS*3 * N_LAYERS], falloff = new float[NUM_LIGHTS*3* N_LAYERS];
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in = (VectorInput) c;
		viewportSize = in.v.x;// * .25f;		
		
	}


}
