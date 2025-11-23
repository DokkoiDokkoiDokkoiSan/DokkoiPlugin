package org.meyason.dokkoi.constants;

import org.meyason.dokkoi.goal.*;

import java.text.DecimalFormat;
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

    private static final HashMap<String, Goal> goalMap = new HashMap<>();

    static{
//        goalMap.put(DEBUG.getName(), DEBUG);
        goalMap.put(LASTMAN.getName(), LASTMAN);
        goalMap.put(SHADOW.getName(), SHADOW);
        goalMap.put(POLICE.getName(), POLICE);
        goalMap.put(KILLER.getName(), KILLER);
        goalMap.put(CARPETBOMBING.getName(), CARPETBOMBING);
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
