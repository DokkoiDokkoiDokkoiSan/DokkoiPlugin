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

    private static final HashMap<String, Job> jobMap = new HashMap<String, Job>();

    static{
        jobMap.put(EXECUTOR.getName(), EXECUTOR);
//        jobMap.put(LONELY.getName(), LONELY);
//        jobMap.put(BOMBER.getName(), BOMBER);
        jobMap.put(IRONMAIDEN.getName(), IRONMAIDEN);
//        jobMap.put(EXPLORER.getName(), EXPLORER);
        jobMap.put(PRAYER.getName(), PRAYER);
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
