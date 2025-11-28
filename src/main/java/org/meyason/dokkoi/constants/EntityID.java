package org.meyason.dokkoi.constants;

public enum EntityID {
    YOSHIO("yoshio", "comedian"),
    ZAKOSHI("zakoshi", "comedian"),
    WAKABAYASHI("wakabayashi", "comedian"),
    OGATA("ogata", "comedian"),
    OOKI("ooki", "comedian"),
    DEALER("dealer", "npc");

    private final String id;
    private final String type;

    EntityID(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public static EntityID getEntityID(String id) {
        for (EntityID entityID : values()) {
            if (entityID.getId().equals(id)) {
                return entityID;
            }
        }
        return null;
    }
}
