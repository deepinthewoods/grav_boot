package com.niz;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.niz.action.Action;
import com.niz.component.DragOption;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.SelectedPlayer;

public class ASelectedLight extends Action {
	private static final String SELECTED_LIGHT_ACTION = "selected light action";
	private static final String TAG = SELECTED_LIGHT_ACTION;
	private ImmutableArray<Entity> selectableEntities;
	private static ComponentMapper<DragOption> dragoM = ComponentMapper.getFor(DragOption.class);
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG, "tick" + selectableEntities.size());

		//move to selected character, kill entity if no options
		if (selectableEntities.size() == 0) {
			isFinished = true;
			return;
		}
		for (int i = 0; i < selectableEntities.size(); i++){
			Entity e = selectableEntities.get(i);
			Position pos = posM.get(e);
			DragOption drag = dragoM.get(e);
			if (drag.selected){
				posM.get(parent.e).pos.set(pos.pos);
				//Gdx.app.log(TAG, "selected" + e.getId() + pos.pos);
				return;
			}
		}
	}

	@Override
	public void onEnd() {
		parent.engine.removeEntity(parent.e);
		//Gdx.app.log(TAG, "end");
	}

	@Override
	public void onStart() {
		selectableEntities = parent.engine.getEntitiesFor(Family.all(Position.class, DragOption.class).get());
		
	}

}
