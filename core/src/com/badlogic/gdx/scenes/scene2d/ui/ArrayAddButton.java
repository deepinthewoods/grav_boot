package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.niz.editor.EditorScreen;
import com.niz.editor.EditorTable;
import com.niz.editor.FieldButton;

public class ArrayAddButton extends TextButton implements Poolable, FieldButton{

	private Array o;
	
	private EditorTable table;
	private Class type;
	private EditorScreen editor;
	private ArrayExpander expander;
	public ArrayAddButton(String text, Skin skin){
		super(text, skin);
		
		
		
	}
	
	public void set( Array object, EditorTable table, EditorScreen editorScreen, Class type, ArrayExpander exp) {
		expander = exp;
		
		this.table = table;
		
		this.o = object;
		this.editor = editorScreen;
		this.type = type;
	}

	
	@Override
	public void reset(){
		super.reset();
		add(getLabel()).expand().fill();
		addListener(this.getClickListener());
		
	}

	@Override
	public void apply() {
		try {
			o.add(ClassReflection.newInstance(type));
		} catch (ReflectionException e) {
			if (type.isAssignableFrom(Boolean.class)) ;
				//o.add(new Boolean(false));
			else
				e.printStackTrace();
			//throw new GdxRuntimeException("failed to add new class of type "+type);
		}
		editor.queueRefresh(expander);
		isChecked = true;
	}

}
