package com.badlogic.gdx.scenes.scene2d.ui;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;
import com.niz.SimplexNoise;
import com.niz.anim.Animations;
import com.niz.component.Inventory;
import com.niz.component.Light;
import com.niz.item.Item;
import com.niz.item.ItemDef;
import com.niz.system.InventorySystem;
import com.niz.system.SpriteAnimationSystem;


public class InventoryButton extends Button{

	private static final String TAG = "inv button";
	private static final float SHAKE_TIME = .735f;
	private static final float SHAKE_ANGLE = 35f;
	private static final float SHAKE_SPEED = 30f;
	//public InventoryDrawable drawable;
	public ImageN image;
	private boolean shaking;
	private float shakeDelta, shakeAccum;
	//public ItemDef def;
	public Item item;
	public String amountLabel = "";
	public int hash;
	public Entity e;
	private TextureRegionDrawable drawable;
	static SimplexNoise noise = new SimplexNoise();
	int seed;
	public InventoryButton(Skin skin){
		super(skin.get("inventory", ButtonStyle.class));
		seed = MathUtils.random(1000000);
		image = new ImageN();
		image.setScaling(Scaling.fit);
		//image.setFillParent(true);
		image.setOrigin(Align.center);
		
		Stack group = new Stack();
		
		this.add(image).expand().fill();//.pad(20f);//.fill();

	}
	
	public Image getImage(){
		return image;
	}
	
	public void setShaking(){
		shakeDelta = 0f;
		shaking = true;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (item == null) return;
		//if (true) return;
		ItemDef def = Inventory.defs.get(item.id);

		if (def.isBlock )
			addBlockDraw(this, shaking);
		else
			addItemDraw(this, shaking);
		if (true) return;
		if (shaking)
		{
			shakeDelta += Gdx.graphics.getDeltaTime();
			if (shakeDelta > SHAKE_TIME){
				shaking = false;
				image.setRotation(0f);
			} else{
				image.setOrigin(Align.center);
				image.setRotation(MathUtils.random(-SHAKE_ANGLE, SHAKE_ANGLE));
				
			}
		}
		
		//super.draw(batch, parentAlpha);

		

	}



	private void addItemDraw(InventoryButton inventoryButton, boolean shaking) {
		itemDrawList.add(inventoryButton);

	}

	public static Array<InventoryButton> blockDrawList = new Array<InventoryButton>();
	public static Array<InventoryButton> itemDrawList = new Array<InventoryButton>();
	private void addBlockDraw(InventoryButton inventoryButton, boolean shaking) {
		blockDrawList.add(inventoryButton);
	}


	public void setFrom(Item item, Entity e) {
		if (e == null) throw new GdxRuntimeException("nill e");
		if (item == null){
			hash = 0;
			return;
			//throw new GdxRuntimeException("ojd");
		} 
		if (item.count <= 0){
			hash = 0;
			item = Inventory.defaultItem;
			getImage().setDrawable(Animations.itemDrawables[0]);
			amountLabel = "";
			//Gdx.app.log(TAG,  "USING DEFAULT ITEM");
			return;
		}
		this.e = e;
		hash = item.hash;
		drawable = Animations.itemDrawables[item.id];
		getImage().setDrawable(drawable);

		//Gdx.app.log(TAG, "set "+item.count + "   "+hash);
		this.item = item;
		if (item.count > 256) amountLabel = ">";
		else amountLabel = ""+item.count;

	}

	public void unSet() {
		hash = 0;
		item = null;
		e = null;
	}
	static Vector2 v = new Vector2(), v2 = new Vector2();
	public void draw2(SpriteBatchN batch, float alpha) {

		v.set(0, 0);
		v2.set(getWidth(), getHeight());
		localToStageCoordinates(v);
		localToStageCoordinates(v2);
		float space = 3f;
		v.add(space, space);
		v2.sub(space, space);
		v2.sub(v);
		drawable.tint(SpriteAnimationSystem.LAYER_COLORS[Light.ITEM_LAYER]);
		//image.setColor(Color.WHITE);
		//drawable.tint(Color.WHITE);
		float ar = (float)drawable.getRegion().getRegionHeight() / (float)drawable.getRegion().getRegionWidth();
		float r = 0f;
		if (shaking)
		{
			shakeDelta += Gdx.graphics.getDeltaTime();
			shakeAccum += Gdx.graphics.getDeltaTime();
			if (shakeDelta > SHAKE_TIME){
				shaking = false;
				seed++;
			} else{
				if (shakeAccum > .2f){//10fps
					shakeAccum -= .2f;
				}
				r = noise.noise(seed, shakeDelta * SHAKE_SPEED)* SHAKE_ANGLE;

			}
		}
		if (ar > 1f)
			drawable.draw(batch, v.x, v.y, v2.x / ar*.5f, v2.y*.5f, v2.x / ar, v2.y, 1f, 1f, r);
		else
			drawable.draw(batch, v.x, v.y, v2.x*.5f, v2.y * ar*.5f, v2.x, v2.y * ar, 1f, 1f, r);

		//image.draw2(batch, v);
	}
}
