package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Control;
import com.niz.component.Map;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.system.OverworldSystem;

public class AEnemy extends Action {
    public GridPoint2 target = new GridPoint2();
    private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
    private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
    private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

    public long targetId;
    @Override
    public void update(float dt) {
        //if (true) return;
        Control con = controlM.get(parent.e);
        Vector2 pos = posM.get(parent.e).pos;
        if (pos.x < target.x){
            con.pressed[Input.WALK_RIGHT] = true;
            con.pressed[Input.WALK_LEFT] = false;
        } else {
            con.pressed[Input.WALK_RIGHT] = !true;
            con.pressed[Input.WALK_LEFT] = !false;
        }
        Physics phys = physM.get(parent.e);
        if (phys.onGround  ){
            if (MathUtils.random(20) == 1){
                con.pressed[Input.JUMP] = true;
            } else
                con.pressed[Input.JUMP] = false;
        }


    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onStart() {
        Vector2 pos = posM.get(parent.e).pos;
        Map map = parent.engine.getSystem(OverworldSystem.class).getMapFor((int) pos.x, (int) pos.y);
        int x = MathUtils.random(Math.max(0, (int)pos.x - 10), Math.min(map.width-1, (int)pos.x + 10));
        int y = MathUtils.random(Math.max(0, (int)pos.y - 10), Math.min(map.width-1, (int)pos.y + 10));
        target.set(x, y);
    }
}
