package com.niz.ui.elements;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.MomentaryTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.niz.component.ButtonInput;
import com.niz.observer.Subject;

/**
 * Created by niz on 29/06/2014.
 */
public class ButtonPad extends UIElement {

	public final Entity entity;
	
	public ButtonPad(Entity e){
		entity = e;
	}
	
    public ButtonInput input_fwd = new ButtonInput(),
            input_l = new ButtonInput(),
            input_r = new ButtonInput(),
            input_back = new ButtonInput(),
            input_jump = new ButtonInput();
            //input_middle = new ButtonInput();

    @Override
    protected void onInit(Skin skin) {
        Table table = new Table();

        table.add(new Actor());
        final MomentaryTextButton fwd_btn = new MomentaryTextButton("F", skin, entity, input_fwd, subjects[0]);
        fwd_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(null, Subject.Event.BUTTON_RELEASE, input_fwd);
                fwd_btn.setCheckedWithoutChanging(false);
                changeEvent.cancel();
            }
        });
        table.add(fwd_btn);
        table.add(new Actor());
        table.row();
        final MomentaryTextButton l_btn = new MomentaryTextButton("F", skin, entity, input_l, subjects[0]);
        l_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(entity, Subject.Event.BUTTON_RELEASE, input_l);
                l_btn.setCheckedWithoutChanging(false);
                changeEvent.cancel();
            }
        });

        table.add(l_btn);
        MomentaryTextButton middle_btn = new MomentaryTextButton("F", skin, entity, input_jump, subjects[0]);
        middle_btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(entity, Subject.Event.BUTTON_RELEASE, input_jump);
                fwd_btn.setCheckedWithoutChanging(false);
                changeEvent.cancel();
            }
        });
        table.add(middle_btn);
        final MomentaryTextButton r_btn = new MomentaryTextButton("F", skin, entity, input_r, subjects[0]);
        r_btn.addListener(new ChangeListener() {
            

			@Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(entity, Subject.Event.BUTTON_RELEASE, input_r);
                r_btn.setCheckedWithoutChanging(false);
                changeEvent.cancel();
               // Gdx.app.log("", "mdsull");
            }
        });
        table.add(r_btn);
        table.row();
        table.add(new Actor());
        final MomentaryTextButton back_btn = new MomentaryTextButton("B", skin, entity, input_back, subjects[0]);
        back_btn.addListener(new ChangeListener() {
            

			@Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                subjects[0].notify(entity, Subject.Event.BUTTON_RELEASE, input_back);
                r_btn.setCheckedWithoutChanging(false);
                Gdx.app.log("", "mdsull");
                changeEvent.cancel();
            }
        });
        table.add(back_btn);
        table.add(new Actor());



        actor = table;
    }
    
}
