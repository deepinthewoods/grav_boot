package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Pool;

public class Door implements Component, Pool.Poolable {
public GridPoint2 endPoint = new GridPoint2();
public int nextZLevel;

public Door(){
    reset();
}

@Override
public void reset(){
    nextZLevel = -1;
}
}
