package org.meyason.dokkoi.game;

import org.bukkit.util.Vector;
import org.meyason.dokkoi.entity.GameEntity;

import java.util.HashMap;
import java.util.List;

public class GameLocation {

    public static List<Vector> chestLocations = List.of(
            new Vector(-517, 73, -16),
            new Vector(-517, 73, -14),
            new Vector(-517, 73, -12),
            new Vector(-517, 73, -10),
            new Vector(-518, 73, -9),
            new Vector(-520, 73, -9),
            new Vector(-522, 73, -9),
            new Vector(-524, 73, -9),
            new Vector(-526, 73, -9)
    );

    public static List<Vector> heliChairLocations = List.of(
            new Vector(-525, 73, -18)
    );

    public static List<Vector> clerkLocations = List.of(
//            ショップおじいちゃん座標
//1(fuck food):46,1,151
//2(シキ・イエークス教):-143,1,140
//3(Lawson):-30,1,-144
//4(濃厚豚骨豚無双):144,1,-140
//5(fuck cafe):55,19,30
//6(デパート美容院):29,11,-30
            new Vector(46, 1, 151),
            new Vector(-143, 1, 140),
            new Vector(-30, 1, -144),
            new Vector(144, 1, -140),
            new Vector(55, 19, 30),
            new Vector(-29, 11, -30)
    );

    public static List<Vector> dealerLocations = List.of(
//密売人座標
//1(Fuck depart 屋上):72,38,-12
//2(裁判所横):-2,1,-147
//3(Fuck depart 地下駐車場):80,-9,-14
//4(Fuck food横):56,1,148
//5(Ascent ヘブン下):-94,2,130
            new Vector(72, 38, -12),
            new Vector(-2, 1, -147),
            new Vector(80, -9, -14),
            new Vector(56, 1, 148),
            new Vector(-94, 2, 130)
    );

//    芸人拘束場所座標
//1(パンサー尾形 デパート女子トイレ個室):36,1,38
//2(ビビる大木 Fuck Game 従業員室):25,28,43
//3(ハリウッドザコシショウ 罰罰病家 裏):-85,1,141
//4(オードリー若林 濃厚豚骨豚無双 裏):153,1,-149
//5(小島よしお Mineall死ね神殿):-120,-6,58
    public static HashMap<String, Vector> comedianLocations = new HashMap<>(){
    {
        put(GameEntity.OGATA, new Vector(36, 1, 38));
        put(GameEntity.OOKI, new Vector(25, 28, 43));
        put(GameEntity.ZAKOSHI, new Vector(-85, 1, 141));
        put(GameEntity.WAKABAYASHI, new Vector(153, 1, -149));
        put(GameEntity.YOSHIO, new Vector(-120, -6, 58));
    }};

}
