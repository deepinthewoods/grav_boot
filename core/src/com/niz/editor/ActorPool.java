package com.niz.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pool;

public abstract class ActorPool<T> extends Pool<T>{
	Skin skin;
	public EditorScreen ed;
	
	public ActorPool(Skin skin, EditorScreen editorScreen){
		this.skin = skin;
		this.ed = editorScreen;
	}
}
