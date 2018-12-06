package com.niz.anim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ShortArray;
import com.niz.Data;
import com.niz.Main;
import com.niz.component.Map;

public class AnimationCommand {
	private static final String TAG = "anim comnd";
	//private static AtlasSprite[] frames;
	//private static AtlasSprite[] tipFrames;
	private static Class arrayClass = (new AtlasSprite[1]).getClass();
		
	
	private static Vector2 tmpV = new Vector2();
	@Range( min = 0f,
			max = 1f)
	public float delta;
	public int offset = 0;
	public int length = 4;
	public String animName = "name";
	public boolean loop;
	public boolean randomStart;
	public boolean velocityDependant;
	//public boolean drawOnMove = false;
	public int bitmask = 0;
	//String fileNamePrefix = "tile";
	public float deltaMultiplier = 1f;
	public int skipFrames;



	static void make(AnimationCommand c, TextureAtlas atlas, AnimSet animSet, String[] layers, String[] baseLayers, AnimationContainer container, String prefix, String[] spritePrefixes){
		for (String sprPrefix : spritePrefixes){
			AtlasSprite[][] layersArr = new AtlasSprite[layers.length][];
			Vector2[][] offsetArr = new Vector2[layers.length][];
			makeArrays(
					c, atlas, layers, baseLayers, prefix, sprPrefix
					, layersArr, offsetArr
			);
			makeActual(c, animSet, layersArr, offsetArr, container, prefix, sprPrefix, layers);
		}
	}

	static void make(AnimationCommand c, TextureAtlas atlas, AnimSet animSet, String[] layers, AtlasSprite[][] layersArr, Vector2[][] offsetArr, AnimationContainer container, String prefix, String[] spritePrefixes){
		for (String sprPrefix : spritePrefixes){

			makeArrays(
					c, atlas, null, null, prefix, sprPrefix
					, layersArr, offsetArr
			);
			makeActual(c, animSet, layersArr, offsetArr, container, prefix, sprPrefix, layers);
		}
	}
	static void make(AnimationCommand c, TextureAtlas atlas, AnimSet animSet, String[] layers, AtlasSprite[][] layersArr, String[] baseLayers, AnimationContainer container, String prefix, String[] spritePrefixes){
		for (String sprPrefix : spritePrefixes){
			//AtlasSprite[][] layersArr = new AtlasSprite[layers.length][];
			Vector2[][] offsetArr = new Vector2[layers.length][];
			makeArrays(
					c, atlas, null, baseLayers, prefix, sprPrefix
					, layersArr, offsetArr
			);
			layersArr = trimArray(c, layersArr, layers);
			makeActual(c, animSet, layersArr, offsetArr, container, prefix, sprPrefix, layers);
		}
	}

	private static AtlasSprite[][] trimArray(AnimationCommand c, AtlasSprite[][] layersArr, String[] layers) {
		AtlasSprite[][] arr = new AtlasSprite[layers.length][];
		for (int y = 0; y < layers.length; y++){
			int frameIndex = c.offset;

			AtlasSprite[] frames = new AtlasSprite[c.length / (c.skipFrames+1)];
			for (int i = 0; i < frames.length; i++){
				//Gdx.app.log(TAG, "layer "+i+ " / "+c.length + " + " + c.offset + baseFileName);

				//frames[i] = (AtlasSprite) atlas.createSprite(fileNamePrefix  , (frameIndex ));
				frames[i] = new AtlasSprite(layersArr[y][frameIndex]);

				frameIndex++;
				frameIndex += c.skipFrames;
				//i += c.skipFrames;
			}
			arr[y] = frames;
		}

		return arr;
	}

