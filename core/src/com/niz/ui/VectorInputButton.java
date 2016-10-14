package com.niz.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;


public class VectorInputButton extends InputButton{
	Object o;
	int type;
	public TextField xButton, yButton, zButton;
	
	public VectorInputButton(Skin skin, int type) {
		super("vector input", skin);
		this.type = type;
		
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void process() {
		
		
		
	}

	@Override
	public void onAdded(Table table) {
		Class c = o.getClass();

	}
	
	
	
	

}
