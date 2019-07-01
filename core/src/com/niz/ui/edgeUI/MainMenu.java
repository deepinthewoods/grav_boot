package com.niz.ui.edgeUI;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.niz.GameInstance;
import com.niz.component.Inventory;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.ui.elements.BackgroundDrag;
import com.niz.ui.elements.ItemDisplay;
import com.niz.ui.elements.UIElement;

/**
 * Created by niz on 27/05/2014.
 */
public class MainMenu extends EdgeUI implements Observer {

    private final ItemDisplay itemDisplay;
    private final MainMenuTable menuTable;

    public MainMenu(GameInstance game, Skin skin){
        sides[0] = new UITable();
        sides[0].min = new UIElement[1];
        //sides[0].min[0] = new ControllerPad();
        table.row();

        sides[1] = new UITable();
        sides[1].min = new UIElement[1];
        itemDisplay = new ItemDisplay(null, game.engine);
        itemDisplay.disableTouch();

        //sides[1].max = new UIElement[1];
        table.row();

        game.engine.getSubject("playerselect").add(this);

        sides[2] = new UITable();
        sides[2].min = new UIElement[1];
        //sides[2].min[0] = new ControllerPad();
        table.row();

        sides[3] = new UITable();
        //sides[3].vertical = true;
        sides[3].min = new UIElement[1];
       // sides[3].min[0] = new BlockSelector();
       // sides[3].max = new UIElement[1];
        //sides[3].max[0] = new BlockColorSelector();
        table.row();

        sides[4] = new UITable();
        sides[4].min = new UIElement[1];
        //expandY[1] = true;
        //sides[4].min[0] = new ControllerPad();
        sides[0].min[0] = itemDisplay;
        table.row();

        sides[5] = new UITable();
        sides[5].min = new UIElement[1];
        //sides[5].min[0] = new ControllerPad();
        table.row();

        sides[6] = new UITable();
        sides[6].min = new UIElement[1];
        //sides[6].min[0] = new ChangeWorldTable();
        //sides[6].table.getCells().get(0).expand();
        table.row();

        sides[7] = new UITable();
        sides[7].min = new UIElement[1];
        //sides[4].min[0]

        menuTable        = new MainMenuTable(this, game, skin);
        //menuTable.onInit(skin);
        sides[5].min[0] = menuTable;
        expandX[5] = true;
        expandY[5] = true;
       // sides[7].min[0] = new ControllerButton("T", 0);
        table.row();

        sides[8] = new UITable();
        sides[8].min = new UIElement[1];
        //ButtonPad btnPad = new ButtonPad();
        //btnPad.send = new String[]{"screen"};
        //sides[8].min[0] = btnPad;
        
        table.row();

        /*for (int y = 0, i = 0; y < 3; y++){
            for (int x = 0; x < 3; x++, i++){
                sides[i] = new UITable();
                sides[i].min = new UIElement[1];
                sides[i].min[0] = new ControllerSlider();
            }
            table.row();
        }*/
        back = new BackgroundDrag();

    }

    @Override
    public void onNotify(Entity e, Subject.Event event, Object c) {
        itemDisplay.setFor(e.getComponent(Inventory.class), e);
    }

    @Override
    public void addTo(Stage stage) {
        super.addTo(stage);
        //stage.addActor(menuTable.getTable());
        //menuTable.getTable().setSize(500, 500);
    }
}