	static void makeArrays(AnimationCommand c, TextureAtlas atlas, String[] layers, String[] baseLayers, String prefix, String spritePrefix, AtlasSprite[][] layersArr, Vector2[][] offsetArr){
		if (layers != null)
			for (int y = 0; y < layers.length; y++){
				AtlasSprite[] frames = new AtlasSprite[c.length / (c.skipFrames+1)];
				//Vector2[] offsets = new Vector2[frames.length];
				int frameIndex = c.offset;
				String fileNamePrefix = "diff/"+spritePrefix+layers[y];
				//String baseFileName = prefix+baseLayers[y];
				//ShortArray base = Animations.guides.get(Data.hash(baseFileName));

				if (atlas.findRegion(fileNamePrefix, 0) == null){
					if (fileNamePrefix.contains("neck")
									||
									fileNamePrefix.contains("tail")	){ continue;
					}
				}
				for (int i = 0; i < frames.length; i++){
					//Gdx.app.log(TAG, "layer "+i+ " / "+c.length + " + " + c.offset + baseFileName);

					frames[i] = (AtlasSprite) atlas.createSprite(fileNamePrefix  , (frameIndex ));
					frames[i] = new AtlasSprite(frames[i]);
					if (frames[i] == null) throw new GdxRuntimeException("null frame! "+fileNamePrefix +"   "+i+"  "+c.length +"  "+c.offset +"  "+c.animName + "  " + frameIndex);

					//AtlasRegion baseF =  atlas.findRegion(baseFileName  , ( i+c.offset));


					frameIndex++;
					frameIndex += c.skipFrames;
					//i += c.skipFrames;
				}
				if (layersArr != null)
					layersArr[y] = frames;

			}
	if (offsetArr != null)
		for (int y = 0; y < offsetArr.length; y++){
			//AtlasSprite[] frames = new AtlasSprite[c.length / (c.skipFrames+1)];
			Vector2[] offsets = new Vector2[c.length / (c.skipFrames+1)];
			int frameIndex = c.offset;

			//String fileNamePrefix = "diff/"+spritePrefix+layers[y];
			String baseFileName = prefix+baseLayers[y];
			ShortArray base = Animations.guides.get(Data.hash(baseFileName));

			/*if (atlas.findRegion(baseFileName, 0) == null){
				if (fileNamePrefix.contains("neck")
						||
						fileNamePrefix.contains("tail")	){ continue;
				}
			}*/
			for (int i = 0; i < offsets.length; i++){
				//Gdx.app.log(TAG, "layer "+i+ " / "+c.length + " + " + c.offset + baseFileName);

				//frames[i] = (AtlasSprite) atlas.createSprite(fileNamePrefix  , (frameIndex ));
				//frames[i] = new AtlasSprite(frames[i]);
				//if (frames[i] == null) throw new GdxRuntimeException("null frame! "+fileNamePrefix +"   "+i+"  "+c.length +"  "+c.offset +"  "+c.animName + "  " + frameIndex);

				//AtlasRegion baseF =  atlas.findRegion(baseFileName  , ( i+c.offset));

				if (base == null) throw new GdxRuntimeException("no file for guides ! "+baseFileName);

				Vector2 offset = new Vector2();
				//offset.set(frames[i].getAtlasRegion().offsetX, frames[i].getAtlasRegion().offsetY);
				short baseX = base.get(frameIndex*2);
				short baseY = base.get(frameIndex*2+1);
				//Gdx.app.log(TAG, "guide base "+baseX+","+baseY + "  " + layers[y] + "  " + baseFileName);
				offset.set(baseX, baseY);
				offset.scl(Main.PX);

				offsets[i] = offset;
				frameIndex++;
				frameIndex += c.skipFrames;
				//i += c.skipFrames;
			}
			//if (layersArr != null)
			//	layersArr[y] = frames;
			if (offsetArr != null)
				offsetArr[y] = offsets;
			//Gdx.app.log(TAG, "save array " + y + " " + layersArr[y] + offsetArr[y] + " " + layersArr.length + " " + offsetArr.length);
		}
	}

	static AnimationContainer makeActual(AnimationCommand c, AnimSet animSet, AtlasSprite[][] layers, Vector2[][] offsetArray, AnimationContainer container, String prefix, String spritePrefix, String[] layerNames){
			for (int y = 0; y < layers.length; y++) {
				//
				AtlasSprite[] frames = layers[y];
				if (frames == null){
					Gdx.app.log(TAG, "null frames " + spritePrefix + layers[y] + "  " + layerNames[y]);
					continue;
				}
				Vector2[] offsets = offsetArray[y];
				AnimSet.addLayerToContainer(c.delta * (1 + c.skipFrames), c, frames, c.bitmask, spritePrefix + layerNames[y], container, offsets);
			}
			//container.isVelocityDependant = c.velocityDependant;
			container.randomStart = c.randomStart;
			container.bitmask = c.bitmask;
			animSet.add(c.animName, container);
		return container;
	}

