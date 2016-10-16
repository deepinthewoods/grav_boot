package com.niz.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class StringArrayElementButton extends TextField implements FieldButton{

	public StringArrayElementButton(Skin skin) {
		super("", skin);
		// TODO Auto-generated constructor stub
	}

	
	private String value;
	private Array<String> arr;
	private int index;
	public void set(String name, Array<String> arr, int index) {

		this.setName(name);
		
		this.arr = arr;
		this.index = index;
		pullValue();
	}

	private void pullValue() {
		
		value =  arr.get(index);
		
		this.setText(value);
		
	}

	public void apply() {
		pushValue();
		pullValue();
	}
	
	public void pushValue(){
		boolean valid = true;
		String newVal = "";
		try {
			newVal = getText();
		} catch (NumberFormatException ex){
			valid = false;
		}
		if (valid){
			value = newVal;
			
			arr.set(index, value);
				
			
		}
		
	}

}
