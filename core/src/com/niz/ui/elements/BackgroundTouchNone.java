package com.niz.ui.elements;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.niz.Input;
import com.niz.component.ButtonInput;

/**
 * Created by niz on 31/05/2014.
 */
public class BackgroundTouchNone extends UIElement {
    private static final String TAG = "background toucher";
    Component c;
	private ClickListener listener;
    public BackgroundTouchNone(){

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

			
        	
        };
        
        actor.setTouchable(Touchable.disabled);
        actor.setSize(100000, 100000);
        

    }
    
}
