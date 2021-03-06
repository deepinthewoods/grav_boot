package com.niz.ui.edgeUI;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.BeltButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageN;
import com.badlogic.gdx.scenes.scene2d.ui.InventoryButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.MomentaryButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SliderNiz;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.niz.GameInstance;
import com.niz.Input;
import com.niz.Main;
import com.niz.anim.Animations;
import com.niz.anim.SpriteCacheNiz;
import com.niz.component.Body;
import com.niz.component.BooleanInput;
import com.niz.component.Door;
import com.niz.component.HealthChangeInfo;
import com.niz.component.Inventory;
import com.niz.component.ItemInput;
import com.niz.component.OnDoor;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.item.Doing;
import com.niz.item.Item;
import com.niz.item.ItemDef;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;
import com.niz.system.LightRenderSystem;
import com.niz.system.MapRenderSystem;
import com.niz.system.OverworldSystem;
import com.niz.system.SpriteAnimationSystem;
import com.niz.ui.elements.BackgroundClickDrag;
import com.niz.ui.elements.BeltTable;
import com.niz.ui.elements.ButtonsDisplay;
import com.niz.ui.elements.ControllerButton;
import com.niz.ui.elements.ControllerSliderBoolean;
import com.niz.ui.elements.ItemDisplay;
import com.niz.ui.elements.UIElement;


