package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.niz.component.ButtonInput;
import com.niz.observer.Subject;

public class MomentaryTextButton extends TextButton {

	private ButtonInput input;
	private Entity entity;
	private Subject subject;
	private boolean wasPressed;

	public MomentaryTextButton(String string, Skin skin, Entity e, ButtonInput but, Subject subj) {
		super(string, skin);
		subject = subj;
		input = but;
		entity = e;
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
	
	
}
