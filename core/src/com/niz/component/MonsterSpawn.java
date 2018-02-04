package com.niz.component;

import com.badlogic.ashley.core.Component;

public class MonsterSpawn implements Component {
    public static final int SMALL = 0, MEDIUM = 1, LARGE = 2, MINOR_BOSS = 3, MAJOR_BOSS = 4;
    public int type;
}
