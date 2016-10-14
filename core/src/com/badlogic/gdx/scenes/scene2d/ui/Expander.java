package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.editor.EditorScreen;
import com.niz.editor.EditorTable;
import com.niz.editor.FieldButton;

public class Expander extends TextButton implements Poolable, FieldButton{

	public Object o;
	
	private EditorTable table;
	
	private EditorScreen editor;

	private TextButton titleButton;
	
	public Expander(String text, Skin skin){
		super(text, skin.get(TextButtonStyle.class));
		
	}
	
	public void set( Object object, EditorTable table, EditorScreen editorScreen, TextButton title) {
		//clear();
		this.table = table;
		this.o = object;
		this.editor = editorScreen;
		this.titleButton = title;
		setText("+");
		isChecked = true;
		//Gdx.app.log("expander", "set"+this.getListeners().size);
	}

	public void expand() {
		//Gdx.app.log("expander", "expanding "+o);
		editor.makeFor(o, table, 0);
	}
	
	public void contract(){
		table.resetTable2(0);
		//table.remove();
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
