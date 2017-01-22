package com.niz.ui.edgeUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GameButton extends TextButton {

	public FileHandle folder;

	public GameButton(Skin skin) {
		super("ggg", skin, "mainmenuselectable");
		// TODO Auto-generated constructor stub
	}

	public void set(FileHandle worldFolder) {
		Gdx.app.log("gamebutton", "set world folder " + worldFolder.path());
		this.folder = worldFolder;
	}

}
