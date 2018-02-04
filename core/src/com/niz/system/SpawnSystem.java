package com.niz.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.niz.WorldDefinition;
import com.niz.component.MonsterSpawn;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject;

public class SpawnSystem extends EntitySystem implements Observer {
    private EngineNiz engine;
    private ImmutableArray<Entity> entities;
    private ComponentMapper<MonsterSpawn> spawnM ;
    private ComponentMapper<Position> posM;
    private WorldDefinition worldDef;

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (EngineNiz) engine;
        entities = engine.getEntitiesFor(Family.all(Position.class, MonsterSpawn.class).get());
        spawnM = ComponentMapper.getFor(MonsterSpawn.class);
        posM = ComponentMapper.getFor(Position.class);
        ((EngineNiz) engine).getSubject("worlddef").add(this);
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); i++){
            Entity e = entities.get(i);
            MonsterSpawn spawn = spawnM.get(e);
            spawn(spawn, posM.get(e));
        }

    }

    private void spawn(MonsterSpawn spawn, Position position) {

    }


    @Override
    public void onNotify(Entity e, Subject.Event event, Object c) {
        if (event == Subject.Event.WORLD_DEFINITION_SET){
            this.worldDef = (WorldDefinition)c;
        }
    }
}
