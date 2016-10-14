package com.niz.actions.mapgen;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.niz.WorldDefinition;
import com.niz.action.Action;
import com.niz.action.ProgressAction;
import com.niz.component.Map;
import com.niz.system.OverworldSystem;
import com.niz.system.ProgressBarSystem;

public class ALoadMap extends ProgressAction {
	protected static final int BLOCKS_PER_RUN = 128;

	private static final String TAG = "Load Map Action";
	public int bit;
	public Map map = null;
	int progress = 0, progressCoarse = 0;;
	private FileHandle file;
	private DataInputStream stream;
	private int progressTarget;
	public WorldDefinition def;

	private OverworldSystem overworld;

	@Override
	public void update(float dt) {

		//Gdx.app.log(TAG, "tick"+progress + "  " + progressCoarse);

		switch (progressCoarse){
		case 0:
			try {
				for (int i = 0; i < BLOCKS_PER_RUN; i++){
					map.tiles[progress] = stream.readInt();
					//if (map.tiles[progress] != 0)Gdx.app.log(TAG, "read"+map.tiles[progress]);

					//Gdx.app.log(TAG, "read"+map.tiles[progress]);
					//stream.writeInt(map.tiles[progress]);
					progress++;
					if (progress == progressTarget){
						progress = 0;
						progressCoarse++;
						progressTarget = map.backTiles.length;
						return;
					}
				} 
			} catch (EOFException ex){
				Gdx.app.log(TAG,  "Eception "+ex.getClass() + ex.toString());
				throw new GdxRuntimeException("ex");
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case 1:
			try {
				for (int i = 0; i < BLOCKS_PER_RUN; i++){
					//Gdx.app.log(TAG, "write an int");
					//stream.writeInt(map.backTiles[progress]);
					map.backTiles[progress] = stream.readInt();
					progress++;
					if (progress == progressTarget){
						
						progress = 0;
						progressCoarse++;
						//progressTarget = map. / BLOCKS_PER_RUN;
						try {
							stream.close();
							//if (true) throw new GdxRuntimeException("jkls");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//Pools.free(map);
						//Gdx.app.log(TAG, "isFinished=");
						isFinished = true;
						return;
					}
				} 
			} catch (EOFException ex){
				Gdx.app.log(TAG,  "Eception "+ex.getClass() + ex.toString());
				throw new GdxRuntimeException("ex");
			} catch (IOException e) {
				Gdx.app.log(TAG,  "Eception "+e.getClass() + e.toString());
				throw new GdxRuntimeException("ex");
				//e.printStackTrace();
			}
			
			
			break;
		case 2:
			
		
		
		}
		
		float progressDelta = progressCoarse * progressTarget + progress;
		progressDelta /= (float)progressTarget*2;
		
		progressSys.setProgressBar(progressBarIndex, progressDelta * .5f );
	
	}
	public Action after;

	private ProgressBarSystem progressSys;

	public int z;
	@Override
	public void onEnd() {
		overworld.onFinishedMap(bit, map);
//		Gdx.app.log(TAG, "end"+isFinished);
		//Gdx.app.log(TAG, "end"+map.offset + " " + bit);
		((ProgressAction)after).progressBarIndex = progressBarIndex;
		addAfterMe(after);
	}

	

	@Override
	public void onStart() {
		//Gdx.app.log(TAG, "start"+map.offset + " " + bit);

		this.overworld = parent.engine.getSystem(OverworldSystem.class);
		progress = 0;
		progressCoarse = 0;
		//Gdx.app.log(TAG, "start"+map.offset);
		file = def.folder.child("map"+z+"-"+bit);
		stream = new DataInputStream(file.read());
		progressTarget = map.tiles.length;
		progressSys = parent.engine.getSystem(ProgressBarSystem.class);

	}

}
