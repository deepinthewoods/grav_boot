package com.niz.ui.edgeUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.ui.elements.UIElement;

public class InventoryInformationDisplay extends UIElement {

	private Table t;
	private Actor a;

	@Override
	protected void onInit(Skin skin) {
		t = new Table();
		a = new Actor();
		a.setSize(Gdx.graphics.getWidth()/2f,  Gdx.graphics.getHeight()/2f);
		//t.add(a);
		Label l = new Label("ettys  diud", skin, "inventory");
		t.add(l);
		l.setWrap(true);
		
		
		
		actor = t;
		//actor.setSize(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
	}

}
