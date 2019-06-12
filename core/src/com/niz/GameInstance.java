package com.niz;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.AutoGibSystem;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.niz.action.ActionList;
import com.niz.action.ProgressAction;
import com.niz.actions.ANotRun;
import com.niz.actions.AStand;
import com.niz.anim.Animations;
import com.niz.anim.SpriteCacheNiz;
import com.niz.component.BooleanInput;
import com.niz.component.Control;
import com.niz.component.DragController;
import com.niz.component.Pathfind;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SelectedPlayer;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;
import com.niz.system.*;
import com.niz.ui.edgeUI.CharacterScreen;
import com.niz.ui.edgeUI.InventoryScreen;
import com.niz.ui.edgeUI.MainMenu;
import com.niz.ui.edgeUI.SettingsScreen;

public class GameInstance implements Screen, Observer {


	public static boolean unPause;
	SpriteBatchN batch;
	public EngineNiz engine;
	//private OrthographicCamera gameCamera;
	private OrthographicCamera uiCamera;
	
	public static float accum;
	private float deltaTime;
	private InputMultiplexer mux;
	//private Texture playerDiffuseTex;
	//private Texture playerNormalTex;
	//private Texture mapDiffuseTex;
	//private Texture mapNormalTex;
	private String[] numberStrings = new String[1000];
	private BitmapFont font;
	private OrthographicCamera camera;
	private Skin skin;
	private Stage stage;
	private PlayerInputSystem playerInput;
	private TextureAtlas uiAtlas;
	TextureAtlas playerAtlas;
	private TextureAtlas atlas;
	private static final String TAG = "game instance";

	public static boolean pixelScrolling = false;
	
	Factory factory;
	private boolean headless;
	private boolean isServer = true;
	private boolean isClient = true;
	private Viewport viewport;
	InventoryScreen invScreen;
	private ShapeRenderer shapeR;
	private Subject menuKeySubject;
	private Subject invRefreshSubject;
	private CharacterScreen charScreen;
	BooleanInput invToggleComponent = new BooleanInput();
	private MainMenu mainMenuScreen;
	private SettingsScreen settingsScreen;
	private Subject zoomSubject;
	private ZoomInput zoomInput;
	private SpriteBatchN mapBatch;
	private SpriteBatchN leftBatch;
	private SpriteBatchN rightBatch;
	private OrthographicCamera defaultCam;
	private LightUpdateSystem lights;
	private Texture logo;
	private ImmutableArray<Entity> playerEntities;
	private InputSystem inputSys;
	private static Vector3 tmpV = new Vector3();
	private ShaderSystem shaderSys;
    private LightRenderSystem lightRender = null;

