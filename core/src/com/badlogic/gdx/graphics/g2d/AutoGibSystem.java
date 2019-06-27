package com.badlogic.gdx.graphics.g2d;

import java.util.Random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.niz.BlockDefinition;
import com.niz.Blocks;
import com.niz.Data;
import com.niz.Main;
import com.niz.action.Action;
import com.niz.action.ActionList;
import com.niz.actions.AItemFall;
import com.niz.anim.AnimationContainer;
import com.niz.anim.AnimationLayer;
import com.niz.component.AutoGib;
import com.niz.component.Body;
import com.niz.component.CollidesWithMap;
import com.niz.component.Inventory;
import com.niz.component.Light;
import com.niz.component.OnMap;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.component.SpriteAnimation;
import com.niz.component.SpriteIsMapTexture;
import com.niz.component.SpriteStatic;
import com.niz.item.Item;
import com.niz.system.LightRenderSystem;
import com.niz.system.LightUpdateSystem;
import com.niz.system.MapRenderSystem;
import com.niz.system.MapSystem;

public class AutoGibSystem extends EntitySystem {
	private static final String TAG = "Auto gib system";
    private final SpriteBatchN batch;
    private final SpriteBatchN leftBatch;
    private Pool<Sprite> spritePool;
	public ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	public ComponentMapper<SpriteAnimation> spriteM = ComponentMapper.getFor(SpriteAnimation.class);
	public ComponentMapper<SpriteStatic> spriteStaticM = ComponentMapper.getFor(SpriteStatic.class);
	protected ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
	
	public AutoGibSystem(SpriteBatchN batch, SpriteBatchN leftBatch, LightUpdateSystem lights) {

		makeTables();
		this.batch = batch;
		this.leftBatch = leftBatch;
		
		spritePool = Pools.get(Sprite.class);
	}



	public IntArray[][] decompositions;//[size][variant]
	public boolean[][] mask = new boolean[32][32];
	private EngineNiz engine;
	private ImmutableArray<Entity> entities;
	private ImmutableArray<Entity> physicsEntities;
	private MapRenderSystem map;
	public IntArray[][] decompositions4;
	public void makeTables() {
		decompositions4 = new IntArray[24][];
		decompositions = new IntArray[24][];
		for (int i = 0; i < decompositions.length; i++){
			int variants = 16;
			decompositions[i] = new IntArray[variants];
			for (int k = 0; k < variants; k++){
				decompositions[i][k] = new IntArray();
				makeDecomposition(i, k, decompositions[i][k]);
			}
			decompositions4[i] = new IntArray[variants];
			for (int k = 0; k < variants; k++){
				decompositions4[i][k] = new IntArray();
				make4PieceDecomposition(i, k, decompositions4[i][k]);
			}
		}
	}
	
	private void make4PieceDecomposition(int size, int variant, IntArray arr){

		Random r = Pools.obtain(Random.class);
		r.setSeed(12344567+variant+size*654);
		size = Math.max(2, size);
		int w = r.nextInt(size/2+1)+size/4;
		int h = r.nextInt(size/2+1) + size/4;
		
		arr.add(0);
		arr.add(0);
		arr.add(w);
		arr.add(h);
		
		arr.add(0);
		arr.add(h);
		arr.add(w);
		arr.add(size - h);
		
		arr.add(w);
		arr.add(0);
		arr.add(size - w);
		arr.add(h);
		
		arr.add(w);
		arr.add(h);
		arr.add(size - w);
		arr.add(size - h);
		
		
		Pools.free(r);

	}
	
	
	private void makeDecomposition(int size, int variant, IntArray arr) {
		resetMask();
		boolean done = false;
		Random r = Pools.obtain(Random.class);
		r.setSeed(12344567+variant+size*4323);
		//done = true;
		for (int i = size/2; i >=0; i--){
			
			int count = 0;
			while (!done){
				//Gdx.app.log(TAG, "update"+count);
				count++;
				if (count > 100) done = true;
				int s = i*2/5;
				s = Math.max(1,  s);
				int gibSizeX = r.nextInt(s)+s, gibSizeY = r.nextInt(s)+s;;
				if (gibSizeX >= size || gibSizeY >= size) continue;
				int x = r.nextInt(size - gibSizeX-1 + 1);
				int y = r.nextInt( size - gibSizeY-1 + 1);
				//int x = MathUtils.random(0, size - gibSizeX-1);
				//int y = MathUtils.random(0, size - gibSizeY-1);
				boolean clear = true;
				for (int ix = 0; ix < gibSizeX; ix++)
					for (int iy = 0; iy < gibSizeY; iy++){
						if (mask[ix+x][iy+y]) clear = false;
					}
				if (clear){
					//Gdx.app.log(TAG, "clear"+count + " w"+gibSizeX);
					for (int ix = 0; ix < gibSizeX; ix++)
						for (int iy = 0; iy < gibSizeY; iy++){
							mask[ix+x][iy+y] = true;
							
						}
					arr.add(x);
					arr.add(y);
					arr.add(gibSizeX);
					arr.add(gibSizeY);
				}
				//Gdx.app.log(TAG, "check"+count);
				
				
			}
		}
		
		//fill in size 1s
		for (int x = 0; x < size; x++){
			for (int y = 0; y < size; y++){
				if (!mask[x][y]){
					arr.add(x);
					arr.add(y);
					arr.add(1);
					arr.add(1);
				}
			}
		}
		
		Pools.free(r);
		
	}

