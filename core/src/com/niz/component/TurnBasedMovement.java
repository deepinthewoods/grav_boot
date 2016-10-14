package com.niz.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.niz.TurnAction;

public class TurnBasedMovement implements Component {
public transient Array<TurnAction> moves = new Array<TurnAction>();
}
