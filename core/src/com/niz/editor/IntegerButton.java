package com.niz.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class IntegerButton extends TextField implements FieldButton{

	public IntegerButton(Skin skin) {
		super("", skin);
		// TODO Auto-generated constructor stub
	}

	private Field f;
	private int value;
	private Object obj;
	public void set(String name, Field f, Object obj) {

		this.setName(name);
		this.f = f;
		this.obj = obj;
		pullValue();
	}

	private void pullValue() {
		try {
			value = (Integer) f.get(obj);
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//throw new GdxRuntimeException("error");
		}
		this.setText(""+value);
		
	}

	public void apply() {
		pushValue();
		pullValue();
	}
	public void pushValue(){
		boolean valid = true;
		int newVal = 0;
		try {
			newVal = Integer.parseInt((String)getText());
		} catch (NumberFormatException ex){
			valid = false;
		}
		if (valid){
			value = newVal;
			try {
				f.set(obj, value);
			} catch (ReflectionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//throw new GdxRuntimeException("error");
			}
		}
		
	}
//	@Override
//	public float getPrefWidth() {
//		// TODO Auto-generated method stub
//		return 250;
//	}
}
