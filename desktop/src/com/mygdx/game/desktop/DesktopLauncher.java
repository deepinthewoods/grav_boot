package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.niz.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		//TexturePacker.process("H:\\sprites\\blocks\\finishedplayer", "C:\\Users\\n\\_dungeonpunch\\android\\assets", "player");
		//TexturePacker.process("H:\\sprites\\blocks\\finished", "C:\\Users\\n\\_dungeonpunch\\android\\assets", "tiles");



		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 480;
		config.vSyncEnabled = false;
		config.useGL30 = true;
		config.title = "Grav Boot";
		new LwjglApplication(new Main(){

			@Override
			public void preProcess() {
				TextureRepacker.process("tiles", "tiles2quantized");
				TextureRepacker.process("player", "player2quantized");
				FileHandle guideFile = Gdx.files.internal("guides/rpgguides.png");
				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpghandfrontguide", 18, 20, Color.rgba8888(Color.GREEN));
				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpghandfronttipguide", 18, 20, Color.rgba8888(Color.MAGENTA));
				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpghandbackguide", 18, 20, Color.rgba8888(Color.BLUE));
				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpghandbacktipguide", 18, 20, Color.rgba8888(Color.RED));
				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgarmsguide", 18, 20, Color.rgba8888(Color.WHITE));
				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgtorsoguide", 18, 20, Color.rgba8888(Color.WHITE));


				TextureRepacker.createGuideSpritesIdentical(guideFile, "rpglegsguide", 18, 20, new GridPoint2(9, 0));


			}
		}, config);
	}
}
