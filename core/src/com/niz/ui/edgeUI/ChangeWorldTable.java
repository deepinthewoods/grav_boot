package com.niz.ui.edgeUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Json;
import com.niz.Data;
import com.niz.WorldDefinition;
import com.niz.ui.elements.UIElement;

public class ChangeWorldTable extends UIElement {

	@Override
	protected void onInit(Skin skin) {
		TextButton changeWorldButton = new TextButton("Change World", skin, "mainmenu");
		changeWorldButton.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
			}
			
		});
		this.actor = changeWorldButton;
	}

}
