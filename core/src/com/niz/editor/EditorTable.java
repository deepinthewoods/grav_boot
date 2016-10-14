package com.niz.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ArrayAddButton;
import com.badlogic.gdx.scenes.scene2d.ui.ArrayExpander;
import com.badlogic.gdx.scenes.scene2d.ui.EditorLabel;
import com.badlogic.gdx.scenes.scene2d.ui.Expander;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.reflect.Field;

public class EditorTable extends Table implements Poolable{
	public EditorTable parentTable;
	public Object self;
	private EditorScreen ed;
	public String name;
	
	@Override
	public void reset(){
		self = null;
		parentTable = null;
	}
	public EditorTable(EditorScreen scr){
		ed = scr;
	}
	
	public void set(EditorTable parent, Object self, String name){
		this.parentTable = parent;
		this.self = self;
		this.name = name;
	}
	
	
	public void resetTable2(int depth){
		
		
		//tchildren.removeRange(2, tchildren.size-1);
		resetRecursively(this.getChildren(), depth, false);
		
	}


	private void resetRecursively(SnapshotArray<Actor> array, int depth, boolean deleteLabel) {
		Actor[] items = array.begin();
		 for (int i = array.size-1; i >= 0; i--) {
			 Actor act = items[i];
			 
			 if (act instanceof EditorTable){
				 EditorTable e = (EditorTable) act;
				 e.resetTableCompletely(depth);
				 //e.remove();
				 //this.removeActor(e);
				 //Pools.free(e);
				 Gdx.app.log("editor", "table freed");
				 //if (depth != 0)continue;
			 } 
			 /*if (act instanceof Group && !(act instanceof TextButton)){
				 Group w = (Group) act;
				 resetRecursively(w.getChildren());
				 Gdx.app.log("editor", "RECURSE"+act);
				 continue;
			 }*/
			 
			 if (act instanceof EditorLabel 
					 //|| (
					 //&& depth == 0  
					 //act instanceof Expander)
					 || act instanceof ArrayAddButton
					 )
			 {
				 Gdx.app.log("editor", "editor label skipped"+depth);
				 if (!deleteLabel)
					 continue;
			 } 
			// if (act instanceof TextField){
			//	 Gdx.app.log("editor", "TExt Field");
			//	 continue;
			 //}
			 if (act instanceof Expander){
				 Expander ex = (Expander) act;
				 if (!ex.isChecked()){
					 ed.rememberExpanded(ex.o);
					 Gdx.app.log("editort", "EXPANDER");
				 }
			 }
			 if (act instanceof ArrayExpander){
				 ArrayExpander ex = (ArrayExpander) act;
				 if (!ex.isChecked()){
					 ed.rememberExpanded(ex.o);
					 Gdx.app.log("editort", "ArrayExpander");
				 }
			 }
			 {
				 Pools.free(act);
				 act.remove();
				 
				 Gdx.app.log("editort", " freed " +act + "  TOTAL"+i);
			 }
		 }
		
		
		
	}

	public void addToStack() {
		if (parentTable != null){
			parentTable.addToStack();
		}
		//if (parentTable != null && parentTable.self != ed.stack.peek().self){
		if ((parentTable != null && parentTable.self == ed.stack.peek().self)){
			ed.addToStack(this);
		}
		
	}
	public void resetTableCompletely(int depth) {
		resetRecursively(this.getChildren(), depth, true);
		
	}
	
	
	public void setArray(Class<?> arrayCl, String labelN){
		isObject = false;
		
		this.arrayClass = arrayCl;
		labelName = labelN;
		
	}
	
	
	
	public void set(String name){
		this.labelName = name;
		isObject = true;
	}
	
	Field f;
	
	private int index;
	public EditorTable table;
	public String labelName;
	public Class<?> arrayClass;
	public boolean isObject;
	
	
}
