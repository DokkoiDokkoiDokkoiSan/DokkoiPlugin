package org.meyason.dokkoi.constants;

import java.util.HashMap;
import java.util.List;

public class GameItemKeyString {

    public static final String ITEM_NAME = "item_name";
    public static final String GACHA_MACHINE = "gacha_machine";
    public static final String SKILL = "skill";
    public static final String ULTIMATE_SKILL = "ultimate_skill";
    public static final String PASSIVE_SKILL = "passive_skill";

    private static final List<String> gameItemKeyStringHashMap = List.of(
        ITEM_NAME,
        GACHA_MACHINE,
        SKILL,
        ULTIMATE_SKILL,
        PASSIVE_SKILL
    );

    public static List<String> getGameItemKeyStringHashMap() {return gameItemKeyStringHashMap;}
}
