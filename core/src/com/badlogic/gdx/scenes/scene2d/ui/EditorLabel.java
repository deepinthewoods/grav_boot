package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.Field;
import com.niz.editor.EditorScreen;
import com.niz.editor.EditorTable;
import com.niz.editor.FieldButton;

public class EditorLabel extends TextButton implements FieldButton{

	public Object parentObject;
	private EditorScreen ed;
	public boolean isObject;
	
	public EditorLabel(String text, Skin skin, EditorScreen ed) {
		super(text, skin);
		this.ed = ed;
	}

	@Override
	public void apply() {
		isChecked = true;
		EditorTable parent = (EditorTable) getParent();
		parent.addToStack();
		ed.reTableStack();
		//Gdx.app.log("editor label", "CLICK");
	}

	
	
	
	
}
