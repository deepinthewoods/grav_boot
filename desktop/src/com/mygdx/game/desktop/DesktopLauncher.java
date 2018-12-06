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
	public static final String BLOCKS_FOLDER = "C:\\Users\\Niall\\_grav_sprites\\blocks";
	public static final String ANDROID_FOLDER = "C:\\Users\\Niall\\_gravboot";
	public static void main (String[] arg) {

//		TexturePacker.process(BLOCKS_FOLDER + "\\finishedplayer", ANDROID_FOLDER + "\\android\\assets", "player");
		//TexturePacker.process(BLOCKS_FOLDER + "\\finished", ANDROID_FOLDER + "\\_dungeonpunch\\android\\assets", "tiles");

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
				//TextureRepacker.process("player", "player2quantized");
				//TextureRepacker.process("tiles", "tiles2quantized");
				FileHandle guideFile = Gdx.files.internal("guides/rpgguides.png");
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpghandfrontguide", 18, 20, Color.rgba8888(Color.GREEN));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpghandfrontguidetip", 18, 20, Color.rgba8888(Color.MAGENTA));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpghandbackguide", 18, 20, Color.rgba8888(Color.BLUE));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpghandbackguidetip", 18, 20, Color.rgba8888(Color.RED));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgarmsguide", 18, 20, Color.rgba8888(Color.WHITE));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgtorsoguide", 18, 20, Color.rgba8888(Color.WHITE));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgtorso2guide", 18, 20, Color.rgba8888(Color.WHITE));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgneckguide", 18, 20, Color.rgba8888(Color.WHITE));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgtailguide", 18, 20, Color.rgba8888(Color.WHITE));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgheadguide", 18, 20, Color.rgba8888(Color.WHITE));
//				TextureRepacker.processGuideSpritesFromSprite(guideFile, "rpgheadguideTip", 18, 20, Color.rgba8888(Color.WHITE), 0, 2);
//				TextureRepacker.createGuideSpritesIdentical(guideFile, "rpglegguide", 18, 20, new GridPoint2(9, 0));
//
//				TextureRepacker.createGuideSpritesIdentical("nonelegguide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("nonehandfrontguide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("nonehandfrontguidetip", 1, new GridPoint2(0, 2));
//				TextureRepacker.createGuideSpritesIdentical("nonehandbackguide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("nonehandbackguidetip", 1, new GridPoint2(0, 2));
//				TextureRepacker.createGuideSpritesIdentical("nonearmsguide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("nonetorsoguide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("nonetorso2guide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("noneneckguide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("nonetailguide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("noneheadguide", 1, new GridPoint2(0, 0));
//				TextureRepacker.createGuideSpritesIdentical("noneheadguideTip", 1, new GridPoint2(0,  2));


			}
		}, config);
	}
}
