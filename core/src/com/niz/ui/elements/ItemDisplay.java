package com.niz.ui.elements;

import java.util.Comparator;
import java.util.Iterator;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.BeltButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.InventoryButton;
import com.badlogic.gdx.scenes.scene2d.ui.ItemEquipButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.Pool;
import com.niz.Main;
import com.niz.anim.Animations;
import com.niz.component.Inventory;
import com.niz.component.ItemInput;
import com.niz.item.Item;
import com.niz.item.ItemDef;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;
import com.niz.ui.edgeUI.InventoryScreen;

public class ItemDisplay extends UIElement {

	private static final String TAG = "temdisplay";
	Table scrollPaneTable;
	private Pool<InventoryButton> pool;
	private Table backTable;
	private ScrollPane pane, infoPane;
	private Label info;
	private Table infoTable;
	private Array<ItemEquipButton> equipButtons = new Array<ItemEquipButton>();
	private Pool<ItemEquipButton> equipPool;
	private InventoryScreen editor;
	private EngineNiz engine;
	private Subject beltRefreshSubject;
	public Actor spacer;
	private Subject invNotifier;
	private Subject toastNotifier;
	
	
	//public InventoryInformationDisplay info;
	public ItemDisplay(InventoryScreen ed, EngineNiz engine) {
		//this.info = invDisplay;
		invNotifier = engine.getSubject("equipitem");
		editor = ed;
		this.engine = engine;
		beltRefreshSubject = engine.getSubject("inventoryRefresh");
		toastNotifier = engine.getSubject("toast");
	}
	protected ItemInput c = new ItemInput();

	@Override
	protected void onInit(Skin skin) {
		//if (true) throw new GdxRuntimeException("");
		Gdx.app.log(TAG, "INIT");
		final Skin sk = skin;
		
		pool = new Pool<InventoryButton>(){

			@Override
			protected InventoryButton newObject() {
				InventoryButton btn = new InventoryButton(sk);
				btn.addListener(new ChangeListener(){

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						//Gdx.app.log(TAG, "event"+event.getClass());
						InventoryButton act = (InventoryButton) event.getListenerActor();
						if (act.isChecked()){
							
							//inv.setActiveItem(butt.hash, butt.doingIndex);
							/*for (int i = 0; i < def.doings.size; i++){
							ItemEquipButton equip = equipPool.obtain();
							equip.set(i, def.doings.get(i), act.item, e);
							infoTable.row();
							infoTable.add(equip).left();
							
						}*/
						/*ItemEquipButton equip = equipPool.obtain();
						equip.set(0, act.item, e);
						infoTable.row();
						infoTable.add(equip).left();*/
						
						
						
						
						}
						
					}
					
					
					
				});
				btn.addListener(new ClickListener(){

					@Override
					public boolean touchDown(InputEvent event, float x,
							float y, int pointer, int button) {
						//Gdx.app.log(TAG, "checked"+event.getClass());
						InventoryButton act = (InventoryButton) event.getListenerActor();
						ItemDef def = act.item.getDef();
						info.setText(def.name + "\n\n"+ def.description);
						
						clearEquipButtons();
						
						BeltButton butt =  editor.belt.buttons[editor.belt.group.getCheckedIndex()];
						//int index = beltIndexOF(act2.item.hash, act2.index);
						
						beltRefreshSubject.notify(e, Event.BELT_REMOVE_DUPES, act.item);
						butt.setFrom(act.item, e);

						beltRefreshSubject.notify(e, Event.BELT_REFRESH, inv);
						butt.setShaking();
						act.setShaking();

						inv.setActiveItem(butt.hash, butt.doingSlot);
						inv.dirtyLimbs = true;
						invNotifier.notify(butt.e, Event.EQUIP_ITEM, null);

						//toastNotifier.notify(null, Event.BELT_TOUCH_START, null);
						//c.item = butt.item;
						//c.value = butt.doingSlot;
						//toastNotifier.notify(null, Event.CHANGE_DOING_SLOT, c);
						//toastNotifier.notify(null, Event.STOP_TOAST, null);
						
						return super.touchDown(event, x, y, pointer, button);
					}
					
				});
				return btn;
			}
			
		};
		
