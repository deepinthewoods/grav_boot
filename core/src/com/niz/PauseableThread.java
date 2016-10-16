package com.niz;

import com.badlogic.gdx.Gdx;

/** A pausable thread. The runnable must not execute an inifite loop but should return control to the thread as often as possible
 * so that the thread can actually pause.
 * 
 * @author mzechner */
public class PauseableThread extends Thread {
	private static final String TAG = "Pauseable thread";
	final Runnable runnable;
	boolean paused = false;
	boolean exit = false;
	public boolean hasPaused = true;

	/** Constructs a new thread setting the runnable which will be called repeatedly in a loop.
	 * 
	 * @param runnable the runnable. */
	public PauseableThread (Runnable runnable) {
		this.runnable = runnable;
	}

	public void run () {
		while (true) {
			synchronized (this) {
				try {
					while (paused){
						//Gdx.app.log(TAG,  "pausedpaused");
						hasPaused = true;
						wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (exit) return;
			//Gdx.app.log(TAG,  "tick");
			runnable.run();
		}
	}

	/** Pauses the thread. This call is non-blocking */
	public void onPause () {
		//Gdx.app.log(TAG,  "onpausedonpaused");
		if (!paused) hasPaused = false;
		paused = true;
	}

	/** Resumes the thread. This call is non-blocking */
	public void onResume () {
		hasPaused = false;
		synchronized (this) {
			paused = false;
			this.notifyAll();
		}
	}

	/** @return whether this thread is paused or not */
	public boolean isPaused () {
		return paused;
	}

	/** Stops this thread */
	public void stopThread () {
		exit = true;
		if (paused) onResume();
	}
}