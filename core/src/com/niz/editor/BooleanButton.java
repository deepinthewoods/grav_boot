package com.niz.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class BooleanButton extends TextButton implements FieldButton{
	private static final String TAG = null;
	//public static ButtonGroup grp = new ButtonGroup();
	public BooleanButton(Skin skin) {
		super("", skin, "boolean");
		// TODO Auto-generated constructor stub
	}

	private Field f;
	private boolean value;
	private Object obj;
	public void set(String name, Field f, Object obj) {
		//grp.clear();
		//grp.add(this);
		this.setName(name);
		this.f = f;
		this.obj = obj;
		pullValue();
		this.setText(f.getName());
	}

	private void pullValue() {
		try {
			value = (Boolean) f.get(obj);
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//throw new GdxRuntimeException("error");
		}
		this.setChecked(value);
		//this.setText(""+value);
		
	}

	public void apply() {
		setChecked(!isChecked());
		Gdx.app.log(TAG, "allpy"+value);
		pushValue();
		Gdx.app.log(TAG, "allpy"+value);
		pullValue();
		Gdx.app.log(TAG, "allpy"+value);
	}
	public void pushValue(){
		
		boolean valid = true;
		boolean newVal = false;
		try {
			newVal = this.isChecked();
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

}
