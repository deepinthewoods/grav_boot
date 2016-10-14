package com.niz;

import com.niz.component.Map;

public class Blocks {

	public static final int AIR = 0, STONE = 2 << Map.ID_BITS, DIRT = 1 << Map.ID_BITS, GRASS = 3 << Map.ID_BITS;
	
	public static final int SLOPE = 4<<Map.ID_BITS;

	public static final int WATER = 5<<Map.ID_BITS, LAVA = 6 << Map.ID_BITS, FIRE = 7 << Map.ID_BITS, FOAM = 8 << Map.ID_BITS;

	public static final int LAVA_LIQUID_ID = 0, WATER_LIQUID_ID = 1;

	public static final int SLOPE_WITH_LIQUID = 9 << Map.ID_BITS;

	public static final int OIL = 10 << Map.ID_BITS;
	
	//public static final int Door = 16 << Map.ID_BITS;


}
