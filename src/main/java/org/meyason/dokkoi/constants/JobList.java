package org.meyason.dokkoi.constants;

import org.meyason.dokkoi.job.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobList {

    public static final Executor EXECUTOR = new Executor();
    public static final Lonely LONELY = new Lonely();
    public static final Bomber BOMBER = new Bomber();
    public static final IronMaiden IRONMAIDEN = new IronMaiden();
    public static final Explorer EXPLORER = new Explorer();
    public static final Prayer PRAYER = new Prayer();
    public static final Photographer PHOTOGRAPHER = new Photographer();
    public static final DrugStore DRUGSTORE = new DrugStore();
    public static final Summoner SUMMONER = new Summoner();

    private static final HashMap<String, Job> jobMap = new HashMap<String, Job>();

    static{
//        jobMap.put(EXECUTOR.getName(), EXECUTOR);
//        jobMap.put(LONELY.getName(), LONELY);
//        jobMap.put(BOMBER.getName(), BOMBER);
//        jobMap.put(IRONMAIDEN.getName(), IRONMAIDEN);
//        jobMap.put(EXPLORER.getName(), EXPLORER);
        jobMap.put(PRAYER.getName(), PRAYER);
        jobMap.put(PHOTOGRAPHER.getName(), PHOTOGRAPHER);
        jobMap.put(DRUGSTORE.getName(), DRUGSTORE);
        jobMap.put(SUMMONER.getName(), SUMMONER);
    }

    public static List<Job> getAllJobs(){
        return new ArrayList<>(jobMap.values());
    }

    public static List<String> getAllJobNames(){
        return new ArrayList<>(jobMap.keySet());
    }

    public static Job getJobByName(String name){
        return jobMap.get(name);
    }
}
