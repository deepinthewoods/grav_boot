package com.niz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.niz.anim.SpriteCacheNiz;
import com.niz.component.Inventory;
public class Main extends ApplicationAdapter {
	private static final String TAG = "Main";
	public static final int PPM = 16;
	public static float ar;
	//public static int VIEWPORT_SIZE = 32;//16;
	public static float timeStep = 1f/256f;
	//public static final int TILE_VIEW_MIN = 639;
	//public static final int TILE_VIEW_MAX = 641;
	public static final float PX = 1f/(float)PPM;
	public static final String APP_NAME = "gravboot";
	private Array<GameInstance> games = new Array<GameInstance>();
	//private ClientInstance client;
	//private ServerInstance server;
	public static Prefs prefs;
	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);
		FileHandle prefsFile = Gdx.files.external(Data.FILE_PATH_PREFIX + "prefs");
		if (prefsFile.exists()){
			//prefsFile.delete();
			//if (true) return;
			Kryo kryo = Data.kryoPool.borrow();
			com.esotericsoftware.kryo.io.Input input = new com.esotericsoftware.kryo.io.Input(100);
			input.setInputStream(prefsFile.read());
			try {
				prefs = kryo.readObject(input, Prefs.class);
			} catch (Exception ex){
				prefsFile.delete();
				prefs = new Prefs();
				
			}
			input.close();
			Data.kryoPool.release(kryo);
		} else {
			prefs = new Prefs();
		}
		Data.init();
		Inventory.initItemDefs();
		SpriteCacheNiz.init();
		int tcp = 1000, udp = 1000;
		String ip = "127.0.0.1";
		//server = new ServerInstance(tcp, udp);
		//client = new ClientInstance();
		//server.updateThread.start();
		//client.connect(ip, tcp, udp);
		
		//client.updateThread.start();
		
		//GameInstance game = new GameInstance();
		//game.create(true, null, server);
		//games.add(game);
		
		//game = new GameInstance();
		//games.add(game);
		//game.create(false, client, null);
		
		GameInstance game = new GameInstance();
		games.add(game);
		game.create(false, true);
		
		
		
		//games.get(0).startGenerating(100);
		
		
	}
//call finishmaskedc block\stone tile finished\diff\tile_ 1088 stonepal2.png 100 100 100
	//call finishmasked block\stone normal finished\normal\tile_ 1088
	
	@Override
	public void resize(int width, int height) {
		for (int i = 0; i < games.size; i++){
			int min = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			int max = min + 1;
			games.get(i).resize(width,  height, min, max);
		}
	}

	@Override
	public void render() {
		float dt = Gdx.graphics.getDeltaTime();
		//server.update();
		//client.update();
		for (int i = 0; i < games.size; i++){
			games.get(i).render(dt);
		}
	}

	@Override
	public void pause() {
		for (int i = 0; i < games.size; i++){
			games.get(i).pause();
		}
		//Gdx.app.log(TAG,  "PAUSE");
	}

	@Override
	public void resume() {
		for (int i = 0; i < games.size; i++){
			games.get(i).resume();
		}
	}

	@Override
	public void dispose() {
		for (int i = 0; i < games.size; i++){
			games.get(i).dispose();
		}
	}

	public static void savePrefs() {
		FileHandle prefsFile = Gdx.files.external(Data.FILE_PATH_PREFIX + "prefs");

		Kryo kryo = Data.kryoPool.borrow();
		com.esotericsoftware.kryo.io.Output output = new com.esotericsoftware.kryo.io.Output(100
				);
		
		output.setOutputStream(prefsFile.write(false));
		kryo.writeObject(output, prefs);
		output.close();
		Data.kryoPool.release(kryo);
	}

	
	

    
	

	
}
