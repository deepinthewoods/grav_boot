package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.niz.action.Action;
import com.niz.component.Map;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;
import com.niz.system.MapSystem;

public class APlaceAnimation extends Action {
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<SpriteAnimation> animM = ComponentMapper.getFor(SpriteAnimation.class);
	public int itemID;
	public GridPoint2 target = new GridPoint2();
	public int val;
	public Vector2 end = new Vector2();
	public Vector2 start = new Vector2();
	private static Vector2 tmpV = new Vector2();
	public float lerpTime;
	private float time;
	@Override
	public void update(float dt) {
		time += dt;
		float alpha = time / lerpTime;
		if (alpha > 1f) isFinished = true;
		Vector2 pos = posM.get(parent.e).pos;
		//Gdx.app.log("place anim action", "jkdsl" + pos);
		tmpV.set(start).lerp(end, alpha);
		pos.set(tmpV);
	}

	@Override
	public void onEnd() {
		MapSystem map = parent.engine.getSystem(MapSystem.class);
		Map mapc = map.getMapFor(target.x, target.y);
		if (mapc != null)
			mapc.set(target.x, target.y, val);;
		parent.engine.removeEntity(parent.e);
	}

	@Override
	public void onStart() {
		time = 0f;
	}

}
