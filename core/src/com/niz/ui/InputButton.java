package com.niz.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class InputButton extends Label{

	public InputButton(CharSequence text, Skin skin) {
		super(text, skin);
		// TODO Auto-generated constructor stub
	}

	public abstract void process();
	public abstract void onAdded(Table table);
}
