package com.niz.actions.mapgen;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.niz.BlockDefinition;
import com.niz.Blocks;
import com.niz.Input;
import com.niz.action.Action;
import com.niz.component.Body;
import com.niz.component.Control;
import com.niz.component.LevelEntrance;
import com.niz.component.Map;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.system.MapSystem;
import com.niz.system.OverworldSystem;

public class AAgentRandomBuildPlatforms extends Action {
	private static final String TAG = "runner builder entitiy";
	private static ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	private static ComponentMapper<Control> controlM = ComponentMapper.getFor(Control.class);
	private static ComponentMapper<Physics> physM = ComponentMapper.getFor(Physics.class);
	private static ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);

	private float groundTime;
	private float airTime;
	private float groundTarget;
	private float airTarget;
	private boolean makePlatform;
	private OverworldSystem overworld;
	private int blockA;
	private boolean running;
	private float runTime;
	private float runTimeTarget;
	private float fallTime;
	private float peakY;
	private float targetDeltaY;
	private float runY;
	private boolean down;
	private float fallTimeTarget;
	private float jumpTime;
	private float jumpTimeTarget;
	private int currentX;
	@Override
	public void update(float dt) {
		Vector2 pos = posM.get(parent.e).pos;
		Control con = controlM.get(parent.e);
		Physics phys = physM.get(parent.e);
		int oldX = currentX; 
		Body body;
		body = bodyM.get(parent.e);
		currentX = (int) (pos.x + body.width);
		if (currentX >= OverworldSystem.SCROLLING_MAP_TOTAL_SIZE * OverworldSystem.SCROLLING_MAP_WIDTH){
			currentX = 0;
			pos.x -=  OverworldSystem.SCROLLING_MAP_TOTAL_SIZE * OverworldSystem.SCROLLING_MAP_WIDTH;
		}
		if (currentX != oldX){
			Map map = overworld.getMapFor(currentX,  0);
			for (int y = 0; y < OverworldSystem.SCROLLING_MAP_HEIGHT; y++){
				map.set(currentX+1,  y, 0);
				map.setBG(currentX+1,  y, Blocks.STONE + MathUtils.random(64));
				
			}
			map.set(currentX+1,  1, Blocks.STONE + MathUtils.random(64));
		}
		con.pressed[Input.WALK_RIGHT] = true;
		
		if (running){
			makePlatform = true;
			if (phys.onGround) runTime += dt;
			if (runTime > runTimeTarget){
				running = false;
				down = MathUtils.randomBoolean();
				if (pos.y > OverworldSystem.SCROLLING_MAP_HEIGHT - 10)
					down = true;
				else if (pos.y < 16)
					down = false;
				targetDeltaY = -MathUtils.random(3);
				jumpTimeTarget = MathUtils.random(1f);
				jumpTimeTarget = jumpTimeTarget * jumpTimeTarget;
				jumpTimeTarget *= 1f;
				fallTimeTarget = MathUtils.random(.4f, 1f);
				con.pressed[Input.JUMP] = true;
				jumpTime = 0f;
				runTime = 0f;
				if (down){
					targetDeltaY = -4;
				}
			}
			runY = pos.y;
		} else {
			makePlatform = false;
			if (phys.vel.y < 0.02f){
				float dy = pos.y - runY;
				fallTime += dt;
				peakY = 0;
				if (down){
					//Gdx.app.log(TAG, "dy " + dy);
					if (dy < targetDeltaY){
						running = true;
						runTimeTarget = MathUtils.random(.2f, .7f);
						peakY = 0;
					}
					
				} else {//up
					if (phys.vel.y < .1f){
						fallTime += dt;
						if (fallTime > fallTimeTarget){
							makePlatform = true;
							running = true;
							runTimeTarget = MathUtils.random(.2f, .7f);
						}
					} else {
						fallTime = 0f;
					}
					
					
				}
			} else {
				peakY = Math.max(peakY,  pos.y);
				
			}
		}
		
		if (con.pressed[Input.JUMP]){
			jumpTime += dt;
			if (jumpTime > jumpTimeTarget){
				con.pressed[Input.JUMP] = false;
			}
		}
		
		if (makePlatform){
			int x = (int) (pos.x + body.width), y = (int) pos.y;
			
			Map map = overworld.getMapFor(x, y-1);
			if (map != null){
				int b = map.get(x, y-1);
				BlockDefinition def = MapSystem.getDef(b);
				if (!def.isSolid){
					map.set(x, y-1, blockA + MathUtils.random(16));
					map.set(x-1, y-1, blockA + MathUtils.random(16));
					//Gdx.app.log(TAG, "MAKEMAKE " + x + "," + y);
					
				}
				
			}
		}
		//Gdx.app.log(TAG, "JDKLSDLKJ");
		
		//map.set(x-1, 10, blockA + MathUtils.random(16));
		
		 
		
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onStart() {
		down = false;
		running = true;
		runTimeTarget = 1f;
		fallTimeTarget = .2f;
		jumpTimeTarget = 1f;
		overworld = parent.engine.getSystem(OverworldSystem.class);
		blockA = Blocks.STONE;
		//blockA = 1024;
		
		Vector2 pos = posM.get(parent.e).pos;
		Map map = overworld.getMapFor(0, 0);
		Body body = bodyM.get(parent.e);
		pos.x = 5;
		pos.y = map.height / 2;
		int x = (int) (pos.x + body.width), y = (int) pos.y;
		for (int i = -2; i < 5; i++) map.set(x-i, y-2, blockA);
		
		PooledEntity en = parent.engine.createEntity();
		Position ePos = parent.engine.createComponent(Position.class);
		ePos.pos.set(pos.x, pos.y+2);
		Gdx.app.log(TAG, "DONEFIFFNIFNI" + ePos.pos + "  total iterations" );
		LevelEntrance entrance = parent.engine.createComponent(LevelEntrance.class);
		en.add(ePos);
		en.add(entrance);
		parent.engine.addEntity(en);;
	}

}
