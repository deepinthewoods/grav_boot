package com.niz.component;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.IntMap.Values;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.niz.Blocks;
import com.niz.item.BlockPlaceDef;
import com.niz.item.Doing;
import com.niz.item.Item;
import com.niz.item.ItemDef;
import com.niz.item.ItemModifier;
import com.niz.ui.edgeUI.InventoryScreen;

public class Inventory implements Component, Poolable, KryoSerializable{
	private static final String TAG = "inv";
	public static IntMap<ItemDef> defs = new IntMap<ItemDef>();
	public IntMap<Item> items = new IntMap<Item>();
	private int[] primaryItem = new int[Race.TOTAL_BODY_PARTS],
	primaryDoing = new int[Race.TOTAL_BODY_PARTS];
	
	public boolean dirty;
	public int activeLimb;
	public boolean dirtyLimbs;
	public static Item defaultItem;
	public static int nextHash = 10;
	@Override
	public void write(Kryo kryo, Output output) {
		output.writeInt(primaryItem.length);
		for (int i = 0; i < primaryItem.length; i++){
			output.writeInt(primaryItem[i]);
		}
		output.writeInt(items.size);
		Iterator<Entry<Item>> i = items.iterator();
		while (i.hasNext()){
			Entry<Item> entry = i.next();
			Item it = entry.value;
			output.writeInt(entry.key);
			
			output.writeInt(it.id);
			output.writeInt(it.count);
			output.writeFloat(it.durability);			
			
			output.writeInt(it.modifiers.size);
			for (int c = 0; c < it.modifiers.size; c++){
				kryo.writeClassAndObject(output, it.modifiers.get(c));
			}
			
			
		}
	}

	@Override
	public void read(Kryo kryo, Input input) {
		int primaryItemsLength = input.readInt();
		if (primaryItem.length != primaryItemsLength){
			primaryItem = new int[primaryItemsLength];
		}
		for (int primaryI = 0; primaryI  < primaryItemsLength; primaryI++){
			primaryItem[primaryI] = input.readInt();
		}
		int itemsLength = input.readInt();
		for (int itemIndex = 0; itemIndex < itemsLength; itemIndex++){
			Item i = Pools.obtain(Item.class);
			int key = input.readInt();;
			i.set(input.readInt(), input.readInt(), input.readFloat());
			i.hash = key;
			int modTotal = input.readInt();
			for (int modCount = 0; modCount < modTotal; modCount++){
				ItemModifier mod = (ItemModifier) kryo.readClassAndObject(input);
			}
			addItem(i);
		}
	}
	
	public Inventory(){
		for (int i = 0; i < InventoryScreen.BELT_SLOTS; i++){
			//Item item = Pools.obtain(Item.class);
			//item.id = 1;
			//items.add(item);
		}
	}

	public void addItem(Item item){
		//Gdx.app.log(TAG, "add item"+item.id);
		dirty = true;
		if (item.count < 0) throw new GdxRuntimeException("item amount cannont be < 0");
		if (item.modifiers.size > 0){
			items.put(item.hash, item);
			//Gdx.app.log(TAG, "add item with modifiers");
			return;
		}
		Iterator<Entry<Item>> i = items.iterator();
		while (i.hasNext()){
			Entry<Item> ent = i.next();
			Item val = ent.value;
			if (val.stack(item)){
				Pools.free(item);
				return;
			}
			//if (val.id == item.id){
				//val.count += item.count;
				//Pools.free(item);
				//Gdx.app.log(TAG, "add item added to stack");
				//return;
			//}
		}
		items.put(item.hash, item);;
		//Gdx.app.log(TAG, "add item");
	}
	public void update() {
		Iterator<Entry<Item>> i = items.iterator();
		while (i.hasNext()){
			Entry<Item> ent = i.next();
			Item val = ent.value;
			//Gdx.app.log(TAG, "item "+val.getDef().name+val.count);
			if (val.count <= 0) {
				//Gdx.app.log(TAG, "remove item");
				i.remove();
				Pools.free(val);
				continue;
			}
			if (val.durability < 0f){
				i.remove();
				Pools.free(val);
				continue;
			}
			
			
		}
		
	}
	public Item getActiveItem(int limb) {
		//if (items.size <= primaryItem) return defaultItem;
		Item i = items.get(primaryItem[limb]);
		if (i == null) return defaultItem;//throw new GdxRuntimeException("null "+items);;
		return i;
	}
	
