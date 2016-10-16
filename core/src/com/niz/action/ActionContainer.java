package com.niz.action;

import com.badlogic.gdx.utils.Array;

/**
 * Created by niz on 07/06/2014.
 */
public class ActionContainer extends Array<Action> {
    public transient final ArrayIterator<Action> iter;
    protected transient ActionList parentList;
    public ActionContainer() {
        super(true, 8);

        iter = new ArrayIterator<Action>(this){
        	public void prev(){
        		
        	}
        };
    }

    public ArrayIterator<Action> iter() {
        iter.reset();
        return iter;
    }
    public void init(ActionList actionList){
        parentList = actionList;
    }

}
