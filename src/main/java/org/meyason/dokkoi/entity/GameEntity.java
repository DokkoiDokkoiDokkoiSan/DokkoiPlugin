package org.meyason.dokkoi.entity;

import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.constants.GameEntityList;
import org.meyason.dokkoi.exception.GameEntityIDNotFoundException;

import java.util.HashMap;

public abstract class GameEntity {

    public static final String YOSHIO = "yoshio";
    public static final String ZAKOSHI = "zakoshi";
    public static final String WAKABAYASHI = "wakabayashi";
    public static final String OGATA = "ogata";
    public static final String OOKI = "ooki";
    public static final String DEALER = "dealer";
    public static final String CLERK = "clerk";
    public static final String SKELETON = "skeleton";

    public static HashMap<String, String> nameMap = new HashMap<>(){{
        put(YOSHIO, "小島よしお");
        put(ZAKOSHI, "ハリウッドザコシショウ");
        put(WAKABAYASHI, "オードリー若林");
        put(OGATA, "パンサー尾形");
        put(OOKI, "ビビる大木");

        put(DEALER, "§5密売人");
        put(CLERK, "§3ショップおじいちゃん");
        put(SKELETON, "§7スケルトン");
    }};

    public static HashMap<String, String> deathMessageMap = new HashMap<>(){{
        put(YOSHIO, "小島よしお「ピーーーーヤ！！！！！！！！ｗｗｗ」");
        put(ZAKOSHI, "ハリウッドザコシショウ「でさーねーｗｗｗゴース！！！！ｗｗｗｗｗｗ」");
        put(WAKABAYASHI, "若林「なんでだよ！！！！ｗｗｗ言うとりますけどもｗｗｗｗ！！ｗ」");
        put(OGATA, "パンサー尾形「俺悲しいっすよ！！！！！！ｗｗｗｗはい！！！ｗｗ」");
        put(OOKI, "ビビる大木「ぎゃあああああああああああああああああああ！！！！！！！！！！！！！！！！！！！！！！！！！！！」");
    }};


    protected String id;
    protected String name;

    public GameEntity(String id){
        this.id = id;
        this.name = nameMap.get(id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static GameEntity getGameEntityFromId(String id){
        if(id==null){ throw new GameEntityIDNotFoundException("Game entity id is null"); }
        GameEntityList list = GameEntityList.getGameEntityList(id);
        if(list==null){ throw new GameEntityIDNotFoundException("wrong id"); }
        String type = list.getType();
        return switch (type) {
            case GameEntityKeyString.COMEDIAN -> new Comedian(id);
            case GameEntityKeyString.NPC -> (
                switch (id) {
                    case DEALER -> new Dealer();
                    case CLERK -> new Clerk();
                    default -> throw new GameEntityIDNotFoundException("wrong id");
                }
            );
            case GameEntityKeyString.ENEMY -> (
                switch (id) {
                    case SKELETON -> new Skeleton();
                    default -> throw new GameEntityIDNotFoundException("wrong id");
                }
            );
            default -> throw new GameEntityIDNotFoundException("wrong id");
        };

    }

}
