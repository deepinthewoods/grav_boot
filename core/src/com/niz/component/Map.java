package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.BlockDefinition;
import com.niz.anim.SpriteCacheNiz;
import com.niz.system.MapRenderSystem;
import com.niz.system.MapSystem;
import com.niz.system.OverworldSystem;

public class Map implements Component, Poolable {
	public transient SpriteCacheNiz cache;// = new SpriteCacheNiz(atlas, shader, backShader);
	public static final int ID_MASK = 0x3e00, VARIANT_MASK = 0x01ff,  DATA_MASK = 0xffffc000
			,ID_BITS = 9, VARIANT_BITS = 0, DATA_BITS = 14, TILE_MASK = 0x3FFF;
	private static final String TAG = "Map";;
	public int[] tiles;
	public int[] backTiles, lightTiles;
	public boolean[] dirty;
	public boolean[] physicsDirty;
	public int width, height;
	public Vector2 offset = new Vector2();
	public transient MapSystem mapSystem;
	public boolean isScrolling;
	//public PooledEntity e;
	//public IntArray[] backgroundRuns;
	//public boolean[] dirtyRuns;
	public BlockDefinition[] defs;
	public Entity mapEntity;
	public boolean free = true;
	public Bits dirtyPath = new Bits(OverworldSystem.SCROLLING_MAP_WIDTH * OverworldSystem.SCROLLING_MAP_HEIGHT), dirtyDestroyed = new Bits(OverworldSystem.SCROLLING_MAP_WIDTH * OverworldSystem.SCROLLING_MAP_HEIGHT);
	public boolean duplicateRenderL;
	public boolean duplicateRenderR;
	public Matrix4 renderMatrix = new Matrix4();;
	public Map(int width, int height, TextureAtlas atlas, ShaderProgram shader, ShaderProgram coeffsShader, ShaderProgram posShader){
		
		this.width = width;
		this.height = height;
		/*backgroundRuns = new IntArray[width];
		for (int i = 0; i < backgroundRuns.length; i++){
			backgroundRuns[i] = new IntArray();
		}
		dirtyRuns = new boolean[width/MapRenderSystem.RENDER_SIZE];*/
		tiles = new int[width*height];
		backTiles = new int[width*height];
		/*for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++){
				//backTiles[i+j*width] = 65;
				if (j == 0 //|| i % 12 == 0 || 
						//|| i % 16 == 0
						 //|| (j == 16 && i%4==1)
						)
					tiles[j+i*width] = Blocks.STONE;
			}*/
		dirty = new boolean[(width/MapRenderSystem.RENDER_SIZE)*(height/MapRenderSystem.RENDER_SIZE)];
		for (int i = 0; i < dirty.length; i++){
			dirty[i] = true;
			
		}
		
		physicsDirty = new boolean[height];
		for (int i = 0; i < physicsDirty.length; i++){
			physicsDirty[i] = true;
		}
		if (atlas != null)
			cache = new SpriteCacheNiz(this, atlas, shader, coeffsShader, posShader);
	}
	
	
	public int get(int x, int y){
		//x = (x % width + width) % width;
		//y = (y % height + height) % height;
		x -= offset.x;
		y -= offset.y;
		if (x < 0 || y < 0 || x >= width || y >= height) return 0;
		return tiles[y+x*width];
	}
	
	private void setSibling(int x, int y, int val) {
		if (mapSystem == null) throw new GdxRuntimeException("x " + x + "," + y + "  " + val + offset);
		
		Map map = mapSystem.getMapFor(x, y);
		if (map != null && map != this)  map.set(x,  y, val);
		
	}
	
