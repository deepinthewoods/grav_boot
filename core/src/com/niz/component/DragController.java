package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.niz.Main;
import com.niz.PlatformerFactory;

public class DragController implements Component {

	public float scale = 1f;
	public float max = PlatformerFactory.CHAR_SELECT_CHARACTERS * PlatformerFactory.CHAR_SELECT_SPACING;
	public float min = 0;

}
