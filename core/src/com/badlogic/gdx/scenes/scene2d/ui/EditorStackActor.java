package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.editor.EditorScreen;
import com.niz.editor.FieldButton;

public class EditorStackActor extends TextButton implements FieldButton {
private EditorScreen ed;



public EditorStackActor(String text, Skin skin, EditorScreen ed) {
		super(text, skin, "navbar");
		this.ed = ed;
		// TODO Auto-generated constructor stub
	}
	
	
	public Object self;
	int index;
	//public EditorTable table;
	public boolean isObject;
	public String labelName;
	public Class<?> arrayClass;
	
	
	
	public void setArray(Object self, Class<?> arrayCl, String labelN, int index){
		isObject = false;
		this.self = self;
		this.arrayClass = arrayCl;
		labelName = labelN;
		this.index = index;
	}
	
	
	
	public void set( Object self, int index, String name){
		this.labelName = name;
		this.self = self;
		this.index = index;
		isObject = true;
	}

	@Override
	public void apply() {
		// 
		ed.setStackSize(index);
		isChecked = false;
		//throw new GdxRuntimeException("H");
	}

}
