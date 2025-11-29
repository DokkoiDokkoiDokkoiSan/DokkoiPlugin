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
    public static final PhotoAllPlayer PHOTOALLPLAYER = new PhotoAllPlayer();

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
        goalMap.put(PHOTOALLPLAYER.getName(), PHOTOALLPLAYER);
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
}
