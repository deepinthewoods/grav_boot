package com.niz.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongArray;
import com.niz.NizPools;
import com.niz.component.Buckets;
import com.niz.component.Position;
import com.niz.observer.Subject;
import com.niz.observer.Subject.Event;

public class BucketSystem extends EntitySystem implements EntityListener {
	
	private static final int PRIME2 = 37;
	private static final int PRIME1 = 641;
	private static final int GRID_SIZE = 2;
	private static final int LARGE_GRID_SIZE = 16;
	private static final String TAG = "buckets sys";
	ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
	ComponentMapper<Buckets> bucketM = ComponentMapper.getFor(Buckets.class);
	private ImmutableArray<Entity> entities;
	int width = 256, height = 256;
	int tot = width*height/(GRID_SIZE*2);

	LongArray[] buckets = new LongArray[tot], largeBuckets = new LongArray[tot];
	private Array<LongArray> arr = new Array<LongArray>();
	private Subject changeSmallNotifier;
	private Subject changeLargeNotifier;
	@Override
	public void addedToEngine(Engine engine) {
		Family family = Family.all(Position.class, Buckets.class).get();
		entities = engine.getEntitiesFor(family);
		engine.addEntityListener(family, this);
		super.addedToEngine(engine);
		changeSmallNotifier = ((EngineNiz)engine).getSubject("changeSmallBuckets");
		changeLargeNotifier = ((EngineNiz)engine).getSubject("changeLargeBuckets");
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}
	
	public Array<LongArray> getSmallBucketsAround(int x, int y){
		LongArray bucket;
		int index;
		arr.clear();
		boolean left = x % 2 == 0;
		if (left){//left
			index = ((x-1)/GRID_SIZE)+((y)/GRID_SIZE)*width; 
			index = (index % tot + tot) % tot;
			bucket = buckets[index];
			if (bucket != null)
				arr.add(bucket);
		} else {//right
			index = ((x+1)/GRID_SIZE)+((y)/GRID_SIZE)*width; 
			index = (index % tot + tot) % tot;
			bucket = buckets[index];
			if (bucket != null)
				arr.add(bucket);
		}
		if (y % 2 == 0){//bottom
			index = ((x)/GRID_SIZE)+((y-1)/GRID_SIZE)*width; 
			index = (index % tot + tot) % tot;
			bucket = buckets[index];
			if (bucket != null)
				arr.add(bucket);
			
			
			if (left){
				index = ((x-1)/GRID_SIZE)+((y-1)/GRID_SIZE)*width; 
				index = (index % tot + tot) % tot;
				bucket = buckets[index];
				if (bucket != null)
					arr.add(bucket);
			} else {
				index = ((x+1)/GRID_SIZE)+((y-1)/GRID_SIZE)*width; 
				index = (index % tot + tot) % tot;
				bucket = buckets[index];
				if (bucket != null)
					arr.add(bucket);
			}
		} else {//top
			
			index = ((x)/GRID_SIZE)+((y+1)/GRID_SIZE)*width; 
			index = (index % tot + tot) % tot;
			bucket = buckets[index];
			if (bucket != null)
				arr.add(bucket);
			
			if (left){
				index = ((x-1)/GRID_SIZE)+((y+1)/GRID_SIZE)*width; 
				index = (index % tot + tot) % tot;
				bucket = buckets[index];
				if (bucket != null)
					arr.add(bucket);
			} else {
				index = ((x+1)/GRID_SIZE)+((y+1)/GRID_SIZE)*width; 
				index = (index % tot + tot) % tot;
				bucket = buckets[index];
				if (bucket != null)
					arr.add(bucket);
			}
		}
		index = ((x)/GRID_SIZE)+((y)/GRID_SIZE)*width; 
		index = (index % tot + tot) % tot;
		bucket = buckets[index];
		if (bucket != null)
			arr.add(bucket);
		return arr;
	}
	

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < entities.size(); i++){
			Entity e = entities.get(i);
			update(e);
			
		}
	}

	private void update(Entity e) {
		Position posC = posM.get(e);
		Vector2 pos = posC.pos;
		int x = (int) (pos.x) / GRID_SIZE;
		int y = (int) (pos.y) / GRID_SIZE;
		int index = x + (y*width);
		//index = x * PRIME1 + y*PRIME2;
		index = (index % tot + tot) % tot;
		
		Buckets b = bucketM.get(e);
		if (b.bucket != index){
			if (b.bucket != -1 && buckets[b.bucket] != null){
				buckets[b.bucket].removeValue(e.getId());
				if (buckets[b.bucket].size == 0){
					NizPools.bucket.free(buckets[b.bucket]);
					buckets[b.bucket] = null;
				}
			}
			
			//Gdx.app.log("buckets", "change"+x + ","+pos.y);
			//changeSmallNotifier.notify(e, Event.CHANGE_SMALL_BUCKET, posC);
			if (buckets[index] == null){
				buckets[index] = NizPools.bucket.obtain();
			}
			buckets[index].add(e.getId());
			b.bucket = index;
			b.x = x;
			b.y = y;
			
			//int largeIndex;// = getLargeIndex(x, y);
			int lax = (int) (pos.x) / LARGE_GRID_SIZE;
			int lay = (int) (pos.y) / LARGE_GRID_SIZE;
			int largeIndex = lax + (lay*width);
			largeIndex = (largeIndex % tot + tot) % tot;
			if (b.largeBucket != largeIndex){
				if (b.largeBucket != -1 && largeBuckets[b.largeBucket] != null){
					largeBuckets[b.largeBucket].removeValue(e.getId());
					if (largeBuckets[b.largeBucket].size == 0){
						NizPools.bucket.free(largeBuckets[b.largeBucket]);
						largeBuckets[b.largeBucket] = null;
						
					}
				}
				changeLargeNotifier.notify(e, Event.CHANGE_LARGE_BUCKET, posC);

				//Gdx.app.log("largeBuckets", "change"+lax + ","+lay);
				if (largeBuckets[largeIndex] == null){
					largeBuckets[largeIndex] = NizPools.bucket.obtain();
				}
				largeBuckets[largeIndex].add(e.getId());
				b.largeBucket = largeIndex;
				b.largeX = lax;
				b.largeY = lay;
			}
			
		}
		
	}

	public Array<LongArray> getSmallBucketsAround(float x, float y) {
		return this.getSmallBucketsAround((int)x, (int)y);
	}

	@Override
	public void entityAdded(Entity e) {
		Buckets b = bucketM.get(e);
		b.bucketSys = this;
		b.eID = e.getId();
		b.bucket = -1;
		b.largeBucket = -1;
		update(e);
	}
	
	

	
	@Override
	public void entityRemoved(Entity e) {
		
		//Gdx.app.log(TAG, "REMOVED");
	}

	public void remove(int bucket, int largeBucket, long eID) {
		// TODO Auto-generated method stub
		
		if (buckets[bucket] != null){
			buckets[bucket].removeValue(eID);
			if (buckets[bucket].size == 0){
				NizPools.bucket.free(buckets[bucket]);
				buckets[bucket] = null;
			}
		} else throw new GdxRuntimeException("buckets null");

		if (largeBuckets[largeBucket] != null){
			largeBuckets[largeBucket].removeValue(eID);
			if (largeBuckets[largeBucket].size == 0){
				NizPools.bucket.free(largeBuckets[largeBucket]);
				largeBuckets[largeBucket] = null;
			}
		} else if (largeBucket == -1) throw new GdxRuntimeException("largebuckets -1");
		else throw new GdxRuntimeException("largebuckets null"+largeBucket);
	}

}
