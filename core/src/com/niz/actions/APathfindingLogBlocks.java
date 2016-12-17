package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.component.Body;
import com.niz.component.Position;
import com.niz.system.PathfindingUpdateSystem;

public class APathfindingLogBlocks extends Action {
	private static final String TAG = "apathlogblocks";
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	public int index = 0;
	public GridPoint2 start = new GridPoint2();
	
	private float time;

	public Array<GridPoint2> blocks;

	private FloatArray blockTimes;
	public boolean stand;
	
	@Override
	public void update(float dt) {
		time += dt;
		Body body = bodyM.get(parent.e);
		Vector2 pos = posM.get(parent.e).pos;
		//for (int x = (int) (pos.x - body.width); x < pos.x + body.width; x++){
			//for (int y = (int) (pos.y - body.height); y < pos.y + body.height; y++){
		int x = (int)(pos.x);
		int y = (int) (pos.y - body.height);
		GridPoint2 pt = Pools.obtain(GridPoint2.class);
		pt.set(x, y).sub(start);
		if (!blocks.contains(pt, false)){
			//Gdx.app.log(TAG,  "add " + pt);
			blocks.add(pt);
			blockTimes.add(time);
		} else {
			Pools.free(pt);
		}
			///}
		//}
	}

	@Override
	public void onEnd() {
		//Gdx.app.log(TAG,  "end " + blocks.size);
		PathfindingUpdateSystem aStar = parent.engine.getSystem(PathfindingUpdateSystem.class);
		aStar.registerJumpBlocks(blocks, blockTimes, index, stand);
	}

	@Override
	public void onStart() {
		blocks = new Array<GridPoint2>();
		blockTimes = new FloatArray();
		time = 0f;
		Vector2 pos = posM.get(parent.e).pos;
		start.set((int)pos.x, (int)pos.y);
	}

}
