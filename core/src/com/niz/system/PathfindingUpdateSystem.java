package com.niz.system;

import java.util.Iterator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.Pools;
import com.niz.BlockDefinition;
import com.niz.actions.AJumpCharSelect;
import com.niz.actions.APathfindingJumpAndHold;
import com.niz.astar.FallPathConnection;
import com.niz.astar.JumpPathConnection;
import com.niz.astar.PathGraph;
import com.niz.astar.PathNode;
import com.niz.astar.RunPathConnection;
import com.niz.component.Map;

public class PathfindingUpdateSystem extends EntitySystem {
	
	private static final float RUN_COST = 0.0000001f;
	private static final String TAG = "path upd sys";
	private static final int REPS = 300;
	private ComponentMapper<Map> mapM;
	private ImmutableArray<Entity> entities;
	private PathfindingSystem pathSys;
	IntMap<Array<GridPoint2>> b = new IntMap<Array<GridPoint2>>();
	IntMap<FloatArray> t = new IntMap<FloatArray>();
	
	

	Array<GridPoint2>[] jumpBlocks;// = new Array[PlatformerFactory.PATHFINDING_COUNT];
	FloatArray[] jumpTimes;// = new FloatArray[PlatformerFactory.PATHFINDING_COUNT];
	int[] jumpKeys;// = new int[PlatformerFactory.PATHFINDING_COUNT];
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
			for (int r = 0; r < REPS; r++){
				
				int index = map.dirtyPath.nextSetBit(0);
				if (index != -1){
					map.dirtyPath.clear(index);
					int x = index % OverworldSystem.SCROLLING_MAP_WIDTH;
					int y = index / OverworldSystem.SCROLLING_MAP_WIDTH;
					makePath(map, index, pathSys.graph, x, y);
				}
			}
		}
	}

	private void makePath(Map map, int nodeIndex, PathGraph graph, int x, int y) {
		//Gdx.app.log(TAG, "make path" + index);
		PathNode node = graph.nodes[nodeIndex];
		//if (graph.getNode(x, y) != node) throw new GdxRuntimeException("JKLLK");
		node.connections.clear();
		BlockDefinition under = map.defs[(map.get(x, y-1) & map.ID_MASK) >> map.ID_BITS];
		if (under.isSolid){
			node.hasFloor = true;
		} else {
			node.hasFloor = false;
		}
		BlockDefinition current = map.defs[(map.get(x, y) & map.ID_MASK) >> map.ID_BITS];
		if (current.isSolid) return;
		
		//run side
		BlockDefinition bl = map.defs[(map.get(x-1, y) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition bbl = map.defs[(map.get(x-1, y-1) & map.ID_MASK) >> map.ID_BITS];
		if (node.hasFloor){
			
		}
		
		BlockDefinition br = map.defs[(map.get(x+1, y) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition bbr = map.defs[(map.get(x+1, y-1) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition bl2 = map.defs[(map.get(x-2, y) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition bbl2 = map.defs[(map.get(x-2, y-1) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition br2 = map.defs[(map.get(x+2, y) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition bbr2 = map.defs[(map.get(x+2, y-1) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition bl3 = map.defs[(map.get(x-3, y) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition bbl3 = map.defs[(map.get(x-3, y-1) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition br3 = map.defs[(map.get(x+3, y) & map.ID_MASK) >> map.ID_BITS];
		BlockDefinition bbr3 = map.defs[(map.get(x+3, y-1) & map.ID_MASK) >> map.ID_BITS];
		boolean runwayL = !bl2.isSolid && !bl3.isSolid && bbl2.isSolid && bbl3.isSolid;
		boolean runwayR = !br2.isSolid && !br3.isSolid && bbr2.isSolid && bbr3.isSolid;
		
		
		if (node.hasFloor){	
			
			if (x != 0 && !bl.isSolid && bbl.isSolid){
				RunPathConnection c = Pools.obtain(RunPathConnection.class);
				c.from = node;
				PathNode to = graph.getNode(x-1, y);
				c.to = to;		
				c.cost = RUN_COST;
				graph.nodes[nodeIndex].connections.add(c);
				//c.index = 1;
				//c.key = 1;
				//Gdx.app.log(TAG, "runl " +  " " + x + "," + y + c);
				if (c.to.y != c.from.y) throw new GdxRuntimeException("err " + c);
			}
			
			
			if (x != OverworldSystem.SCROLLING_MAP_WIDTH-1 && !br.isSolid && bbr.isSolid){			
				RunPathConnection c = Pools.obtain(RunPathConnection.class);
				c.from = node;
				PathNode to = graph.getNode(x+1, y);
				c.to = to;		
				c.cost = RUN_COST;
				graph.nodes[nodeIndex].connections.add(c);
				//Gdx.app.log(TAG, "runr " +  " " + x + "," + y + c);
				if (c.to.y != c.from.y) throw new GdxRuntimeException("err " + c);
			}
			
			///
			makeJumpConnections(
					(APathfindingJumpAndHold.NORMAL_JUMP | APathfindingJumpAndHold.DELAYED_REVERSE_JUMP 
							| APathfindingJumpAndHold.STANDING_DELAYED_RUN_JUMP | APathfindingJumpAndHold.STANDING_JUMP)
					, map, x, y, runwayL, nodeIndex, graph, 1);
			makeJumpConnections(
					(APathfindingJumpAndHold.NORMAL_JUMP | APathfindingJumpAndHold.DELAYED_REVERSE_JUMP 
							| APathfindingJumpAndHold.STANDING_DELAYED_RUN_JUMP | APathfindingJumpAndHold.STANDING_JUMP)
					, map, x, y, runwayR, nodeIndex, graph, -1);
			/*for (int i = 0; i < jumpBlocks.length; i++){
				Array<GridPoint2> blocks = jumpBlocks[i];
				FloatArray times = jumpTimes[i];
				
				for (int c = 0; c < blocks.size; c++){
					GridPoint2 block = blocks.get(c);
					//Gdx.app.log(TAG, "try " + block);
					float time = times.get(c);
					int b = map.get(x - block.x, y + block.y);
					int id = (b & map.ID_MASK) >> map.ID_BITS;
					BlockDefinition def = map.defs[id];
					if (def.isSolid){
						boolean st = jumpStand[i];
						if (!st || runwayL)
						finishedJumpPath(map, nodeIndex, graph, x, y, blocks, times, c-1, jumpKeys[i], i, st, true);
						break;
					}
				}
			}//*/
			
			
			
			
		} else{//no floor
			if (y != 0){//fall
				BlockDefinition bd = map.defs[(map.get(x, y-1) & map.ID_MASK) >> map.ID_BITS];
				if (!bd.isSolid){	
					
					FallPathConnection c = Pools.obtain(FallPathConnection.class);
					c.from = node;
					PathNode to = graph.getNode(x, y-1);
					c.to = to;		
					c.cost = 1f/20f;
					graph.nodes[nodeIndex].connections.add(c);
					//Gdx.app.log(TAG, "fall" + index);
				}
			}
			
			//walljump
			if (bl.isSolid){
				makeJumpConnections(
						(APathfindingJumpAndHold.WALLJUMP )
						, map, x, y, runwayL, nodeIndex, graph, 1);
			}
			
			if (br.isSolid){
				makeJumpConnections(
						(APathfindingJumpAndHold.WALLJUMP )
						, map, x, y, runwayL, nodeIndex, graph, -1);
			}
			
		}
	}
	
	private void makeJumpConnections(int possibleKeys, Map map, int x, int y, boolean runway, int nodeIndex, PathGraph graph, int flipX) {
		for (int i = 0; i < jumpBlocks.length; i++){
			int key = jumpKeys[i];
			if ((key & possibleKeys) == 0){
				//Gdx.app.log(TAG, "skip" + key + "  " + possibleKeys + " - " + (key & possibleKeys));
				continue;
			}
			Array<GridPoint2> blocks = jumpBlocks[i];
			FloatArray times = jumpTimes[i];
			boolean done = false;
			boolean st = isStand(key);
			boolean needsHeadroom = needsHeadroom(key);
			if (needsHeadroom){
				for (int c = 0; c < blocks.size; c++){
					GridPoint2 block = blocks.get(c);
					//Gdx.app.log(TAG, "try " + block);
					float time = times.get(c);
					int b = map.get(x + block.x * flipX, y + block.y);
					int id = (b & map.ID_MASK) >> map.ID_BITS;
				BlockDefinition def = map.defs[id];
				int topb = map.get(x + block.x * flipX, y + block.y + 1);
				int topid = (topb & map.ID_MASK) >> map.ID_BITS;
				BlockDefinition topDef = map.defs[topid];

				if (def.isSolid || topDef.isSolid){
					if (st || runway)
						finishedJumpPath(map, nodeIndex, graph, x, y, blocks, times, c-1, jumpKeys[i], i, st, flipX);
					done = true;
					break;
				}
				}

			} else {
				
				for (int c = 0; c < blocks.size; c++){
					GridPoint2 block = blocks.get(c);
					//Gdx.app.log(TAG, "try " + block);
					float time = times.get(c);
					int b = map.get(x + block.x * flipX, y + block.y);
					int id = (b & map.ID_MASK) >> map.ID_BITS;
				BlockDefinition def = map.defs[id];
				if (def.isSolid){
					if (st || runway)
						finishedJumpPath(map, nodeIndex, graph, x, y, blocks, times, c-1, jumpKeys[i], i, st, flipX);
					done = true;
					break;
				}
				}
			}
			if (!done){
				if (st || runway)
					finishedJumpPath(map, nodeIndex, graph, x, y, blocks, times, blocks.size-1-1, jumpKeys[i], i, st, flipX);
				
			}
			
		}
	}

	private boolean needsHeadroom(int i) {
		i &= AStar.TYPE_MASK;
		return  (i & (APathfindingJumpAndHold.DELAYED_REVERSE_JUMP )) != 0;
	}

	private boolean isStand(int i) {
		i &= AStar.TYPE_MASK;
		return  (i & (APathfindingJumpAndHold.STANDING_DELAYED_RUN_JUMP | APathfindingJumpAndHold.STANDING_JUMP | APathfindingJumpAndHold.DELAYED_REVERSE_JUMP)) != 0;
		
	}

	private void finishedJumpPath(Map map, int nodeIndex, PathGraph graph, int x, int y, Array<GridPoint2> blocks, FloatArray times, int end, int jumpkey, int jumpIndex, boolean stand, int flipX){
		
		
		
		if (end > 0);
		else return;
		PathNode from = graph.nodes[nodeIndex];
		for (int i = 1; i <= end; i++){
			GridPoint2 block = blocks.get(i);
			float time = times.get(i);
			//int b = map.get(x + block.x, y + block.y - 1);
			//int id = (b & map.ID_MASK) >> map.ID_BITS;
			//B//lockDefinition def = map.defs[id];
			//if (def.isSolid){
			
			PathNode to = graph.getNode(x + block.x * flipX, y + block.y);
			if (to != null){

			}
			JumpPathConnection c = Pools.obtain(JumpPathConnection.class);
			c.from = from;
			c.to = to;
			c.key = jumpkey;
			c.index = jumpIndex;
			c.cost = time;
			c.stand = stand;
			c.isLeft = (flipX == -1);
			graph.nodes[nodeIndex].connections.add(c);
			
			//}
			//Gdx.app.log(TAG, "finish jump path " + end + "  " + time);
		}
	}

	public void registerJumpBlocks(Array<GridPoint2> blocks, FloatArray blockTimes, int index) {
		b.put(index, blocks);
		t.put(index, blockTimes);
	
		
		//Gdx.app.log(TAG,  "register " + index + "  " + blocks.size);
	}

	public void setJumpPaths() {
		//Gdx.app.log(TAG,  "JUMP PATHS JUMP PATHSJUOMPPATHSSPJAU)POAMPAHS)APSYSWTHSHPSAPO ");
		Iterator<Entry<Array<GridPoint2>>> it = b.iterator();
		
		jumpBlocks = new Array[b.size];
		jumpTimes = new FloatArray[b.size];
		jumpKeys = new int[b.size];
		
 		int i = 0;
		while (it.hasNext()){
			Entry<Array<GridPoint2>> val = it.next();
			Array<GridPoint2> arr = val.value;
			int key = val.key;
			jumpBlocks[i] = arr;
			FloatArray time = t.get(key);
			jumpTimes[i] = time;
			jumpKeys[i] = key;
			//Gdx.app.log(TAG,  "hjgjh" + i);
			//Gdx.app.log(TAG,  "hjgjh" + arr.size);
			i++;

		}
		//if (true) throw new GdxRuntimeException("hkj ");
	}
}
