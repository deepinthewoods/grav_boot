package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.niz.anim.AnimSetDef;
import com.niz.anim.AnimationCommand;
import com.niz.editor.Executable;

public class TestComponent implements Component {
//public Array<AnimSetDef> animSets = new Array<AnimSetDef>();
//public String testString = "fobar";
public int bleh = 5;
//public Object obj = new Object(){;
	public boolean setting1;
	public boolean setting2;
	public boolean setting3;
public TestComponent(){
	//AnimSetDef def = new AnimSetDef();
	//def.animations.add(new AnimationCommand());
	//animSets.add(def);
	//animSets.add(new AnimSetDef());
	setting3 = false;
	setting2 = false;
}


public void _start(){
	Gdx.app.log("testc", "do something");
}


}