	public void setLocal(int x, int y, int val) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			setSibling(x+(int)offset.x,  y+(int)offset.y, val);
			return;
		}
		//x = (x % width + width) % width;
		//y = (y % height + height) % height;
		tiles[y+x*width] = val;
		
		int wx = x / MapRenderSystem.RENDER_SIZE;
		int wy = y / MapRenderSystem.RENDER_SIZE;
		
		if (wx < 0) wx += (width / MapRenderSystem.RENDER_SIZE );
		if (wy < 0) wy += (height / MapRenderSystem.RENDER_SIZE);

		int index = wy + wx*(width / MapRenderSystem.RENDER_SIZE);
		dirty[index] = true;
	}
	
	public void set(int x, int y, int val){
		x -= offset.x;
		y -= offset.y;
		if (x < 0 || y < 0 || x >= width || y >= height) {
			setSibling(x+(int)offset.x,  y+(int)offset.y, val);
			return;
		}
		//x = (x % width + width) % width;
		//y = (y % height + height) % height;
		tiles[y+x*width] = val;
		
		int wx = x / MapRenderSystem.RENDER_SIZE;
		int wy = y / MapRenderSystem.RENDER_SIZE;
		
		if (wx < 0) wx += (width / MapRenderSystem.RENDER_SIZE );
		if (wy < 0) wy += (height / MapRenderSystem.RENDER_SIZE);

		int index = wy + wx*(width / MapRenderSystem.RENDER_SIZE);
		dirty[index] = true;
		//dirtyRuns[x / MapRenderSystem.RENDER_SIZE] = true;
		
		//Gdx.app.log(TAG, "dirty index "+index+" x "+x);ww
	}
	
	public void setBG(int x, int y, int val){
		x -= offset.x;
		y -= offset.y;
		if (x < 0 || y < 0 || x >= width || y >= height) {
			setSibling(x+(int)offset.x,  y+(int)offset.y, val);
			return;
		}
		//x = (x % width + width) % width;
		//y = (y % height + height) % height;
		backTiles[y+x*width] = val;
		
		int wx = x / MapRenderSystem.RENDER_SIZE;
		int wy = y / MapRenderSystem.RENDER_SIZE;
		
		if (wx < 0) wx += (width / MapRenderSystem.RENDER_SIZE );
		if (wy < 0) wy += (height / MapRenderSystem.RENDER_SIZE);

		int index = wy + wx*(width / MapRenderSystem.RENDER_SIZE);
		dirty[index] = true;
		//dirtyRuns[x / MapRenderSystem.RENDER_SIZE] = true;
		
		//Gdx.app.log(TAG, "dirty index "+index+" x "+x);ww
	}
	
	public void setBGLocal(int x, int y, int val){
		if (x < 0 || y < 0 || x >= width || y >= height) {
			setSibling(x+(int)offset.x,  y+(int)offset.y, val);
			return;
		}
		backTiles[y+x*width] = val;
		
		int wx = x / MapRenderSystem.RENDER_SIZE;
		int wy = y / MapRenderSystem.RENDER_SIZE;
		
		if (wx < 0) wx += (width / MapRenderSystem.RENDER_SIZE );
		if (wy < 0) wy += (height / MapRenderSystem.RENDER_SIZE);

		int index = wy + wx*(width / MapRenderSystem.RENDER_SIZE);
		dirty[index] = true;
		
	}
	
	public boolean isSolid(int b) {
		b &= ID_MASK;
		b >>= ID_BITS;
		if (b != 0)
			return true;
		return false;
	}
	public boolean isSeeThrough(int b) {
		b &= ID_MASK;
		b >>= ID_BITS;
		BlockDefinition def = defs[b];
		return (def.isSeeThrough);
	}

	public void setBit(int x, int y, int bit) {
		x -= offset.x;
		y -= offset.y;
		if (x < 0 || y < 0 || x >= width || y >= height) return;
		//x = (x % width + width) % width;
		//y = (y % height + height) % height;
		int val = tiles[y+x*width];
		val |= 1<<bit;

		tiles[y+x*width] = val;
		int wx = x / MapRenderSystem.RENDER_SIZE;
		int wy = y / MapRenderSystem.RENDER_SIZE;
		
		if (wx < 0) wx += (width / MapRenderSystem.RENDER_SIZE );
		if (wy < 0) wy += (height / MapRenderSystem.RENDER_SIZE);
		
		int index = wy + wx*(width / MapRenderSystem.RENDER_SIZE);
		dirty[index] = true;
	}
	public void setBackgroundBit(int x, int y, int bit) {
		x -= offset.x;
		y -= offset.y;
		if (x < 0 || y < 0 || x >= width || y >= height) return;
		//x = (x % width + width) % width;
		//y = (y % height + height) % height;
		int val = backTiles[y+x*width];
		val |= 1<<bit;
		if (backTiles[y+x*width] != val){
			backTiles[y+x*width] = val;
			
			int wx = x / MapRenderSystem.RENDER_SIZE;
			int wy = y / MapRenderSystem.RENDER_SIZE;
			
			if (wx < 0) wx += (width / MapRenderSystem.RENDER_SIZE );
			if (wy < 0) wy += (height / MapRenderSystem.RENDER_SIZE);
			//Gdx.app.log(TAG, "dirty index "+(x/width+(y/width)*(width>>MapRenderSystem.BITS)));
			
			int index = wy + wx*(width / MapRenderSystem.RENDER_SIZE);
			dirty[index] = true;
		}
		//dirtyRuns[wx / MapRenderSystem.RENDER_SIZE] = true;
	}
	public int getBackground(int x, int y) {
		x -= offset.x;
		y -= offset.y;
		if (x < 0 || y < 0 || x >= width || y >= height) return 0;
		//x = (x % width + width) % width;
		//y = (y % height + height) % height;
		return backTiles[y+x*width];
	}
	public void setBackgroundBitsSquare(int x0, int y0, int x1, int y1, int bit) {
		
	
			for (int x = x0; x <= x1; x++)
				for (int y = y0; y <= y1; y++){
					setBackgroundBit(x,y,bit);
				}
			
		
		
		
	}

	/*public void makeRuns(int p, OverworldSystem overworld) {
		dirtyRuns[p] = false;
		//Gdx.app.log(TAG, "runs " + (p*MapRenderSystem.RENDER_SIZE+offset.x) + " to " + (p*MapRenderSystem.RENDER_SIZE+offset.x + MapRenderSystem.RENDER_SIZE));
		for (int dx = 0; dx < MapRenderSystem.RENDER_SIZE; dx++){
			int x = dx + p*MapRenderSystem.RENDER_SIZE;
			int wx = (int) (x + offset.x);
			IntArray arr = backgroundRuns[x];
			arr.clear();
			int h = (int) (height + offset.y);
			int y = (int) overworld.getHeight(wx);
			int run = 1, val = get(x, y), valr = get(x+1, y);
			y++;
			if (isSeeThrough(val) || isSeeThrough(valr)) val = 1; else val = 0;
			boolean didSomething = false;
			
			for (; y <= h; y++){
				didSomething = true;
				int cval = get(x, y);
				int cvalr = get(x+1, y);
				if (isSeeThrough(cval) || isSeeThrough(cvalr)) cval = 1; else cval = 0;
				if (cval == val)
					run++;
				else {
					
					//if (val == 1 && run < 7)Gdx.app.log(TAG, "runx "+x + ","+y+"  "+run + " v:"+val);
					arr.add(run);
					arr.add(val);
					run = 1;
					val = cval;
				}
			}
			if (didSomething){
				//if (val == 1 && run < 7) Gdx.app.log(TAG, "drunx "+x + ","+y+"  "+run + " v:"+val);
				arr.add(run);
				arr.add(val);
			}
			//Gdx.app.log(TAG, "end run "+x + ","+y+"  "+run);
		}
		
	}*/
	
	@Override
	public void reset(){
//		if (cache != null){
//			cache.dispose();
//			cache = null;
//		}
		//Gdx.app.log(TAG, "RESET");
		for (int i = 0; i < OverworldSystem.SCROLLING_MAP_WIDTH * OverworldSystem.SCROLLING_MAP_HEIGHT; i++){
			dirtyPath.set(i);;
		}
	}


	


	

	
}
