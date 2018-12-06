package com.niz.anim;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ShortArray;
import com.niz.Data;
import com.niz.action.ProgressAction;
import com.niz.component.Race;
import com.niz.item.Doing;
import com.niz.system.ProgressBarSystem;
import com.niz.system.WorkerSystem;

public class Animations {
	public static final int PLAYER = Data.hash("player");
	public final static String playerStr = "player";
	private static final String FOLDER = null;
	private static final String PATH_PREFIX = null;
	public static final int TOTAL_ANIMS = 19;
	public static final int NUMBER_OF_ITEM_ANGLES = 24;
	public static final int NUMBER_OF_ITEMS = 256;
	private static final String TAG = "Animation";
	public static IntMap<AnimSet> anim;
	public static AtlasSprite[][] itemSprites, tailSprites, itemSpinSprites, itemTipSprites;
	
	public static Sprite blank;
	public static TextureRegion[] doingTypeImages;
	public static AtlasSprite[] doors = new AtlasSprite[8];
	private static Sprite rpgSprite;
	public static Texture processedPlayerTexture;

	public static void init(TextureAtlas atlas, EngineNiz engine, TextureAtlas uiAtlas, TextureAtlas mapAtlas){
		blank = atlas.createSprite("diff/blank2");
		processedPlayerTexture = new Texture(Gdx.files.internal("playerprocessed.png"));

		//if (blank  == null) throw new GdxRuntimeException(" jkl");
		anim = new IntMap<AnimSet>();
		itemSprites = new AtlasSprite[NUMBER_OF_ITEMS][NUMBER_OF_ITEM_ANGLES];
		itemSpinSprites = new AtlasSprite[NUMBER_OF_ITEMS][NUMBER_OF_ITEM_ANGLES];
		itemTipSprites = new AtlasSprite[NUMBER_OF_ITEMS][NUMBER_OF_ITEM_ANGLES];
		itemDrawables = new TextureRegionDrawable[NUMBER_OF_ITEMS];
		tailSprites = new AtlasSprite[NUMBER_OF_ITEMS][];
		tailDrawables = new TextureRegionDrawable[NUMBER_OF_ITEMS];
		piles = new Sprite[16][8][64];
		int index = 0;
		for (int b = 0; b < 2; b++){
			for (int v = 0; v < 8; v++){
				for (int c = 0; c < 64; c++){
					piles[b][v][c] = SpriteCacheNiz.findSprite(index+12288);
					index++;
				}
			}
		}
		createGuideSprites("");
		createGuideSprites("player");
		createGuideSprites("dragon");
		createGuideSprites("rpg");
		createGuideSprites("none");
		guides.put(Data.hash("centreblockguide"), new ShortArray(new short[]{16, 16}));
		//1-40 walk
		createPlayerSprites("player", atlas, engine);
		
		//createBlockSprite(atlas,  1, "centreblockguide", "centreblockguide", "centreblockguide",  1);
		
		for (int i = 0; i < 8; i++){
			createBlockSprite(atlas,  24+i, "centreblockguide", "centreblockguide", "centreblockguide",  24+i);//25);//slope
			createBlockSprite(atlas,  32+i, "centreblockguide", "centreblockguide", "centreblockguide",  32+i);//25);//stone
			createBlockSprite(atlas,  40+i, "centreblockguide", "centreblockguide", "centreblockguide",  40+i);//25);//dirt

		}
		createBlockSprite(atlas,  14, "centreblockguide", "centreblockguide", "centreblockguide",  14);
		createBlockSprite(atlas,  15, "centreblockguide", "centreblockguide", "centreblockguide",  15);
		
		//block particles/piles
		for (int i  = 0; i < 16; i++){
			
			createBlockSprite(atlas,  100+i, "centreblockguide", "centreblockguide", "centreblockguide",  100+i);
		}
		
		
		
		
		createWeaponSprites(atlas, "sword", 16, "playerspin0guide", "playerspin2guide", "playerspin5guide", 24);
		createWeaponSprites(atlas, "axe", 17, "playerspin0guide", "playerspin2guide", "playerspin5guide", 24);
		createWeaponSprites(atlas, "scimitar", 18, "playerspin0guide", "playerspin2guide", "playerspin4guide", 24);
		createWeaponSprites(atlas, "longsword", 19, "playerspin0guide", "playerspin2guide", "playerspin5guide", 24);
		createWeaponSprites(atlas, "swordshort", 20, "playerspin0guide", "playerspin2guide", "playerspin4guide", 24);
		createWeaponSprites(atlas, "gun", 21, "playerspin0guide", "playerspin2guide", "playerspin2guide", 24);
		createWeaponSprites(atlas, "pickaxe", 22, "playerspin0guide", "playerspin2guide", "playerspin4guide", 24);

		doingLimbImages = new TextureRegion[4];
		doingTypeImages = new TextureRegion[6];
		
		//if (doingLimbImages[0] == null) throw new GdxRuntimeException("null limb anim");
		doingLimbImages[Race.LIMB_BACK_HAND] = uiAtlas.findRegion("handback");
		doingLimbImages[Race.LIMB_FRONT_HAND] = uiAtlas.findRegion("handfront");
		doingLimbImages[Race.LIMB_TAIL] = uiAtlas.findRegion("tail");
		doingLimbImages[Race.LIMB_HEAD] = uiAtlas.findRegion("mouth");
		
		doingTypeImages[Doing.TYPE_THROW] = uiAtlas.findRegion("throw");
		doingTypeImages[Doing.TYPE_PLACE] = uiAtlas.findRegion("place");
		doingTypeImages[Doing.TYPE_SLASH] = uiAtlas.findRegion("slash");
		doingTypeImages[Doing.TYPE_THRUST] = uiAtlas.findRegion("thrust");
		
		doingTypeImages[Doing.TYPE_DESTROY] = uiAtlas.findRegion("destroy");

		//doingTypeImages[Doing.t] = uiAtlas.findRegion("place");
		//doingTypeImages[2] = uiAtlas.findRegion("destroy");
		//doingTypeImages[3] = uiAtlas.findRegion("slash");
		//createWeaponSprites(atlas, "sword", 2, "playerhandgunguide", 24);
		//createWeaponSprites(atlas, "axe", 3, "playerspin0guide", "playerspin2guide", "playerspin4guide", 24);
		
		
		//createTailSprites(atlas, "tentaclepiecesmall", 0, "tentacleguide", 24);
		//createWeaponSprites(atlas, "potion", 3);
		createDoorSprites(mapAtlas);

		blank = uiAtlas.createSprite("button");
	}
	private static void createDoorSprites(TextureAtlas atlas) {
		for (int i = 0; i < 2; i++){
			AtlasRegion reg = atlas.findRegion("diff/doorway", i);
			AtlasSprite as = new AtlasSprite(reg);
			
			doors [i] = as; 
			if (doors[i] == null) throw new GdxRuntimeException("null sprite door "+i + atlas.getRegions());
		}				
	}
	public static IntMap<ShortArray> guides= new IntMap<ShortArray>();
	static String fileFolder = "guides/";
	public static TextureRegionDrawable[] itemDrawables, tailDrawables;
	public static IntMap<IntArray> directions = new IntMap<IntArray>();
	public static RotatedAnimationLayer[] itemLayers = new RotatedAnimationLayer[NUMBER_OF_ITEMS], itemSpinLayers = new RotatedAnimationLayer[NUMBER_OF_ITEMS]
			, itemTipLayers = new RotatedAnimationLayer[NUMBER_OF_ITEMS];
	private static void createGuideSprites(String prefix) {
		String[] layers = {"legsguide", "torsoguide", "neckguide", "headguide", "armsguide"
				, "handfrontguide", "handbackguide"
				, "handfrontguidetip", "handbackguidetip", "tailguide", "tailtipguide", "headguidetip", "tailtipguidetip"
				, "handswordguide", "handaxeguide", "handaxeguide", "handgunguide", "tentacleguide", "tentacleguidetip", "torso2guide"
				, "handdefaultguide" , "spin0guide", "spin1guide", "spin2guide", "spin3guide", "spin4guide", "spin5guide", "spin6guide", "spin7guide"

				};
		
		for (String layer : layers){
			FileHandle file = Gdx.files.internal(fileFolder+prefix+layer);
			if (file.exists())makeGuideSprites(file, prefix+layer);
			//else Gdx.app.log(TAG,  "file doesn't exist: "+fileFolder+prefix+layer);
		}
		
	}

