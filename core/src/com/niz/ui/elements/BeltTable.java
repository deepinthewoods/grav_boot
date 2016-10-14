package com.niz.ui.elements;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.BeltButton;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.niz.component.Inventory;
import com.niz.component.ItemInput;
import com.niz.component.SpriteAnimation;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;
import com.niz.ui.edgeUI.InventoryScreen;

public class BeltTable extends UIElement {

	protected static final String TAG = "belt table";
	public BeltButton[] buttons;
	public ButtonGroup group = new ButtonGroup();
	public Inventory inv;
	public Subject invNotifier;
	private Subject toastNotifier;
	private Subject screenNotifier;
	public BeltTable(EngineNiz engine) {
		invNotifier = engine.getSubject("equipitem");
		toastNotifier = engine.getSubject("toast");
		screenNotifier = engine.getSubject("screen");
	}
	protected ItemInput c = new ItemInput();
	private TextButton doneButton;
	private Table t;
	private Table buttonTable;
	
	@Override
	protected void onInit(Skin skin) {
		buttons = new BeltButton[InventoryScreen.BELT_SLOTS];
		doneButton = new TextButton("OK", skin);
		doneButton.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenNotifier.notify(null, Event.OK_BUTTON, null);
			}
			
		});
		t = new Table(skin);
		
		for (int i = 0; i < InventoryScreen.BELT_SLOTS; i++){
			buttons[i] = new BeltButton(skin);
			buttons[i].addListener(new ChangeListener(){

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					BeltButton butt = (BeltButton) event.getListenerActor();
					//butt.item.getDef().doings.get(butt.doingIndex).;
					//Gdx.app.log(TAG, "beltchange"+butt.hash);

					/*if (inv != null){
						Gdx.app.log(TAG, "beltBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"+butt.hash);
						//inv.primaryItem = butt.hash;
						//inv.primaryDoing = butt.doingIndex;
						inv.setActiveItem(butt.hash, butt.doingSlot);
						invNotifier.notify(butt.e, Event.EQUIP_ITEM, null);
						
					}*/
					
					
				}
				
				
				
			});
			final Subject invNotifier = this.invNotifier;
			buttons[i].addListener(new DragListener(){
				private boolean on = false;
				private float sx;
				private float sy;
				private int doingSlot;
				private int newSlot;
				@Override
				public void drag(InputEvent event, float x, float y, int pointer) {
					if (on == false){
						on = true;
						sx = x;
						sy = y;
					}
					boolean left  = Gdx.input.getX(pointer) - Gdx.graphics.getWidth()/2 > 0;

					float delta = (y - sy);
					int slot = (int)(delta / event.getListenerActor().getHeight());
					BeltButton b= (BeltButton) event.getListenerActor();
					if (b.item == null) b.item = Inventory.defaultItem;
					slot = Math.abs(slot);
					//slot %= b.item.getDef().doings.size;
					//doingSlot = slot;
					//Gdx.app.log("button", "drag "+slot + "  " + b.item.getDef().doings.size);
					//Gdx.app.log("button", "doingslot "+b.doingSlot);
					//b.doingSlot += slot;
					//b.doingSlot %= b.item.getDef().doings.size;
					int modulus = b.item.getDef().doings.size;//TODO
					newSlot = (slot)  ;
					if (newSlot != doingSlot){
						c.value = newSlot - doingSlot;
						doingSlot = newSlot;
						c.item = b.item;
						c.butt = b;
						c.left = left;
						//Gdx.app.log("button", "doingslot after "+c.value);
						toastNotifier.notify(b.e, Event.CHANGE_DOING_SLOT, c);
					}
					
					
					//super.drag(event, x, y, pointer);
				}

				@Override
				public void dragStop(InputEvent event, float x, float y,
						int pointer) {
					//Gdx.app.log("button", "dragstop");
					on = false;
					super.dragStop(event, x, y, pointer);
					BeltButton b= (BeltButton) event.getListenerActor();
					//b.doingSlot = newSlot;
					doingSlot = 0;
				}

				@Override
				public void touchUp(InputEvent event, float x, float y,
						int pointer, int button) {
					BeltButton butt= (BeltButton) event.getListenerActor();
					//Gdx.app.log("button", "touchup"+(Gdx.input.getX(pointer) - Gdx.graphics.getWidth()/2));

					boolean left  = Gdx.input.getX(pointer) - Gdx.graphics.getWidth()/2 > 0;
					super.touchUp(event, x, y, pointer, button);
					if (inv != null){
						//inv.primaryItem = butt.hash;
						//inv.primaryDoing = butt.doingIndex;
						if (butt.item != null && butt.e != null){
							
							inv.setActiveItem(butt.hash, butt.doingSlot);
							invNotifier.notify(butt.e, Event.EQUIP_ITEM, null);
							c.item = butt.item;
							c.value = butt.doingSlot;
							c.left = left;
							butt.e.getComponent(Inventory.class).dirtyLimbs = true;
							SpriteAnimation anim = butt.e.getComponent(SpriteAnimation.class);
							
						} else {
							c.item = Inventory.defaultItem;
							c.left = left;
							inv.setActiveItem(0, 0);
							//Gdx.app.log(TAG, "beltBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"+butt.hash);
						}
						inv.dirtyLimbs = true;
						//throw new GdxRuntimeException("null button");
						//toastNotifier.notify(null, Event.BELT_TOUCH_START, null);
						toastNotifier.notify(butt.e, Event.CHANGE_DOING_SLOT, c);	
						toastNotifier.notify(null, Event.STOP_TOAST, c);
						
					}
				}

				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					boolean left  = Gdx.input.getX(pointer) - Gdx.graphics.getWidth()/2 > 0;

					BeltButton butt= (BeltButton) event.getListenerActor();
					butt.setChecked(true);
					if (butt.item != null ){
						c.item = butt.item;
					} else {
						c.item = Inventory.defaultItem;
					}
					doingSlot = 0;
					c.value = butt.doingSlot;
					c.left = left;
					c.butt = butt;
					//throw new GdxRuntimeException("null button");
					toastNotifier.notify(null, Event.BELT_TOUCH_START, null);
					toastNotifier.notify(butt.e, Event.CHANGE_DOING_SLOT, c);						
					//Gdx.app.log(TAG, "beltBBBBCCCCCCCCCCCCCCBBBB"+c.left);
					return super.touchDown(event, x, y, pointer, button);
					
				}
				
			});
		
			t.add(buttons[i]).padBottom(10);//.size(Main.prefs.inventory_button_width, Main.prefs.inventory_button_height);
			group.add(buttons[i]);
		}
		buttonTable = new Table();
		t.add(buttonTable);
		
		actor = t;

	}

	public void enableOk() {
		buttonTable.clear();
		

		buttonTable.add(doneButton).padBottom(10);
	}
	
	public void disableOk(){
		buttonTable.removeActor(doneButton);
	}

}
