package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.niz.item.Doing;
import com.niz.item.Item;

public class ItemEquipButton extends TextButton {
	public int index;
	public Item item;
	public Entity e;
	public ItemEquipButton(Skin skin) {
		super("Equip", skin);
	}
	
	public void setCheckedNoCallbacks(boolean checked){
		super.isChecked = checked;
	}

	public void set(int i, Item item, Entity e) {
		index = i;
		this.item = item;
		//setText(doing.name);
		
	}

}
