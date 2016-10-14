package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.Input;

/**
 * Created by niz on 29/05/2014.
 */
public class Vector3Input implements Component, Poolable{
    public Vector3 v = new Vector3();
    public int code;

    @Override
    public void reset() {

    }

	
}
