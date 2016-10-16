package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.niz.editor.FieldButton;

public class MethodButton extends TextButton implements FieldButton {

	private Object o;
	private Method m;

	public MethodButton(Skin skin) {
		super("", skin, "method");
		
		isChecked = true;
		
	}

	public void set(String name, Method m, Object o) {
		setText(name);
		this.m = m;
		this.o = o;
		
	}

	@Override
	public void apply() {
		isChecked = true;
		//Gdx.app.log("method button", "do");
		try {
			m.invoke(o);
		} catch (ReflectionException e) {
			
			e.printStackTrace();
		}
		
	}

	

}
