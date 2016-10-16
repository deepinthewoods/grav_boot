package com.niz.ui.elements;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.niz.Input;
import com.niz.component.ButtonInput;
import com.niz.observer.Subject;

/**
 * Created by niz on 31/05/2014.
 */
public class BackgroundTouch extends UIElement {
    private static final String TAG = "background toucher";
    Component c;
	private ClickListener listener;
    public BackgroundTouch(){

        send = new String[]{"screen"};
        ButtonInput b = new ButtonInput();
        b.code = Input.JUMP;
        c = b;
    }
    @Override
    protected void onInit(Skin skin) {
        //Table table = new Table();
        //table.setFillParent(true);
        //actor = table;
        //actor = new Button(skin);
        actor = new Actor(){

			@Override
			public void act(float delta) {
				if (listener.isPressed()){
					subjects[0].notify(null, Subject.Event.BUTTON_IS_PRESSED, c);
					
					//Gdx.app.log(TAG, "nitufy");
				}
				super.act(delta);
			}
        	
        };
        
        
        actor.setSize(100000, 100000);
        listener = new ClickListener() {
            

			

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				subjects[0].notify(null, Subject.Event.BUTTON_RELEASE, c);
				//Gdx.app.log(TAG, "nituffdsfadsy");

				super.touchUp(event, x, y, pointer, button);
			}

            
        };
        actor.addListener(listener);

    }
    
}
