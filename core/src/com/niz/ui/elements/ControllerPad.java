package com.niz.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.niz.Input;
import com.niz.component.VectorInput;
import com.niz.observer.Subject;

/**
 * Created by niz on 30/05/2014.
 */
public class ControllerPad extends UIElement {
    VectorInput inp;
    public ControllerPad(){
        inp = new VectorInput();
        inp.code = Input.JUMP;

        send = new String[]{"screen"};
    }
    @Override
    protected void onInit(Skin skin) {
        Touchpad p = new Touchpad(.1f, skin){
            @Override
            public void act(float dt){
                super.act(dt);
                if (this.isTouched()){
                    inp.v.set(getKnobX(), getKnobY());
                    subjects[0].notify(null, Subject.Event.SLIDER_PRESSED, inp);
                }
            }
        };
        //p.setScale(12f);
        p.getStyle().background.setMinHeight(100f);
        p.getStyle().background.setMinWidth(100f);
        actor = p;
    }
}
