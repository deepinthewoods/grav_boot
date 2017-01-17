package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.AStar;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.niz.PlatformerFactory;
import com.niz.astar.OutPath;
import com.niz.astar.PathGraph;
import com.niz.astar.PathHeuristic;
import com.niz.astar.PathNode;
import com.niz.component.Body;
import com.niz.component.PathResult;
import com.niz.component.Pathfind;
import com.niz.component.Position;

public class PathfindingSystem extends EntitySystem {
	private static final String TAG = "pathfinding sys";
	private Family family;
	private ImmutableArray<Entity> entities;
	private EngineNiz engine;
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	ComponentMapper<Pathfind> pathM = ComponentMapper.getFor(Pathfind.class);
	ComponentMapper<PathResult> resultM = ComponentMapper.getFor(PathResult.class);
	ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);

	private AStar<PathNode> finder;
	public PathGraph graph;
	public PathHeuristic heuristic;
	

	@Override
	public void addedToEngine(Engine engine) {
		family = Family.all(Position.class, Body.class, Pathfind.class).get();
		entities = engine.getEntitiesFor(family);
		this.engine = (EngineNiz) engine;
		
		graph = new PathGraph();
		
		finder = new AStar<PathNode>(graph, true);
		
		heuristic = new PathHeuristic();
	}
	
	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			Pathfind path = pathM.get(e);
			Vector2 pos = posM.get(e).pos;
			Body body = bodyM.get(e);
			int x = (int) pos.x;
			int y = (int)(pos.y - body.height + .5f);
			//Gdx.app.log(TAG, "path " +  " from " + x + "," + y + "  to " + path.targetX + "," + path.targetY);
			PathNode startNode = graph.getNode(x, y);
			PathNode endNode = graph.getNode(path.targetX, path.targetY);
			
			//GraphPath<PathConnection<PathNode>> outPath = connectionPathPool.obtain();
			
			GraphPath<Connection<PathNode>> outPath = connectionPathPool.obtain();
			int mask = 0xffffffff;
			boolean madePath = finder.searchConnectionPath(startNode, endNode, heuristic, outPath, mask);
			if (madePath) Gdx.app.log(TAG, "made path");
			e.remove(Pathfind.class);
			if (madePath){
				PathResult result = engine.createComponent(PathResult.class);
				result.path = outPath;
				e.add(result);
				
			}
			//Gdx.app.log(TAG, "path result " +  result.path.getCount() + "  " + startNode.connections.size);

			return;
		}
	}
	
	
	private Pool<OutPath> connectionPathPool = new Pool<OutPath>(){

		@Override
		protected OutPath newObject() {
			return new OutPath();
		}
		
	};
	
	
	
}
