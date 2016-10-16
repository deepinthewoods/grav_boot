/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.niz.anim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatchNiz;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.niz.BlockDefinition;
import com.niz.Data;
import com.niz.Main;
import com.niz.component.Light;
import com.niz.component.Map;
import com.niz.system.BufferStartSystem;
import com.niz.system.LightRenderSystem;
import com.niz.system.MapRenderSystem;
import com.niz.system.MapSystem;


public class SpriteCacheNiz{
	private static final String 	TAG = "sprite cache";
	private static final String SPRITE_FILENAME_PREFIX = "diff/tile";
	
	public SpriteBatchNiz[] batches// = new SpriteBatchNiz[((map.width/MapRenderSystem.RENDER_SIZE)*(map.height/MapRenderSystem.RENDER_SIZE))]
			, bbatches// = new SpriteBatchNiz[((map.width/MapRenderSystem.RENDER_SIZE)*(map.height/MapRenderSystem.RENDER_SIZE))]
					, fbatches;// = new SpriteBatchNiz[((map.width/MapRenderSystem.RENDER_SIZE)*(map.height/MapRenderSystem.RENDER_SIZE))];
	public static Sprite[] sprites = new Sprite[34*512];;
	public boolean hasCached;
	private static TextureAtlas atlas;
	private SpriteBatchNiz currentBatch, currentBBatch, currentFBatch, currentLitBatch;
	
	private ShaderProgram shader;
	private Matrix4 mat = new Matrix4();
	private int[] backTiles;
	private ShaderProgram bShader;;
	
	private static Pool<SpriteBatchNiz> batchPool = new Pool<SpriteBatchNiz>(){

		@Override
		protected SpriteBatchNiz newObject() {
			return new SpriteBatchNiz((MapRenderSystem.RENDER_SIZE*MapRenderSystem.RENDER_SIZE));
			
		}
		
		
	};
	public static void init(){
		SpriteBatchNiz[] bs = new SpriteBatchNiz[16];
		for (int i = 0; i < bs.length; i++){
			bs[i] = batchPool.obtain();
		}
		for (int i = 0; i < bs.length; i++){
			batchPool.free(bs[i]);
			bs[i] = null;
		}
	}
	
	
	
	private SpriteBatchNiz[] litbatches;
	private Map map;
	private ShaderProgram litShader;
	private ShaderProgram fgShader;
	
	public SpriteCacheNiz(Map map, TextureAtlas atlas, ShaderProgram shader, ShaderProgram bshader, ShaderProgram litShader, ShaderProgram fgShader){
		this.shader = shader;
		this.bShader = bshader;
		this.litShader = litShader;
		this.fgShader = fgShader;
		this.map = map;
		batches = new SpriteBatchNiz[((map.width/MapRenderSystem.RENDER_SIZE)*(map.height/MapRenderSystem.RENDER_SIZE))];
		fbatches = new SpriteBatchNiz[((map.width/MapRenderSystem.RENDER_SIZE)*(map.height/MapRenderSystem.RENDER_SIZE))];
		bbatches = new SpriteBatchNiz[((map.width/MapRenderSystem.RENDER_SIZE)*(map.height/MapRenderSystem.RENDER_SIZE))];
		litbatches = new SpriteBatchNiz[((map.width/MapRenderSystem.RENDER_SIZE)*(map.height/MapRenderSystem.RENDER_SIZE))];

		
		int len = batches.length /(MapRenderSystem.RENDER_SIZE*MapRenderSystem.RENDER_SIZE);
		Texture tex = atlas.getTextures().first();
		for (int i = 0; i < batches.length; i++){
			
		}
		
		
	}
	
