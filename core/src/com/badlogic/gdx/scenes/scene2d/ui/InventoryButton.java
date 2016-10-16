package com.badlogic.gdx.scenes.scene2d.ui;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;
import com.niz.anim.Animations;
import com.niz.component.Inventory;
import com.niz.item.Item;


public class InventoryButton extends Button{

	private static final String TAG = "inv button";
	private static final float SHAKE_TIME = .35f;
	private static final float SHAKE_ANGLE = 25f;
	//public InventoryDrawable drawable;
	public Image image;
	private boolean shaking;
	private float shakeDelta;
	//public ItemDef def;
	public Item item;
	public String amountLabel = "";
	public int hash;
	public Entity e;

	public InventoryButton(Skin skin){
		super(skin.get("inventory", ButtonStyle.class));

		image = new Image();
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
		
		super.draw(batch, parentAlpha);

		

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
	
}
