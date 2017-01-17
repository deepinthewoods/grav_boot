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

	public static final int BUFFER_SIZE = 512;

	private static final String TAG = "light render system";

	ComponentMapper<Light> lightM = ComponentMapper.getFor(Light.class);
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	private ImmutableArray<Entity> lights;
	//String lightStr[] = new String[8], colorStr[] = new String[8], falloffStr[] = new String[8];
	int posLoc[] = new int[8], colorLoc[] = new int[8], falloffLoc[] = new int[8], ambientLoc[] = new int[8];

	private BufferStartSystem startBuffer;

	private ShaderSystem shaderSys;

	private ShaderProgram spriteShader;

	private ShaderProgram backShader;

	private ShaderProgram shader;

	private float viewportSize;

	private ImmutableArray<Entity> playerEntities;

	private CameraSystem camSys;

	private ShaderProgram litShader;

	private OrthographicCamera zoomOutCamera;

	public LightRenderSystem(OrthographicCamera zoomCamera) {
		zoomOutCamera = zoomCamera;
		//posLoc[0] = shader.getUniformLocation("LightPos[0]");
		//colorLoc[0] = shader.getUniformLocation("LightColor[0]");
		//falloffLoc[0] = shader.getUniformLocation("Falloff[0]");
	}
	
	private float zoom;

	private ShaderProgram lShader;

	@Override
	public void addedToEngine(Engine engine) {
		shaderSys = engine.getSystem(ShaderSystem.class);
		spriteShader = shaderSys.spriteShader;
		backShader = shaderSys.backShader;
		shader = shaderSys.shader;
		lShader = shaderSys.lShader;
		litShader = shaderSys.shader;
		String[] att = shader.getUniforms();
		for (String s : att){
			
			//Gdx.app.log(TAG,  ""+s);;
		}
		
			posLoc[1] = spriteShader.getUniformLocation("LightPos[0]");
			colorLoc[1] = spriteShader.getUniformLocation("LightColor[0]");
			falloffLoc[1] = spriteShader.getUniformLocation("Falloff[0]");
			ambientLoc[1] = spriteShader.getUniformLocation("AmbientColor");
			
			posLoc[0] = lShader.getUniformLocation("LightPos[0]");
			colorLoc[0] = lShader.getUniformLocation("LightColor[0]");
			falloffLoc[0] = lShader.getUniformLocation("Falloff[0]");
			ambientLoc[0] = lShader.getUniformLocation("AmbientColor");
			
			posLoc[2] = backShader.getUniformLocation("LightPos[0]");
			colorLoc[2] = backShader.getUniformLocation("LightColor[0]");
			falloffLoc[2] = backShader.getUniformLocation("Falloff[0]");
			ambientLoc[2] = backShader.getUniformLocation("AmbientColor");
			
			posLoc[3] = shader.getUniformLocation("LightPos[0]");
			colorLoc[3] = shader.getUniformLocation("LightColor[0]");
			falloffLoc[3] = shader.getUniformLocation("Falloff[0]");
			ambientLoc[3] = shader.getUniformLocation("AmbientColor");
			
			posLoc[4] = litShader.getUniformLocation("LightPos[0]");
			colorLoc[4] = litShader.getUniformLocation("LightColor[0]");
			falloffLoc[4] = litShader.getUniformLocation("Falloff[0]");
			ambientLoc[4] = litShader.getUniformLocation("AmbientColor");
			
			posLoc[5] = shader.getUniformLocation("LightPos[0]");
			colorLoc[5] = shader.getUniformLocation("LightColor[0]");
			falloffLoc[5] = shader.getUniformLocation("Falloff[0]");
			ambientLoc[5] = shader.getUniformLocation("AmbientColor");
			
		
		
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
					zoom = 1f;
					return;
				}
				//zoom = (float) Math.sqrt(zoom);
				zoom -= 1f;
				//zoom *= .101125f;
				zoom += 1f;
				//zoom = 1f;
				//zoom = 1f;
				zoom += 1f/zoom;
				zoom += 1f/zoom;
				zoom /= 2f;
				zoom = z.zoom;
			}
			
		});;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}
	BinaryHeap<Light> heap = new BinaryHeap<Light>(16, false);
	@Override
	public void update(float deltaTime){
		
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
			v.set(playerPos.x, playerPos.y, 0);
			cam.project(v);
			v.x /= w;
			v.y /= h;
			unprojectedPlayerPos.set(v.x, v.y);
			
			v.set(pos.x, pos.y, 0).scl(Main.PPM);
			cam.project(v);
			
			v.x /= w;
			v.y /= h;
			//if (camSys.zoomedOut) v.y = 1f - v.y;
			//Gdx.app.log(TAG, "light "+v);
			light.position.set(v.x, v.y, 0);
			heap.add(light, unprojectedPlayerPos.dst2(v.x, v.y));
			light.isOn = false;
		}
		
		for (int i = 0; i < NUM_LIGHTS; i++){
			if (heap.size == 0){
				break;
			}
			Light l = heap.pop();
			//Gdx.app.log(TAG,  "heap " + l.getValue() + " / " + heap.size);
			
			l.isOn = true;
		}
	}
	
	Vector3 v = new Vector3();
	Vector2 unprojectedPlayerPos = new Vector2();
	public void setUniforms(int layer, ShaderProgram shader) {
		setUniforms(layer, shader, false);
	}
	
	public void setUniforms(int layer, ShaderProgram shader, boolean zoomOut) {
		if (shader == null) return;
		//if (layer == this.MAP_BACK_LAYER){
			if (startBuffer.hasStarted)
				shader.setUniformf("Resolution", viewportSize, viewportSize);
			else shader.setUniformf("Resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//}
		int index = 0;
		//shader.begin();
		float maxAmbient = 0f;
		for (int i = 0; i < lights.size(); i++){
			Entity e = lights.get(i);
			Light light = lightM.get(e);
			if (i >= NUM_LIGHTS) break;
			if (light.isOn){
				//if (true) continue;
				/*shader.setUniformf(posLoc[layer][index]
						//, (int)(Main.PPM * light.position.x*BufferStartSystem.BUFFER_SIZE)/(Main.PPM*BufferStartSystem.BUFFER_SIZE)
						//, (int)(Main.PPM * light.position.y*BufferStartSystem.BUFFER_SIZE)/(Main.PPM*BufferStartSystem.BUFFER_SIZE)
						, light.position.x
						, light.position.y
						, light.position.z + light.yOffset[layer]);//*/
				
				//shader.setUniformf(colorLoc[layer][index], light.color[layer].x, light.color[layer].y, light.color[layer].z, light.intensity[layer]);
				//shader.setUniformf(falloffLoc[layer][index], light.falloff[layer].x, light.falloff[layer].y, light.falloff[layer].z);
				//shader.setUniformf(ambientLoc[layer][index], light.ambient[layer].x, light.ambient[layer].y, light.ambient[layer].z, light.ambientIntensity[layer]);
				
				//if (light.ambientColor[layer] != null){
				//	shader.setUniformf("AmbientColor", light.ambientColor[layer].x, light.ambientColor[layer].y, light.ambientColor[layer].z, 1f);
				//}
				//shader.setUniformf("Resolution", BufferStartSystem.BUFFER_SIZE, BufferStartSystem.BUFFER_SIZE);
				//shader.setUniformf("Resolution", BufferStartSystem.BUFFER_SIZE, BufferStartSystem.BUFFER_SIZE);
				//shader.setUniformi("u_texture", 0);
				//shader.setUniformi("u_normals", 1); //GL_TEXTURE1
				//shader.setUniformf("AmbientColor", 1f, 1f, 1f, 0f);
				
				falloff[index*3] = light.falloff[layer].x;
				
				falloff[index*3+1] = light.falloff[layer].y; 
				falloff[index*3+2] = light.falloff[layer].z;
				
				pos[index*3] = light.position.x;
				pos[index*3+1] = light.position.y;
				pos[index*3+2] = light.position.z + light.yOffset[layer];
				//Gdx.app.log(TAG, "falloff "+light.position + "  " + index);
				index++;
				maxAmbient = Math.max(maxAmbient, light.ambientIntensity[layer]);
			}
		}
		for (;index < NUM_LIGHTS; index++){
			//Gdx.app.log(TAG, "disabled light"+index);
			/*shader.setUniformf(posLoc[layer][index]
					
					, 0f
					, .5f
					, .01f);
			shader.setUniformf(colorLoc[layer][index], 0f, 0, 0f);
			//shader.setUniformf(7, 0f, 0, 0f);
			shader.setUniformf(falloffLoc[layer][index], 11111111111111111f, 11111111111111111111f, 111111111111111111111111111111f);//*/
			falloff[index*3] = 1111111f;
			
		}
		
		//shader.setUniformf("Falloff[0]", 1111111f, 1111111111f, 1111111111111f);
		//shader.setUniformf("Falloff[1]", 1111111f, 1111111111f, 1111111111111f);
		shader.setUniform3fv(falloffLoc[layer], falloff, 0, 12);
		shader.setUniform3fv(posLoc[layer], pos, 0, 12);
		shader.setUniformf(ambientLoc[layer], maxAmbient);
		//if (maxAmbient != 0f) Gdx.app.log(TAG, "ambient" + maxAmbient);
		shader.setUniformf("Zoom", zoom);
		//shader.setUniform3fv("Falloff[0]", falloff, 0, 12);
		//shader.setUniform3fv("LightPos[0]", pos, 0, 12);
		//shader.setUniformf("AmbientColor", .3f);
		
		//shader.setUniformf("Falloff[2]", 1111111f, 1111111111f, 1111111111111f);
		//shader.setUniformf("Falloff[3]", 1111111f, 1111111111f, 1111111111111f);
		//shader.end();
	}
	float[] pos = new float[NUM_LIGHTS*3], falloff = new float[NUM_LIGHTS*3];
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in = (VectorInput) c;
		viewportSize = in.v.x;		
		
	}

	
}
