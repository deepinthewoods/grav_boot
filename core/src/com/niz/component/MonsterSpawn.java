package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.niz.PlatformerFactory;

public class MonsterSpawn implements Component, Pool.Poolable {
    public PlatformerFactory.MobSpawnType type;
    public int z;
    public boolean valid = false;

    @Override
    public void reset() {
        valid = false;
    }
}
