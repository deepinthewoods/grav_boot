package com.niz.ui.edgeUI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.niz.item.ItemDef;

public class TextDoingLabel extends Label{

	private int index;

	public TextDoingLabel(int i, Skin skin) {
		super("--"+i, skin, "iteminfo");
		index = i;
	}

	public void setSelected(Skin skinn) {
		setStyle(skinn.get("iteminfoselected", LabelStyle.class));	
	}

	public void setNotSelected(Skin skinn) {
		setStyle(skinn.get("iteminfo", LabelStyle.class));
	}

	public void set(ItemDef def, int i) {
		if (i < def.doings.size)
			setText(def.doings.get(i).name);
		else {
			setText("");
		}
		
	}

	public void resize(int screenWidth, float height, int screenHeight) {
		//setX(screenWidth/2);
		setX(0);;
		setWidth(screenWidth);
		setAlignment(Align.center);
		setY(screenHeight/3*2-(index+1)*height);
	}

}
