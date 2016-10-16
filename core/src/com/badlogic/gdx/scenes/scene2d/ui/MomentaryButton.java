package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.niz.component.ButtonInput;
import com.niz.observer.Subject;

public class MomentaryButton extends Button {

	private ButtonInput input;
	private Entity entity;
	private Subject subject;
	private boolean wasPressed;
	public int cutOff;
	public static final int CUTOFF_NONE = 0, CUTOFF_TOP_LEFT = 1, CUTOFF_TOP_RIGHT = 2;

	public MomentaryButton(Skin skin, Entity e, ButtonInput but, Subject subj) {
		super(skin, "momentaryButton");
		subject = subj;
		input = but;
		entity = e;
		//this.setFillParent(true);
	}

	public void setCheckedWithoutChanging(boolean b) {
		isChecked = b;
	}

	@Override
	public void act(float delta) {
		if (isPressed()){
			wasPressed = true;
			//Gdx.app.log("jkdsl", "act"+this.isPressed());
			subject.notify(entity, Subject.Event.BUTTON_IS_PRESSED, input);
		} else if (wasPressed){
			wasPressed = false;
			subject.notify(entity, Subject.Event.BUTTON_RELEASE, input);

		}
		super.act(delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//Gdx.app.log("momentarybutton", "draw"+getWidth());
		super.draw(batch, parentAlpha);
	}
	
	@Override
	public Actor hit (float x, float y, boolean touchable) {
		if (touchable && this.getTouchable() != Touchable.enabled) return null;
		if ( cutOff == CUTOFF_TOP_LEFT){
			y = getHeight() - y;
			float len = Math.min(getWidth(), getHeight())/2;
			if (x + y < len) return null;
		} else if ( cutOff == CUTOFF_TOP_RIGHT){
			y = getHeight() - y;
			x = getWidth() - x;
			float len = Math.min(getWidth(), getHeight())/2;
			if (x + y < len) return null;
		}
		
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
	}
	
}
