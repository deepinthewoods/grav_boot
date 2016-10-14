package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.action.LimbAction;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.Inventory;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.Race;
import com.niz.component.SpriteAnimation;
import com.niz.item.Doing;

public class AUseInventory extends Action {	
	private static final String TAG = "use inv action";
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	private static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	private static ComponentMapper<Inventory> invM = ComponentMapper.getFor(Inventory.class);
	
	private static ComponentMapper<Race> raceM = ComponentMapper.getFor(Race.class);

	boolean on;
	//private Item item;
	
	@Override
	public void update(float dt) {
		Inventory inv = invM.get(parent.e);
		Control control = controlM.get(parent.e);
		if (control.pressed[Input.SCREEN_TOUCH]){
			//Gdx.app.log(TAG, "p " );

			if (!on){
				//item = inv.getActiveItem();
				Race race = raceM.get(parent.e);
				Doing doing = inv.getActiveDoing(inv.activeLimb);
				LimbAction a = Pools.obtain(race.limbThrowActions[doing.doingTypeIndex][doing.limbIndex]);
				//Gdx.app.log(TAG, "p "+ doing.doingTypeIndex+doing.limbIndex);
				int limb = doing.limbIndex;
				a.limb = limb;
				
				if (race.limbTotals[limb] < 1){
					on = true;	
					a.lanes = a.getLanes();
					
					//a.started = false;
					this.addBeforeMe(a);
					//Gdx.app.log(TAG, "start "+limbTotals[limb]);
					race.limbTotals[limb]++;
					
				} else Pools.free(a);;
				
			}
			
			
		} else {//not pressed
			//Gdx.app.log(TAG, "notp " );
			if (on){
				on = false;
				//Gdx.app.log(TAG, "release " );
			}
		}
		//inv = invM.get(parent.e);
		
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
	}

}
