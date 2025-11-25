package org.meyason.dokkoi.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.EntityID;

public enum Comedian {
    YOSHIO("yoshio","小島よしお", "小島よしお「ピーーーーヤ！！！！！！！！ｗｗｗ」"),
    ZAKOSHI("zakoshi","ハリウッドザコシショウ", "ハリウッドザコシショウ「でさーねーｗｗｗゴース！！！！ｗｗｗｗｗｗ」"),
    WAKABAYASHI("wakabayashi","オードリー若林", "若林「なんでだよ！！！！ｗｗｗ言うとりますけどもｗｗｗｗ！！ｗ」"),
    OGATA("ogata","パンサー尾形", "パンサー尾形「俺悲しいっすよ！！！！！！ｗｗｗｗはい！！！ｗｗ」"),
    OOKI("ooki","ビビる大木", "ビビる大木「ぎゃあああああああああああああああああああ！！！！！！！！！！！！！！！！！！！！！！！！！！！」");


    private final String id;
    private final String name;
    private final String deathMessage;

    Comedian(String id, String name, String deathMessage) {
        this.id = id;
        this.name = name;
        this.deathMessage = deathMessage;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public static Comedian getComedianById(String id){
        for(Comedian comedian : Comedian.values()){
            if(comedian.getId().equals(id)){
                return comedian;
            }
        }
        return null;
    }

    public static boolean isComedianByID(String id){
        return getComedianById(id) != null;
    }

    public static Comedian getComedianByVillager(Villager villager){
        for(Comedian id : Comedian.values()){
            if(villager.getPersistentDataContainer().has(new NamespacedKey(Dokkoi.getInstance(), id.getId()), PersistentDataType.STRING)){
                return id;
            }
        }
        return null;
    }
}