	private void resetMask(){
		for (int x = 0; x < mask.length; x++){
			for (int y = 0; y < mask[x].length; y++){
				mask[x][y] = false;
			}
		}
	}

	@Override
	public void update(float deltaTime) {
		processSprites();
        /*for (int i = 0; i < entities.size(); i++){
            Entity e = entities.get(i);
            SpriteAnimation spr = spriteM.get(e);

        }*/
	}
    Vector2 v3 = new Vector2();
    private void processSprites() {
        int layerI = Light.CHARACTER_SPRITES_LAYER_RIGHT;

        for (int i = 0; i < physicsEntities.size(); i++){
            Entity e  = physicsEntities.get(i);
            SpriteAnimation spr = spriteM.get(e);
            if (!spr.hasStarted) continue;
            Position pos = posM.get(e);
            AnimationContainer parent =  spr.currentAnim;
            //Gdx.app.log(TAG, "process phy "+e.getId());
            //Gdx.app.log(TAG, "process phy "+pos.pos.y * Main.PPM);

            for (int index = 0; index < parent.layers.size; index++)
            {
                AnimationContainer container = parent;
                AnimationLayer layer;// = parent.layers[index];
                if (spr.overriddenAnim.get(index)){
                    container = spr.overriddenAnimationLayers[index];
                    layer = container.layers.get(0);
                }
                else layer = parent.layers.get(index);
                if (layer == null) {continue;}
                AtlasSprite s;
                int frame = spr.frameIndices[index];
                boolean left = spr.adjustedLeft[index];
                s = (AtlasSprite) layer.getKeyFrame(frame, left);
                Vector2 p = layer.offsets[frame];

                int guideLayer = spr.layerSources[index];
                //guideLayer = 0;
                Vector2 g = spr.guides.get(guideLayer);
                v3.set(g.x, g.y);
                v3.add(0+(left?p.x-s.getAtlasRegion().originalWidth/(float) Main.PPM:-p.x), -p.y).add(pos.pos);
                v3.scl(16f);
                s.setPosition((int)v3.x, (int)v3.y);
                s.setColor(Data.colors[spr.colors[index]]);

                SpriteBatchN theBatch = left?leftBatch:batch;

                draw(s, theBatch, left, e);//, layerI);
            }
        }
    }

    public void draw(Sprite s, SpriteBatchN theBatch, boolean left, Entity entity) {
		Vector2 basePos = posM.get(entity).pos;
		Body eBody = bodyM.get(entity);
		makeGibs(s, left, basePos, false, null, Blocks.STONE, 0);
		entity.remove(AutoGib.class);
	}
	
