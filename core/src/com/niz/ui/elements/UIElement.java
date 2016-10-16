package com.niz.ui.elements;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.observer.AutoObserver;
import com.niz.observer.Subject;
import com.niz.ui.edgeUI.UITable;

/**
 * Created by niz on 27/05/2014.
 *     //encompasses one element of the ui i.e. a controller, button, health display, etc.

 */
public abstract class UIElement {

    private static final String TAG = "UI element";
    //protected String[] recieve;//Subjects to listen to, and refresh on onNotify
    public String[] send;//Subject to notify
    protected transient Subject[] subjects;
    protected AutoObserver[] observers;
    public transient Actor actor;
    protected transient UITable parent;

    public UIElement(){

    }
    public void init(Skin skin, UITable parent, EngineNiz engine){
        this.parent = parent;
        if (observers != null)
            for (int i = 0; i < observers.length; i++){
                //create Observers
                AutoObserver o = observers[i];
                if (o == null) continue;
                Subject sub = engine.getSubject(o.from);
                sub.add(o);
            }
        if (send != null) {
            subjects = new Subject[send.length];
            for (int i = 0; i < send.length; i++) {
                subjects[i] = engine.getSubject(send[i]);
            }
        }
        onInit(skin);
    }

    /*
    for making the actor

     */
    protected abstract void onInit(Skin skin);

    public void addTo(Table table){
        table.add(actor);
    }

    public void onMaximize(){

    }

    public void onMinimize(){

    }

}
