package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.niz.Blocks;
import com.niz.LayerData;
import com.niz.SimplexNoise;
import com.niz.WorldDefinition;
import com.niz.action.Action;
import com.niz.action.ActionList;
import com.niz.actions.mapgen.AAgentBuildMap;
import com.niz.actions.mapgen.AGenerateEntities;
import com.niz.actions.mapgen.ALoadEntities;
import com.niz.actions.mapgen.ALoadMap;
import com.niz.actions.mapgen.ASaveEntities;
import com.niz.actions.mapgen.ASaveMap;
import com.niz.component.Agent;
import com.niz.component.Buckets;
import com.niz.component.Map;
import com.niz.component.PlaceAtStartPoint;
import com.niz.component.Player;
import com.niz.component.Position;

public class OverworldSystem extends RenderSystem implements EntityListener {
	public static final int SCROLLING_MAP_WIDTH = 256, SCROLLING_MAP_HEIGHT = 256;

	public static final int SCROLLING_MAP_TOTAL_SIZE = 1;

	private static final String TAG = "Overworld syatem";

	private static final int NEW_GAME_MAP_WIDTH = 256;

	private static final int NEW_GAME_HEIGHT = 256; 

	Map[] scrollingMaps = new Map[SCROLLING_MAP_TOTAL_SIZE];
	private Family family;
	private ImmutableArray<Entity> entities;
	private ComponentMapper<Map> mapM;
	static ComponentMapper<Player>playerM = ComponentMapper.getFor(Player.class);

	public int currentLevel = 0;
	
	private Bits loaded = new Bits(SCROLLING_MAP_TOTAL_SIZE), shouldLoad = new Bits(SCROLLING_MAP_TOTAL_SIZE), tmpBits = new Bits(SCROLLING_MAP_TOTAL_SIZE), loading = new Bits(SCROLLING_MAP_TOTAL_SIZE), saving = new Bits(SCROLLING_MAP_TOTAL_SIZE);
	Pool<Map> mapPool = new Pool<Map>(){

		@Override
		protected Map newObject() {
			// TODO Auto-generated method stub
			return new Map(OverworldSystem.SCROLLING_MAP_WIDTH, OverworldSystem.SCROLLING_MAP_HEIGHT, atlas, shader, backShader, litShader, fgShader);
		}

		@Override
		public Map obtain() {
			
			Map map = super.obtain();
			if (!map.free) throw new GdxRuntimeException("map nto free");
			map.free = false;
			return map;
		}

		@Override
		public void free(Map object) {
			//if (object.free | true) throw new GdxRuntimeException("map nto free");

			object.free = true;
			super.free(object);
		}
		
		
		
	};

	private MapSystem mapSystem;

	private Family entityFamily;

	private ComponentMapper<Position> posM;

	private EngineNiz engine;

	int currentZ = 5;

	//private float[] parallaxZOffsets = {1f, 2f, 4f, 8f, 16f, 32f};

	private WorkerSystem workSys;

	private ImmutableArray<Entity> positionEntities;

	private TextureAtlas atlas;

	private ImmutableArray<Entity> playerEntities;;
	
	public OverworldSystem(TextureAtlas atlas){
		this.atlas = atlas;
	}
	
	public Map getMapFor(int x, int y) {
		if (x < 0) return null;
		if (x > SCROLLING_MAP_WIDTH * SCROLLING_MAP_TOTAL_SIZE) return null;
		int key = (int) (x) / SCROLLING_MAP_WIDTH;
		key %= scrollingMaps.length;
		Map map = scrollingMaps[key];
		if (map == null) return null;
		if (y >= map.offset.y +map.height || y < map.offset.y) return null;//TODO look at other chunks
		return map;
	}
	@Override
	public void addedToEngine(Engine engine) {
		family = Family.one(Map.class).get();
		entityFamily = Family.all(Position.class, Player.class).get();

		entities = engine.getEntitiesFor(entityFamily);
		mapM = ComponentMapper.getFor(Map.class);
		posM = ComponentMapper.getFor(Position.class);
		//mapM = ComponentMapper.getFor(Map.class);

		engine.getSystem(OnMapSystem.class).overworld = this;
		//engine.addEntityListener(family, this);

		playerEntities = engine.getEntitiesFor(Family.all(Player.class).get());
		
		Pools.set(Map.class, mapPool);
		
		mapSystem = engine.getSystem(MapSystem.class);
		mapSystem.overworld = this;
		this.engine = (EngineNiz) engine;
		//saveThread.resume();
		//saveThread.onResume();
		workSys = engine.getSystem(WorkerSystem.class);
		positionEntities = engine.getEntitiesFor(Family.one(Position.class).get());
		
		
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		//if (true) return;
		//Gdx.app.log(TAG,  "update"+entities.size());
		updateLoad(entities);
		
		
	}
	
	

