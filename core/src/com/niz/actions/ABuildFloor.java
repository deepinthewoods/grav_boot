package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.niz.action.Action;
import com.niz.component.MovementData;
import com.niz.component.Physics;
import com.niz.component.Position;

public class ABuildFloor extends BuildAction {
	
	public int y  = 0;
	@Override
	public void update(float dt) {
		super.update(dt);;
		if (changed){
			int diff = currentY - y;
			if (
					//diff == 0 
					//|| 
					diff == 1 
					&& 
					physM.get(parent.e).vel.y < .001f
					){
				setSolid(currentX, y);
			}
		}
		
		
	}

	@Override
	public void onEnd() {
		super.onEnd();
		
		
		
	}

	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub
		
	}

}