	public void setActiveItem(int itemHash, int doingIndex){
		Item item = this.getItem(itemHash);
		if (item == null) item = defaultItem;
		ItemDef def = item.getDef();
		
		if (doingIndex < def.doings.size){
			
			int limb = def.doings.get(doingIndex).limbIndex;
			for (int i = 0; i < primaryItem.length; i++){
				if (primaryItem[i] == itemHash){
					primaryItem[i] = 0;
					primaryDoing[i] = 0;
					//Gdx.app.log(TAG, "change active item DUPOEDUPEDUPEDUPDUPDUPDUDP "+i);;
					
				}
			}
			primaryItem[limb] = itemHash;
			primaryDoing[limb] = doingIndex;
			activeLimb = limb;
			//Gdx.app.log(TAG, "change active item"+primaryItem[limb]);;
		}
		//if (itemHash == 0) throw new GdxRuntimeException("null "+primaryItem);;
	}

	private static Vector2 tmpV = new Vector2();
	public static void initItemDefs(){
		defaultItem = new Item();
		defaultItem.count = 10000000;
		defaultItem.durability = 1f;
		defaultItem.id = 0;
		
		
		ItemDef defaultDef = new ItemDef("(nothing)"){

			
		};
		
		Doing defaultDoing = new Doing("l");;
		defaultDoing.doingTypeIndex = Doing.TYPE_NOTHING;
		
		defaultDef.doings.add(defaultDoing );
		
		
		defs.put(0, defaultDef);
		
		
		ItemDef sword = new ItemDef("sword"){};
		sword.id = 16;		
		sword.doings.add(new Doing("throw (back hand)", Race.LIMB_BACK_HAND, Doing.TYPE_THROW));
		sword.doings.add(new Doing("throw (front hand", Race.LIMB_FRONT_HAND, Doing.TYPE_THROW));
		sword.doings.add(new Doing("throw (tail)", Race.LIMB_TAIL, Doing.TYPE_THROW));
		sword.doings.add(new Doing("slash", Race.BACK_ARM, Doing.TYPE_SLASH));
		defs.put(sword.id,  sword);
		
		ItemDef axe = new ItemDef("Axe"){};
		axe.id = 17;		
		axe.doings.add(new Doing("throw (back hand)", Race.LIMB_BACK_HAND, Doing.TYPE_THROW));
		axe.doings.add(new Doing("throw (front hand", Race.LIMB_FRONT_HAND, Doing.TYPE_THROW));
		axe.doings.add(new Doing("throw (tail)", Race.LIMB_TAIL, Doing.TYPE_THROW));
		defs.put(axe.id,  axe);
		
		ItemDef scimitar = new ItemDef("Scimitar"){};
		scimitar.id = 18;		
		scimitar.doings.add(new Doing("throw (back hand)", Race.LIMB_BACK_HAND, Doing.TYPE_THROW));
		scimitar.doings.add(new Doing("throw (front hand", Race.LIMB_FRONT_HAND, Doing.TYPE_THROW));
		scimitar.doings.add(new Doing("throw (tail)", Race.LIMB_TAIL, Doing.TYPE_THROW));
		defs.put(scimitar.id,  scimitar);
		
		ItemDef longsword = new ItemDef("Longsword"){};
		longsword.id = 19;		
		longsword.doings.add(new Doing("throw (back hand)", Race.LIMB_BACK_HAND, Doing.TYPE_THROW));
		longsword.doings.add(new Doing("throw (front hand", Race.LIMB_FRONT_HAND, Doing.TYPE_THROW));
		longsword.doings.add(new Doing("throw (tail)", Race.LIMB_TAIL, Doing.TYPE_THROW));
		defs.put(longsword.id,  longsword);
		
		ItemDef shortsword = new ItemDef("Shortsword"){};
		shortsword.id = 20;		
		shortsword.doings.add(new Doing("throw (back hand)", Race.LIMB_BACK_HAND, Doing.TYPE_THROW));
		shortsword.doings.add(new Doing("throw (front hand", Race.LIMB_FRONT_HAND, Doing.TYPE_THROW));
		shortsword.doings.add(new Doing("throw (tail)", Race.LIMB_TAIL, Doing.TYPE_THROW));
		defs.put(shortsword.id,  shortsword);
		
		ItemDef gun = new ItemDef("Gun"){};
		gun.id = 21;		
		gun.doings.add(new Doing("throw (back hand)", Race.LIMB_BACK_HAND, Doing.TYPE_THROW));
		gun.doings.add(new Doing("throw (front hand", Race.LIMB_FRONT_HAND, Doing.TYPE_THROW));
		gun.doings.add(new Doing("throw (tail)", Race.LIMB_TAIL, Doing.TYPE_THROW));
		defs.put(gun.id,  gun);
		
		ItemDef pickaxe = new ItemDef("Pickaxe"){};
		pickaxe.id = 22;		
		pickaxe.doings.add(new Doing("destroy(back hand)", Race.LIMB_BACK_HAND, Doing.TYPE_DESTROY, Doing.ARROW_PLACE));
		pickaxe.doings.add(new Doing("destroy(front hand)", Race.LIMB_FRONT_HAND, Doing.TYPE_DESTROY, Doing.ARROW_PLACE));
		pickaxe.doings.add(new Doing("destroy(tail)", Race.LIMB_TAIL, Doing.TYPE_DESTROY, Doing.ARROW_PLACE));
		pickaxe.doings.add(new Doing("destroy(mouth)", Race.LIMB_HEAD, Doing.TYPE_DESTROY, Doing.ARROW_PLACE));

		defs.put(pickaxe.id,  pickaxe);
		
		
		for (int i = 0; i < 8; i++){
			ItemDef blockParticle = new ItemDef("Pebble"){};
			blockParticle.id = 108+i;
			defs.put(blockParticle.id, blockParticle);
		}
		
		for (int i = 0; i < 8; i++){
			ItemDef blockParticle = new ItemDef("Dirt Clump"){};
			blockParticle.id = 100+i;
			defs.put(blockParticle.id, blockParticle);
		}
		
		
		//slope
		
		
		for (int i = 0; i < 8; i++){
			BlockPlaceDef slope = new BlockPlaceDef("Dirt Slope");
			slope.id = 24+i;
			slope.blockID = Blocks.SLOPE + i*64;
			defs.put(slope.id, slope);
		}
		
		for (int i = 0; i < 8; i++){
			
			BlockPlaceDef item = new BlockPlaceDef("Stone Block");
			item.id = i+32;
			item.blockID = Blocks.STONE + i*64;
			item.random = 8;
			defs.put(item.id, item);
		}
		
		for (int i = 0; i < 8; i++){
			
			BlockPlaceDef item = new BlockPlaceDef("Dirt Block");
			item.id = i+40;
			item.blockID = Blocks.DIRT + i*64;
			item.random = 8;
			defs.put(item.id, item);
		}
		
		BlockPlaceDef water = new BlockPlaceDef("Water Block");
		water.id = 14;
		water.blockID = Blocks.WATER;
		water.random = 0;
		defs.put(water.id, water);
		
		BlockPlaceDef lava = new BlockPlaceDef("Lava Block");
		lava.id = 15;
		lava.blockID = Blocks.LAVA+12;
		lava.random = 0;
		defs.put(lava.id, lava);
		
		
		
	}
	
