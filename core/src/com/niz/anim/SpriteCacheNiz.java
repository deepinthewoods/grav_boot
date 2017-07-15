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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
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
import com.niz.system.OverworldSystem;

public class SpriteCacheNiz{
	private static final String TAG = "sprite cache";
	private static final String SPRITE_FILENAME_PREFIX = "diff/tile";
	private static final int CACHE_TOTAL_TARGET = 64;
	
	public static Sprite[] sprites = new Sprite[34*512];;
	private static final Texture atlasTexture = new Texture(Gdx.files.internal("tilesprocessed.png"));
	private final Texture indexTexture;
	public boolean hasCa2ched;
	public int cachedTotal;
	private static TextureAtlas atlas;

	private ShaderProgram shader;
	private Matrix4 mat = new Matrix4();
	private int[] backTiles;

	private Map map;
	private FrameBuffer[] buffers;

	public SpriteCacheNiz(Map map, TextureAtlas atlas, ShaderProgram shader){
		this.shader = shader;
		//atlasTexture = atlas.getTextures().first();
		indexTexture = new Texture(Gdx.files.internal("indexTexture.png"));
		//atlasTexture = new Texture(Gdx.files.internal("tilesprocessed.png"));
		this.map = map;
		buffers = new FrameBuffer[(map.width / MapRenderSystem.RENDER_SIZE) * (map.height / MapRenderSystem.RENDER_SIZE)];

	}
	
	public void beginDrawBack(LightRenderSystem lights){
	}

	public void draw(Map map, int x, int y, int[] tiles, int[] backTiles, OrthographicCamera camera, LightRenderSystem lights, BufferStartSystem buffer, boolean setAllDirty, ShaderProgram shader, int xOffset, SpriteBatch batch) {
		x -= map.offset.x/MapRenderSystem.RENDER_SIZE;
		y -= map.offset.y/MapRenderSystem.RENDER_SIZE;
		int wy =  y;//(int) (y -( map.offset.y/MapRenderSystem.RENDER_SIZE));
		int wx =  x;//(int) (x -( map.offset.x/MapRenderSystem.RENDER_SIZE));
		wx += xOffset / (MapRenderSystem.RENDER_SIZE * Main.PPM);
		int v = (OverworldSystem.SCROLLING_MAP_WIDTH * OverworldSystem.SCROLLING_MAP_TOTAL_SIZE )/ MapRenderSystem.RENDER_SIZE;
		wx = ((wx % v) + v) % v;
		if (xOffset < 0){
			
			//Gdx.app.log(TAG, "draw " + x + " wx:" + wx + " y:" + y);
		}
		if (wx < 0 || wy < 0 || wx >= map.width/MapRenderSystem.RENDER_SIZE || wy >= map.height/MapRenderSystem.RENDER_SIZE){
			//int width = (OverworldSystem.SCROLLING_MAP_WIDTH * OverworldSystem.SCROLLING_MAP_TOTAL_SIZE) / MapRenderSystem.RENDER_SIZE;
			//wx = (((wx % width) + width) % width);
			//if (wy == 0)
			////Gdx.app.log(TAG, "draw afatwe " + x + " wx:" + wx + " y:" + y);
			//if (wx < 0 || wy < 0 || wx >= map.width/MapRenderSystem.RENDER_SIZE || wy >= map.height/MapRenderSystem.RENDER_SIZE){
				
				return;
				
			//}  
		}

		int index = wy + wx*(map.width/MapRenderSystem.RENDER_SIZE);
		
		//x *= MapRenderSystem.RENDER_SIZE;
		//y *= MapRenderSystem.RENDER_SIZE;
		
		//Gdx.app.log(TAG,  "draw "+ "  index "+index + " wx "+wx+" wy"+wy + " tot"+map.width);
		
		//if (x >= 0) return;
		mat.set(camera.combined);
		mat.translate(Main.PPM*(x)*(MapRenderSystem.RENDER_SIZE) +map.offset.x*Main.PPM, Main.PPM*(y)*(MapRenderSystem.RENDER_SIZE)+map.offset.y*Main.PPM, 0);
		mat.translate(xOffset, 0, 0);
		map.renderMatrix.set(mat);
		//if (moveX) 
			//mat.translate(Main.PPM*(x)*(MapRenderSystem.RENDER_SIZE) +map.offset.x*Main.PPM, Main.PPM*(y)*(MapRenderSystem.RENDER_SIZE)+map.offset.y*Main.PPM, 0);
		//Gdx.app.log(TAG,  "draw "+ "  index "+index + " wx "+(Main.PPM*(x)*(MapRenderSystem.RENDER_SIZE) +map.offset.x*Main.PPM)/Main.PPM+" wy"+(Main.PPM*(y)*(MapRenderSystem.RENDER_SIZE)+map.offset.y*Main.PPM)/Main.PPM + " tot");
		//bshader.begin();
		//bshader.end();
			
		populateCurrentBatches(index);
		
		if (cachedTotal < CACHE_TOTAL_TARGET && (map.dirty[index] || setAllDirty)){
			//map.dirtyRuns[wx] = true;
			Gdx.app.log(TAG,  "cache chunk  "+index + setAllDirty);

			cacheChunk(map, index, tiles, backTiles, batch);
			cachedTotal++;
			map.dirty[index] = false;
		}
		
		//Gdx.gl.glDisable(GL20.GL_BLEND);
		drawnBits.set(index);
		
		//if (shader == null){
		//	currentBBatch.setColor(.3f, .3f, .3f, 1f);
		//}else currentBBatch.setColor(1f,  1f,  1f,  1f);
		//if (shader == null) return;

		batch.setProjectionMatrix(mat);

		batch.disableBlending();
		batch.setShader(shader);
		batch.begin();

		indexTexture.bind(1);
		shader.setUniformi("u_index_texture", 1); //passing first texture!!!
		atlasTexture.bind(0);
		shader.setUniformi("u_texture", 0);

		//lights.setUniforms(Light.MAP_BACK_LAYER, shader);
		batch.draw(buffers[index].getColorBufferTexture(), 0, 0);
		//batch.render();
		batch.end();
		v3.set(0, 0, 0);
		mat.getTranslation(v3);
		//Gdx.app.log(TAG, "draw" + v3);

	}
	Vector3 v3 = new Vector3();

