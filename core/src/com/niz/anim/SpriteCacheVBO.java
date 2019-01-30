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
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatchN;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
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
import com.niz.system.SpriteAnimationSystem;

public class SpriteCacheVBO {
	private static final String TAG = "sprite cache";
	private static final String SPRITE_FILENAME_PREFIX = "diff/tile";
	private static final int CACHE_TOTAL_TARGET = 32;
	private static final int INDEX_BUFFER_HEIGHT = 66;
	private static final int NUM_VERTS = 6;
	private static final int NUM_INDICES = 6;

	public static Sprite[] sprites = new Sprite[34*512];
	public static final Texture atlasTexture = new Texture(Gdx.files.internal("tilesprocessed.png"));
	public final ShaderProgram cacheShader;
	private final int u_index_texture;
	private final int u_texture;
	private final int[] totalVerts;
	private final short[] meshIndices;
	public int cachedTotal;
	private static TextureAtlas atlas;

//	private ShaderProgram shader;
	private Matrix4 mat = new Matrix4();
	private int[] backTiles;

	private Map map;
	private Mesh[] buffers;
	private int totalV;

	float[] verts = new float[MapRenderSystem.RENDER_SIZE * MapRenderSystem.RENDER_SIZE * NUM_VERTS * 4];

	public SpriteCacheVBO(Map map, TextureAtlas atlas, ShaderProgram shader, ShaderProgram coeffsS, ShaderProgram posShader){
//		this.shader = shader;
		//atlasTexture = atlas.getTextures().first();
		//indexTexture = new Texture(Gdx.files.internal("tilesindexTexture.png"));
		//atlasTexture = new Texture(Gdx.files.internal("tilesprocessed.png"));
		this.map = map;
		buffers = new Mesh[(map.width / MapRenderSystem.RENDER_SIZE) * (map.height / MapRenderSystem.RENDER_SIZE)];
		totalVerts = new int[(map.width / MapRenderSystem.RENDER_SIZE) * (map.height / MapRenderSystem.RENDER_SIZE)];

		cacheShader = createDefaultShader();
		//indexBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 128, INDEX_BUFFER_HEIGHT, false);
		//indexBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		u_index_texture = shader.getUniformLocation("u_index_texture");
		u_texture = shader.getUniformLocation("u_texture");
		meshIndices = new short[MapRenderSystem.RENDER_SIZE * MapRenderSystem.RENDER_SIZE * NUM_INDICES];
		for (int i = 0; i < meshIndices.length/NUM_INDICES; i++){
			int c = i * NUM_INDICES;
			int d = i * 4;
			meshIndices[c] = (short) (d + 0);
			meshIndices[c+1] = (short) (d + 1);
			meshIndices[c+2] = (short) (d + 2);
			meshIndices[c+3] = (short) (d + 2);
			meshIndices[c+4] = (short) (d + 3);
			meshIndices[c+5] = (short) (d + 0);
		}
	}
	/** Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified. */
	static public ShaderProgram createDefaultShader () {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
				+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  vec4 diff = texture2D(u_texture, v_texCoords);  \n"
				+ "  gl_FragColor = vec4(diff.rg, v_color.r, diff.a);\n" //
				+ "}";

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}
	
	public void beginDrawBack(LightRenderSystem lights){
	}

	public void beginDraw(boolean skipDraw, SpriteBatchN batch, LightRenderSystem lights) {
		drawnBits.clear();
		cachedTotal = 0;
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/*batch.getProjectionMatrix().setToOrtho2D(0, 0, indexBuffer.getWidth(), indexBuffer.getHeight());
		indexBuffer.begin();
		batch.setShader(null);
		batch.begin();
		batch.draw(indexTexture, 0, 0);
		batch.end();
		batch.setShader(coefficientsShader);
		lights.setUniformsNew(coefficientsShader, shader, positionShader);
		batch.begin();
		//any texture
		batch.draw(indexTexture, 0, 2, indexBuffer.getWidth(), 1);
		batch.end();

		batch.setShader(positionShader);
		//lights.setUniformsNew(coefficientsShader, shader, positionShader);
		batch.begin();
		//any texture
		batch.draw(indexTexture, 0, 3, indexBuffer.getWidth(), 1);
		batch.end();
		//batch.setShader(fxSshader);
		//batch.begin();
		//any texture
		//batch.draw(indexTexture, 0, 3, indexBuffer.getWidth(), indexBuffer.getHeight()-3);
		//batch.end();
		indexBuffer.end();*/
	}