	private void updateLoad(ImmutableArray<Entity> entityList) {
		shouldLoad.clear();
		if (entityList.size() == 0){
			Gdx.app.log(TAG,  "not update");
			if (!loaded.get(0) && !loading.get(0)  )startMap(0, engine);	
			return;
		}
		for (int i = 0; i < entityList.size(); i++){
			Entity e = entityList.get(i);
			Position p = posM.get(e);
			//Vector2 pos = p.pos;
			int xChunk = (int) p.pos.x;
			xChunk /= (SCROLLING_MAP_WIDTH /4);
			int ind = xChunk/4;
			if (ind >= 0 && ind < scrollingMaps.length)
				shouldLoad.set(ind);
			boolean left = false;
			boolean right = false;
			switch (xChunk % 4){
			case 0:left=true;break;
			case 1:left=true;right=true;break;
			case 2:left=true;right=true;break;
			case 3:right=true;break;
			}
			if (left){//left
				int leftInd = xChunk/4-1;
				if (leftInd >= 0 && leftInd < scrollingMaps.length)
					shouldLoad.set(leftInd);
			} 
			if (right){//right
				int rightInd = xChunk/4+1;
				if (rightInd >= 0 && rightInd < scrollingMaps.length)
					shouldLoad.set(rightInd);
			}
		}
		tmpBits.clear();
		tmpBits.or(loaded);
		tmpBits.andNot(shouldLoad);
		removeChunks(tmpBits);
		
		shouldLoad.andNot(loaded);
		shouldLoad.andNot(loading);
		shouldLoad.andNot(saving);
		
		//Gdx.app.log(TAG,  "update");
		while (shouldLoad.nextSetBit(0) != -1){
			Gdx.app.log(TAG,  "start to load ");
			int bit = shouldLoad.nextSetBit(0);
			shouldLoad.clear(bit);
			startMap(bit, engine);	
		}
	}
	private void createSaveMapEntity(Map map, EngineNiz engine, Entity e, int bit) {
		//Gdx.app.log(TAG,  "CREATE SAVEAGENT");
		
		
		ActionList act = Pools.obtain(ActionList.class);
		
		ASaveMap abuild = Pools.obtain(ASaveMap.class);
		abuild.bit = bit;
		abuild.map = map;
		abuild.def = worldDef;
		abuild.z = currentZ;
		abuild.after = createEntitySaveAction(map, bit);
		act.addToStart(abuild);
		
		e.add(act);
		
	}

	public static Object saveListLock = new Object();;
	
	private void removeChunks(Bits bits) {
		
		while (bits.nextSetBit(0) != -1){
			//if (true)throw new GdxRuntimeException("jhkl");
			int bit = bits.nextSetBit(0);
			Map map = scrollingMaps[bit];
			scrollingMaps[bit] = null;
			loaded.clear(bit);
			if (map == null) return;//throw new GdxRuntimeException("nuill engine");
			
			engine.removeEntityNoPool((PooledEntity) map.mapEntity);
			map.mapEntity = null;
			
			synchronized (saveListLock){
				//Gdx.app.log(TAG, "ADD TO SAVE LIST");
				//shouldSave.add(map);
				//if (map.mapEntity == null) throw new GdxRuntimeException("null mapE"+map.offset);
				saving.set(bit);
				Entity saver = engine.createEntity();
				createSaveMapEntity(map, engine, saver, bit);
				workSys.addWorker(saver);
				
				
				
				
				
				//engine.addEntity(saver);
				//if (map.mapEntity == null) throw new GdxRuntimeException("null mapE"+map.offset);
				//engine.removeEntity(map.mapEntity);
				map.mapEntity = null;
			}
		}	
	}
	
