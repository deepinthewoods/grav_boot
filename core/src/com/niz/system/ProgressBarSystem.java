package com.niz.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.FloatArray;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class ProgressBarSystem extends RenderSystem implements Observer {
	
	private static final String TAG = "progress bar system";
	FloatArray progressBars = new FloatArray();
	Bits progressBarBits = new Bits(128);
	private SpriteBatchN batch;
	private OrthographicCamera camera;
	private Sprite s;
	private float width, progressBarsTotal;
	private float y;
	private float height;

	public ProgressBarSystem(OrthographicCamera uiCamera, SpriteBatchN batch) {
		camera = uiCamera;
		this.batch = batch;
		s = new Sprite(new Texture(Gdx.files.internal("progbar.png")));
		//s = atlas.createSprite("button");
		s.setPosition(0, 0);
	}

	public int registerForProgressBar() {
		int n = progressBars.size;
		progressBars.add(0f);
		progressBarBits.set(n);
		return n;
	}

	public void deregisterProgressBar(int n) {
		setProgressBar(n, 1f);
		progressBarBits.clear(n);
	}

	public void setProgressBar(int key, float val) {
		float oldVal = progressBars.get(key);
		progressBarsTotal += val - oldVal;;
		progressBars.set(key, val);
	}
	@Override
	public void addedToEngine(Engine engine) {
        ((EngineNiz) engine).getSubject("resize").add(this);
        engine.getSystem(WorkerSystem.class).progressSys = this;
		
	}

	@Override
	public void removedFromEngine(Engine engine) {
		
	}

	@Override
	public void update(float deltaTime) {
		float alpha = 0f;
		if (progressBars.size > 0 && progressBarBits.isEmpty()){
			progressBarsTotal = 0f;
			progressBars.clear();
		}
		
		if (progressBars.size == 0) return;
		
		alpha =  progressBarsTotal / (float)progressBars.size;
		float w = width * alpha;
		s.setPosition(0, y);
		s.setSize(w, 10);
		s.setColor(Color.WHITE);
		//Gdx.app.log(TAG, "draw "+w + "  " + alpha);

		batch.setColor(Color.WHITE);
		batch.setProjectionMatrix(camera.combined);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

		batch.setShader(null);
		batch.disableBlending();
		s.getTexture().bind(0);
		batch.begin();
		s.draw(batch);
		batch.end();

		{
			//drawNew(batch, s);
		}

	}

	public void drawNew(SpriteBatchN batch) {
		if (progressBars.size == 0) return;
		float h = Gdx.graphics.getWidth()/20, ww = h*3,  x = Gdx.graphics.getWidth()/2 - ww/2, y = Gdx.graphics.getHeight()/2 - h/2;
		float alpha =  progressBarsTotal / (float)progressBars.size;
		alpha %= 1f;
		batch.getProjectionMatrix().setToOrtho2D(0,  0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		batch.draw(s, 0, 0, Gdx.graphics.getWidth() * alpha , h);
		batch.end();
		//Gdx.app.log(TAG, "render " + alpha);
	}

	@Override
	public void onNotify(Entity e, Event event, Object c) {
		if (event == Event.RESIZE){
			VectorInput in = (VectorInput) c;
			width = in.v.x;
			y = -in.v.y /2f;
			height = in.v.y;
		}
	}
	
	
	
}
