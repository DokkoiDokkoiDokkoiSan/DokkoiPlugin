package org.meyason.dokkoi.constants;

import org.meyason.dokkoi.goal.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoalList {

//    public static final Debug DEBUG = new Debug();
    public static final LastMan LASTMAN = new LastMan();
    public static final Shadow SHADOW = new Shadow();
    public static final Police POLICE = new Police();
    public static final Killer KILLER = new Killer();
    public static final CarpetBombing CARPETBOMBING = new CarpetBombing();
    public static final MassTierKiller MASSTIERKILLER = new MassTierKiller();
    public static final MaidenGazer MAIDENGAZER = new MaidenGazer();
    public static final ComedianKiller COMEDIANKILLER = new ComedianKiller();
    public static final KetsumouHunter KETSUMOUHUNTER = new KetsumouHunter();
    public static final KetsumouPirate KETSUMOUPIRATE = new KetsumouPirate();
    public static final Defender DEFENDER = new Defender();
    public static final GachaBeginner GACHABEGINNER = new GachaBeginner();
    public static final Pachikasu PACHIASU = new Pachikasu();
    public static final GamblerMaster GAMBLERMASTER = new GamblerMaster();
    public static final DrugEnforcementAdministration DRUGENFORCEMENTADMINISTRATION = new DrugEnforcementAdministration();
    public static final PhotoAllPlayer PHOTOALLPLAYER = new PhotoAllPlayer();
    public static final TakeTwoShot TAKETWOSHORT = new TakeTwoShot();
    public static final SugiYakkyoku SUGIYAKKYOKU = new SugiYakkyoku();
    public static final MatsumotoKiyoshi MATSUMOTOKIYOSHI = new MatsumotoKiyoshi();
    public static final GangStar GANGSTAR = new GangStar();
    public static final FiftyPercent FIFTYPERCENT = new FiftyPercent();
    public static final SkeletonSlayer SKELETONSLAYER = new SkeletonSlayer();
    public static final Assasin ASSASIN = new Assasin();
    public static final EscapeFromUnkov ESCAPEFROMUNKOV = new EscapeFromUnkov();

    private static final HashMap<String, Goal> goalMap = new HashMap<>();

    static{
//        goalMap.put(DEBUG.getName(), DEBUG);
        goalMap.put(LASTMAN.getName(), LASTMAN);
        goalMap.put(SHADOW.getName(), SHADOW);
        goalMap.put(POLICE.getName(), POLICE);
        goalMap.put(KILLER.getName(), KILLER);
        goalMap.put(CARPETBOMBING.getName(), CARPETBOMBING);
        goalMap.put(MASSTIERKILLER.getName(), MASSTIERKILLER);
        goalMap.put(MAIDENGAZER.getName(), MAIDENGAZER);
        goalMap.put(COMEDIANKILLER.getName(), COMEDIANKILLER);
        goalMap.put(KETSUMOUHUNTER.getName(), KETSUMOUHUNTER);
        goalMap.put(KETSUMOUPIRATE.getName(), KETSUMOUPIRATE);
        goalMap.put(DEFENDER.getName(), DEFENDER);
        goalMap.put(GACHABEGINNER.getName(), GACHABEGINNER);
        goalMap.put(PACHIASU.getName(), PACHIASU);
        goalMap.put(GAMBLERMASTER.getName(), GAMBLERMASTER);
        goalMap.put(DRUGENFORCEMENTADMINISTRATION.getName(), DRUGENFORCEMENTADMINISTRATION);
        goalMap.put(PHOTOALLPLAYER.getName(), PHOTOALLPLAYER);
        goalMap.put(TAKETWOSHORT.getName(), TAKETWOSHORT);
        goalMap.put(SUGIYAKKYOKU.getName(), SUGIYAKKYOKU);
        goalMap.put(MATSUMOTOKIYOSHI.getName(), MATSUMOTOKIYOSHI);
        goalMap.put(GANGSTAR.getName(), GANGSTAR);
        goalMap.put(FIFTYPERCENT.getName(), FIFTYPERCENT);
        goalMap.put(SKELETONSLAYER.getName(), SKELETONSLAYER);
        goalMap.put(ASSASIN.getName(), ASSASIN);
        goalMap.put(ESCAPEFROMUNKOV.getName(), ESCAPEFROMUNKOV);
    }

    public static List<Goal> getAllGoals(){
        return new ArrayList<>(goalMap.values());
    }

    public static List<String> getAllGoalNames(){
        return new ArrayList<>(goalMap.keySet());
    }

    public static Goal getGoalByName(String goalName){
        return goalMap.get(goalName);
    }

    /**
     * 各Goal型の新しいインスタンスを生成するファクトリーメソッド
     */
    public static LastMan createLastMan(){ return new LastMan(); }
    public static Shadow createShadow(){ return new Shadow(); }
    public static Police createPolice(){ return new Police(); }
    public static Killer createKiller(){ return new Killer(); }
    public static CarpetBombing createCarpetBombing(){ return new CarpetBombing(); }
    public static MassTierKiller createMassTierKiller(){ return new MassTierKiller(); }
    public static MaidenGazer createMaidenGazer(){ return new MaidenGazer(); }
    public static ComedianKiller createComedianKiller(){ return new ComedianKiller(); }
    public static KetsumouHunter createKetsumouHunter(){ return new KetsumouHunter(); }
    public static KetsumouPirate createKetsumouPirate(){ return new KetsumouPirate(); }
    public static Defender createDefender(){ return new Defender(); }
    public static GachaBeginner createGachaBeginner(){ return new GachaBeginner(); }
    public static Pachikasu createPachikasu(){ return new Pachikasu(); }
    public static GamblerMaster createGamblerMaster(){ return new GamblerMaster(); }
    public static DrugEnforcementAdministration createDrugEnforcementAdministration(){ return new DrugEnforcementAdministration(); }
    public static PhotoAllPlayer createPhotoAllPlayer(){ return new PhotoAllPlayer(); }
    public static TakeTwoShot createTakeTwoShot(){ return new TakeTwoShot(); }
    public static SugiYakkyoku createSugiYakkyoku(){ return new SugiYakkyoku(); }
    public static MatsumotoKiyoshi createMatsumotoKiyoshi(){ return new MatsumotoKiyoshi(); }
    public static GangStar createGangStar(){ return new GangStar(); }
    public static FiftyPercent createFiftyPercent(){ return new FiftyPercent(); }
    public static SkeletonSlayer createSkeletonSlayer(){ return new SkeletonSlayer(); }
    public static Assasin createAssasin(){ return new Assasin(); }
    public static EscapeFromUnkov createEscapeFromUnkov(){ return new EscapeFromUnkov(); }
}
