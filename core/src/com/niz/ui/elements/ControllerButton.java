package com.niz.ui.elements;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.ui.MomentaryButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.Main;
import com.niz.component.ButtonInput;

/**
 * Created by niz on 29/05/2014.
 */
public class ControllerButton extends UIElement{
    public String text;
    Component c;
	private ButtonInput b;
	public MomentaryButton but;

    public ControllerButton(String text, int code){
        this.text = text;
        b = new ButtonInput();
        b.code = code;
        c = b;
        send = new String[]{"screen"};

    }

    @Override
    protected void onInit(Skin skin) {
        but = new MomentaryButton(skin, null, b, subjects[0]){

			
        	
        };
        //if (text != null) b.add(new Label(text, skin));
        Table tab = new Table();
        //tab.add(new Actor()).fillX();
       // tab.add(but);
       // but.setFillParent(true);
        
        actor = but;

    }
    
    
    
}
