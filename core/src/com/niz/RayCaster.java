package com.niz;


import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public class RayCaster{
	//converted from xna wiki http://www.xnawiki.com/index.php?title=Voxel_traversal
	private static final float PositiveInfinity = Float.POSITIVE_INFINITY;
	public final static int TOP = 0, BOTTOM = 1, RIGHT = 2, LEFT = 3;
	public static GridPoint2[] normalOffsets = {new GridPoint2(0, 1), new GridPoint2(0, -1), new GridPoint2(1, 0), new GridPoint2(-1, 0)};
	public static Vector2[] normalLineOffsets = {new Vector2(0, 0), new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0)};
	public static Vector2[] normalLine2Offsets = {new Vector2(1, 0), new Vector2(1, 1), new Vector2(0, 1), new Vector2(1, 1)};
	public static Vector2[] normal2LineOffsets = {new Vector2(0, 1), new Vector2(0, 0), new Vector2(1, 0), new Vector2(0, 0)};
	public static Vector2[] normal2Line2Offsets = {new Vector2(1, 1), new Vector2(1, 0), new Vector2(1, 1), new Vector2(0, 1)};

	public int x,y;
	public int stepX, stepY;
	//public IEnumerable<Point3D> GetCellsOnRay(Ray ray, int maxDepth)
	GridPoint2 cellBoundary = new GridPoint2(), start = new GridPoint2();
	Vector2 tDelta = new Vector2(), tMax = new Vector2(), tmpDirection = new Vector2();
	public int progress;
	public int face, limit;
	private int xFace, yFace;
	public boolean hasNext, hasTarget;
	public int targetX, targetY, srcX, srcY;
	
	
	public void trace(Vector2 src, Vector2 dst){
		trace(src.x, src.y,  dst);
	}
	public void trace(float x, float y,  Vector2 dst) {
		trace(x,y,dst.x, dst.y);
	}
		
	public void trace(float x, float y, float dstX, float dstY) {
        srcX = (int) x;
        srcY = (int) y;
		targetX = (int) dstX;
		targetY = (int) dstY;
		tmpDirection.set(dstX, dstY).sub(x,y);
		limit = (int) 100;//Math.max(tmpDirection.x,tmpDirection.z)+4;
		hasTarget = true;
		//Gdx.app.log("rayc", "trace limit"+limit);
		traceWithDirectionReal(x, y, tmpDirection.nor());
	}
	
	public void traceWithDirection(float x2, float y2, Vector2 direction)
	{
		hasTarget = false;
		traceWithDirectionReal(x2, y2, direction);
	}
	public void traceWithDirection(Vector2 position, Vector2 direction) {
		hasTarget = false;
		traceWithDirectionReal(position.x, position.y, direction);
		
	}

	public void traceWithDirectionReal(float x2, float y2, Vector2 direction)
	{
	    // Implementation is based on:
	    // "A Fast Voxel Traversal Algorithm for Ray Tracing"
	    // John Amanatides, Andrew Woo
	    // http://www.cse.yorku.ca/~amana/research/grid.pdf
	    // http://www.devmaster.net/articles/raytracing_series/A%20faster%20voxel%20traversal%20algorithm%20for%20ray%20tracing.pdf
	 
	    // NOTES:
	    // * This code assumes that the ray's position and direction are in 'cell coordinates', which means
	    //   that one unit equals one cell in all directions.
	    // * When the ray doesn't start within the voxel grid, calculate the first position at which the
	    //   ray could enter the grid. If it never enters the grid, there is nothing more to do here.
	    // * Also, it is important to test when the ray exits the voxel grid when the grid isn't infinite.
	    // * The Point3D structure is a simple structure having three integer fields (X, Y and Z).
		hasNext = true;
	    // The cell in which the ray starts.
		progress = 0;
		start.set((int)x2, (int)y2);        // Rounds the position's X, Y and Z down to the nearest integer values.
	    x = start.x;
	    y = start.y;
	 
	    // Determine which way we go.
	    stepX = sign(direction.x);
	    stepY = sign(direction.y);
	 
	    // Calculate cell boundaries. When the step (i.e. direction sign) is positive,
	    // the next boundary is AFTER our current position, meaning that we have to add 1.
	    // Otherwise, it is BEFORE our current position, in which case we add nothing.
	    cellBoundary.set(
	        x + (stepX > 0 ? 1 : 0),
	        y + (stepY > 0 ? 1 : 0));
	 
	    // NOTE: For the following calculations, the result will be Single.PositiveInfinity
	    // when direction.x, Y or Z equals zero, which is OK. However, when the left-hand
	    // value of the division also equals zero, the result is Single.NaN, which is not OK.
	 
	    // Determine how far we can travel along the ray before we hit a voxel boundary.
	    tMax.set(
	        (cellBoundary.x - x2) / direction.x,    // Boundary is a plane on the YZ axis.
	        (cellBoundary.y - y2) / direction.y)    // Boundary is a plane on the XZ axis.
	        ;    // Boundary is a plane on the XY axis.
	    //Gdx.app.log("ray", "MMMMMMMMMMMMMAAAABBBBBBBBBBAAAAAXXXXXXXXXXXX"+tMax);
	    if (isNaN(tMax.x)) tMax.x = PositiveInfinity;
	    if (isNaN(tMax.y)) tMax.y = PositiveInfinity;
	    //Gdx.app.log("ray", "MMMMMMMMMMMMMAAAAAAAAAXXXXXXXXXXXX"+tMax);
	    // Determine how far we must travel along the ray before we have crossed a gridcell.
	    tDelta.set(
	        stepX / direction.x,                    // Crossing the width of a cell.
	        stepY / direction.y)                    // Crossing the height of a cell.
	        ;                    // Crossing the depth of a cell.
	    if (isNaN(tDelta.x)) tDelta.x = PositiveInfinity;
	    if (isNaN(tDelta.y)) tDelta.y = PositiveInfinity;
	 
	    // For each step, determine which distance to the next voxel boundary is lowest (i.e.
	    // which voxel boundary is nearest) and walk that way.
	   // for (int i = 0; i < maxDepth; i++)
	    xFace = stepX>0?LEFT:RIGHT;
	    yFace = stepY>0?BOTTOM:TOP;
	}
	
	public void next()
		{
			progress++;
	        // Do the next step.
	        if (tMax.x < tMax.y )
	        {
	            // tMax.x is the lowest, an YZ cell boundary plane is nearest.
	            x += stepX;
	            tMax.x += tDelta.x;
	            face = xFace;
	           // Gdx.app.log("rayc", "nx");
	        }
	        else 
	        {
	            // tMax.y is the lowest, an XZ cell boundary plane is nearest.
	            y += stepY;
	            tMax.y += tDelta.y;
	            face = yFace;
	           // Gdx.app.log("rayc", "ny");
	        }
	        
	        if (progress >= limit) hasNext = false;
	       if (hasTarget){
	    	   if (stepX > 0){
	    		   if (x > targetX) hasNext = false;
	    	   }
	    	   else if (stepX < 0){
	    		   if (x < targetX) hasNext = false;
	    	   }
	    	   
	    	   if (stepY > 0){
	    		   if (y > targetY) hasNext = false;
	    	   }
	    	   else if (stepY < 0){
	    		   if (y < targetY) hasNext = false;
	    	   }
	    	   
	    	  
	       }
	       //Gdx.app.log("rayc", "next "+x+","+y+","+z + " delat "+tMax);
	    }
	
	
	private int sign(float x2) {
		if (x2 == 0f) return 0;
		return x2>0?1:-1;
	}
	private boolean isNaN(float x) {
		if (x == Float.NEGATIVE_INFINITY) return true;
		if (x == x) return false;
		return true;
	}



}