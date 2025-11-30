package org.meyason.dokkoi.constants;

import java.util.List;

public class GameEntityKeyString {

    public static final String COMEDIAN = "comedian";
    public static final String NPC = "npc";


    private static final List<String> entityKeyStringHashMap = List.of(
            COMEDIAN,
            NPC
    );

    public static List<String> getEntityKeyStringHashMap() {return entityKeyStringHashMap;}
}
