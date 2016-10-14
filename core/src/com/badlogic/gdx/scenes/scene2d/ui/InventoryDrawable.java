package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class InventoryDrawable extends SpriteDrawable{

	private static final String TAG = "inv drawable";
	
	
	public InventoryDrawable() {
		super();
	}
	private static Color tmpColor = new Color();
	@Override
	public void draw (Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX,
			float scaleY, float rotation) {
		Sprite sprite = getSprite();
			sprite.setOrigin(originX, originY);
			sprite.setRotation(rotation);
			sprite.setScale(scaleX*4, scaleY*4);
			sprite.setBounds(x, y-height/2f, width, height);
			Color color = sprite.getColor();
			sprite.setColor(tmpColor.set(color).mul(batch.getColor()));
			sprite.draw(batch);
			sprite.setColor(color);
		}
	
	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		if (getSprite() == null){
			return;
		}
		
		super.draw(batch, x, y, width, height);
		//Gdx.app.log(TAG, "draw"+y);
		/*if (sprite != null){
			
			batch.draw(sprite, x, y, width, height);
			Gdx.app.log(TAG, "draw"+y);
			
		}//*/
	}//*/

	

	

}
