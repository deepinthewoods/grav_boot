package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SpriteStatic implements Component, KryoSerializable, Poolable {
	public transient Sprite s;
	public boolean left;
	@Override
	public void write(Kryo kryo, Output output) {
		output.writeFloat(s.getU());
		output.writeFloat(s.getV());
		output.writeFloat(s.getU2());
		output.writeFloat(s.getV2());
		output.writeFloat(s.getWidth());
		output.writeFloat(s.getHeight());
		output.writeBoolean(left);
	}
	
	@Override
	public void read(Kryo kryo, Input input) {
		s = Pools.obtain(Sprite.class);
		s.setU(input.readFloat());
		s.setV(input.readFloat());
		s.setU2(input.readFloat());
		s.setV2(input.readFloat());
		s.setSize(input.readFloat(), input.readFloat());
		left = input.readBoolean();
	}
	
	@Override
	public void reset() {
		Pools.get(Sprite.class).free(s);
		s = null;
		left = false;
	}
	
}
