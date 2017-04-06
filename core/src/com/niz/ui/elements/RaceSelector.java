package com.niz.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.component.Race;

public class RaceSelector extends UIElement {

	private static final String TAG = "race selector";
	RaceSelectionButton[] buttons = new RaceSelectionButton[Race.TOTAL_BODY_PARTS];
	private Race race;
	
	@Override
	protected void onInit(Skin skin) {
		Table table = new Table();
		
		for (int i = 0; i < Race.TOTAL_BODY_PARTS; i++){
			buttons[i] =new RaceSelectionButton(i, skin);
			table.add(buttons[i]).width(Gdx.graphics.getWidth()/2).left();
			table.row();
		}
		
		actor = table;
	}

	public void setFrom(Race race){
		this.race = new Race();
		this.race.setFrom(race);
		for (int i = 0; i < Race.TOTAL_BODY_PARTS; i++){
			buttons[i].current = race.raceID[i];
			//Gdx.app.log(TAG, "limb "+i+": "+buttons[i].current);
		}
	}
	

	public void set(Race race) {
		//this.race = race;
		for (int i = 0; i < Race.TOTAL_BODY_PARTS; i++){
			race.raceID[i] = buttons[i].current;
			//Gdx.app.log(TAG, "limb "+i+": "+buttons[i].current);
		}
	}
	
}
