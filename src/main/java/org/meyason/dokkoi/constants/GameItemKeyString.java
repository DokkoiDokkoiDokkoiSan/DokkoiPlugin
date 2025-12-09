package org.meyason.dokkoi.constants;

import java.util.List;

public class GameItemKeyString {

    public static final String ITEM_NAME = "item_name";

    // スキルやクールダウンなど、複数の人が持つ可能性のあるアイテムにシリアルナンバーを持たせるためのキー
    public static final String UNIQUE_ITEM = "unique_item";

    public static final String GUN_SERIAL = "gun_serial";

    private static final List<String> gameItemKeyStringHashMap = List.of(
        ITEM_NAME,
        UNIQUE_ITEM,
        GUN_SERIAL
    );

    public static List<String> getGameItemKeyStringHashMap() {return gameItemKeyStringHashMap;}
}