	public void addItem(ItemDef itemDef, int i) {
		//Gdx.app.log(TAG, "not null"+items);;

		Item item = Pools.obtain(Item.class);
		
		item.count = i;
		item.id = itemDef.id;
		item.hash = nextHash();
		addItem(item);
		//Gdx.app.log(TAG, "added"+items);;

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public Item getItem(int hash) {
		return items.get(hash);
	}

	public Doing getActiveDoing(int limb) {
		//Gdx.app.log(TAG, "active doing"+primaryDoing + "  "+primaryItem +
		//		"  "+ getActiveItem().getDef().doings.size);
		return getActiveItem(limb).getDef().doings.get(primaryDoing[limb]);
	}

	public int getPrimaryItemKey(int limb) {
		
		return primaryItem[limb];
	}

	public int getItemIDByLimb(int limb) {
		
		Item item = getItem(primaryItem[limb]);
		if (item == null) return 0;
		int id = item.getDef().id;
		//Gdx.app.log(TAG,  "idIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIDIIDIDIDIDIIDIDIIDIDIIDI "+id);
		//id = 1;
		return id;
	}

	public void addItem(int itemID, int i) {
		addItem(defs.get(itemID), i);
	}

	public static int nextHash() {
		
		return nextHash++;
		
	}

	public void copyFrom(Inventory inventory) {
		Values<Item> re = inventory.items.values();
		
		while (re.hasNext){
			Item item = re.next();
			addItem(item.getDef(), item.count);
		}
		
	}

	

}


