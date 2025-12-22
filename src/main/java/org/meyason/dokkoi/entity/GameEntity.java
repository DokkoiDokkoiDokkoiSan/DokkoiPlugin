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
    public static final String INMU = "inmu";
    public static final String SUSURU = "susuru";
    public static final String SKELETON = "skeleton";

    public static HashMap<String, String> nameMap = new HashMap<>(){{
        put(YOSHIO, "小島よしお");
        put(ZAKOSHI, "ハリウッドザコシショウ");
        put(WAKABAYASHI, "オードリー若林");
        put(OGATA, "パンサー尾形");
        put(OOKI, "ビビる大木");

        put(DEALER, "§5密売人");
        put(CLERK, "§3ショップおじいちゃん");
        put(INMU, "§3一般店主爺");
        put(SUSURU, "§6ヤバいラーメン店主のSUSURU");
        put(SKELETON, "§7スケルトン");
    }};

    public static HashMap<String, String> deathMessageMap = new HashMap<>(){{
        put(YOSHIO, "小島よしお「ピーーーーヤ！！！！！！！！ｗｗｗ」");
        put(ZAKOSHI, "ハリウッドザコシショウ「でさーねーｗｗｗゴース！！！！ｗｗｗｗｗｗ」");
        put(WAKABAYASHI, "若林「なんでだよ！！！！ｗｗｗ言うとりますけどもｗｗｗｗ！！ｗ」");
        put(OGATA, "パンサー尾形「俺悲しいっすよ！！！！！！ｗｗｗｗはい！！！ｗｗ」");
        put(OOKI, "ビビる大木「ぎゃあああああああああああああああああああ！！！！！！！！！！！！！！！！！！！！！！！！！！！」");
    }};

    public static HashMap<String, String> notEnoughMoneyMessageMap = new HashMap<>(){{
        put(CLERK, "§cショップおじいちゃん「お金が足りんのじゃよ・・・。」");
        put(INMU, "§c一般店主爺「そちら、14万3000円になっております。」");
        put(SUSURU, "§cヤバいラーメン店主のSUSURU「§6§lコラ～～～！！！殺すぞ～！§r§6」");
    }};

    public static HashMap<String, String> getGameItemFailedMessageMap = new HashMap<>(){{
        put(CLERK, "§cショップおじいちゃん「商品がないのじゃよ・・・。」");
        put(INMU, "§c一般店主爺「(商品が)ないです。」");
        put(SUSURU, "§cヤバいラーメン店主のSUSURU「§6§l商品の在庫がなく　さすがのSUSURUも倉庫に入っていってしまいました～！§r§6」");
    }};

    public static HashMap<String, String> notEnoughInventoryMessageMap = new HashMap<>(){{
        put(CLERK, "§cショップおじいちゃん「荷物を整理してからまたおいで」");
        put(INMU, "§c一般店主爺「あっおい待てい、インベントリに空きがないゾ」");
        put(SUSURU, "§cヤバいラーメン店主のSUSURU「§6§l満杯のインベントリを見て　怒りのあまり卓上調味料を全部倒してしまいました～！§r§6」");
    }};

    public static HashMap<String, String> talkMessageMap = new HashMap<>(){{
        put(CLERK, "§cbショップおじいちゃん「いらっしゃい！」");
        put(INMU, "§b一般店主爺「入って、どうぞ。ゆっくり見てけよ見てけよ～」");
        put(SUSURU, "§bヤバいラーメン店主のSUSURU「§6§lこちらが濃厚とんこつ豚無双さんの濃厚無双ラーメン　海苔トッピングです§r§6」");
    }};

    public static HashMap<String, String> buyItemMessageMap = new HashMap<>(){{
        put(CLERK, "§bショップおじいちゃん「ありがとう！また来てくれよ」");
        put(INMU, "§b一般店主爺「ありがとナス！またいいよ！こいよ！」");
        put(SUSURU, "§bヤバいラーメン店主のSUSURU「§6§lちなみに、店主さんが土下座している様子は ぜひサブチャンネルをご覧ください§r§6」");
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
                    case CLERK -> new Clerk(CLERK);
                    case INMU -> new Clerk(INMU);
                    case SUSURU -> new Clerk(SUSURU);
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