    public void create (boolean headless, boolean newGame) {

		//Log.DEBUG();
//		GLProfiler.enable();
		final GameInstance inst = this;
		this.headless = headless;
		batch = new SpriteBatchN(50);
		uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		engine = new EngineNiz();
		logo = new Texture(Gdx.files.internal("logo.png"));
		final WorkerSystem workSys = new WorkerSystem();
		engine.addSystem(workSys);
		ProgressBarSystem progBar = new ProgressBarSystem(uiCamera, batch);
		progBar.priority = 1000000;
		engine.addSystem(progBar);
		engine.getSubject("worlddef").add(this);
		playerEntities = engine.getEntitiesFor(Family.all(Player.class, Position.class).get());
		inputSys = new InputSystem();
		inputSys.addedToEngine(engine);
		
		workSys.addWorker(new ProgressAction(){

			private ProgressBarSystem progressSys;
			private int progress, total = 30;
			private Texture blankNormalTexture;

			@Override
			public void update(float dt) {
				switch (progress++){
				case 1:
					factory =  new PlatformerFactory();//new RunnerFactory();//
					uiAtlas = new TextureAtlas(Gdx.files.internal("ui.atlas"));
					break;case 2:
					playerAtlas = new TextureAtlas(Gdx.files.internal("player.atlas"));
					break;case 3:
					atlas = new TextureAtlas(Gdx.files.internal("tiles.atlas"));
					break;case 4:
					SpriteCacheNiz.setAtlas(atlas);
					Animations.init(playerAtlas, engine, uiAtlas, atlas);
					blankNormalTexture = new Texture(Gdx.files.internal("blanknormaltexture.png"));
					skin = new Skin();
					camera = new OrthographicCamera();
					camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					//mapRenderCam = new OrthographicCamera();
					//mapRenderCam.setToOrtho(false, Gdx.graphics.getWidth() * Main.PPM, Gdx.graphics.getHeight() * Main.PPM);
					//mapRenderCam.position.set(OverworldSystem.SCROLLING_MAP_WIDTH * Main.PPM / 2, OverworldSystem.SCROLLING_MAP_WIDTH * Main.PPM / 2, 0);
					for (int i = 0; i < 1000; i++){
						numberStrings [i] = ""+i;
					}
					font = new BitmapFont(Gdx.files.internal("font/Pixel-12.fnt"), Gdx.files.internal("font/pixelfonts.png"), false);
					font.setUseIntegerPositions(false);
					font.getData().setScale(2f);
					viewport = new ScreenViewport(uiCamera);
					stage = new Stage(viewport, batch);
					mapBatch = new SpriteBatchN(1000);
					rightBatch = new SpriteBatchN(5460);
					//gameCamera = new OrthographicCamera(10, 10);//Main.PPM*Main.VIEWPORT_SIZE, (int)(Main.PPM*Main.VIEWPORT_SIZE/Main.ar));
					if (uiAtlas.createSprite("button") == null) throw new GdxRuntimeException("kjl");
					Styles.makeSkin(skin, uiAtlas);
					mux = new InputMultiplexer();
					mux.addProcessor(new InputProcessor(){
						@Override
						public boolean keyDown(int keycode) {
							switch (keycode){
							case Keys.ESCAPE:case Keys.BACK:
								//invRefreshSubject.notify(null, Event.INVENTORY_REFRESH, null);
								invToggleComponent.value = false;
								if (stage.getActors().contains(mainMenuScreen.getTable(), false)) return false;
								menuKeySubject.notify(null, Event.INVENTORY_TOGGLE, invToggleComponent);
								return true;
							case Keys.C:
								engine.getSystem(MapRenderSystem.class).setProcessing(false);
								return true;
							case Keys.X:
								engine.getSystem(MapRenderSystem.class).setProcessing(true);
								
								return true;
								
							}
							return false;				}
						@Override
						public boolean keyUp(int keycode) {return false;				}
						@Override
						public boolean keyTyped(char character) {return false;				}
						@Override
						public boolean touchDown(int screenX, int screenY, int pointer,	int button) {
							if (engine.getSystem(PlayerSystem.class).getEntities().size() == 0) return false;
							Entity player = engine.getSystem(PlayerSystem.class).getEntities().get(0);
							ActionList plact = player.getComponent(ActionList.class);
							Pathfind pathC = engine.createComponent(Pathfind.class);
							Vector2 plpos = player.getComponent(Position.class).pos;
							tmpV.set(screenX, screenY, 0);
							engine.getSystem(CameraSystem.class).adjustedCamera.unproject(tmpV);
							pathC.targetX = (int) tmpV.x / Main.PPM;
							pathC.targetY = (int) tmpV.y / Main.PPM;
							
							//player.add(pathC);
							//Gdx.app.log(TAG,  "path");
							return false;				}
						@Override
						public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;				}
						@Override
						public boolean touchDragged(int screenX, int screenY,
								int pointer) {return false;				}
						@Override
						public boolean mouseMoved(int screenX, int screenY) {return false;				}
						@Override
						public boolean scrolled(int amount) {
							if (amount > 0) zoomInput.zoom = 1.2f;
							else zoomInput.zoom = 1f/1.2f;
							//Gdx.app.log("zoom", " " + zoomInput.zoom);
							zoomSubject.notify(null, null, zoomInput);
							return false;				}

					});
					mux.addProcessor(stage);
					Gdx.input.setInputProcessor(mux);
					defaultCam = new OrthographicCamera(1f, 1f);
					defaultCam.setToOrtho(true, 1f, 1f);
					menuKeySubject = engine.getSubject("inventoryToggle");
					invRefreshSubject = engine.getSubject("inventoryRefresh");
					zoomSubject = engine.getSubject("zoominput");
					zoomInput = new ZoomInput();
					zoomInput.zoom = 1f;
					zoomSubject.notify(null, null, zoomInput);

					shaderSys = new ShaderSystem();
					engine.addSystem(shaderSys);


					lightRender = new LightRenderSystem(batch);
					lights = new LightUpdateSystem(batch, lightRender);

					engine.addSystem(new RoomSystem());
					engine.addSystem(new RoomCatalogSystem());
					engine.addSystem(new DragControllerSystem());
					engine.addSystem(new SelectedPlayerSystem(factory));
					engine.addSystem(new PlaceAtStartSystem());
					engine.addSystem(new SpawnSystem());

					engine.addSystem(new OnMapSystem(atlas));
					engine.addSystem(new MapCollisionSystem());
					engine.addSystem(new ActionSystem());
					engine.addSystem(new SpriteAnimationUpdateSystem());
					engine.addSystem(new Physics2dSystem());
					engine.addSystem(new LineMapCollisionSystem());
					break;case 5:
					engine.addSystem(new BitmaskedCollisionsSystem());
					engine.addSystem(new BucketSystem());
					engine.addSystem(new PickUpCollisionsSystem());
					engine.addSystem(new MapSystem());
					OverworldSystem overworld = new OverworldSystem(atlas, factory);
					overworld.setProcessing(false);
					engine.addSystem(overworld);
					break;case 10:
					engine.addSystem(new DoorSystem());
					engine.addSystem(new SpeedLimitSystem());
					engine.addSystem(new EntitySerializationSystem());
					break;case 11:
					engine.addSystem(new RandomMapUpdateSystem());
					engine.addSystem(new ZoomSystem());
					engine.addSystem(new PathfindingSystem());
					engine.addSystem(new PathfindingUpdateSystem());

					break;case 12:
					engine.addSystem(new PreRenderSystem());
					engine.addSystem(new CameraSystem());
					//engine.addSystem(new ParallaxBackgroundRenderNoBufferSystem());

					engine.addSystem(new BufferStartSystem());
					break;case 13:
					break;case 14:
					engine.addSystem(lights);
					engine.addSystem(lightRender);
					break;case 15:
					shapeR = new ShapeRenderer();			
					engine.addSystem(new ShapeRenderingSystem());
					//engine.addSystem(new ParallaxBackgroundSystem());
					break;case 16:
					engine.addSystem(new MapRenderSystem(mapBatch, atlas, lights));

					break;case 17:
					engine.addSystem(new RaceSystem());
					engine.addSystem(new WeaponSensorSystem());
					break;case 18:
					engine.addSystem(new SpriteAnimationSystem(rightBatch, lights));




                    engine.addSystem(new BufferEndSystem(batch, blankNormalTexture));
					engine.addSystem(new LineBatchSystem(lights));
					break;case 19:
					engine.addSystem(new LineBatchPostSystem());
					//engine.addSystem(new ParallaxBackgroundFrontLayersRenderingSystem());
					engine.addSystem(new AutoGibSystem(rightBatch, leftBatch,  lights));;
					resize();
					break;case 20:
					engine.addSystem(new DragBlockSystem());
					engine.addSystem(new InventorySystem());
					engine.addSystem(new AgentSystem());
					//resize();
					break;case 21:

					charScreen = new CharacterScreen(engine, skin);
					charScreen.create(skin, stage, engine);
					charScreen.init(skin, stage, engine);
					break;case 22:

					settingsScreen = new SettingsScreen(skin, engine);
					settingsScreen.create(skin, stage, engine);
					settingsScreen.init(skin, stage, engine);
					settingsScreen.setObject(Main.prefs, "Settings");
					break;case 23:

					invScreen = new InventoryScreen(engine, skin, charScreen, settingsScreen, shaderSys.shader);
					invScreen.create(skin, stage, engine);
					invScreen.init(skin, stage, engine);

					charScreen.invScreen = invScreen;
					settingsScreen.invScreen = invScreen;
					break;case 24:

					mainMenuScreen = new MainMenu(inst, skin);
					mainMenuScreen.create(skin, stage, engine);
					mainMenuScreen.init(skin, stage, engine);
					mainMenuScreen.addTo(stage);

					break;case 25:

					playerInput = new PlayerInputSystem(engine, invScreen);
					engine.addSystem(playerInput);
					engine.addSystem(new PlayerSystem());;
					//break;case 26:

					boolean newGame = true;
					if (newGame){
						Entity dragger = engine.createEntity();
						DragController drag = Pools.obtain(DragController.class);
						
						dragger.add(drag);
						Position pos = engine.createComponent(Position.class);
						pos.pos.set(16 + PlatformerFactory.CHAR_SELECT_SPACING * .5f, 1);
						dragger.add(pos);
						drag.min = 16;
						drag.max = PlatformerFactory.CHAR_SELECT_SPACING * PlatformerFactory.CHAR_SELECT_CHARACTERS + 16;
						engine.addEntity(dragger);
						
						engine.addEntity(factory.createCamera(engine));
						
						OverworldSystem over = engine.getSystem(OverworldSystem.class);
						over.setForNewGameScreen();

						ProgressAction lvlSelect = new ProgressAction(){

							@Override
							public void update(float dt) {
								factory.makeLevelSelection(engine, worldDef);
								isFinished = true;
							}

							@Override
							public void onEnd() {
								parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);
							}

							@Override
							public void onStart() {
							}
							
						};
						workSys.addWorker(lvlSelect);
					}
					resize();
					break;
					case 30:
					isFinished = true;
					
				}
				float progressDelta = (float)progress / (float)total;
				//Gdx.app.log(TAG,  "creating engine " + progressDelta);
				progressSys.setProgressBar(progressBarIndex, progressDelta );

			}

			@Override
			public void onStart() {
				progress = 0;
				super.onStart();
				progressSys = parent.engine.getSystem(ProgressBarSystem.class);
			}

			@Override
			public void onEnd() {
				super.onEnd();
				
			}
			
		});
		resize();
	}
	
	protected void resize() {
		int min = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		int max = min + 1;
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), min, max);
		
	}

	float highestDelta = 4f;
	private float lowestDelta;
	
	//int[] tileHeights = {32, 28, 27, 25, 24,   20, 24};
	
	public void resize(int width, int height, int min, int max) {
		Main.ar = (float)width/(float)height;
		int lowestIndex = 0, lowestError = 9999999;
		for (int i = max; i > min; i--){
			
			int error1 = width % (i)
					, error2 = height % ((int)(i*Main.ar));
			int error = error1 + error2;
			if (error < lowestError){
				lowestError = error;
				lowestIndex = i;
			}
		}
		//Gdx.app.log(TAG, "resize");
		if (uiCamera != null){
			uiCamera.setToOrtho(false, width, height);
			uiCamera.position.set(width/2, height/2, 0);
			//uiCamera.position.set(0, 0, 0);
			uiCamera.update();
		}
		//Gdx.app.log(TAG, "resize"+lowestIndex);
		resC.v.x = lowestIndex;
		if (stage != null){
			viewport = new ScalingViewport(Scaling.none, width, height, uiCamera);
			stage.setViewport(viewport);
			stage.getViewport().update(width, height, true);
		}
		resC.v.x = width;
		resC.v.y = height;
		engine.getSubject("resize").notify(null, Event.RESIZE, resC);
		//engine.getSubject("inventoryRefresh").notify(null, Event.INVENTORY_REFRESH, resC);
	}
	private VectorInput resC = new VectorInput();
	private WorldDefinition worldDef;
	private boolean showingLogo = true
			;
	@Override
	public void dispose() {
//		if (playerDiffuseTex != null)playerDiffuseTex.dispose();// = playerAtlas.findRegion("diff/player", 0).getTexture();
//		if (playerNormalTex != null)playerNormalTex.dispose();// = playerAtlas.findRegion("normal/player", 0).getTexture();
//		if (mapDiffuseTex != null)mapDiffuseTex.dispose();// = atlas.findRegion("diff/tile", 0).getTexture();
//		if (mapNormalTex != null)mapNormalTex.dispose();// = atlas.findRegion("normal/tile", 0).getTexture();
		engine.dispose();
		if (atlas != null){
			
			atlas.dispose();
			uiAtlas.dispose();
//			playerAtlas.dispose();
		}
	}

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		if (headless) delta *= 8f;
		//delta *= .1f;
		//if (isClient){Gdx.app.log(TAG,  "render cl"+accum);} else Gdx.app.log(TAG,  "render serv"+accum);
		{
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.setColor(Color.WHITE);
			
		}
		deltaTime = delta;//Gdx.graphics.getDeltaTime();
		inputSys.update(deltaTime);
		ActionSystem actionSys = engine.getSystem(ActionSystem.class);
		if (actionSys != null && playerEntities.size() > 0){
			actionSys.updateRender(delta);
			boolean paused = false;
			Entity player = playerEntities.get(0);
			Control con = player.getComponent(Control.class);
			ActionList act = player.getComponent(ActionList.class);
			if (
					(act.containsAction(AStand.class) && act.containsAction(ANotRun.class))
					&& 
					(!con.pressed[Input.JUMP] && !con.pressed[Input.WALK_LEFT] && !con.pressed[Input.WALK_RIGHT])
					
					) paused = true;
			if (unPause){
			    paused = false;
			    unPause = false;
            }
//			if (paused) deltaTime = 0f;
		}
		if (deltaTime > .1f) deltaTime = .1f;
		
		accum += deltaTime;
		float timeStep = Main.timeStep;
		while (accum > timeStep){
			engine.tick++;
			accum -= timeStep;
			//Gdx.app.log(TAG,  "render"+accum + isClient + deltaTime);;
			engine.update(timeStep);
			if (engine.simulating ){
				//Gdx.app.log(TAG,  "sim"+accum);;
				//if (MathUtils.random(100) > 39)
					//accum += timeStep;	
			}
		}
		
		engine.render(deltaTime);
		if (!engine.getSystem(WorkerSystem.class).allPaused){
			if (showingLogo ){
				float h = Gdx.graphics.getWidth()/20, w = h*3,  x = Gdx.graphics.getWidth()/2 - w/2, y = Gdx.graphics.getHeight()/2 - h/2;
				batch.getProjectionMatrix().setToOrtho2D(0,  0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				batch.begin();
				batch.draw(logo, x, y, w, h);
				batch.end();
				//Gdx.app.log(TAG,  "render");
				return;
			}
		} else showingLogo = false;//*/
		//if (isClient)Gdx.app.log(TAG,  "stepped"); else Gdx.app.log(TAG,  "stepped server");
		if (camera != null){
			batch.setProjectionMatrix(camera.combined);
			batch.setColor(Color.WHITE);;
			batch.begin();
			if (deltaTime < highestDelta || MathUtils.random(100) == 0){
				highestDelta = deltaTime;
			}
			if (deltaTime < lowestDelta || MathUtils.random(100) == 0){
				lowestDelta = deltaTime;
			}
			ImmutableArray<Entity> playerArr = engine.getEntitiesFor(Family.one(Player.class).get());
			if (playerArr.size() != 0){
				Entity player = playerArr.get(0);
//				String.format("%.2f", player.getComponent(Position.class).pos.x)
//				+"\ny:" +String.format("%.2f", player.getComponent(Position.class).pos.y);
				
				/*String s = "";
				if (player.getComponent(Physics.class) != null){
					s += "\n"+ (player.getComponent(Physics.class).onGround?"ground":"");
					s += "\n"+ (player.getComponent(Physics.class).wasOnGround2?"wasGround":"");
					s += "\n"+ (player.getComponent(Physics.class).onSlope?"slope":"");
					s += "\n"+ (player.getComponent(ActionList.class));
					//String s = ""+Gdx.graphics.getFramesPerSecond() + "\n"+
					s += "\n" + String.format("%.1f", player.getComponent(Position.class).pos.x);
					s += "," +String.format("%.1f", player.getComponent(Position.class).pos.y);
					//s += "\n"+ (player.getComponent(Physics.class).onGround?"ground":"");
					//s += "\n"+ (player.getComponent(Physics.class).wasOnGround2?"wasGround":"");
					//s += "\n"+ (player.getComponent(Physics.class).onSlope?"slope":"");
					//s += "fps:"+String.format("%.1f",1f/deltaTime);
					//
				}*/

			String s = "fps:"+Gdx.graphics.getFramesPerSecond() ;//+ " " + GLProfiler.calls + " drawc:" + GLProfiler.drawCalls
//					+ "\nbind: " + GLProfiler.textureBindings + "  shad: " + GLProfiler.shaderSwitches
//					+ " vcount:" + GLProfiler.vertexCount.total
//					;
//			GLProfiler.reset();

			/*s += " sw:"+GLProfiler.shaderSwitches;
			s += " tex:"+GLProfiler.textureBindings;
			s += " vert:"+GLProfiler.vertexCount.average;
			/*s += "\n";
			s += Gdx.app.getJavaHeap()>>20 ;
			s += " ";
			s += Gdx.app.getNativeHeap()>>20;*/
			font.draw(batch,
						s, 100, font.getLineHeight()*8);
				
			
			//*/
			}
			/*""
					+ "  high"+(""+(1f/highestDelta)).substring(0,4)
					+ " \n  low"+(""+(1f/lowestDelta)).substring(0,4)
					+ (player.getComponent(ActionList.class).actions.get(AWallSlide.class) != null)
					+ player.getComponent(Position.class).pos
					+ "\n entities:"+engine.getEntities().size()
					+ "\n tick:"+engine.tick
					, 100, 100);//*/
			batch.end();
			
			//uiAtlas.getTextures().iterator().next().bind();
			//batch.setColor(Color.WHITE);
			if (settingsScreen != null)settingsScreen.update();
			camera.update();
			if (shapeR != null)shapeR.setProjectionMatrix(uiCamera.combined);
			//stage.setDebugAll(true);
			//stage.act(1f);
			if (stage != null)stage.act(delta);
			//stage.getBatch().setShader(shaderSys.shader);
			stage.getBatch().setShader(null);
			batch.enableBlending();
			stage.draw();
			if (invScreen != null){
				SpriteBatchN bat = (SpriteBatchN) stage.getBatch();
				invScreen.update(delta);
				invScreen.draw(shapeR, bat, Styles.inventoryFont);
			}
		}
	}

	@Override
	public void pause() {
		//factory.save(engine, invScreen.belt);
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	public void switchToCharacterGenScreen(WorldDefinition worldDef) {
		stage.clear();
		charScreen.startGameButton.setFor(this, worldDef, charScreen, new Race());
		charScreen.addTo(stage);
		
	}
	
	public void startNewGame(WorldDefinition def){
		stage.clear();
		invScreen.addTo(stage);
		invScreen.changeToHUD();
		if (def.isRoomEditor){
			invScreen.setRoomEditor(true);
			
		}
		else {
			invScreen.setRoomEditor(false);
		}
		factory.def = def;
		engine.getSystem(EntitySerializationSystem.class).worldDef = def;
		
		OverworldSystem overworld = engine.getSystem(OverworldSystem.class);
		overworld.simplexNoise = new SimplexNoise(def.seed);
		overworld.setProcessing(true);
		overworld.worldDef = def;
		ParallaxBackgroundSystem parall = engine.getSystem(ParallaxBackgroundSystem.class);
		if (parall != null) parall.setProcessing(true);
		factory.startMap(engine);
		playerArr = Data.entityArrayPool.obtain();
		factory.createPlayer(engine, playerArr, def);
		overworld.startLoadingChunksFor(playerArr);
		playerArr = null;
		engine.getSystem(PathfindingUpdateSystem.class).setJumpPaths();
	}
	
	Array<PooledEntity> playerArr;// = new Array<PooledEntity>();
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		if (event == Event.WORLD_DEFINITION_SET){
			this.worldDef = (WorldDefinition)c;
		}
	}

	public void startWorld(WorldDefinition def) {
		FileHandle playerFile = def.folder.child("player.e");
		if (playerFile.exists()){
			//loadGame(def, playerFile);//TODO
		} else {
			//switchToCharacterGenScreen(def);
			DragControllerSystem drag = engine.getSystem(DragControllerSystem.class);
			Entity player = drag.getSelectedPlayerEntity();
			SelectedPlayer sel = new SelectedPlayer();
			sel.def = def;
			player.add(sel);
		}
		startNewGame(def);
	}
}
