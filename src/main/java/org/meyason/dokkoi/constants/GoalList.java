package org.meyason.dokkoi.constants;

import org.meyason.dokkoi.goal.Collector;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.goal.Killer;
import org.meyason.dokkoi.goal.LastMan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoalList {

    public static final Killer KILLER = new Killer();
    public static final LastMan LASTMAN = new LastMan();
    public static final Collector COLLECTOR = new Collector();

    private static final HashMap<String, Goal> goalMap = new HashMap<>();

    static{
        goalMap.put(KILLER.getName(), KILLER);
        goalMap.put(LASTMAN.getName(), LASTMAN);
        goalMap.put(COLLECTOR.getName(), COLLECTOR);
    }

    public static List<Goal> getAllGoals(){
        return new ArrayList<>(goalMap.values());
    }
}
