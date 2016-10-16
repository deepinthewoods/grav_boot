package com.badlogic.gdx.scenes.scene2d.ui;

public class ToggleableTextButton extends TextButton {
	public ToggleableTextButton(String text, Skin skin) {
		super(text, skin);
		// TODO Auto-generated constructor stub
	}

	public void setCheckedWithoutChanging(boolean b) {
		isChecked = b;
	}
}
