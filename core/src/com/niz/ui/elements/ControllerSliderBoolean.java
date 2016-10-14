package com.niz.ui.elements;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SliderNiz;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.niz.Input;
import com.niz.Main;
import com.niz.component.ButtonInput;
import com.niz.observer.Subject;

/**
 * Created by niz on 29/05/2014.
 */
public class ControllerSliderBoolean extends UIElement{
    ButtonInput inp;
    boolean touched, left;
	public SliderNiz slider;
	
    public ControllerSliderBoolean(){
        inp = new ButtonInput();
        

        send = new String[]{"screen"};
    }

    @Override
    protected void onInit(Skin skin) {
        slider = new SliderNiz(-1f, 1f, .01f, false, skin){
        	@Override
			public float getPrefWidth() {
				
				return Main.prefs.move_slider_width;
			}
        	
    	
            


			@Override
			public void draw(Batch batch, float parentAlpha) {
				// TODO Auto-generated method stub
				//super.draw(batch, parentAlpha);
			}





			@Override
			public float getPrefHeight() {
				// TODO Auto-generated method stub
				return Main.prefs.control_button_height;
			}





			@Override
            public void act(float dt){
                super.act(dt);
                
                if (this.isDragging()){
                	touched = true;
                    //Gdx.app.log("controller slider b", "dragged");
                    //inp.v.set(getValue(), 0);
                	boolean l = getValue() > 0;
                	if (l ^ left){
                		subjects[0].notify(null, Subject.Event.BUTTON_RELEASE, inp);
                		left = l;
                	}
                    if (left){
                    	inp.code = Input.WALK_RIGHT;
                    	subjects[0].notify(null, Subject.Event.BUTTON_IS_PRESSED, inp);
                    } else {
                    	inp.code = Input.WALK_LEFT;
                    	subjects[0].notify(null, Subject.Event.BUTTON_IS_PRESSED, inp);
                    }
                    
                } else if (touched){
                    setValue(0f);
                    touched = false;
                    subjects[0].notify(null, Subject.Event.BUTTON_RELEASE, inp);

                }
            }
        };
        slider.setValue(0f);
        slider.addListener(new InputListener(){

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				
				
				super.touchUp(event, x, y, pointer, button);
			}
        	
        });
        //s.setHeight(Gdx.graphics.getHeight()/5f);
       // s.setWidth(Gdx.graphics.getWidth()/2f);
        actor = slider;
    }
}
