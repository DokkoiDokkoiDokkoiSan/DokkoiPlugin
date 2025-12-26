package org.meyason.dokkoi.game;

import org.bukkit.World;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.entity.GameEntity;
import org.meyason.dokkoi.util.BlockCopyUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GameLocation {

    private static GameLocation instance;
    public static GameLocation getInstance() {
        if (instance == null) {
            instance = new GameLocation();
        }
        return instance;
    }

    public GameLocation(){
        instance = this;
    }

    public List<Vector> chestLocations = List.of(
            new Vector(89,5,132),
            new Vector(91,1,107),
            new Vector(128,1,107),
            new Vector(110,1,145),
            new Vector(150,1,150),
            new Vector(7,1,88),
            new Vector(14,1,102),
            new Vector(14,1,118),
            new Vector(4,1,150),
            new Vector(37,1,145),
            new Vector(38,1,114),
            new Vector(37,1,88),
            new Vector(-74,3,101),
            new Vector(-47,3,101),
            new Vector(-20,3,101),
            new Vector(-18,1,141),
            new Vector(-30,2,133),
            new Vector(-62,1,133),
            new Vector(-44,3,155),
            new Vector(-70,1,136),
            new Vector(-68,1,150),
            new Vector(-108,5,131),
            new Vector(-104,2,130),
            new Vector(-93,2,125),
            new Vector(-87,1,103),
            new Vector(-122,0,124),
            new Vector(-145,0,124),
            new Vector(-145,0,101),
            new Vector(-122,0,102),
            new Vector(-112,13,112),
            new Vector(-123,1,149),
            new Vector(-146,1,149),
            new Vector(-99,-15,51),
            new Vector(-99,-7,31),
            new Vector(-141,-15,51),
            new Vector(-120,1,26),
            new Vector(-120,1,56),
            new Vector(-141,-7,31),
            new Vector(-120,-13,19),
            new Vector(-124,-2,12),
            new Vector(-122,-2,-7),
            new Vector(-90,-2,-7),
            new Vector(-92,-2,12),
            new Vector(-120,1,-30),
            new Vector(-145,2,-48),
            new Vector(-110,1,-88),
            new Vector(-135,5,-103),
            new Vector(-88,1,-110),
            new Vector(-103,5,-135),
            new Vector(-48,2,-145),
            new Vector(-99,1,-91),
            new Vector(-91,1,-99),
            new Vector(-12,1,-109),
            new Vector(-47,1,-93),
            new Vector(-13,1,-123),
            new Vector(-26,1,-137),
            new Vector(-35,1,-132),
            new Vector(2,1,-128),
            new Vector(21,1,-147),
            new Vector(40,1,-128),
            new Vector(-6,1,-128),
            new Vector(48,1,-128),
            new Vector(41,1,-150),
            new Vector(86,7,-148),
            new Vector(98,5,-150),
            new Vector(109,5,-121),
            new Vector(79,6,-126),
            new Vector(91,2,-99),
            new Vector(75,2,-92),
            new Vector(143,13,-101),
            new Vector(136,1,-140),
            new Vector(128,1,-90),
            new Vector(110,1,-42),
            new Vector(102,1,-12),
            new Vector(110,1,9),
            new Vector(79,1,37),
            new Vector(125,1,44),
            new Vector(125,1,12),
            new Vector(125,1,-26),
            new Vector(28,1,85),
            new Vector(-79,1,70),
            new Vector(-79,1,49),
            new Vector(-45,3,64),
            new Vector(-61,3,13),
            new Vector(-69,2,-68),
            new Vector(-40,1,-50),
            new Vector(39,1,-85),
            new Vector(3,1,-85),
            new Vector(66,1,-50),
            new Vector(123,1,-50),
            new Vector(-1,-9,30),
            new Vector(15,-9,10),
            new Vector(-1,-9,-30),
            new Vector(36,-9,-26),
            new Vector(57,-9,34),
            new Vector(42,-9,-14),
            new Vector(78,-9,18),
            new Vector(79,-9,2),
            new Vector(74,1,43),
            new Vector(61,1,32),
            new Vector(19,1,47),
            new Vector(28,1,38),
            new Vector(48,2,-30),
            new Vector(8,2,-22),
            new Vector(-48,1,2),
            new Vector(-12,1,-20),
            new Vector(-13,1,11),
            new Vector(-26,1,35),
            new Vector(-36,1,41),
            new Vector(-4,1,38),
            new Vector(-47,1,-37),
            new Vector(-5,1,-35),
            new Vector(69,20,30),
            new Vector(51,19,-47),
            new Vector(76,20,10),
            new Vector(61,20,-8),
            new Vector(34,19,27),
            new Vector(29,19,45),
            new Vector(70,19,47),
            new Vector(60,22,41),
            new Vector(34,20,-24),
            new Vector(21,24,-19),
            new Vector(46,19,-43),
            new Vector(42,22,-34),
            new Vector(-26,19,-45),
            new Vector(-14,20,-20),
            new Vector(-35,20,-20),
            new Vector(-41,19,45),
            new Vector(-5,19,31),
            new Vector(-36,32,31),
            new Vector(-5,19,2),
            new Vector(-36,32,2),
            new Vector(-2,19,47),
            new Vector(-2,28,45),
            new Vector(9,28,-19),
            new Vector(25,28,-45),
            new Vector(42,28,-44),
            new Vector(2,28,-45),
            new Vector(51,28,-45),
            new Vector(74,28,1),
            new Vector(66,28,45),
            new Vector(38,28,45),
            new Vector(31,28,36),
            new Vector(38,28,17),
            new Vector(19,28,22),
            new Vector(23,28,45),
            new Vector(-44,38,-43),
            new Vector(6,38,-32),
            new Vector(-46,38,-4),
            new Vector(6,38,32),
            new Vector(-46,38,45),
            new Vector(18,38,45),
            new Vector(74,38,5),
            new Vector(18,38,-45),
            new Vector(77,1,-12),
            new Vector(130,15,-111)
    );

    public List<Vector> originalHelicopterLocations = List.of(
//            コピー元ヘリコプター元座標
//-11,-6,-225(右下)
//-23,1,-237(左上)
            new Vector(-11, -6, -225),
            new Vector(-23, 1, -237)
    );

    public List<Vector> originalHeliPortLocations = List.of(
//            コピー元ヘリポート元座標
            new Vector(-27, -6, -225),
            new Vector(-39, 1, -237)
    );


    public List<Vector> heliPortLocations = List.of(
//            ヘリポート座標(右下)
            new Vector(6,1,-91),
            new Vector(52,38,6),
            new Vector(-14,38,6),
            new Vector(-88,1,-24),
            new Vector(10,1,147)
    );

    public Vector cloneHeli(){
        Vector cloneLocation = heliPortLocations.get(new Random().nextInt(heliPortLocations.size()));
        BlockCopyUtil.copyAndPaste(originalHelicopterLocations, cloneLocation);
        return cloneLocation;
    }

    public void revertHeliPort(Vector heliPortLocation){
        BlockCopyUtil.copyAndPaste(originalHeliPortLocations, heliPortLocation);
    }

    public void revertAllHeliPort(){
        for(Vector heliPortLocation : heliPortLocations){
            BlockCopyUtil.copyAndPaste(originalHeliPortLocations, heliPortLocation);
        }
    }

    public boolean isInHeliChair(Vector heliPortLocation, Vector chairLocation){
        // chairがheli座標の空間内にあるかどうか
        Vector topCorner = heliPortLocation.clone().add(new Vector(-12, 7, -12));
        Vector bottomCorner = heliPortLocation.clone();
        return chairLocation.getX() <= bottomCorner.getX() && chairLocation.getX() >= topCorner.getX()
                && chairLocation.getY() >= bottomCorner.getY() && chairLocation.getY() <= topCorner.getY()
                && chairLocation.getZ() <= bottomCorner.getZ() && chairLocation.getZ() >= topCorner.getZ();
    }

    public List<Vector> clerkLocations = List.of(
//            ショップおじいちゃん座標
            new Vector(46.5, 1, 151.5),
            new Vector(-142.5, 1, 140.5),
            new Vector(-29.5, 1, -143.5),
            new Vector(144.5, 1, -139.5),
            new Vector(55.5, 19, 30.5),
            new Vector(29.5, 1, -29.5)
    );

    public List<Vector> dealerLocations = List.of(
//密売人座標
            new Vector(72.5, 38, -11.5),
            new Vector(-1.5, 1, -146.5),
            new Vector(80.5, -9, -13.5),
            new Vector(56.5, 1, 148.5),
            new Vector(-93.5, 2, 130.5)
    );

//    芸人拘束場所座標
    public HashMap<String, Vector> comedianLocations = new HashMap<>(){
    {
        put(GameEntity.OGATA, new Vector(36.5, 1, 38.5));
        put(GameEntity.OOKI, new Vector(25.5, 28, 43.5));
        put(GameEntity.ZAKOSHI, new Vector(-84.5, 1, 141.5));
        put(GameEntity.WAKABAYASHI, new Vector(153.5, 1, -148.5));
        put(GameEntity.YOSHIO, new Vector(-119.5, -6, 58.5));
    }};

    public List<Vector> respawnLocations = List.of(
//            プレイヤーリスポーン地点座標
            new Vector(91.5, 6, -144.5),
            new Vector(21.5, 2, -127.5),
            new Vector(-141.5, 5, -141.5),
            new Vector(-119.5,-14,22.5),
            new Vector(-132.5,13,112.5),
            new Vector(-46.5,14,133.5),
            new Vector(111.5,1,126.5),
            new Vector(69.5,28,1.5),
            new Vector(29.5,-9,2.5),
            new Vector(29.5,3,-0.5)
    );

    public Vector LobbyLocation = new Vector(187.5,-59,67.5);

    public List<Vector> prayerUltimateLocations = List.of(
            new Vector(-138.5,9,161.5),
            new Vector(-131.5,9,161.5)
    );

    public List<Vector> skeletonSpawnLocations = List.of(
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