public class InventoryScreen extends EdgeUI implements Observer{
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	private static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);

	public static final int BELT_SLOTS = 8;
	private static final String TAG = "inv scre";
	private static final int TOTAL_TOAST_LABELS = 16;
	private static final float TOAST_SPEED = 1f;
	private static final float TOAST_DELAY = 1f;
	private final ShaderProgram shader;
	private final MapRenderSystem map;
	private final LightRenderSystem lights;
	private final SpriteAnimationSystem spriteRenderer;
	private final int u_texture;
	private final int u_index_texture;
	private final OverworldSystem overworld;
	public BeltTable belt;
	private ControllerButton btnPad;
	private ControllerSliderBoolean slider;
	//private ButtonInput buttonInput;
	Skin skin;
	private ItemDisplay inv;
	//private InventoryInformationDisplay invDisplay;
	private boolean showInv, showDoorButton;
	public boolean on = true;
	private Subject invRefreshSubject;
	private BackgroundClickDrag backgroundDragger;
	private CharacterScreen charScreen;
	private SettingsScreen settingsScreen;
	private RoomEditorTable roomEditor;
	private TextButton[] doorButtons;
	private Table doorTable;
	private float playerHealth = 0f;

	public InventoryScreen(final EngineNiz engine, Skin skin, CharacterScreen charScr, SettingsScreen setScr, ShaderProgram indexedShader){
		Subject healthChangeNotifier = ((EngineNiz) engine).getSubject("health");
		healthChangeNotifier.add(new Observer(){

			@Override
			public void onNotify(Entity e, Event event, Object c) {
				HealthChangeInfo info = (HealthChangeInfo) c;
				if (info.isPlayer){
					playerHealth = info.health / (float)info.maxHealth;
					Gdx.app.log(TAG, "FDSKJJFDKLSJFASDKJFSLKFJSD");
				}
			}
		});
		this.shader = indexedShader;
		u_index_texture = shader.getUniformLocation("u_index_texture");
		u_texture = shader.getUniformLocation("u_texture");
		settingsScreen = setScr;
		charScreen = charScr;
		map = engine.getSystem(MapRenderSystem.class);
		lights = engine.getSystem(LightRenderSystem.class);
		spriteRenderer = engine.getSystem(SpriteAnimationSystem.class);
		overworld = engine.getSystem(OverworldSystem.class);
		this.skin = skin;
		
		doorTable = new Table();
		doorButtons = new TextButton[8];
		for (int i = 0; i < doorButtons.length; i++){
			doorButtons[i] = new TextButton("door "+i, skin);
			doorTable.add(doorButtons[i]);
			final int index = i;
			doorButtons[i].addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					((TextButton)event.getListenerActor()).setChecked(true);
					long id = currentDoor.doors.get(index);
					Entity door = engine.getEntity(id);
					Entity player = currentDoorE;
					Position pPos = player.getComponent(Position.class);
					Door doorC = door.getComponent(Door.class);
					GridPoint2 dPos = doorC.endPoint;
					Body body = player.getComponent(Body.class);
					Body dBody = door.getComponent(Body.class);
					pPos.pos.set(dPos.x + .5f, dPos.y +1);
					
					//Gdx.app.log(TAG, "move player" + pPos.pos + dPos);
					super.clicked(event, x, y);
				}
			});
		}
		doorTable.setPosition(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/2);
		
		
        //sides[0].min[0] = new ControllerPad();
		belt = new BeltTable(engine);
		
		
		sides[0] = new UITable();
		sides[0].min = new UIElement[1];
		sides[0].min[0] = belt;
		sides[0].min[0].send = new String[]{"screen"};
		//this.expandX[3] = true;
		//this.expandX[8] = true;
		//this.
        //table.row();

        sides[1] = new UITable();
        sides[1].min = new UIElement[1];
       // sides[1].min[0] = settingsScreen.getElement();
        /*sides[1].min[0] = new UIElement(){

			@Override
			protected void onInit(Skin skin) {
				actor = new Actor();
				
			}
        	
        };*/
        /*ButtonPad pad = new ButtonPad(player);
        pad.input_l.code = Input.WALK_LEFT;
        pad.input_r.code = Input.WALK_RIGHT;
        pad.input_jump.code = Input.JUMP;*/
        //sides[1].max = new UIElement[1];
        //table.row();
        
        invRefreshSubject = engine.getSubject("inventoryRefresh");
        invRefreshSubject.add(this);
        engine.getSubject("resize").add(this);
        engine.getSubject("inventoryToggle").add(this);
        engine.getSubject("screen").add(this);


		sides[2] = new UITable();
        sides[2].min = new UIElement[1];
        //sides[2].min[0] = new ControllerPad();
        table.row();

        sides[3] = new UITable();
        sides[3].vertical = true;
        sides[3].min = new UIElement[1];
        //invDisplay = new InventoryInformationDisplay();
        
        inv = new ItemDisplay(this, engine);

		sides[3].min[0] = inv;
        /*sides[3].min[0] = new UIElement(){

			@Override
			protected void onInit(Skin skin) {
				this.actor = new Actor();
				actor.setSize(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()*.75f);
				
				
			}
        	
        };//*/
       // sides[3].max = new UIElement[1];
        //sides[3].max[0] = new BlockColorSelector();
        //table.row();

        sides[4] = new UITable();
        sides[4].min = new UIElement[1];
        
        //sides[3].min[0] = invDisplay;//new InventoryInformationDisplay();
        //table.row();

        sides[5] = new UITable();
        sides[5].min = new UIElement[1];
        //sides[5].min[0] 
        roomEditor	= new RoomEditorTable(engine, skin);
        //table.row();

        sides[6] = new UITable();
        sides[6].min = new UIElement[1];
        slider = new ControllerSliderBoolean();
		sides[6].min[0] = slider;
        //sides[6].table.getCells().get(0).expand();
        //table.row();
        this.expandX[6] = true;
        //expandY[8] = true;

        sides[7] = new UITable();
        sides[7].min = new UIElement[1];
        /*sides[7].min[0] = new UIElement(){

			@Override
			protected void onInit(Skin skin) {
				actor = new Actor();
				
			}
        	
        };*/
        sides[7].min[0] = new ButtonsDisplay();

        btnPad = new ControllerButton("j", Input.JUMP);//"J", Input.JUMP);
        //btnPad.send = new String[]{"screen"};
       // sides[7].min[0] = new ControllerButton("T", 0);
        //table.row();

        sides[8] = new UITable();
        sides[8].min = new UIElement[1];
        sides[8].min[0] = btnPad;
       
        //buttonInput = new ButtonInput();
        //Entity e = null;
        
        //table.row();

        /*for (int y = 0, i = 0; y < 3; y++){
            for (int x = 0; x < 3; x++, i++){
                sides[i] = new UITable();
                sides[i].min = new UIElement[1];
                sides[i].min[0] = new ControllerSlider();
            }
            table.row();
        }*/
        backgroundDragger = new BackgroundClickDrag();
        
        back = backgroundDragger;
    
	}
	
	public void setRoomEditor(boolean on){
		if (on){
			//sides[5].unHide();
			stage.addActor(roomEditor);
			roomEditor.setPosition(0, 200);
			
		} else {
			
			//sides[5].hide();
		}
	}
	
	private boolean toastOn;
	private float toastTimer = 9999f;
	private int toastSelectedIndex;
	private int totalDoings = 1;

	Label toastTitleLabel;
	Array<DoingLabel> toastLabels = new Array<DoingLabel>();
	private Stage theStage;
	private Subject toastSubject;
	@Override
	public void init(Skin skin, Stage stage, EngineNiz engine) {
		// TODO Auto-generated method stub
		theStage = stage;
		super.init(skin, stage, engine);
		if (btnPad.actor == null) throw new GdxRuntimeException("null btn");
		 btnPad.actor.setHeight(Main.prefs.control_button_height);
		
		toastTitleLabel = new Label("Item", skin, "iteminfotitle");
		toastTitleLabel.setX(0);
		toastTitleLabel.setY(Gdx.graphics.getHeight());
		toastTitleLabel.setTouchable(Touchable.disabled);
		stage.addActor(toastTitleLabel);
		
		for (int i = 0; i < TOTAL_TOAST_LABELS; i++){
			//Label lab = new Label("--"+i, skin, "iteminfo");
			DoingLabel lab = new DoingLabel(i, skin, engine);
			lab.setTouchable(Touchable.enabled);
			stage.addActor(lab);
			toastLabels.add(lab);
		}
		
		final Skin skinn = skin;
		toastSubject = engine.getSubject("toast");
		toastSubject .add(new Observer(){


			@Override
			public void onNotify(Entity e, Event event, Object c) {
				//Gdx.app.log(TAG, "NOTIFY" + event);
				 if (event == Event.STOP_TOAST){
				 	//Gdx.app.log(TAG, "stop toast");
					toastOn = false;
					toastTimer = 0;
					for (int i = 0; i < toastLabels.size; i++){
						//Label prev = toastTitleLabel;
						//if (i != 0) prev = toastLabels.get(i-1);
						DoingLabel lab = toastLabels.get(i);
						lab.setTouchable(Touchable.disabled);
						lab.setScale(0f, 0f);
					}
					//if (true)throw new GdxRuntimeException("hkl");

				} else if (event == Event.CHANGE_DOING_SLOT) {
					//if (e == null) throw new GdxRuntimeException("jkds");
					
					ItemInput input = (ItemInput) c;
					if (input.item == null) {
						toastTimer = 9999;
						return;
					}
					//input.value %= totalDoings;
					ItemDef def = input.item.getDef();
					 toastTitleLabel.setText(""+def.name);
					//toastSelectedIndex = input.value;
					//for (int i = 0; i < toastLabels.size; i++){
					//Gdx.app.log(TAG, "toast" + toastSelectedIndex+" "+input.value + "  "+ totalDoings);
					toastSelectedIndex = input.value;
					if (toastSelectedIndex < 0) toastSelectedIndex += totalDoings;
					if (toastSelectedIndex >= totalDoings) toastSelectedIndex -= totalDoings;
					//Gdx.app.log(TAG, "toast" + toastSelectedIndex+" "+input.value);
					//}
					int adjustedIndex = 0;
					for (int i = 0; i < toastLabels.size; i++){
						float prevHeight, prevY, prevWidth, prevX;
						DoingLabel lab = toastLabels.get(i);
						if (i == 0){
							prevHeight = toastTitleLabel.getHeight();
							prevY = toastTitleLabel.getY();
							prevY = Gdx.graphics.getHeight() - Main.prefs.inventory_button_height - lab.getHeight();
							prevWidth = 0;//toastTitleLabel.getWidth();
							prevX = 0;//toastTitleLabel.getX();
						} else {
							prevHeight = toastLabels.get(i-1).getHeight();
							prevY = toastLabels.get(i-1).getY();
							prevWidth =  toastLabels.get(i-1).getWidth();
							prevX =  toastLabels.get(i-1).getX();
						}
						//Label prev = toastTitleLabel;
						//if (i != 0) prev = toastLabels.get(i-1);
						//Gdx.app.log(TAG, "prevw " +prevWidth + "  from " + prevX);
						lab.setX(prevX + prevWidth);
						lab.setY(prevY );
						//lab.setX(0);;
						//if (input.value == i) text += "* ";
						//if (i < def.doings.size) text += def.doings.get(i).name;
						//if (input.value == i)lab.setStyle(skinn.get("iteminfoselected", LabelStyle.class));
						//else lab.setStyle(skinn.get("iteminfo", LabelStyle.class));
						if (adjustedIndex >= def.doings.size){
							if (adjustedIndex == def.doings.size)totalDoings = i;
							lab.setNotSelected(skinn);
							lab.set(def, adjustedIndex, e, input.left, input.butt);
							adjustedIndex++;
							continue;
						}
						if (e == null) return;
						Race race = raceM.get(e);
						while (adjustedIndex < def.doings.size && !race.enabledLimb[def.doings.get(adjustedIndex).limbIndex] ){
							adjustedIndex++;
						}
						
						if (i == toastSelectedIndex){
							lab.setSelected(skinn);
							input.butt.doingSlot = adjustedIndex;
						}
						else lab.setNotSelected(skinn);
						
						lab.set(def, adjustedIndex, e, input.left, input.butt);
						
						adjustedIndex++;
						//Gdx.app.log(TAG, "done t " + toastSelectedIndex+" "+input.value + "  "+ totalDoings);
					}
					totalDoings = adjustedIndex+1;
					if (showInv){
						this.onNotify(e, Event.STOP_TOAST, null);
					}
					//table.invalidateHierarchy();
				} else if (event == Event.BELT_TOUCH_START){
				
					//if (true) throw new GdxRuntimeException("dsfjkhl;ksd");
					if (!showInv){
						toastOn = true;
						toastTimer = 0;
						
					}
				}
				
			}
			
		});
		invToggleComponent.value = false;
		this.onNotify(null, Event.INVENTORY_TOGGLE, invToggleComponent);
		this.onNotify(null, Event.INVENTORY_TOGGLE, invToggleComponent);
		//table.getCell(sides[7].table).left();
	}
	BooleanInput invToggleComponent = new BooleanInput();
	private OnDoor currentDoor;
	private Entity currentDoorE;
	@Override
	public void onNotify(Entity e, Event event, Object c) {
		//Gdx.app.log(TAG,  "notify " + event);
		if (event == Event.ON_DOOR){
			OnDoor onDoor = (OnDoor) c;
			currentDoor = onDoor;
			currentDoorE = e;
			doorButtonsOn = true;
		}
		else
		if (event == Event.BELT_REMOVE_DUPES){
			Item item = (Item) c;
			for (int i = 0; i <  InventoryScreen.BELT_SLOTS; i++){
				BeltButton butt = belt.buttons[i];
				if (butt.hash == item.hash){
					butt.unSet();
				}
				
			}
			
		}
		else if (event == Event.BELT_REFRESH ){

			Inventory in = (Inventory) c;
			for (int i = 0; i <  InventoryScreen.BELT_SLOTS; i++){
				BeltButton butt = belt.buttons[i];
				
				if (butt == null) throw new GdxRuntimeException("button null");
				Item item = in.getItem(butt.hash);
				if (item == null) {
					butt.getImage().setDrawable(null);
					//Gdx.app.log(TAG,  "no item "+i);


					continue;
					//throw new GdxRuntimeException("jkljkl");
				}
				ItemDef def = item.getDef();
				//Gdx.app.log(TAG,  "blet redtawafr");
				//butt.invalidateHierarchy();
				//butt.setFrom(item, e);
				//butt.getImage().setDrawable(Animations.itemDrawables[def.id]);
				//butt.getImage().setRotation(20f);
				//butt.getImage().setOrigin(Align.center);
				butt.setFrom(item, e);
			}
			Table t = (Table)(belt.actor);//.invalidateAll();
			
			//t.invalidateHierarchy();
			
		} else if (event == Event.INVENTORY_REFRESH){
			Inventory inventory = (Inventory) c;
			if (e == null) throw new GdxRuntimeException("null e");
			inv.setFor(inventory, e);
			belt.inv = inventory;
			//sides[3].hide();;
			//sides[5].hide();;
		}else if (event == Event.RESIZE){
		
			
			
			//Gdx.app.log(TAG,  "resize");
			Slider s = (Slider) slider.actor;
			SliderStyle style = skin.get("default-horizontal", Slider.SliderStyle.class);
			if (Main.prefs == null) return;
			style.background.setMinHeight(Main.prefs.control_button_height);
			s.setStyle(style);
			
			ControllerButton s2 = btnPad;
			ButtonStyle jumpButtonStyle = skin.get("momentaryButton", Button.ButtonStyle.class);
			//if (Main.prefs == null) return;
			//Gdx.app.log(TAG, "RESIZE"+Main.prefs.control_button_height);
			jumpButtonStyle.up.setMinHeight(Main.prefs.control_button_height);
			jumpButtonStyle.down.setMinHeight(0);
			
			jumpButtonStyle.checked.setMinHeight(0);
			jumpButtonStyle.up.setMinWidth(Main.prefs.jump_button_width);
			jumpButtonStyle.down.setMinWidth(Main.prefs.jump_button_width);
			jumpButtonStyle.checked.setMinWidth(Main.prefs.jump_button_width);
			MomentaryButton m = (MomentaryButton) s2.actor;
			
			m.setStyle(jumpButtonStyle);
			m.setHeight(Main.prefs.control_button_height);
			//sides[5].hide();
			
			ButtonStyle bStyle = skin.get("inventory", Button.ButtonStyle.class);
			//if (Main.prefs == null) return;
			//bStyle.up.setMinHeight(Main.prefs.inventory_button_height);
			//bStyle.down.setMinHeight(Main.prefs.inventory_button_height);
			//bStyle.checked.setMinHeight(Main.prefs.inventory_button_height);
			//bStyle.up.setMinWidth(Main.prefs.inventory_button_width);
			//bStyle.down.setMinWidth(Main.prefs.inventory_button_width);
			//bStyle.checked.setMinWidth(Main.prefs.inventory_button_width);
			//*/
			Viewport v = stage.getViewport();
			inv.resize(v.getScreenWidth()/2, v.getScreenHeight()/2, bStyle);
			//inv.actor.setSize(stage.getViewport().getWorldWidth()/2, stage.getViewport().getWorldHeight()/2);
			//invDisplay.actor.setSize(v.getScreenWidth()/2 , v.getScreenHeight()/2);
			//Gdx.app.log(TAG,  "gutter"+v.getLeftGutterWidth() + "  " + v.getRightGutterWidth());
			//invDisplay.actor.setSize(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
			backgroundDragger.actor.setSize(v.getScreenWidth(), v.getScreenHeight());
			
			toastTitleLabel.setX(0);
			toastTitleLabel.setWidth(v.getScreenWidth());
			toastTitleLabel.setY(v.getScreenHeight()/3*2);
			toastTitleLabel.setAlignment(Align.center);
			for (int i = 0; i < TOTAL_TOAST_LABELS; i++){
				DoingLabel lab = toastLabels.get(i);
				
				lab.resize(v.getScreenWidth(), Main.prefs.action_label_size, v.getScreenHeight());
				
			}
			
			//*/
			//table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			//table.layout();
			//table.invalidate();
			table.invalidateHierarchy();
			table.layout();
		} else if (event == Event.INVENTORY_TOGGLE){
			if (on){
				
				//toastSubject.notify(e, Event.STOP_TOAST, c);
				BooleanInput b = (BooleanInput) c;
				b.value ^= false;
				if (settingsOn){
					Main.savePrefs();
					for (BeltButton bu : belt.buttons){
						Array<EventListener> arr;
						if (cachedListeners.containsKey(bu)){
							arr = cachedListeners.get(bu);
						}else {
							//Gdx.app.log(TAG, "no cached listeners");
							continue;
						}						
						bu.clearListeners();
						
						for (EventListener o : arr){
							bu.addListener(o);
						}						
						arr.clear();
					}
					
					Actor bu = slider.actor;
					Array<EventListener> arr;
					if (cachedListeners.containsKey(bu)){
						arr = cachedListeners.get(bu);
					}else {
						//Gdx.app.log(TAG, "no cached listeners");
						arr = new Array<EventListener>();
					}						
					bu.clearListeners();
					
					for (EventListener o : arr){
						bu.addListener(o);
					}		
					arr.clear();
					
					
					settingsOn = false;
					showInv = false;
					sides[3].hide();
					sides[7].hide();
					sides[6].unHide();
					sides[8].unHide();
				} else {
					
					if (showInv){
						showInv = false;
						sides[3].hide();
						sides[7].hide();
						sides[6].unHide();
						sides[8].unHide();
					} else {
						//Gdx.app.log(TAG, "show inv");
						showInv = true;
						sides[3].unHide();
						sides[7].unHide();
						sides[6].hide();
						sides[8].hide();
						
					}
				}
				

			}
			
		} else if (event == Event.OK_BUTTON_ENABLE){
			enableOkButton();
		} else if (event == Event.OK_BUTTON_DISABLE){
			disableOkButton();
		}
		
	}
	
	public void hideAllButtons(){
		sides[3].hide();
		sides[7].hide();
		sides[6].hide();
		sides[8].hide();
	}
	
	private static final String SPRITE_FILENAME_PREFIX = "diff/tile";
	

	private AtlasSprite getInventorySprite(ItemDef def) {
		//Gdx.app.log(TAG, "inv spr"+def.id);
		return Animations.itemSprites[def.id][0];
	}

	public Table getTable() {
		return table;
	}

	Vector2 tmpV = new Vector2(), tmpV2 = new Vector2(), tmpV3 = new Vector2(), tmpV4 = new Vector2(), tmpV5 = new Vector2();
	Matrix4 matrix = new Matrix4();
	private boolean doorButtonsWasOn;

	public void draw(ShapeRenderer rend, SpriteBatchN batch, BitmapFont font) {

		if (doorButtonsOn){
			if (!doorButtonsWasOn){
				doorTable.clear();
				for (int i = 0; i < currentDoor.doors.size && currentDoor != null; i++){
					doorTable.addActor(doorButtons[i]);
					doorButtons[i].setChecked(true);
				}
				stage.addActor(doorTable);
				//Gdx.app.log(TAG, "to on");
			}
			doorButtonFontHeight = doorButtons[0].getHeight();
			doorTable.setPosition(Gdx.graphics.getWidth()/2
					, Gdx.graphics.getHeight() - Main.prefs.inventory_button_height - doorButtonFontHeight);
			//Gdx.app.log(TAG, "onnn");
		} else if (doorButtonsWasOn){
			//Gdx.app.log(TAG, "off");
			doorTable.remove();
		}
		doorButtonsWasOn = doorButtonsOn;
		doorButtonsOn = false;
		float x, y, w, h;
		float alpha = 1f;
		if (!toastOn){
			//toastTimer += delta;
			alpha = Math.max(0,  1f - toastTimer * TOAST_SPEED);
			//Gdx.app.log(TAG, "alpha " + alpha);
		}
		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		Gdx.gl.glLineWidth(1f);//*/

		//batch.setShader(null);
		//batch.setColor(Color.WHITE);
		batch.begin();

		matrix.set(batch.getProjectionMatrix());
		spriteRenderer.drawColors();

		batch.end();
		map.drawAllWhiteColorRamp(batch);
		batch.getProjectionMatrix().set(matrix);

		batch.setShader(shader);
		matrix.set(batch.getProjectionMatrix());
		spriteRenderer.drawColors();
		batch.getProjectionMatrix().set(matrix);
		batch.setColor(Color.WHITE);
		batch.disableTextureBinding();
		SpriteAnimationSystem.indexBuffer.getColorBufferTexture().bind(1);
		//shader.setUniformi(u_index_texture, 1); //passing first texture!!!
		SpriteAnimationSystem.atlasTexture.bind(0);
		//shader.setUniformi(u_texture, 0);

		batch.begin();

		for (InventoryButton b : InventoryButton.itemDrawList){//works with blocks
			b.draw2(batch, 1f);
		}
		for (InventoryButton b : InventoryButton.blockDrawList){
			b.draw2(batch, 1f);
			//Gdx.app.log(TAG, "draw block " + b.item);
		}
		batch.end();

		InventoryButton.blockDrawList.clear();
		InventoryButton.itemDrawList.clear();
		batch.setShader(null);
		batch.enableTextureBinding();
		if (overworld.isNewGameScreen()) return;
		batch.begin();
		rend.setColor(Color.DARK_GRAY);
		rend.begin(ShapeType.Line);
		if (!sides[3].isHidden()) {
			for (int i = 0; i < inv.buttons.size; i++){
				InventoryButton butt3 = inv.buttons.get(i);
				//butt3.draw2(batch, 1f);
				//if (butt.hash == 0) continue;
				//Gdx.app.log(TAG, "draw item amount");
				drawButton(butt3, batch, rend);
			}
		}
		if (!sides[0].isHidden()) {
			for (int i = 0; i < BELT_SLOTS; i++){
				InventoryButton butt2 = belt.buttons[i];
				drawButton(butt2, batch, rend);
			}
		}
		drawButton(belt.buttons[belt.group.getCheckedIndex()], batch, rend, true);
		rend.end();

		rend.setColor(Color.RED);
		rend.begin(ShapeType.Filled);
		tmpV.set(0, 0);
		belt.buttons[0].localToScreenCoordinates(tmpV);
		rend.rect(0, tmpV.y, Gdx.graphics.getWidth() * playerHealth, 30);

		rend.end();
		batch.end();
		//
		batch.begin();
		if (!sides[0].isHidden()) {
			for (int i = 0; i < BELT_SLOTS; i++){
				InventoryButton butt2 = belt.buttons[i];
				//if (butt.hash == 0) continue;
				drawButtonAmount(butt2, batch, font);
			}
		}

		if (!sides[3].isHidden()) {
			for (int i = 0; i < inv.buttons.size; i++){
				InventoryButton butt3 = inv.buttons.get(i);
				//Gdx.app.log(TAG, "draw item amount");
				drawButtonAmount(butt3, batch, font);
			}
		}
		batch.end();
		rend.begin(ShapeType.Line);
		rend.setColor(Color.WHITE);
		for (int i = 0; i < checkedQueue.size / 4; i++){
			drawShape(rend, checkedQueue.get(i*4), checkedQueue.get(i*4+1), checkedQueue.get(i*4+2), checkedQueue.get(i*4+3));
			//Gdx.app.log(TAG,  "checked"+checkedQueue.get(i*4));
			//rend.setColor(Color.LIGHT_GRAY);
		}
		checkedQueue.clear();
		rend.end();
		if (showInv) return;

		/*rend.begin(ShapeType.Filled);
		for (int i = 0; i < toastLabels.size; i++){
			DoingLabel lab = toastLabels.get(i);
			if (!lab.disableDraw && lab.doing != null){
				int doingType = lab.doing.doingTypeIndex;
				
				x = lab.getX();
				y = lab.getY();
				w = lab.getWidth()/2f;
				h = lab.getHeight()/2f;
				if (!lab.selected){
					w *= .8f;
					h *= .8f;
					rend.setColor(.51f,  .51f,  .51f, alpha);
				}
				else rend.setColor(.51f,  .51f,  .51f, alpha*15);
				
				switch (doingType){
				case Doing.TYPE_THROW:
					rend.rect(x, y, w, h/2);
					//rend.triangle(x-w/2, y+h/2, x+w/2, y+h/2, x, y+h);
					rend.triangle(x, y+h/2, x+w, y+h/2, x+w/2, y+h);
					break;
				default:
					
					rend.rect(x, y, w, h);
					//drawShape(rend, x, y, w, h);
					break;
				}
			}
		}
		rend.end();*/
		
		
		//Gdx.app.log(TAG, "show inv");
		Gdx.gl.glLineWidth(2f);
		rend.setColor(Color.DARK_GRAY);
		rend.begin(ShapeType.Line);
		/*if (!sides[0].isHidden())
			for (int i = 0; i < BELT_SLOTS; i++){
				InventoryButton butt = belt.buttons[i];
				//if (butt.hash == 0) continue;
				drawButton(butt, rend);
			}
		if (!sides[3].isHidden())
			for (int i = 0; i < inv.buttons.size; i++){
				InventoryButton butt = inv.buttons.get(i);
				//if (butt.hash == 0) continue;
				drawButton(butt, rend);
			}*/
		Gdx.gl.glLineWidth(1f);
		if (drawArrow){
			rend.setColor(Color.WHITE);
			drawArrow = false;
			w = Gdx.graphics.getWidth();
			h = Gdx.graphics.getHeight();
			
			int size = 20;
			tmpV.set(arrowAngle).scl(-size).add(w/2, h/2);
			rend.line(w/2, h/2, tmpV.x, tmpV.y);
			
			tmpV2.set(arrowAngle).scl(size);
			tmpV4.set(tmpV2).add(w/2, h/2);
			rend.line(w/2, h/2, tmpV4.x, tmpV4.y);
			
			tmpV5.set(tmpV4).scl(2).add(w/2, h/2);
			//rend.line(tmpV4.x, tmpV4.y, tmpV5.x, tmpV5.y);
			
			tmpV5.set(tmpV4).scl(2).add(tmpV3);
			//rend.line(tmpV4.x, tmpV4.y, tmpV5.x, tmpV5.y);
			
			
			tmpV3.set(tmpV2).scl(.5f).rotate(-45).add(w/2, h/2);
			rend.line(tmpV4.x, tmpV4.y, tmpV3.x, tmpV3.y);
			
			tmpV3.set(tmpV2).scl(.5f).rotate(45).add(w/2, h/2);
			rend.line(tmpV4.x, tmpV4.y, tmpV3.x, tmpV3.y);

			int segments = 16, angle = 360 / segments;
			for (int i = 0; i < segments+1; i++){
				
				tmpV.set(0, size*.9f);
				tmpV2.set(0, size*.9f);
				tmpV2.rotate(angle * i);
				tmpV.rotate(angle * i + angle);
				tmpV.add(w/2, h/2);
				tmpV2.add(w/2, h/2);
				rend.line(tmpV.x,  tmpV.y, tmpV2.x, tmpV2.y);
			}
			
//			tmpV.set(arrowAngle).scl(-40).add(w/2, h/2);
//			rend.line(w/2, h/2, tmpV.x, tmpV.y);
//			tmpV2.set(arrowAngle).scl(-10).rotate(15).add(w/2, h/2);
//			rend.line(w/2, h/2, tmpV2.x, tmpV2.y);
//			tmpV3.set(arrowAngle).scl(-10).rotate(-15).add(w/2, h/2);		
//			rend.line(w/2, h/2, tmpV3.x, tmpV3.y);
		}
		rend.setColor(Color.LIGHT_GRAY);
		if (settingsOn){
			rend.setColor(Color.WHITE);
		}
		
		MomentaryButton butt = btnPad.but;
		//Image im = .getImage();
		Table im = (Table)btnPad.actor;
		//if (butt.hash == 0) continue;
		x = im.getX() + 
				butt.getX();
		
		//y ;//= im.getY() + 
				//butt.getY();
		w = Main.prefs.jump_button_width;
		h = Main.prefs.control_button_height;
		x = Gdx.graphics.getWidth()-w;
		y = 0;
		if (!sides[8].isHidden()){//jump button
			//Gdx.app.log(TAG, "cutoffs st"+cutOffJump);
			MomentaryButton bu = (MomentaryButton) sides[8].min[0].actor;
			if (cutOffJump){
				//Gdx.app.log(TAG, "cutoffs actually");
				drawShape(rend, x, y, w, h, MomentaryButton.CUTOFF_TOP_LEFT);
			}
			else drawShape(rend, x, y, w, h);
			
		}
		//y = Gdx.graphics.getHeight()-h;
		Viewport v = stage.getViewport();

		//Gdx.app.log(TAG,  "dr"+x+" , "+y+" "+w + "   "+v.getScreenWidth());
		if (!sides[6].isHidden()){//slider
			w = Main.prefs.move_slider_width;
			x = 0;
			SliderNiz sl = (SliderNiz) sides[6].min[0].actor;
			drawShape(rend, x, y, w, h, sl.cutOff);
			x += .5 * w * slider.slider.getValue() + w/2f;
			float dy = h * .3f;
			if (settingsOn){
				
				c.set(x, y +h/2);
				for (int i = 0; i < 4; i++){
					t.set(Math.min(w,  h), 0).scl(.25f);
					a.set(Math.min(w,  h), 0).scl(.25f).scl(.77f);
					b.set(Math.min(w,  h), 0).scl(.25f).scl(.77f);
					a.rotate(20f);
					b.rotate(-20f);
					
					a.rotate(90*i);
					b.rotate(90*i);
					t.rotate(90*i);
					a.add(c);
					b.add(c);
					t.add(c);
					rend.line(c.x, c.y, t.x, t.y);
					rend.line(t.x, t.y, a.x, a.y);
					rend.line(t.x, t.y, b.x, b.y);
					
				}
				
				//rend.line(x+dy,  y+h/2, x-dy, y+h/2);
				//rend.line(x+5,  y+h, x+17, y+h-dy);
				//Gdx.app.log(TAG,  "dr"+x);
			} else {
				rend.line(x,  y+dy, x, y+h-dy);
			}
		}

		rend.end();
		
	}
	private Vector2 a = new Vector2(), b = new Vector2(), t = new Vector2(), c = new Vector2();

	private void drawButton(InventoryButton butt, SpriteBatchN batch, ShapeRenderer rend) {
		drawButton(butt, batch, rend, false);
	}
	private void drawButton(InventoryButton butt, SpriteBatchN batch, ShapeRenderer rend, boolean forceSelected) {
		ImageN im = (ImageN) butt.getImage();
		a.set(0, 0);
		b.set(butt.getWidth(), butt.getHeight());
		butt.localToStageCoordinates(a);
		butt.localToStageCoordinates(b);
		float space = 2f;
		a.add(space, space);
		b.sub(space, space);
		b.sub(a);
		//y = Gdx.graphics.getHeight()-y;
		float x = a.x, y = a.y, w = b.x, h = b.y;
		if (butt.isChecked() || forceSelected){
			queueCheckedButtonDraw(x, y, w, h);
		}
		//else
		drawShape(rend, x, y, w, h);
		//im.draw2(batch, 1f);
	}
	
	private void drawButtonAmount(InventoryButton butt, SpriteBatchN batch, BitmapFont font) {
		Image im = butt.getImage();

		float x = 
				butt.getX();
		float y =  
				 butt.getY();
		Actor a = butt.getParent();
		while (a != null){
			x += a.getX();
			y += a.getY();
			a = a.getParent();
		}
		float w = //Main.prefs.inventory_button_width;
		im.getWidth();
		float h = //Main.prefs.inventory_button_height;
		butt.getHeight();
		font.draw(batch, butt.amountLabel, x+font.getCapHeight()*.25f, y + font.getCapHeight()+font.getCapHeight()*.25f);
	}
	
	private FloatArray checkedQueue = new FloatArray(true, 4);
	private boolean drawArrow;
	private Vector2 arrowAngle = new Vector2();
	private int arrowType;
	private boolean settingsOn;
	private float doorButtonFontHeight;
	private boolean doorButtonsOn;
	private void queueCheckedButtonDraw(float x, float y, float w, float h) {
		checkedQueue.add(x);
		checkedQueue.add(y);
		checkedQueue.add(w);
		checkedQueue.add(h);
		//Gdx.app.log(TAG,  "q");
	}

	private void drawShape(ShapeRenderer rend, float x, float y, float width,
			float height) {
		rend.rect(x, y, width, height);
		//Gdx.app.log(TAG,  "drwa"+x+" , "+y+"  "+height);
	}

	private void drawShape(ShapeRenderer rend, float x, float y, float w,
			float h, int cutOff) {
		float len;
		switch (cutOff){
		case MomentaryButton.CUTOFF_TOP_LEFT:
			
				len = Math.min(w, h)/2;
				rend.line(x , y, x+w, y);
				rend.line(x+w , y, x+w, y+h);
				
				rend.line(x,  y, x, y+h-len);
				rend.line(x+len,  y+h, x+w, y+h);
				rend.line(x,  y+h-len, x+len, y+h);
			
			break;
		case MomentaryButton.CUTOFF_TOP_RIGHT:
			
			len = Math.min(w, h)/2;
			rend.line(x , y, x+w, y);
			rend.line(x , y, x, y+h);
			
			rend.line(x+w, y+h-len, x+w, y);
			rend.line(x,  y+h, x+w-len, y+h);
			rend.line(x+w-len,  y+h, x+w, y+h-len);
		
		break;
		default:rend.rect(x,  y,  w,  h);
		break;
		}
		
		
	}

	public void update(float delta) {
		
		float alpha = 1f;
		if (!toastOn){
			toastTimer += delta;
			alpha = Math.max(0,  1f - toastTimer * TOAST_SPEED);
			//Gdx.app.log(TAG, "alpha " + alpha);
		} else {
			toastTimer += delta;
			if (toastTimer > TOAST_DELAY){
				toastOn = false;
				toastTimer = 0f;
			}
		}
		Color c, cb;
		for (int i = 0; i < toastLabels.size; i++){
			//Label prev = toastTitleLabel;
			//if (i != 0) prev = toastLabels.get(i-1);
			DoingLabel lab = toastLabels.get(i);
			c = Color.WHITE;
			//cb = Color.GRAY;
			if (i == toastSelectedIndex)
				lab.setColor(c.r, c.g, c.b, Math.min(alpha*15,  1));
			else lab.setColor(c.r, c.g, c.b, Math.min(alpha,  1));
		}
		c = toastTitleLabel.getColor();
		toastTitleLabel.setColor(c.r, c.g, c.b, Math.min(alpha*15,  1));
		
	}

	public void changeToCharacterScreen() {
		on = false;
		theStage.clear();
		charScreen.addTo(theStage);
		sides[3].hide();
		sides[7].hide();
		sides[6].unHide();
		sides[8].unHide();
	}

	@Override
	public void addTo(Stage stage) {
		
		super.addTo(stage);
		stage.addActor(toastTitleLabel);
		for (int i = 0; i < toastLabels.size; i++){
			DoingLabel t = toastLabels.get(i);
			stage.addActor(t);
		}
		//stage.addActor(doorTable);
	}

	public void changeToHUD() {
		sides[3].hide();
		sides[7].hide();
		sides[6].unHide();
		sides[8].unHide();
		showInv = false;
		on = true;
	}

	public ObjectMap<Actor, Array<EventListener>> cachedListeners = new ObjectMap<Actor, Array<EventListener>>();
	private boolean cutOffJump;
	private boolean cutOffMove;
	public void changeToSettingsScreen(){
		//remove listeners, add new ones for resize
		sides[3].hide();//item display
		sides[7].hide();//button display
		sides[6].unHide();//slider
		sides[8].hide();//buttonpad
		showInv = false;
		on = true;
		settingsOn = true;
		
		settingsScreen.addTo(theStage);
		settingsScreen.reTableStack();
		
		for (BeltButton b : belt.buttons){
			Array<EventListener> arr;
			if (cachedListeners.containsKey(b)){
				arr = cachedListeners.get(b);
			}else {
				arr = new Array<EventListener>();
				cachedListeners.put(b, arr);
			}
			if (arr.size > 0) throw new GdxRuntimeException("Listeners not decached");
			
			for (EventListener o : b.getListeners()){
				arr.add(o);
				b.removeListener(o);
			}
		
			b.addListener(new DragListener(){
				@Override
				public void drag(InputEvent event, float x, float y, int pointer) {
					int size = Main.prefs.inventory_button_height;
					float dy = this.getDeltaY();
					size += dy;
					size = Math.max(15,  size);
					size = Math.min(Gdx.graphics.getHeight()/2, size);
					Main.prefs.inventory_button_height = size;
					Main.prefs.inventory_button_width = size;
					super.drag(event, x, y, pointer);
				}
			});
		}
		
		Actor b = slider.actor;
		
		Array<EventListener> arr;
		if (cachedListeners.containsKey(b)){
			arr = cachedListeners.get(b);
		}else {
			arr = new Array<EventListener>();
			cachedListeners.put(b, arr);
		}
		if (arr.size > 0) throw new GdxRuntimeException("Listeners not decached");
		
		for (EventListener o : b.getListeners()){
			arr.add(o);
			b.removeListener(o);
		}
	
		b.addListener(new DragListener(){
			float prevX, prevY;
			@Override
			public void dragStart(InputEvent event, float x, float y,
					int pointer) {
				prevX = Gdx.input.getX(pointer);
				prevY = Gdx.input.getY(pointer);
				super.dragStart(event, x, y, pointer);
			}

			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer) {
				
				super.dragStop(event, x, y, pointer);
			}

			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				int size = Main.prefs.control_button_height;
				int width = Main.prefs.move_slider_width;
				float dx = prevX - Gdx.input.getX(pointer);
				width -= dx;
				width = Math.max(15,  width);
				width = Math.min(Gdx.graphics.getWidth()/2, width);
				float dy = prevY - Gdx.input.getY(pointer);
				
				//Gdx.app.log(TAG, "drag "+dx+","+dy);
				size += dy;
				size = Math.max(15,  size);
				size = Math.min(Gdx.graphics.getHeight()/2, size);
				Main.prefs.control_button_height = size;
				Main.prefs.move_slider_width =  width;
				super.drag(event, x, y, pointer);
				onNotify(null, Event.RESIZE, null);
				
				prevX = Gdx.input.getX(pointer);
				prevY = Gdx.input.getY(pointer);
			}
		});
	}

	public void setDrawArrow(Entity e, Vector2 rotation) {
		drawArrow = true;
		arrowAngle.set(rotation);
		Inventory in = invM.get(e);
		Doing doing = in.getActiveDoing(in.activeLimb);
		arrowType = doing.arrowType;
	}

	public void setCutOffButtons(boolean cut_off_jump_button,
			boolean cut_off_move_buttons) {
		cutOffJump = cut_off_jump_button;
		cutOffMove = cut_off_move_buttons;
		MomentaryButton m = (MomentaryButton) sides[8].min[0].actor;
		if (cut_off_jump_button){
			m.cutOff = MomentaryButton.CUTOFF_TOP_LEFT;
		} else m.cutOff = MomentaryButton.CUTOFF_NONE;
		SliderNiz s = (SliderNiz) sides[6].min[0].actor;
		if (cut_off_move_buttons){
			s.cutOff = MomentaryButton.CUTOFF_TOP_RIGHT;
		} else s.cutOff = MomentaryButton.CUTOFF_NONE;
		//Gdx.app.log(TAG, "cutoffs set" + cutOffJump);
	}
	
	public void enableOkButton(){
		belt.enableOk();
		table.invalidateHierarchy();

	}
	
	public void disableOkButton(){
		belt.disableOk();
		table.invalidateHierarchy();
	}

}