package org.meyason.dokkoi.game;

import org.bukkit.World;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.entity.GameEntity;
import org.meyason.dokkoi.util.BlockCopyUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GameLocation {

    public static List<Vector> chestLocations = List.of(
//            new Vector(-517, 73, -16),
//            new Vector(-517, 73, -14),
//            new Vector(-517, 73, -12),
//            new Vector(-517, 73, -10),
//            new Vector(-518, 73, -9),
//            new Vector(-520, 73, -9),
//            new Vector(-522, 73, -9),
//            new Vector(-524, 73, -9),
            new Vector(-526, 73, -9)
    );

    public static List<Vector> originalHelicopterLocations = List.of(
//            コピー元ヘリコプター元座標
//-11,-6,-225(右下)
//-23,1,-237(左上)
            new Vector(-11, -6, -225),
            new Vector(-23, 1, -237)
    );

    public static List<Vector> originalHeliPortLocations = List.of(
//            コピー元ヘリポート元座標
//            -27,-6,-225
//                    -39,1,-237
            new Vector(-27, -6, -225),
            new Vector(-39, 1, -237)
    );


    public static List<Vector> heliPortLocations = List.of(
//            ヘリポート座標(右下)
//1　6,1,-91
//2　52,38,6
//3　-14,38,6
//4　-88,1,-24
//5　10,1,147
            new Vector(6,1,-91),
            new Vector(52,38,6),
            new Vector(-14,38,6),
            new Vector(-88,1,-24),
            new Vector(10,1,147)
    );

    public static Vector cloneHeli(){
        Vector cloneLocation = heliPortLocations.get(new Random().nextInt(heliPortLocations.size()));
        BlockCopyUtil.copyAndPaste(originalHelicopterLocations, cloneLocation);
        return cloneLocation;
    }

    public static void revertHeliPort(Vector heliPortLocation){
        BlockCopyUtil.copyAndPaste(originalHeliPortLocations, heliPortLocation);
    }

    public static void revertAllHeliPort(){
        for(Vector heliPortLocation : heliPortLocations){
            BlockCopyUtil.copyAndPaste(originalHeliPortLocations, heliPortLocation);
        }
    }

    public static boolean isInHeliChair(Vector heliPortLocation, Vector chairLocation){
        // chairがheli座標の空間内にあるかどうか
        Vector topCorner = heliPortLocation.clone().add(new Vector(-12, 7, -12));
        Vector bottomCorner = heliPortLocation.clone();
        return chairLocation.getX() <= bottomCorner.getX() && chairLocation.getX() >= topCorner.getX()
                && chairLocation.getY() >= bottomCorner.getY() && chairLocation.getY() <= topCorner.getY()
                && chairLocation.getZ() <= bottomCorner.getZ() && chairLocation.getZ() >= topCorner.getZ();
    }

    public static List<Vector> clerkLocations = List.of(
//            ショップおじいちゃん座標
//1(fuck food):46,1,151
//2(シキ・イエークス教):-143,1,140
//3(Lawson):-30,1,-144
//4(濃厚豚骨豚無双):144,1,-140
//5(fuck cafe):55,19,30
//6(デパート美容院):29,11,-30
            new Vector(46.5, 1, 151.5),
            new Vector(-142.5, 1, 140.5),
            new Vector(-29.5, 1, -143.5),
            new Vector(144.5, 1, -139.5),
            new Vector(55.5, 19, 30.5),
            new Vector(-28.5, 11, -29.5)
    );

    public static List<Vector> dealerLocations = List.of(
//密売人座標
//1(Fuck depart 屋上):72,38,-12
//2(裁判所横):-2,1,-147
//3(Fuck depart 地下駐車場):80,-9,-14
//4(Fuck food横):56,1,148
//5(Ascent ヘブン下):-94,2,130
            new Vector(72.5, 38, -11.5),
            new Vector(-1.5, 1, -146.5),
            new Vector(80.5, -9, -13.5),
            new Vector(56.5, 1, 148.5),
            new Vector(-93.5, 2, 130.5)
    );

//    芸人拘束場所座標
//1(パンサー尾形 デパート女子トイレ個室):36,1,38
//2(ビビる大木 Fuck Game 従業員室):25,28,43
//3(ハリウッドザコシショウ 罰罰病家 裏):-85,1,141
//4(オードリー若林 濃厚豚骨豚無双 裏):153,1,-149
//5(小島よしお Mineall死ね神殿):-120,-6,58
    public static HashMap<String, Vector> comedianLocations = new HashMap<>(){
    {
        put(GameEntity.OGATA, new Vector(36.5, 1, 38.5));
        put(GameEntity.OOKI, new Vector(25.5, 28, 43.5));
        put(GameEntity.ZAKOSHI, new Vector(-84.5, 1, 141.5));
        put(GameEntity.WAKABAYASHI, new Vector(153.5, 1, -148.5));
        put(GameEntity.YOSHIO, new Vector(-119.5, -6, 58.5));
    }};

    public static List<Vector> respawnLocations = List.of(
//            プレイヤーリスポーン地点座標
//1　90,6,-145
//2　21,2,-128
//3　-142,5,-142
//4　-120,-14,22
//5　-133,13,112
//6　-47,14,133
//7　111,1,126
//8　69,28,1
//9　29,-9,2
//10　デパートの二階のどこかの予定、まだ制作中だから待って
            new Vector(90, 6, -145),
            new Vector(21, 2, -128),
            new Vector(-142, 5, -142),
            new Vector(-120,-14,22),
            new Vector(-133,13,112),
            new Vector(-47,14,133),
            new Vector(111,1,126),
            new Vector(69,28,1),
            new Vector(29,-9,2)
    );

    public static Vector LobbyLocation = new Vector(187.5,-59,67.5);

    public static List<Vector> prayerUltimateLocations = List.of(
            new Vector(-138.5,9,161.5),
            new Vector(-131.5,9,161.5)
    );

}
