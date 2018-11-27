package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.niz.BlockDefinition;
import com.niz.blocks.LavaBlockDef;
import com.niz.blocks.LiquidSlopeBlockDef;
import com.niz.blocks.SlopeBlockDefinition;
import com.niz.blocks.WaterBlockDef;
import com.niz.component.Map;

public class MapSystem extends EntitySystem implements EntityListener {
	private static final String TAG = "map system";

	
	public MapRenderSystem mapRenderSys;
	public OrthographicCamera mapCamera;
	
	public static BlockDefinition[] defs;

	private ImmutableArray<Entity> entities;

	private ComponentMapper<Map> mapM;
	private Family family;

	OverworldSystem overworld;
	//public WorldDefinition worldDef = new WorldDefinition();
	
	public MapSystem(){
		defs = new BlockDefinition[16];
		makeBlockDefs(defs);
	}
	
	
	
	private void makeBlockDefs(BlockDefinition[] defs) {
		defs[0] = new BlockDefinition(){
			
			
			@Override
			public void randomUpdate(Map map, int x, int y, int b) {
			}
			
			
			
		};
		defs[0].isSeeThrough = true;
		defs[0].isSolid = false;
		
		defs[1] = new BlockDefinition(){

			@Override
			public void randomUpdate(Map map, int x, int y, int b) {
			}
			
		};
		defs[2] = new BlockDefinition(){

			@Override
			public void randomUpdate(Map map, int x, int y, int b) {
			}
			
		};
		defs[2].particleItemID = 108;
		defs[2].particleCount = 256;
		defs[2].breaksWithBigPieces = true;
		
		defs[3] = new BlockDefinition(){

			@Override
			public void randomUpdate(Map map, int x, int y, int b) {
			}
			
		};
		
		
		defs[4] = new SlopeBlockDefinition();
		
		defs[5] = new WaterBlockDef();
		defs[6] = new LavaBlockDef();
		
		defs[9] = new LiquidSlopeBlockDef();
		
		
	}
	@Override
	public void addedToEngine(Engine engine) {
		//world = engine.getSystem(PhysicsSystem.class).world;
		family = Family.one(Map.class).get();
		entities = engine.getEntitiesFor(family);
		mapM = ComponentMapper.getFor(Map.class);
		engine.addEntityListener(family, this);
	}
	@Override
	public void removedFromEngine(Engine engine) {
		// TODO Auto-generated method stub
		super.removedFromEngine(engine);
	}
	@Override
	public void update(float deltaTime) {
		//Gdx.app.log(TAG, "updat3");
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			Map map = mapM.get(e);
			for (int ind = 0; ind < map.physicsDirty.length; ind++){
				if (map.physicsDirty[ind]){
					//Gdx.app.log(TAG, "dirty"+i);
					map.physicsDirty[ind] = false;
					//destroyBodiesForRow(i);
					//createBodiesForRow(i);
				}
			}
			
		}

	}



	public Map getMapFor(int x, int y) {
		
		return overworld.getMapFor(x, y);
	}



	@Override
	public void entityAdded(Entity e) {
		mapM.get(e).defs = defs;
		
	}



	@Override
	public void entityRemoved(Entity entity) {
		// TODO Auto-generated method stub
		
	}



	public static BlockDefinition getDef(int other) {
		
		return defs[((other & Map.ID_MASK ) >> Map.ID_BITS)];
	}



	
	
	
}
