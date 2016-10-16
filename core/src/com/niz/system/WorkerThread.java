package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.niz.PauseableThread;
import com.niz.action.ActionList;

public class WorkerThread extends PauseableThread {

	private WorkerRunnable run;
	
	public WorkerThread(WorkerRunnable run, EngineNiz engine) {
		super(run);
		run.thread = this;
		this.run = run;
		run.engine = engine;
		// TODO Auto-generated constructor stub
	}
	
	public void start(Entity e){
		run.e = e;
		onResume();
	}

	public static class WorkerRunnable implements Runnable{
		private static final String TAG = "worker runnable";
		public Entity e;
		public EngineNiz engine;
		ComponentMapper<ActionList> actM = ComponentMapper.getFor(ActionList.class);
		private ActionList act;
		public PauseableThread thread;
		
		@Override
		public void run() {
			if (e != null){
				act = actM.get(e);
				act.update(1f);
				if (act.actions.isEmpty()){
					thread.onPause();
					//engine.removeEntity(e);
					//e = null;
				}
				
			} else {
				thread.onPause();
			}
			
		}
		
	}

	public WorkerRunnable getRunnable() {
		
		return run;
	}
	
}
