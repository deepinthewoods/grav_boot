package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pools;
import com.niz.BlockDefinition;
import com.niz.PlatformerFactory;
import com.niz.astar.FallPathConnection;
import com.niz.astar.JumpPathConnection;
import com.niz.astar.PathGraph;
import com.niz.astar.PathNode;
import com.niz.astar.RunPathConnection;
import com.niz.component.Map;

public class PathfindingUpdateSystem extends EntitySystem {
	
	private static final String TAG = "path upd sys";
	private ComponentMapper<Map> mapM;
	private ImmutableArray<Entity> entities;
	private PathfindingSystem pathSys;
	IntMap<Array<GridPoint2>> b = new IntMap<Array<GridPoint2>>();
	IntMap<FloatArray> t = new IntMap<FloatArray>();
	Array<GridPoint2>[] jumpBlocks = new Array[PlatformerFactory.PATHFINDING_COUNT];
	FloatArray[] jumpTimes = new FloatArray[PlatformerFactory.PATHFINDING_COUNT];

	@Override
	public void addedToEngine(Engine engine) {
		MapSystem map = engine.getSystem(MapSystem.class);
		super.addedToEngine(engine);
		pathSys = engine.getSystem(PathfindingSystem.class);
		mapM = ComponentMapper.getFor(Map.class);
		Family family = Family.one(Map.class).get();
		entities = engine.getEntitiesFor(family);
	}
	
	@Override
	public void update(float deltaTime) {
		//look for dirties
		//
		for (int ind = 0; ind < entities.size(); ind++){
			Entity e = entities.get(ind);
			Map map = mapM.get(e);
			if (map == null) return;
			int index = map.dirtyPath.nextSetBit(0);
			if (index != -1){
				map.dirtyPath.clear(index);
				int x = index % OverworldSystem.SCROLLING_MAP_WIDTH;
				int y = index / OverworldSystem.SCROLLING_MAP_WIDTH;
				makePath(map, index, pathSys.graph, x, y);
			}
		}
	}

	private void makePath(Map map, int index, PathGraph graph, int x, int y) {
		PathNode node = graph.nodes[index];
		node.connections.clear();
		BlockDefinition under = map.defs[(map.get(x, y-1) & map.ID_MASK) >> map.ID_BITS];
		if (under.isSolid){
			node.hasFloor = true;
		} else {
			node.hasFloor = false;
		}
		if (node.hasFloor){
			
			for (int i = 0; i < jumpBlocks.length; i++){
				Array<GridPoint2> blocks = jumpBlocks[i];
				FloatArray times = jumpTimes[i];
				for (int c = 0; c < blocks.size; c++){
					GridPoint2 block = blocks.get(c);
					float time = times.get(c);
					int b = map.get(x + block.x, y + block.y);
					int id = (b & map.ID_MASK) >> map.ID_BITS;
				BlockDefinition def = map.defs[id];
				if (def.isSolid){
					finishedPath(map, index, graph, x, y, blocks, times, c-1, i);
					return;
				}
				}
			}
			//run side
			BlockDefinition bl = map.defs[(map.get(x-1, y) & map.ID_MASK) >> map.ID_BITS];
			if (x != 0 && !bl.isSolid){
				RunPathConnection c = Pools.obtain(RunPathConnection.class);
				c.from = node;
				PathNode to = graph.getNode(x-1, y);
				c.to = to;		
				c.cost = 1f/20f;
				graph.nodes[index].connections.add(c);
				
			}
			BlockDefinition br = map.defs[(map.get(x+1, y) & map.ID_MASK) >> map.ID_BITS];
			if (x != OverworldSystem.SCROLLING_MAP_WIDTH-1 && !br.isSolid){			
				RunPathConnection c = Pools.obtain(RunPathConnection.class);
				c.from = node;
				PathNode to = graph.getNode(x+1, y);
				c.to = to;		
				c.cost = 1f/20f;
				graph.nodes[index].connections.add(c);
			}
			
		} else if (y != 0){//fall
			FallPathConnection c = Pools.obtain(FallPathConnection.class);
			c.from = node;
			PathNode to = graph.getNode(x, y-1);
			c.to = to;		
			c.cost = 1f/20f;
			graph.nodes[index].connections.add(c);
		}
	}
	
	private void finishedPath(Map map, int index, PathGraph graph, int x, int y, Array<GridPoint2> blocks, FloatArray times, int end, int jumpTypeIndex) {
		PathNode from = graph.nodes[index];
		for (int i = 1; i <= end; i++){
			GridPoint2 block = blocks.get(i);
			float time = times.get(i);
			//int b = map.get(x + block.x, y + block.y - 1);
			//int id = (b & map.ID_MASK) >> map.ID_BITS;
			//B//lockDefinition def = map.defs[id];
			//if (def.isSolid){
			PathNode to = graph.getNode(x + block.x, y + block.y);
			JumpPathConnection c = Pools.obtain(JumpPathConnection.class);
			c.from = from;
			c.to = to;
			c.index = jumpTypeIndex;
			c.cost = time;
			graph.nodes[index].connections.add(c);
			
			//}
		}
	}

	public void registerJumpBlocks(Array<GridPoint2> blocks, FloatArray blockTimes, int index) {
		b.put(index, blocks);
		t.put(index, blockTimes);
	}

	public void setJumpPaths() {
		Gdx.app.log(TAG,  "JUMP PATHS JUMP PATHSJUOMPPATHSSPJAU)POAMPAHS)APSYSWTHSHPSAPO ");
		for (int i = 0; i < PlatformerFactory.PATHFINDING_COUNT; i++){
			Array<GridPoint2> arr = b.get(i);
			jumpBlocks[i] = arr;
			FloatArray time = t.get(i);
			jumpTimes[i] = time;
		}
	}
}
