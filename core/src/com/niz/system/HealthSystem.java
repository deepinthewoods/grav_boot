package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.niz.component.Health;
import com.niz.component.HealthChangeInfo;
import com.niz.component.Inventory;
import com.niz.component.Player;
import com.niz.observer.Subject;

public class HealthSystem extends EntitySystem {
    ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
    ComponentMapper<Health> healthM = ComponentMapper.getFor(Health.class);
    private ImmutableArray<Entity> playerEntities;
    private ImmutableArray<Entity> entities;
    private Subject healthChangeNotifier;
    private HealthChangeInfo healthChangeInfo;
    private Family playerEntityFamily;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        playerEntityFamily = Family.all(Player.class).get();
        entities = engine.getEntitiesFor(Family.all(Health.class).get());
        healthChangeNotifier = ((EngineNiz)engine).getSubject("health");
        healthChangeInfo = new HealthChangeInfo();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (Entity e : entities){

            Health health = healthM.get(e);
            if (health.health != health.lastHealth){
                healthChangeInfo.amount = health.health - health.lastHealth;
                health.lastHealth = health.health;
                healthChangeInfo.dead = health.health < 0;
                healthChangeInfo.isPlayer = playerEntityFamily.matches(e);
                healthChangeInfo.health = health.health;
                healthChangeInfo.maxHealth = health.maxHealth;
                healthChangeNotifier.notify(e, Subject.Event.HEALTH_CHANGE, healthChangeInfo);
            }
        }
    }
}
