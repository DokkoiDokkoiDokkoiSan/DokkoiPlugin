package org.meyason.dokkoi.entity;

public enum NPC {
    DEALER("dealer", "§5密売人");

    private final String id;
    private final String name;

    NPC(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public static NPC getNPCById(String id) {
        for (NPC npc : values()) {
            if (npc.getId().equals(id)) {
                return npc;
            }
        }
        return null;
    }

}