	private Array<PooledEntity> saveEntities = new Array<PooledEntity>();
	private Class entityArrayClass = saveEntities.getClass();
	private ASaveEntities createEntitySaveAction(Map map, int bit) {
		saveEntities = Pools.obtain(entityArrayClass);
		removeEntitiesForPosition(map.offset, map.width, map.height, saveEntities);
		ASaveEntities abuild = new ASaveEntities();
		abuild.bit = bit;
		abuild.entities = saveEntities;
		abuild.map = map;
		abuild.def = worldDef;
		return abuild;
	}
	private AGenerateEntities createEntityGenerationAgent(
			 Map map, int x, int y, int w, int h) {
		AGenerateEntities abuild = new AGenerateEntities();
		abuild.x = x;
		abuild.y = y;
		abuild.w = w;
		abuild.h = h;
		abuild.map = map;
		abuild.def = worldDef;
		return abuild;
	}
	private Action createEntityDeserializationAgent(
			 Map map, int bit, FileHandle mapFile) {
		ALoadEntities abuild = new ALoadEntities();
		abuild.bit = bit;
		abuild.map = map;
		abuild.def = worldDef;
		return abuild;
	}

	private void removeEntitiesForPosition(Vector2 offset, int width, int height,
			Array<PooledEntity> list) {
		list.clear();
		for (int i = 0; i < positionEntities.size(); i++){
			Entity e = positionEntities.get(i);
			Vector2 pos = posM.get(e).pos;
			if (pos.x > offset.x && pos.y > offset.y && pos.x < offset.x + width && pos.y < offset.y + height && playerM.get(e) == null){
				engine.removeEntityNoPool((PooledEntity) e);
				list.add((PooledEntity) e);
			}
		}
	}

	//Array<Map> shouldSave = new Array<Map>();
	//Array<Map> saving = new Array<Map>();
	
	@Override
	public void entityAdded(Entity e) {
		//Gdx.app.log(TAG,  "added");
		Map map = mapM.get(e);
		if (map.isScrolling){
			int key = (int) (map.offset.x) / SCROLLING_MAP_WIDTH;
			key %= scrollingMaps.length;
			scrollingMaps[key] =  map;
		}
	}
	@Override
	public void entityRemoved(Entity e) {
		//Gdx.app.log(TAG,  "rem");
		Map map = mapM.get(e);
		if (map.isScrolling){
			int key = (int) (map.offset.x) / SCROLLING_MAP_WIDTH;
			key %= scrollingMaps.length;
			scrollingMaps[key] =  null;
		}
		
	}
	
	private void createMapGenerationAgent(PooledEntity e, EngineNiz engine, Map map, int bit) {
		Gdx.app.log(TAG,  "create gen agent " + bit);
		Position pos = engine.createComponent(Position.class);
		e.add(pos);
		pos.pos.set(0,0);
		
		//e.add(engine.createComponent(SpriteAnimation.class).set(Animations.PLAYER));
		ActionList act = Pools.obtain(ActionList.class);
		
		AAgentBuildMap abuild = new AAgentBuildMap();
		abuild.bit = bit;
		abuild.map = map;
		abuild.z = currentZ;
		abuild.after = createEntityGenerationAgent(map, (int)map.offset.x, (int)map.offset.y, map.width, map.height);
		//map.e = e;
		act.addToStart(abuild);
		
		e.add(act);
		
		
		
	}
	
	private void createMapDeserializationAgent(PooledEntity e, EngineNiz engine, Map map, int bit, FileHandle mapFile) {
		//Gdx.app.log(TAG,  "CREATE DESERIALIZATIONAGENT");
		Position pos = engine.createComponent(Position.class);
		e.add(pos);
		pos.pos.set(0,0);
		
		//e.add(engine.createComponent(SpriteAnimation.class).set(Animations.PLAYER));
		ActionList act = Pools.obtain(ActionList.class);
		
		ALoadMap abuild = new ALoadMap();
		//abuild.seed = mapSystem.worldDef.seed;
		abuild.bit = bit;
		abuild.map = map;
		abuild.def = worldDef;
		abuild.z = currentZ;
		abuild.after = createEntityDeserializationAgent(map, bit, mapFile);
;
		//abuild.file = mapFile;
		//map.e = e;
		act.addToStart(abuild);
		
		e.add(act);
		e.add(new Agent());
		
	}