	public void draw(Map map, int x, int y, int[] tiles, int[] backTiles, OrthographicCamera camera, LightRenderSystem lights, BufferStartSystem buffer, boolean setAllDirty, ShaderProgram shader) {
		x -= map.offset.x/MapRenderSystem.RENDER_SIZE;
		y -= map.offset.y/MapRenderSystem.RENDER_SIZE;
		int wy =  y;//(int) (y -( map.offset.y/MapRenderSystem.RENDER_SIZE));
		int wx =  x;//(int) (x -( map.offset.x/MapRenderSystem.RENDER_SIZE));
		
		int index = wy + wx*(map.width/MapRenderSystem.RENDER_SIZE);
		if (wx < 0 || wy < 0 || wx >= map.width/MapRenderSystem.RENDER_SIZE || wy >= map.height/MapRenderSystem.RENDER_SIZE){
			
			return;
		}

		
		//x *= MapRenderSystem.RENDER_SIZE;
		//y *= MapRenderSystem.RENDER_SIZE;
		
		//Gdx.app.log(TAG,  "draw "+ "  index "+index + " wx "+wx+" wy"+wy + " tot"+map.width);
		
		//if (x >= 0) return;
		mat.set(camera.combined);
		mat.translate(Main.PPM*(x)*(MapRenderSystem.RENDER_SIZE) +map.offset.x*Main.PPM, Main.PPM*(y)*(MapRenderSystem.RENDER_SIZE)+map.offset.y*Main.PPM, 0);
		//Gdx.app.log(TAG,  "draw "+ "  index "+index + " wx "+(Main.PPM*(x)*(MapRenderSystem.RENDER_SIZE) +map.offset.x*Main.PPM)/Main.PPM+" wy"+(Main.PPM*(y)*(MapRenderSystem.RENDER_SIZE)+map.offset.y*Main.PPM)/Main.PPM + " tot");
		//bshader.begin();
		//bshader.end();
		
			
		currentBBatch = bbatches[index];
		if (currentBBatch == null) {
			populateCurrentBatches(index);
			currentBBatch = bbatches[index];
			if (currentBBatch == null) {return;}
		}
		
		
		
			
		if (!hasCached && (map.dirty[index] || setAllDirty)){
			//map.dirtyRuns[wx] = true;
			cacheChunk(map, index, tiles, backTiles);
			hasCached = true;
			map.dirty[index] = false;
		}
		
		
		
		//Gdx.gl.glDisable(GL20.GL_BLEND);
		drawnBits.set(index);
		
		
		//if (shader == null){
		//	currentBBatch.setColor(.3f, .3f, .3f, 1f);
		//}else currentBBatch.setColor(1f,  1f,  1f,  1f);
		if (shader == null) return;

		currentBBatch.setProjectionMatrix(mat);
		currentBBatch.disableBlending();
		currentBBatch.setShader(shader);
		currentBBatch.beginDraw();		
		lights.setUniforms(Light.MAP_BACK_LAYER, bShader);
		currentBBatch.render();
		currentBBatch.end();
		
		
		
	}
	
	public void draw(int x, int y, LightRenderSystem lights, ShaderProgram shader){
		x -= map.offset.x/MapRenderSystem.RENDER_SIZE;
		y -= map.offset.y/MapRenderSystem.RENDER_SIZE;
		int wy =  y;//(int) (y -( map.offset.y/MapRenderSystem.RENDER_SIZE));
		int wx =  x;//(int) (x -( map.offset.x/MapRenderSystem.RENDER_SIZE));
		
		int index = wy + wx*(map.width/MapRenderSystem.RENDER_SIZE);
		if (wx < 0 || wy < 0 || wx >= map.width/MapRenderSystem.RENDER_SIZE || wy >= map.height/MapRenderSystem.RENDER_SIZE){
			
			return;
		}
		currentBatch = batches[index];
		currentBatch.setProjectionMatrix(mat);
		currentBatch.disableBlending();
		currentBatch.setShader(shader);
		currentBatch.beginDraw();		
		lights.setUniforms(Light.MAP_FRONT_LAYER, shader);
		currentBatch.render();
		currentBatch.end();
		
		
	}
	
	public void drawLit(int x, int y, LightRenderSystem lights, ShaderProgram shader){
		x -= map.offset.x/MapRenderSystem.RENDER_SIZE;
		y -= map.offset.y/MapRenderSystem.RENDER_SIZE;
		int wy =  y;//(int) (y -( map.offset.y/MapRenderSystem.RENDER_SIZE));
		int wx =  x;//(int) (x -( map.offset.x/MapRenderSystem.RENDER_SIZE));
		
		int index = wy + wx*(map.width/MapRenderSystem.RENDER_SIZE);
		if (wx < 0 || wy < 0 || wx >= map.width/MapRenderSystem.RENDER_SIZE || wy >= map.height/MapRenderSystem.RENDER_SIZE){
			
			return;
		}
		currentLitBatch = litbatches[index];
		currentLitBatch.setProjectionMatrix(mat);
		currentLitBatch.disableBlending();
		currentLitBatch.setShader(shader);
		currentLitBatch.beginDraw();		
		lights.setUniforms(Light.MAP_LIT_LAYER, litShader);
		currentLitBatch.render();
		currentLitBatch.end();
		
		
		
	}
	
