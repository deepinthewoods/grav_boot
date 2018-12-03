package com.niz.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.niz.Input;
import com.niz.component.ButtonInput;
import com.niz.component.VectorInput;
import com.niz.component.VectorInput2;
import com.niz.component.VectorInput4;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

/**
 * Created by niz on 31/05/2014.
 */
public class BackgroundClickDrag extends UIElement {
    private static final String TAG = "background toucher";
    transient VectorInput vec = new VectorInput();
    transient VectorInput2 vec2 = new VectorInput2();
    transient VectorInput4 vec4 = new VectorInput4();
    public BackgroundClickDrag(){
        send = new String[]{"screen"};

    }
    
    
    
    @Override
    protected void onInit(Skin skin) {
        //Table table = new Table();
        //table.setFillParent(true);
        //actor = table;
        //actor = new Button(skin);
    	final ActorGestureListener listener = new ActorGestureListener(){
            long lastDownTime;
            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                vec2.v.set(x,y);
                vec2.v2.set(deltaX, deltaY);
                if (subjects != null)
                    subjects[0].notify(null, Event.ROTATION_CHANGE, vec2);

               // Gdx.app.log(TAG, "drag"+x);

            }

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {

                super.touchDown(event, x, y, pointer, button);
                if (event.isHandled()) return;
                lastDownTime = System.currentTimeMillis();
                vec2.v.set(x, y);
                if (subjects != null){
                    subjects[0].notify(null, Event.ROTATION_CHANGE, vec2);
                    pressed = true;
                }
            }
            
            
            

            @Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				
				super.touchUp(event, x, y, pointer, button);
				pressed = false;
			}

			@Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                /*if (System.currentTimeMillis() - 500 > lastDownTime)
                    return;
                vec.v.set(event.getStageX(),Gdx.graphics.getHeight()-event.getStageY());
                if (subjects != null && subjects.length >0) {
                    subjects[0].notify(null, null, vec);
                }*/
                   // Gdx.app.log(TAG, "click");

            }
			
            Vector2 old1 = new Vector2(), old2 = new Vector2();
            @Override
            public void pinch(InputEvent event, Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                if (old1.equals(pointer1) && old2.equals(pointer2)) return;//this was firing off every frame
                old1.set(pointer1);
                old2.set(pointer2);
                //if (event.isHandled()) return;
                vec4.v.set(initialPointer1);
                vec4.v2.set(initialPointer2);
                vec4.v3.set(pointer1);
                vec4.v4.set(pointer2);
                event.handle();
                if (subjects != null){
                    subjects[0].notify(null, Event.CANCEL_TOUCH, null);
                    pressed = false;
                	subjects[0].notify(null, Event.PINCH, vec4);
                    
                }
            }


        };
    	
    	actor = new Actor(){
    		boolean wasPressed = false;
			@Override
			public void act(float delta) {
				if (pressed){
					c.code = Input.SCREEN_TOUCH;
					subjects[0].notify(null, Subject.Event.BUTTON_IS_PRESSED, c);
					wasPressed = true;
					//Gdx.app.log(TAG, "pressed");
				} else{
					if (wasPressed){
						c.code = Input.SCREEN_TOUCH;
						subjects[0].notify(null, Subject.Event.BUTTON_RELEASE, c);
						//Gdx.app.log(TAG, "nitufy unpr");
					}
					wasPressed = false;
				}
					
				
				super.act(delta);
			}
			
			
        	
        };
        actor.setSize(100000, 100000);//Gdx.graphics.getWidth(), Gdx.graphics.getHeight());//TODO listen for res changes



        actor.addListener(listener);
        actor.addListener(new DragListener(){

			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				vecI.v.set(Gdx.input.getDeltaX(pointer), Gdx.input.getDeltaY(pointer));
				subjects[0].notify(null, Subject.Event.DRAG_SCREEN, vecI);
				
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
    ButtonInput c = new ButtonInput();
    VectorInput2 vecI = new VectorInput2();
    boolean pressed;
}
