package org.bool.rhine;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * 做一些JOB统计工作
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineJobListener implements JobListener {
	
	public static final String LISTENER_NAME = "RhineJobListener";

	public String getName() {
		return LISTENER_NAME;
	}

	public void jobToBeExecuted(JobExecutionContext context) {
		System.out.println("jobToBeExecuted - " + context.getJobDetail().getKey().getName());
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		System.out.println("jobExecutionVetoed - " + context.getJobDetail().getKey().getName());
	}

	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		System.out.println("jobWasExecuted - " + context.getJobDetail().getJobClass().getName());
	}

}
