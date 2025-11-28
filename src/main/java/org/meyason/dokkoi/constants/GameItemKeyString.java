package org.meyason.dokkoi.constants;

import java.util.List;

public class GameItemKeyString {

    public static final String ITEM_NAME = "item_name";

    public static final String GACHA_MACHINE = "gacha_machine";
    public static final String KILLER_LIST = "killer_list";
    public static final String RAPIER = "rapier";
    public static final String TIERPLAYERLIST = "tier_player_list";
    public static final String BURIBURIGUARD = "buri_buri_guard";
    public static final String STRONGESTBALL = "strongest_ball";
    public static final String STRONGESTSTRONGESTBALL = "strongest_strongest_ball";
    public static final String STRONGESTSTRONGESTSTRONGESTBALL = "strongest_strongest_strongest_ball";

    public static final String KETSUMOU = "ketsumou";
    public static final String HEARINGCRISTAL = "healing_cristal";

    public static final String SKILL = "skill";
    public static final String ULTIMATE_SKILL = "ultimate_skill";
    public static final String PASSIVE_SKILL = "passive_skill";

    public static final String GOLDENCARROT = "golden_carrot";

    private static final List<String> gameItemKeyStringHashMap = List.of(
        ITEM_NAME,
        GACHA_MACHINE,
        KILLER_LIST,
        RAPIER,
        TIERPLAYERLIST,
        SKILL,
        ULTIMATE_SKILL,
        PASSIVE_SKILL,
        KETSUMOU,
        HEARINGCRISTAL,
        BURIBURIGUARD,
        STRONGESTBALL,
        STRONGESTSTRONGESTBALL,
        STRONGESTSTRONGESTSTRONGESTBALL,
        GOLDENCARROT
    );

    public static List<String> getGameItemKeyStringHashMap() {return gameItemKeyStringHashMap;}
}
