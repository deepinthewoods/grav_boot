package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.RenderSystem;

public class RaceRenderSystem extends RenderSystem {

    private RaceSystem raceSys;

    @Override
    public void update(float deltaTime) {
        raceSys.update(0f);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.raceSys = engine.getSystem(RaceSystem.class);
    }
}
