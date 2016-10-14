package com.niz.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

public class ExpanderEventListener implements EventListener{

	@Override
	public boolean handle(Event event) {
		if (event instanceof ChangeEvent){
			ChangeEvent i = (ChangeEvent) event;
			//if (i.getType() == Type.keyUp && i.getKeyCode() == Keys.ENTER){
				//Gdx.app.log("button", "event"+event+event.getClass() + "  from "+event.getListenerActor().getClass());
				//event.handle();
				
				event.getListenerActor().getStage().unfocusAll();
				((FieldButton)event.getListenerActor()).apply(); 
			//}
		}
		return false;
	}
}
