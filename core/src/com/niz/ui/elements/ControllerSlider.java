package com.niz.ui.elements;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.niz.Input;
import com.niz.component.VectorInput;
import com.niz.observer.Subject;

/**
 * Created by niz on 29/05/2014.
 */
public class ControllerSlider extends UIElement{
    VectorInput inp;
    public ControllerSlider(){
        inp = new VectorInput();
        inp.code = Input.JUMP;

        send = new String[]{"playerControl"};
    }

    @Override
    protected void onInit(Skin skin) {
        Slider s = new Slider(-1f, 1f, .01f, false, skin){
            @Override
            public void act(float dt){
                super.act(dt);
                if (this.isDragging()){
                    inp.v.set(getValue(), 0);
                    subjects[0].notify(null, Subject.Event.SLIDER_PRESSED, inp);
                } else {
                    setValue(getValue()/1.52f);
                }
            }

			
			
            
        };

        actor = s;
    }
}