	private static void makeGuideSprites(FileHandle file, String prefix2) {
		ShortArray arr = new ShortArray();
		DataInputStream in = new DataInputStream(file.read());
		while (true){
			try {
				short x = in.readShort();
				short y = in.readShort();
				//Gdx.app.log(TAG,  "guide "+x+","+y);
				//if (x == -1) break;
				arr.add(x);
				arr.add(y);
			} catch (EOFException ex){
				break;
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		guides.put(Data.hash(prefix2), arr);
		//Gdx.app.log(TAG, "make GUIDE "+prefix2 + " = "+arr.size + "  hash:"+Data.hash(prefix2));
	}
	/*public static void add(TextureAtlas atlas, AnimSetDef def){
		AnimSet animSet = new AnimSet();
		for (int i = 0; i < def.animations.size; i++){
			AnimationCommand.make(def.animations.get(i), atlas, animSet, def.layers.toArray());
		}
		anim.put(def.name.hashCode(), animSet);
	}*/
	//public static final int LEGS_G = 0, TORSO_G = 1, NECK_G = 2, HEAD_G = 3, 
	//		ARMS_G = 4, HAND_BACK_G = 5, HAND_FRONT_G = 6, TAIL_G = 7;
	public static String[] legLayers = {"legback"
			, "legfront"};
	public static String[] legBaseLayers = {"legsguide",  
			"legsguide"};
	public static String[] legGuideLayers = {
			"legsguide", "torsoguide", "torso2guide"};
	public static String[] legBaseGuideLayers = {
			"", "legsguide", "legsguide"
	};
	
	public static String[] armLayers = {"armback", "armfront"};
	public static String[] armBaseLayers = {"armsguide", "armsguide"};
	public static String[] armGuideLayers = {
			  "handbackguide", "handfrontguide"
			, "handbackguidetip", "handfrontguidetip" };
	public static String[] armBaseGuideLayers = {
		 "armsguide", "armsguide"
		, "handbackguide", "handfrontguide"
	};
	public static String[] torsoLayers = {
			"torso"
			};
	public static String[] torsoBaseLayers = { 
			"torsoguide"
			
	};
	public static String[] torsoGuideLayers = {
			"neckguide", "tailguide", "armsguide"
			};
	public static String[] torsoBaseGuideLayers = {
		 "torsoguide", "torsoguide", "torsoguide" 
	};
	
	public static String[] headLayers = { "head"
			};
	public static String[] headBaseLayers = {"headguide", 
			};
	
	public static String[] headGuideLayers = {
			 "headguidetip"};
	public static String[] headBaseGuideLayers = {
		"headguide"
	};
	
	public static String[] neckLayers = {"neck"};
	public static String[] neckBaseLayers = {"neckguide"};
	public static String[] neckGuideLayers = {"headguide"
			};
	public static String[] neckBaseGuideLayers = {"neckguide"
	};
	
	public static String[] tailLayers = {"tail"};


	public static String rpg = "rpg";
	public static String[] rpgs;// = {"rpg1", "rpg2"};
	public static String[] rpgLayers;
	public static String[] rpgBaseGuideLayers;// = {"legsguide"};

	public static String none = "none";

	public static String[] tailBaseLayers = {"tailguide"};
	public static String[] tailGuideLayers = { "tailtipguide"};
	public static String[] tailBaseGuideLayers = {"tailguide"};

	public static String[] rpgGuideLayers;// = {"rpg"};




	
	public static String dragon = "dragon";
	public static String[] dragons = {"reddragon", "greendragon", "whitedragon"}, players = {"player"};


	//public static TextureRegion[] doingTypeImages;
	public static TextureRegion[] doingLimbImages;
	public static Sprite[][][] piles;
	public static final int TOTAL_GUIDED_LAYERS = 12;

	private static void createPlayerSprites( final String player, final TextureAtlas atlas, EngineNiz engine) {
		final AnimSet animSet = new AnimSet();
		//animSet.add("walk", true, false, true, false, .0512f, frames);
		//name loop random vel 1f 
		
		rpgSprite = atlas.createSprite("diff/rpg1");
		float one = 1f/2048f;
		/*rpgSprite.setRegion(rpgSprite.getU()+one
		, rpgSprite.getV()+one
		, rpgSprite.getU2()-one
		, rpgSprite.getV2()-one);*/
		rpgSprite.setRegion(rpgSprite.getRegionX()+1, rpgSprite.getRegionY()+1, rpgSprite.getRegionWidth()-2, rpgSprite.getRegionHeight()-2);

		TextureRegion[][] rpgSplits = rpgSprite.split(18, 20);

		final AtlasSprite[][] rpgSprites = new AtlasSprite[26 * 2][12 * 3];
		rpgLayers = new String[26 * 2];
		//rpgGuideLayers = new String[26 * 2];
		rpgBaseGuideLayers = new String[26 * 2];
		rpgs = new String[26*2];
		for (int i = 0; i < rpgLayers.length; i++){
			rpgLayers[i] = "torso";
			//rpgGuideLayers[i] = "torsoguide";
			rpgBaseGuideLayers[i] = "legsguide";
			rpgs[i] = "rpg" +i;
		}
		//Gdx.app.log(TAG, "make sprite group  " + rpgSplits.length + "  w " + rpgSplits[0].length );

		int half = rpgSprites.length / 2;
		for (int i = 0; i < half; i+=1) {
			Gdx.app.log(TAG, "make sprite group  " + rpgSprites.length + "  splits " + rpgSplits.length );

			for (int j = 0; j < 12; j++) {
				TextureRegion split;
				Gdx.app.log(TAG, "make sprite " + i*3 + " " + j);
				int y = i * 3;
				split = rpgSplits[y][j];
				rpgSprites[i][j + 12] = new AtlasSprite(new TextureAtlas.AtlasRegion(split.getTexture(), split.getRegionX(), split.getRegionY(), split.getRegionWidth(), split.getRegionHeight()));

				split = rpgSplits[y+1][j];
				rpgSprites[i][j] = new AtlasSprite(new TextureAtlas.AtlasRegion(split.getTexture(), split.getRegionX(), split.getRegionY(), split.getRegionWidth(), split.getRegionHeight()));

				split = rpgSplits[y+2][j];
				rpgSprites[i][j + 24] = new AtlasSprite(new TextureAtlas.AtlasRegion(split.getTexture(), split.getRegionX(), split.getRegionY(), split.getRegionWidth(), split.getRegionHeight()));


				split = rpgSplits[y][j];
				rpgSprites[i+half][j + 12] = new AtlasSprite(new TextureAtlas.AtlasRegion(split.getTexture(), split.getRegionX(), split.getRegionY(), split.getRegionWidth(), split.getRegionHeight()));

				split = rpgSplits[y+1][j];
				rpgSprites[i+half][j] = new AtlasSprite(new TextureAtlas.AtlasRegion(split.getTexture(), split.getRegionX(), split.getRegionY(), split.getRegionWidth(), split.getRegionHeight()));

				split = rpgSplits[y+2][j];
				rpgSprites[i+half][j + 24] = new AtlasSprite(new TextureAtlas.AtlasRegion(split.getTexture(), split.getRegionX(), split.getRegionY(), split.getRegionWidth(), split.getRegionHeight()));
			}
		}
		
		final float dragonWalkDelta = 0.1f;
		//int offset, length;
		//41-58 roll w2ind-up
		WorkerSystem workSys = engine.getSystem(WorkerSystem.class);
		
		ProgressAction walk = new ProgressAction(){
			@Override
			public void update(float dt) {
				AnimationCommand c = new AnimationCommand();
				c.offset = 50;
				c.length = 39;
				c.animName = "walk";
				c.delta = .0512f;
				c.velocityDependant = true;
				c.loop = true;
				c.bitmask = 2;
				c.skipFrames = 0;//5;
				AnimationContainer container = new AnimationContainer();
				//AnimationCommand.make(c, atlas, animSet, layers, baseLayers, container, player, players);
				//AnimationCommand.makeGuideFrames(c, atlas, animSet, guideLayers, baseGuideLayers, container, player, players);



				AnimationCommand.make(c, atlas, animSet, legLayers, legBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, player);


				AnimationCommand.make(c, atlas, animSet, torsoLayers, torsoBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, neckLayers, neckBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, headLayers, headBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, player);
				c.skipFrames = 0;
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, player);


				c.offset = 0;
				c.length = 3;
				c.skipFrames = 0;
				AnimationCommand.make(c, atlas, animSet, rpgLayers, rpgSprites, rpgBaseGuideLayers, container, rpg, rpgs);

				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, rpg);
				c.length = 1;
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, none);

				
				c.offset = 50;
				c.length = 20;
				c.delta = dragonWalkDelta;
				AnimationCommand.make(c, atlas, animSet, legLayers, legBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, dragon);
				c.offset = 50;
				c.length = 1;
				AnimationCommand.make(c, atlas, animSet, torsoLayers, torsoBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, dragon);

				c.offset = 55;
				c.length = 3;
				c.delta = 3.5f;
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, dragon);
				
				c.offset = 57;
				c.length = 1;
				AnimationCommand.make(c, atlas, animSet, neckLayers, neckBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, dragon);
				AnimationCommand.make(c, atlas, animSet, headLayers, headBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, dragon);
				
				c.delta = dragonWalkDelta;
				c.deltaMultiplier = .027f;
				c.offset = 59;
				c.length = 2;
				c.randomStart = true;
				
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, dragon);
				
