package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.niz.system.BucketSystem;

public class Buckets implements Component, Poolable{
	public int bucket,largeBucket;
	public int x, y;
	public int largeX, largeY;
	public transient BucketSystem bucketSys;
	public long eID;
	@Override
	public void reset() {
		bucketSys.remove(bucket, largeBucket, eID);
		
	}
	
	
}
