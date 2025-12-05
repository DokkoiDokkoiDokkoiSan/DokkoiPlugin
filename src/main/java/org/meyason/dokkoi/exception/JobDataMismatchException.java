package org.meyason.dokkoi.exception;

import org.meyason.dokkoi.job.Job;

public class JobDataMismatchException extends RuntimeException {
    public JobDataMismatchException(Job requiredJob, Job providedJob) {
        super("Job data mismatch: required " + requiredJob.getName() + ", but provided " + providedJob.getName());
    }
}
