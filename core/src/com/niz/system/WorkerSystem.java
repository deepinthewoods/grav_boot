package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.niz.Data;
import com.niz.action.Action;
import com.niz.action.ActionList;
import com.niz.action.ProgressAction;
import com.niz.component.Position;
import com.niz.system.WorkerThread.WorkerRunnable;

public class WorkerSystem extends RenderSystem {
	ComponentMapper<ActionList> actM = ComponentMapper.getFor(ActionList.class);
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	private static final String TAG = "worker system";
	Array<Entity> queue = new Array<Entity>();
	Entity current;
	Array<WorkerThread> threads = new Array<WorkerThread>();
	public ProgressBarSystem progressSys;
	private EngineNiz engine;
	private boolean threaded = true;
	private boolean waitToAddEntities = true;
	boolean queueBeltRefresh = false;
	@Override
	public void addedToEngine(Engine engine) {
		if (Gdx.app.getType() == ApplicationType.WebGL){
			threaded = false;
		} else {
			threaded = true;
			for (int i = 0; i < 1; i++){
				WorkerThread thread = new WorkerThread(new WorkerRunnable(), (EngineNiz) engine);
				threads.add(thread);
				thread.start();
				
			}
		}
			
		this.engine = (EngineNiz) engine;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}
	private boolean linear = true;
	public boolean allPaused;
	public void setLinear(boolean on){
		//Gdx.app.log(TAG,  "linear "+on);
		if (linear == on) return;
		if (!on){
			//finish current
			if (current == null) return;
			ActionList act = actM.get(current);
			//Gdx.app.log(TAG,  "finishing current, switching off linear loading");
			while (!act.actions.isEmpty()){
				act.update(1f);
			}
		}
	}
	@Override
	public void update(float deltaTime) {
	//Gdx.app.log(TAG, "queue"+queue.size + linear + (current == null) + waitToAddEntities);
		synchronized (entityAddLock){

			allPaused = true;
			
			if (linear || !threaded){
				if (current == null){
					if (queue.size == 0) {
						
					}
					else {
						current = queue.removeIndex(0);
						allPaused = false;
					}
                } else {
					allPaused = false;
					ActionList act = actM.get(current);
					//Gdx.app.log(TAG,  "linear process ");
					act.update(1f);
					if (act.actions.isEmpty()){
						engine.freeEntity((PooledEntity) current);
						current = null;
					}
				}
				
				
			} else {//start threads from queue
				for (int i = 0; i < threads.size; i++){
					
					WorkerThread thread = threads.get(i);
					if (thread.isPaused()){
						
						if (queue.size == 0) break;
						thread.start(queue.removeIndex(0));
						//Gdx.app.log(TAG,  "thread started");
					} else {
						allPaused = false;
					}
				}
				
				
			}
			boolean allHavePaused = true;
			if (linear && current != null) allHavePaused = false;
			for (int i = 0; i < threads.size; i++){
				WorkerThread t = threads.get(i);
				if (!t.hasPaused) allHavePaused = false; 
			}
			if ( ((waitToAddEntities && allHavePaused && queue.size == 0) || !waitToAddEntities) ){
				
				//Gdx.app.log(TAG, "add ENTs"+  waitToAddEntities);
				waitToAddEntities = false;
				while (entityAddQueue.size > 0){

					Array<PooledEntity> arr = entityAddQueue.pop();
					for (PooledEntity e : arr){

						engine.addEntityNoID(e);
						String s = "";
						for (com.badlogic.ashley.core.Component c : e.getComponents()){
							if (c instanceof Position){
								s += posM.get(e).pos;
							}
							s += c.getClass();
							
						}
						//Gdx.app.log(TAG, "ADDING ENTITIES"+s);
					}
					arr.clear();
					Data.entityArrayPool.free(arr);
				}
				
				
			} 
			if (allPaused && !allHavePaused) allPaused = false;
		}
		
	}
	private Object entityAddLock = new Object();

	public void addWorker(Entity e){
		actM.get(e).engine = engine;
		actM.get(e).inserted(e);
		queue.add(e);
		Action a = actM.get(e).actions.getFirst();
		if (a instanceof ProgressAction){
			ProgressAction p = (ProgressAction) a;
			p.progressBarIndex = progressSys.registerForProgressBar();
		}
		allPaused = false;
		//Gdx.app.log(TAG, "add worker");

	}
	
	

	public void saveAllNow() {
		//pause all threads
		for (int i = 0; i < threads.size; i++){
			WorkerThread t = threads.get(i);
			t.onPause();
		}
		//process all pending workers
		while (queue.size > 0) {
			Entity w = queue.pop();
			ActionList act = actM.get(w);
			//Gdx.app.log(TAG,  "finishing queued workers");
			while (!act.actions.isEmpty()){
				act.update(1f);
			}
		}
		//try finish processing threads, sleep if any aren't finished
		boolean allFinished = false;
		while (!allFinished){
			allFinished = true;
			for (int i = 0; i < threads.size; i++){
				WorkerThread t = threads.get(i);
				WorkerRunnable runnable = t.getRunnable();
				if (t.hasPaused){
					if (runnable.e != null){
						Entity w = runnable.e;
						ActionList act = actM.get(w);
						//if (!act.actions.isEmpty())Gdx.app.log(TAG,  "finishing worker"+act.actions.getFirst().getClass());
						while (!act.actions.isEmpty()){
							act.update(1f);
						}
						runnable.e = null;
						
					} 
				} 
				if (t.hasPaused){
					
				} else {
					allFinished = false;
					//Gdx.app.log(TAG,  "not paused "+i);
				}
				if (!t.hasPaused || runnable.e != null){
					//allFinished = false;
				}
			}
			
			if (!allFinished){
				synchronized (this){
					try {
						//Gdx.app.log(TAG,  "waiting");
						this.wait(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} 
		}
	}

	public void addWorker(ProgressAction action) {
		allPaused = false;
		PooledEntity e = engine.createEntity();
		ActionList act = engine.createComponent(ActionList.class);
		e.add(act);
		act.addToStart(action);
		addWorker(e);
	}

	//IntFloatMap progressBars = new IntFloatMap();
	
	Array<Array<PooledEntity>> entityAddQueue = new Array<Array<PooledEntity>>();
	
	public void addEntities(Array<PooledEntity> arr) {
		synchronized (entityAddLock){
			
			entityAddQueue.add(arr);
		}
		
	}
	
	

	public void setWaitToAddEntities(boolean b) {
		synchronized (entityAddLock){
			waitToAddEntities = b;			
		}
	}
	
}
