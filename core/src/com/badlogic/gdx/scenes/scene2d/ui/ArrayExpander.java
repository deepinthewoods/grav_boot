package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.editor.EditorScreen;
import com.niz.editor.EditorTable;
import com.niz.editor.FieldButton;

public class ArrayExpander extends TextButton implements Poolable, FieldButton{

	public Array o;
	
	private EditorTable table;
	private Class type;
	private EditorScreen editor;

	private TextButton titleButton;

	public ArrayExpander(String text, Skin skin){
		super(text, skin);
		
		
		
		
	}
	
	public void set( Array object, EditorTable table, EditorScreen editorScreen, Class type, TextButton title) {
		
		this.table = table;
		this.titleButton = title;
		this.o = object;
		this.editor = editorScreen;
		this.type = type;
	}

	public void expand() {
		editor.makeForArray(o, table, 0, type, this);
		isChecked = false;
		setText("-");
	}
	
	public void contract(){
		/*SnapshotArray<Actor> actors = getChildren();
		for (Actor act : actors){
			if (act instanceof EditorTable){
				EditorTable e = (EditorTable) act;
				e.resetTable();
			}
			Pools.free(act);
		}*/
		
		//this.clear();
		isChecked = true;
		setText("+");
		table.resetTable2(0);
	}
	@Override
	public void reset(){
		super.reset();
		add(getLabel()).expand().fill();
		addListener(this.getClickListener());
		
	}

	@Override
	public void apply() {
		if (isChecked())
		
			editor.contract(this);
		 else 
			
			expand();
		setText();
		
	}

	public void setText() {
		if (isChecked()){
			setText("+");
			Array<Cell> cells = titleButton.getCells();
			for (int i = 0; i < cells.size; i++){
				cells.get(i).left();
			}
		}
		
		
			
		 else {
			setText("-");
			Array<Cell> cells = titleButton.getCells();
			for (int i = 0; i < cells.size; i++){
				cells.get(i).left();
			}
		 }
		
		
	}

	public void setCheckedWithoutChanging(boolean b) {
		isChecked = b;
		setText();
		
	}

}
