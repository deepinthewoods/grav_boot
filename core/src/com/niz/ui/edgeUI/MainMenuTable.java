package com.niz.ui.edgeUI;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pool;
import com.niz.Data;
import com.niz.GameInstance;
import com.niz.Main;
import com.niz.WorldDefinition;
import com.niz.component.PathfinderPreLog;
import com.niz.ui.elements.UIElement;

public class MainMenuTable extends UIElement {
	protected static final int LONG_PRESS_TIME = (int)(1f / Main.timeStep);

	protected static final String TAG = "main menun table";

	private Pool<GameButton> buttonPool;
	
	private GameInstance game;
	private MainMenu mainMenu;
	private final TextButton newGameButton;

	private Table worldSelectTable;

	private Table worldTable;

	private TextButton deleteButton;

	private Table mainTable;

	private TextButton changeWorldButton;
	
	private long touchTime;

	private boolean holding;

	private TextButton startButton;

	private ScrollPane worldPane;

	private ButtonGroup gameGroup;

	private TextButton editorButton;

	protected boolean startQueued;

	private Family loggerCheckFamily;


	public MainMenuTable(MainMenu mainMenu, final GameInstance game, final Skin skin){
		this.mainMenu = mainMenu;
		this.game = game;
		loggerCheckFamily = Family.one(PathfinderPreLog.class).get();
		changeWorldButton = new TextButton("Change World", skin, "mainmenu");
		newGameButton = new TextButton("New World", skin, "mainmenu");
		deleteButton = new TextButton("Delete All", skin, "mainmenu");
		startButton = new TextButton("Start", skin, "mainmenu"){


			@Override
			public void act(float delta) {
				super.act(delta);
				if (holding
						 && touchTime <  game.engine.tick - LONG_PRESS_TIME){
					//Gdx.app.log(TAG, "long press actor");
					worldSelectTable.clear();
					worldSelectTable.add(newGameButton);
					//worldSelectTable.add(new Actor()).expand();
					worldSelectTable.row();
					worldSelectTable.add(deleteButton);
					worldSelectTable.add(worldPane);
				} else {
					if (startQueued ){
						if (game.engine.getEntitiesFor(loggerCheckFamily).size() == 0){
							startQueued = false;
							GameButton butt =  (GameButton) gameGroup.getChecked();
							if (butt == null){
								
							}
							if (butt.folder == null) throw new GdxRuntimeException("exc "+butt);
							
							FileHandle worldFile = butt.folder.child(Data.WORLD_MAIN_FILE_NAME);
							Gdx.app.log(TAG, "world file " + worldFile.path());
							
							WorldDefinition worldDef = Data.json.fromJson(WorldDefinition.class, worldFile);
							//WorldDefinition worldDef = new WorldDefinition();//Data.json.fromJson(WorldDefinition.class, worldFile);
							worldDef.folder = butt.folder;
							//game.startGenerating(worldDef);
							game.startWorld(worldDef);
							Main.prefs.previously_launched_game = butt.folder.name();
							
						}else {
							//Gdx.app.log(TAG, "delaying for jump loggers");
						}
					
					} 
				}
				//Gdx.app.log(TAG, "act press actor" + touchTime + "  " + (game.engine.tick + LONG_PRESS_TIME));
			}
			
			
		};
		buttonPool = new Pool<GameButton>(){
			@Override
			protected GameButton newObject() {
				GameButton butt = new GameButton(skin);
				butt.addListener(new ChangeListener(){

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						//load game here
						Gdx.app.log(TAG, "changed");
						worldSelectTable.clear();
						worldSelectTable.add(startButton);
						worldSelectTable.add(editorButton);

					}
					
				});
				return butt;
			}
			
		};
		
		editorButton = new TextButton("Editor", skin, "mainmenu"){

			
			
		};
		
		editorButton.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				FileHandle folder = Gdx.files.external(Data.FILE_PATH_PREFIX).child(Data.WORLDS_SUBFOLDER_PATH);

				WorldDefinition worldDef = new WorldDefinition();
				worldDef.isRoomEditor = true;
				worldDef.folder = folder;
				String worldName = "";
				int count = 0;
				do {
					count++;
					worldName = "room " + count;
				} while (folder.child(worldName).exists());
				
				FileHandle worldFolder = folder.child(worldName);
				if (worldFolder.exists()) return;
				FileHandle worldFile = worldFolder.child(Data.WORLD_MAIN_FILE_NAME);
				worldFolder.mkdirs();
				//worldFile.mkdirs();
				Json json = Data.json;
				
				WorldDefinition def = new WorldDefinition();
				