	public void startMap(int bit, EngineNiz engine){
		//if (bit != 0 || true) return;
		int x = bit * SCROLLING_MAP_WIDTH;
		FileHandle mapFile = worldDef.folder.child("map"+bit);
		if (mapFile.exists()){
			Map map = mapPool.obtain();
			//highest point
			
			int y = 0; 
			//Gdx.app.log(TAG, "start load"+x);
			map.offset.set(x, y);
			
			PooledEntity gen = engine.createEntity();
			createMapDeserializationAgent(gen, engine, map, bit, mapFile);
			workSys.addWorker(gen);
			
			
			
//			engine.addEntity(gen);
			loading.set(bit);
		}
		else {//generate
			//int x = bit * SCROLLING_MAP_WIDTH;
			Map map = mapPool.obtain();
			//map = new Map(SCROLLING_MAP_WIDTH, SCROLLING_MAP_HEIGHT);
			//highest point
			int y = 0; 
			//Gdx.app.log(TAG, "start gen"+x);
			map.offset.set(x, y);
			
			PooledEntity gen = engine.createEntity();
			createMapGenerationAgent(gen, engine, map, bit);
			workSys.addWorker(gen);
			
			
			
			//engine.addEntity(gen);
			loading.set(bit);
		
		}
		
	}
	
	public float getHeight(int x){
		return getHeight(x, 0, 1f);
	}
	public SimplexNoise simplexNoise;

	Array<Entity> eArr = new Array<Entity>();
	private ImmutableArray<Entity> entityList = new ImmutableArray<Entity>(eArr);

	public WorldDefinition worldDef;

	public ShaderProgram shader;

	public ShaderProgram backShader;

	public ShaderProgram fgShader;

	public ShaderProgram litShader;

	private Map newGameMap;

	private boolean newGameScreen;

	public float getHeight(int x, int z, float factor){ 
		if (true) return 4;
		if (x < 0 )return getHeight(0, z, factor) + x ;
		
		
		//float factor = ParallaxBackgroundSystem.LAYER_PARALLAX_FACTORS[z];
		if ( x / factor >= SCROLLING_MAP_TOTAL_SIZE * SCROLLING_MAP_WIDTH){
			//Gdx.app.log(TAG,  "jkld"+(- x/factor + (int) (SCROLLING_MAP_TOTAL_SIZE * SCROLLING_MAP_WIDTH -1)) + "   /  " + 
		//(x/factor)
		//);
			return getHeight((int) (SCROLLING_MAP_TOTAL_SIZE * SCROLLING_MAP_WIDTH * factor -1), z, factor)
					////(x/factor- SCROLLING_MAP_TOTAL_SIZE * SCROLLING_MAP_WIDTH*factor)
					-( x/factor - (int) (SCROLLING_MAP_TOTAL_SIZE * SCROLLING_MAP_WIDTH -1))*factor
					;
			
		}
			
		//int dz = z*5;
		//if (z == 0) dz = 0;
		float px = x;
		px /= factor;
		x = (int) px;
		//float oz = Math.min(1,  ParallaxBackgroundSystem.LAYER_PARALLAX_FACTORS[z]*3);
		//oz = 1;
		if (false && z != 0)
			z = (int) (currentZ + ParallaxBackgroundSystem.LAYER_PARALLAX_FACTORS[z] );
		//z = (int) (currentZ + z*31);
		
		float multiplier = 16f, xmultiplier = 1f/64f, zMultiplier = 1f/16f;
		float noise = 0f;
		
		LayerData data = worldDef.overworldLayers[z];
		int fx = x / SCROLLING_MAP_WIDTH;
		
		int height = lerp(data.heights[fx], data.heights[fx+1], x % SCROLLING_MAP_WIDTH);
		//seed = 0f;
		noise += simplexNoise.noise(x * xmultiplier, z * zMultiplier) * multiplier;
		
		noise += multiplier;
		xmultiplier /= 2f;
		multiplier /= 2f;	
		//noise += 5;
		
		return height * factor;//
		//(noise+128 ) * factor ;
	}
	private int lerp(int i, int j, int k) {
		int r = j * k + i * (SCROLLING_MAP_WIDTH - k-1);
		r /= SCROLLING_MAP_WIDTH*2;
		return r;
	}

	public void onFinishedMap(int bit, Map map) {
		//Gdx.app.log(TAG, "onfinishMap"+map.offset + " " + bit);

		loading.clear(bit);
		loaded.set(bit);
		scrollingMaps[bit] = map;
		Entity mapE = engine.createEntity();
		map.mapEntity = mapE;
		mapE.add(map);
		engine.addEntity(mapE);
		map.mapSystem = mapSystem;
		
		
		
	}
	public Map getMapForX(int x) {
		
		if (x < 0) return null;
		int key = (int) (x) / SCROLLING_MAP_WIDTH;
		key %= scrollingMaps.length;
		Map map = scrollingMaps[key];
		//dddddddddddGdx.app.log(TAG, "getMapForX"+x+" key:"+key);
		if (map == null) return null;
		//if (y >= map.offset.y +map.height || y < map.offset.y) return null;//TODO look at other chunks
		return map;
	}
	public void onFinishedSave(Map map, int bit) {
		saving.clear(bit);
		mapPool.free(map);
		
	}
	
