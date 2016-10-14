package com.niz.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.niz.component.VectorInput;

/**
 * Created by niz on 31/05/2014.
 */
public class BackgroundClick extends UIElement {
    private static final String TAG = "background toucher";
    transient VectorInput vec = new VectorInput();
    public BackgroundClick(){

        send = new String[]{"backgroundTouched"};

    }
    @Override
    protected void onInit(Skin skin) {
        //Table table = new Table();
        //table.setFillParent(true);
        //actor = table;
        //actor = new Button(skin);
        actor = new Actor(){
        	
        };
        
        actor.setSize(100000, 100000);
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event,
                                float x,
                                float y){
                vec.v.set(x,y);
                subjects[0].notify(null, null, vec);
                Gdx.app.log(TAG, "notify");
            }

		

            
        });

    }
    
}
