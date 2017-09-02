package com.niz.ui;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.TimeUtils;

public class ClickDragListener extends InputListener {
	private static final long CLICK_INTERVAL = (long)(0.3f * 1000000000l);
    private float tapSquareSize = 14, touchDownX = -1, touchDownY = -1;
	private int pressedPointer = -1;
	private int button;
	private boolean dragging;
	private float deltaX, deltaY;
	private long time;
	
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if (pressedPointer != -1) return false;
		if (pointer == 0 && this.button != -1 && button != this.button) return false;
		pressedPointer = pointer;
		touchDownX = x;
		touchDownY = y;
		time = TimeUtils.nanoTime();
		return true;
	}

	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (pointer != pressedPointer) return;
		if (!dragging && (Math.abs(touchDownX - x) > tapSquareSize || Math.abs(touchDownY - y) > tapSquareSize)) {
			dragging = true;
			dragStart(event, x, y, pointer);
			deltaX = x;
			deltaY = y;
		}
		if (dragging) {
			deltaX -= x;
			deltaY -= y;
			drag(event, x, y, pointer);
			deltaX = x;
			deltaY = y;
		}
	}

	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		if (pointer == pressedPointer) {
			if (dragging) dragStop(event, x, y, pointer);
			else {
				long time2 = TimeUtils.nanoTime();
				if (time2 - time < CLICK_INTERVAL)
					click(event, x, y, pointer);
			}
			cancel();
		}
	}

	public void click(InputEvent event, float x, float y, int pointer) {
	}

	public void dragStart (InputEvent event, float x, float y, int pointer) {
	}

	public void drag (InputEvent event, float x, float y, int pointer) {
	}

	public void dragStop (InputEvent event, float x, float y, int pointer) {
	}

	/* If a drag is in progress, no further drag methods will be called until a new drag is started. */
	public void cancel () {
		dragging = false;
		pressedPointer = -1;
	}

	/** Returns true if a touch has been dragged outside the tap square. */
	public boolean isDragging () {
		return dragging;
	}

	public void setTapSquareSize (float halfTapSquareSize) {
		tapSquareSize = halfTapSquareSize;
	}

	public float getTapSquareSize () {
		return tapSquareSize;
	}

	public float getTouchDownX () {
		return touchDownX;
	}

	public float getTouchDownY () {
		return touchDownY;
	}

	/** Returns the amount on the x axis that the touch has been dragged since the last drag event. */
	public float getDeltaX () {
		return deltaX;
	}

	/** Returns the amount on the y axis that the touch has been dragged since the last drag event. */
	public float getDeltaY () {
		return deltaY;
	}

	public int getButton () {
		return button;
	}

	/** Sets the button to listen for, all other buttons are ignored. Default is {@link Buttons#LEFT}. Use -1 for any button. */
	public void setButton (int button) {
		this.button = button;
	}
}
