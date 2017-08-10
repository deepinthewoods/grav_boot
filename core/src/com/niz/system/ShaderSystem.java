package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class ShaderSystem extends RenderSystem implements Observer {
	//ShaderProgram spriteShader;

	//ShaderProgram backShader;
	ShaderProgram shader;

	public ShaderProgram lShader;

	//public ShaderProgram mapFgShader;

	//public ShaderProgram mapBgShader;

	//public ShaderProgram mapShader;

	//public ShaderProgram mapLitShader;
	
	private static final Vector3 LIGHT_COLOR = new Vector3(1f,1f,1f);
	//private static final float[] lightArr = {1f, 1f, 1f, 1f};
	private static final Vector3 AMBIENT_COLOR = new Vector3(.01f,.01f,.01f);
	private static final Vector3 FALLOFF = new Vector3(.053f, .13f, 35f);
	private static final float AMBIENT_INTENSITY = .327f;
	private static final float LIGHT_INTENSITY = .628f;
	public ShaderProgram coeffsShader;
	public ShaderProgram posShader;
	public ShaderProgram lightRampShader;
	public ShaderProgram charShader;


	@Override
	public void addedToEngine(Engine engine) {
		EngineNiz niz = (EngineNiz)engine;
		niz.getSubject("resize").add(this);
		setupShader();
	}

	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
	}

	private void setupShader() {
		//shader = new ShaderProgram(VERT, FRAG);
		String vert = Gdx.files.internal("lighting.vert").readString();
		String frag = Gdx.files.internal("lightingtoon.frag").readString();
		shader = new ShaderProgram(vert, frag);
		//Gdx.app.log(TAG,  "shader \n"+FRAG + "\n\n\n vert  \n\n\n"+VERT);
		if (!shader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());

		charShader = new ShaderProgram(vert, "#define IMMEDIATE \n" + frag);
		//Gdx.app.log(TAG,  "shader \n"+FRAG + "\n\n\n vert  \n\n\n"+VERT);
		if (!charShader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+charShader.getLog());

		//print any warnings
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		//setup default uniforms
		shader.begin();
		
		//our normal map
		shader.setUniformi("u_texture", 0);
		shader.setUniformi("u_index_texture", 1); //GL_TEXTURE1
		
		//shader.setUniformi("u_texture", 0);
		//shader.setUniformi("u_index_texture", 1); //GL_TEXTURE1
		//shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, 1f);

		shader.end();
		
		
		/*backShader = new ShaderProgram(Gdx.files.internal("lighting.vert"), Gdx.files.internal("lighting.frag"));
		//Gdx.app.log(TAG,  "shader \n"+FRAG + "\n\n\n vert  \n\n\n"+VERT);
		if (!backShader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+backShader.getLog());
		//print any warnings
		if (backShader.getLog().length()!=0)
			System.out.println(backShader.getLog());
		//setup default uniforms
		backShader.begin();
		AMBIENT_COLOR.set(.1f, .1f, .1f);
		
		//our normal map
		backShader.setUniformi("u_texture", 0);
		backShader.setUniformi("u_normals", 1); //GL_TEXTURE1
		
		backShader.setUniformi("u_texture", 0);
		backShader.setUniformi("u_normals", 1); //GL_TEXTURE1
		backShader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, 1f);

		backShader.end();
		
		spriteShader = new ShaderProgram(Gdx.files.internal("lighting.vert"), Gdx.files.internal("lighting.frag"));
		//Gdx.app.log(TAG,  "shader \n"+FRAG + "\n\n\n vert  \n\n\n"+VERT);
		if (!spriteShader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+spriteShader.getLog());
		//print any warnings
		if (spriteShader.getLog().length()!=0)
			System.out.println(spriteShader.getLog());
		//setup default uniforms
		spriteShader.begin();
		
		//our normal map
		spriteShader.setUniformi("u_texture", 0);
		spriteShader.setUniformi("u_normals", 1); //GL_TEXTURE1
		
		spriteShader.setUniformi("u_texture", 0);
		spriteShader.setUniformi("u_normals", 1); //GL_TEXTURE1
		spriteShader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, 1f);

		spriteShader.end();*/
		
		lShader = new ShaderProgram(Gdx.files.internal("lighting.vert"), Gdx.files.internal("lightingtoonleft.frag"));
		//Gdx.app.log(TAG,  "shader \n"+FRAG + "\n\n\n vert  \n\n\n"+VERT);
		if (!lShader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+lShader.getLog());
		//print any warnings
		if (lShader.getLog().length()!=0)
			System.out.println(lShader.getLog());
		//setup default uniforms
		lShader.begin();
		AMBIENT_COLOR.set(.1f, .1f, .1f);
		
		//our normal map
		lShader.setUniformi("u_texture", 0);
		lShader.setUniformi("u_normals", 1); //GL_TEXTURE1
		
		lShader.setUniformi("u_texture", 0);
		lShader.setUniformi("u_normals", 1); //GL_TEXTURE1
		//lShader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, 1f);

		lShader.end();

		//shader = null;
		//lShader = null;

		coeffsShader = new ShaderProgram(Gdx.files.internal("lighting.vert"), Gdx.files.internal("lightingtooncoeffs.frag"));
		if (!coeffsShader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+coeffsShader.getLog());

		posShader = new ShaderProgram(Gdx.files.internal("lighting.vert"), Gdx.files.internal("lightingtoonpos.frag"));
		if (!posShader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+posShader.getLog());

		lightRampShader = new ShaderProgram(Gdx.files.internal("lighting.vert"), Gdx.files.internal("lightingtoonramp.frag"));
		if (!lightRampShader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+lightRampShader.getLog());
	}

	private ShaderProgram makeShader() {
		ShaderProgram shad = new ShaderProgram(Gdx.files.internal("lighting.vert"), Gdx.files.internal("lightingtoon.frag"));
		//Gdx.app.log(TAG,  "shader \n"+FRAG + "\n\n\n vert  \n\n\n"+VERT);
		if (!shad.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+shad.getLog());
		//print any warnings
		if (shad.getLog().length()!=0)
			System.out.println(shad.getLog());
		//setup default uniforms
		shad.begin();
		
		//our normal map
		//shad.setUniformi("u_texture", 0);
		//shad.setUniformi("u_normals", 1); //GL_TEXTURE1
		
		//shad.setUniformi("u_texture", 0);
		//shad.setUniformi("u_normals", 1); //GL_TEXTURE1
		//shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, 1f);

		shad.end();
		return shad;
	}




	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in =  (VectorInput) c;
		int resolution = (int) in.v.x;
		if (shader == null) return;
		lShader.begin();
		lShader.setUniformf("Resolution", resolution/2f, resolution/2f);
		lShader.end();
		
		shader.begin();
		shader.setUniformf("Resolution", resolution/2f, resolution/2f);
		shader.end();
		
	}
}
