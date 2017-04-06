package com.niz.ui.edgeUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.GameInstance;
import com.niz.WorldDefinition;
import com.niz.component.Race;
import com.niz.ui.elements.UIElement;

public class StartGameButton extends UIElement {

	private TextButton butt;
	private StartBtnListener listener;

	@Override
	protected void onInit(Skin skin) {
		butt = new TextButton("Start", skin, "mainmenu");
		listener = new StartBtnListener();
		butt.addListener(listener);
		actor = butt;
	}
	
	public void setFor(GameInstance game, WorldDefinition def, CharacterScreen ch, Race race){
		listener.game = game;
		listener.def = def;
		listener.charScreen = ch;
		listener.race = race;
	}

	private static final class StartBtnListener extends ChangeListener{
		public Race race;
		public GameInstance game;
		public WorldDefinition def;
		public CharacterScreen charScreen;
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			//if (charScreen == null) throw new GdxRuntimeException("NULL CHARSCREEN");
			//charScreen.raceSelector.set(race);
			//game.startNewGame(def);
		}
		
	}
}
