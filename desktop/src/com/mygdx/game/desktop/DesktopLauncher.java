package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.Main;

public class DesktopLauncher {
	public static final String BLOCKS_FOLDER = "C:\\Users\\Niall\\_grav_sprites\\blocks";
	public static final String ANDROID_FOLDER = "C:\\Users\\Niall\\_gravboot";
	public static void main (String[] arg) {

		pack();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 480;
		config.vSyncEnabled = false;
		config.useGL30 = true;
		config.title = "Grav Boot";
		new LwjglApplication(new Main(){

			@Override
			public void preProcess() {
				//run reduceNormalTextures.bat before doing this


//				TextureRepacker.process("player", "player2quantized");
//				TextureRepacker.process("tiles", "tiles2quantized");

			}
		}, config);
	}

	private static void pack(){
//		TexturePacker.process(BLOCKS_FOLDER + "\\finishedplayer", ANDROID_FOLDER + "\\android\\assets", "player");
//		TexturePacker.process(BLOCKS_FOLDER + "\\finished", ANDROID_FOLDER + "\\android\\assets", "tiles");

//		try {
//			Runtime.getRuntime().exec("reduceNormalTextures.bat");
//		} catch (Exception ex){
//			throw new GdxRuntimeException("ex " + ex);
//		}


	}
}