	public void draw(Map map, int x, int y, int[] tiles, int[] backTiles, OrthographicCamera camera, LightRenderSystem lights, BufferStartSystem buffer, boolean setAllDirty, ShaderProgram shader, int xOffset, SpriteBatchN batch, FrameBuffer indexBuffer, Texture atlasTexture) {
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
				return;
				
			//}  
		}
		//setAllDirty = true;
		int index = wy + wx*(map.width/MapRenderSystem.RENDER_SIZE);
		mat.set(camera.combined);
		mat.translate(Main.PPM*(x)*(MapRenderSystem.RENDER_SIZE) +map.offset.x*Main.PPM, Main.PPM*(y)*(MapRenderSystem.RENDER_SIZE)+map.offset.y*Main.PPM, 0);
		mat.translate(xOffset, 0, 0);
		map.renderMatrix.set(mat);

		populateCurrentBatches(index);
		

		
		//Gdx.gl.glDisable(GL20.GL_BLEND);
		drawnBits.set(index);

		batch.setProjectionMatrix(mat);
		//batch.disableBlending();
		//batch.enableBlending();
		//batch.setShader(shader);
		//batch.setShader(null);
//		batch.begin();
		//lights.setUniforms(Light.MAP_FRONT_LAYER, shader);
		//indexTexture.bind(1);

		//lights.setUniforms(Light.MAP_BACK_LAYER, shader);
		//Texture tex = buffers[index].getColorBufferTexture();
		shader.begin();
		shader.setUniformMatrix("u_projTrans", mat);
		indexBuffer.getColorBufferTexture().bind(1);
		shader.setUniformi(u_index_texture, 1); //passing first texture!!!
		SpriteCacheVBO.atlasTexture.bind(0);
		shader.setUniformi(u_texture, 0);


		buffers[index].render(shader, GL20.GL_TRIANGLES, 0, totalVerts[index] * NUM_INDICES);
		shader.end();

//		batch.draw(tex, 0, tex.getHeight(), tex.getWidth(), -tex.getHeight());

		//batch.render();
//		batch.end();
		//v3.set(0, 0, 0);
		//mat.getTranslation(v3);

