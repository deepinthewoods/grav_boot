package com.niz.ui.edgeUI;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.BeltButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.niz.Data;
import com.niz.anim.Animations;
import com.niz.component.Inventory;
import com.niz.component.ItemInput;
import com.niz.component.Race;
import com.niz.item.Doing;
import com.niz.item.ItemDef;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class DoingLabel extends Stack{
	
	private static final String TAG = "doing label";
	ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);
	public int index;
	Image limbImage, typeImage;
	private TextureRegionDrawable limbDrawable;
	public boolean disableDraw;
	private float w;
	private float h;
	public Doing doing;
	public boolean selected;
	private int screenWidth;
	private Actor leftPad;
	private boolean left;
	private TextureRegionDrawable typeDrawable;
	protected ItemInput c = new ItemInput();
	private Subject toastNotifier;
	private BeltButton butt;

	public DoingLabel(int i, Skin skin, EngineNiz engine) {
		//super("--"+i, skin, "iteminfo");
		super();
		toastNotifier = engine.getSubject("toast");

		index = i;
		//typeImage = new Image(Animations.doingTypeImages[0]);
		limbImage = new Image(Animations.doingLimbImages[0]);
		limbImage.setScaling(Scaling.stretch);
		limbImage.setFillParent(true);
		typeImage = new Image(Animations.doingTypeImages[0]);
		typeImage.setScaling(Scaling.stretch);
		typeImage.setFillParent(true);
		typeImage.setColor(Data.colors[Data.YELLOW_INDEX]);
		setSize(0f, 0f);
		limbDrawable = (TextureRegionDrawable) limbImage.getDrawable();
		typeDrawable = (TextureRegionDrawable) typeImage.getDrawable();
		//addActor(typeImage);
		leftPad = new Actor();
		//leftPad.setWidth(100);
		limbImage.setScale(0f);
		//add(leftPad);
		add(limbImage);//.center().fill();
		add(typeImage);
		//this.setFillParent(fillParent);
		this.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				DoingLabel l= (DoingLabel) event.getListenerActor();
				BeltButton b = l.butt;
				if (b.item == null) b.item = Inventory.defaultItem;
				c.value = index;
				c.item = b.item;
				c.butt = b;
				c.left = left;
				Gdx.app.log("button", "doingslot after "+c.value);
				toastNotifier.notify(b.e, Event.CHANGE_DOING_SLOT, c);
				event.handle();
				super.clicked(event, x, y);
			}
		});
	}

	public void setSelected(Skin skinn) {
		//Gdx.app.log(TAG, "set"+left);
		//limbImage.setSize(w*100f, h*1f);
		
		//cell.padLeft(w*1.7f*.5f);
		leftPad.setWidth(w*.5f*.5f);
		invalidate();
		selected = true;
		limbImage.setScale(.8f);
		typeImage.setScale(.8f);
	}

	public void setNotSelected(Skin skinn) {
		
		//cell.padLeft(w*1.5f*.5f);
		leftPad.setWidth(w*.5f*.5f);
		//cell.padLeft(100);
		invalidate();
		selected = false;
		limbImage.setScale(.7f);
		typeImage.setScale(.7f);
	}
	

	public boolean set(ItemDef def, int i, Entity e, boolean left, BeltButton but) {
		//if (left)
			//setX((screenWidth/3) - w*.7f*.5f);  
		//else
		//	setX((screenWidth/3)*2 - w*.7f*.5f);;  
		butt = but;
		this.left = left;
		Race race = raceM.get(e);
		setScale(1);
		if (i < def.doings.size){
			doing = def.doings.get(i);
			limbDrawable.setRegion(Animations.doingLimbImages[doing.limbIndex]);
			typeDrawable.setRegion(Animations.doingTypeImages[doing.doingTypeIndex]);
			disableDraw = false;
			this.setTouchable(Touchable.enabled);
			//setSize(w, h);
			if (!race.enabledLimb[doing.limbIndex]){
				disableDraw = true;
				limbImage.setScale(0f);
				typeImage.setScale(0f);
				setScale(0);
			}
		} else {
			disableDraw = true;
			limbImage.setScale(0, 0);
			typeImage.setScale(0, 0);
			this.setTouchable(Touchable.disabled);
		}
		
		return disableDraw;
	}

	public void resize(int screenWidth, float height, int screenHeight) {
		//setX(screenWidth/2);
		w = height;
		h = height;
		this.screenWidth = screenWidth;
		
		//setWidth(screenWidth);
		setSize(w, h);
		limbImage.setSize(w,  h);
		typeImage.setSize(w,  h);
		//setAlignment(Align.center);
		//setY(screenHeight/3*2-(index+1)*height);
		//if (disableDraw)setSize(0, 0);
		invalidate();
	}

}

/**
 * package com.niz.ui.edgeUI;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.niz.anim.Animations;
import com.niz.item.ItemDef;

public class DoingLabel extends Table{

	private int index;
	Image limbImage;
	private TextureRegionDrawable drawable;
	private boolean disableDraw;
	public DoingLabel(int i, Skin skin) {
		//super("--"+i, skin, "iteminfo");
		super();
		index = i;
		//typeImage = new Image(Animations.doingTypeImages[0]);
		drawable = new TextureRegionDrawable();
		limbImage = new Image(drawable);
		//addActor(typeImage);
		addActor(limbImage);
		drawable.setRegion(Animations.doingLimbImages[0]);
	
	}

	public void setSelected(Skin skinn) {
		
	}

	public void setNotSelected(Skin skinn) {
		
	}

	public void set(ItemDef def, int i) {
		if (i < def.doings.size){
			drawable.setRegion( Animations.doingLimbImages[i]);
			disableDraw = false;
		} else {
			disableDraw = true;
		}
	}

	public void resize(int screenWidth, float height, int screenHeight) {
		//setX(screenWidth/2);
		float w = height, h = height;
		setX(screenWidth/2 - w);
		//setX(0f);
		//setWidth(screenWidth);
		setSize(w, h);
		//setAlignment(Align.center);
		setY(screenHeight/3*2-(index+1)*height);
//		if (disableDraw) setSize(0f, 0f);
		setY(100);
	}

}
**/