	public void makeGibs(Sprite s, boolean left, Vector2 basePos, boolean isMapTexture, Class<? extends Action> actionClass, int b, int seed){
		//decompose here

		//Gdx.app.log("agibsys", "sprite "+s.u + " , "+s.v);
		AtlasSprite as = (AtlasSprite) s;
		BlockDefinition def = MapSystem.getDef(b);
		
		float pix = 1f/2048f;
		int size = Math.max(((AtlasSprite)(s)).getAtlasRegion().packedHeight, ((AtlasSprite)(s)).getAtlasRegion().packedWidth);
		IntArray arr;
		if (def.breaksWithBigPieces){
			arr = decompositions4[size][seed % decompositions[size].length];
		} else 
			arr = decompositions[size][seed % decompositions[size].length];
		for (int i = 0; i < arr.size; i+=4){
			Sprite spr = spritePool.obtain();
			spr.setRegion(as.getAtlasRegion());
			AtlasRegion reg = as.getAtlasRegion();
			
			//reg.setRegion(s.getAtlasRegion());
			int x = arr.get(i);
			int y = arr.get(i+1);
			int w = arr.get(i+2);
			int h = arr.get(i+3);
			/*reg.originalWidth = s.getAtlasRegion().originalWidth;
			reg.originalHeight = s.getAtlasRegion().originalHeight;
			reg.offsetX = s.getAtlasRegion().offsetX;
			reg.offsetY = s.getAtlasRegion().offsetY;
			reg.packedWidth = w;//s.getAtlasRegion().packedWidth;
			reg.packedHeight = h;//s.getAtlasRegion().packedHeight;*/
			
			if (x > reg.packedWidth)continue;//outside of x range
			if (x + w >= reg.packedWidth){//too wide
				w = reg.packedWidth - x;
			}
			if (y > reg.packedHeight)continue;//outside of y range
			if (y + h >= reg.packedHeight){//too high
				h = reg.packedHeight - y;
			}//*/
			//reg.offsetX += x;
			if (left){
				spr.setU(s.getU()-x*pix);
				spr.setV2(s.getV()+as.getAtlasRegion().packedHeight*pix-y*pix);
				spr.setU2(s.getU()-x*pix-w*pix);
				spr.setV(s.getV()+as.getAtlasRegion().packedHeight*pix-y*pix-h*pix);	//*/
				}	//*/
			else {
				spr.setU(s.getU()+x*pix);
				spr.setV2(s.getV()+as.getAtlasRegion().packedHeight*pix-y*pix);
				spr.setU2(s.getU()+x*pix+w*pix);
				spr.setV(s.getV()+as.getAtlasRegion().packedHeight*pix-y*pix-h*pix);	//*/
				
			}
			spr.setSize(w,  h);
			
			//Gdx.app.log(TAG,  "  su " + spr.getU()/pix + " sv "+ spr.getV()/pix + "  su2 " + spr.getU2()/pix + " sv2 "+ spr.getV2()/pix + " w "+w+" h "+h + " x "+x + "  y "+y
			//		+ "  i "+i);
			//Gdx.app.log(TAG,  "  su " + spr.getU() + " sv "+ spr.getV() + "  su2 " + spr.getU2() + " sv2 "+ spr.getV2() + " w "+w+" h "+h + " x "+x + "  y "+y
			//		+ "  i "+i);
		
			//setRegion(spr, reg);
			
			Entity e = engine.createEntity();
			Body body = engine.createComponent(Body.class);
			Position pos = engine.createComponent(Position.class);
			Physics physics = engine.createComponent(Physics.class);
			physics.vel.set(MathUtils.random(-3f, 3f), MathUtils.random(5f, 15f));
			body.width = w/32f;
			body.height = h/32f;
			pos.pos.set(basePos)
			.add(w/32f, h/32f)
			.add(as.getAtlasRegion().offsetX/16f, as.getAtlasRegion().offsetY/16f)
			//.sub(2.5f-0f/16f, 2f+5f/16f)
			//.sub(eBody.width, eBody.height);
			;
			if (left){
				//pos.pos.add(as.getAtlasRegion().packedWidth/16f - x/16f-w/16f, y/16f);
				Gdx.app.log(TAG, "LEFT");
				spr.setFlip(true, false);
				pos.pos.add(x/16f, y/16f);
			} else {
				
				pos.pos.add(x/16f, y/16f);
			}
			
			ActionList actionList = engine.createComponent(ActionList.class);
			if (actionClass != null){
				actionList.addToStart(Pools.obtain(actionClass));
				
			}
			e.add(pos);
			e.add(physics);
			e.add(body);
			e.add(actionList);
			physics.limit.set(10,10, 10);
			//physics.gravity.set(0, -30);
			SpriteStatic sprite = engine.createComponent(SpriteStatic.class);
			sprite.s =  spr;
			if (isMapTexture){
				e.add(engine.createComponent(SpriteIsMapTexture.class));
			}
			
			e.add(sprite);
			CollidesWithMap coll = engine.createComponent(CollidesWithMap.class);
			e.add(coll);
			OnMap onMap = engine.createComponent(OnMap.class);
			e.add(onMap);
			
			
			Item item = Pools.obtain(Item.class);
			item.set(def.particleItemID, w * h, 1f);
			//body.width += Main.PX * 4;
			//body.height += Main.PX * 4;
			//item.id = ;
			//Gdx.app.log(TAG, "item "+item.id);
			
			//item.count = ;
			item.hash = Inventory.nextHash();
			e.add(item);
			
			
			/*BlockPart part = engine.createComponent(BlockPart.class);
			part.id = (b & Map.ID_MASK) >> Map.ID_BITS;
			part.count = w * h;
			e.add(part);*/
			
						
			
			engine.addEntity(e);;
			//break;
		}
		
	}

	private void setRegion(AtlasSprite spr, AtlasRegion reg) {
		spr.originalOffsetX = spr.region.offsetX;
		spr.originalOffsetY = spr.region.offsetY;
		spr.setRegion(reg);
		spr.setOrigin(spr.region.originalWidth / 2f, spr.region.originalHeight / 2f);
		int width = spr.region.getRegionWidth();
		int height = spr.region.getRegionHeight();
		if (spr.region.rotate) {
			spr.rotate90(true);
			spr.setBounds(spr.region.offsetX, spr.region.offsetY, height, width);
		} else
			spr.setBounds(spr.region.offsetX, spr.region.offsetY, width, height);
		spr.setColor(1, 1, 1, 1);
		
	}

	@Override
	public void addedToEngine(Engine engine) {
		Family fam = Family.all(Position.class, SpriteAnimation.class, AutoGib.class).exclude(Body.class).get();
		entities = engine.getEntitiesFor(fam);
		Family physFam = Family.all(Position.class, SpriteAnimation.class, Body.class, AutoGib.class).get();
		
		physicsEntities = engine.getEntitiesFor(physFam);
		
		//physicsEntities = engine.getEntitiesFor(Family.getFor(Position.class, SpriteAnimation.class, Physics.class));
		map = engine.getSystem(MapRenderSystem.class);
		//Family family = new Family();
		this.engine = (EngineNiz) engine;
		//Family.all;
}
}
