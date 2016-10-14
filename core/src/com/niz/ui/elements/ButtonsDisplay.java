package com.niz.ui.elements;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ToggleableTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.ui.edgeUI.InventoryScreen;

public class ButtonsDisplay extends UIElement {

	@Override
	protected void onInit(Skin skin) {
		Table table = new Table();
		ToggleableTextButton butt = new ToggleableTextButton("Char", skin){
			
		};
		
		butt.addListener(new ChangeListener(){
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ToggleableTextButton tb = (ToggleableTextButton) actor;
				tb.setCheckedWithoutChanging(false);
				InventoryScreen ed = (InventoryScreen) parent.parent;
				ed.changeToCharacterScreen();
			}
			
		});
		table.add(butt);
		
		ToggleableTextButton settingsButt = new ToggleableTextButton("Setting", skin){
			
		};
		settingsButt.addListener(new ChangeListener(){
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ToggleableTextButton tb = (ToggleableTextButton) actor;
				tb.setCheckedWithoutChanging(false);
				InventoryScreen ed = (InventoryScreen) parent.parent;
				ed.changeToSettingsScreen();
			}
			
		});
		table.add(settingsButt);
		actor = table;
	}

}
