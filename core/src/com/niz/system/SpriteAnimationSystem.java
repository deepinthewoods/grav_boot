package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.Data;
import com.niz.Main;
import com.niz.anim.AnimationContainer;
import com.niz.anim.AnimationLayer;
import com.niz.anim.SpriteCacheNiz;
import com.niz.component.Body;
import com.niz.component.DragOption;
import com.niz.component.Light;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;
import com.niz.component.SpriteIsMapTexture;
import com.niz.component.SpriteStatic;
import com.niz.component.VectorInput;
import com.niz.item.Item;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class SpriteAnimationSystem extends RenderSystem implements Observer, EntityListener {
private static final String TAG = "sprite anim system";

private static final Vector3 LIGHT_POS = new Vector3(.5f,.5f, 1f/200f);

public ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
public ComponentMapper<SpriteAnimation> spriteM = ComponentMapper.getFor(SpriteAnimation.class);
public ComponentMapper<SpriteStatic> spriteStaticM = ComponentMapper.getFor(SpriteStatic.class);
protected ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
private static ComponentMapper<DragOption> dragM = ComponentMapper.getFor(DragOption.class);

private SpriteBatchN batch;
//protected ImmutableArray<Entity> entities;
private OrthographicCamera camera;
private ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
protected MapRenderSystem map;
private SpriteBatchN leftBatch;

private ShaderProgram shader, lShader;

private Texture uvTexture;
private Texture diffTexture;

private LightRenderSystem lights;

protected ImmutableArray<Entity> physicsEntities;

private ImmutableArray<Entity> staticEntities;

private ImmutableArray<Entity> allEntities;

private ImmutableArray<Entity> staticMapEntities;

private Texture mapDiff;

private Texture mapNormal;

private ImmutableArray<Entity> staticBodyMapEntities;

private ImmutableArray<Entity> staticGibMapEntities;

private ImmutableArray<Entity> dragEntities;

private CameraSystem camSys;

private Sprite square;

	public static final Color[] LAYER_COLORS = new Color[LightRenderSystem.N_LAYERS];
	private Texture indexTexture;
	public static Texture atlasTexture;
	public static  FrameBuffer indexBuffer;
	private ShaderProgram coefficientsShader;
	private ShaderProgram positionShader;
	private ShaderProgram lightRampShader;

	public SpriteAnimationSystem(OrthographicCamera gameCamera, SpriteBatchN batch,
							  Texture diff, Texture normal, LightRenderSystem lights, Texture mapDiff, Texture mapNormal) {
	this.lights = lights;
	this.batch = batch;
	this.camera = gameCamera;
	this.leftBatch = batch;
	this.diffTexture = diff;
	this.uvTexture = normal;
	this.mapDiff = mapDiff;
	this.mapNormal = mapNormal;
	Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
	pixmap.setColor(Color.WHITE);
	pixmap.drawPixel(0, 0);
	pixmap.fill();
	
	square = new Sprite(new Texture(pixmap));
	for (int i = 0; i < LAYER_COLORS.length; i++){
		LAYER_COLORS[i] = new Color((float)(i + .5f) / 128f, 0f, 0f, 1f);

	}
}

@Override
public void addedToEngine(Engine engine) {
	ShaderSystem shaderSys = engine.getSystem(ShaderSystem.class);
	//spriteShader = shaderSys.spriteShader;
	//backShader = shaderSys.backShader;
	shader = shaderSys.charShader;
	lShader = shaderSys.lShader;
	coefficientsShader = shaderSys.coeffsShader;
	positionShader = shaderSys.posShader;
	lightRampShader = shaderSys.lightRampShader;
	//batch.setShader(shader);
	//leftBatch.setShader(lShader);
	
	Family physFam = Family.all(Position.class, SpriteAnimation.class).exclude(DragOption.class).get();
	physicsEntities = engine.getEntitiesFor(physFam);
	Family statFam = Family.all(Position.class, SpriteStatic.class).exclude(SpriteIsMapTexture.class).get();
	staticEntities = engine.getEntitiesFor(statFam);
	Family statMFam = Family.all(Position.class, SpriteStatic.class, SpriteIsMapTexture.class).exclude(Body.class).get();
	staticMapEntities = engine.getEntitiesFor(statMFam);
	Family statBodyMFam = Family.all(Position.class, SpriteStatic.class, SpriteIsMapTexture.class, Body.class).exclude(Item.class).get();
	staticBodyMapEntities = engine.getEntitiesFor(statBodyMFam);
	
	Family statBodyMGibFam = Family.all(Position.class, SpriteStatic.class, SpriteIsMapTexture.class, Body.class, Item.class).get();
	staticGibMapEntities = engine.getEntitiesFor(statBodyMGibFam);
	
	
	Family allFam = Family.all(Position.class, SpriteAnimation.class).exclude(SpriteStatic.class, DragOption.class).get();
	allEntities = engine.getEntitiesFor(allFam);
	Family dragFam = Family.all(Position.class, SpriteAnimation.class, DragOption.class).exclude(SpriteStatic.class).get();
	dragEntities = engine.getEntitiesFor(dragFam);
	//physicsEntities = engine.getEntitiesFor(Family.getFor(Position.class, SpriteAnimation.class, Physics.class));
	map = engine.getSystem(MapRenderSystem.class);
	camSys = engine.getSystem(CameraSystem.class);
	//Family family = new Family();
	//Gdx.app.log(TAG, "added "+(staticEntities == null));
	//Family.all;
	((EngineNiz) engine).getSubject("resize").add(this);;
	engine.addEntityListener(Family.one(SpriteAnimation.class).get(), this);

	indexTexture = new Texture(Gdx.files.internal("playerindexTexture.png"));
	atlasTexture = new Texture(Gdx.files.internal("playerprocessed.png"));
	indexBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 128, MapRenderSystem.INDEX_BUFFER_HEIGHT, false);
	indexBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
}

