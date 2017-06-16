package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
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
		new LwjglApplication(new Main(), config);
	}
}