		equipPool = new Pool<ItemEquipButton>(){

			@Override
			protected ItemEquipButton newObject() {
				ItemEquipButton btn = new ItemEquipButton(sk);
				btn.addListener(new ChangeListener(){

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						
						/*ItemEquipButton act =  (ItemEquipButton) event.getListenerActor();
						act.setCheckedNoCallbacks(false);
						BeltButton butt =  editor.belt.buttons[editor.belt.group.getCheckedIndex()];
						int index = beltIndexOF(act.item.hash, act.index);
						act.e = e;
						butt.setFrom(act);
						if (index == -1 || index == editor.belt.group.getCheckedIndex()){
							//Gdx.app.log(TAG,  "equip" + editor.belt.group.getCheckedIndex());

						} else {
							//Gdx.app.log(TAG,  "UNSUCCESSFUL equip" + index);
							editor.belt.buttons[index].setFrom(null);
						}
						beltRefreshSubject.notify(null, Event.BELT_REFRESH, inv);
						butt.setShaking();
						//inv.setActiveItem(butt.hash, butt.doingIndex);*/
					}

					
					
				});
				return btn;
			}
			
		};
		
		scrollPaneTable = new Table();
		backTable = new Table();
		this.actor = backTable;
		pane  = new ScrollPane(scrollPaneTable);
		info = new Label("leb fgfdd afjklh lkjh lkjh lkj hlkj kj", skin, "inventory");
		info.setWrap(true);
		//info.setWidth(1);
		backTable.add(pane).left().top();
		
		infoTable = new Table();
		infoPane = new ScrollPane(infoTable);
		infoTable.add(info);
		infoTable.row();
		
		
		backTable.add(infoPane);
		
		backTable.row();
		/*Actor act = new Actor();
		act.setWidth(Gdx.graphics.getWidth()/2);
		backTable.add(act);*/
		spacer = new Actor();
		backTable.add(spacer);//*/
		
		//this.actor = new Actor();
		//table.setSize(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()*.82785f);
		//actor.debug();
	}
	ButtonGroup group = new ButtonGroup();
	private Inventory inv;
	private Entity e;
	
	
	public void clearEquipButtons(){
		infoTable.clear();
		infoTable.add(info).left().width(Gdx.graphics.getWidth()/2);
		for (int i = 0; i < equipButtons.size; i++){
			equipPool.free(equipButtons.get(i));
		}
		equipButtons.clear();
	}
	Array<Item> tmpItems = new Array<Item>();
	Comparator<Item> comparator = new Comparator<Item>(){

		@Override
		public int compare(Item a, Item b) {
			
			return a.getDef().name.compareTo(b.getDef().name);
			
		}
		
	};
	public void setFor(Inventory inv, Entity e){
		//Gdx.app.log(TAG,  "setting "+ inv.items.size);

		this.e = e;
		this.inv = inv;
		scrollPaneTable.clear();
		group.clear();
		for (int i = 0; i < buttons.size; i++){
			pool.free(buttons.get(i));
		}
		buttons.clear();
		
		tmpItems.clear();
		Values<Item> iter = inv.items.values();
		while (iter.hasNext()){
			tmpItems.add(iter.next());
		}
		iter.reset();
		tmpItems.sort(comparator);
		Iterator<Item> iiter = tmpItems.iterator();
		int w = 0, maxW = Gdx.graphics.getWidth() / Main.prefs.inventory_button_width / 2 - 1;
		while (iiter.hasNext()){
			
			Item item = iiter.next();
			ItemDef def = item.getDef();
			
			InventoryButton butt = pool.obtain();
			//ItemDef def = in.getItemAt(i).getDef();
			//butt.getImage().setDrawable(Animations.itemDrawables[def.id]);
			//butt.getImage().setOrigin(Align.center);
//			Sprite s = Animations.weaponSprites[def.id][0];
	//		if (s != null){
			butt.setFrom(item, e);
			
			//Gdx.app.log(TAG,  "added "+def.id);
		//	}
			//else {//TODO set blank
				//butt.drawable.getSprite().setAlpha(0f);
				
				//throw new GdxRuntimeException("jkls "+def.id);
			//}
			group.add(butt);
			buttons.add(butt);
			scrollPaneTable.add(butt);
			if (w++ == maxW){
				w = 0;
				scrollPaneTable.row();
			}
		}
		scrollPaneTable.layout();
		//Gdx.app.log(TAG,  "updated "+buttons.size);
	}
	public Array<InventoryButton> buttons = new Array<InventoryButton>();
	
	public void resize(int w, int h, ButtonStyle bStyle){
		for (int i = 0; i < buttons.size; i++){
			buttons.get(i).setStyle(bStyle);
		}
		//backTable
		//.setWidth(w/2);
		
		backTable.invalidateHierarchy();
		spacer.setWidth(w);
		
		scrollPaneTable.invalidate();
		scrollPaneTable.layout();
	}
}