@Override
public void removedFromEngine(Engine engine) {
	
}
Vector2 v = new Vector2();
private Vector2 v3 = new Vector2();

private Vector2 tmpV = new Vector2(), zeroVector = new Vector2();;

private float viewportSize;

	private void start() {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, indexBuffer.getWidth(), indexBuffer.getHeight());
		indexBuffer.begin();
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setShader(null);

		batch.enableBlending();
		batch.setColor(Color.WHITE);
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
public void update(float deltaTime) {
	
	//if (true) return;
	start();
	if (camSys.zoomedOut) return;
	
	//TODO dt += Main.accum;
	//if (map == null) return;
	batch.setProjectionMatrix(map.camera.combined);
	batch.setColor(Color.WHITE);
	float sc = Gdx.graphics.getWidth()/viewportSize;
	sc *= 2;
	sc = 1f;
	//batch.setShader(null);;
	//leftBatch.setShader(null);
	batch.getProjectionMatrix().scale(sc, sc, sc);
	

	Gdx.gl.glDisable(GL20.GL_BLEND);
	batch.setShader(shader);
	//batch.setShader(null);
	batch.begin();

	//processMap();
	
	batch.end();

	//mapNormal.bind(1);
	//mapDiff.bind(0);
	batch.begin();
	//lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_RIGHT, shader, null);
	//batch.render();
	batch.end();
	//uvTextureLeft.bind(1);
	//diffTexture.bind(0);
	//leftBatch.enableBlending();


	////////////////////////////////////////////////////////////////////////////////////////////

	batch.enableBlending();
	Gdx.gl.glEnable(GL20.GL_BLEND);
	batch.setShader(shader);
	//batch.setShader(null);
	batch.begin();
	indexBuffer.getColorBufferTexture().bind(1);
	//indexTexture.bind(1);
	shader.setUniformi("u_index_texture", 1); //passing first texture!!!
	atlasTexture.bind(0);
	shader.setUniformi("u_texture", 0);
	processSprites();

	batch.end();
	batch.disableTextureBinding();
	batch.begin();
	map.indexBuffer.getColorBufferTexture().bind(1);
	//indexTexture.bind(1);
	shader.setUniformi("u_index_texture", 1); //passing first texture!!!
	map.atlasTexture.bind(0);
	shader.setUniformi("u_texture", 0);
	processMap();

	batch.end();
	batch.enableTextureBinding();
	//shader.begin();
	//shader.end();
	//batch.enableBlending();
	//batch.begin();
	//uvTexture.bind(1);
	//diffTexture.bind(0);
	//lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_RIGHT, batch.getShader());
	//batch.render();
	//batch.end();

	batch.setShader(null);
	Texture t = indexBuffer.getColorBufferTexture();
	batch.enableBlending();
	batch.getProjectionMatrix().setToOrtho2D(0,  0, t.getWidth(), t.getHeight());
	batch.begin();
	batch.draw(t, 0, 0);
	batch.end();

}

