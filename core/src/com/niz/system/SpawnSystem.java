package com.niz.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.niz.Factory;
import com.niz.PlatformerFactory;
import com.niz.WorldDefinition;
import com.niz.component.MonsterSpawn;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject;


public class SpawnSystem extends EntitySystem implements Observer {
    public static final String TAG = "Spawn System";
    private final Factory factory;
    private EngineNiz engine;
    private ImmutableArray<Entity> entities;
    private ComponentMapper<MonsterSpawn> spawnM ;
    private ComponentMapper<Position> posM;
    private WorldDefinition worldDef;
    private boolean markAllValidQ;

    public SpawnSystem(Factory factory){
        this.factory = factory;
    }
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
        //Gdx.app.log(TAG, "update" + entities.size());
        for (int i = 0; i < entities.size(); i++){
            Entity e = entities.get(i);
            MonsterSpawn spawn = spawnM.get(e);
            if (spawn.valid){
                spawn(spawn, posM.get(e));
                engine.removeEntity(e);
            } //else Gdx.app.log(TAG, "not valid");
        }
        if (markAllValidQ){
            updateMarkAllValid();
            markAllValidQ = false;
        }
    }

    private void spawn(MonsterSpawn spawn, Position position) {
        Entity mob = factory.generateMob(spawn.z, spawn.type, engine);
        mob.getComponent(Position.class).pos.set(position.pos);
        engine.addEntity(mob);
       // Gdx.app.log(TAG, "SPAWN");
    }


    @Override
    public void onNotify(Entity e, Subject.Event event, Object c) {
        if (event == Subject.Event.WORLD_DEFINITION_SET){
            this.worldDef = (WorldDefinition)c;
        }
    }
    public void markAllValid() {
        markAllValidQ = true;
    }

    public void updateMarkAllValid() {
        //Gdx.app.log(TAG, "mark spawns valid" + entities.size());
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            MonsterSpawn spawn = spawnM.get(e);
            spawn.valid = true;
            Gdx.app.log(TAG, "mark spawn valid");
        }
    }
}