	public static void makeGuideFrames(AnimationCommand c, TextureAtlas atlas,
			AnimSet animSet, String[] layers, String[] baseLayers
			, AnimationContainer container, String prefix) {
		
			
		for (int y = 0; y < layers.length; y++){
			if (layers[y] == null)
				throw new GdxRuntimeException("null layer! "+ prefix+"   "+"  "+y +"  "+c.length );
			//frames = new AtlasSprite[c.length];
			//tipFrames = new AtlasSprite[c.length];
			Vector2[] offsets = new Vector2[c.length];
			float[] angles = new float[c.length / (1+c.skipFrames)];
			String fileName = prefix+layers[y];
			String baseFileName = prefix+baseLayers[y];
			ShortArray base = Animations.guides.get(Data.hash(baseFileName));
			ShortArray main = Animations.guides.get(Data.hash(fileName));
			ShortArray tip = Animations.guides.get(Data.hash(fileName+"tip"));
			boolean hasTip = tip != null;
			if (main == null){
				//throw new GdxRuntimeException("jskld! "+fileName +"   "+"  "+y +"  "+c.length );
				Gdx.app.log(TAG, "null guide, replacing with empty "+fileName +"   "+"  "+y +"  "+c.length );
				for (int i = 0; i < c.length / (1+c.skipFrames); i++){
					offsets[i] = new Vector2(0, 0);

				}
				AnimSet.addGuideToContainer(c.animName, c.loop, c.randomStart, c.velocityDependant, c.delta, c.bitmask, prefix+layers[y], container, offsets, angles);

				continue;
			}
			//if (base != null)
			//Gdx.app.log(TAG, "guide layer "+baseFileName+c.offset + "  hash:"+Data.hash(baseFileName) + "  len"+
			//base.size);
			//(base == null));
			//if (base == null) throw new GdxRuntimeException("null");
			int frameIndex = c.offset;
			for (int i = 0; i < c.length / (1+c.skipFrames); i++){
				
				//frames[i] = (AtlasSprite) atlas.createSprite(fileName  , (frameIndex));

				//AtlasRegion baseF =  atlas.findRegion(baseFileName  , ( i+c.offset));
				//Gdx.app.log(TAG, "guide "+i+ " / "+c.length + " + " + c.offset);
				short mainX = main.get(frameIndex*2);
				short mainY = main.get(frameIndex*2+1);
				Vector2 offset = new Vector2();
				offset.set(mainX, mainY);
				if (base == null && baseLayers[y].length() > 0){ 
					throw new GdxRuntimeException("jskld! "+layers[y]);
				} else if (baseLayers[y].length() == 0){
					//Gdx.app.log(TAG, "no length layer"+fileName+c.offset);
					
					//throw new GdxRuntimeException("jskld! "+baseFileName + "  "+c.animName);
				} else {
					if (frameIndex*2 >= base.size){// throw new GdxRuntimeException("ex"+base.size);
						frameIndex = (base.size - 2)/2;
					}
					//Gdx.app.log(TAG, "base "+fileName+base.size);
					short baseX = base.get(frameIndex*2);
					short baseY = base.get(frameIndex*2+1);
					offset.sub(baseX, baseY);

					if (hasTip){
						tmpV.set(tip.get(frameIndex*2), tip.get(frameIndex*2+1));
						tmpV.sub(mainX, mainY);
						//tmpV.scl(-1);
						angles[i] = (tmpV.angle()+180)%360;
						//Gdx.app.log(TAG, "tip:"+(angles[i]));
					}
					
				}
				
				
				offset.scl(1f/Main.PPM);
				//if (offset.len2() < .5f)Gdx.app.log(TAG, "offset"+fileName+"  ,  "+baseFileName);
				offsets[i] = offset;
				
				frameIndex++;
				frameIndex += c.skipFrames;
			}
			AnimSet.addGuideToContainer(c.animName, c.loop, c.randomStart, c.velocityDependant, c.delta, c.bitmask, prefix+layers[y], container, offsets, angles);
		}
		//animSet.addGuide(c.animName, container);
	}