public void processSprites() {
	//Gdx.app.log(TAG, "process phys:"+physicsEntities.size() + "  static:");
	
	///////////////////////////////////////////////
	int layerI = Light.CHARACTER_SPRITES_LAYER_RIGHT;


	for (int i = 0; i < dragEntities.size(); i++){
		Entity e  = dragEntities.get(i);
		SpriteAnimation spr = spriteM.get(e);
		DragOption drag = dragM.get(e);
		if (!spr.hasStarted) continue;
		Position pos = posM.get(e);
		AnimationContainer parent =  spr.currentAnim;
		//Gdx.app.log(TAG, "process dr "+parent.layers.size);
		for (int index = 0; index < parent.layers.size; index++)
		{
			AnimationContainer container = parent;
			AnimationLayer layer;// = parent.layers[index];
			if (spr.overriddenAnim.get(index)){
				container = spr.overriddenAnimationLayers[index];
				layer = container.layers.get(0);
			}
			else layer = parent.layers.get(index);
			if (layer == null) {continue;}
			AtlasSprite s;
			int frame = spr.frameIndices[index];
			boolean left = spr.adjustedLeft[index];
			if (frame == 9012920) throw new GdxRuntimeException("layer not animated " 
					+ index + " " + Data.getString(spr.layerIDs.get(index))
					);
			s = (AtlasSprite) layer.getKeyFrame(frame, left);
			Vector2 p = layer.offsets[frame];
			
			int guideLayer = spr.layerSources[index];
			//guideLayer = 0;
			Vector2 g = spr.guides.get(guideLayer);
			v3.set(g.x, g.y);
			v3.add(0+(left?p.x-s.getAtlasRegion().originalWidth/(float)Main.PPM:-p.x), -p.y).add(pos.pos);
			v3.scl(16f);
			s.setPosition((int)v3.x, (int)v3.y);
			s.setColor(Data.colorFloats[spr.colors[index]]);
			if (drag == null) throw new GdxRuntimeException("dsfa");
			if (drag.disabled){
				//Gdx.app.log(TAG, "disabled drago" + e.getId());
				continue; 
			}
			SpriteBatchN theBatch = left?leftBatch:batch;
			draw(s, theBatch, left, e, layerI);
			if (!drag.multiDraw) continue;
			s.setPosition(s.getX() + drag.spacing * Main.PPM, s.getY());
			draw(s, theBatch, left, e, layerI);
			s.setPosition(s.getX() - drag.spacing * 2 * Main.PPM, s.getY());
			draw(s, theBatch, left, e, layerI);
		}
	}
	
	//Gdx.app.log(TAG, "update "+(staticEntities == null));
	if (staticEntities != null){
		for (int i = 0; i < staticEntities.size(); i++){
			Entity e  = staticEntities.get(i);
			SpriteStatic spr = spriteStaticM.get(e);
			Body body = bodyM.get(e);
			Vector2 p = posM.get(e).pos;
			v3.set(p.x - body.width, p.y-body.height);
			map.camera.update();
			v3.scl(16);
			spr.s.setPosition((int)(v3.x), (int)v3.y);
			//Gdx.app.log(TAG, "draw "+v3.x+" , "+v3.y);
			SpriteBatchN theBatch = spr.left?leftBatch:batch;
			draw(spr.s, theBatch, spr.left, e, layerI);
		}
	}
	
	for (int i = 0; i < physicsEntities.size(); i++){
		Entity e  = physicsEntities.get(i);
		SpriteAnimation spr = spriteM.get(e);
		if (!spr.hasStarted) continue;
		Position pos = posM.get(e);
		AnimationContainer parent =  spr.currentAnim;
		//Gdx.app.log(TAG, "process phy "+e.getId());
		//Gdx.app.log(TAG, "process phy "+pos.pos.y * Main.PPM);

		for (int index = 0; index < parent.layers.size; index++)
		{
			AnimationContainer container = parent;
			AnimationLayer layer;// = parent.layers[index];
			if (spr.overriddenAnim.get(index)){
				container = spr.overriddenAnimationLayers[index];
				layer = container.layers.get(0);
			}
			else layer = parent.layers.get(index);
			if (layer == null) {continue;}
			AtlasSprite s;
			int frame = spr.frameIndices[index];
			boolean left = spr.adjustedLeft[index];
			s = (AtlasSprite) layer.getKeyFrame(frame, left);
			Vector2 p = layer.offsets[frame];
			
			int guideLayer = spr.layerSources[index];
			//guideLayer = 0;
			Vector2 g = spr.guides.get(guideLayer);
			v3.set(g.x, g.y);
			v3.add(0+(left?p.x-s.getAtlasRegion().originalWidth/(float)Main.PPM:-p.x), -p.y).add(pos.pos);
			v3.scl(16f);
			s.setPosition((int)v3.x, (int)v3.y);
			s.setColor(Data.colorFloats[spr.colors[index]]);
			SpriteBatchN theBatch = left?leftBatch:batch;
			
			draw(s, theBatch, left, e, layerI);
		}
	}
}

