package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.Input;

/**
 * Created by niz on 29/05/2014.
 */
public class VectorInput implements Component, Poolable{
    public Vector2 v = new Vector2();
    public int code;

    @Override
    public void reset() {

    }
}
