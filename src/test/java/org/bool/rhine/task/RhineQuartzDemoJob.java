package org.bool.rhine.task;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
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
		String ft = DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss");
		System.out.println("RhineQuartzDemoJob : " + ft + ", executed thread(" + Thread.currentThread().getName() + ")");
	}

}
