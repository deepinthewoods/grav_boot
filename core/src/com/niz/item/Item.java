package com.niz.item;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.niz.component.Inventory;

public class Item implements Poolable, Component{
	public int count;
	public float durability = 1f;
	public int id;
	public Array<ItemModifier> modifiers = new Array<ItemModifier>();
	public int hash;
	public float destroyBlockBaseAlpha = 1f;

	public ItemDef getDef(){
		return Inventory.defs.get(id);
	}
	
	@Override
	public void reset() {
		count = 0;
		id = 0;
		for (;modifiers.size > 0;){
			Pools.free(modifiers.pop());
		}
		
		
	}
	
	public void set(int id, int count, float dur) {
		this.id = id;
		this.count = count;
		this.durability = dur;
	}

	
	/**
	 * removes and returns one of this item. for throwing.
	 * @return a new item
	 */
	public Item separateOne() {
		count--;
		if (count < 0	) throw new GdxRuntimeException("count is already 0");
		Item i = Pools.obtain(Item.class);
		i.count = 1;
		i.durability = durability;
		i.id = id;
		for (int n = 0; n < modifiers.size; n++){
			i.modifiers.add(modifiers.get(n));
		}
		return i;
	}

	/** tries to stack onto self
	 * @param item 
	 * @return true if it has stacked
	 */
	public boolean stack(Item item) {
		if (canStack(item)){
			count += item.count;
			item.count = 0;
			return true;
		}
		return false;
	}
	
	public boolean canStack(Item item) {
		if (item.id == id){
			
			return true;
		}
		return false;
	}
}
