package com.niz.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class SkillSelector extends UIElement {
	ButtonGroup group = new ButtonGroup();
	@Override
	protected void onInit(Skin skin) {	
		Table table = new Table();
		table.add(new Label("jhfdsk;alkds", skin));
		
		actor = table;
	}

}
