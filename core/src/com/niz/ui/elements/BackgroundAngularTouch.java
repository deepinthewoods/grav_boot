package com.niz.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.niz.component.VectorInput;
import com.niz.component.VectorInput2;
import com.niz.component.VectorInput4;

/**
 * Created by niz on 31/05/2014.
 */
public class BackgroundAngularTouch extends UIElement {
    private static final String TAG = "background toucher";
    transient VectorInput vec = new VectorInput();
    transient VectorInput2 vec2 = new VectorInput2();
    transient VectorInput4 vec4 = new VectorInput4();
    public BackgroundAngularTouch(){

        send = new String[]{"screenClicked", "screenDragged", "screenPinched"};

    }
    @Override
    protected void onInit(Skin skin) {
        //Table table = new Table();
        //table.setFillParent(true);
        //actor = table;
        //actor = new Button(skin);
        actor = new Actor();
        actor.setSize(100000, 100000);



        actor.addListener(new ActorGestureListener(){
            long lastDownTime;
            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                vec2.v.set(x,y);
                vec2.v2.set(deltaX, deltaY);
                if (subjects != null && subjects.length >1)
                    subjects[1].notify(null, null, vec2);

                //Gdx.app.log(TAG, "drag");

            }

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {

                super.touchDown(event, x, y, pointer, button);
                lastDownTime = System.currentTimeMillis();
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (System.currentTimeMillis() - 500 > lastDownTime)
                    return;
                vec.v.set(event.getStageX(),Gdx.graphics.getHeight()-event.getStageY());
                if (subjects != null && subjects.length >0) {
                    subjects[0].notify(null, null, vec);
                }
                    //Gdx.app.log(TAG, "click");

            }

            @Override
            public void pinch(InputEvent event, Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                vec4.v.set(initialPointer1);
                vec4.v2.set(initialPointer2);
                vec4.v3.set(pointer1);
                vec4.v4.set(pointer2);

                if (subjects != null && subjects.length >2)
                    subjects[2].notify(null, null, vec4);
            }


        });
       
       /* actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event,
                                float x,
                                float y){
                vec.v.set(x,y);
                subjects[0].notify(null, null, vec);
                Gdx.app.log(TAG, "click");

            }
        });//*/
    }
}
