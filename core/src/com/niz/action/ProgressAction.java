package com.niz.action;

import com.niz.system.ProgressBarSystem;

public abstract class ProgressAction extends Action {

	public int progressBarIndex;
	@Override
	public void onEnd() {parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}
	
}
