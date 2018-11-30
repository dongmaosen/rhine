package org.bool.rhine.task;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineQuartzDemoJob implements RhineQuartzJob {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("RhineQuartzDemoJob : " + new Date().getTime() );
	}

}