	public static int[] makeGuideFramesRotated(AnimationCommand c,
			TextureAtlas atlas, String layers,
			 AnimationContainer container,
			String prefix, int startAngle, int endAngle) {

		//frames = new AtlasSprite[c.length];
		//tipFrames = new AtlasSprite[c.length];
		Vector2[] offsets = new Vector2[c.length];
		float[] angles = new float[c.length];
		String fileName = prefix+layers;
		ShortArray main = Animations.guides.get(Data.hash(fileName));
		boolean hasTip = atlas.findRegion(fileName+"tip", 0) != null;
		//if (base != null)
			//Gdx.app.log(TAG, "guide layer "+baseFileName+c.offset + "  hash:"+Data.hash(baseFileName) + "  len"+
		//base.size);
				//(base == null));
		//if (base == null) throw new GdxRuntimeException("null");
		Vector2[] diff = new Vector2[c.length];
		float[] degrees = new float[c.length];
		for (int i = 0; i < c.length; i++){
			int frameIndex = i+c.offset;
			//frames[i] = (AtlasSprite) atlas.createSprite(fileName  , (frameIndex));
			//AtlasRegion baseF =  atlas.findRegion(baseFileName  , ( i+c.offset));
			
			short mainX = main.get(frameIndex*2);
			short mainY = main.get(frameIndex*2+1);
			Vector2 offset = new Vector2();
			offset.set(-mainX, mainY);
			
			
			
			offset.scl(1f/Main.PPM);
			//if (offset.len2() < .5f)Gdx.app.log(TAG, "offset"+fileName+"  ,  "+baseFileName);
			offsets[i] = offset;
			if (i > 0){
				diff[i] = new Vector2();
				diff[i].set(offsets[i]).sub(offsets[i-1]);
			}
		}
		for (int i = 1; i < diff.length; i++){
			float angle = diff[i].angle();
			
			//Gdx.app.log(TAG, "i "+i 
					//+"index:"+adjustedAngle 
					//+ "  angle "+angle);
			
			//float angleNext = diff[i+1].angle();
			
			/*float dx1 = anglePrev - angle;
			float dx2 = angleNext - angle;
			
			if (dx1 < -180) anglePrev += 360;
			if (dx1 > 180) anglePrev -= 360;
			
			if (dx2 < -180) angleNext += 360;
			if (dx2 > 180) angleNext -= 360;
			
			float adjustedAngle = angle + anglePrev + angleNext;
			adjustedAngle /= 3f;*/
			degrees[i] = angle;
		}
		
		int[] indices = new int[360];
		int[] firstIndices = new int[360];
		for (int i = 0; i < firstIndices.length; i++){
			firstIndices[i] = -1;
		}
		int x = 0;
		for (int i = 1; i < degrees.length-1; i++){
			for (x = (int) degrees[i]; x != (int)degrees[i+1]; x = (x+359) % 360){
				//Gdx.app.log(TAG, "from "+x + "  to "+(int)degrees[i+1] + "  ind "+i);
				indices[x] = i;
				if (firstIndices[x] == -1) firstIndices[x] = i;
			}
		}
		if (false)for (; x != (int)degrees[2]; x = (x+359)%360){
			indices[x] = degrees.length-2;
			
			//Gdx.app.log(TAG, "from "+x + "  to "+(int)degrees[2]);
		}
		
		for (int a = startAngle; a <= endAngle; a++){
			indices[a] = firstIndices[a];
			//Gdx.app.log(TAG, "i "+a 
					//+"index:"+adjustedAngle 
					//+ "  angle "+indices[a]);
		}
		//Gdx.app.log(TAG, " jkl;kjsdfjkllk;dj;ls "+layers
				//+ "  angle "+prefix);
		
		return indices;				
		
	
		
		//animSet.addGuide(c.animName, container);
	}


	public static RotatedAnimationLayer makeItemFrames(String name, int id,
													   String baseName, int total, TextureAtlas atlas, Texture processedPlayerTexture) {
		String prefix = "";
		AtlasSprite[] frames = new AtlasSprite[total];
		AtlasSprite[] tipFrames = new AtlasSprite[total];
		
		String fileNamePrefix = "diff/"+prefix+name;
		String baseFileName = prefix+baseName;
		ShortArray base = Animations.guides.get(Data.hash(baseFileName));
		Vector2[] offsets = new Vector2[total];
		//Vector2[] toThrowOffsets = new Vector2[total];
		boolean hasTip = atlas.findRegion(fileNamePrefix+"tip", 0) != null;
		for (int i = 0; i < total; i++){
			
			int frameIndex =  i;
			frames[i] = (AtlasSprite) atlas.createSprite(fileNamePrefix  , (frameIndex ));
			frames[i] = new AtlasSprite(frames[i]);

			if (frames[i] == null) throw new GdxRuntimeException("jskld! "+fileNamePrefix +"   "+i+"  " +"  "+total +"  ");
			
			//AtlasRegion baseF =  atlas.findRegion(baseFileName  , ( i+c.offset));
			
			if (base == null) throw new GdxRuntimeException("jskld! "+baseFileName);
			frames[i].setTexture(processedPlayerTexture);
			Vector2 offset = new Vector2();
			//offset.set(frames[i].getAtlasRegion().offsetX, frames[i].getAtlasRegion().offsetY);
			short baseX = base.get(frameIndex*2);
			short baseY = base.get(frameIndex*2+1);
			//Gdx.app.log(TAG, "guide base "+baseX+","+baseY + "  " + name + "  " + baseFileName);
			offset.set(baseX, baseY);
			offset.scl(Main.PX);

			offsets[i] = offset;
			
			/*Vector2 toThrow = new Vector2();
			toThrowOffsets[i] = toThrow;
			toThrow.set(frames[i].getWidth()/2, frames[i].getHeight()/2);
			toThrow.sub(offset);*/
			
		}
		
		
		//AnimSet.addLayerToContainer(c, frames, c.bitmask, prefix+name, container, offsets);				
		//Gdx.app.log(TAG, "ROTATIONSTARTSTARTASTST");
		int divisor = 360 / total;
		int[] angleToIndex = new int[360];
		for (int i = 0; i < 360; i++){
			int half = 360/(total*2);
			int angle = (i + half) % 360;
			angle = angle +90;
			angle = 180-angle+360+180;
			angle %= 360;
			angleToIndex[i] = (angle / divisor);
			
			//Gdx.app.log(TAG, "rotation"+i+"@"+angleToIndex[i]);
		}
		
		RotatedAnimationLayer layer = new RotatedAnimationLayer(frames, offsets, angleToIndex);
		layer.flipped = layer.doFlip();
		return layer;
	}


