package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Physics implements Component, Poolable{
	private static final String TAG = "physics c ";
	//public Body body;
	public Vector2 vel = new Vector2();
	public Vector2 acc = new Vector2();
	public Vector3 limit = new Vector3(10,10, 10);//z is down
	public Vector2 gravity = new Vector2();
	public int bodyTypeID = 1;
	public boolean onGround;
	public boolean onWall;
	
	public long onGroundTime;
	public boolean onSlope;
	public boolean left;
	public boolean wasOnGround, wasOnGround2;
	public float apexTime;
	@Override
	public void reset() {
		//body = null;
		vel.set(0,0);
		acc.set(0,0);
		limit.set(10,10, 10);
		onWall = false;
		onGround = false;
		wasOnGround = false;
		wasOnGround2 = false;
		gravity.set(0, -30);
	}

	public void applyLinearImpulse(float x, float y) {
		vel.add(x, y);
		if (x >  1000 || x < -1000){
			//Gdx.app.log(TAG, "impulse "+vel);
			throw new GdxRuntimeException("kjhlsdk");
		}
	}

	public void applyForceToCenter(float x, float y) {
		acc.add(x, y);
		if (x >  1000 || x < -1000){
			//Gdx.app.log(TAG, "force to centre"+acc);
			//Gdx.app.log(TAG, "impulse "+vel);
			throw new GdxRuntimeException("kjhlsdk");
		}
	}

	public void setLinearVelocity(float x, float y) {
		vel.set(x, y);
		//Gdx.app.log(TAG, "set linvel "+vel);
		if (x >  1000 || x < -1000){
			
			throw new GdxRuntimeException("kjhlsdk");
		}
	}

	
	
	
	
}
