package com.niz.ui.edgeUI;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.SnapshotArray;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 26/05/2014.
 * one edge of an edgeUI
 */
public class UITable{
    //private transient int face;
   // private transient Table minTable, maxTable;
    public UIElement[] min, max;
    transient Table table = new Table();
    public boolean vertical = false;
    private boolean hidden = false;
    transient public EdgeUI parent;
	private SnapshotArray<Actor> actors = new SnapshotArray<Actor>();
    public UITable(){
       // minTable = new Table();
        //maxTable = new Table();

    }


    public void init(Skin skin,  EdgeUI parent, EngineNiz engine){
        this.parent = parent;
        for (UIElement e : min){
            if (e != null)
                e.init(skin,  this, engine);
        }
        if (max != null)
        for (UIElement e : max){
            if (e != null)
                e.init(skin,  this, engine);
        }
    }
    public void addTo(int i, EdgeUI edgeUI, boolean expandX, boolean expandY, boolean span){
    	table.clear();
        for (UIElement e : min){
            if (e != null){
                e.addTo(table);
                if (vertical) table.row();
            }
        }
        Cell cell = null;
        if (expandX){
        	if (expandY)
        		cell = edgeUI.table.add(table).expand();
        	else cell = edgeUI.table.add(table).expandX();
        }
        else {
        	if (expandY)
        		cell = edgeUI.table.add(table).expandY();
        	 else 
        		 cell = edgeUI.table.add(table);
        }
        if (span){
        	cell.colspan(100);
        }
        if (i == 3)cell.colspan(3);
        
        if (i == 3) cell.left().top();
        else if (i == 2 || i == 5) cell.right().top();
        else if (i == 6) cell.left().bottom();
        else if (i == 8) cell.right().bottom();
        else cell.left().top();
    }

    public void maximizeTo(Table maxTable){
        for (UIElement e : max) {
            if (e != null)
                e.addTo(maxTable);
        }
    }

    public void maximize() {
        parent.setMiddleScreen(this);

    }

    public void minimize() {
        parent.unsetMiddleScreen();
        
    }


    public void onMinimize() {
        for (UIElement e : max){
            if (e != null)
                e.onMinimize();
        }
    }

    public void onMaximize(){
        for (UIElement e : max){
            if (e != null)
                e.onMaximize();
        }
    }

    
	public void hide() {
		if (hidden) return;
		hidden = true;
		actors.clear();
		SnapshotArray<Actor> children = table.getChildren();
		for (int i = 0; i < children.size; i++){
			Actor child = children.get(i);
			actors.add(child);
		}
		table.clear();
	}
    
    public void unHide(){
    	if (!hidden) return;
    	hidden = false;
    	for (int i = 0; i < actors.size; i++){
    		table.add(actors.get(i));
    	}
    	actors.clear();
    }


	public boolean isHidden() {
		
		return hidden;
	}
    
    
    
}