public void processMap(){
	int layer = Light.MAP_FOREGROUND_LAYER;
	if (staticMapEntities != null){
		for (int i = 0; i < staticMapEntities.size(); i++){
			Entity e  = staticMapEntities.get(i);
			SpriteStatic spr = spriteStaticM.get(e);
			//Body body = bodyM.get(e);
			Vector2 p = posM.get(e).pos;
			//v3.set(p.x - body.width, p.y-body.height);
			v3.set(p.x, p.y);
			map.camera.update();
			v3.scl(Main.PPM);

			spr.s.setPosition((int)(v3.x), (int)v3.y);
			//Gdx.app.log(TAG, "draw "+v3.x+" , "+v3.y);
			SpriteBatchN theBatch = spr.left?leftBatch:batch;
			draw(spr.s, theBatch, spr.left, e, layer);
			
		}
		
	}
	
	if (staticGibMapEntities != null){
		for (int i = 0; i < staticGibMapEntities.size(); i++){
			Entity e  = staticGibMapEntities.get(i);
			SpriteStatic spr = spriteStaticM.get(e);
			Body body = bodyM.get(e);
			Vector2 p = posM.get(e).pos;
			v3.set(p.x - body.width, p.y-body.height);
			map.camera.update();
			v3.scl(Main.PPM);

			spr.s.setPosition((int)(v3.x), (int)v3.y);
			//Gdx.app.log(TAG, "draw gib "+v3.x+" , "+v3.y + spr.left);
			SpriteBatchN theBatch = spr.left?leftBatch:batch;
			draw(spr.s, theBatch, spr.left, e, layer);
			
		}
		
	}
	
	if (staticBodyMapEntities != null){
		for (int i = 0; i < staticBodyMapEntities.size(); i++){
			Entity e  = staticBodyMapEntities.get(i);
			SpriteStatic spr = spriteStaticM.get(e);
			Body body = bodyM.get(e);
			Vector2 p = posM.get(e).pos;
			AtlasSprite os = (AtlasSprite) spr.s;
			AtlasRegion ar = os.getAtlasRegion();
			float fx = ar.offsetX + ar.packedWidth/2;
			float fy = ar.offsetY;// + ar.packedHeight/2;

			v3.set(p.x - fx * Main.PX , p.y - fy * Main.PX - body.height);
			//v3.set(p.x, p.y);
			map.camera.update();
			//Gdx.app.log(TAG, "draw static body "+v3.x+" , "+v3.y + "   ---  " + fy);
			v3.scl(Main.PPM);

			spr.s.setPosition((int)(v3.x), (int)v3.y);
			SpriteBatchN theBatch = spr.left?leftBatch:batch;
			draw(spr.s, theBatch, spr.left, e, layer);
			
		}
		
	}
}