	public void drawFG(int x, int y, LightRenderSystem lights, ShaderProgram shader){

		x -= map.offset.x/MapRenderSystem.RENDER_SIZE;
		y -= map.offset.y/MapRenderSystem.RENDER_SIZE;
		int wy =  y;//(int) (y -( map.offset.y/MapRenderSystem.RENDER_SIZE));
		int wx =  x;//(int) (x -( map.offset.x/MapRenderSystem.RENDER_SIZE));
		
		int index = wy + wx*(map.width/MapRenderSystem.RENDER_SIZE);
		if (wx < 0 || wy < 0 || wx >= map.width/MapRenderSystem.RENDER_SIZE || wy >= map.height/MapRenderSystem.RENDER_SIZE){
			
			return;
		}
		currentFBatch = fbatches[index];
		currentFBatch.setProjectionMatrix(mat);
		currentFBatch.enableBlending();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		currentFBatch.setShader(shader);
		currentFBatch.beginDraw();		
		lights.setUniforms(Light.MAP_FOREGROUND_LAYER, fgShader);
		currentFBatch.render();
		currentFBatch.end();
	}
	
	

	private void populateCurrentBatches(int index) {
		bbatches[index] = batchPool.obtain();
		batches[index] = batchPool.obtain();
		fbatches[index] = batchPool.obtain();
		litbatches[index] = batchPool.obtain();
		
		bbatches[index].setShader(bShader);;
		batches[index].setShader(shader);;
		fbatches[index].setShader(fgShader);;
		litbatches[index].setShader(litShader);;
		
		bbatches[index].disableBlending();
		batches[index].disableBlending();;// = batchPool.obtain();
		fbatches[index].disableBlending();// = batchPool.obtain();
		litbatches[index].disableBlending();// = batchPool.obtain();
		
		batchBits.set(index);
		
		map.dirty[index] = true;
	}
	
	

	/**
	 * @param index
	 * @return true if successful(found a batch)
	 */
	public boolean beginCache(int index) {
		if (batches[index] == null){
			populateCurrentBatches(index);
			
		}
		currentBatch = batches[index];
		currentBatch.clearCache();
		currentBatch.begin();
		currentBBatch = bbatches[index];
		currentBBatch.clearCache();
		currentBBatch.begin();
		currentFBatch = fbatches[index];
		currentFBatch.clearCache();
		currentFBatch.begin();
		currentLitBatch = fbatches[index];
		currentLitBatch.clearCache();
		currentLitBatch.begin();
		return true;
	}

	public void endCache() {
		currentBatch.end();
		currentBBatch.end();
		currentFBatch.end();
		currentLitBatch.end();

	}

	public void add(Sprite s) {
		s.draw(currentBatch);
	}
	public void addB(Sprite s) {
		s.draw(currentBBatch);
	}
	public void addFG(Sprite s) {
		s.draw(currentFBatch);
	}
	public void addLit(Sprite s){
		s.draw(currentLitBatch);
	}
	

