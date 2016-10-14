package com.niz.ui.edgeUI;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.niz.component.BooleanInput;
import com.niz.component.Player;
import com.niz.component.Race;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;
import com.niz.ui.elements.BackgroundTouchNone;
import com.niz.ui.elements.RaceSelector;
import com.niz.ui.elements.SkillSelector;
import com.niz.ui.elements.UIElement;

public class CharacterScreen extends EdgeUI implements Observer {
	private static final String TAG = "character screen"
			;
	public boolean on;
	public InventoryScreen invScreen;
	private Stage theStage;
	private EngineNiz engine;
	RaceSelector raceSelector;
	private Subject raceNotifier;
	public StartGameButton startGameButton;

	public CharacterScreen(EngineNiz engine, Skin skin, TextureAtlas playerAtlas){
		this.engine = engine;
        engine.getSubject("inventoryToggle").add(this);
        raceNotifier = engine.getSubject("populatepaperdoll");
		 sides[0] = new UITable();
		 sides[0].min = new UIElement[1];
		 //sides[0].min[0] = new ControllerPad();
		 //table.row();
		 startGameButton = new StartGameButton();
		 sides[1] = new UITable();
		 sides[1].min = new UIElement[1];
		 //sides[1].max = new UIElement[1];
		 //table.row();
		 
		 sides[2] = new UITable();
		 sides[2].min = new UIElement[1];
		 //sides[2].min[0] = new ControllerPad();
		 //table.row();
		 
		 sides[3] = new UITable();
		 sides[3].vertical = true;
		 sides[3].min = new UIElement[1];
		 // sides[3].min[0] = new BlockSelector();
		 // sides[3].max = new UIElement[1];
		 //sides[3].max[0] = new BlockColorSelector();
		 table.row();
		 
		 sides[4] = new UITable();
		 sides[4].min = new UIElement[1];
		 //sides[4].min[0] = new ControllerPad();
		 // table.row();
		 
		 sides[5] = new UITable();
		 sides[5].min = new UIElement[1];
		 
		 //expandX[1] = true;
		 raceSelector = new RaceSelector();
		 sides[0].min[0] = raceSelector;
		 sides[1].min[0] = new SkillSelector();
		 sides[5].min[0] = startGameButton;
		 //sides[5].min[0] = new ControllerPad();
		 //table.row();
		 
		 sides[6] = new UITable();
		 sides[6].min = new UIElement[1];
		 //ControllerSliderBoolean slider = new ControllerSliderBoolean();;
		 //sides[6].min[0] = slider;
		 //sides[6].table.getCells().get(0).expand();
		 //table.row();
		 //this.expandX[6] = true;
		 
		 
		 sides[7] = new UITable();
		 sides[7].min = new UIElement[1];
		 //sides[7].min[0] = new ControllerButton("T", 0);
		 //table.row();
		 
		 sides[8] = new UITable();
		 sides[8].min = new UIElement[1];
		 //sides[8].min[0] = new ControllerButton("T", 0);
		 //ButtonPad btnPad = new ButtonPad();
		 //btnPad.send = new String[]{"screen"};
		 //sides[8].min[0] = btnPad; 
		 
		 //table.row();
		 
		 /*for (int y = 0, i = 0; y < 3; y++){
	            for (int x = 0; x < 3; x++, i++){
	                sides[i] = new UITable();
	                sides[i].min = new UIElement[1];
	                sides[i].min[0] = new ControllerSlider();
	            }
	            table.row();
	        }*/
		 back = new BackgroundTouchNone();
		
	}
	Family family = Family.one(Player.class).get();
	public void close(){
		ImmutableArray<Entity> arr = engine.getEntitiesFor(family);
		if (arr.size() == 0) return;
		Entity player = arr.get(0);
		Race race = player.getComponent(Race.class);
		raceSelector.set(race);
		raceNotifier.notify(player, Event.PAPERDOLL, null);
		
		theStage.clear();
		invScreen.addTo(theStage);
		on = false;
	}
	
	

	@Override
	public void addTo(Stage stage) {
		Gdx.app.log(TAG, "add to stage");
		on = true;
		super.addTo(stage);
	}

	@Override
	public void init(Skin skin, Stage stage, EngineNiz engine) {
		
		super.init(skin, stage, engine);
		theStage = stage;
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		BooleanInput b = (BooleanInput) c;
		if (!b.value && on){
			close();
			b.value = true;
			invScreen.on = true;
		}
	}
	
	
	
}