				//AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, dragon, dragons);
				//AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, dragon, dragons);



				isFinished = true;
			}

			@Override
			public void onEnd() {
				parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);
			}

			@Override
			public void onStart() {
			}
			
		};
		workSys.addWorker(walk);
		
		ProgressAction stand = new ProgressAction(){

			@Override
			public void update(float dt) {
				AnimationCommand c = new AnimationCommand();
				c.offset = 90;
				c.length = 39;
				c.animName = "stand";
				c.delta = .0512f;
				c.velocityDependant = true;
				c.loop = true;
				c.bitmask = 2;
				
				AnimationContainer container = new AnimationContainer();
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, neckLayers, neckBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, headLayers, headBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, legLayers, legBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, torsoLayers, torsoBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, player);

				c.offset = 0;
				c.length = 3;
				c.skipFrames = 0;

				AnimationCommand.make(c, atlas, animSet, rpgLayers, rpgSprites, rpgBaseGuideLayers, container, rpg, rpgs);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, rpg);
				c.length = 1;
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, none);

				c.offset = 70;
				c.length = 20;
				c.delta = dragonWalkDelta;
				AnimationCommand.make(c, atlas, animSet, legLayers, legBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, dragon);
				c.offset = 50;
				c.length = 1;
				AnimationCommand.make(c, atlas, animSet, torsoLayers, torsoBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, dragon);
				
				
				c.offset = 60;
				c.length = 1;
				//c.randomStart = true;
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, dragon);
				
				c.offset = 56;
				c.length = 1;
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, dragon);
				
				c.offset = 57;
				AnimationCommand.make(c, atlas, animSet, neckLayers, neckBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, dragon);
				AnimationCommand.make(c, atlas, animSet, headLayers, headBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, dragon);


				isFinished = true;
			}

			@Override
			public void onEnd() {
				parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);

			}

			@Override
			public void onStart() {
			}
			
		};
		workSys.addWorker(stand);
		
		ProgressAction jump = new ProgressAction(){

			@Override
			public void update(float dt) {
				AnimationCommand c = new AnimationCommand();
				c.offset = 130;
				c.length = 39;
				c.animName = "jump";
				//c.randomStart = true;
				c.delta = .0512f;
				c.deltaMultiplier = .3f;
				//c.velocityDependant = true;
				c.loop = true;
				c.bitmask = 2;
				//c.randomStart = true;
				AnimationContainer container = new AnimationContainer();
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, neckLayers, neckBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, headLayers, headBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, legLayers, legBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, torsoLayers, torsoBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, player);

				c.offset = 0;
				c.length = 3;
				c.skipFrames = 0;
				AnimationCommand.make(c, atlas, animSet, rpgLayers, rpgSprites, rpgBaseGuideLayers, container, rpg, rpgs);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, rpg);

				c.length = 1;
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, none);


				c.offset = 90;
				c.length = 20;
				c.delta = dragonWalkDelta;
				AnimationCommand.make(c, atlas, animSet, legLayers, legBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, dragon);
				c.offset = 50;
				c.length = 1;
				AnimationCommand.make(c, atlas, animSet, torsoLayers, torsoBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, dragon);
				
				c.offset = 58;
				c.length = 2;
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, dragon);
				
				c.offset = 52;
				c.length = 2;
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, dragon);
				
				c.offset = 57;
				AnimationCommand.make(c, atlas, animSet, neckLayers, neckBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, dragon);
				AnimationCommand.make(c, atlas, animSet, headLayers, headBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, dragon);


				isFinished = true;
			}

			@Override
			public void onEnd() {
				parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);

			}

			@Override
			public void onStart() {
			}
			
		};
		workSys.addWorker(jump);
		
		
		
		//145-149 fall/pushedup
		ProgressAction fall = new ProgressAction(){

			@Override
			public void update(float dt) {
				
				AnimationCommand c = new AnimationCommand();
				c.offset = 131;
				c.length = 39;
				c.animName = "fall";
				//c.randomStart = true;
				c.delta = .0512f;
				c.deltaMultiplier = .3f;
				//c.velocityDependant = true;
				c.loop = true;
				c.bitmask = 2;
				//c.randomStart = true;
				AnimationContainer container = new AnimationContainer();
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, neckLayers, neckBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, headLayers, headBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, legLayers, legBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, player);
				AnimationCommand.make(c, atlas, animSet, torsoLayers, torsoBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, player);

				c.offset = 0;
				c.length = 3;
				//c.skipFrames = 0;
				AnimationCommand.make(c, atlas, animSet, rpgLayers, rpgSprites, rpgBaseGuideLayers, container, rpg, rpgs);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, rpg);

				c.length = 1;
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, none);



				c.offset = 90;
				c.length = 20;
				c.delta = dragonWalkDelta;
				AnimationCommand.make(c, atlas, animSet, legLayers, legBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, dragon);
				c.offset = 50;
				c.length = 1;
				AnimationCommand.make(c, atlas, animSet, torsoLayers, torsoBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, dragon);
				
				c.offset = 56;
				c.length = 3;
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, dragon);
				
				c.offset = 50;
				c.length = 1;
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, dragon);
				
				c.offset = 57;
				AnimationCommand.make(c, atlas, animSet, neckLayers, neckBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, dragon);
				AnimationCommand.make(c, atlas, animSet, headLayers, headBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, dragon);


				isFinished = true;
			}

			@Override
			public void onEnd() {
				parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);

			}

			@Override
			public void onStart() {
			}
			
		};
		workSys.addWorker(fall);
		
		ProgressAction throwAction = new ProgressAction(){

			private AnimationCommand c;
			private int prog;

			@Override
			public void update(float dt) {
				int progress = prog++;
				//c.randomStart = true;
				//c.angleDependantFlip = true;
				//AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				//AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player, players);
				//5, 14
				
				AnimationContainer container = new AnimationContainer();
				int index = progress+5;
				//c.offset = 125;
				c.length = 13;
				c.animName = "throw"+index;
				//Gdx.app.log(TAG, "throw anim "+index);
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				//////////
				container = new AnimationContainer();
				c.offset += 13;
				c.length = 7;
				c.animName = "throwcooldown"+index;
				//Gdx.app.log(TAG, "throw anim "+index);
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				c.offset -= 13;
				///////////
				if (index <= 9) {
					index -= 4;
					index = 5 - index;
					//Gdx.app.log(TAG, "alt animm "+index);
				} else {
					index = -(index - 9);
					index += 18;
					//Gdx.app.log(TAG, "alt anim "+index);
				}
				container = new AnimationContainer();
				//c.offset = 125;
				c.length = 13;
				c.animName = "throw"+index;
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				//////////////////////
				container = new AnimationContainer();
				c.offset += 13;
				c.length = 7;
				c.animName = "throwcooldown"+index;
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				c.offset -= 13;

				c.offset += 21;
				
				if (prog >= 9) {

					c.offset = 0;
					c.length = 3;
					//c.skipFrames = 0;
					AnimationCommand.make(c, atlas, animSet, rpgLayers, rpgSprites, rpgBaseGuideLayers, container, rpg, rpgs);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, rpg);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, rpg);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, rpg);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, rpg);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, rpg);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, rpg);
					c.length = 1;
					AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, none);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, none);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, none);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, none);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, none);
					AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, none);

					isFinished = true;
				}
			}

			@Override
			public void onEnd() {
				parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);

			}

			@Override
			public void onStart() {
				c = new AnimationCommand();
				c.offset = 175;
				c.length = 13;
				c.animName = "throw0";
				c.delta = .02512f;
				c.deltaMultiplier = 1f;
				prog = 0;
			}
			
		};
		workSys.addWorker(throwAction);
		
		ProgressAction slashAction = new ProgressAction(){

			private AnimationCommand c;
			private int prog;

			@Override
			public void update(float dt) {
				//int progress = prog++;
				//c.randomStart = true;
				//c.angleDependantFlip = true;
				//AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				//AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player, players);
				//5, 14
				
				AnimationContainer container = new AnimationContainer();
				//int index = progress+5;
				//c.offset = 125;
				c.length = 13;
				c.animName = "slash";
				//Gdx.app.log(TAG, "throw anim "+index);
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				//////////
				container = new AnimationContainer();
				c.offset += 13;
				c.length = 7;
				c.animName = "slashcooldown";
				//Gdx.app.log(TAG, "throw anim "+index);
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				c.offset -= 13;

				c.offset = 0;
				c.length = 3;
				c.skipFrames = 0;
				AnimationCommand.make(c, atlas, animSet, rpgLayers, rpgSprites, rpgBaseGuideLayers, container, rpg, rpgs);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, rpg);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, rpg);
				c.length = 1;
				AnimationCommand.makeGuideFrames(c, atlas, animSet, legGuideLayers, legBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, torsoGuideLayers, torsoBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, neckGuideLayers, neckBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, headGuideLayers, headBaseGuideLayers, container, none);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, none);

				//if (prog >= 9) 
					isFinished = true;
				
			}

			@Override
			public void onEnd() {
				parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);

			}

			@Override
			public void onStart() {
				c = new AnimationCommand();
				c.offset = 321;
				c.length = 13;
				c.animName = "slash";
				c.delta = .02512f;
				c.deltaMultiplier = 1f;
				prog = 0;
			}
			
		};
		workSys.addWorker(slashAction);
		
		ProgressAction thrustAction = new ProgressAction(){

			private AnimationCommand c;
			private int prog;

			@Override
			public void update(float dt) {
				//int progress = prog++;
				//c.randomStart = true;
				//c.angleDependantFlip = true;
				//AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				//AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player, players);
				//5, 14
				
				AnimationContainer container = new AnimationContainer();
				//int index = progress+5;
				//c.offset = 125;
				c.length = 13;
				c.animName = "thrust";
				//Gdx.app.log(TAG, "throw anim "+index);
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				//////////
				container = new AnimationContainer();
				c.offset += 13;
				c.length = 7;
				c.animName = "thrustcooldown";
				//Gdx.app.log(TAG, "throw anim "+index);
				AnimationCommand.make(c, atlas, animSet, armLayers, armBaseLayers, container, player, players);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, armGuideLayers, armBaseGuideLayers, container, player);
				c.offset -= 13;
				
				
				//if (prog >= 9) 
					isFinished = true;
				
			}

			@Override
			public void onEnd() {
				parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);

			}

			@Override
			public void onStart() {
				c = new AnimationCommand();
				c.offset = 363;
				c.length = 13;
				c.animName = "thrust";
				c.delta = .02512f;
				c.deltaMultiplier = 1f;
				prog = 0;
			}
			
		};
		workSys.addWorker(thrustAction);
		
		
		
		//tail
		
		ProgressAction tail = new ProgressAction(){

			@Override
			public void update(float dt) {
				AnimationCommand c = new AnimationCommand();
				c.offset = 51;
				c.length = 20;
				c.animName = "tail";
				//c.randomStart = true;
				c.delta = 1f/20f;
				c.deltaMultiplier = 1f;
				//c.velocityDependant = true;
				//c.loop = true;
				//c.bitmask = 0;
				//c.randomStart = true;
				AnimationContainer container = new AnimationContainer();
				AnimationCommand.make(c, atlas, animSet, tailLayers, tailBaseLayers, container, dragon, dragons);
				AnimationCommand.makeGuideFrames(c, atlas, animSet, tailGuideLayers, tailBaseGuideLayers, container, dragon);
				int[] dragonTailDirections = AnimationCommand.makeGuideFramesRotated(c, atlas, "tailtipguide", container, dragon, 0, 180);
				//for (String pre : dragons)
				directions.put(Data.hash(dragon+"tailtipguide"), new IntArray(dragonTailDirections));
				isFinished = true;
			}

			@Override
			public void onEnd() {
				parent.engine.getSystem(ProgressBarSystem.class).deregisterProgressBar(progressBarIndex);

			}

			@Override
			public void onStart() {
			}
			
		};
		workSys.addWorker(tail);

		
		/*c = new AnimationCommand();
		c.offset = 41;
		c.length = 17;
		c.animName = "preroll";
		container = new AnimationContainer();
		AnimationCommand.make(c, atlas, animSet, layers, baseLayers, container, player, players);
		AnimationCommand.makeGuideFrames(c, atlas, animSet, guideLayers, baseGuideLayers, container, player, players);

		
		c = new AnimationCommand();
		c.offset = 58;
		c.length = 40;
		c.animName = "roll";
		container = new AnimationContainer();
		AnimationCommand.make(c, atlas, animSet, layers, baseLayers, container, player, players);
		AnimationCommand.makeGuideFrames(c, atlas, animSet, guideLayers, baseGuideLayers, container, player, players);

		//99-110 die
		c = new AnimationCommand();
		c.offset = 99;
		c.length = 12;
		c.animName = "die";
		container = new AnimationContainer();
		AnimationCommand.make(c, atlas, animSet, layers, baseLayers, container, player, players);
		AnimationCommand.makeGuideFrames(c, atlas, animSet, guideLayers, baseGuideLayers, container, player, players);

		//111-122 die
		c = new AnimationCommand();
		c.offset = 111;
		c.length = 12;
		c.animName = "die";
		container = new AnimationContainer();
		AnimationCommand.make(c, atlas, animSet, layers, baseLayers, container, player, players);
		AnimationCommand.makeGuideFrames(c, atlas, animSet, guideLayers, baseGuideLayers, container, player, players);

		//123-134 die
		c = new AnimationCommand();
		c.offset = 123;
		c.length = 12;
		c.animName = "die";
		container = new AnimationContainer();
		AnimationCommand.make(c, atlas, animSet, layers, baseLayers, container, player, players);
		AnimationCommand.makeGuideFrames(c, atlas, animSet, guideLayers, baseGuideLayers, container, player, players);

		
		c = new AnimationCommand();
		c.offset = 253;
		c.length = 1;
		c.animName = "wallslide";
		c.velocityDependant = true;
		c.loop = true;
		c.bitmask = 2;
		container = new AnimationContainer();
		AnimationCommand.make(c, atlas, animSet, layers, baseLayers, container, player, players);
		AnimationCommand.makeGuideFrames(c, atlas, animSet, guideLayers, baseGuideLayers, container, player, players);		
		
		*/
		
		anim.put(player.hashCode(), animSet);	
		
	}

	
	private static final String PREFIX = "diff/";
	private static void createWeaponSprites(TextureAtlas atlas, String name, int id, String base, String spinBase, String tipBase, int total) {
		
		itemLayers[id] = AnimationCommand.makeItemFrames(name, id, base, total, atlas, processedPlayerTexture);
		itemSprites[id] = (AtlasSprite[]) itemLayers[id].getKeyFrames();
		for (int i = 0; i < itemSprites[id].length; i++){
			AtlasSprite s = itemSprites[id][i];
			AtlasRegion r = s.getAtlasRegion();
			//s.setCenter(r.offsetX + r.packedWidth / 2, r.offsetY);
			s.setOriginCenter();

		}
		itemDrawables[id] = new TextureRegionDrawable(itemSprites[id][0]);
		itemSpinLayers[id] = AnimationCommand.makeItemFrames(name, id, spinBase, total, atlas, processedPlayerTexture);
		itemSpinSprites[id] = (AtlasSprite[]) itemSpinLayers[id].getKeyFrames();
		itemTipLayers[id] = AnimationCommand.makeItemFrames(name, id, tipBase, total, atlas, processedPlayerTexture);
		itemTipSprites[id] = (AtlasSprite[]) itemTipLayers[id].getKeyFrames();
		//weaponDrawables[id] = new TextureRegionDrawable(weaponSprites[id][0]);
	}
	
	
	
	private static void createBlockSprite(TextureAtlas atlas, int id, String base, String spinBase, String tipBase, int blockID) {
		
		itemLayers[id] = AnimationCommand.makeBlockFrames(atlas, blockID, base, processedPlayerTexture);
		itemSprites[id] = (AtlasSprite[]) itemLayers[id].getKeyFrames();
		for (int i = 0; i < itemSprites[id].length; i++){
			AtlasSprite s = itemSprites[id][i];
			AtlasRegion r = s.getAtlasRegion();
			//s.setCenter(r.offsetX + r.packedWidth / 2, r.offsetY);
			s.setOriginCenter();

		}
		itemDrawables[id] = new TextureRegionDrawable(itemSprites[id][0]);
		itemSpinLayers[id] = AnimationCommand.makeBlockFrames(atlas, blockID, spinBase, processedPlayerTexture);
		itemSpinSprites[id] = (AtlasSprite[]) itemSpinLayers[id].getKeyFrames();
		itemTipLayers[id] = AnimationCommand.makeBlockFrames(atlas, blockID, tipBase, processedPlayerTexture);
		itemTipSprites[id] = (AtlasSprite[]) itemTipLayers[id].getKeyFrames();
	}
	private static void createTailSprites(TextureAtlas atlas, String name, int id, String base, int total) {
		
		RotatedAnimationLayer tailLayers;
		tailLayers = AnimationCommand.makeItemFrames(name, id, base, total, atlas, processedPlayerTexture);
		tailSprites[id] = (AtlasSprite[]) tailLayers.getKeyFrames();
		tailDrawables[id] = new TextureRegionDrawable(tailSprites[id][0]);
	}

	private static void createTailSprites(TextureAtlas atlas, String name, int id, int numberOfFrames){
		tailSprites[id] = new AtlasSprite[numberOfFrames];
		for (int i = 0; i < numberOfFrames; i++){
			AtlasSprite s = (AtlasSprite) atlas.createSprite(PREFIX+name, i);
			//s.setSize(1f, 1f);
			if (s == null) throw new GdxRuntimeException(PREFIX+"sprite doesn't exist "+name);
			
			tailSprites[id][i] = s;
			
			
			
		}	
	}
	
	public static void dispose(){
		for (int i = 0; i < anim.size; i++){
			
		}
	}

}
