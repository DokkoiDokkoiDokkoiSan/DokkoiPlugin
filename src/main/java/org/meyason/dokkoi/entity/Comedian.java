package org.meyason.dokkoi.entity;

import java.util.List;

public class Comedian extends GameEntity {

    public static final List<String> comedianIDLIST = List.of(
        YOSHIO,
        ZAKOSHI,
        WAKABAYASHI,
        OGATA,
        OOKI
    );

    private final String deathMessage;

    public Comedian(String id) {
        super(id);
        this.name = nameMap.get(id);
        this.deathMessage = deathMessageMap.get(id);
    }

    public String getDeathMessage() {
        return deathMessage;
    }

}

