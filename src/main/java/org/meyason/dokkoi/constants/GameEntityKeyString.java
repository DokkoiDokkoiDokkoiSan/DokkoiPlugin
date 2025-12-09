package org.meyason.dokkoi.constants;

import java.util.List;

public class GameEntityKeyString {

    public static final String COMEDIAN = "comedian";
    public static final String NPC = "npc";
    public static final String ENEMY = "enemy";


    private static final List<String> entityKeyStringHashMap = List.of(
            COMEDIAN,
            NPC,
            ENEMY
    );

    public static List<String> getEntityKeyStringHashMap() {return entityKeyStringHashMap;}
}