				def.set(MathUtils.random(1000000), worldName);
				def.folder = worldFolder;
				//worldFile.writeString("", false);
				
				worldFile.writeString(json.toJson(def), false);
				//refreshGameList();
				//game.startGenerating(worldDef);
				game.startWorld(worldDef);
			}
			
		});
		
		gameGroup = new ButtonGroup();
		gameGroup.setMaxCheckCount(1);
		gameGroup.setMinCheckCount(1);
	}
	
	@Override
	protected void onInit(Skin skin) {
		worldSelectTable = new Table();
		
		newGameButton.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//Gdx.app.log("newwordlbutton", "pressed"+event.toString());
				//game.startGenerating(seed);
				//mainMenu.showNewGameScreen();
				createNewGame();
			}

			
			
		});
		
		deleteButton.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//Gdx.app.log("newwordlbutton", "pressed"+event.toString());
				//game.startGenerating(seed);
				//mainMenu.showNewGameScreen();
				FileHandle folder = Gdx.files.external(Data.FILE_PATH_PREFIX);
				for (FileHandle f : folder.list()){
					f.deleteDirectory();
				}
				refreshGameList();
			}
			
		});
		
		changeWorldButton.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				mainTable.clear();
				mainTable.addActor(worldSelectTable);
			}
			
		});
		
		
		
		startButton.addListener(new ClickListener(){



			

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				touchTime = game.engine.tick;;
				//Gdx.app.log(TAG, "dwon press");
				holding = true;
				return true;//super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				holding = false;
				if (touchTime <  game.engine.tick - LONG_PRESS_TIME ){
					
					//Gdx.app.log(TAG, "long press");
				} else {
					//Gdx.app.log(TAG, "short press");
					startQueued = true;
				}
				
			}
			
			

			
			
		});
		
		
		
		
		worldSelectTable.add(startButton);
		worldSelectTable.add(editorButton);
		worldTable = new Table();
		worldPane = new ScrollPane(worldTable);
		//
		this.actor = worldSelectTable;
		refreshGameList();
		
		mainTable = new Table();
		
		//mainTable.addActor(changeWorldButton);
		//this.actor = mainTable;
	}
	
	private void createNewGame() {
		FileHandle baseFolder = Gdx.files.external(Data.FILE_PATH_PREFIX);
		if (!baseFolder.exists()) baseFolder.mkdirs();
		FileHandle folder = baseFolder.child(Data.WORLDS_SUBFOLDER_PATH);
		if (!folder.exists()) folder.mkdirs();
		String worldName = "world " + MathUtils.random(100000);
		FileHandle worldFolder = folder.child(worldName);
		if (worldFolder.exists()) throw new GdxRuntimeException("world name collision");
		FileHandle worldFile = worldFolder.child(Data.WORLD_MAIN_FILE_NAME);
		worldFolder.mkdirs();
		//worldFile.mkdirs();
		Json json = Data.json;
		
		WorldDefinition def = new WorldDefinition();
		
		def.set(MathUtils.random(1000000), worldName);
		def.folder = worldFolder;
		//worldFile.writeString("", false);
		
		worldFile.writeString(json.toJson(def), false);
		refreshGameList();
	}

	public void refreshGameList(){
		gameGroup.clear();
		
		worldTable.clear();
		FileHandle baseFolder = Gdx.files.external(Data.FILE_PATH_PREFIX);
		if (!baseFolder.exists()) baseFolder.mkdirs();
		FileHandle folder = baseFolder.child(Data.WORLDS_SUBFOLDER_PATH);
		if (!folder.exists()) folder.mkdirs();

		FileHandle[] worldFolders = folder.list();
		for (int i = 0; i < worldFolders.length; i++){
			FileHandle worldFolder = worldFolders[i];
			if (!worldFolder.isDirectory() || worldFolder.nameWithoutExtension().charAt(0) == '_') continue;
			worldFolder.list();
			FileHandle mainFile = worldFolder.child(Data.WORLD_MAIN_FILE_NAME);
			if (mainFile.exists()){
				
			}
			GameButton butt = buttonPool.obtain();
			butt.setText(worldFolder.name());
			butt.set(worldFolder);
			worldTable.add(butt);
			worldTable.row();
			
			//if (butt.folder == null) throw new GdxRuntimeException("");
			Gdx.app.log(TAG, "add "+butt);
			gameGroup.add(butt);
			if (Main.prefs.previously_launched_game.equals(worldFolder.name())){
				butt.setChecked(true);
			}
		}
		if (worldTable.getChildren().size == 0){
			Gdx.app.log(TAG,  "no games, making new game");
			createNewGame();
			//refreshGameList();
		}
		
		
	}
	
	
	
}
