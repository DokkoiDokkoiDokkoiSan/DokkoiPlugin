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
//            89,5,132
//91,1,107
//128,1,107
//110,1,145
//150,1,150
            new Vector(89,5,132),
            new Vector(91,1,107),
            new Vector(128,1,107),
            new Vector(110,1,145),
            new Vector(150,1,150)
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

    public static List<Vector> skeletonSpawnLocations = List.of(
//        ・道路エリア
//149,1,74
//149,1,0
//149,1,-74
//36,1,-74
//-75,1,-74
//-75,1,0
//-75,1,74
//37,1,74
//104,1,29
//100,1,-26
//・MARUHAN
//72,1,98
//145,1,98
//152,1,148
//109,1,144
//72,1,142
//・地都ルイン
//54,1,130
//45,1,91
//34,1,118
//24,1,115
//13,1,92
//1,1,108
//16,1,143
//kotoma — 22:07
//金玉＆Ascent Asite
//-47,1,133
//-18,1,145
//-20,3,98
//-47,3,98
//-74,3,98
//-93,2,116
//-108,2,120
//-100,5,130
//            -119,1,98
//                    -150,1,98
//                    -149,1,129
//                    -120,1,129
//                    -113,13,112
//                    -92,1,41
//                    -147,1,41
//                    -120,-17,37
//                    -108,-15,53
//                    -132,-15,53
//            イエークス美術館＆LAWSON
//-120,1,-29
//-142,4,-84
//-99,10,-99
//-127,5,-126
//-84,4,-142
//-81,1,-116
//-44,1,-102
//-15,1,-102
//-45,1,-134
//-14,1,-134
//            ハンナ死ぬなあああああああああああああ！！！！！！！！
//9,1,-128
//21,1,-140
//33,1,-128
//21,2,-116
//47,1,-92
//47,1,-144
//-5,1,-146
//            トンネルは丸みを帯びたほうがいいのか。ほーん...＆濃厚豚骨豚無双
//144,1,-108
//102,2,-113
//100,1,-103
//90,7,-146
//72,2,-127
//68,1,-96
//127,16,-105
//            地下駐車場＆1F
//50,-9,43
//71,-9,-2
//50,-9,-43
//7,-9,-2
//68,1,2
//31,1,17
//-31,1,-35
//-44,1,4
//-21,1,29
//            4F
//50,28,36
//53,28,1
//2,28,6
//29,28,-21
//3F
//-7,19,32
//-35,31,32
//-42,19,6
//-7,19,3
//-35,31,3
//-23,19,-36
//1,19,-2
//17,19,-27
//54,19,-2
//35,19,37
//            67,38,-3
//57,38,38
//31,38,36
//29,38,11
//31,38,-13
//48,38,-24
            new Vector(149.5,1,74.5),
            new Vector(149.5,1,0.5),
            new Vector(149.5,1,-73.5),
            new Vector(36.5,1,-73.5),
            new Vector(-74.5,1,-73.5),
            new Vector(-74.5,1,0.5),
            new Vector(-74.5,1,74.5),
            new Vector(37.5,1,74.5),
            new Vector(104.5,1,29.5),
            new Vector(100.5,1,-25.5),
            new Vector(72.5,1,98.5),
            new Vector(145.5,1,98.5),
            new Vector(152.5,1,148.5),
            new Vector(109.5,1,144.5),
            new Vector(72.5,1,142.5),
            new Vector(54.5,1,130.5),
            new Vector(45.5,1,91.5),
            new Vector(34.5,1,118.5),
            new Vector(24.5,1,115.5),
            new Vector(13.5,1,92.5),
            new Vector(1.5,1,108.5),
            new Vector(16.5,1,143.5),
            new Vector(-46.5,1,133.5),
            new Vector(-17.5,1,145.5),
            new Vector(-19.5,3,98.5),
            new Vector(-46.5,3,98.5),
            new Vector(-73.5,3,98.5),
            new Vector(-92.5,2,116.5),
            new Vector(-107.5,2,120.5),
            new Vector(-99.5,5,130.5),
            new Vector(-118.5,1,98.5),
            new Vector(-149.5,1,98.5),
            new Vector(-148.5,1,129.5),
            new Vector(-119.5,1,129.5),
            new Vector(-112.5,13,112.5),
            new Vector(-91.5,1,41.5),
            new Vector(-146.5,1,41.5),
            new Vector(-119.5,-17,37.5),
            new Vector(-107.5,-15,53.5),
            new Vector(-131.5,-15,53.5),
            new Vector(-119.5,1,-28.5),
            new Vector(-141.5,4,-83.5),
            new Vector(-98.5,10,-98.5),
            new Vector(-126.5,5,-125.5),
            new Vector(-83.5,4,-141.5),
            new Vector(-80.5,1,-115.5),
            new Vector(-43.5,1,-101.5),
            new Vector(-14.5,1,-101.5),
            new Vector(-44.5,1,-133.5),
            new Vector(-13.5,1,-133.5),
            new Vector(9.5,1,-127.5),
            new Vector(21.5,1,-139.5),
            new Vector(33.5,1,-127.5),
            new Vector(21.5,2,-115.5),
            new Vector(47.5,1,-91.5),
            new Vector(47.5,1,-143.5),
            new Vector(-4.5,1,-145.5),
            new Vector(144.5,1,-107.5),
            new Vector(102.5,2,-112.5),
            new Vector(100.5,1,-102.5),
            new Vector(90.5,7,-145.5),
            new Vector(72.5,2,-126.5),
            new Vector(68.5,1,-95.5),
            new Vector(127.5,16,-104.5),
            new Vector(50.5,-9,43.5),
            new Vector(71.5,-9,-1.5),
            new Vector(50.5,-9,-42.5),
            new Vector(7.5,-9,-1.5),
            new Vector(68.5,1,2.5),
            new Vector(31.5,1,17.5),
            new Vector(-30.5,1,-34.5),
            new Vector(-43.5,1,4.5),
            new Vector(-20.5,1,29.5),
            new Vector(50.5,28,36.5),
            new Vector(53.5,28,1.5),
            new Vector(2.5,28,6.5),
            new Vector(29.5,28,-20.5),
            new Vector(-6.5,19,32.5),
            new Vector(-34.5,31,32.5),
            new Vector(-41.5,19,6.5),
            new Vector(-6.5,19,3.5),
            new Vector(-34.5,31,3.5),
            new Vector(-22.5,19,-35.5),
            new Vector(1.5,19,-1.5),
            new Vector(17.5,19,-26.5),
            new Vector(54.5,19,-1.5),
            new Vector(35.5,19,37.5),
            new Vector(67.5, 38,-2.5),
            new Vector(57.5, 38,38.5),
            new Vector(31.5, 38,36.5),
            new Vector(29.5, 38,11.5),
            new Vector(31.5, 38,-12.5),
            new Vector(48.5, 38,-23.5)
    );

}
