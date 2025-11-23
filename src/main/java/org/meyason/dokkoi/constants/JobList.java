package org.meyason.dokkoi.constants;

import org.bukkit.entity.Player;
import org.meyason.dokkoi.job.Bomber;
import org.meyason.dokkoi.job.Executor;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.job.Lonely;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobList {

    public static final Executor EXECUTOR = new Executor();
    public static final Lonely LONELY = new Lonely();
    public static final Bomber BOMBER = new Bomber();

    private static final HashMap<String, Job> jobMap = new HashMap<String, Job>();

    static{
        jobMap.put(EXECUTOR.getName(), EXECUTOR);
        jobMap.put(LONELY.getName(), LONELY);
        jobMap.put(BOMBER.getName(), BOMBER);
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
