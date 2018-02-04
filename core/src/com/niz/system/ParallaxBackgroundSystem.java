package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;
import com.niz.Data;
import com.niz.Main;
import com.niz.ZoomInput;
import com.niz.component.CameraControl;
import com.niz.component.Map;
import com.niz.component.Position;
import com.niz.component.VectorInput;
import com.niz.observer.Observer;
import com.niz.observer.Subject.Event;

public class ParallaxBackgroundSystem extends RenderSystem implements Observer {
	public static float zoom = 1f, zoomOutThreshold = 16f;
	public static float ZOOM_OUT_MAX;
	public static final int PARALLAX_LAYERS = 6, PARALLAX_NEAR_LAYERS = 0;
	public static final float[] LAYER_PARALLAX_FACTORS = new float[PARALLAX_LAYERS];
	public static final float[] LAYER_PARALLAX_FACTORS_ZOOMED_IN = new float[PARALLAX_LAYERS];
	public static final float[] LAYER_PARALLAX_FACTORS_ZOOMED_OUT = new float[PARALLAX_LAYERS];
	public static final float[] LAYER_PARALLAX_OFFSETS = new float[PARALLAX_LAYERS];


	private static final Color[] LAYER_COLORS = new Color[PARALLAX_LAYERS] ;


	private float radius = 19;
	private static final String TAG = "px bg sys";
	private static final int[] LAYER_Z_SOURCES = new int[PARALLAX_LAYERS];
	private LightRenderSystem lights;
	private BufferStartSystem buffer;
	private Family family;
	private ComponentMapper<Map> mapM;
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);

	private ImmutableArray<Entity> mapEntities;
	private OverworldSystem overworld;
	private Slice[][] slices = new Slice[100][PARALLAX_LAYERS];
	private float[] offsets = new float[PARALLAX_LAYERS], smallOffsets = new float[PARALLAX_LAYERS];
	private int[][] rel = new int[PARALLAX_LAYERS][PARALLAX_LAYERS];
	private ImmutableArray<Entity> entities;
	private CameraSystem cameraSystem;
	private EngineNiz engine;
	public ShapeRenderingSystem shaper;
	private float viewportSize;
	private ImmutableArray<Entity> camEntities;
	public int startZ;
	public boolean drawSelfLayer;
	public ParallaxBackgroundSystem() {
		this.setProcessing(false);
	}
	@Override
	public void addedToEngine(Engine engine) {
		for (int i = 0; i < LAYER_COLORS.length; i++){
			LAYER_COLORS[i] = new Color();
		}
		for (int x = 0; x < slices.length; x++){
			for (int z = 0; z < PARALLAX_LAYERS; z++){
				slices[x][z] = new Slice(z);
			}
		}
		lights = engine.getSystem(LightRenderSystem.class);
		buffer = engine.getSystem(BufferStartSystem.class);
		family = Family.one(CameraControl.class).get();
		entities = engine.getEntitiesFor(family);
		//engine.addEntityListener(family, this);
		shaper = engine.getSystem(ShapeRenderingSystem.class);
		mapM = ComponentMapper.getFor(Map.class);
		mapEntities = engine.getEntitiesFor(Family.one(Map.class).get());
		overworld = engine.getSystem(OverworldSystem.class); 
		cameraSystem = engine.getSystem(CameraSystem.class);
		cameraSystem.parallaxSys = this;
		engine.getSystem(ParallaxBackgroundRenderNoBufferSystem.class).par = this;
		this.engine = (EngineNiz) engine;
		for (int x = 0; x < slices.length; x++){
			//Slice[] arr = slices[x];
			for (int z = 0; z < slices[0].length; z++){
				//slices[x][z].setHeight(overworld, x, z);
			}
		}
		((EngineNiz) engine).getSubject("resize").add(this);;
		((EngineNiz) engine).getSubject("zoom").add(new Observer(){


			@Override
			public void onNotify(Entity e, Event event, Object c) {
				ZoomInput z = (ZoomInput) c;
				zoom = z.zoom;
				ZOOM_OUT_MAX = 16f;
				float width = overworld.SCROLLING_MAP_TOTAL_SIZE * overworld.SCROLLING_MAP_WIDTH;
				
				ZOOM_OUT_MAX = Gdx.graphics.getWidth() / width; 
				ZOOM_OUT_MAX = 1f/ZOOM_OUT_MAX* Main.PPM*2;
				ZOOM_OUT_MAX = 48;
				
				float f = Gdx.graphics.getWidth() / Main.PPM;//meters the screen displays
				ZOOM_OUT_MAX = width / f * 1.2f;
				//ZOOM_OUT_MAX = 1f/ZOOM_OUT_MAX;
				//Gdx.app.log(TAG, "zoom out max"+ZOOM_OUT_MAX);
			}
			
		});;
		
		camEntities = engine.getEntitiesFor(Family.all(Position.class, CameraControl.class).get());

	}

	@Override
	public void removedFromEngine(Engine engine) {
		
	}

	@Override
	public void update(float deltaTime) {
		//if (!cameraSystem.zoomedOut){
			draw();
		//}
		
	}
	
	public void draw(){

		/*for (int i = 0; i < mapEntities.size(); i++){
			Entity e = mapEntities.get(i);
			Map map = mapM.get(e);
			boolean done = false;
			for (int p = 0; p < map.dirtyRuns.length; p++){
				if (!done && map.dirtyRuns[p]){
					done = true;
					map.makeRuns(p, overworld);
				}
			}
			
		}*/
		if (cameraSystem.zoomedOut){
			//buffer.currentBuffer.end();
		}
		float originalZoom = cameraSystem.camera.zoom;
		if (true || originalZoom > 1){
			cameraSystem.camera.zoom = 1f;
			//Gdx.app.log(TAG,  "zoom bigger than 1");
			//LAYER_PARALLAX_FACTORS[0] = 1f/zoom;
			
			
		} else {
		}
		startZ = overworld.currentZ;
		//float zoom = cameraSystem.camera.zoom;
		
		if (true || zoom < zoomOutThreshold){
			//lerp 
			float zm = zoom;
			float alpha = zm - 1f;
			alpha = alpha / (zoomOutThreshold-1f);
			alpha = Math.min(1f, alpha);
			//alpha = Math.max(0f, alpha);
			//Gdx.app.log(TAG, "alpha "+alpha + "  zoom"+zoom);
			//((x) * (x) * (3 - 2 * (x)))
			float alphas = alpha * alpha * (3-2*alpha);//smoothstep
			float alphap = 1 - (1 - alpha) * (1 - alpha);
			alphap = 1 - (1 - alphap) * (1 - alphap);
			alphap = 1 - (1 - alphap) * (1 - alphap);
			float alphap2 = alpha * alpha;
			for (int i = 0; i < PARALLAX_LAYERS; i++){
				LAYER_PARALLAX_FACTORS[i] = MathUtils.lerp(LAYER_PARALLAX_FACTORS_ZOOMED_IN[i], LAYER_PARALLAX_FACTORS_ZOOMED_OUT[i], alpha);
				
				//if (i == 7){
					//Gdx.app.log(TAG, "dist  " + alpha + "  " + i + " " + LAYER_PARALLAX_OFFSETS[i]);
				//}
				
				if (i > startZ && i < startZ + 1 + PARALLAX_NEAR_LAYERS)
					LAYER_PARALLAX_FACTORS[i] = MathUtils.lerp(LAYER_PARALLAX_FACTORS_ZOOMED_IN[i], LAYER_PARALLAX_FACTORS_ZOOMED_OUT[i], alpha);
					
			}
		} 
		
		
		setFactors(startZ, originalZoom);
		//LAYER_PARALLAX_FACTORS[startZ] = 1f/zoom;
		for (int i = 0; i < PARALLAX_LAYERS; i++){
			//if (i > startZ && 
					//LAYER_PARALLAX_FACTORS[i] > 1f/zoom){
			//	LAYER_PARALLAX_FACTORS[i] = 1f/zoom;
			//}
		}
		
		if (cameraSystem.zoomedOut){
			drawSelfLayer = true;
		} else {
			drawSelfLayer = false;
		}
			//startZ = 0;
		//else 
		//startZ++;
		//startZ = 0;
		cameraSystem.camera.update();
		shaper.rend.setProjectionMatrix(cameraSystem.camera.combined);
		//shaper.rend.getProjectionMatrix().scale(1,  -1,  1);
		
		
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			
			
		}
		if (camEntities.size() == 0) return;
		Entity cam = camEntities.get(0);
		
		float x = (int)(cameraSystem.camera.position.x )/(float)Main.PPM;
		//x = posM.get(cam).pos.x;
		//Gdx.app.log(TAG, "x "+x);
		for (int i = 0; i < offsets.length; i++){
			offsets[i] = //(int)
					(x * LAYER_PARALLAX_FACTORS[i] );
			//Gdx.app.log(TAG, "layer "+i+" offset: "+offsets[i]);
		}
		
		for (int bx = 0; bx < PARALLAX_LAYERS; bx++){
			for (int by = 0; by < PARALLAX_LAYERS; by++){
				float off = offsets[bx];
				float smallOffset = off - MathUtils.floor(off);
				
				float offB = offsets[by];
				float smallOffsetB = offB - MathUtils.floor(offB);
				
				if (smallOffset > smallOffsetB){
					rel[bx][by] = (int)offB-(int)off ;
				} else rel[bx][by] = (int)offB - (int)off+1;
				
			}
		}
		
		
		int ny = (int)
				(cameraSystem.camera.position.y);
		shaper.rend.begin(ShapeType.Filled);
		shaper.rend.setColor(Color.WHITE);
		Gdx.gl.glLineWidth(1f);
		
		
		
		for (int layer = PARALLAX_LAYERS-1; layer >= 0; layer--){
			float offset = offsets[layer];
			//float smallOffset = offset - MathUtils.floor(offset);
			//float y = ny * LAYER_PARALLAX_FACTORS[layer];
			//smallOffset = cameraSystem.camera.position.x - (int)cameraSystem.camera.position.x;
			//smallOffset /= (float)Main.PPM;
			int tot = slices.length;
			for (int lx = MathUtils.floor( - radius)-1; lx < + radius; lx++){
				Slice slice = slices[(MathUtils.floor(offset+lx) % tot + tot) % tot][layer];
				if (slice.setHeight(overworld, MathUtils.floor(offset+lx), layer, MathUtils.floor(offset+lx), 1f ));
				//slice.setOverlaps(slices, rel[layer], ny - y);
				
				//slice.draw((x+lx-smallOffset), shaper.rend, lx, smallOffset, offset, overworld);
			}
		}
		
		for (int layer = 0; layer < PARALLAX_LAYERS; layer++){
			float offset = offsets[layer];
			//float smallOffset = offset - MathUtils.floor(offset);
			int y = (int)(ny * LAYER_PARALLAX_FACTORS[layer] - LAYER_PARALLAX_OFFSETS[layer]);
			//smallOffset = cameraSystem.camera.position.x - (int)cameraSystem.camera.position.x;
			//smallOffset /= (float)Main.PPM;
			int tot = slices.length;
			for (int lx = MathUtils.floor( - radius)-1; lx < + radius; lx++){
				Slice slice = slices[(MathUtils.floor(offset+lx) % tot + tot) % tot][layer];
				//if (slice.setHeight(overworld, MathUtils.floor(offset+lx), layer));
				slice.setOverlaps(slices, rel[layer], ny - y, startZ);
				//if (slice.z == 3 && slice.x == 0)Gdx.app.log(TAG, "height "+slice.worldHeight + "   " + "  actual "+
					//("")			);
				//slice.draw((x+lx-smallOffset), shaper.rend, lx, smallOffset, offset);
			}
		}
		
		for (int layer = PARALLAX_LAYERS-1; layer >= (drawSelfLayer?0:startZ); layer--){
			float offset = offsets[layer];
			float smallOffset = offset - MathUtils.floor(offset);
			//float y = ny * LAYER_PARALLAX_FACTORS[layer];
			//smallOffset = cameraSystem.camera.position.x - (int)cameraSystem.camera.position.x;
			//smallOffset /= (float)Main.PPM;
			int tot = slices.length;
			for (int lx = MathUtils.floor( - radius)-1; lx < + radius; lx++){
				Slice slice = slices[(MathUtils.floor(offset+lx) % tot + tot) % tot][layer];
				//if (slice.setHeight(overworld, MathUtils.floor(offset+lx), layer));
				//slice.setOverlaps(slices, rel[layer], ny - y);
				if (layer != startZ || drawSelfLayer)
				slice.draw((x+lx-smallOffset), shaper.rend, lx, smallOffset, offset, overworld);
			}
		}
		shaper.rend.end();
		cameraSystem.camera.zoom = originalZoom;
		
		float heightM = Gdx.graphics.getHeight() * Main.PX;
		float space = heightM/ (float)(PARALLAX_LAYERS - PARALLAX_NEAR_LAYERS ) * Main.PPM * .8f;
		
		//cameraSystem.camera.position.y -= LAYER_PARALLAX_OFFSETS[zValue] * cameraSystem.camera.zoom;
		//cameraSystem.camera.position.y += 1000;
		//Gdx.app.log(TAG, "cam y adjust "+LAYER_PARALLAX_OFFSETS[zValue]);
		cameraSystem.camera.update();
		if (cameraSystem.zoomedOut){
			//buffer.currentBuffer.begin();
		}
	}
	
	public void drawFront(){
		float originalZoom = cameraSystem.camera.zoom;
		if (originalZoom > 1){
			//cameraSystem.camera.zoom = 1f;
			
			//LAYER_PARALLAX_FACTORS[0] = 1f/zoom;
			
			
		}
		float x = (int)(cameraSystem.camera.position.x )/(float)Main.PPM;
		cameraSystem.camera.update();
		shaper.rend.setProjectionMatrix(cameraSystem.camera.combined);
		
		shaper.rend.begin(ShapeType.Filled);
		shaper.rend.setColor(Color.WHITE);
		Gdx.gl.glLineWidth(1f);
		for (int layer = startZ-1; layer >= 0; layer--){
			float offset = offsets[layer];
			float smallOffset = offset - MathUtils.floor(offset);
			//float y = ny * LAYER_PARALLAX_FACTORS[layer];
			//smallOffset = cameraSystem.camera.position.x - (int)cameraSystem.camera.position.x;
			//smallOffset /= (float)Main.PPM;
			int tot = slices.length;
			for (int lx = MathUtils.floor( - radius)-1; lx < + radius; lx++){
				Slice slice = slices[(MathUtils.floor(offset+lx) % tot + tot) % tot][layer];
				//if (slice.setHeight(overworld, MathUtils.floor(offset+lx), layer));
				//slice.setOverlaps(slices, rel[layer], ny - y);
				if (layer != startZ || drawSelfLayer)
				slice.draw((x+lx-smallOffset), shaper.rend, lx, smallOffset, offset, overworld);
			}
		}
		shaper.rend.end();
		cameraSystem.camera.zoom = originalZoom;
	}
	int[] nearbgColors = {Data.LIGHT_BLUEISH_GREY_INDEX, Data.DARK_BROWN_INDEX, Data.DARK_GREY_INDEX, Data.PURPLE_INDEX};
	int[] backColors = {Data.BLUE_INDEX, Data.RED_INDEX, Data.YELLOW_INDEX, Data.BRIGHT_GREEN_INDEX, Data.DARK_OLIVE_BROWN_INDEX, Data.DARK_OLIVE_INDEX, Data.ORANGE_INDEX, Data.RED_INDEX, Data.LIGHT_GREY_INDEX, Data.DARK_OLIVE_BROWN_INDEX, Data.VERY_DARK_BROWN_INDEX, Data.LIGHT_RED_INDEX};
	private int zValue;
	public void setFactors(int startZ, float originalZoom){
		this.zValue = startZ;
		float heightM = Gdx.graphics.getHeight() * Main.PX;
		float space = heightM/ (float)(PARALLAX_LAYERS - PARALLAX_NEAR_LAYERS ) * Main.PPM * .8f;
		for (int i = 0; i < LAYER_PARALLAX_FACTORS.length; i++){
				if (i == startZ){
					LAYER_PARALLAX_FACTORS_ZOOMED_IN[i] = 1f/originalZoom;
					LAYER_PARALLAX_FACTORS_ZOOMED_OUT[i] = 1f/originalZoom;
					LAYER_PARALLAX_OFFSETS[i] = -16 / originalZoom;
					LAYER_Z_SOURCES[i] = startZ ;
					LAYER_COLORS[i].set(Data.colors[Data.MEDIUM_GREY_INDEX]);
					LAYER_COLORS[i].mul(1f);
				} else
				{ //back layers
					int distance = i - startZ - PARALLAX_NEAR_LAYERS - 1;
					//distance = 0;
					float outFactor = .17f;
					float inFactor = .25f;
					for (int f = 0; f < distance; f++){
						outFactor *= .2357885f;
						inFactor *= .4f;
					}
					//inFactor = 2f;
					//outFactor = inFactor;
					LAYER_PARALLAX_FACTORS_ZOOMED_IN[i] = inFactor;
					LAYER_PARALLAX_FACTORS_ZOOMED_OUT[i] = outFactor;
					LAYER_PARALLAX_OFFSETS[i] = (space) * (distance+1)  * .1f + space;
	
					//LAYER_COLORS[i].set(Data.colors[backColors[distance]]);
					LAYER_Z_SOURCES[i] = startZ - distance;
					
					switch (i){
					case 0:
						LAYER_COLORS[i].set(Data.colors[Data.MEDIUM_GREY_INDEX]);
						LAYER_COLORS[i].mul(1f);
						break;
					case 1: 
						LAYER_COLORS[i].set(Data.colors[Data.DARK_OLIVE_INDEX]);
						LAYER_COLORS[i].mul(1f);
						
						break;
					case 2: 
						LAYER_COLORS[i].set(Data.colors[Data.DARK_PURPLE_INDEX]);
						LAYER_COLORS[i].mul(1f);
						break;
					case 3: 
						LAYER_COLORS[i].set(Data.colors[Data.VERY_DARK_BLUEISH_GREY_INDEX]);
						LAYER_COLORS[i].mul(1f);
						break;
					case 4: 
						LAYER_COLORS[i].set(Data.colors[Data.LIGHT_GREY_INDEX]);
						LAYER_COLORS[i].mul(.55f);
						break;
					}
				}
		}
	}
	
	
	public static float easeIn (float t,float b , float c, float d) {
		return c*(t/=d)*t*t + b;
	}
	
	public static float easeOut (float t,float b , float c, float d) {
		return c*((t=t/d-1)*t*t + 1) + b;
	}
	
	public static float easeInOut (float t,float b , float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t*t + b;
		return c/2*((t-=2)*t*t + 2) + b;
	}
	

	public static class Slice{
		private static final int SLICE_WIDTH = 1;
		private static final String TAG = "slice";
		public static final float d1 = .5f, d2 = .3f, d3 = .225f, d4 = .15f, d5 = .25f, d6 = .15f;
			;
		
		public float height, heightNext, worldHeight, worldHeightNext, worldHeight3, worldHeight4;
		public float lowest2, lowest3, highest2, highest3;
		public int x = Integer.MAX_VALUE, z;
		private float bottom2;
		private float bottom3;
		private float lowest4;
		private float highest4;
		private float bottom4;
		private float height3;
		private float height4;
		public Slice(int z2) {
			z = z2;
		}
		public boolean setHeight(OverworldSystem overworld, int x, int z, int adjustedX, float dx) {
			//if (x == this.x) return true;
			worldHeight = overworld.getHeight(adjustedX+1, z, LAYER_PARALLAX_FACTORS[z])*Main.PPM;
			this.x = x;
			this.z = z;
			worldHeightNext = overworld.getHeight((int) (adjustedX+dx*1+1), z, LAYER_PARALLAX_FACTORS[z])*Main.PPM;
			worldHeight3 = overworld.getHeight((int) (adjustedX+dx*2+1), z, LAYER_PARALLAX_FACTORS[z])*Main.PPM;
			worldHeight4 = overworld.getHeight((int) (adjustedX+dx*3+1), z, LAYER_PARALLAX_FACTORS[z])*Main.PPM;
			
			//worldHeight-= Main.PPM;
			//worldHeight3-= Main.PPM;
			//worldHeight4-= Main.PPM;
			//worldHeightNext-= Main.PPM;
			return true;
		}
		
		public void setOverlaps(Slice[][] slices, int[] relativeOffsets, float y, int startZ){
			//worldHeight = height + ny - y;
			//worldHeightNext = heightNext + ny - y;
			height = worldHeight+y;
			
			heightNext = worldHeightNext+y;
			height3 = worldHeight3+y;
			height4 = worldHeight4+y;
			//int t = 400 * 16; height = t; heightNext = t; height3 = t; height4 = t;
			lowest2 = Math.min(height,  heightNext);
			highest2 = Math.max(height, heightNext);
			lowest3 = Math.min(height3, Math.min(height,  heightNext));
			highest3 = Math.max(height3, Math.max(height, heightNext));
			//lowest2 = Math.min(heightNext,  heightNext);
			highest4 = Math.max(height3, height4);
			lowest4 = Math.min(Math.min(height3, height4), heightNext);
			
			
			float low3 = 0, low2 = 0, low4 = 0;
			int tot = slices.length;
			for (int i = startZ; i < z; i++){
				Slice slice = slices[((x+relativeOffsets[i]-2)% tot + tot) % tot][i];
				if (slice.lowest2 > low2)
					low2 = slice.lowest2;
				if (slice.lowest3 > low3)
					low3 = slice.lowest3;
				if (slice.lowest4 > low4)
					low4 = slice.lowest4;
				
			}
			bottom2 = low2;
			bottom3 = low3;
			
			bottom4 = low4;
		}
		private static float[] vertices = new float[8];
		public void draw(float x, ShapeRenderer rend, int lx,
				float smallOffset, float offset, OverworldSystem overWorld) {
			//if (z == 0) return;
			float x2 = x+SLICE_WIDTH;
			float y = height, y2 = heightNext;
			float x0 = x;
			
			rend.setColor(LAYER_COLORS[z]);
			//rend.line((x0 * Main.PPM), (y * Main.PPM), (x2 * Main.PPM), (y2 * Main.PPM));
			//rend.line((x0 * Main.PPM), (bottom3 * Main.PPM), (x2 * Main.PPM), (bottom4 * Main.PPM));
			int i = 0;
			
			float yb = bottom3, yb2 = bottom4;
			if (y < bottom3 && y2 < bottom4) return;
			
			
//			vertices[i++] = x0 * Main.PPM;
//			vertices[i++] = y ;//* Main.PPM;
//			vertices[i++] = x0 * Main.PPM;
//			vertices[i++] = yb ;//* Main.PPM;
//			vertices[i++] = x2 * Main.PPM;
//			vertices[i++] = yb2 ;//* Main.PPM;
//			vertices[i++] = x2 * Main.PPM;
//			vertices[i++] = y2;// * Main.PPM;
			int bx = (int) (x0 );
			Map map = overWorld.getMapForX(bx);
			if (map == null) {}
			else if (false){
				if (bx < 0) return;
				//Gdx.app.log(TAG, "par"+map.offset.x + "  - " + bx);
				//IntArray run = map.backgroundRuns[(bx - (int)map.offset.x)  ];
				IntArray run = null;//map.backgroundRuns[(bx - (int)map.offset.x)  ];
				int currentY = (int)overWorld.getHeight(bx) * Main.PPM;
					
				//Gdx.app.log(TAG, "0 size " + x0 + "  " + run.size);
				for (int r = 0; r < run.size; r+=2){
					boolean top = (r == run.size-2);
					int runLength = run.get(r);
					int value = run.get(r+1);
					//draw run of value 1
					
					//Gdx.app.log(TAG,  "run "+runLength + "  val:"+value + "  @ "+((int)(x0 
							//* Main.PX) ));
					
					if (value == 1){
						y = height; yb = bottom3; yb2 = bottom4; y2 = heightNext;
						int topY = currentY + runLength * Main.PPM;
						if (top) topY = 100000000;
						y = Math.min(Math.max(y,  currentY), topY);
						yb = Math.min(Math.max(yb,  currentY), topY);
						yb2 = Math.min(Math.max(yb2,  currentY), topY);
						y2 = Math.min(Math.max(y2,  currentY), topY);
						//float th = (topY - currentY)*Main.PX;
						//if (runLength < 5) Gdx.app.log(TAG, "h "+ th + " @ "+x0);
						//Gdx.app.log(TAG,  "draw "+currentY + " to "+(currentY+runLength) + "  @ "+(int)x0);
						i = 0;
						vertices[i++] = x0 * Main.PPM;
						vertices[i++] = y ;//* Main.PPM;
						vertices[i++] = x0 * Main.PPM;
						vertices[i++] = yb ;//* Main.PPM;
						vertices[i++] = x2 * Main.PPM;
						vertices[i++] = yb2 ;//* Main.PPM;
						vertices[i++] = x2 * Main.PPM;
						vertices[i++] = y2;// * Main.PPM;
						rend.triangle(vertices[4], vertices[5],  vertices[2], vertices[3], vertices[0], vertices[1]);
						
						rend.triangle(vertices[0], vertices[1], vertices[6], vertices[7],  vertices[4], vertices[5]);
					}
					currentY += runLength * Main.PPM;
				}
			}
			
			
			//for ()
			if (true){
				
				vertices[i++] = x0 * Main.PPM;
				vertices[i++] = y ;//* Main.PPM;
				vertices[i++] = x0 * Main.PPM;
				vertices[i++] = yb ;//* Main.PPM;
				vertices[i++] = x2 * Main.PPM;
				vertices[i++] = yb2 ;//* Main.PPM;
				vertices[i++] = x2 * Main.PPM;
				vertices[i++] = y2;// * Main.PPM;
				rend.triangle(vertices[4], vertices[5],  vertices[2], vertices[3], vertices[0], vertices[1]);
				
				rend.triangle(vertices[0], vertices[1], vertices[6], vertices[7],  vertices[4], vertices[5]);
			}
			
			//Gdx.app.log(TAG,  "y " + y + "  y2 " + y2 + " yb " + yb + "  yb2 "+yb2 + "  m "+x);
			//rend.polygon(vertices);
			//rend.triangle(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5]);
			
			//rend.triangle(vertices[4], vertices[5], vertices[6], vertices[7], vertices[0], vertices[1]);
			

			//rend.circle(x2, y, 16);
			//if (x == 0)Gdx.app.log(TAG, "draw"+x0+","+x2+"  y "+(y/16f)+"  small "+smallOffset);
			
		}
		
	}



	@Override
	public void onNotify(Entity e, Event event, Object c) {
		VectorInput in = (VectorInput) c;
		viewportSize = in.v.x;
		radius = viewportSize / Main.PPM / 2 +1;
	}
}