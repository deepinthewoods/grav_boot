package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.ui.BeltButton;
import com.niz.item.Item;

public class ItemInput implements Component {
public int value;
public transient Item item;
public transient BeltButton butt;
public boolean left;
}
