package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.RenderSystem;

public class SpriteAnimationUpdateRenderSystem extends RenderSystem {
    private SpriteAnimationUpdateSystem sys;

    @Override
    public void addedToEngine(Engine engine) {
        sys = engine.getSystem(SpriteAnimationUpdateSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        sys.update(0f);

    }
}
