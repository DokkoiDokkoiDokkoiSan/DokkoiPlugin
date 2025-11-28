package org.meyason.dokkoi.constants;

import org.meyason.dokkoi.item.battleitem.ArcherArmor;

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

    public static final String SKILL = "skill";
    public static final String ULTIMATE_SKILL = "ultimate_skill";
    public static final String PASSIVE_SKILL = "passive_skill";

    public static final String GOLDENCARROT = "golden_carrot";
    public static final String BAKEDPOTATO = "baked_potato";

    public static final String LONGSWORD = "long_sword";
    public static final String THUNDERJAVELIN = "thunder_javelin";

    public static final String ARCHERARMOR = "archer_armor";
    public static final String REDHELMET = "red_helmet";

    public static final String TSUYOKUNARU = "tsuyokunaru";

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
        BURIBURIGUARD,
        STRONGESTBALL,
        STRONGESTSTRONGESTBALL,
        STRONGESTSTRONGESTSTRONGESTBALL,
        GOLDENCARROT,
        LONGSWORD,
        ARCHERARMOR,
        BAKEDPOTATO,
        THUNDERJAVELIN,
        REDHELMET,
        TSUYOKUNARU
    );

    public static List<String> getGameItemKeyStringHashMap() {return gameItemKeyStringHashMap;}
}
