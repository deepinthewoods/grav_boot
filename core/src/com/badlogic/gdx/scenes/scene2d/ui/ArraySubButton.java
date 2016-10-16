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

public class ArraySubButton extends TextButton implements Poolable, FieldButton{

	private Array o;
	
	private EditorTable table;
	private Class type;
	private EditorScreen editor;
	private ArrayExpander expander;
	int index;
	public ArraySubButton(String text, Skin skin){
		super(text, skin);
		
		
		
	}
	
	public void set( Array object, EditorTable table, EditorScreen editorScreen, Class type, ArrayExpander exp, int index) {
		expander = exp;
		this.index = index;
		this.table = table;
		
		this.o = object;
		this.editor = editorScreen;
		this.type = type;
		isChecked = true;
	}

	
	@Override
	public void reset(){
		super.reset();
		add(getLabel()).expand().fill();
		addListener(this.getClickListener());
		
	}

	@Override
	public void apply() {
		
		o.removeIndex(index);
		
		editor.queueRefresh(expander);
		isChecked = true;
	}

}
