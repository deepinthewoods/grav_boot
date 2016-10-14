package com.niz.actions.mapgen;

import com.niz.action.Action;

public class ARoomSetup extends Action{
	public Action after;

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		addAfterMe(after);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

}
