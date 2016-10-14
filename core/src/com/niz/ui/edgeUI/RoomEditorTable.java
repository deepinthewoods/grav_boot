package com.niz.ui.edgeUI;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.niz.component.Grid2dInput;
import com.niz.component.RoomDefinition;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class RoomEditorTable extends Window implements Observer {

	private TextButton saveButton;
	private Subject notifier;
	private Table table;
	private ScrollPane scroll;
	private Label borderLabel;
	private TextButton borderButtonL;
	private TextButton borderButtonR;
	private TextButton borderButtonU;
	private TextButton borderButtonD;
	private Grid2dInput c;
	public static final String TAG = "room editor table";
	private EngineNiz engine;
	private TextButton borderButtonMin;
	private TextButton borderButtonMax;
	private TextButton patchButtonMin;
	private TextButton patchButtonMax;
	private Subject saveNotifier;
	
	public RoomEditorTable(EngineNiz engine, Skin skin){
		super("room details", skin);
		this.engine = engine;
		saveNotifier = engine.getSubject("save");
		notifier = engine.getSubject("roomeditor");
		notifier.add(this);
		onInit(skin);
	}
	
	
	protected void onInit(Skin skin) {
		// TODO Auto-generated method stub
		table = new Table();
		saveButton = new TextButton("save", skin, "mainmenu");
		saveButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log(TAG, "save");
				saveNotifier.notify(null, Event.SAVE_ROOM, null);
			}
			
		});
		//table.add(saveButton);
		
		borderLabel = new Label("border", skin);
		c = new Grid2dInput();
		borderButtonMin = new TextButton("border min", skin, "mainmenu");
		borderButtonMax = new TextButton("border max", skin, "mainmenu");
		patchButtonMin = new TextButton("patch min", skin, "mainmenu"); 
		patchButtonMax = new TextButton("patch max", skin, "mainmenu"); 

		borderButtonMin.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				//Gdx.app.log(TAG, "touch down");
				c.p.set(-1, 0);
				notifier.notify(null, Event.ROOM_BORDER_CHANGE, c);
				//Gdx.app.log(TAG, "clicked");
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		borderButtonMax.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				c.p.set(1, 0);
				notifier.notify(null, Event.ROOM_BORDER_CHANGE, c);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		patchButtonMin.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				c.p.set(-1, 0);
				notifier.notify(null, Event.ROOM_PATCH_CHANGE, c);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		patchButtonMax.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				c.p.set(1, 0);
				notifier.notify(null, Event.ROOM_PATCH_CHANGE, c);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		
		scroll = new ScrollPane(table);
		scroll.setScrollingDisabled(true, false);
		scroll.setLayoutEnabled(true);
		//table.setFillParent(true);
		this.add(table).expand();//.fill();
		this.setSize(180, 300);
	}
	
	public void makeMenu(RoomDefinition c){
		table.clear();
		table.add(borderButtonMin);
		table.row();
		table.add(borderButtonMax);
		table.row();
		table.add(patchButtonMin);
		table.row();
		table.add(patchButtonMax);
		table.row();
		//table.add(nameButton);
		table.row();
		table.add(saveButton).expand();
		table.row();
		//table.setSize(100,  5000);
		//this.setClip(true);
		//this.set
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		if (event == Event.ROOM_DEFINITION_CHANGE){
			makeMenu((RoomDefinition) c);
		}
	}

}
