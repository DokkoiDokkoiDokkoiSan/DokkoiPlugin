package org.meyason.dokkoi.constants;

public enum GameEntityList {
    YOSHIO("yoshio", GameEntityKeyString.COMEDIAN),
    ZAKOSHI("zakoshi", GameEntityKeyString.COMEDIAN),
    WAKABAYASHI("wakabayashi", GameEntityKeyString.COMEDIAN),
    OGATA("ogata", GameEntityKeyString.COMEDIAN),
    OOKI("ooki", GameEntityKeyString.COMEDIAN),
    DEALER("dealer", GameEntityKeyString.NPC),
    CLERK("clerk", GameEntityKeyString.NPC),
    SKELETON("skeleton", GameEntityKeyString.ENEMY);

    private final String id;
    private final String type;

    GameEntityList(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public static GameEntityList getGameEntityList(String id) {
        for (GameEntityList gameEntityList : values()) {
            if (gameEntityList.getId().equals(id)) {
                return gameEntityList;
            }
        }
        return null;
    }
}
