package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.IDisposeable;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.niz.Main;
import com.niz.ZoomInput;
import com.niz.component.Map;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class MapRenderSystem extends RenderSystem implements EntityListener, IDisposeable {
	
	public static final int RENDER_SIZE = 		64;
	private static final String TAG = "MapRenderSystem";
	private static final Vector3 LIGHT_POS = new Vector3(53f,.753f,0.51075f);
	private static final int OVERDRAW_PIXELS = 10;
	private static final int INDEX_BUFFER_HEIGHT = 66;
	public final FrameBuffer indexBuffer;
	public final Texture indexTexture;

	private Array<Vector2> topTiles = new Array<Vector2>();
	OrthographicCamera camera;
	//private 
	//OrthographicCamera defaultCamera;
	private SpriteBatchN batch;
	private Sprite[] sprites;
	private Sprite s;
	private Vector3 tmpV = new Vector3();
	private Texture normalTexture;
	private Texture diffTexture;
	ShaderProgram shader;
	private LightRenderSystem lights;
	private BufferStartSystem buffer;
	private Family family;
	private ComponentMapper<Map> mapM;
	private ImmutableArray<Entity> entities;
	private TextureAtlas atlas;
	private ShaderSystem shaderSys;
	private EngineNiz engine;
	float zoom = 1f;
	public boolean hasRendered;
	private boolean skippedDraw;
	private MapSystem mapSys;
	private OrthographicCamera renderCamera;
	private CameraSystem camSys;
	private OrthographicCamera zoomOutCamera;
	private ShaderProgram coefficientsShader;
	private ShaderProgram positionShader;
	public Texture atlasTexture;// = new Texture(Gdx.files.internal("tilesprocessed.png"));
	private ShaderProgram lightRampShader;


	public MapRenderSystem(OrthographicCamera gameCamera, OrthographicCamera zoomOutCamera, SpriteBatchN batch, TextureAtlas atlas, Texture diffTexture, Texture normalTexture) {
		this.diffTexture = diffTexture;
		this.normalTexture = normalTexture;
		this.camera = gameCamera;//mapCollisionCamera;
		this.renderCamera = gameCamera;
		this.zoomOutCamera = zoomOutCamera;
		//defaultCamera = defaultCam;
		this.batch = batch;
		
		float ar = Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		
		//this.shader = shader;
		this.atlas = atlas;
		//this.backShader = backShader;
		indexBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 128, INDEX_BUFFER_HEIGHT, false);
		indexBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		indexTexture = new Texture(Gdx.files.internal("indexTexture.png"));
		atlasTexture = new Texture(Gdx.files.internal("tilesprocessed.png"));
		//Gdx.app.log("map",  "write "+(int) (16/ar));
	}

	@Override
	public void addedToEngine(Engine engine) {
		shaderSys = engine.getSystem(ShaderSystem.class);
		
		//spriteShader = shaderSys.spriteShader;
		
		shader = shaderSys.shader;
		coefficientsShader = shaderSys.coeffsShader;
		positionShader = shaderSys.posShader;
		lightRampShader = shaderSys.lightRampShader;
		//shader = createDefaultShader();
		
		lights = engine.getSystem(LightRenderSystem.class);
		buffer = engine.getSystem(BufferStartSystem.class);
		family = Family.one(Map.class).get();
		engine.addEntityListener(family, this);
		mapM = ComponentMapper.getFor(Map.class);
		entities = engine.getEntitiesFor(family);
		this.engine = (EngineNiz) engine;
		((EngineNiz) engine).getSubject("zoom").add(new Observer(){

			@Override
			public void onNotify(Entity e, Event event, Object c) {
				ZoomInput z = (ZoomInput) c;
				zoom = z.zoom;
			}
			
		});;
		camSys = engine.getSystem(CameraSystem.class);
		mapSys = engine.getSystem(MapSystem.class);
		OnMapSystem onMap = engine.getSystem(OnMapSystem.class);
		onMap.shader = shader;
		onMap.coeffsShader = shaderSys.coeffsShader;
		onMap.posShader = shaderSys.posShader;
		OverworldSystem over = engine.getSystem(OverworldSystem.class);
		over.shader = shader;
		over.coeffsShader = shaderSys.coeffsShader;
		over.posShader = shaderSys.posShader;

		Map[] maps = new Map[3];
		for (int i = 0; i < maps.length; i++){
			maps[i] = over.mapPool.obtain();
		}
		for (int i = 0; i < maps.length; i++){
			over.mapPool.free(maps[i]);
			maps[i] = null;
		}
	}
	/** Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified. */
	/*static public ShaderProgram createDefaultShader () {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
				+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "uniform sampler2D u_index_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ " vec4 DiffuseColor = texture2D(u_texture, v_texCoords); \n"
				+ " int cIndex = int(DiffuseColor.r * 128.0); \n"
				+ " int nIndex = int(DiffuseColor.g * 128.0);  \n"
				+ " vec4 IndexedColor = texture2D(u_index_texture, vec2(DiffuseColor.r, 0.0)); \n"
				+ "  gl_FragColor = v_color * IndexedColor;\n" //
				//+ "  gl_FragColor = v_color * DiffuseColor;\n" //
				+ "}";

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}*/



	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		//if (true) return;
		//if (zoom > 1f) return;
		boolean skipDraw = false;//zoom > Main.PPM+.1f;//1.001f;
		if (skipDraw){
			hasRendered = false;
			return;
		} else{
			//Gdx.app.log(TAG,  "draw");;
			hasRendered = true;
		}
		float originalZoom = camera.zoom;
		
		camera.update();
		int 	x0 = (int) camera.position.x
				, x1 = x0
				, y0 = (int) camera.position.y
				, y1 = y0
				;
		
		tmpV.set(Gdx.graphics.getWidth()+OVERDRAW_PIXELS, Gdx.graphics.getHeight()+OVERDRAW_PIXELS, 0);
		camera.unproject(tmpV );
		x0 = (int) tmpV.x/Main.PPM;
		y0 = (int) tmpV.y/Main.PPM;
		//Gdx.app.log(TAG, "unproj "+tmpV);
		tmpV.set(-OVERDRAW_PIXELS, -OVERDRAW_PIXELS, 0);
		camera.unproject(tmpV);
		x1 = (int) tmpV.x/Main.PPM;
		y1 = (int) tmpV.y/Main.PPM;
		//Gdx.app.log(TAG, "unproj "+tmpV);
		//Gdx.app.log(TAG, " "+camera.position);
		x0 /= RENDER_SIZE;
		x1 /= RENDER_SIZE;
		y0 /= RENDER_SIZE;
		y1 /= RENDER_SIZE;
	
		if (x0 > x1){
			int t = x0;
			x0 = x1;
			x1 = t;
		}
		if (y0 > y1){
			int t = y0;
			y0 = y1;
			y1 = t;
		}
		y1++;
		camera.zoom = originalZoom;
		camera.update();
		
		renderCamera = camera;
		ShaderProgram shader = this.shader;
		if (camSys.zoomedOut){
			renderCamera = zoomOutCamera;
			shader = null;
		} 
		renderCamera.update();
		batch.setProjectionMatrix(renderCamera.combined);
		
		normalTexture.bind(1);
		diffTexture.bind(0);
		
		boolean setAllDirty = false;
		if (skipDraw != skippedDraw){
			setAllDirty = true;
		}
		
		x0 -= 1;//meh, dupe rendering doesn't work without this4
		start();
		int count = 0;
		{
			
			for (int i = 0; i < entities.size(); i++){
				Entity e = entities.get(i);
				Map map = mapM.get(e);
				//camera.zoom = Math.min(originalZoom, 10f);
				int[] tiles = map.tiles, backTiles = map.backTiles;
				batch.setProjectionMatrix(renderCamera.combined);
				if (Gdx.input.isKeyPressed(Input.Keys.P))map.setDirtyAll();
				map.cache.beginDraw(skipDraw, batch, lights);
				map.cache.beginDrawBack(lights);
				//if (false)                                                           
				endbp:
				{
					for (int x = x0; x <= x1; x++){
						for (int y = y0; y <= y1; y++){
							map.cache.draw(map, x, y, tiles, backTiles, renderCamera, lights, buffer, setAllDirty, shader, 0, batch, indexBuffer, atlasTexture);
							//if (count++ > 6) break endbp;
							int dupeOffset = OverworldSystem.SCROLLING_MAP_WIDTH* OverworldSystem.SCROLLING_MAP_TOTAL_SIZE * Main.PPM;
							if (map.duplicateRenderL){
								//map.cache.draw(map, x, y, tiles, backTiles, renderCamera, lights, buffer, setAllDirty, shader, -dupeOffset);
							}
							if (map.duplicateRenderR){
								//map.cache.draw(map, x, y, tiles, backTiles, renderCamera, lights, buffer, setAllDirty, shader, dupeOffset);
							}
						}
					}

				}

				map.cache.endDraw();
			
				if (skipDraw){
					batch.end();
				}
				batch.setShader(null);
				Texture t = indexBuffer.getColorBufferTexture();
				batch.enableBlending();
				batch.getProjectionMatrix().setToOrtho2D(0,  0, t.getWidth(), t.getHeight());
				batch.begin();
				batch.draw(t, 0, 0);
				batch.end();
			}
			camera.zoom = originalZoom;
			camera.update();
			renderCamera.update();
			skippedDraw = skipDraw;

		}

	}

	private void start() {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, indexBuffer.getWidth(), indexBuffer.getHeight());
		indexBuffer.begin();
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setShader(null);
		batch.begin();
		batch.draw(indexTexture, 0, 0);
		batch.end();
		batch.setShader(coefficientsShader);
		lights.setUniformsNew(coefficientsShader, shader, positionShader);
		batch.begin();
		//any texture
		batch.draw(indexTexture, 0, 2, indexBuffer.getWidth(), 1);
		batch.end();

		batch.setShader(positionShader);
		//lights.setUniformsNew(coefficientsShader, shader, positionShader);
		batch.begin();
		//any texture
		batch.draw(indexTexture, 0, 3, indexBuffer.getWidth(), 1);
		batch.end();

		batch.setShader(lightRampShader);
		//lights.setUniformsNew(coefficientsShader, shader, positionShader);
		batch.begin();
		//any texture
		batch.draw(indexTexture, 0, 4, indexBuffer.getWidth(), 1);
		batch.end();

		//batch.setShader(fxSshader);
		//batch.begin();
		//any texture
		//batch.draw(indexTexture, 0, 3, indexBuffer.getWidth(), indexBuffer.getHeight()-3);
		//batch.end();
		indexBuffer.end();
	}

	@Override
	public void entityAdded(Entity e) {
		Map map = mapM.get(e);
		
		
	}

	@Override
	public void entityRemoved(Entity entity) {
		
	}

	@Override
	public void dispose() {
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			Map map = mapM.get(e);
			engine.removeEntity(e);//prob not necessary	
		}
		
	}

	
}