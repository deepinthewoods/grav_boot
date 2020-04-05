package com.niz.actions.path;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.component.PathResult;

public class AWaitForPath extends Action{
	private static final String TAG = "wait for path action";
	private static ComponentMapper<PathResult> resultM = ComponentMapper.getFor(PathResult.class);
	public AWaitForPath() {
		reset();
	}
	@Override
	public void update(float dt) {
		//Gdx.app.log(TAG,  "update");
		PathResult path = resultM.get(parent.e);
		if (path != null){
			if (path.path.getCount() == 0){
				//isFinished = true;
				parent.e.remove(PathResult.class);
				Gdx.app.log(TAG,  "NO PATH");
			} else {
				Gdx.app.log(TAG,  "FOUND" + path);
				AFollowPath follower = Pools.obtain(AFollowPath.class);
				follower.path = path;
				addBeforeMe(follower);
			}
		}
	}

	@Override
	public void onEnd() {
		
	}

	@Override
	public void onStart() {
		
	}
	@Override
	public void reset() {
		super.reset();
		lanes = Action.LANE_PATH;
	}
	
}
