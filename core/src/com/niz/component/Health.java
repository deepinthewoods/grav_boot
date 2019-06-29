package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class Health implements Component, Pool.Poolable {
    public int health;
    public int lastHealth;
    public int maxHealth;

    public Health(){
        reset();
    }

    @Override
    public void reset() {
        health = 100;
        lastHealth = 99;
        maxHealth = 100;
    }
}
