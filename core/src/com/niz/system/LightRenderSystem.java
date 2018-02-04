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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BinaryHeap;
import com.niz.Main;
import com.niz.ZoomInput;
import com.niz.component.CameraControl;
import com.niz.component.Light;
import com.niz.component.Position;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class LightRenderSystem extends RenderSystem implements Observer{
	private static final int NUM_LIGHTS = 4;
	public static final int N_LAYERS = 7;

	public static final int BUFFER_SIZE = 512;

	private static final String TAG = "light render system";

	ComponentMapper<Light> lightM = ComponentMapper.getFor(Light.class);
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	private ImmutableArray<Entity> lights;
	//String lightStr[] = new String[8], colorStr[] = new String[8], falloffStr[] = new String[8];
	int posLoc , colorLoc,
			 ambientLoc;

	int falloffLoc;
	private BufferStartSystem startBuffer;

	private ShaderSystem shaderSys;

	private ShaderProgram shader;

	private float viewportSize;

	private ImmutableArray<Entity> playerEntities;

	private CameraSystem camSys;

	private OrthographicCamera zoomOutCamera;

	public LightRenderSystem(OrthographicCamera zoomCamera) {
		zoomOutCamera = zoomCamera;
		//posLoc[0] = shader.getUniformLocation("LightPos[0]");
		//colorLoc[0] = shader.getUniformLocation("LightColor[0]");
		//falloffLoc[0] = shader.getUniformLocation("Falloff[0]");
	}
	
	private float zoom;


	@Override
	public void addedToEngine(Engine engine) {
		shaderSys = engine.getSystem(ShaderSystem.class);
		
		shader = shaderSys.shader;
		ShaderProgram coeffShader = shaderSys.coeffsShader;
		ShaderProgram posShader = shaderSys.posShader;
		if (shader != null){
			String[] att = shader.getUniforms();
			for (String s : att){
				
				//Gdx.app.log(TAG,  ""+s);;
			}
			
				posLoc = posShader.getUniformLocation("LightPos[0]");
				colorLoc = shader.getUniformLocation("LightColor[0]");
				falloffLoc = coeffShader.getUniformLocation("Falloff[0]");
				ambientLoc = shader.getUniformLocation("AmbientColor[0]");
				

				

		}
		
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
				if (zoom < 1.01f) {
					//zoom = 1f;
					return;
				}
				//zoom = (float) Math.sqrt(zoom);



				zoom = z.zoom;
			}
			
		});;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}
	BinaryHeap<Light> heap = new BinaryHeap<Light>(16, false);
	
	@Override
	public void update(float deltaTime){
		if (shader == null) return;
		//shader.begin();
		
		heap.clear();
		if (playerEntities.size() == 0){
			//Gdx.app.log(TAG,  "skip lights");
			return;
		}
		Entity player = playerEntities.first();
		Vector2 playerPos;// = posM.get(player).pos;
		
		playerPos = posM.get(player).pos;
		OrthographicCamera cam = camSys.camera;
		
		//if (camSys.zoomedOut) cam = zoomOutCamera;
		
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		//shader.end();
		
		for (int i = 0; i < lights.size(); i++){
			Entity e = lights.get(i);
			Vector2 pos = posM.get(e).pos;
			Light light = lightM.get(e);
			v.set(playerPos.x, playerPos.y, 0).scl(Main.PPM);
			cam.project(v);
			v.x /= w;
			v.y /= h;
			unprojectedPlayerPos.set(v.x, v.y);
			
			v.set(pos.x, pos.y, 0).scl(Main.PPM);
			cam.project(v);
			
			v.x /= w;
			v.y /= h;
			//if (camSys.zoomedOut) v.y = 1f - v.y;
		//	Gdx.app.log(TAG, "light "+v + " dist " + playerPos.dst2(pos));
			light.position.set(v.x, v.y, 0);
			heap.add(light, playerPos.dst2(pos));
			
			light.isOn = false;
		}
		
		for (int i = 0; i < NUM_LIGHTS; i++){
			if (heap.size == 0){
				break;
			}
			Light l = heap.pop();
			//Gdx.app.log(TAG,  "heap " + l.getValue() + " / " + heap.size + l.position);
			
			l.isOn = true;
		}

	}
	
	Vector3 v = new Vector3();
	Vector2 unprojectedPlayerPos = new Vector2();
	public void setUniforms(int layer, ShaderProgram shader) {
		setUniforms(layer, shader, false);
	}
	float[] resolutionArr = {0,0}, ambient = new float[N_LAYERS];
	public void setUniforms(int layer, ShaderProgram shader, boolean zoomOut) {

	}

	public void setUniformsData(int layer, ShaderProgram shader, boolean zoomOut){

	}

	public void setUniformsNew(ShaderProgram shader, boolean zoomOut, ShaderProgram lightShader, ShaderProgram posShader) {
		//if (true) return;
		if (shader == null) return;
		//if (layer == this.MAP_BACK_LAYER){

		//}
		for (int layer = 0; layer < N_LAYERS; layer++){
			int index = 0;
			//shader.begin();
			float maxAmbient = 0f;
			for (int i = 0; i < lights.size(); i++){
				Entity e = lights.get(i);
				Light light = lightM.get(e);
				//if (index >= NUM_LIGHTS) break;
				if (light.isOn){

					falloff[layer * NUM_LIGHTS * 3 +index*3] = light.falloff[layer].x;

					falloff[layer * NUM_LIGHTS * 3 +index*3+1] = light.falloff[layer].y;
					falloff[layer * NUM_LIGHTS * 3 +index*3+2] = light.falloff[layer].z;

					pos[layer * NUM_LIGHTS * 3 +index*3] = light.position.x;
					pos[layer * NUM_LIGHTS * 3 +index*3+1] = light.position.y;
					pos[layer * NUM_LIGHTS * 3 +index*3+2] = //light.position.z +
							light.yOffset[layer];
					//Gdx.app.log(TAG, "pos  "+light.position + "  " + (layer * NUM_LIGHTS * 3 +index*3));
					index++;
					maxAmbient = Math.max(maxAmbient, light.ambientIntensity[layer]);
				}
			}
			ambient[layer] = maxAmbient;
			for (;index < NUM_LIGHTS; index++){
				//Gdx.app.log(TAG, "skip light" + index);
			/*shader.setUniformf(posLoc[layer][index]
					, 0f
					, .5f
					, .01f);
			shader.setUniformf(colorLoc[layer][index], 0f, 0, 0f);
			//shader.setUniformf(7, 0f, 0, 0f);
			shader.setUniformf(falloffLoc[layer][index], 11111111111111111f, 11111111111111111111f, 111111111111111111111111111111f);//*/
				falloff[layer * NUM_LIGHTS * 3 + index*3] = 1f;
				falloff[layer * NUM_LIGHTS * 3 + index*3+1] = 10f;
				falloff[layer * NUM_LIGHTS * 3 + index*3+2] = 50f;
				pos[layer * NUM_LIGHTS * 3 + index*3+2] = 11111;
				pos[layer * NUM_LIGHTS * 3 + index*3] = 111110;

			}
		}

		shader.begin();
		shader.setUniform3fv(falloffLoc, falloff, 0, falloff.length);
		shader.end();

		posShader.begin();
		posShader.setUniform3fv(posLoc, pos, 0, pos.length);

		posShader.end();

		resolutionArr[0] = viewportSize;
		resolutionArr[1] = viewportSize;
		lightShader.begin();
		lightShader.setUniformf("Resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		lightShader.setUniform1fv(ambientLoc, ambient, 0, ambient.length);//maxAmbient);
		lightShader.setUniformf("Zoom", zoom);
		lightShader.end();
	}
	float[] pos = new float[NUM_LIGHTS*3 * N_LAYERS], falloff = new float[NUM_LIGHTS*3* N_LAYERS];
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in = (VectorInput) c;
		viewportSize = in.v.x;// * .25f;		
		
	}

	public void setUniformsNew(ShaderProgram shader, ShaderProgram lightShader, ShaderProgram posShader) {
		//for (int i =  0; i < Light.MAX_LAYERS; i++){
			setUniformsNew( shader, false, lightShader, posShader);
		//}
	}
}
