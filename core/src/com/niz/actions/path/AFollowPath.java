package com.niz.actions.path;

import java.util.Iterator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.actions.AStopRunning;
import com.niz.astar.FallPathConnection;
import com.niz.astar.JumpPathConnection;
import com.niz.astar.PathConnection;
import com.niz.astar.PathNode;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.PathResult;
import com.niz.component.Physics;
import com.niz.component.Position;

public class AFollowPath extends Action {
	private static final String TAG = "follow path action";
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);

	public PathResult path;
	private int currentIndex;
	

	public AFollowPath() {
		lanes = Action.LANE_PATH;
		isBlocking = true;
		
	}
	
	@Override
	public void update(float dt) {
		PathConnection conn = (PathConnection) path.path.get(currentIndex);
		//Gdx.app.log(TAG, "tivk " +conn.getClass());
		Vector2 pos = posM.get(parent.e).pos;
		Body body = bodyM.get(parent.e);
		Control con = controlM.get(parent.e);
		PathNode to = (PathNode) conn.to, from = (PathNode) conn.from;
		Physics phys = physM.get(parent.e);
		float fromX = Math.abs(from.x + .5f - pos.x), fromY = Math.abs(from.y + .5f - pos.y );
		/*if (fromX > 1f || fromY > 1f){
			Gdx.app.log(TAG,  "path deviation " + fromX + " , " + fromY);
			parent.e.remove(PathResult.class);
			isFinished = true;
		}*/
		if (conn instanceof JumpPathConnection){
			float toX = Math.abs(to.x + .5f - pos.x), toY = Math.abs(to.y + .5f - pos.y );
			
			JumpPathConnection jconn = (JumpPathConnection) conn;
			//lr
			float dx = from.x + .5f - pos.x;
			
			
			con.pressed[Input.JUMP] = false;
			if (dx < 0f){
				if (con.pressed[Input.WALK_RIGHT]){
					//Gdx.app.log(TAG, "INIT JUMP " + from.x + " , " + from.y + "  " + pos + conn);
					AFollowJump act = Pools.obtain(AFollowJump.class);
					//act.index = jconn.key;
					act.conn = jconn;
					addBeforeMe(act);
					if (jconn.stand){
						AStopRunning stop = Pools.obtain(AStopRunning.class);
						addBeforeMe(stop);
					}
					currentIndex++;
					if (currentIndex >= path.path.getCount()){
						//Gdx.app.log(TAG,  "FINISHED jump" + dx  );
						isFinished = true;
					}
				}
				
			} else {
				if (dx < 0f){
					if (con.pressed[Input.WALK_RIGHT]){
						//Gdx.app.log(TAG, "INIT JUMP " + from.x + " , " + from.y + "  " + pos + conn);
						AFollowJump act = Pools.obtain(AFollowJump.class);
						//act.index = jconn.key;
						act.conn = jconn;
						addBeforeMe(act);
						if (jconn.stand){
							AStopRunning stop = Pools.obtain(AStopRunning.class);
							addBeforeMe(stop);
						}
						currentIndex++;
						if (currentIndex >= path.path.getCount()){
							//Gdx.app.log(TAG,  "FINISHED jump" + dx  );
							isFinished = true;
						}
					}
					
				}
			}
			
			if (dx < 0f){//
				con.pressed[Input.WALK_LEFT] = true;
				con.pressed[Input.WALK_RIGHT] = false;
			} else if (dx >= 0){
				con.pressed[Input.WALK_LEFT] = false;
				con.pressed[Input.WALK_RIGHT] = true;
			}
				
			
		} else if (conn instanceof FallPathConnection){
			float toX = Math.abs(to.x + .5f - pos.x), toY = Math.abs(to.y + .5f - pos.y );
			
			FallPathConnection jconn = (FallPathConnection) conn;
			//lr
			
				
			AFollowFall act = Pools.obtain(AFollowFall.class);
					
			act.conn = jconn;
			addBeforeMe(act);
			currentIndex++;
			if (currentIndex >= path.path.getCount())
				isFinished = true;
					
			
		} else {
			float dx = to.x + .5f - pos.x;
			Gdx.app.log(TAG,  "OTHER" + pos + to  + conn.getClass().getSimpleName());
			if (dx < 0){
				con.pressed[Input.WALK_LEFT] = true;
				con.pressed[Input.WALK_RIGHT] = false;
			} else {
				con.pressed[Input.WALK_LEFT] = false;
				con.pressed[Input.WALK_RIGHT] = true;
			}
			
			if ((int)pos.x == to.x && (int)(pos.y) == to.y){
				//Gdx.app.log(TAG,  "FINISHED other" + dx + " , " );
				currentIndex++;
				if (currentIndex >= path.path.getCount()){
					isFinished = true;
				}
			}
		}
		
	}

	@Override
	public void onEnd() {
		Control con = controlM.get(parent.e);
		con.pressed[Input.WALK_LEFT] = false;
		con.pressed[Input.WALK_RIGHT] = false;
		//Gdx.app.log(TAG, "END ");
		AFinishPath finish = Pools.obtain(AFinishPath.class);
		addAfterMe(finish);
		finish.x = path.path.get(path.path.getCount() - 1).getToNode().x; 
		finish.y = path.path.get(path.path.getCount() - 1).getToNode().y; 

		path = null;
		parent.e.remove(PathResult.class);
	}

	@Override
	public void onStart() {
		currentIndex = 0;
		String s = "";
		Iterator<Connection<PathNode>> i = path.path.iterator();
		while (i.hasNext()){
			Connection<PathNode> val = i.next();
			s += val + "\n";
			
		}
			
		
		Gdx.app.log(TAG, "start path \n" + s);
	}

}
