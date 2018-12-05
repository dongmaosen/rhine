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
public class RhineQuartzDemoJob extends RhineQuartzJob {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("RhineQuartzDemoJob : " + new Date().getTime() + "(" + Thread.currentThread().getName() + ")");
	}

}