	public static RotatedAnimationLayer makeBlockFrames(TextureAtlas atlas, int blockIndex,
														String baseName, Texture processedPlayerTexture) {
		int total = 1;
		blockIndex &= Map.VARIANT_MASK;
		String prefix = "";
		AtlasSprite[] frames = new AtlasSprite[total];
		//AtlasSprite[] tipFrames = new AtlasSprite[total];
		
		String fileNamePrefix = "diff/"+prefix+"block";
		String baseFileName = prefix+baseName;
		ShortArray base = Animations.guides.get(Data.hash(baseFileName));
		Vector2[] offsets = new Vector2[1];
		//Vector2[] toThrowOffsets = new Vector2[total];
		//boolean hasTip = atlas.findRegion(fileNamePrefix+"tip", 0) != null;
		for (int i = 0; i < total; i++){
			
			int frameIndex =  i;
			//Gdx.app.log(TAG, "guide base "+blockIndex);
			frames[i] = (AtlasSprite) atlas.createSprite(fileNamePrefix, blockIndex);
			frames[i] = new AtlasSprite(frames[i]);
			frames[i].setTexture(processedPlayerTexture);
			//if (frames[i] == null) throw new GdxRuntimeException("jskld! "+fileNamePrefix +"   "+i+"  " +"  "+total +"  ");
			
			//AtlasRegion baseF =  atlas.findRegion(baseFileName  , ( i+c.offset));
			
			if (base == null) throw new GdxRuntimeException("jskld! "+baseFileName);

			Vector2 offset = new Vector2();
			//offset.set(frames[i].getAtlasRegion().offsetX, frames[i].getAtlasRegion().offsetY);
			short baseX = base.get(frameIndex*2);
			short baseY = base.get(frameIndex*2+1);
			//Gdx.app.log(TAG, "guide base "+baseX+","+baseY + "  " + name + "  " + baseFileName);
			offset.set(baseX, baseY);
			offset.scl(Main.PX);

			offsets[i] = offset;
			
			/*Vector2 toThrow = new Vector2();
			toThrowOffsets[i] = toThrow;
			toThrow.set(frames[i].getWidth()/2, frames[i].getHeight()/2);
			toThrow.sub(offset);*/
			
		}
		
		
		//AnimSet.addLayerToContainer(c, frames, c.bitmask, prefix+name, container, offsets);				
		//Gdx.app.log(TAG, "ROTATIONSTARTSTARTASTST");
		int divisor = 360 / total;
		int[] angleToIndex = new int[360];
		for (int i = 0; i < 360; i++){
			int half = 360/(total*2);
			int angle = (i + half) % 360;
			angle = angle +90;
			angle = 180-angle+360+180;
			angle %= 360;
			angleToIndex[i] = (angle / divisor);
			
			//Gdx.app.log(TAG, "rotation"+i+"@"+angleToIndex[i]);
		}
		
		RotatedAnimationLayer layer = new RotatedAnimationLayer(frames, offsets, angleToIndex);
		layer.flipped = layer.doFlip();
		return layer;
	}



}
