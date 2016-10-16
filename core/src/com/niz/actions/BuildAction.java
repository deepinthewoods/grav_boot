package com.niz.actions;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.niz.action.Action;
import com.niz.component.BitmaskedCollisions;
import com.niz.component.Body;
import com.niz.component.Map;
import com.niz.component.MovementData;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;

public abstract class BuildAction extends Action  {
	private static final String TAG = "Build Action";
	public static ComponentMapper<MovementData> moveM = ComponentMapper.getFor(MovementData.class);
	public static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	public static ComponentMapper<OnMap> onMapM = ComponentMapper.getFor(OnMap.class);
	public static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	public static ComponentMapper<BitmaskedCollisions> bitM = ComponentMapper.getFor(BitmaskedCollisions.class);
	public static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	
	Vector2 
	start = new Vector2()
	, end = new Vector2()
	, min = new Vector2()
	, max = new Vector2()
	;
	public int currentX, currentY;

	Array<Vector2> placed = new Array<Vector2>();
	transient Map map;
	public Vector2 pos;
	protected boolean changed;
	
	public void setSolid(int x, int y){
		//placed.add(Pools.obtain(Vector2.class).set(x, y));
		int val = map.getBackground(x,  y);
		int bit = bitM.get(parent.e).startBit;
		val >>= bit;
		val &= 7;
		if (val == 0)
			map.setBit(x, y, bit);	
		//Gdx.app.log(TAG, "b setbit "+x+","+y);
	}

	
	public void setHasBeen(){
		int bit = bitM.get(parent.e).startBit;
		Body bod = bodyM.get(parent.e);
		Vector2 pos = posM.get(parent.e).pos;
		for (int bx = (int) (pos.x - bod.width); bx <= (int)( pos.x + bod.width); bx++)
			for (int by = (int) (pos.y - bod.height); by <= (int)(pos.y + bod.height); by++)
				map.setBackgroundBit(bx, by, bit);	
	}
	
	public void setBackgroundSquare(){
		//Gdx.app.log(TAG, "bg sq");
		int bit = bitM.get(parent.e).startBit+1;
		map.setBackgroundBitsSquare((int)min.x, (int)min.y, (int)max.x+1, (int)max.y+1, bit);
	}

	
	

	@Override
	public void update(float dt) {
		map = onMapM.get(parent.e).map;
		pos = posM.get(parent.e).pos;
		Body body = bodyM.get(parent.e);
		int charX = (int) (pos.x);
		int charY = (int) (pos.y - body.height);
		if (charX != currentX || charY != currentY){
			currentX = charX;
			currentY = charY;
			changed = true;
		} else changed = false;
		
		int px = (int) pos.x;
		int py = (int) pos.y;
		int dx = (int) (pos.x - start.x);
		int dy = (int) (pos.y - start.y);
		if (dx > map.width/2){
			px -= map.width;
		}
		if (dy > map.height/2){
			py -= map.height;
		}
		
		if (dx < -map.width/2){
			px += map.width;
		}
		if (dy < -map.height/2){
			py += map.height;
		}
		//Gdx.app.log(TAG, "b "+px+" , "+py);
		//if (px > map.width) px -= map.width;
		//if (py > map.height) py -= map.height;
		
		
		min.x = Math.min(px, min.x);
		min.y = Math.min(py, min.y);
		max.x = Math.max(px, max.x);
		max.y = Math.max(py, max.y);
		
	}

	@Override
	public void onEnd() {
		Vector2 pos = posM.get(parent.e).pos;
		end.set(pos);
		setBackgroundSquare();
	}

	@Override
	public void onStart() {
		
		Vector2 pos = posM.get(parent.e).pos;
		start.set(pos);
		min.set(pos);//.add(-1, -1);
		max.set(pos);//.add(1,1);
	}

	@Override
	public void reset() {
		
		super.reset();
	}

	
}