		//Gdx.app.log(TAG, "draw " + x + "," + y);

	}

	public void cache(Map map, int x, int y, int[] tiles, int[] backTiles, OrthographicCamera camera, LightRenderSystem lights, BufferStartSystem buffer, boolean setAllDirty, ShaderProgram shader, int xOffset, SpriteBatchN batch, FrameBuffer indexBuffer, Texture atlasTexture) {
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
			return;
		}
		int index = wy + wx*(map.width/MapRenderSystem.RENDER_SIZE);

		mat.set(camera.combined);
		mat.translate(Main.PPM*(x)*(MapRenderSystem.RENDER_SIZE) +map.offset.x*Main.PPM, Main.PPM*(y)*(MapRenderSystem.RENDER_SIZE)+map.offset.y*Main.PPM, 0);
		mat.translate(xOffset, 0, 0);
		map.renderMatrix.set(mat);

		populateCurrentBatches(index);

		if (cachedTotal < CACHE_TOTAL_TARGET && (map.dirty[index] || setAllDirty)){
			//map.dirtyRuns[wx] = true;
			//Gdx.app.log(TAG,  "cache chunk  "+index + setAllDirty);

			cacheChunk(map, index, tiles, backTiles, batch);
			cachedTotal++;
			map.dirty[index] = false;
		}

		//Gdx.gl.glDisable(GL20.GL_BLEND);


	}
	Vector3 v3 = new Vector3();
	Matrix4 zeroMatrix = new Matrix4();
	private void populateCurrentBatches(int index) {
		if (buffers[index] != null) return;
		//buffers[index] = new FrameBuffer(RGBA8888
		//		, MapRenderSystem.RENDER_SIZE * Main.PPM, MapRenderSystem.RENDER_SIZE * Main.PPM, false);
		buffers[index] = new Mesh(false
				, MapRenderSystem.RENDER_SIZE * MapRenderSystem.RENDER_SIZE * NUM_VERTS * 4
				, MapRenderSystem.RENDER_SIZE * MapRenderSystem.RENDER_SIZE * NUM_INDICES

				, VertexAttribute.Position()
				, VertexAttribute.ColorPacked()
				, VertexAttribute.TexCoords(0)
		);
		buffers[index].setIndices(meshIndices);
		//buffers[index].getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		

		batchBits.set(index);
		
		map.dirty[index] = true;
	}

	/**
	 * @param index
	 * @return true if successful(found a batch)
	 */
	public boolean beginCache(int index) {
		totalV = 0;
		totalVerts[index] = 0;
		return true;
	}

	public void endCache(int index) {
		totalVerts[index] = totalV;
		buffers[index].setVertices(verts, 0, totalV * NUM_VERTS);
	}

	private void addSprite(Sprite s){
		int t = totalV++ * NUM_VERTS;
		int c = 0;
		float[] v = s.getVertices();

		verts[t + 0 ] = v[c + 0];
		verts[t + 1 ] = v[c + 1];
		verts[t + 2 ] = 0f;
		verts[t + 3 ] = v[c + 2];
		verts[t + 4 ] = v[c + 3];
		verts[t + 5 ] = v[c + 4];
		c = 1 * 5;
		t = totalV++ * NUM_VERTS;
		verts[t + 0 ] = v[c + 0];
		verts[t + 1 ] = v[c + 1];
		verts[t + 2 ] = 0f;
		verts[t + 3 ] = v[c + 2];
		verts[t + 4 ] = v[c + 3];
		verts[t + 5 ] = v[c + 4];
		c = 2 * 5;
		t = totalV++ * NUM_VERTS;
		verts[t + 0 ] = v[c + 0];
		verts[t + 1 ] = v[c + 1];
		verts[t + 2 ] = 0f;
		verts[t + 3 ] = v[c + 2];
		verts[t + 4 ] = v[c + 3];
		verts[t + 5 ] = v[c + 4];
		c = 3 * 5;
		t = totalV++ * NUM_VERTS;
		//Gdx.app.log(TAG, " " + c + "  " + t);
		verts[t + 0 ] = v[c + 0];
		verts[t + 1 ] = v[c + 1];
		verts[t + 2 ] = 0f;
		verts[t + 3 ] = v[c + 2];
		verts[t + 4 ] = v[c + 3];
		verts[t + 5 ] = v[c + 4];





	}

	public void add(Sprite s, SpriteBatchN batch) {
		s.setColor(SpriteAnimationSystem.LAYER_COLORS[Light.MAP_FRONT_LAYER]);
		addSprite(s);//s.draw(batch);

	}
	public void addB(Sprite s, SpriteBatchN batch) {

		//Gdx.app.log(TAG, "BACK");
		s.setColor(SpriteAnimationSystem.LAYER_COLORS[Light.MAP_BACK_LAYER]);
		addSprite(s);//s.draw(batch);
		//batch.setColor(Color.WHITE);
	}
	public void addFG(Sprite s, SpriteBatchN batch) {

		s.setColor(SpriteAnimationSystem.LAYER_COLORS[Light.MAP_FOREGROUND_LAYER]);
		addSprite(s);//s.draw(batch);
	}
	public void addLit(Sprite s, SpriteBatchN batch){

		s.setColor(SpriteAnimationSystem.LAYER_COLORS[Light.MAP_LIT_LAYER]);
		addSprite(s);//s.draw(batch);
	}

	private boolean cacheChunk(Map map, int index, int[] tiles, int[] backTiles, SpriteBatchN batch) {
		
		Sprite s = null;
		int w = MapRenderSystem.RENDER_SIZE * Main.PPM;
		int h = MapRenderSystem.RENDER_SIZE * Main.PPM;
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//batch.setShader(cacheShader);
		//batch.enableBlending();
		//batch.setShader(cacheShader);
		//batch.setColor(Color.BLACK);
		//batch.setColor(Color.WHITE);
		//batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		//batch.begin();

		int x = index / (map.width / MapRenderSystem.RENDER_SIZE);
		int y = index % (map.height / MapRenderSystem.RENDER_SIZE);//TODO width?
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
					//Gdx.app.log(TAG,  "update  "+((id&Map.ID_MASK) >> Map.ID_BITS));
					s = findSprite(tile);

					if (s == null) throw new GdxRuntimeException("Sprite not found: "+tile);
					//s.setPosition(Main.PPM*tx ,  Main.PPM*ty);
					s.setPosition( Main.PPM*(tx-.5f) ,  Main.PPM*(ty-.5f));
					if (def.isSeeThrough) {
						int bid = backTiles[ty+y+(tx+x)*map.width];
						int tileb = bid & Map.TILE_MASK;
						if (bid != 0){
							Sprite s2 = findSprite(tileb);
							if (s2 == null) throw new GdxRuntimeException("Sprite not found: "+tileb);
							s2.setPosition( Main.PPM*(tx-.5f) ,  Main.PPM*(ty-.5f));
							addB(s2, batch);
						}
						addFG(s, batch);
							//Gdx.app.log(TAG, "addingB "+s.getX());
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
						//Gdx.app.log(TAG, "addingB "+tile + "  " + tx);
						addB(s, batch);
						
					}
				}
			}
		}		
		//batch.end();

		endCache(index);
		//batch.setColor(Color.WHITE);
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
		//s.setColor(Color.BLACK);
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
			if (buffers[index] == null) throw new GdxRuntimeException("clearing buffer error" );


			buffers[index].dispose();
			buffers[index] = null;
			batchBits.clear(index);
			bits.clear(index);
			
		}

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