	public void printHeights(){
		String s = "";
		
		for (int i = 0; i < 1000; i++){
			s += getHeight(i, 0, 1f);
			s += "\n";
		}
		
		//Gdx.app.log(TAG, s);
	}
	
	public void saveAllNow() {
		removeChunks(loaded);
	}
	public void startLoadingChunksFor(Array<PooledEntity> arr) {
		//Entity player = arr.get(0);
		//Player play = playerM.get(player);
		//Vector2 pos = posM.get(player).pos;
		//eArr.clear();
		//eArr.add(player);
		updateLoad(entityList);
		workSys.setWaitToAddEntities(true);;
		workSys.addEntities(arr);
	}
	public int size() {
		int s = 0, index = -1;
		do {
			index = loaded.nextSetBit(index++);
			if (index != -1) s++;
		} while (index != -1);
		return s;
	}

	public void setForNewGameScreen() {
		if (newGameScreen) throw new GdxRuntimeException("already on new game screen");
		newGameScreen = true;
		if (newGameMap == null){
			newGameMap = new Map(OverworldSystem.NEW_GAME_MAP_WIDTH, OverworldSystem.NEW_GAME_HEIGHT, atlas, shader, backShader, litShader, fgShader);
			for (int x = 0; x < 256; x++){
				for (int y = 0; y < 256; y++){
					newGameMap.setBG(x,  y, Blocks.STONE + MathUtils.random(64));
				}
			}
			for (int i = 0; i < NEW_GAME_MAP_WIDTH; i++){
				newGameMap.set(i,  0, Blocks.STONE + MathUtils.random(64));
				if (i <= AStar.PATHFINDING_X_START)newGameMap.set(i,  AStar.PATHFINDING_INITIAL_Y_OFFSET-1, Blocks.STONE + MathUtils.random(64));

			}
			for (int i = 0; i < 16; i++){
				newGameMap.set(0,  i + AStar.PATHFINDING_INITIAL_Y_OFFSET, Blocks.STONE + MathUtils.random(64));
			}
		}
		int bit = 0;
		loading.clear(bit);
		loaded.set(bit);
		scrollingMaps[bit] = newGameMap;
		Entity mapE = engine.createEntity();
		newGameMap.mapEntity = mapE;
		mapE.add(newGameMap);
		engine.addEntity(mapE);
		newGameMap.mapSystem = mapSystem;
		
	}
	public void stopNewGameScreen(){
		Gdx.app.log(TAG, "stop new game screen");
		newGameScreen = false;
		int bit = 0;
		Map map = scrollingMaps[bit];
		scrollingMaps[bit] = null;
		loaded.clear(bit);
		if (map == null) return;//throw new GdxRuntimeException("nuill engine");
		
		engine.removeEntityNoPool((PooledEntity) map.mapEntity);
		map.mapEntity = null;
		//changeLevel(1);
	}
	
	public void changeLevel(int newLevel){
		currentLevel = newLevel;
		removeChunks(loaded);
		//remove/queue re-placing of player
		Array<PooledEntity> arr = new Array<PooledEntity>();
		for (Entity e : playerEntities){
			//Control control = e.getComponent(Control.class);
			//if (control != null){
			//	e.remove(Control.class);
			//}
			//e.remove(Position.class);
			//Gdx.app.log(TAG, "rem player ent" + e.getId());

			//engine.removeEntityNoPool((PooledEntity) e);
			//arr.add((PooledEntity) e);
			//e.remove(Position.class);
			PlaceAtStartPoint placer = engine.createComponent(PlaceAtStartPoint.class);
			e.add(placer);
			e.remove(Buckets.class);
			e.add(engine.createComponent(Buckets.class));
			
		}
		
		workSys.setWaitToAddEntities(true);
		//workSys.addEntities(arr);;
	}

	public void changeToRoomEditor(WorldDefinition def) {
		if (def != worldDef) throw new GdxRuntimeException("fdjslk");
		changeLevel(0);
		
		
	}
}
