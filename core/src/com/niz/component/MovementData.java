package com.niz.component;

import com.badlogic.ashley.core.Component;

public class MovementData implements Component {
public float jump_y_force, jump_impulse, jump_y_force_decrement_rate;
public float run_force;
public boolean left;
public float jump_y_force_time;
public float jump_y_force_delay;
public float jump_x_force;
//public float walljump_x_impulse;
public float walljump_y_impulse;
public boolean cancelLiftOnRelease;
public float changeDirectionForceMultiplier = 1f;
public float walljump_x_impulse;
private int doubleJumpsRemaining = 0;
public int numberOFDoubleJumps = 0;
public boolean hasWallSlide;

public boolean hasDoubleJump() {
	return doubleJumpsRemaining > 0;
}
public void decrementDoubleJump(){
	doubleJumpsRemaining--;
}

public void resetDoubleJumps(){
	doubleJumpsRemaining = numberOFDoubleJumps;
}
}