	private boolean cacheChunk(Map map, int index, int[] tiles, int[] backTiles) {
		
		//Gdx.app.log(TAG,  "cache chunk  "+index);
		Sprite s = null;
		
		int x = index / (map.width / MapRenderSystem.RENDER_SIZE);
		int y = index % (map.height / MapRenderSystem.RENDER_SIZE);
		x *= MapRenderSystem.RENDER_SIZE;
		y *= MapRenderSystem.RENDER_SIZE;
		if (!beginCache(index)) return true;
		for (int tx = 0; tx < MapRenderSystem.RENDER_SIZE; tx++){
			for (int ty = 0; ty < MapRenderSystem.RENDER_SIZE; ty++){
				int id = tiles[ty+y+(tx+x)*map.width];
				//if (id != 0)Gdx.app.log(TAG,  "update  "+tx+","+ty + "  "+((id & Map.ID_MASK)>>Map.ID_BITS));
				if ((id & Map.ID_MASK) != 0){
					int tile = id & Map.TILE_MASK;
					BlockDefinition def = MapSystem.defs[(id&Map.ID_MASK) >> Map.ID_BITS];
					
					s = findSprite(tile);
					if (s == null) throw new GdxRuntimeException("Sprite not found: "+tile);
					//s.setPosition(Main.PPM*tx ,  Main.PPM*ty);
					s.setPosition( Main.PPM*(tx-.5f) ,  Main.PPM*(ty-.5f));
					if (def.isSeeThrough) {
						addFG(s);
						int bid = backTiles[ty+y+(tx+x)*map.width];
						int tileb = bid & Map.TILE_MASK;
						if (bid != 0){
							s = findSprite(tileb);
							if (s == null) throw new GdxRuntimeException("Sprite not found: "+tileb);
							s.setPosition( Main.PPM*(tx-.5f) ,  Main.PPM*(ty-.5f));
							//Gdx.app.log(TAG, "addingB "+s.getX());
							addB(s);
							
						}
					}
					else if (def.isLit)addLit(s);
					else add(s);
				} else {
					int bid = backTiles[ty+y+(tx+x)*map.width];
					int tile = bid & Map.TILE_MASK;
					if (bid != 0){
						s = findSprite(tile);
						if (s == null) throw new GdxRuntimeException("Sprite not found: "+tile);
						s.setPosition( Main.PPM*(tx-.5f) ,  Main.PPM*(ty-.5f));
						//Gdx.app.log(TAG, "addingB "+s.getX());
						addB(s);
						
					}
				}
			}
		}		
		endCache();
		//Gdx.app.log(TAG,  "done cache chunk  "+index);
		return true;
	}

	public static Sprite findSprite(int i) {
		//Gdx.app.log(TAG,  "find "+i);
		if (sprites[i] != null) return sprites[i];
		//Gdx.app.log(TAG,  "create"+i);
		Sprite s = atlas.createSprite(SPRITE_FILENAME_PREFIX, i);
		if (s == null) throw new GdxRuntimeException("null spr " + i + Data.getString(i));
		s.setSize(Main.PPM*2, Main.PPM*2);
		sprites[i] = s;
		//Gdx.app.log(TAG,  "done create  "+i + " " + s.getU() + " v "+s.getV() + " u2:"+s.getU2() + ", " + s.getV2() );
		return s;

	}
	Bits drawnBits = new Bits(), batchBits = new Bits(), bits = new Bits()
			;
	
	public void endDraw() {
		bits.clear();
		bits.or(batchBits);
		
		bits.andNot(drawnBits);
		
		while (true){
			int index = bits.nextSetBit(0);
			if (index == -1) break;
			//Gdx.app.log(TAG,  "remove batches"+index);
			if (batches[index] == null) throw new GdxRuntimeException("clearing batch error" );
			batchPool.free(batches[index]);
			batchPool.free(bbatches[index]);
			batchPool.free(fbatches[index]);
			batchPool.free(litbatches[index]);
			
			litbatches[index] = null;
			batches[index] = null;
			fbatches[index] = null;
			bbatches[index] = null;
			batchBits.clear(index);
			bits.clear(index);
			
		}
		
		
	}

	public void beginDraw(boolean skipDraw) {
		drawnBits.clear();
		hasCached = false;
	}
	

	public static void setAtlas(TextureAtlas atlas2) {
		atlas = atlas2;
	}

	public void dispose() {
		bits.clear();
		bits.or(batchBits);
		//bits.andNot(drawnBits);
		while (true){
			int index = bits.nextSetBit(0);
			if (index == -1) break;
			//Gdx.app.log(TAG,  "remove batches"+index);
			batchPool.free(batches[index]);
			batchPool.free(bbatches[index]);
			batchPool.free(fbatches[index]);
			batchPool.free(litbatches[index]);
			
			litbatches[index] = null;
			batches[index] = null;
			fbatches[index] = null;
			bbatches[index] = null;
		}
		while (batchPool.getFree() > 0){
			SpriteBatchNiz b = batchPool.obtain();
			b.dispose();
			
		}
	}

	
	
	
}