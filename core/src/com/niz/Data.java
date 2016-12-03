package com.niz;

import java.util.Iterator;

import com.badlogic.ashley.core.EngineNiz.PooledEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

public class Data {
	private static IntMap<String> strings = new IntMap<String>();
	
	public static final String FILE_PATH_PREFIX = "DungeonPunch";

	public static final String WORLD_MAIN_FILE_NAME = "world.data";

	public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);;
	
	public static int BLACK_INDEX = 21 
	, VERY_DARK_GREY_INDEX = 1 , VERY_DARK_BROWN_INDEX = 2 
	, MED_DARK_BROWN_INDEX = 3 , DARK_BROWN_INDEX = 4 
	, ORANGE_INDEX = 5 , DARK_TAN_INDEX = 6 
	, TAN_INDEX = 7 , YELLOW_INDEX = 8
	, BRIGHT_GREEN_INDEX = 9 , GREEN_INDEX = 10 
	, TURQUOISE_INDEX = 11 , DARK_OLIVE_INDEX = 12 
	, DARK_GREYISH_BROWN_INDEX = 13 , DARK_OLIVE_BROWN_INDEX = 14 
	, DARK_PURPLE_INDEX = 15 , DARK_TURQUOISE_INDEX = 16 
	, TURQUIOSE_INDEX = 17 , BLUE_INDEX = 18 
	, DARK_CYAN_INDEX = 19, CYAN_INDEX = 20 , LIGHT_GREY_INDEX = 21 
	, WHITE_INDEX = 0 //, //MEDIUM_GREY_INDEX = 22 
	, REDDISH_GREY_INDEX = 22, BROWN_INDEX = 23, PURPLE_INDEX = 24 
	
	, RED_INDEX = 27 , LIGHT_RED_INDEX = 26 
	, PINK_INDEX = 25, VERY_DARK_BLUEISH_GREY_INDEX = 28, LIGHT_BLUEISH_GREY_INDEX = 29, MEDIUM_GREY_INDEX = 30, DARK_GREY_INDEX = 31;
	
	public static float[] colorFloats = new float[32];
	public static Color[] colors = new Color[32];
	public static Json json;

	public static int[] colorInts = new int[32];

	
	public static int hash(String s){
		int key = s.hashCode();
		//if (s.equals("inventoryToggle"))
		
			//throw new GdxRuntimeException("");
		strings.put(key, s);
		return key;
	}
	
	public static String getString(int hash){
		return strings.get(hash, "[none]");
	}
	
	public static void init(){
		Pixmap pix = new Pixmap(Gdx.files.internal("allpal.png"));
		Color c = new Color();
		for (int x = 0; x < 8; x++)
		for (int y = 0; y < 4; y++){
			int i = pix.getPixel(x, y);
			c.set(i);
			colorInts[x+y*8] = i;
			colorFloats[x+y*8] = c.toFloatBits();
			colors[x+y*8] = new Color(c);
		}
		json = new Json();
		/*json.setSerializer(IntMap.class, new Json.Serializer<IntMap>() {

			@Override
			public void write(Json json, IntMap object, Class knownType) {
				json.writeObjectStart();
				json.writeArrayStart();
				Iterator iter = object.iterator();
				while (iter.hasNext()){
					Object v = iter.next();
					json.writeValue(v);
					json.writeObjectEnd();
				}
				
			}

			@Override
			public IntMap read(Json json, JsonValue jsonData, Class type) {
				IntMap ret = new IntMap();
				while (jsonData.h)
				return ret;
			}

			

			
		});//*/
		json.setSerializer(IntMap.class, new IntMapSerializer());
	}
	
	
	public static KryoFactory factory = new KryoFactory() {
		  public Kryo create () {
		    Kryo kryo = new Kryo();
		    kryo.register(IntArray.class, new Serializer<IntArray>() {
		        {
		            setAcceptsNull(true);
		        }

		        public void write (Kryo kryo, Output output, IntArray array) {
		            int length = array.size;
		            output.writeInt(length, true);
		            if (length == 0) return;
		            for (int i = 0, n = array.size; i < n; i++)
		                output.writeInt(array.get(i), true);
		        }

		        public IntArray read (Kryo kryo, com.esotericsoftware.kryo.io.Input input, Class<IntArray> type) {
		            IntArray array = new IntArray();
		            kryo.reference(array);
		            int length = input.readInt(true);
		            array.ensureCapacity(length);
		            for (int i = 0; i < length; i++)
		                array.add(input.readInt(true));
		            return array;
		        }

				
		    });

		    kryo.register(FloatArray.class, new Serializer<FloatArray>() {
		        {
		            setAcceptsNull(true);
		        }

		        public void write (Kryo kryo, Output output, FloatArray array) {
		            int length = array.size;
		            output.writeInt(length, true);
		            if (length == 0) return;
		            for (int i = 0, n = array.size; i < n; i++)
		                output.writeFloat(array.get(i));
		        }

		        public FloatArray read (Kryo kryo, com.esotericsoftware.kryo.io.Input input, Class<FloatArray> type) {
		            FloatArray array = new FloatArray();
		            kryo.reference(array);
		            int length = input.readInt(true);
		            array.ensureCapacity(length);
		            for (int i = 0; i < length; i++)
		                array.add(input.readFloat());
		            return array;
		        }
		    });

		    kryo.register(Color.class, new Serializer<Color>() {
		        public Color read (Kryo kryo, com.esotericsoftware.kryo.io.Input input, Class<Color> type) {
		            Color color = new Color();
		            Color.rgba8888ToColor(color, input.readInt());
		            return color;
		        }

		        public void write (Kryo kryo, Output output, Color color) {
		            output.writeInt(Color.rgba8888(color));
		        }
		    });
		    // configure kryo instance, customize settings
		    //kryo.addDefaultSerializer(ActionList.class, ActionList.Serializer.class);
		    return kryo;
		  }
		};
		// Build pool with SoftReferences enabled (optional)
	public static KryoPool kryoPool = new KryoPool.Builder(factory).softReferences().build();
	
	
	public static Pool<Array<PooledEntity>> entityArrayPool = new Pool<Array<PooledEntity>>(){

		@Override
		protected Array<PooledEntity> newObject() {
			
			return new Array<PooledEntity>();
		}
		
	};
	
}
