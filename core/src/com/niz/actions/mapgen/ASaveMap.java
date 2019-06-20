package com.niz.actions.mapgen;

import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.niz.WorldDefinition;
import com.niz.action.Action;
import com.niz.action.ProgressAction;
import com.niz.component.Map;
import com.niz.system.OverworldSystem;
import com.niz.system.ProgressBarSystem;

public class ASaveMap extends ProgressAction {
	protected static final int BLOCKS_PER_RUN = 8*4096;

	private static final String TAG = "Save Map Action";

	public Map map = null;
	int progress = 0, progressCoarse = 0;;
	private FileHandle file;
	private DataOutputStream stream;
	private int progressTarget;
	
	
	

	public WorldDefinition def;

	private OverworldSystem overworld;

	public int bit;

	@Override
	public void update(float dt) {

		//Gdx.app.log(TAG, "tick "+progress + " " + bit);
		//Gdx.app.log(TAG, "tick"+progress + "  " + progressCoarse);

		switch (progressCoarse){
		case 0:
			try {
				for (int i = 0; i < BLOCKS_PER_RUN; i++){
						stream.writeInt(map.tiles[progress]);
						//if (map.tiles[progress] != 0)
						//	Gdx.app.log(TAG, "write"+map.tiles[progress]);
						progress++;
						if (progress == progressTarget){
							progress = 0;
							progressCoarse++;
							progressTarget = map.backTiles.length;
							return;
						}
				} 
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			break;
		case 1:
			try {
				for (int i = 0; i < BLOCKS_PER_RUN; i++){
					//Gdx.app.log(TAG, "write an int");
					stream.writeInt(map.backTiles[progress]);
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
						
						//Gdx.app.log(TAG, "isFinished=");
						isFinished = true;
						return;
					}
				} 
			} catch (IOException e) {
				e.printStackTrace();
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
		overworld.onFinishedSave(map, bit);
		//Gdx.app.log(TAG, "end"+isFinished);
		((ProgressAction)after).progressBarIndex = progressBarIndex;
		addAfterMe(after);
	}

	@Override
	public void onStart() {
		this.overworld = parent.engine.getSystem(OverworldSystem.class);
		progress = 0;
		progressCoarse = 0;
		//Gdx.app.log(TAG, "start"+map.offset);
		file = def.folder.child("map"+z+"-"+bit);
		stream = new DataOutputStream(file.write(false));
		
		progressTarget = map.tiles.length;
		progressSys = parent.engine.getSystem(ProgressBarSystem.class);

	}

}