public void draw(Sprite s, SpriteBatchN theBatch, boolean left, Entity entity, int layer) {
	//Gdx.app.log(TAG, "layer " + layer);
	s.setColor(LAYER_COLORS[layer]);
	s.setColor(Color.WHITE);
	s.setTexture(atlasTexture);
	s.draw(theBatch);
}

public void drawLowLOD(){
	batch.setProjectionMatrix(camSys.adjustedCamera.combined);	
	float sc = Gdx.graphics.getWidth()/viewportSize;
	sc *= 2;
	sc = 1f;
	//batch.setColor(Color.RED);

	batch.getProjectionMatrix().scale(sc, sc, sc);batch.begin();
	
	processSpritesLowLOD();
	
	
	
	
	////////////////////////
	
	batch.end();
	batch.setShader(null);
	//uvTexture.bind(1);
	square.getTexture().bind(0);
	batch.begin();
	//lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_RIGHT, batch.getShader());
	//batch.render();
	batch.end();
	batch.setShader(shader);
	
}

private void processSpritesLowLOD() {

	//Gdx.app.log(TAG, "process phys:"+physicsEntities.size() + "  static:");
	int layer = Light.CHARACTER_SPRITES_LAYER_RIGHT;

	///////////////////////////////////////////////
	for (int i = 0; i < physicsEntities.size(); i++){
		Entity e  = physicsEntities.get(i);
		SpriteAnimation spr = spriteM.get(e);
		if (!spr.hasStarted) continue;
		Position pos = posM.get(e);
		AnimationContainer parent =  spr.currentAnim;
		v3.set(pos.pos.x, pos.pos.y);
		v3.scl(Main.PPM);
		int w = Main.PPM ;
		int h = Main.PPM ;
		v3.add(-w / 2, spr.guides.get(0).y);
		square.setSize(w, h);
		square.setPosition((int)v3.x,  (int)v3.y);
		//Gdx.app.log(TAG, "process phy "+square.getX() + "," + square.getY());
		square.setColor(Color.RED);
		draw(square, batch, spr.left, e, layer);
		//batch.setColor(Color.WHITE);
	}
	square.setColor(Color.BLUE);

	for (int i = 0; i < dragEntities.size(); i++){
		Entity e  = dragEntities.get(i);
		SpriteAnimation spr = spriteM.get(e);
		DragOption drag = dragM.get(e);
		if (!spr.hasStarted) continue;
		Position pos = posM.get(e);
		AnimationContainer parent =  spr.currentAnim;
		//Gdx.app.log(TAG, "process phy "+parent.layers.size);
		v3.set(pos.pos.x, pos.pos.y);
		v3.scl(Main.PPM);
		int w = Main.PPM;
		int h = Main.PPM;
		square.setSize(w, h);
		square.setPosition((int)v3.x,  (int)v3.y);
		draw(square, batch, spr.left, e, layer);
	}
	//batch.setColor(Color.WHITE);

	//Gdx.app.log(TAG, "update "+(staticEntities == null));
	if (staticEntities != null){
		for (int i = 0; i < staticEntities.size(); i++){
			Entity e  = staticEntities.get(i);
			SpriteStatic spr = spriteStaticM.get(e);
			Body body = bodyM.get(e);
			Vector2 p = posM.get(e).pos;
			//Gdx.app.log(TAG, "draw "+v3.x+" , "+v3.y);
			v3.set(p.x, p.y);
			v3.scl(Main.PPM);
			int w = Main.PPM;
			int h = Main.PPM;
			square.setSize(w, h);
			square.setPosition((int)v3.x,  (int)v3.y);
			draw(square, batch, spr.left, e, layer);
		}
	}

	//batch.setColor(Color.WHITE);

	
	if (staticMapEntities != null){
		for (int i = 0; i < staticMapEntities.size(); i++){
			Entity e  = staticMapEntities.get(i);
			SpriteStatic spr = spriteStaticM.get(e);
			//Body body = bodyM.get(e);
			Vector2 p = posM.get(e).pos;
			//v3.set(p.x - body.width, p.y-body.height);
			v3.set(p.x, p.y);
			//map.camera.update();
			v3.scl(Main.PPM);
			int w = Main.PPM;
			int h = Main.PPM;
			square.setSize(w, h);
			square.setPosition((int)v3.x,  (int)v3.y);
			draw(square, batch, spr.left, e, layer);
			
			
		}
		
	}
	//batch.setColor(Color.WHITE);

	if (staticGibMapEntities != null){
		for (int i = 0; i < staticGibMapEntities.size(); i++){
			Entity e  = staticGibMapEntities.get(i);
			SpriteStatic spr = spriteStaticM.get(e);
			Body body = bodyM.get(e);
			Vector2 p = posM.get(e).pos;
			v3.set(p.x - body.width, p.y-body.height);
			//map.camera.update();
			v3.scl(Main.PPM);
			
			//int w = (int)((spr.s.getWidth()* Main.PX + 1)) * Main.PPM, h = (int)((spr.s.getHeight()* Main.PX + 1)) * Main.PPM;
			int w = Main.PPM;
			int h = Main.PPM;
			square.setSize(w, h);
			square.setPosition((int)v3.x,  (int)v3.y);
			draw(square, batch, false, e, layer);
			
		}
		
	}
	//batch.setColor(Color.WHITE);

	if (staticBodyMapEntities != null){
		for (int i = 0; i < staticBodyMapEntities.size(); i++){
			Entity e  = staticBodyMapEntities.get(i);
			SpriteStatic spr = spriteStaticM.get(e);
			Body body = bodyM.get(e);
			Vector2 p = posM.get(e).pos;
			AtlasSprite os = (AtlasSprite) spr.s;
			AtlasRegion ar = os.getAtlasRegion();
			float fx = ar.offsetX + ar.packedWidth/2;
			float fy = ar.offsetY;// + ar.packedHeight/2;

			v3.set(p.x - fx * Main.PX , p.y - fy * Main.PX - body.height);
			//v3.set(p.x, p.y);
			//map.camera.update();
			//Gdx.app.log(TAG, "draw static body "+v3.x+" , "+v3.y + "   ---  " + fy);
			v3.scl(Main.PPM);

			//v3.set(pos.pos.x, pos.pos.y);
			//v3.scl(Main.PPM);
			int w = Main.PPM;
			int h = Main.PPM;
			square.setSize(w, h);
			square.setPosition((int)v3.x,  (int)v3.y);
			draw(square, batch, spr.left, e, layer);
			
		}
		
	}
	//batch.setColor(Color.WHITE);

	
}

@Override
public void onNotify(Entity e, Event event, Object c) {
	VectorInput in = (VectorInput) c;
	viewportSize = in.v.x;
	
}

@Override
public void entityAdded(Entity entity) {
	SpriteAnimation s = spriteM.get(entity);
	if (s == null) throw new GdxRuntimeException("nill");
	
}

@Override
public void entityRemoved(Entity entity) {
}



}