	private void populateCurrentBatches(int index) {
		if (buffers[index] != null) return;
		buffers[index] = new FrameBuffer(Pixmap.Format.RGBA8888
				, MapRenderSystem.RENDER_SIZE * Main.PPM, MapRenderSystem.RENDER_SIZE * Main.PPM, false);
		buffers[index].getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		

		batchBits.set(index);
		
		map.dirty[index] = true;
	}

	/**
	 * @param index
	 * @return true if successful(found a batch)
	 */
	public boolean beginCache(int index) {

		return true;
	}

	public void endCache() {


	}

	public void add(Sprite s, SpriteBatch batch) {
		s.draw(batch);
	}
	public void addB(Sprite s, SpriteBatch batch) {
		s.draw(batch);
	}
	public void addFG(Sprite s, SpriteBatch batch) {
		s.draw(batch);
	}
	public void addLit(Sprite s, SpriteBatch batch){
		s.draw(batch);
	}

	private boolean cacheChunk(Map map, int index, int[] tiles, int[] backTiles, SpriteBatch batch) {
		
		Sprite s = null;
		int w = MapRenderSystem.RENDER_SIZE * Main.PPM;
		int h = MapRenderSystem.RENDER_SIZE * Main.PPM;
		buffers[index].begin();
		batch.setShader(null);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		batch.begin();
		atlasTexture.bind(0);
		batch.getShader().setUniformi("u_texture", 0);

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
						addFG(s, batch);
						int bid = backTiles[ty+y+(tx+x)*map.width];
						int tileb = bid & Map.TILE_MASK;
						if (bid != 0){
							s = findSprite(tileb);
							if (s == null) throw new GdxRuntimeException("Sprite not found: "+tileb);
							s.setPosition( Main.PPM*(tx-.5f) ,  Main.PPM*(ty-.5f));
							//Gdx.app.log(TAG, "addingB "+s.getX());
							addB(s, batch);
							
						}
					}
					else if (def.isLit)addLit(s, batch);
					else add(s, batch);
				} else {
					int bid = backTiles[ty+y+(tx+x)*map.width];
					int tile = bid & Map.TILE_MASK;
					if (bid != 0){
						s = findSprite(tile);
						if (s == null) throw new GdxRuntimeException("Sprite not found: "+tile);
						s.setPosition( Main.PPM*(tx-.5f) ,  Main.PPM*(ty-.5f));
						//Gdx.app.log(TAG, "addingB "+s.getX());
						addB(s, batch);
						
					}
				}
			}
		}		
		batch.end();
		buffers[index].end();
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
		s.setTexture(atlasTexture);
		//Gdx.app.log(TAG,  "done create  "+i + " " + s.getU() + " v "+s.getV() + " u2:"+s.getU2() + ", " + s.getV2() );
		return s;

	}
	public static void cloneSprite(int i, int src){
		if (sprites[i] != null) return;
		//Gdx.app.log(TAG,  "create"+i);
		Sprite s = atlas.createSprite(SPRITE_FILENAME_PREFIX, src);
		if (s == null) throw new GdxRuntimeException("null spr " + i + Data.getString(i));
		s.setSize(Main.PPM*2, Main.PPM*2);
		sprites[i] = s;
		//Gdx.app.log(TAG,  "done create  "+i + " " + s.getU() + " v "+s.getV() + " u2:"+s.getU2() + ", " + s.getV2() );
		//return s;
		
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
			if (buffers[index] == null) throw new GdxRuntimeException("clearing buffer error" );


			buffers[index].dispose();
			buffers[index] = null;
			batchBits.clear(index);
			bits.clear(index);
			
		}
		
		
	}

	public void beginDraw(boolean skipDraw) {
		drawnBits.clear();
		cachedTotal = 0;
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



			buffers[index].dispose();

			buffers[index] = null;
		}

	}

	

	
	
	
}