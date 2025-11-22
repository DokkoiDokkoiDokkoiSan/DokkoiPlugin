package org.meyason.dokkoi.constants;

import org.bukkit.entity.Player;
import org.meyason.dokkoi.job.Executor;
import org.meyason.dokkoi.job.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobList {

    public static final Executor EXECUTOR = new Executor();

    private static final HashMap<String, Job> jobMap = new HashMap<String, Job>();

    static{
        jobMap.put(EXECUTOR.getName(), EXECUTOR);
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
