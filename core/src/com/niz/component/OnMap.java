package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class OnMap implements Component {

	public transient Map map;
	public transient Array<Map> maps = new Array<Map>();

}
