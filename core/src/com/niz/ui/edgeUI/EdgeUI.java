package com.niz.ui.edgeUI;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 26/05/2014.
 */
public class EdgeUI{
    private static final String TAG = "EdgeUI";
    public transient Table table = new Table(), disablerTable = new Table();
    transient UITable middleScreen = null;
    transient Stage stage;
    public UITable[] sides = new UITable[9];
    public UIElement back;
    private transient boolean touchDisabled = false;
    protected boolean[] expandX = {false, false, false, false, false, false, false, false, false};
    protected boolean[] expandY = {false, false, false, false, true, false, false, false, false};
    Table backTable = new Table();
	public boolean colspanTop;
    
    public EdgeUI(){
        table.setFillParent(true);
        disablerTable.setFillParent(true);
        disablerTable.setTouchable(Touchable.enabled);
        disablerTable.addListener(new InputListener(){
            @Override
            public boolean handle(Event e) {
                Gdx.app.log(TAG, "disabled");
                return super.handle(e);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(TAG, "disabled");return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(TAG, "disabled");
                event.cancel();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                Gdx.app.log(TAG, "disabled");
                event.cancel();
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log(TAG, "disabled");
                event.cancel();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.app.log(TAG, "disabled");
                event.cancel();
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }

            @Override
            public boolean keyTyped(InputEvent event, char character) {
                Gdx.app.log(TAG, "disabled");
                return true;
            }
        });
    }
    
    public void create(Skin skin, Stage stage, EngineNiz engine){
    	for (int y = 0, i = 0; y < 3; y++){
            for (int x = 0; x < 3; x++, i++){
                if (sides[i] != null){
                	//Gdx.app.log(TAG,  "init "+i);;
                    sides[i].init(skin, this, engine);
                    
                }
            }
    	}
        back.init(skin, null, engine);

    }

    public void init(Skin skin, Stage stage, EngineNiz engine){
    	
        stage.clear();
        for (int y = 0, i = 0; y < 3; y++){
            for (int x = 0; x < 3; x++, i++){
                if (sides[i] != null){
                    //sides[i].init(skin, this, engine);

                    if (colspanTop && i == 1){
                    	sides[i].addTo(i, this, expandX[i], expandY[i], true);
                    } 
                    else
                    	sides[i].addTo(i, this, expandX[i], expandY[i], false);
                    //sides[i].addTo(this, i==4?false:false);

                    //Gdx.app.log(TAG, "face");
                }
            }
            table.row();
        }
        //table.layout();
        this.stage = stage;
        //for screen touches
        backTable.clear();
        backTable.setFillParent(true);
        back.addTo(backTable);

        //s//tage.addActor(backTable);

        //stage.addActor(table);
    }
    
    
    
    public void setMiddleScreen(UITable table){
        if (middleScreen != null){
            middleScreen.onMinimize();
        }
        sides[4].table.clear();
        table.maximizeTo(sides[4].table);
        table.onMaximize();
    }

    public void unsetMiddleScreen() {
        if (middleScreen != null){
            middleScreen.onMinimize();
        }
        middleScreen = null;
        sides[4].table.clear();
    }

    public void disableTouches() {
        if (!touchDisabled){
            touchDisabled = true;
            stage.addActor(disablerTable);
        }
    }

    public void enableTouches(){
        if (touchDisabled){
            touchDisabled = false;
            stage.getActors().removeValue(disablerTable, true);
        }
    }

    public void addTo(Stage stage) {
		stage.addActor(backTable);
		stage.addActor(table);
	}
    
    public Table getTable() {
		
		return table;
	}
 
    
}
