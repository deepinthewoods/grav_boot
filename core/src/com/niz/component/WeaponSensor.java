package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class WeaponSensor implements Component {
public long parent;
public Vector2 prevOffsetA = new Vector2(), prevOffsetB = new Vector2();
public int guideLayer;
public int limbIndex;
public int itemID;
}
